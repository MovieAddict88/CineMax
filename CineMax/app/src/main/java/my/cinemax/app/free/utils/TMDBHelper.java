package my.cinemax.app.free.utils;

import android.os.AsyncTask;
import android.util.Log;
import my.cinemax.app.free.api.TMDBService;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Source;

import java.util.ArrayList;
import java.util.List;

/**
 * Helper class to demonstrate TMDB integration usage
 * This class shows how to enhance content with TMDB metadata while preserving streaming sources
 */
public class TMDBHelper {
    private static final String TAG = "TMDBHelper";

    /**
     * Interface for callback when TMDB enrichment is complete
     */
    public interface TMDBEnrichmentCallback {
        void onSuccess(Poster enrichedPoster);
        void onError(String error);
    }

    /**
     * Example: Enrich Big Buck Bunny movie with TMDB data while keeping vidsrc.net sources
     */
    public static void enrichBigBuckBunnyExample(TMDBEnrichmentCallback callback) {
        // Create a basic poster with missing metadata
        Poster poster = createBasicBigBuckBunnyPoster();
        
        // Enrich with TMDB data in background
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return TMDBService.enrichMovieWithTMDB(poster, "Big Buck Bunny");
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    // Add vidsrc.net sources after enrichment
                    addVidSrcSources(poster, false);
                    callback.onSuccess(poster);
                } else {
                    callback.onError("Failed to enrich with TMDB data");
                }
            }
        }.execute();
    }

    /**
     * Example: Enrich a TV series with TMDB data while keeping vidsrc.net sources
     */
    public static void enrichTVSeriesExample(String seriesTitle, TMDBEnrichmentCallback callback) {
        // Create a basic poster for TV series
        Poster poster = createBasicTVSeriesPoster(seriesTitle);
        
        // Enrich with TMDB data in background
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return TMDBService.enrichTVSeriesWithTMDB(poster, seriesTitle);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    // Add vidsrc.net sources for TV series
                    addVidSrcSources(poster, true);
                    callback.onSuccess(poster);
                } else {
                    callback.onError("Failed to enrich TV series with TMDB data");
                }
            }
        }.execute();
    }

    /**
     * Creates a basic Big Buck Bunny poster with minimal data (as if missing metadata)
     */
    private static Poster createBasicBigBuckBunnyPoster() {
        Poster poster = new Poster();
        poster.setId(1);
        poster.setTitle("Big Buck Bunny");
        poster.setType("movie");
        poster.setLabel("FREE");
        poster.setSublabel("HD");
        poster.setPlayas("movie");
        poster.setComment(true);
        
        // Basic description - TMDB will provide richer description
        poster.setDescription("A short animated film");
        poster.setYear("2008");
        poster.setClassification("G");
        
        // Basic image - TMDB will provide better posters and backdrops
        poster.setImage("https://peach.blender.org/wp-content/uploads/title_anouncement.jpg");
        
        return poster;
    }

    /**
     * Creates a basic TV series poster with minimal data
     */
    private static Poster createBasicTVSeriesPoster(String title) {
        Poster poster = new Poster();
        poster.setId(2);
        poster.setTitle(title);
        poster.setType("tv");
        poster.setLabel("HD");
        poster.setSublabel("SERIES");
        poster.setPlayas("series");
        poster.setComment(true);
        
        // Minimal data - TMDB will enrich this
        poster.setDescription("A popular TV series");
        
        return poster;
    }

    /**
     * Add vidsrc.net streaming sources to the poster
     * @param poster The poster to add sources to
     * @param isTVSeries Whether this is a TV series or movie
     */
    private static void addVidSrcSources(Poster poster, boolean isTVSeries) {
        List<Source> sources = new ArrayList<>();
        
        if (isTVSeries) {
            // For TV series, we'll add sources to episodes in seasons
            // This is handled differently - sources are added to individual episodes
            Log.d(TAG, "TV series sources should be added to individual episodes");
        } else {
            // For movies, add vidsrc.net embed source
            Source vidsrcSource = new Source();
            vidsrcSource.setId(1);
            vidsrcSource.setType("embed");
            vidsrcSource.setTitle("VidSrc Player");
            vidsrcSource.setQuality("1080p");
            vidsrcSource.setKind("external");
            vidsrcSource.setPremium("free");
            vidsrcSource.setExternal(true);
            
            // For Big Buck Bunny, we use a generic embed URL
            // In real implementation, you'd use the actual TMDB ID
            vidsrcSource.setUrl("https://vidsrc.net/embed/movie/bigbuckbunny");
            
            sources.add(vidsrcSource);
            
            // Add direct source as backup
            Source directSource = new Source();
            directSource.setId(2);
            directSource.setType("direct");
            directSource.setTitle("Direct Stream HD");
            directSource.setQuality("1080p");
            directSource.setSize("698 MB");
            directSource.setKind("direct");
            directSource.setPremium("free");
            directSource.setExternal(false);
            directSource.setUrl("https://download.blender.org/peach/bigbuckbunny_movies/big_buck_bunny_1080p_h264.mov");
            
            sources.add(directSource);
        }
        
        poster.setSources(sources);
    }

    /**
     * Example usage: Enrich any movie with TMDB data
     */
    public static void enrichAnyMovie(String movieTitle, TMDBEnrichmentCallback callback) {
        Poster poster = new Poster();
        poster.setTitle(movieTitle);
        poster.setType("movie");
        poster.setPlayas("movie");
        
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                return TMDBService.enrichMovieWithTMDB(poster, movieTitle);
            }

            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    // Add generic vidsrc.net source based on TMDB ID
                    if (poster.getTmdbId() != null) {
                        addVidSrcSourcesWithTMDBId(poster);
                    }
                    callback.onSuccess(poster);
                } else {
                    callback.onError("Failed to find movie in TMDB: " + movieTitle);
                }
            }
        }.execute();
    }

    /**
     * Add vidsrc.net sources using TMDB ID
     */
    private static void addVidSrcSourcesWithTMDBId(Poster poster) {
        List<Source> sources = new ArrayList<>();
        
        Source vidsrcSource = new Source();
        vidsrcSource.setId(1);
        vidsrcSource.setType("embed");
        vidsrcSource.setTitle("VidSrc Player");
        vidsrcSource.setQuality("1080p");
        vidsrcSource.setKind("external");
        vidsrcSource.setPremium("free");
        vidsrcSource.setExternal(true);
        
        // Use TMDB ID to create vidsrc.net URL
        if (poster.getType().equals("movie")) {
            vidsrcSource.setUrl("https://vidsrc.net/embed/movie/" + poster.getTmdbId());
        } else {
            vidsrcSource.setUrl("https://vidsrc.net/embed/tv/" + poster.getTmdbId());
        }
        
        sources.add(vidsrcSource);
        poster.setSources(sources);
    }

    /**
     * Utility method to log enriched poster information
     */
    public static void logEnrichedPosterInfo(Poster poster) {
        Log.d(TAG, "=== Enriched Poster Information ===");
        Log.d(TAG, "Title: " + poster.getTitle());
        Log.d(TAG, "Original Title: " + poster.getOriginalTitle());
        Log.d(TAG, "TMDB ID: " + poster.getTmdbId());
        Log.d(TAG, "Rating: " + poster.getRating());
        Log.d(TAG, "Country: " + poster.getCountry());
        Log.d(TAG, "Release Date: " + poster.getReleaseDate());
        Log.d(TAG, "Popularity: " + poster.getPopularity());
        Log.d(TAG, "Vote Count: " + poster.getVoteCount());
        Log.d(TAG, "Poster URL: " + poster.getPosterTmdb());
        Log.d(TAG, "Backdrop URL: " + poster.getBackdrop());
        Log.d(TAG, "Genres: " + (poster.getGenres() != null ? poster.getGenres().size() : 0));
        Log.d(TAG, "Actors: " + (poster.getActors() != null ? poster.getActors().size() : 0));
        Log.d(TAG, "Sources: " + (poster.getSources() != null ? poster.getSources().size() : 0));
        
        if (poster.getType().equals("tv")) {
            Log.d(TAG, "Number of Seasons: " + poster.getNumberOfSeasons());
            Log.d(TAG, "Number of Episodes: " + poster.getNumberOfEpisodes());
            Log.d(TAG, "Status: " + poster.getStatus());
            Log.d(TAG, "Networks: " + poster.getNetworks());
        }
        
        Log.d(TAG, "===================================");
    }
}