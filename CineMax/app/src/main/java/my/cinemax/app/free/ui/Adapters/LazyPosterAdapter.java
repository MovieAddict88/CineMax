package my.cinemax.app.free.ui.Adapters;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.squareup.picasso.Picasso;
import my.cinemax.app.free.Provider.DataRepository;
import my.cinemax.app.free.R;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.ui.activities.LoadActivity;
import my.cinemax.app.free.ui.activities.MovieActivity;
import my.cinemax.app.free.ui.activities.SerieActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * LazyPosterAdapter - High-performance adapter for large datasets
 * 
 * Features:
 * - Lazy loading with pagination
 * - Memory efficient view recycling
 * - Automatic data loading on scroll
 * - Loading states and error handling
 * - Optimized for 10,000+ items
 */
public class LazyPosterAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    
    private static final String TAG = "LazyPosterAdapter";
    
    // View types
    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;
    private static final int VIEW_TYPE_ERROR = 2;
    
    // Pagination settings
    private static final int PAGE_SIZE = 20;
    private static final int PRELOAD_THRESHOLD = 5; // Load next page when 5 items from end
    
    private List<Poster> items;
    private Activity activity;
    private DataRepository repository;
    private Handler mainHandler;
    
    // Pagination state
    private boolean isLoading = false;
    private boolean hasMoreData = true;
    private int currentPage = 0;
    private String dataType; // "movies", "tv_series", or "all"
    private String searchQuery = "";
    private int genreFilter = -1;
    
    // Loading states
    private boolean showLoadingFooter = false;
    private boolean showErrorFooter = false;
    private String errorMessage = "";
    
    // Callbacks
    public interface OnLoadMoreListener {
        void onLoadMore(int page);
    }
    
    private OnLoadMoreListener loadMoreListener;
    
    public LazyPosterAdapter(Activity activity, String dataType) {
        this.activity = activity;
        this.dataType = dataType;
        this.items = new ArrayList<>();
        this.repository = DataRepository.getInstance();
        this.mainHandler = new Handler(Looper.getMainLooper());
        
        // Initialize repository if not already done
        repository.initialize(activity);
    }
    
    @Override
    public int getItemViewType(int position) {
        if (position == items.size()) {
            if (showErrorFooter) {
                return VIEW_TYPE_ERROR;
            } else if (showLoadingFooter) {
                return VIEW_TYPE_LOADING;
            }
        }
        return VIEW_TYPE_ITEM;
    }
    
    @Override
    public int getItemCount() {
        int baseCount = items.size();
        if (showLoadingFooter || showErrorFooter) {
            baseCount += 1;
        }
        return baseCount;
    }
    
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        
        switch (viewType) {
            case VIEW_TYPE_LOADING:
                View loadingView = inflater.inflate(R.layout.item_loading, parent, false);
                return new LoadingViewHolder(loadingView);
                
            case VIEW_TYPE_ERROR:
                View errorView = inflater.inflate(R.layout.item_error, parent, false);
                return new ErrorViewHolder(errorView);
                
            default:
                View itemView = inflater.inflate(R.layout.item_poster_grid, parent, false);
                return new PosterViewHolder(itemView);
        }
    }
    
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_ITEM:
                if (position < items.size()) {
                    PosterViewHolder posterHolder = (PosterViewHolder) holder;
                    posterHolder.bind(items.get(position));
                    
                    // Check if we need to load more data
                    if (position >= items.size() - PRELOAD_THRESHOLD && hasMoreData && !isLoading) {
                        loadNextPage();
                    }
                }
                break;
                
            case VIEW_TYPE_ERROR:
                ErrorViewHolder errorHolder = (ErrorViewHolder) holder;
                errorHolder.bind(errorMessage);
                break;
                
            case VIEW_TYPE_LOADING:
                // Loading view doesn't need binding
                break;
        }
    }
    
    /**
     * Load the initial page of data
     */
    public void loadInitialData() {
        currentPage = 0;
        items.clear();
        hasMoreData = true;
        showErrorFooter = false;
        showLoadingFooter = true;
        notifyDataSetChanged();
        
        loadDataPage(0);
    }
    
    /**
     * Load the next page of data
     */
    private void loadNextPage() {
        if (isLoading || !hasMoreData) {
            return;
        }
        
        currentPage++;
        loadDataPage(currentPage);
    }
    
    /**
     * Load a specific page of data
     */
    private void loadDataPage(int page) {
        if (isLoading) {
            return;
        }
        
        isLoading = true;
        showLoadingFooter = true;
        showErrorFooter = false;
        
        if (page == 0) {
            notifyDataSetChanged();
        } else {
            notifyItemChanged(items.size()); // Update loading footer
        }
        
        Log.d(TAG, "Loading page " + page + " for " + dataType);
        
        DataRepository.DataCallback<List<Poster>> callback = new DataRepository.DataCallback<List<Poster>>() {
            @Override
            public void onSuccess(List<Poster> data) {
                mainHandler.post(() -> {
                    isLoading = false;
                    showLoadingFooter = false;
                    
                    if (data == null || data.isEmpty()) {
                        hasMoreData = false;
                        Log.d(TAG, "No more data available");
                    } else {
                        int insertPosition = items.size();
                        items.addAll(data);
                        
                        if (page == 0) {
                            notifyDataSetChanged();
                        } else {
                            notifyItemRangeInserted(insertPosition, data.size());
                            notifyItemRemoved(items.size()); // Remove loading footer
                        }
                        
                        // Check if we got fewer items than expected (end of data)
                        if (data.size() < PAGE_SIZE) {
                            hasMoreData = false;
                            Log.d(TAG, "Reached end of data (got " + data.size() + " items)");
                        }
                        
                        Log.d(TAG, "Loaded " + data.size() + " items. Total: " + items.size());
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                mainHandler.post(() -> {
                    isLoading = false;
                    showLoadingFooter = false;
                    showErrorFooter = true;
                    errorMessage = error;
                    
                    if (page == 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyItemChanged(items.size()); // Update to show error
                    }
                    
                    Log.e(TAG, "Error loading page " + page + ": " + error);
                });
            }
            
            @Override
            public void onLoading() {
                // Already handled in loadDataPage
            }
        };
        
        // Load data based on type and filters
        if (!searchQuery.isEmpty()) {
            loadSearchResults(callback);
        } else if (genreFilter != -1) {
            loadGenreFiltered(callback);
        } else {
            loadRegularData(page, callback);
        }
    }
    
    /**
     * Load regular paginated data
     */
    private void loadRegularData(int page, DataRepository.DataCallback<List<Poster>> callback) {
        switch (dataType) {
            case "movies":
                repository.getMoviesPaginated(page, PAGE_SIZE, callback);
                break;
            case "tv_series":
                repository.getTvSeriesPaginated(page, PAGE_SIZE, callback);
                break;
            default:
                // Load all movies for now, can be extended
                repository.getMoviesPaginated(page, PAGE_SIZE, callback);
                break;
        }
    }
    
    /**
     * Load search results
     */
    private void loadSearchResults(DataRepository.DataCallback<List<Poster>> callback) {
        switch (dataType) {
            case "movies":
                repository.searchMovies(searchQuery, callback);
                break;
            case "tv_series":
                repository.searchTvSeries(searchQuery, callback);
                break;
            default:
                repository.searchMovies(searchQuery, callback);
                break;
        }
    }
    
    /**
     * Load genre filtered results
     */
    private void loadGenreFiltered(DataRepository.DataCallback<List<Poster>> callback) {
        repository.getMoviesByGenre(genreFilter, callback);
    }
    
    /**
     * Set search query and reload data
     */
    public void setSearchQuery(String query) {
        this.searchQuery = query;
        hasMoreData = !query.isEmpty(); // Disable pagination for search
        loadInitialData();
    }
    
    /**
     * Set genre filter and reload data
     */
    public void setGenreFilter(int genreId) {
        this.genreFilter = genreId;
        hasMoreData = genreId == -1; // Disable pagination for filtered results
        loadInitialData();
    }
    
    /**
     * Clear all filters
     */
    public void clearFilters() {
        this.searchQuery = "";
        this.genreFilter = -1;
        this.hasMoreData = true;
        loadInitialData();
    }
    
    /**
     * Refresh data (force reload from server)
     */
    public void refreshData() {
        repository.refreshData(new DataRepository.ApiResponseCallback() {
            @Override
            public void onSuccess(JsonApiResponse response) {
                loadInitialData();
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error refreshing data: " + error);
            }
            
            @Override
            public void onFromCache(JsonApiResponse response) {
                loadInitialData();
            }
        });
    }
    
    /**
     * Get the current number of loaded items
     */
    public int getLoadedItemCount() {
        return items.size();
    }
    
    /**
     * Check if more data is available
     */
    public boolean hasMoreData() {
        return hasMoreData;
    }
    
    // ===== VIEW HOLDERS =====
    
    /**
     * ViewHolder for poster items
     */
    class PosterViewHolder extends RecyclerView.ViewHolder {
        private ImageView posterImage;
        private TextView titleText;
        private TextView yearText;
        private TextView ratingText;
        private LinearLayout container;
        
        PosterViewHolder(@NonNull View itemView) {
            super(itemView);
            posterImage = itemView.findViewById(R.id.image_view_poster);
            titleText = itemView.findViewById(R.id.text_view_title);
            yearText = itemView.findViewById(R.id.text_view_year);
            ratingText = itemView.findViewById(R.id.text_view_rating);
            container = itemView.findViewById(R.id.linear_layout_container);
        }
        
        void bind(Poster poster) {
            if (poster == null) return;
            
            // Set title
            if (titleText != null && poster.getTitle() != null) {
                titleText.setText(poster.getTitle());
            }
            
            // Set year
            if (yearText != null && poster.getYear() != null) {
                yearText.setText(poster.getYear());
            }
            
            // Set rating
            if (ratingText != null) {
                if (poster.getRating() > 0) {
                    ratingText.setText(String.valueOf(poster.getRating()));
                    ratingText.setVisibility(View.VISIBLE);
                } else {
                    ratingText.setVisibility(View.GONE);
                }
            }
            
            // Load poster image
            if (posterImage != null && poster.getImage() != null) {
                Picasso.get()
                    .load(poster.getImage())
                    .placeholder(R.drawable.poster_placeholder)
                    .error(R.drawable.poster_error)
                    .fit()
                    .centerCrop()
                    .into(posterImage);
            }
            
            // Set click listener
            if (container != null) {
                container.setOnClickListener(v -> {
                    Intent intent;
                    
                    if ("serie".equals(poster.getType()) || "series".equals(poster.getType())) {
                        intent = new Intent(activity, SerieActivity.class);
                    } else {
                        intent = new Intent(activity, MovieActivity.class);
                    }
                    
                    intent.putExtra("poster", poster);
                    intent.putExtra("from", "list");
                    activity.startActivity(intent);
                });
            }
        }
    }
    
    /**
     * ViewHolder for loading indicator
     */
    class LoadingViewHolder extends RecyclerView.ViewHolder {
        private ProgressBar progressBar;
        private TextView loadingText;
        
        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
            loadingText = itemView.findViewById(R.id.text_view_loading);
        }
    }
    
    /**
     * ViewHolder for error state
     */
    class ErrorViewHolder extends RecyclerView.ViewHolder {
        private TextView errorText;
        private TextView retryButton;
        
        ErrorViewHolder(@NonNull View itemView) {
            super(itemView);
            errorText = itemView.findViewById(R.id.text_view_error);
            retryButton = itemView.findViewById(R.id.text_view_retry);
            
            if (retryButton != null) {
                retryButton.setOnClickListener(v -> {
                    showErrorFooter = false;
                    loadNextPage();
                });
            }
        }
        
        void bind(String error) {
            if (errorText != null) {
                errorText.setText(error);
            }
        }
    }
}