package my.cinemax.app.free.api;

import android.app.Activity;
import android.util.Log;

import my.cinemax.app.free.entity.JsonApiResponse;

/**
 * Simple helper class to integrate TMDB into existing fragments
 * Usage: Add this to your existing HomeFragment, MoviesFragment, etc.
 */
public class TMDBIntegrationHelper {
    private static final String TAG = "TMDBIntegration";
    private TMDBManager tmdbManager;
    
    public TMDBIntegrationHelper() {
        this.tmdbManager = new TMDBManager();
    }
    
    /**
     * Enhance API response with TMDB data
     * Call this in your existing fragment's onResponse() method
     */
    public void enhanceApiResponse(JsonApiResponse apiResponse, Activity activity, EnhancementCallback callback) {
        if (apiResponse == null) {
            callback.onComplete(apiResponse);
            return;
        }
        
        Log.d(TAG, "Starting TMDB enhancement...");
        
        tmdbManager.enhanceApiResponseWithTMDB(apiResponse, new TMDBManager.TMDBEnhancementCallback() {
            @Override
            public void onComplete(JsonApiResponse enhancedResponse) {
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        Log.d(TAG, "TMDB enhancement completed successfully!");
                        callback.onComplete(enhancedResponse);
                    });
                }
            }
        });
    }
    
    /**
     * Create content from TMDB when API fails
     * Call this in your existing fragment's onFailure() method
     */
    public void createContentFromTMDB(Activity activity, EnhancementCallback callback) {
        Log.d(TAG, "Creating content from TMDB...");
        
        JsonApiResponse emptyResponse = new JsonApiResponse();
        tmdbManager.autoEnhancePopularContent(emptyResponse, new TMDBManager.TMDBEnhancementCallback() {
            @Override
            public void onComplete(JsonApiResponse enhancedResponse) {
                if (activity != null) {
                    activity.runOnUiThread(() -> {
                        Log.d(TAG, "TMDB content creation completed!");
                        callback.onComplete(enhancedResponse);
                    });
                }
            }
        });
    }
    
    /**
     * Callback interface for enhancement operations
     */
    public interface EnhancementCallback {
        void onComplete(JsonApiResponse enhancedResponse);
    }
}