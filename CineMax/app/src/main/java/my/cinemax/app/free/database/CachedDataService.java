package my.cinemax.app.free.database;

import android.content.Context;
import android.util.Log;

import java.util.List;

import my.cinemax.app.free.MyApplication;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.database.entities.CachedEpisode;
import my.cinemax.app.free.database.entities.CachedMovie;
import my.cinemax.app.free.entity.Episode;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.entity.Poster;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CachedDataService {
    private static final String TAG = "CachedDataService";
    private CacheManager cacheManager;
    private Context context;

    public CachedDataService(Context context) {
        this.context = context;
        this.cacheManager = CacheManager.getInstance(context);
    }

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(String error);
        void onCacheLoaded(T cachedData); // Called when cache is loaded first
    }

    /**
     * Get movies with caching support
     * First checks cache, then loads from API if needed
     */
    public void getMoviesWithCache(int type, DataCallback<List<Poster>> callback) {
        // First, try to load from cache
        List<CachedMovie> cachedMovies = cacheManager.getCachedMovies(type);
        if (!cachedMovies.isEmpty()) {
            List<Poster> posters = cacheManager.convertCachedMoviesToPosters(cachedMovies);
            Log.d(TAG, "Loaded " + posters.size() + " movies from cache");
            callback.onCacheLoaded(posters);
            
            // Still fetch fresh data in background to update cache
            fetchMoviesFromApi(type, callback, true);
        } else {
            // No cache available, fetch from API
            fetchMoviesFromApi(type, callback, false);
        }
    }

    /**
     * Get episodes with caching support
     */
    public void getEpisodesWithCache(String serieId, DataCallback<List<Episode>> callback) {
        // First, try to load from cache
        List<CachedEpisode> cachedEpisodes = cacheManager.getCachedEpisodes(serieId);
        if (!cachedEpisodes.isEmpty()) {
            List<Episode> episodes = cacheManager.convertCachedEpisodesToEpisodes(cachedEpisodes);
            Log.d(TAG, "Loaded " + episodes.size() + " episodes from cache");
            callback.onCacheLoaded(episodes);
            
            // Still fetch fresh data in background to update cache
            fetchEpisodesFromApi(serieId, callback, true);
        } else {
            // No cache available, fetch from API
            fetchEpisodesFromApi(serieId, callback, false);
        }
    }

    /**
     * Get home data with caching support
     */
    public void getHomeDataWithCache(DataCallback<JsonApiResponse> callback) {
        // Check if we have cached movies/series for home display
        boolean hasMovieCache = cacheManager.hasCachedMovies(1); // Movies
        boolean hasSeriesCache = cacheManager.hasCachedMovies(2); // Series
        
        if (hasMovieCache || hasSeriesCache) {
            // Create a cached response with available data
            JsonApiResponse cachedResponse = createCachedHomeResponse();
            if (cachedResponse != null) {
                Log.d(TAG, "Loaded home data from cache");
                callback.onCacheLoaded(cachedResponse);
            }
        }
        
        // Always fetch fresh data for home
        fetchHomeDataFromApi(callback);
    }

    private void fetchMoviesFromApi(int type, DataCallback<List<Poster>> callback, boolean isBackgroundUpdate) {
        // This would integrate with your existing API calls
        // For now, I'll show the pattern - you would replace this with actual API calls
        
        // Example: Using the existing GitHub JSON API pattern
        apiClient.getJsonApiData(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse apiResponse = response.body();
                    List<Poster> posters = extractPostersFromResponse(apiResponse, type);
                    
                    if (posters != null && !posters.isEmpty()) {
                        // Cache the new data
                        cacheManager.cacheMovies(posters);
                        Log.d(TAG, "Cached " + posters.size() + " movies from API");
                        
                        if (!isBackgroundUpdate) {
                            callback.onSuccess(posters);
                        }
                    }
                } else {
                    if (!isBackgroundUpdate) {
                        callback.onError("Failed to load movies from server");
                    }
                }
            }

            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                if (!isBackgroundUpdate) {
                    callback.onError("Network error: " + t.getMessage());
                }
                Log.e(TAG, "Failed to fetch movies from API", t);
            }
        });
    }

    private void fetchEpisodesFromApi(String serieId, DataCallback<List<Episode>> callback, boolean isBackgroundUpdate) {
        // Similar pattern for episodes
        // You would integrate this with your existing episode API calls
        
        Log.d(TAG, "Fetching episodes for serie " + serieId + " from API");
        
        // Placeholder - replace with actual API call for episodes
        // For example, if you have an API call like:
        // apiClient.getSerieEpisodes(serieId, new Callback<EpisodeResponse>() { ... });
        
        // For now, just call error to show the pattern
        if (!isBackgroundUpdate) {
            callback.onError("Episode API integration needed");
        }
    }

    private void fetchHomeDataFromApi(DataCallback<JsonApiResponse> callback) {
        apiClient.getJsonApiData(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse apiResponse = response.body();
                    
                    // Cache all the data from home response
                    cacheHomeData(apiResponse);
                    
                    callback.onSuccess(apiResponse);
                } else {
                    callback.onError("Failed to load home data from server");
                }
            }

            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                callback.onError("Network error: " + t.getMessage());
                Log.e(TAG, "Failed to fetch home data from API", t);
            }
        });
    }

    private void cacheHomeData(JsonApiResponse response) {
        if (response == null || response.getHome() == null) return;
        
        try {
            // Cache movies if available
            if (response.getHome().getGenres() != null) {
                for (int i = 0; i < response.getHome().getGenres().size(); i++) {
                    if (response.getHome().getGenres().get(i) != null && 
                        response.getHome().getGenres().get(i).getPosters() != null) {
                        List<Poster> posters = response.getHome().getGenres().get(i).getPosters();
                        cacheManager.cacheMovies(posters);
                    }
                }
            }
            
            Log.d(TAG, "Cached home data successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error caching home data", e);
        }
    }

    private JsonApiResponse createCachedHomeResponse() {
        try {
            // Create a response with cached data
            // This is a simplified version - you would populate it more comprehensively
            JsonApiResponse cachedResponse = new JsonApiResponse();
            
            // You would populate this with cached data based on your actual data structure
            // This is just a placeholder to show the concept
            
            return cachedResponse;
        } catch (Exception e) {
            Log.e(TAG, "Error creating cached home response", e);
            return null;
        }
    }

    private List<Poster> extractPostersFromResponse(JsonApiResponse response, int type) {
        // Extract posters of specific type from the JSON response
        // This would depend on your actual response structure
        
        if (response == null || response.getHome() == null || response.getHome().getGenres() == null) {
            return null;
        }
        
        // This is a simplified extraction - adjust based on your actual data structure
        for (int i = 0; i < response.getHome().getGenres().size(); i++) {
            if (response.getHome().getGenres().get(i) != null && 
                response.getHome().getGenres().get(i).getPosters() != null) {
                return response.getHome().getGenres().get(i).getPosters();
            }
        }
        
        return null;
    }

    /**
     * Force refresh data from API and update cache
     */
    public void forceRefresh(DataCallback<JsonApiResponse> callback) {
        Log.d(TAG, "Force refreshing data from API");
        fetchHomeDataFromApi(callback);
    }

    /**
     * Clear all cached data
     */
    public void clearCache() {
        cacheManager.clearAllCache();
        Log.d(TAG, "All cache cleared");
    }

    /**
     * Get cache statistics
     */
    public String getCacheStats() {
        return cacheManager.getCacheStats();
    }
}