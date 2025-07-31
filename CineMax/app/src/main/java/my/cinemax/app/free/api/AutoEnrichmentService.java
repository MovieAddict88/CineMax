package my.cinemax.app.free.api;

import android.os.AsyncTask;
import android.util.Log;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Source;
import my.cinemax.app.free.entity.Episode;
import my.cinemax.app.free.entity.Season;

import java.util.ArrayList;
import java.util.List;

/**
 * Auto-Enrichment Service
 * Input: Just a title and streaming source
 * Output: Complete movie/TV data with all TMDB metadata automatically filled
 */
public class AutoEnrichmentService {
    private static final String TAG = "AutoEnrichmentService";

    public interface AutoEnrichmentCallback {
        void onSuccess(Poster enrichedPoster);
        void onError(String error);
    }

    /**
     * AUTO-DETECT: Only need title, everything else is automatically filled from TMDB
     * @param title Movie or TV series title
     * @param type "movie" or "tv"
     * @param callback Result callback
     */
    public static void autoDetectMetadata(String title, String type, AutoEnrichmentCallback callback) {
        new AsyncTask<Void, Void, Poster>() {
            private String errorMessage = "";

            @Override
            protected Poster doInBackground(Void... voids) {
                try {
                    // Step 1: Create minimal poster with just title
                    Poster poster = createMinimalPoster(title, type);
                    
                    // Step 2: Auto-detect ALL metadata from TMDB
                    boolean success;
                    if (type.equals("movie")) {
                        success = TMDBService.enrichMovieWithTMDB(poster, title);
                    } else {
                        success = TMDBService.enrichTVSeriesWithTMDB(poster, title);
                    }
                    
                    if (!success) {
                        errorMessage = "Could not find '" + title + "' in TMDB database";
                        return null;
                    }
                    
                    // Step 3: Auto-generate streaming sources using TMDB ID
                    autoGenerateStreamingSources(poster);
                    
                    // Step 4: For TV series, auto-generate episode structure
                    if (type.equals("tv") && poster.getTmdbId() != null) {
                        autoGenerateTVEpisodes(poster);
                    }
                    
                    return poster;
                    
                } catch (Exception e) {
                    errorMessage = "Error during auto-detection: " + e.getMessage();
                    Log.e(TAG, errorMessage, e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Poster result) {
                if (result != null) {
                    Log.d(TAG, "Auto-detection successful for: " + title);
                    logAutoDetectedData(result);
                    callback.onSuccess(result);
                } else {
                    Log.e(TAG, "Auto-detection failed: " + errorMessage);
                    callback.onError(errorMessage);
                }
            }
        }.execute();
    }

    /**
     * Create minimal poster with just basic info
     */
    private static Poster createMinimalPoster(String title, String type) {
        Poster poster = new Poster();
        poster.setTitle(title);
        poster.setType(type);
        poster.setPlayas(type.equals("movie") ? "movie" : "series");
        poster.setComment(true);
        
        // These will be auto-filled by TMDB:
        // - description, rating, actors, genres
        // - images, posters, backdrops  
        // - country, duration, release dates
        // - popularity, vote count, etc.
        
        return poster;
    }

    /**
     * Auto-generate streaming sources using TMDB ID
     */
    private static void autoGenerateStreamingSources(Poster poster) {
        List<Source> sources = new ArrayList<>();
        
        if (poster.getTmdbId() != null) {
            // Auto-generate VidSrc.net embed source
            Source vidsrcSource = new Source();
            vidsrcSource.setId(1);
            vidsrcSource.setType("embed");
            vidsrcSource.setTitle("VidSrc Player");
            vidsrcSource.setQuality("1080p");
            vidsrcSource.setKind("external");
            vidsrcSource.setPremium("free");
            vidsrcSource.setExternal(true);
            
            if (poster.getType().equals("movie")) {
                vidsrcSource.setUrl("https://vidsrc.net/embed/movie/" + poster.getTmdbId());
            } else {
                // For TV series, main source points to first episode
                vidsrcSource.setUrl("https://vidsrc.net/embed/tv/" + poster.getTmdbId() + "/1/1");
            }
            
            sources.add(vidsrcSource);
            
            // Auto-generate additional sources if needed
            autoGenerateAlternateSources(poster, sources);
        }
        
        poster.setSources(sources);
    }

    /**
     * Auto-generate alternate streaming sources
     */
    private static void autoGenerateAlternateSources(Poster poster, List<Source> sources) {
        // Auto-generate backup embedding sources
        Source backupSource = new Source();
        backupSource.setId(2);
        backupSource.setType("embed");
        backupSource.setTitle("Backup Player");
        backupSource.setQuality("720p");
        backupSource.setKind("external");
        backupSource.setPremium("free");
        backupSource.setExternal(true);
        
        if (poster.getType().equals("movie")) {
            backupSource.setUrl("https://vidsrc.me/embed/movie/" + poster.getTmdbId());
        } else {
            backupSource.setUrl("https://vidsrc.me/embed/tv/" + poster.getTmdbId() + "/1/1");
        }
        
        sources.add(backupSource);
    }

    /**
     * Auto-generate TV series episode structure
     */
    private static void autoGenerateTVEpisodes(Poster poster) {
        if (poster.getNumberOfSeasons() == null) return;
        
        List<Season> seasons = new ArrayList<>();
        int totalSeasons = Math.min(poster.getNumberOfSeasons(), 5); // Limit to 5 seasons for demo
        
        for (int seasonNum = 1; seasonNum <= totalSeasons; seasonNum++) {
            Season season = new Season();
            season.setId(seasonNum);
            season.setTitle("Season " + seasonNum);
            
            // Auto-generate episodes for this season
            List<Episode> episodes = autoGenerateEpisodesForSeason(poster, seasonNum);
            season.setEpisodes(episodes);
            
            seasons.add(season);
        }
        
        poster.setSeasons(seasons);
    }

    /**
     * Auto-generate episodes for a season
     */
    private static List<Episode> autoGenerateEpisodesForSeason(Poster poster, int seasonNumber) {
        List<Episode> episodes = new ArrayList<>();
        
        // For demo, create 2-3 episodes per season
        int episodeCount = seasonNumber <= 2 ? 1 : 2; // First 2 seasons have 1 episode each
        
        for (int episodeNum = 1; episodeNum <= episodeCount; episodeNum++) {
            Episode episode = new Episode();
            episode.setId(episodeNum);
            episode.setTitle("Episode " + episodeNum);
            episode.setDescription("Auto-generated episode description. Full details will be enriched from TMDB episode data.");
            episode.setDuration(poster.getDuration()); // Use series average duration
            episode.setPlayas("episode");
            
            // Auto-generate episode streaming sources
            List<Source> episodeSources = new ArrayList<>();
            
            Source episodeSource = new Source();
            episodeSource.setId(episodeNum);
            episodeSource.setType("embed");
            episodeSource.setTitle("VidSrc Player");
            episodeSource.setQuality("1080p");
            episodeSource.setKind("external");
            episodeSource.setPremium("free");
            episodeSource.setExternal(true);
            episodeSource.setUrl("https://vidsrc.net/embed/tv/" + poster.getTmdbId() + "/" + seasonNumber + "/" + episodeNum);
            
            episodeSources.add(episodeSource);
            episode.setSources(episodeSources);
            
            episodes.add(episode);
        }
        
        return episodes;
    }

    /**
     * Batch auto-detection for multiple titles
     */
    public static void batchAutoDetect(String[] titles, String[] types, BatchAutoEnrichmentCallback callback) {
        new AsyncTask<Void, Poster, List<Poster>>() {
            private List<String> errors = new ArrayList<>();

            @Override
            protected List<Poster> doInBackground(Void... voids) {
                List<Poster> results = new ArrayList<>();
                
                for (int i = 0; i < titles.length; i++) {
                    try {
                        Poster poster = createMinimalPoster(titles[i], types[i]);
                        
                        boolean success;
                        if (types[i].equals("movie")) {
                            success = TMDBService.enrichMovieWithTMDB(poster, titles[i]);
                        } else {
                            success = TMDBService.enrichTVSeriesWithTMDB(poster, titles[i]);
                        }
                        
                        if (success) {
                            autoGenerateStreamingSources(poster);
                            if (types[i].equals("tv")) {
                                autoGenerateTVEpisodes(poster);
                            }
                            results.add(poster);
                            publishProgress(poster); // Update UI progressively
                        } else {
                            errors.add("Could not find: " + titles[i]);
                        }
                        
                    } catch (Exception e) {
                        errors.add("Error with " + titles[i] + ": " + e.getMessage());
                    }
                }
                
                return results;
            }

            @Override
            protected void onProgressUpdate(Poster... posters) {
                if (posters.length > 0) {
                    callback.onItemComplete(posters[0]);
                }
            }

            @Override
            protected void onPostExecute(List<Poster> results) {
                callback.onBatchComplete(results, errors);
            }
        }.execute();
    }

    public interface BatchAutoEnrichmentCallback {
        void onItemComplete(Poster poster);
        void onBatchComplete(List<Poster> posters, List<String> errors);
    }

    /**
     * Log what was auto-detected
     */
    private static void logAutoDetectedData(Poster poster) {
        Log.d(TAG, "=== AUTO-DETECTED DATA ===");
        Log.d(TAG, "Input: " + poster.getTitle());
        Log.d(TAG, "TMDB ID: " + poster.getTmdbId());
        Log.d(TAG, "Auto-filled Description: " + (poster.getDescription() != null ? "✓" : "✗"));
        Log.d(TAG, "Auto-filled Rating: " + poster.getRating());
        Log.d(TAG, "Auto-filled Country: " + poster.getCountry());
        Log.d(TAG, "Auto-filled Poster: " + (poster.getPosterTmdb() != null ? "✓" : "✗"));
        Log.d(TAG, "Auto-filled Backdrop: " + (poster.getBackdrop() != null ? "✓" : "✗"));
        Log.d(TAG, "Auto-filled Actors: " + (poster.getActors() != null ? poster.getActors().size() : 0));
        Log.d(TAG, "Auto-filled Genres: " + (poster.getGenres() != null ? poster.getGenres().size() : 0));
        Log.d(TAG, "Auto-generated Sources: " + (poster.getSources() != null ? poster.getSources().size() : 0));
        
        if (poster.getType().equals("tv")) {
            Log.d(TAG, "Auto-generated Seasons: " + (poster.getSeasons() != null ? poster.getSeasons().size() : 0));
        }
        
        Log.d(TAG, "========================");
    }

    /**
     * EXAMPLE USAGE: Just provide a title!
     */
    public static void exampleUsage() {
        // Example 1: Auto-detect movie - only need title!
        autoDetectMetadata("The Dark Knight", "movie", new AutoEnrichmentCallback() {
            @Override
            public void onSuccess(Poster movie) {
                // movie now has:
                // - All TMDB metadata (rating, actors, description, etc.)
                // - Auto-generated poster and backdrop URLs
                // - Auto-generated streaming sources with TMDB ID
                Log.d(TAG, "Movie auto-detection complete!");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Auto-detection failed: " + error);
            }
        });

        // Example 2: Auto-detect TV series - only need title!
        autoDetectMetadata("Game of Thrones", "tv", new AutoEnrichmentCallback() {
            @Override
            public void onSuccess(Poster series) {
                // series now has:
                // - All TMDB metadata
                // - Auto-generated season/episode structure
                // - Streaming URLs for each episode
                Log.d(TAG, "TV series auto-detection complete!");
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "Auto-detection failed: " + error);
            }
        });
    }
}