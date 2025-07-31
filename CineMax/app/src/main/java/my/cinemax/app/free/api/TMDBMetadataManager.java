package my.cinemax.app.free.api;

import android.util.Log;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Actor;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.JsonApiResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * TMDB Metadata Manager for auto-enhancing movie and series data
 * This class integrates with the existing JSON API to automatically
 * fill missing metadata using TMDB API
 */
public class TMDBMetadataManager {
    
    private static final String TAG = "TMDBMetadataManager";
    private static TMDBMetadataManager instance;
    private TMDBService tmdbService;
    
    private TMDBMetadataManager() {
        tmdbService = TMDBService.getInstance();
    }
    
    public static TMDBMetadataManager getInstance() {
        if (instance == null) {
            instance = new TMDBMetadataManager();
        }
        return instance;
    }
    
    /**
     * Enhance movie data with TMDB metadata if missing
     */
    public void enhanceMovieData(Poster movie, TMDBEnhancementCallback callback) {
        // Check if movie needs enhancement (missing description, actors, etc.)
        if (needsEnhancement(movie)) {
            Log.d(TAG, "Enhancing movie: " + movie.getTitle());
            
            tmdbService.searchMovie(movie.getTitle(), new TMDBService.TMDBMovieCallback() {
                @Override
                public void onSuccess(TMDBService.TMDBMovieData tmdbData) {
                    enhanceMovieWithTMDBData(movie, tmdbData);
                    callback.onSuccess(movie);
                }
                
                @Override
                public void onError(String error) {
                    Log.w(TAG, "Failed to enhance movie " + movie.getTitle() + ": " + error);
                    callback.onError(error);
                }
            });
        } else {
            // Movie already has complete data
            callback.onSuccess(movie);
        }
    }
    
    /**
     * Enhance TV series data with TMDB metadata if missing
     */
    public void enhanceTVSeriesData(Poster series, TMDBEnhancementCallback callback) {
        // Check if series needs enhancement
        if (needsEnhancement(series)) {
            Log.d(TAG, "Enhancing TV series: " + series.getTitle());
            
            tmdbService.searchTVSeries(series.getTitle(), new TMDBService.TMDBTVCallback() {
                @Override
                public void onSuccess(TMDBService.TMDBTVData tmdbData) {
                    enhanceSeriesWithTMDBData(series, tmdbData);
                    callback.onSuccess(series);
                }
                
                @Override
                public void onError(String error) {
                    Log.w(TAG, "Failed to enhance series " + series.getTitle() + ": " + error);
                    callback.onError(error);
                }
            });
        } else {
            // Series already has complete data
            callback.onSuccess(series);
        }
    }
    
    /**
     * Enhance all movies in the JSON response
     */
    public void enhanceAllMovies(JsonApiResponse jsonResponse, TMDBEnhancementCallback callback) {
        if (jsonResponse.getMovies() != null) {
            List<Poster> movies = jsonResponse.getMovies();
            final int[] enhancedCount = {0};
            final int totalMovies = movies.size();
            
            for (Poster movie : movies) {
                enhanceMovieData(movie, new TMDBEnhancementCallback() {
                    @Override
                    public void onSuccess(Poster enhancedItem) {
                        enhancedCount[0]++;
                        if (enhancedCount[0] == totalMovies) {
                            callback.onSuccess(null); // All movies enhanced
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        enhancedCount[0]++;
                        if (enhancedCount[0] == totalMovies) {
                            callback.onSuccess(null); // Continue even if some fail
                        }
                    }
                });
            }
        } else {
            callback.onSuccess(null);
        }
    }
    
    /**
     * Check if a movie/series needs enhancement
     */
    private boolean needsEnhancement(Poster item) {
        // Check if description is missing or too short
        if (item.getDescription() == null || item.getDescription().isEmpty() || 
            item.getDescription().length() < 50) {
            return true;
        }
        
        // Check if actors list is empty or missing
        if (item.getActors() == null || item.getActors().isEmpty()) {
            return true;
        }
        
        // Check if genres are missing or incomplete
        if (item.getGenres() == null || item.getGenres().isEmpty()) {
            return true;
        }
        
        // Check if IMDB ID is missing
        if (item.getImdb() == null || item.getImdb().isEmpty() || 
            !item.getImdb().startsWith("tt")) {
            return true;
        }
        
        return false;
    }
    
    /**
     * Enhance movie with TMDB data
     */
    private void enhanceMovieWithTMDBData(Poster movie, TMDBService.TMDBMovieData tmdbData) {
        // Update description if missing or too short
        if (movie.getDescription() == null || movie.getDescription().isEmpty() || 
            movie.getDescription().length() < 50) {
            movie.setDescription(tmdbData.overview);
        }
        
        // Update IMDB ID if missing
        if (movie.getImdb() == null || movie.getImdb().isEmpty() || 
            !movie.getImdb().startsWith("tt")) {
            movie.setImdb(tmdbData.imdbId);
        }
        
        // Update rating if missing
        if (movie.getRating() == 0.0f) {
            movie.setRating((float) tmdbData.voteAverage);
        }
        
        // Update year if missing
        if (movie.getYear() == null || movie.getYear().isEmpty()) {
            if (tmdbData.releaseDate != null && tmdbData.releaseDate.length() >= 4) {
                movie.setYear(tmdbData.releaseDate.substring(0, 4));
            }
        }
        
        // Update duration if missing
        if (movie.getDuration() == null || movie.getDuration().isEmpty()) {
            if (tmdbData.runtime > 0) {
                int hours = tmdbData.runtime / 60;
                int minutes = tmdbData.runtime % 60;
                movie.setDuration(String.format("%d:%02d", hours, minutes));
            }
        }
        
        // Update genres if missing
        if (movie.getGenres() == null || movie.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>();
            for (int i = 0; i < tmdbData.genres.size(); i++) {
                Genre genre = new Genre();
                genre.setId(i + 1);
                genre.setTitle(tmdbData.genres.get(i));
                genres.add(genre);
            }
            movie.setGenres(genres);
        }
        
        // Update actors if missing
        if (movie.getActors() == null || movie.getActors().isEmpty()) {
            List<Actor> actors = new ArrayList<>();
            for (int i = 0; i < Math.min(tmdbData.cast.size(), 5); i++) {
                TMDBService.TMDBMovieData.Actor tmdbActor = tmdbData.cast.get(i);
                Actor actor = new Actor();
                actor.setId(i + 1);
                actor.setName(tmdbActor.name);
                actor.setRole(tmdbActor.character);
                actor.setType("actor");
                // Don't set image - keep original images untouched
                actors.add(actor);
            }
            movie.setActors(actors);
        }
        
        Log.d(TAG, "Enhanced movie: " + movie.getTitle());
    }
    
    /**
     * Enhance TV series with TMDB data
     */
    private void enhanceSeriesWithTMDBData(Poster series, TMDBService.TMDBTVData tmdbData) {
        // Update description if missing or too short
        if (series.getDescription() == null || series.getDescription().isEmpty() || 
            series.getDescription().length() < 50) {
            series.setDescription(tmdbData.overview);
        }
        
        // Update rating if missing
        if (series.getRating() == 0.0f) {
            series.setRating((float) tmdbData.voteAverage);
        }
        
        // Update year if missing
        if (series.getYear() == null || series.getYear().isEmpty()) {
            if (tmdbData.firstAirDate != null && tmdbData.firstAirDate.length() >= 4) {
                series.setYear(tmdbData.firstAirDate.substring(0, 4));
            }
        }
        
        // Update genres if missing
        if (series.getGenres() == null || series.getGenres().isEmpty()) {
            List<Genre> genres = new ArrayList<>();
            for (int i = 0; i < tmdbData.genres.size(); i++) {
                Genre genre = new Genre();
                genre.setId(i + 1);
                genre.setTitle(tmdbData.genres.get(i));
                genres.add(genre);
            }
            series.setGenres(genres);
        }
        
        // Update actors if missing
        if (series.getActors() == null || series.getActors().isEmpty()) {
            List<Actor> actors = new ArrayList<>();
            for (int i = 0; i < Math.min(tmdbData.cast.size(), 5); i++) {
                TMDBService.TMDBTVData.Actor tmdbActor = tmdbData.cast.get(i);
                Actor actor = new Actor();
                actor.setId(i + 1);
                actor.setName(tmdbActor.name);
                actor.setRole(tmdbActor.character);
                actor.setType("actor");
                // Don't set image - keep original images untouched
                actors.add(actor);
            }
            series.setActors(actors);
        }
        
        Log.d(TAG, "Enhanced TV series: " + series.getTitle());
    }
    
    /**
     * Auto-enhance all content in JSON response
     */
    public void autoEnhanceJsonResponse(JsonApiResponse jsonResponse, TMDBEnhancementCallback callback) {
        Log.d(TAG, "Starting auto-enhancement of JSON response");
        
        // Enhance movies
        if (jsonResponse.getMovies() != null) {
            Log.d(TAG, "Found " + jsonResponse.getMovies().size() + " movies/series to process");
            
            for (Poster movie : jsonResponse.getMovies()) {
                if ("movie".equals(movie.getType())) {
                    Log.d(TAG, "Processing movie: " + movie.getTitle());
                    enhanceMovieData(movie, new TMDBEnhancementCallback() {
                        @Override
                        public void onSuccess(Poster enhancedItem) {
                            Log.d(TAG, "Movie enhanced successfully: " + enhancedItem.getTitle());
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.w(TAG, "Failed to enhance movie " + movie.getTitle() + ": " + error);
                        }
                    });
                } else if ("series".equals(movie.getType())) {
                    Log.d(TAG, "Processing TV series: " + movie.getTitle());
                    enhanceTVSeriesData(movie, new TMDBEnhancementCallback() {
                        @Override
                        public void onSuccess(Poster enhancedItem) {
                            Log.d(TAG, "TV series enhanced successfully: " + enhancedItem.getTitle());
                        }
                        
                        @Override
                        public void onError(String error) {
                            Log.w(TAG, "Failed to enhance TV series " + movie.getTitle() + ": " + error);
                        }
                    });
                } else {
                    Log.d(TAG, "Skipping item with type: " + movie.getType() + " - " + movie.getTitle());
                }
            }
        } else {
            Log.d(TAG, "No movies found in JSON response");
        }
        
        // Don't touch channels (Live TV) as requested
        Log.d(TAG, "Auto-enhancement completed - channels left untouched");
        callback.onSuccess(null);
    }
    
    /**
     * Callback interface for enhancement operations
     */
    public interface TMDBEnhancementCallback {
        void onSuccess(Poster enhancedItem);
        void onError(String error);
    }
}