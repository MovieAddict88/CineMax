package my.cinemax.app.free.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.ui.Adapters.PosterAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private String query;
    private SwipeRefreshLayout swipe_refresh_layout_list_search_search;
    private Button button_try_again;
    private LinearLayout linear_layout_layout_error;
    private RecyclerView recycler_view_activity_search;
    private ImageView image_view_empty_list;
    private GridLayoutManager gridLayoutManager;
    private PosterAdapter adapter;

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private Integer page = 0;
    private Integer position = 0;
    private Integer item = 0 ;
    ArrayList<Poster> posterArrayList = new ArrayList<>();
    ArrayList<Channel> channelArrayList = new ArrayList<>();
    private LinearLayout linear_layout_load_search_activity;

    private Integer lines_beetween_ads = 2 ;
    private boolean tabletSize = false;
    private Boolean native_ads_enabled = false ;
    private int type_ads = 0;
    private PrefManager prefManager;

    // JSON API data cache
    private JsonApiResponse cachedJsonResponse = null;
    private List<Poster> allMovies = new ArrayList<>();
    private List<Channel> allChannels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        try {
            setContentView(R.layout.activity_search);
            prefManager = new PrefManager(getApplicationContext());

            initView();
            initAction();
            loadSearchResultsFromJson();
            showAdsBanner();
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in onCreate: " + e.getMessage(), e);
            showError();
        }
    }

    private void initView() {
        try {
            // Initialize tablet size properly
            tabletSize = getResources().getBoolean(R.bool.isTablet);
            
            // Initialize native ads settings
            if (prefManager != null && !prefManager.getString("ADMIN_NATIVE_TYPE").equals("FALSE")) {
                native_ads_enabled = true;
                if (tabletSize) {
                    lines_beetween_ads = 6 * Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
                } else {
                    lines_beetween_ads = 3 * Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
                }
            }
            
            if (checkSUBSCRIBED()) {
                native_ads_enabled = false;
            }

            // Get query from intent with null check
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                this.query = bundle.getString("query", "");
            } else {
                this.query = "";
            }
            
            // Validate query
            if (query == null || query.trim().isEmpty()) {
                Log.w("SearchActivity", "Empty or null search query");
                query = "";
            }
            
            // Setup toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setTitle(query.isEmpty() ? "Search" : query);
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }

            // Initialize views with null checks
            this.linear_layout_load_search_activity = findViewById(R.id.linear_layout_load_search_activity);
            this.swipe_refresh_layout_list_search_search = findViewById(R.id.swipe_refresh_layout_list_search_search);
            button_try_again = findViewById(R.id.button_try_again);
            image_view_empty_list = findViewById(R.id.image_view_empty_list);
            linear_layout_layout_error = findViewById(R.id.linear_layout_layout_error);
            recycler_view_activity_search = findViewById(R.id.recycler_view_activity_search);
            
            // Initialize adapter with null check
            if (recycler_view_activity_search != null) {
                adapter = new PosterAdapter(posterArrayList, this);
                recycler_view_activity_search.setHasFixedSize(true);
                recycler_view_activity_search.setAdapter(adapter);
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in initView: " + e.getMessage(), e);
            showError();
        }
    }

    /**
     * Load search results from GitHub JSON API instead of old server API
     */
    private void loadSearchResultsFromJson() {
        try {
            if (swipe_refresh_layout_list_search_search != null) {
                swipe_refresh_layout_list_search_search.setRefreshing(false);
            }
            
            // Show loading state
            if (linear_layout_load_search_activity != null) {
                linear_layout_load_search_activity.setVisibility(View.VISIBLE);
            }
            
            // If we already have cached data, use it
            if (cachedJsonResponse != null && !allMovies.isEmpty()) {
                filterAndDisplaySearchResults();
                return;
            }
            
            // Load data from GitHub JSON API
            apiClient.getJsonApiData(new apiClient.JsonApiCallback() {
                @Override
                public void onSuccess(JsonApiResponse jsonResponse) {
                    try {
                        if (jsonResponse != null) {
                            cachedJsonResponse = jsonResponse;
                            allMovies = jsonResponse.getMovies() != null ? jsonResponse.getMovies() : new ArrayList<>();
                            allChannels = jsonResponse.getChannels() != null ? jsonResponse.getChannels() : new ArrayList<>();
                            filterAndDisplaySearchResults();
                        } else {
                            Log.w("SearchActivity", "Received null JSON response");
                            showError();
                        }
                    } catch (Exception e) {
                        Log.e("SearchActivity", "Error processing JSON response: " + e.getMessage(), e);
                        showError();
                    }
                }
                
                @Override
                public void onError(String error) {
                    Log.e("SearchActivity", "Error loading JSON data: " + error);
                    runOnUiThread(() -> {
                        showError();
                        Toast.makeText(SearchActivity.this, "Failed to load search data: " + error, Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in loadSearchResultsFromJson: " + e.getMessage(), e);
            showError();
        }
    }
    
    /**
     * Filter movies and channels by search query and display them
     */
    private void filterAndDisplaySearchResults() {
        try {
            // Clear existing data safely
            if (posterArrayList != null) {
                posterArrayList.clear();
            } else {
                posterArrayList = new ArrayList<>();
            }
            
            if (channelArrayList != null) {
                channelArrayList.clear();
            } else {
                channelArrayList = new ArrayList<>();
            }
            
            // Validate search query
            if (query == null || query.trim().isEmpty()) {
                Log.w("SearchActivity", "Empty search query, showing empty results");
                displayResults();
                return;
            }
            
            String searchQuery = query.toLowerCase().trim();
            
            // Filter channels by search query with null checks
            if (allChannels != null) {
                for (Channel channel : allChannels) {
                    try {
                        if (channel != null && channel.getTitle() != null && 
                            channel.getTitle().toLowerCase().contains(searchQuery)) {
                            channelArrayList.add(channel);
                        }
                    } catch (Exception e) {
                        Log.w("SearchActivity", "Error filtering channel: " + e.getMessage());
                    }
                }
            }
            
            // Filter movies by search query with null checks
            if (allMovies != null) {
                for (Poster movie : allMovies) {
                    try {
                        if (movie != null && movie.getTitle() != null && 
                            movie.getTitle().toLowerCase().contains(searchQuery)) {
                            posterArrayList.add(movie.setTypeView(1));
                        }
                    } catch (Exception e) {
                        Log.w("SearchActivity", "Error filtering movie: " + e.getMessage());
                    }
                }
            }
            
            // Add channel section header if channels found
            if (channelArrayList.size() > 0) {
                posterArrayList.add(0, new Poster().setTypeView(3));
                setupGridLayoutForChannels();
            } else {
                setupGridLayoutForMovies();
            }
            
            // Add native ads to movies
            if (native_ads_enabled) {
                addNativeAdsToMovies();
            }
            
            displayResults();
            
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in filterAndDisplaySearchResults: " + e.getMessage(), e);
            showError();
        }
    }
    
    /**
     * Display the filtered results
     */
    private void displayResults() {
        try {
            runOnUiThread(() -> {
                try {
                    // Display results based on data availability
                    if ((channelArrayList == null || channelArrayList.size() == 0) && 
                        (posterArrayList == null || posterArrayList.size() == 0)) {
                        // Show empty state
                        if (linear_layout_layout_error != null) {
                            linear_layout_layout_error.setVisibility(View.GONE);
                        }
                        if (recycler_view_activity_search != null) {
                            recycler_view_activity_search.setVisibility(View.GONE);
                        }
                        if (image_view_empty_list != null) {
                            image_view_empty_list.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Show results
                        if (linear_layout_layout_error != null) {
                            linear_layout_layout_error.setVisibility(View.GONE);
                        }
                        if (recycler_view_activity_search != null) {
                            recycler_view_activity_search.setVisibility(View.VISIBLE);
                        }
                        if (image_view_empty_list != null) {
                            image_view_empty_list.setVisibility(View.GONE);
                        }
                    }
                    
                    // Update UI elements
                    if (swipe_refresh_layout_list_search_search != null) {
                        swipe_refresh_layout_list_search_search.setRefreshing(false);
                    }
                    if (linear_layout_load_search_activity != null) {
                        linear_layout_load_search_activity.setVisibility(View.GONE);
                    }
                    
                    // Setup RecyclerView
                    if (recycler_view_activity_search != null && gridLayoutManager != null) {
                        recycler_view_activity_search.setLayoutManager(gridLayoutManager);
                    }
                    
                    // Notify adapter
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Log.e("SearchActivity", "Error updating UI: " + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in displayResults: " + e.getMessage(), e);
        }
    }
    
    /**
     * Setup grid layout for channels section
     */
    private void setupGridLayoutForChannels() {
        try {
            if (tabletSize) {
                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                if (native_ads_enabled) {
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return ((position) % (lines_beetween_ads + 1) == 0 || position == 0) ? 6 : 1;
                        }
                    });
                } else {
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return (position == 0) ? 6 : 1;
                        }
                    });
                }
            } else {
                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3, RecyclerView.VERTICAL, false);
                if (native_ads_enabled) {
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return ((position) % (lines_beetween_ads + 1) == 0 || position == 0) ? 3 : 1;
                        }
                    });
                } else {
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return (position == 0) ? 3 : 1;
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in setupGridLayoutForChannels: " + e.getMessage(), e);
            // Fallback to simple grid layout
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), tabletSize ? 6 : 3, RecyclerView.VERTICAL, false);
        }
    }
    
    /**
     * Setup grid layout for movies only
     */
    private void setupGridLayoutForMovies() {
        try {
            if (tabletSize) {
                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                if (native_ads_enabled) {
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return ((position + 1) % (lines_beetween_ads + 1) == 0 && position != 0) ? 6 : 1;
                        }
                    });
                }
            } else {
                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3, RecyclerView.VERTICAL, false);
                if (native_ads_enabled) {
                    gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            return ((position + 1) % (lines_beetween_ads + 1) == 0 && position != 0) ? 3 : 1;
                        }
                    });
                }
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in setupGridLayoutForMovies: " + e.getMessage(), e);
            // Fallback to simple grid layout
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), tabletSize ? 6 : 3, RecyclerView.VERTICAL, false);
        }
    }
    
    /**
     * Add native ads to movies list
     */
    private void addNativeAdsToMovies() {
        try {
            if (posterArrayList == null || prefManager == null) {
                return;
            }
            
            item = 0;
            for (int i = 0; i < posterArrayList.size(); i++) {
                try {
                    if (posterArrayList.get(i) != null && posterArrayList.get(i).getTypeView() == 1) { // Only for movies, not channels
                        item++;
                        if (item == lines_beetween_ads) {
                            item = 0;
                            String nativeType = prefManager.getString("ADMIN_NATIVE_TYPE");
                            if ("FACEBOOK".equals(nativeType)) {
                                posterArrayList.add(i + 1, new Poster().setTypeView(4));
                            } else if ("ADMOB".equals(nativeType)) {
                                posterArrayList.add(i + 1, new Poster().setTypeView(5));
                            } else if ("BOTH".equals(nativeType)) {
                                if (type_ads == 0) {
                                    posterArrayList.add(i + 1, new Poster().setTypeView(4));
                                    type_ads = 1;
                                } else if (type_ads == 1) {
                                    posterArrayList.add(i + 1, new Poster().setTypeView(5));
                                    type_ads = 0;
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.w("SearchActivity", "Error adding native ad at position " + i + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in addNativeAdsToMovies: " + e.getMessage(), e);
        }
    }
    
    /**
     * Show error state
     */
    private void showError() {
        try {
            runOnUiThread(() -> {
                try {
                    if (linear_layout_layout_error != null) {
                        linear_layout_layout_error.setVisibility(View.VISIBLE);
                    }
                    if (recycler_view_activity_search != null) {
                        recycler_view_activity_search.setVisibility(View.GONE);
                    }
                    if (image_view_empty_list != null) {
                        image_view_empty_list.setVisibility(View.GONE);
                    }
                    if (swipe_refresh_layout_list_search_search != null) {
                        swipe_refresh_layout_list_search_search.setVisibility(View.GONE);
                    }
                    if (linear_layout_load_search_activity != null) {
                        linear_layout_load_search_activity.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("SearchActivity", "Error updating error UI: " + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in showError: " + e.getMessage(), e);
        }
    }

    /**
     * @deprecated Old API method - replaced with loadSearchResultsFromJson()
     */
    @Deprecated
    private void loadPosters() {
        // This method is kept for backward compatibility but now redirects to JSON API
        loadSearchResultsFromJson();
    }

    private void initAction() {
        try {
            if (swipe_refresh_layout_list_search_search != null) {
                swipe_refresh_layout_list_search_search.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        try {
                            if (posterArrayList != null) {
                                posterArrayList.clear();
                            }
                            if (channelArrayList != null) {
                                channelArrayList.clear();
                            }
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            loadSearchResultsFromJson();
                        } catch (Exception e) {
                            Log.e("SearchActivity", "Error in swipe refresh: " + e.getMessage(), e);
                        }
                    }
                });
            }
            
            if (button_try_again != null) {
                button_try_again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (posterArrayList != null) {
                                posterArrayList.clear();
                            }
                            if (channelArrayList != null) {
                                channelArrayList.clear();
                            }
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            loadSearchResultsFromJson();
                        } catch (Exception e) {
                            Log.e("SearchActivity", "Error in try again: " + e.getMessage(), e);
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in initAction: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem itemMenu) {
        switch (itemMenu.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(itemMenu);
    }
    
    public boolean checkSUBSCRIBED(){
        try {
            PrefManager prefManager = new PrefManager(getApplicationContext());
            if (!prefManager.getString("SUBSCRIBED").equals("TRUE") && !prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e("SearchActivity", "Error checking subscription: " + e.getMessage(), e);
            return false;
        }
    }
    
    public void showAdsBanner() {
        try {
            if (!checkSUBSCRIBED()) {
                PrefManager prefManager = new PrefManager(getApplicationContext());
                if (prefManager != null && !prefManager.getString("ADMIN_BANNER_TYPE").equals("FALSE")) {
                    showAdmobBanner();
                }
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "Error showing ads banner: " + e.getMessage(), e);
        }
    }
    
    public void showAdmobBanner(){
        try {
            PrefManager prefManager = new PrefManager(getApplicationContext());
            LinearLayout linear_layout_ads = (LinearLayout) findViewById(R.id.linear_layout_ads);
            
            if (linear_layout_ads != null && prefManager != null) {
                final AdView mAdView = new AdView(this);
                mAdView.setAdSize(AdSize.SMART_BANNER);
                mAdView.setAdUnitId(prefManager.getString("ADMIN_BANNER_ADMOB_ID"));
                AdRequest adRequest = new AdRequest.Builder().build();
                mAdView.loadAd(adRequest);
                linear_layout_ads.addView(mAdView);

                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        mAdView.setVisibility(View.VISIBLE);
                    }
                });
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "Error showing AdMob banner: " + e.getMessage(), e);
        }
    }
}
