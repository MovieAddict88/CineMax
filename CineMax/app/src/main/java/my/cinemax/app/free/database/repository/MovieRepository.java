package my.cinemax.app.free.database.repository;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.cinemax.app.free.database.CineMaxDatabase;
import my.cinemax.app.free.database.dao.MovieDao;
import my.cinemax.app.free.database.entities.MovieEntity;
import my.cinemax.app.free.entity.Poster;

/**
 * Repository class for movies - single source of truth
 * Handles data from both database and network
 */
public class MovieRepository {
    private static final String TAG = "MovieRepository";
    private static final long CACHE_DURATION = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
    
    private MovieDao movieDao;
    private LiveData<List<MovieEntity>> allMovies;
    private LiveData<List<MovieEntity>> featuredMovies;
    private ExecutorService executor;
    private Gson gson;

    public MovieRepository(Context context) {
        CineMaxDatabase database = CineMaxDatabase.getInstance(context);
        movieDao = database.movieDao();
        allMovies = movieDao.getAllMovies();
        featuredMovies = movieDao.getFeaturedMovies();
        executor = Executors.newFixedThreadPool(2);
        gson = new Gson();
    }

    // Get all movies (from database)
    public LiveData<List<MovieEntity>> getAllMovies() {
        return allMovies;
    }

    // Get featured movies
    public LiveData<List<MovieEntity>> getFeaturedMovies() {
        return featuredMovies;
    }

    // Get movies by type
    public LiveData<List<MovieEntity>> getMoviesOnly() {
        return movieDao.getMoviesOnly();
    }

    public LiveData<List<MovieEntity>> getSeriesOnly() {
        return movieDao.getSeriesOnly();
    }

    // Search movies
    public LiveData<List<MovieEntity>> searchMovies(String query) {
        return movieDao.searchMovies(query);
    }

    // Get movie by ID
    public LiveData<MovieEntity> getMovieById(int id) {
        return movieDao.getMovieById(id);
    }

    // Get top rated movies
    public LiveData<List<MovieEntity>> getTopRatedMovies(int limit) {
        return movieDao.getTopRatedMovies(limit);
    }

    // Get most watched movies
    public LiveData<List<MovieEntity>> getMostWatchedMovies(int limit) {
        return movieDao.getMostWatchedMovies(limit);
    }

    // Insert or update movies from API
    public void insertMoviesFromAPI(List<Poster> apiMovies) {
        executor.execute(() -> {
            try {
                List<MovieEntity> movieEntities = convertPostersToEntities(apiMovies);
                movieDao.insertMovies(movieEntities);
                Log.d(TAG, "Inserted " + movieEntities.size() + " movies into database");
            } catch (Exception e) {
                Log.e(TAG, "Error inserting movies: " + e.getMessage(), e);
            }
        });
    }

    // Convert API Poster objects to MovieEntity
    private List<MovieEntity> convertPostersToEntities(List<Poster> posters) {
        List<MovieEntity> entities = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        
        for (Poster poster : posters) {
            try {
                MovieEntity entity = new MovieEntity();
                entity.setId(poster.getId() != null ? poster.getId() : 0);
                entity.setTitle(poster.getTitle() != null ? poster.getTitle() : "");
                entity.setType(poster.getType() != null ? poster.getType() : "movie");
                entity.setLabel(poster.getLabel() != null ? poster.getLabel() : "");
                entity.setSublabel(poster.getSublabel() != null ? poster.getSublabel() : "");
                entity.setImdb(poster.getImdb() != null ? poster.getImdb() : "");
                entity.setDownloadas(poster.getDownloadas() != null ? poster.getDownloadas() : "");
                entity.setComment(poster.getComment() != null ? poster.getComment() : false);
                entity.setPlayas(poster.getPlayas() != null ? poster.getPlayas() : "");
                entity.setDescription(poster.getDescription() != null ? poster.getDescription() : "");
                entity.setClassification(poster.getClassification() != null ? poster.getClassification() : "");
                entity.setYear(poster.getYear() != null ? poster.getYear() : "");
                entity.setDuration(poster.getDuration() != null ? poster.getDuration() : "");
                entity.setRating(poster.getRating() != null ? poster.getRating() : 0.0f);
                entity.setImage(poster.getImage() != null ? poster.getImage() : "");
                entity.setCover(poster.getCover() != null ? poster.getCover() : "");
                entity.setViews(poster.getViews() != null ? poster.getViews() : 0);
                entity.setCreatedAt(poster.getCreatedAt() != null ? poster.getCreatedAt() : "");
                entity.setTrailer(poster.getTrailer() != null ? poster.getTrailer() : "");
                entity.setFeatured(poster.getFeatured() != null ? poster.getFeatured() : false);
                entity.setLastUpdated(currentTime);

                // Convert complex objects to JSON
                if (poster.getGenres() != null) {
                    entity.setGenres(gson.toJson(poster.getGenres()));
                } else {
                    entity.setGenres("[]");
                }

                if (poster.getActors() != null) {
                    entity.setActors(gson.toJson(poster.getActors()));
                } else {
                    entity.setActors("[]");
                }

                if (poster.getSources() != null) {
                    entity.setSources(gson.toJson(poster.getSources()));
                } else {
                    entity.setSources("[]");
                }

                if (poster.getSubtitles() != null) {
                    entity.setSubtitles(gson.toJson(poster.getSubtitles()));
                } else {
                    entity.setSubtitles("[]");
                }

                entities.add(entity);
            } catch (Exception e) {
                Log.e(TAG, "Error converting poster to entity: " + e.getMessage(), e);
            }
        }
        
        return entities;
    }

    // Check if data needs refresh (older than cache duration)
    public void checkAndRefreshData(DataRefreshCallback callback) {
        executor.execute(() -> {
            try {
                long lastUpdateTime = movieDao.getLastUpdateTime();
                long currentTime = System.currentTimeMillis();
                
                if (lastUpdateTime == 0 || (currentTime - lastUpdateTime) > CACHE_DURATION) {
                    // Data is stale or doesn't exist
                    callback.onRefreshNeeded();
                } else {
                    // Data is fresh
                    callback.onDataFresh();
                }
            } catch (Exception e) {
                Log.e(TAG, "Error checking data freshness: " + e.getMessage(), e);
                callback.onRefreshNeeded(); // Default to refresh on error
            }
        });
    }

    // Get movie count
    public void getMovieCount(CountCallback callback) {
        executor.execute(() -> {
            try {
                int count = movieDao.getMovieCount();
                callback.onCount(count);
            } catch (Exception e) {
                Log.e(TAG, "Error getting movie count: " + e.getMessage(), e);
                callback.onCount(0);
            }
        });
    }

    // Clean old data
    public void cleanOldData() {
        executor.execute(() -> {
            try {
                long threshold = System.currentTimeMillis() - (CACHE_DURATION * 2); // Clean data older than 2x cache duration
                movieDao.deleteOldMovies(threshold);
                Log.d(TAG, "Cleaned old movie data");
            } catch (Exception e) {
                Log.e(TAG, "Error cleaning old data: " + e.getMessage(), e);
            }
        });
    }

    // Callback interfaces
    public interface DataRefreshCallback {
        void onRefreshNeeded();
        void onDataFresh();
    }

    public interface CountCallback {
        void onCount(int count);
    }
}