package my.cinemax.app.free.demo;

import android.util.Log;
import my.cinemax.app.free.api.AutoEnrichmentService;
import my.cinemax.app.free.entity.Poster;

/**
 * Demo: How to add ANY movie/TV series with AUTO-DETECTION
 * 
 * INPUT: Just a title
 * OUTPUT: Complete metadata + streaming sources
 * NO MANUAL IMAGE LINKS NEEDED!
 */
public class AutoDetectionDemo {
    private static final String TAG = "AutoDetectionDemo";

    /**
     * DEMO 1: Add a movie with ZERO manual input
     */
    public static void addMovieDemo() {
        Log.d(TAG, "=== ADDING MOVIE WITH AUTO-DETECTION ===");
        Log.d(TAG, "Input: Just the title 'Avengers: Endgame'");
        
        // ONLY INPUT NEEDED: Title!
        AutoEnrichmentService.autoDetectMetadata("Avengers: Endgame", "movie", 
            new AutoEnrichmentService.AutoEnrichmentCallback() {
                @Override
                public void onSuccess(Poster movie) {
                    Log.d(TAG, "SUCCESS! Auto-detected movie data:");
                    Log.d(TAG, "Title: " + movie.getTitle());
                    Log.d(TAG, "TMDB ID: " + movie.getTmdbId());
                    Log.d(TAG, "Rating: " + movie.getRating());
                    Log.d(TAG, "Country: " + movie.getCountry());
                    Log.d(TAG, "Poster URL: " + movie.getPosterTmdb());
                    Log.d(TAG, "Backdrop URL: " + movie.getBackdrop());
                    Log.d(TAG, "Cast: " + (movie.getActors() != null ? movie.getActors().size() + " actors" : "0"));
                    Log.d(TAG, "Streaming URL: " + (movie.getSources() != null && movie.getSources().size() > 0 ? 
                                                   movie.getSources().get(0).getUrl() : "None"));
                    Log.d(TAG, "ALL DATA AUTO-FILLED FROM TMDB!");
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Auto-detection failed: " + error);
                }
            });
    }

    /**
     * DEMO 2: Add a TV series with ZERO manual input
     */
    public static void addTVSeriesDemo() {
        Log.d(TAG, "=== ADDING TV SERIES WITH AUTO-DETECTION ===");
        Log.d(TAG, "Input: Just the title 'Game of Thrones'");
        
        // ONLY INPUT NEEDED: Title!
        AutoEnrichmentService.autoDetectMetadata("Game of Thrones", "tv", 
            new AutoEnrichmentService.AutoEnrichmentCallback() {
                @Override
                public void onSuccess(Poster series) {
                    Log.d(TAG, "SUCCESS! Auto-detected TV series data:");
                    Log.d(TAG, "Title: " + series.getTitle());
                    Log.d(TAG, "TMDB ID: " + series.getTmdbId());
                    Log.d(TAG, "Rating: " + series.getRating());
                    Log.d(TAG, "Country: " + series.getCountry());
                    Log.d(TAG, "Seasons: " + series.getNumberOfSeasons());
                    Log.d(TAG, "Episodes: " + series.getNumberOfEpisodes());
                    Log.d(TAG, "Poster URL: " + series.getPosterTmdb());
                    Log.d(TAG, "Backdrop URL: " + series.getBackdrop());
                    Log.d(TAG, "Auto-generated Seasons: " + (series.getSeasons() != null ? series.getSeasons().size() : 0));
                    
                    if (series.getSeasons() != null && series.getSeasons().size() > 0) {
                        Log.d(TAG, "Season 1 Episodes: " + 
                              (series.getSeasons().get(0).getEpisodes() != null ? 
                               series.getSeasons().get(0).getEpisodes().size() : 0));
                        
                        if (series.getSeasons().get(0).getEpisodes() != null && 
                            series.getSeasons().get(0).getEpisodes().size() > 0) {
                            Log.d(TAG, "Episode 1 Streaming URL: " + 
                                  series.getSeasons().get(0).getEpisodes().get(0).getSources().get(0).getUrl());
                        }
                    }
                    
                    Log.d(TAG, "ALL DATA AUTO-FILLED FROM TMDB!");
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Auto-detection failed: " + error);
                }
            });
    }

    /**
     * DEMO 3: Batch add multiple movies/shows
     */
    public static void batchAddDemo() {
        Log.d(TAG, "=== BATCH AUTO-DETECTION DEMO ===");
        
        String[] titles = {
            "The Matrix",
            "Inception", 
            "The Office",
            "Stranger Things",
            "Interstellar"
        };
        
        String[] types = {
            "movie",
            "movie", 
            "tv",
            "tv",
            "movie"
        };
        
        Log.d(TAG, "Input: Just 5 titles");
        Log.d(TAG, "Processing...");
        
        AutoEnrichmentService.batchAutoDetect(titles, types, 
            new AutoEnrichmentService.BatchAutoEnrichmentCallback() {
                @Override
                public void onItemComplete(Poster poster) {
                    Log.d(TAG, "✓ Auto-detected: " + poster.getTitle() + 
                              " (TMDB ID: " + poster.getTmdbId() + ")");
                }

                @Override
                public void onBatchComplete(java.util.List<Poster> posters, java.util.List<String> errors) {
                    Log.d(TAG, "BATCH COMPLETE!");
                    Log.d(TAG, "Successfully auto-detected: " + posters.size() + " items");
                    Log.d(TAG, "Errors: " + errors.size());
                    
                    for (String error : errors) {
                        Log.e(TAG, "Error: " + error);
                    }
                    
                    Log.d(TAG, "ALL METADATA AUTO-FILLED - NO MANUAL WORK!");
                }
            });
    }

    /**
     * COMPARISON: Old way vs New auto-detection way
     */
    public static void showComparison() {
        Log.d(TAG, "=== COMPARISON: Manual vs Auto-Detection ===");
        
        Log.d(TAG, "OLD WAY (Manual):");
        Log.d(TAG, "1. Input title: 'The Dark Knight'");
        Log.d(TAG, "2. Manually find description");
        Log.d(TAG, "3. Manually find rating");
        Log.d(TAG, "4. Manually find poster image URL");
        Log.d(TAG, "5. Manually find backdrop image URL");
        Log.d(TAG, "6. Manually find actor names");
        Log.d(TAG, "7. Manually find actor images");
        Log.d(TAG, "8. Manually find country");
        Log.d(TAG, "9. Manually find duration");
        Log.d(TAG, "10. Manually create streaming URL");
        Log.d(TAG, "RESULT: 30+ minutes of manual work");
        
        Log.d(TAG, "");
        Log.d(TAG, "NEW WAY (Auto-Detection):");
        Log.d(TAG, "1. Input title: 'The Dark Knight'");
        Log.d(TAG, "2. Call AutoEnrichmentService.autoDetectMetadata()");
        Log.d(TAG, "RESULT: Everything auto-filled in seconds!");
        
        Log.d(TAG, "Time saved: 99% less work!");
    }

    /**
     * REAL USAGE EXAMPLE: How you would actually use this
     */
    public static void realUsageExample() {
        Log.d(TAG, "=== REAL USAGE EXAMPLE ===");
        
        // Scenario: User wants to add "Spider-Man: No Way Home" to their app
        String movieTitle = "Spider-Man: No Way Home";
        
        Log.d(TAG, "User wants to add: " + movieTitle);
        Log.d(TAG, "Developer does:");
        
        AutoEnrichmentService.autoDetectMetadata(movieTitle, "movie", 
            new AutoEnrichmentService.AutoEnrichmentCallback() {
                @Override
                public void onSuccess(Poster movie) {
                    // Movie is now ready with:
                    // ✓ TMDB rating and popularity
                    // ✓ High-quality poster and backdrop
                    // ✓ Complete cast with actor photos
                    // ✓ Production country and companies
                    // ✓ Accurate duration and release date
                    // ✓ VidSrc.net streaming URL: https://vidsrc.net/embed/movie/{tmdb_id}
                    // ✓ Backup streaming sources
                    
                    Log.d(TAG, "DONE! Movie ready for users:");
                    Log.d(TAG, "- Rating: " + movie.getRating() + "/10");
                    Log.d(TAG, "- Streaming: " + movie.getSources().get(0).getUrl());
                    Log.d(TAG, "- NO manual image links needed!");
                    
                    // Save to database or add to JSON
                    // saveToDatabase(movie);
                }

                @Override
                public void onError(String error) {
                    Log.e(TAG, "Could not find movie in TMDB: " + error);
                    // Handle case where movie doesn't exist in TMDB
                }
            });
    }
}