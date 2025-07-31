package my.cinemax.app.free.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.entity.Poster;

/**
 * Simplified DataManager for AndroidIDE compatibility
 * Uses SimpleCineMaxDatabase instead of Room
 */
public class SimpleDataManager {
    private static final String TAG = "SimpleDataManager";
    private static SimpleDataManager instance;
    
    private Context context;
    private SimpleCineMaxDatabase database;
    private ExecutorService executor;
    private boolean isInitialized = false;
    
    private SimpleDataManager(Context context) {
        this.context = context.getApplicationContext();
        this.executor = Executors.newFixedThreadPool(2);
        initializeDatabase();
    }
    
    public static synchronized SimpleDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new SimpleDataManager(context);
        }
        return instance;
    }
    
    private void initializeDatabase() {
        try {
            database = SimpleCineMaxDatabase.getInstance(context);
            isInitialized = true;
            Log.d(TAG, "SimpleDataManager initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing SimpleDataManager: " + e.getMessage(), e);
        }
    }
    
    /**
     * Load data with database-first approach
     * 1. Load from database immediately (fast UI)
     * 2. Check if data needs refresh
     * 3. Update from network if needed
     */
    public void loadDataWithCaching(DataLoadCallback callback) {
        if (!isInitialized) {
            callback.onError("DataManager not initialized");
            return;
        }
        
        Log.d(TAG, "Starting database-first data loading...");
        
        executor.execute(() -> {
            try {
                // Check if we have data in database
                int movieCount = database.getMovieCount();
                
                if (movieCount > 0) {
                    // We have data in database, use it immediately
                    Log.d(TAG, "Found " + movieCount + " movies in database");
                    callback.onDataLoaded(true); // true = from cache
                    
                    // Check if data needs refresh in background
                    if (database.needsRefresh()) {
                        Log.d(TAG, "Data is stale, refreshing from network...");
                        refreshDataFromNetwork(callback);
                    } else {
                        Log.d(TAG, "Database data is fresh, no network update needed");
                    }
                } else {
                    // No data in database, must load from network
                    Log.d(TAG, "No data in database, loading from network...");
                    refreshDataFromNetwork(callback);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in loadDataWithCaching: " + e.getMessage(), e);
                callback.onError("Database error: " + e.getMessage());
            }
        });
    }
    
    /**
     * Force refresh data from network
     */
    public void refreshDataFromNetwork(DataLoadCallback callback) {
        Log.d(TAG, "Refreshing data from network...");
        
        apiClient.getJsonApiData(new apiClient.JsonApiCallback() {
            @Override
            public void onSuccess(JsonApiResponse jsonResponse) {
                if (jsonResponse != null && jsonResponse.getMovies() != null) {
                    Log.d(TAG, "Successfully received " + jsonResponse.getMovies().size() + " movies from network");
                    
                    // Save movies to database in background
                    executor.execute(() -> {
                        try {
                            database.insertMovies(jsonResponse.getMovies());
                            Log.d(TAG, "Movies saved to database successfully");
                            callback.onDataLoaded(false); // false = from network
                            callback.onNetworkUpdate("Content updated successfully");
                        } catch (Exception e) {
                            Log.e(TAG, "Error saving movies to database: " + e.getMessage(), e);
                            callback.onError("Failed to save data: " + e.getMessage());
                        }
                    });
                } else {
                    Log.e(TAG, "Received null or empty response from network");
                    callback.onError("Failed to load data from network");
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e(TAG, "Network error: " + error);
                callback.onError("Network error: " + error);
            }
        });
    }
    
    /**
     * Get all movies from database
     */
    public void getAllMovies(MoviesCallback callback) {
        executor.execute(() -> {
            try {
                List<Poster> movies = database.getAllMovies();
                callback.onMoviesLoaded(movies);
            } catch (Exception e) {
                Log.e(TAG, "Error getting all movies: " + e.getMessage(), e);
                callback.onError("Database error: " + e.getMessage());
            }
        });
    }
    
    /**
     * Get movies by type
     */
    public void getMoviesByType(String type, MoviesCallback callback) {
        executor.execute(() -> {
            try {
                List<Poster> movies = database.getMoviesByType(type);
                callback.onMoviesLoaded(movies);
            } catch (Exception e) {
                Log.e(TAG, "Error getting movies by type: " + e.getMessage(), e);
                callback.onError("Database error: " + e.getMessage());
            }
        });
    }
    
    /**
     * Get featured movies
     */
    public void getFeaturedMovies(MoviesCallback callback) {
        executor.execute(() -> {
            try {
                List<Poster> movies = database.getFeaturedMovies();
                callback.onMoviesLoaded(movies);
            } catch (Exception e) {
                Log.e(TAG, "Error getting featured movies: " + e.getMessage(), e);
                callback.onError("Database error: " + e.getMessage());
            }
        });
    }
    
    /**
     * Search movies
     */
    public void searchMovies(String query, MoviesCallback callback) {
        executor.execute(() -> {
            try {
                List<Poster> movies = database.searchMovies(query);
                callback.onMoviesLoaded(movies);
            } catch (Exception e) {
                Log.e(TAG, "Error searching movies: " + e.getMessage(), e);
                callback.onError("Database error: " + e.getMessage());
            }
        });
    }
    
    /**
     * Check database status
     */
    public void checkDatabaseStatus(DatabaseStatusCallback callback) {
        if (!isInitialized) {
            callback.onStatus(false, 0);
            return;
        }
        
        executor.execute(() -> {
            try {
                int count = database.getMovieCount();
                callback.onStatus(count > 0, count);
            } catch (Exception e) {
                Log.e(TAG, "Error checking database status: " + e.getMessage(), e);
                callback.onStatus(false, 0);
            }
        });
    }
    
    /**
     * Clean old cached data
     */
    public void cleanOldData() {
        if (isInitialized) {
            executor.execute(() -> {
                try {
                    database.cleanOldData();
                } catch (Exception e) {
                    Log.e(TAG, "Error cleaning old data: " + e.getMessage(), e);
                }
            });
        }
    }
    
    /**
     * Callback interface for data loading operations
     */
    public interface DataLoadCallback {
        void onDataLoaded(boolean fromCache);
        void onNetworkUpdate(String message);
        void onError(String error);
    }
    
    /**
     * Callback interface for movies operations
     */
    public interface MoviesCallback {
        void onMoviesLoaded(List<Poster> movies);
        void onError(String error);
    }
    
    /**
     * Callback interface for database status
     */
    public interface DatabaseStatusCallback {
        void onStatus(boolean hasData, int itemCount);
    }
}