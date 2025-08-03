package my.cinemax.app.free.Provider;

import android.content.Context;
import android.util.Log;
import my.cinemax.app.free.Utils.EnhancedCacheManager;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.entity.*;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Enhanced Data Repository - Multi-layer caching for optimal performance
 * 
 * This repository provides a single source of truth for all data operations
 * using the enhanced multi-layer caching system. It intelligently manages
 * cache and API calls to provide optimal performance for large datasets
 * (10,000+ entries) with instant loading and background refresh.
 * 
 * Features:
 * - Multi-layer cache-first strategy
 * - Automatic background refresh
 * - Memory-efficient pagination
 * - Smart data prefetching
 * - Offline support
 * - Performance monitoring
 */
public class EnhancedDataRepository {
    
    private static final String TAG = "EnhancedDataRepository";
    private static EnhancedDataRepository instance;
    
    private EnhancedCacheManager cacheManager;
    private Context context;
    private ExecutorService executorService;
    private boolean isLoading = false;
    
    // Callback interfaces
    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
        void onLoading();
    }
    
    public interface ApiResponseCallback {
        void onSuccess(JsonApiResponse response);
        void onError(String error);
        void onFromCache(JsonApiResponse response);
    }
    
    private EnhancedDataRepository() {
        this.executorService = Executors.newCachedThreadPool();
        this.cacheManager = EnhancedCacheManager.getInstance();
    }
    
    public static synchronized EnhancedDataRepository getInstance() {
        if (instance == null) {
            instance = new EnhancedDataRepository();
        }
        return instance;
    }
    
    /**
     * Initialize the repository
     */
    public void initialize(Context context) {
        this.context = context.getApplicationContext();
        cacheManager.initialize(this.context);
        Log.d(TAG, "Enhanced DataRepository initialized");
    }
    
    /**
     * Load all data with enhanced cache-first strategy
     */
    public void loadAllData(ApiResponseCallback callback) {
        if (callback != null) {
            callback.onLoading();
        }
        
        // Check cache first
        JsonApiResponse cachedResponse = cacheManager.getCachedApiResponse();
        if (cachedResponse != null && cacheManager.isCacheValid()) {
            Log.d(TAG, "Returning cached data from enhanced cache");
            if (callback != null) {
                callback.onFromCache(cachedResponse);
            }
            
            // Still refresh in background if cache is older than 1 hour
            if (shouldRefreshInBackground()) {
                refreshDataInBackground(null);
            }
            return;
        }
        
        // Load from API
        loadFromApi(callback);
    }
    
    /**
     * Refresh data from API
     */
    public void refreshData(ApiResponseCallback callback) {
        isLoading = true;
        loadFromApi(callback);
    }
    
    /**
     * Load data from API
     */
    private void loadFromApi(ApiResponseCallback callback) {
        apiClient.getJsonApiData(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                isLoading = false;
                
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse jsonResponse = response.body();
                    
                    // Store in enhanced cache
                    cacheManager.storeApiResponse(jsonResponse);
                    
                    Log.d(TAG, "Data loaded from API and stored in enhanced cache");
                    
                    if (callback != null) {
                        callback.onSuccess(jsonResponse);
                    }
                } else {
                    String error = "API response error: " + response.code();
                    Log.e(TAG, error);
                    
                    // Try to return cached data as fallback
                    JsonApiResponse cachedResponse = cacheManager.getCachedApiResponse();
                    if (cachedResponse != null) {
                        Log.d(TAG, "Returning stale cached data as fallback");
                        if (callback != null) {
                            callback.onFromCache(cachedResponse);
                        }
                    } else {
                        if (callback != null) {
                            callback.onError(error);
                        }
                    }
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                isLoading = false;
                String error = "Network error: " + t.getMessage();
                Log.e(TAG, error, t);
                
                // Try to return cached data as fallback
                JsonApiResponse cachedResponse = cacheManager.getCachedApiResponse();
                if (cachedResponse != null) {
                    Log.d(TAG, "Returning stale cached data as fallback");
                    if (callback != null) {
                        callback.onFromCache(cachedResponse);
                    }
                } else {
                    if (callback != null) {
                        callback.onError(error);
                    }
                }
            }
        });
    }
    
    /**
     * Refresh data in background
     */
    private void refreshDataInBackground(Runnable onComplete) {
        executorService.execute(() -> {
            apiClient.getJsonApiData(new Callback<JsonApiResponse>() {
                @Override
                public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        cacheManager.storeApiResponse(response.body());
                        Log.d(TAG, "Background refresh completed");
                    }
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
                
                @Override
                public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                    Log.e(TAG, "Background refresh failed", t);
                    if (onComplete != null) {
                        onComplete.run();
                    }
                }
            });
        });
    }
    
    /**
     * Get all movies with enhanced caching
     */
    public void getAllMovies(DataCallback<List<Poster>> callback) {
        if (callback != null) {
            callback.onLoading();
        }
        
        executorService.execute(() -> {
            try {
                List<Poster> movies = cacheManager.getAllMovies();
                
                if (movies != null && !movies.isEmpty()) {
                    Log.d(TAG, "Movies loaded from enhanced cache: " + movies.size());
                    if (callback != null) {
                        callback.onSuccess(movies);
                    }
                } else {
                    // Fallback to API
                    loadAllData(new ApiResponseCallback() {
                        @Override
                        public void onSuccess(JsonApiResponse response) {
                            List<Poster> movies = response.getMovies();
                            if (callback != null) {
                                callback.onSuccess(movies != null ? movies : new ArrayList<>());
                            }
                        }
                        
                        @Override
                        public void onError(String error) {
                            if (callback != null) {
                                callback.onError(error);
                            }
                        }
                        
                        @Override
                        public void onFromCache(JsonApiResponse response) {
                            List<Poster> movies = response.getMovies();
                            if (callback != null) {
                                callback.onSuccess(movies != null ? movies : new ArrayList<>());
                            }
                        }
                    });
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting movies", e);
                if (callback != null) {
                    callback.onError("Error loading movies: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Get paginated movies
     */
    public void getMoviesPaginated(int page, int pageSize, DataCallback<List<Poster>> callback) {
        if (callback != null) {
            callback.onLoading();
        }
        
        executorService.execute(() -> {
            try {
                List<Poster> movies = cacheManager.getMoviesPaginated(page, pageSize);
                
                if (callback != null) {
                    callback.onSuccess(movies != null ? movies : new ArrayList<>());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting paginated movies", e);
                if (callback != null) {
                    callback.onError("Error loading paginated movies: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Search movies
     */
    public void searchMovies(String query, DataCallback<List<Poster>> callback) {
        if (callback != null) {
            callback.onLoading();
        }
        
        executorService.execute(() -> {
            try {
                List<Poster> results = cacheManager.searchMovies(query);
                
                if (callback != null) {
                    callback.onSuccess(results != null ? results : new ArrayList<>());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error searching movies", e);
                if (callback != null) {
                    callback.onError("Error searching movies: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Get movie by ID
     */
    public void getMovieById(int movieId, DataCallback<Poster> callback) {
        if (callback != null) {
            callback.onLoading();
        }
        
        executorService.execute(() -> {
            try {
                Poster movie = cacheManager.getMovieById(movieId);
                
                if (callback != null) {
                    if (movie != null) {
                        callback.onSuccess(movie);
                    } else {
                        callback.onError("Movie not found");
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting movie by ID", e);
                if (callback != null) {
                    callback.onError("Error loading movie: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Get all TV series
     */
    public void getAllTvSeries(DataCallback<List<Poster>> callback) {
        if (callback != null) {
            callback.onLoading();
        }
        
        executorService.execute(() -> {
            try {
                List<Poster> tvSeries = cacheManager.getAllTvSeries();
                
                if (callback != null) {
                    callback.onSuccess(tvSeries != null ? tvSeries : new ArrayList<>());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting TV series", e);
                if (callback != null) {
                    callback.onError("Error loading TV series: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Get paginated TV series
     */
    public void getTvSeriesPaginated(int page, int pageSize, DataCallback<List<Poster>> callback) {
        if (callback != null) {
            callback.onLoading();
        }
        
        executorService.execute(() -> {
            try {
                List<Poster> tvSeries = cacheManager.getTvSeriesPaginated(page, pageSize);
                
                if (callback != null) {
                    callback.onSuccess(tvSeries != null ? tvSeries : new ArrayList<>());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting paginated TV series", e);
                if (callback != null) {
                    callback.onError("Error loading paginated TV series: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Get all channels
     */
    public void getAllChannels(DataCallback<List<Channel>> callback) {
        if (callback != null) {
            callback.onLoading();
        }
        
        executorService.execute(() -> {
            try {
                List<Channel> channels = cacheManager.getAllChannels();
                
                if (callback != null) {
                    callback.onSuccess(channels != null ? channels : new ArrayList<>());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting channels", e);
                if (callback != null) {
                    callback.onError("Error loading channels: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Get all actors
     */
    public void getAllActors(DataCallback<List<Actor>> callback) {
        if (callback != null) {
            callback.onLoading();
        }
        
        executorService.execute(() -> {
            try {
                List<Actor> actors = cacheManager.getAllActors();
                
                if (callback != null) {
                    callback.onSuccess(actors != null ? actors : new ArrayList<>());
                }
            } catch (Exception e) {
                Log.e(TAG, "Error getting actors", e);
                if (callback != null) {
                    callback.onError("Error loading actors: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Preload essential data
     */
    public void preloadEssentialData() {
        executorService.execute(() -> {
            try {
                // Preload first page of movies and TV series
                cacheManager.getMoviesPaginated(0, 20);
                cacheManager.getTvSeriesPaginated(0, 20);
                
                Log.d(TAG, "Essential data preloaded");
            } catch (Exception e) {
                Log.e(TAG, "Error preloading essential data", e);
            }
        });
    }
    
    /**
     * Get cache statistics
     */
    public EnhancedCacheManager.CacheStats getCacheStats() {
        return cacheManager.getStats();
    }
    
    /**
     * Check if cache is valid
     */
    public boolean isCacheValid() {
        return cacheManager.isCacheValid();
    }
    
    /**
     * Clear all caches
     */
    public void clearCache() {
        executorService.execute(() -> {
            try {
                cacheManager.clearAllCaches();
                Log.d(TAG, "All caches cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing caches", e);
            }
        });
    }
    
    /**
     * Check if should refresh in background
     */
    private boolean shouldRefreshInBackground() {
        // Refresh if cache is older than 1 hour
        return true; // Always refresh in background for now
    }
    
    /**
     * Shutdown repository
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        if (cacheManager != null) {
            cacheManager.shutdown();
        }
    }
}