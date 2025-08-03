package my.cinemax.app.free.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import my.cinemax.app.free.Provider.DataRepository;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.ui.Adapters.LazyPosterAdapter;
import my.cinemax.app.free.Utils.CacheManager;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

/**
 * OptimizedMoviesFragment - Demonstrates the new caching system
 * 
 * Features:
 * - Lazy loading with pagination
 * - Persistent caching for large datasets
 * - Memory efficient rendering
 * - Search and filtering capabilities
 * - Offline support
 * - Optimized for 10,000+ movies
 */
public class OptimizedMoviesFragment extends Fragment {
    
    private static final String TAG = "OptimizedMoviesFragment";
    
    // UI Components
    private View view;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LazyPosterAdapter adapter;
    private ProgressBar progressBar;
    private TextView statusText;
    private LinearLayout filterContainer;
    private CardView filterCard;
    private RelativeLayout filterButton;
    private Spinner genreSpinner;
    private EditText searchEdit;
    private ImageView searchButton;
    private ImageView clearButton;
    
    // Data and state
    private DataRepository dataRepository;
    private List<Genre> genreList;
    private boolean isFiltersVisible = false;
    private int selectedGenreId = -1;
    private String currentSearchQuery = "";
    
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_optimized_movies, container, false);
        
        initComponents();
        initData();
        initListeners();
        
        return view;
    }
    
    /**
     * Initialize UI components
     */
    private void initComponents() {
        recyclerView = view.findViewById(R.id.recycler_view_movies);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_movies);
        progressBar = view.findViewById(R.id.progress_bar_movies);
        statusText = view.findViewById(R.id.text_view_status);
        filterContainer = view.findViewById(R.id.linear_layout_filters);
        filterCard = view.findViewById(R.id.card_view_filters);
        filterButton = view.findViewById(R.id.relative_layout_filter_button);
        genreSpinner = view.findViewById(R.id.spinner_genre);
        searchEdit = view.findViewById(R.id.edit_text_search);
        searchButton = view.findViewById(R.id.image_view_search);
        clearButton = view.findViewById(R.id.image_view_clear);
        
        // Setup RecyclerView
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        
        // Create adapter for movies
        adapter = new LazyPosterAdapter(getActivity(), "movies");
        recyclerView.setAdapter(adapter);
        
        // Configure swipe refresh
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        
        // Initially hide filter container
        filterContainer.setVisibility(View.GONE);
        isFiltersVisible = false;
    }
    
    /**
     * Initialize data repository and load genres
     */
    private void initData() {
        dataRepository = DataRepository.getInstance();
        dataRepository.initialize(getContext());
        
        loadGenres();
        loadMovies();
    }
    
    /**
     * Initialize event listeners
     */
    private void initListeners() {
        // Filter button click
        filterButton.setOnClickListener(v -> toggleFilters());
        
        // Search button click
        searchButton.setOnClickListener(v -> performSearch());
        
        // Clear button click
        clearButton.setOnClickListener(v -> clearSearch());
        
        // Swipe refresh
        swipeRefreshLayout.setOnRefreshListener(() -> {
            refreshData();
        });
        
        // Genre spinner selection
        genreSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    // "All Genres" selected
                    selectedGenreId = -1;
                    adapter.clearFilters();
                } else if (genreList != null && position <= genreList.size()) {
                    // Specific genre selected
                    selectedGenreId = genreList.get(position - 1).getId();
                    adapter.setGenreFilter(selectedGenreId);
                    showStatus("Loading " + genreList.get(position - 1).getTitle() + " movies...");
                }
            }
            
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }
    
    /**
     * Load genres for filtering
     */
    private void loadGenres() {
        dataRepository.getGenres(new DataRepository.DataCallback<List<Genre>>() {
            @Override
            public void onSuccess(List<Genre> genres) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        genreList = genres;
                        setupGenreSpinner(genres);
                    });
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading genres: " + error);
            }
            
            @Override
            public void onLoading() {
                // Genres loading doesn't need UI feedback
            }
        });
    }
    
    /**
     * Setup genre spinner with loaded genres
     */
    private void setupGenreSpinner(List<Genre> genres) {
        List<String> genreNames = new ArrayList<>();
        genreNames.add("All Genres");
        
        for (Genre genre : genres) {
            genreNames.add(genre.getTitle());
        }
        
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
            getContext(), 
            android.R.layout.simple_spinner_item, 
            genreNames
        );
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        genreSpinner.setAdapter(spinnerAdapter);
    }
    
    /**
     * Load movies using lazy loading
     */
    private void loadMovies() {
        showStatus("Loading movies...");
        showProgress(true);
        
        adapter.loadInitialData();
        
        // Hide progress after a short delay (adapter will handle loading states)
        view.postDelayed(() -> {
            showProgress(false);
            showCacheStats();
        }, 1000);
    }
    
    /**
     * Refresh data from server
     */
    private void refreshData() {
        Log.d(TAG, "Refreshing data from server");
        
        adapter.refreshData();
        
        swipeRefreshLayout.postDelayed(() -> {
            swipeRefreshLayout.setRefreshing(false);
            showCacheStats();
            Toasty.success(getContext(), "Content refreshed", Toast.LENGTH_SHORT).show();
        }, 2000);
    }
    
    /**
     * Perform search
     */
    private void performSearch() {
        String query = searchEdit.getText().toString().trim();
        
        if (query.isEmpty()) {
            Toasty.warning(getContext(), "Enter search term", Toast.LENGTH_SHORT).show();
            return;
        }
        
        currentSearchQuery = query;
        selectedGenreId = -1; // Clear genre filter when searching
        genreSpinner.setSelection(0); // Reset spinner to "All Genres"
        
        adapter.setSearchQuery(query);
        showStatus("Searching for \"" + query + "\"...");
        
        Log.d(TAG, "Searching for: " + query);
    }
    
    /**
     * Clear search and filters
     */
    private void clearSearch() {
        searchEdit.setText("");
        currentSearchQuery = "";
        selectedGenreId = -1;
        genreSpinner.setSelection(0);
        
        adapter.clearFilters();
        showStatus("Showing all movies");
        
        Log.d(TAG, "Cleared search and filters");
    }
    
    /**
     * Toggle filter visibility
     */
    private void toggleFilters() {
        isFiltersVisible = !isFiltersVisible;
        
        if (isFiltersVisible) {
            filterContainer.setVisibility(View.VISIBLE);
            filterCard.animate().alpha(1.0f).setDuration(300);
        } else {
            filterCard.animate().alpha(0.0f).setDuration(300).withEndAction(() -> {
                if (filterContainer != null) {
                    filterContainer.setVisibility(View.GONE);
                }
            });
        }
    }
    
    /**
     * Show loading progress
     */
    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }
    
    /**
     * Show status message
     */
    private void showStatus(String message) {
        if (statusText != null) {
            statusText.setText(message);
            statusText.setVisibility(View.VISIBLE);
            
            // Hide status after 3 seconds
            statusText.postDelayed(() -> {
                if (statusText != null) {
                    statusText.setVisibility(View.GONE);
                }
            }, 3000);
        }
        
        Log.d(TAG, "Status: " + message);
    }
    
    /**
     * Show cache statistics (for debugging)
     */
    private void showCacheStats() {
        if (dataRepository != null) {
            CacheManager.CacheStats stats = dataRepository.getCacheStats();
            String message = "Cache: " + stats.movieCount + " movies, " + 
                           stats.channelCount + " channels";
            
            Log.d(TAG, "Cache stats: " + stats.toString());
            
            // Show cache info in debug mode
            PrefManager prefManager = new PrefManager(getContext());
            if ("true".equals(prefManager.getString("DEBUG_MODE"))) {
                showStatus(message);
            }
        }
    }
    
    /**
     * Update movies data from HomeActivity
     */
    public void updateMoviesData(List<my.cinemax.app.free.entity.Poster> movies) {
        if (movies != null && !movies.isEmpty()) {
            Log.d(TAG, "Received " + movies.size() + " movies from HomeActivity");
            // The adapter will load from cache automatically
            loadMovies();
        }
    }
    
    @Override
    public void onResume() {
        super.onResume();
        
        // Reload data if cache is invalid
        if (dataRepository != null && !dataRepository.isCacheValid()) {
            Log.d(TAG, "Cache invalid, reloading data");
            loadMovies();
        }
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        
        // Clean up references
        view = null;
        adapter = null;
        genreList = null;
    }
}