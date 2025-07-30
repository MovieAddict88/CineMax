package my.cinemax.app.free.utils;

import android.util.Log;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.entity.ActorActressResponse;
import my.cinemax.app.free.entity.ContentResponse;
import my.cinemax.app.free.entity.ThrillerResponse;
import my.cinemax.app.free.entity.Actor;
import my.cinemax.app.free.entity.Poster;
import java.util.List;

/**
 * Example utility class demonstrating how to use the separated JSON APIs
 * This shows how to load data from thriller.json, actor_actress.json, and actual_content.json
 */
public class SeparatedJsonApiExample {
    
    private static final String TAG = "SeparatedJsonApiExample";
    
    /**
     * Load thriller movies from thriller.json
     */
    public static void loadThrillerMovies() {
        apiClient.getThrillerData(new apiClient.ThrillerCallback() {
            @Override
            public void onSuccess(ThrillerResponse thrillerResponse) {
                Log.d(TAG, "Thriller data loaded successfully");
                
                if (thrillerResponse.getGenreInfo() != null) {
                    Log.d(TAG, "Genre: " + thrillerResponse.getGenreInfo().getTitle());
                    Log.d(TAG, "Description: " + thrillerResponse.getGenreInfo().getDescription());
                    Log.d(TAG, "Total movies: " + thrillerResponse.getGenreInfo().getTotalMovies());
                }
                
                if (thrillerResponse.getThrillerMovies() != null) {
                    Log.d(TAG, "Found " + thrillerResponse.getThrillerMovies().size() + " thriller movies");
                    
                    // Process each thriller movie
                    for (int i = 0; i < Math.min(thrillerResponse.getThrillerMovies().size(), 5); i++) {
                        Poster movie = thrillerResponse.getThrillerMovies().get(i);
                        Log.d(TAG, "Movie " + (i + 1) + ": " + movie.getTitle() + " (" + movie.getYear() + ")");
                    }
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load thriller data: " + error);
            }
        });
    }
    
    /**
     * Load actor and actress data from actor_actress.json
     */
    public static void loadActorActressData() {
        apiClient.getActorActressData(new apiClient.ActorActressCallback() {
            @Override
            public void onSuccess(ActorActressResponse actorActressResponse) {
                Log.d(TAG, "Actor/Actress data loaded successfully");
                
                Log.d(TAG, "Total actors: " + actorActressResponse.getTotalActors());
                Log.d(TAG, "Total actresses: " + actorActressResponse.getTotalActresses());
                Log.d(TAG, "Total cast: " + actorActressResponse.getTotalCast());
                
                // Process actors
                if (actorActressResponse.getActors() != null) {
                    Log.d(TAG, "Found " + actorActressResponse.getActors().size() + " actors");
                    
                    for (int i = 0; i < Math.min(actorActressResponse.getActors().size(), 3); i++) {
                        Actor actor = actorActressResponse.getActors().get(i);
                        Log.d(TAG, "Actor " + (i + 1) + ": " + actor.getName() + " (" + actor.getType() + ")");
                    }
                }
                
                // Process actresses
                if (actorActressResponse.getActresses() != null) {
                    Log.d(TAG, "Found " + actorActressResponse.getActresses().size() + " actresses");
                    
                    for (int i = 0; i < Math.min(actorActressResponse.getActresses().size(), 3); i++) {
                        Actor actress = actorActressResponse.getActresses().get(i);
                        Log.d(TAG, "Actress " + (i + 1) + ": " + actress.getName() + " (" + actress.getType() + ")");
                    }
                }
                
                // Get all cast members
                List<Actor> allCast = actorActressResponse.getAllCast();
                Log.d(TAG, "All cast members: " + allCast.size());
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load actor/actress data: " + error);
            }
        });
    }
    
    /**
     * Load main content data from actual_content.json
     */
    public static void loadContentData() {
        apiClient.getContentData(new apiClient.ContentCallback() {
            @Override
            public void onSuccess(ContentResponse contentResponse) {
                Log.d(TAG, "Content data loaded successfully");
                
                // API Info
                if (contentResponse.getApiInfo() != null) {
                    Log.d(TAG, "API Version: " + contentResponse.getApiInfo().getVersion());
                    Log.d(TAG, "Description: " + contentResponse.getApiInfo().getDescription());
                    Log.d(TAG, "Last Updated: " + contentResponse.getApiInfo().getLastUpdated());
                    Log.d(TAG, "Total Movies: " + contentResponse.getApiInfo().getTotalMovies());
                    Log.d(TAG, "Total Channels: " + contentResponse.getApiInfo().getTotalChannels());
                }
                
                // Home data
                if (contentResponse.getHome() != null) {
                    if (contentResponse.getHome().getSlides() != null) {
                        Log.d(TAG, "Home slides: " + contentResponse.getHome().getSlides().size());
                    }
                    
                    if (contentResponse.getHome().getFeaturedMovies() != null) {
                        Log.d(TAG, "Featured movies: " + contentResponse.getHome().getFeaturedMovies().size());
                    }
                    
                    if (contentResponse.getHome().getChannels() != null) {
                        Log.d(TAG, "Home channels: " + contentResponse.getHome().getChannels().size());
                    }
                    
                    if (contentResponse.getHome().getGenres() != null) {
                        Log.d(TAG, "Home genres: " + contentResponse.getHome().getGenres().size());
                    }
                }
                
                // Main content
                if (contentResponse.getMovies() != null) {
                    Log.d(TAG, "Total movies: " + contentResponse.getMovies().size());
                }
                
                if (contentResponse.getChannels() != null) {
                    Log.d(TAG, "Total channels: " + contentResponse.getChannels().size());
                }
                
                if (contentResponse.getGenres() != null) {
                    Log.d(TAG, "Total genres: " + contentResponse.getGenres().size());
                }
                
                if (contentResponse.getCategories() != null) {
                    Log.d(TAG, "Total categories: " + contentResponse.getCategories().size());
                }
                
                if (contentResponse.getCountries() != null) {
                    Log.d(TAG, "Total countries: " + contentResponse.getCountries().size());
                }
                
                if (contentResponse.getSubscriptionPlans() != null) {
                    Log.d(TAG, "Subscription plans: " + contentResponse.getSubscriptionPlans().size());
                }
                
                // Video sources
                if (contentResponse.getVideoSources() != null) {
                    Log.d(TAG, "Video sources available");
                }
                
                // Ads config
                if (contentResponse.getAdsConfig() != null) {
                    Log.d(TAG, "Ads configuration loaded");
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load content data: " + error);
            }
        });
    }
    
    /**
     * Load all separated JSON data in parallel
     */
    public static void loadAllSeparatedData() {
        Log.d(TAG, "Loading all separated JSON data...");
        
        // Load thriller data
        loadThrillerMovies();
        
        // Load actor/actress data
        loadActorActressData();
        
        // Load main content data
        loadContentData();
    }
    
    /**
     * Compare performance between single JSON file and separated JSON files
     */
    public static void comparePerformance() {
        long startTime = System.currentTimeMillis();
        
        // Load from single JSON file (legacy)
        apiClient.getJsonApiData(new apiClient.JsonApiCallback() {
            @Override
            public void onSuccess(my.cinemax.app.free.entity.JsonApiResponse jsonResponse) {
                long singleFileTime = System.currentTimeMillis() - startTime;
                Log.d(TAG, "Single JSON file loaded in: " + singleFileTime + "ms");
                
                // Now load separated files
                loadAllSeparatedData();
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Failed to load single JSON file: " + error);
            }
        });
    }
}