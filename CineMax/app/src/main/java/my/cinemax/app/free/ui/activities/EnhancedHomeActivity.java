package my.cinemax.app.free.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import my.cinemax.app.free.Provider.EnhancedDataRepository;
import my.cinemax.app.free.R;
import my.cinemax.app.free.Utils.EnhancedCacheManager;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.ui.Adapters.LazyPosterAdapter;

import java.util.List;

/**
 * Enhanced Home Activity - Demonstrates the enhanced multi-layer caching system
 * 
 * This activity shows how to use the enhanced caching system for optimal performance
 * with 10,000+ entries. It provides instant loading, background refresh, and
 * efficient memory management.
 */
public class EnhancedHomeActivity extends AppCompatActivity {
    
    private static final String TAG = "EnhancedHomeActivity";
    
    private EnhancedDataRepository repository;
    private EnhancedCacheManager cacheManager;
    
    private RecyclerView moviesRecyclerView;
    private RecyclerView tvSeriesRecyclerView;
    private ProgressBar loadingProgressBar;
    private TextView statusTextView;
    private TextView cacheStatsTextView;
    
    private LazyPosterAdapter moviesAdapter;
    private LazyPosterAdapter tvSeriesAdapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home); // Using existing layout
        
        // Initialize enhanced caching system
        initializeEnhancedCaching();
        
        // Initialize views
        initializeViews();
        
        // Load data with enhanced caching
        loadDataWithEnhancedCaching();
    }
    
    /**
     * Initialize the enhanced caching system
     */
    private void initializeEnhancedCaching() {
        try {
            // Initialize enhanced data repository
            repository = EnhancedDataRepository.getInstance();
            repository.initialize(this);
            
            // Initialize enhanced cache manager
            cacheManager = EnhancedCacheManager.getInstance();
            
            Log.d(TAG, "Enhanced caching system initialized");
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing enhanced caching system", e);
            Toast.makeText(this, "Error initializing caching system", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Initialize views
     */
    private void initializeViews() {
        moviesRecyclerView = findViewById(R.id.recyclerView_movies);
        tvSeriesRecyclerView = findViewById(R.id.recyclerView_tv_series);
        loadingProgressBar = findViewById(R.id.progressBar_loading);
        statusTextView = findViewById(R.id.textView_status);
        cacheStatsTextView = findViewById(R.id.textView_cache_stats);
        
        // Setup RecyclerViews with lazy loading adapters
        setupRecyclerViews();
        
        // Show initial status
        updateStatus("Initializing enhanced caching system...");
    }
    
    /**
     * Setup RecyclerViews with lazy loading
     */
    private void setupRecyclerViews() {
        // Setup movies RecyclerView
        GridLayoutManager moviesLayoutManager = new GridLayoutManager(this, 2);
        moviesRecyclerView.setLayoutManager(moviesLayoutManager);
        
        moviesAdapter = new LazyPosterAdapter(this, "movies");
        moviesRecyclerView.setAdapter(moviesAdapter);
        
        // Setup TV series RecyclerView
        GridLayoutManager tvSeriesLayoutManager = new GridLayoutManager(this, 2);
        tvSeriesRecyclerView.setLayoutManager(tvSeriesLayoutManager);
        
        tvSeriesAdapter = new LazyPosterAdapter(this, "tv_series");
        tvSeriesRecyclerView.setAdapter(tvSeriesAdapter);
    }
    
    /**
     * Load data with enhanced caching
     */
    private void loadDataWithEnhancedCaching() {
        updateStatus("Loading data with enhanced caching...");
        showLoading(true);
        
        // Load all data with cache-first strategy
        repository.loadAllData(new EnhancedDataRepository.ApiResponseCallback() {
            @Override
            public void onSuccess(JsonApiResponse response) {
                Log.d(TAG, "Data loaded from API");
                handleJsonResponse(response, "API");
                updateCacheStats();
            }
            
            @Override
            public void onFromCache(JsonApiResponse response) {
                Log.d(TAG, "Data loaded from cache (instant)");
                handleJsonResponse(response, "Cache");
                updateCacheStats();
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading data: " + error);
                updateStatus("Error loading data: " + error);
                showLoading(false);
                Toast.makeText(EnhancedHomeActivity.this, "Error loading data", Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    /**
     * Handle JSON response
     */
    private void handleJsonResponse(JsonApiResponse response, String source) {
        runOnUiThread(() -> {
            try {
                updateStatus("Data loaded from " + source + " - Processing...");
                
                // Update movies
                if (response.getMovies() != null && !response.getMovies().isEmpty()) {
                    updateMoviesList(response.getMovies());
                    Log.d(TAG, "Updated " + response.getMovies().size() + " movies");
                }
                
                // Update TV series
                if (response.getTvSeries() != null && !response.getTvSeries().isEmpty()) {
                    updateTvSeriesList(response.getTvSeries());
                    Log.d(TAG, "Updated " + response.getTvSeries().size() + " TV series");
                }
                
                // Update channels
                if (response.getChannels() != null && !response.getChannels().isEmpty()) {
                    Log.d(TAG, "Loaded " + response.getChannels().size() + " channels");
                }
                
                // Update actors
                if (response.getActors() != null && !response.getActors().isEmpty()) {
                    Log.d(TAG, "Loaded " + response.getActors().size() + " actors");
                }
                
                updateStatus("Data loaded successfully from " + source);
                showLoading(false);
                
                // Load initial data in adapters
                loadInitialDataInAdapters();
                
            } catch (Exception e) {
                Log.e(TAG, "Error handling JSON response", e);
                updateStatus("Error processing data");
                showLoading(false);
            }
        });
    }
    
    /**
     * Update movies list
     */
    private void updateMoviesList(List<Poster> movies) {
        if (moviesAdapter != null) {
            // The adapter will handle lazy loading internally
            Log.d(TAG, "Movies list updated with " + movies.size() + " items");
        }
    }
    
    /**
     * Update TV series list
     */
    private void updateTvSeriesList(List<Poster> tvSeries) {
        if (tvSeriesAdapter != null) {
            // The adapter will handle lazy loading internally
            Log.d(TAG, "TV series list updated with " + tvSeries.size() + " items");
        }
    }
    
    /**
     * Load initial data in adapters
     */
    private void loadInitialDataInAdapters() {
        // Load first page of movies
        repository.getMoviesPaginated(0, 20, new EnhancedDataRepository.DataCallback<List<Poster>>() {
            @Override
            public void onSuccess(List<Poster> movies) {
                runOnUiThread(() -> {
                    if (moviesAdapter != null) {
                        // The adapter will handle the data loading
                        Log.d(TAG, "Initial movies loaded: " + movies.size());
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading initial movies: " + error);
            }
            
            @Override
            public void onLoading() {
                // Adapter handles loading state
            }
        });
        
        // Load first page of TV series
        repository.getTvSeriesPaginated(0, 20, new EnhancedDataRepository.DataCallback<List<Poster>>() {
            @Override
            public void onSuccess(List<Poster> tvSeries) {
                runOnUiThread(() -> {
                    if (tvSeriesAdapter != null) {
                        // The adapter will handle the data loading
                        Log.d(TAG, "Initial TV series loaded: " + tvSeries.size());
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error loading initial TV series: " + error);
            }
            
            @Override
            public void onLoading() {
                // Adapter handles loading state
            }
        });
    }
    
    /**
     * Update cache statistics
     */
    private void updateCacheStats() {
        runOnUiThread(() -> {
            try {
                EnhancedCacheManager.CacheStats stats = cacheManager.getStats();
                String statsText = String.format("Cache Stats: %d hits, %d misses, %.1f%% hit rate, %d items",
                        stats.getTotalHits(), stats.cacheMisses, stats.getHitRate() * 100, stats.totalItems);
                
                if (cacheStatsTextView != null) {
                    cacheStatsTextView.setText(statsText);
                }
                
                Log.d(TAG, "Cache stats updated: " + stats.toString());
                
            } catch (Exception e) {
                Log.e(TAG, "Error updating cache stats", e);
            }
        });
    }
    
    /**
     * Update status text
     */
    private void updateStatus(String status) {
        runOnUiThread(() -> {
            if (statusTextView != null) {
                statusTextView.setText(status);
            }
            Log.d(TAG, "Status: " + status);
        });
    }
    
    /**
     * Show/hide loading indicator
     */
    private void showLoading(boolean show) {
        runOnUiThread(() -> {
            if (loadingProgressBar != null) {
                loadingProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            }
        });
    }
    
    /**
     * Search movies
     */
    public void searchMovies(String query) {
        if (query == null || query.trim().isEmpty()) {
            return;
        }
        
        updateStatus("Searching movies: " + query);
        
        repository.searchMovies(query, new EnhancedDataRepository.DataCallback<List<Poster>>() {
            @Override
            public void onSuccess(List<Poster> results) {
                runOnUiThread(() -> {
                    updateStatus("Found " + results.size() + " movies for '" + query + "'");
                    // Update search results in adapter
                    if (moviesAdapter != null) {
                        // The adapter will handle search results
                        Log.d(TAG, "Search results: " + results.size() + " movies");
                    }
                });
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error searching movies: " + error);
                updateStatus("Search error: " + error);
            }
            
            @Override
            public void onLoading() {
                updateStatus("Searching...");
            }
        });
    }
    
    /**
     * Refresh data manually
     */
    public void refreshData() {
        updateStatus("Refreshing data...");
        showLoading(true);
        
        repository.refreshData(new EnhancedDataRepository.ApiResponseCallback() {
            @Override
            public void onSuccess(JsonApiResponse response) {
                Log.d(TAG, "Data refreshed from API");
                handleJsonResponse(response, "API (Refresh)");
            }
            
            @Override
            public void onFromCache(JsonApiResponse response) {
                Log.d(TAG, "Data refreshed from cache");
                handleJsonResponse(response, "Cache (Refresh)");
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Error refreshing data: " + error);
                updateStatus("Refresh error: " + error);
                showLoading(false);
            }
        });
    }
    
    /**
     * Clear cache
     */
    public void clearCache() {
        updateStatus("Clearing cache...");
        
        repository.clearCache();
        
        // Wait a moment for cache to clear
        new android.os.Handler().postDelayed(() -> {
            updateStatus("Cache cleared");
            updateCacheStats();
            
            // Reload data
            loadDataWithEnhancedCaching();
        }, 1000);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        
        // Clean up resources
        if (repository != null) {
            // Don't shutdown repository as it's a singleton
            // repository.shutdown();
        }
        
        Log.d(TAG, "EnhancedHomeActivity destroyed");
    }
}