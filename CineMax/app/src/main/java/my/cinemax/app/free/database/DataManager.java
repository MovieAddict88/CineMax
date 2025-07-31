package my.cinemax.app.free.database;

import android.content.Context;
import android.util.Log;

import java.util.List;

import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.database.repository.MovieRepository;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Actor;

/**
 * Central data manager for CineMax app
 * Coordinates between repositories and API calls
 * Provides single interface for data operations
 */
public class DataManager {
    private static final String TAG = "DataManager";
    private static DataManager instance;
    
    private Context context;
    private MovieRepository movieRepository;
    private boolean isInitialized = false;
    
    private DataManager(Context context) {
        this.context = context.getApplicationContext();
        initializeRepositories();
    }
    
    public static synchronized DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager(context);
        }
        return instance;
    }
    
    private void initializeRepositories() {
        try {
            movieRepository = new MovieRepository(context);
            isInitialized = true;
            Log.d(TAG, "DataManager initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing DataManager: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get movie repository
     */
    public MovieRepository getMovieRepository() {
        return movieRepository;
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
        
        Log.d(TAG, "Starting data load with caching...");
        
        // First, check if we have any data and if it needs refresh
        movieRepository.getMovieCount(count -> {
            if (count > 0) {
                // We have data in database, use it immediately
                Log.d(TAG, "Found " + count + " movies in database");
                callback.onDataLoaded(true); // true = from cache
                
                // Check if data needs refresh in background
                movieRepository.checkAndRefreshData(new MovieRepository.DataRefreshCallback() {
                    @Override
                    public void onRefreshNeeded() {
                        Log.d(TAG, "Data refresh needed, updating from network...");
                        refreshDataFromNetwork(callback);
                    }
                    
                    @Override
                    public void onDataFresh() {
                        Log.d(TAG, "Database data is fresh, no network update needed");
                    }
                });
            } else {
                // No data in database, must load from network
                Log.d(TAG, "No data in database, loading from network...");
                refreshDataFromNetwork(callback);
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
                if (jsonResponse != null) {
                    Log.d(TAG, "Successfully received data from network");
                    
                    // Save movies to database
                    if (jsonResponse.getMovies() != null && !jsonResponse.getMovies().isEmpty()) {
                        movieRepository.insertMoviesFromAPI(jsonResponse.getMovies());
                        Log.d(TAG, "Saved " + jsonResponse.getMovies().size() + " movies to database");
                    }
                    
                    // TODO: Save channels and actors to their respective repositories
                    // This will be implemented when we create ChannelRepository and ActorRepository
                    
                    callback.onDataLoaded(false); // false = from network
                    callback.onNetworkUpdate("Content updated successfully");
                } else {
                    Log.e(TAG, "Received null response from network");
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
     * Clean old cached data
     */
    public void cleanOldData() {
        if (isInitialized) {
            movieRepository.cleanOldData();
            // TODO: Clean other repositories when implemented
        }
    }
    
    /**
     * Check if database has any data
     */
    public void checkDatabaseStatus(DatabaseStatusCallback callback) {
        if (!isInitialized) {
            callback.onStatus(false, 0);
            return;
        }
        
        movieRepository.getMovieCount(count -> {
            callback.onStatus(count > 0, count);
        });
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
     * Callback interface for database status
     */
    public interface DatabaseStatusCallback {
        void onStatus(boolean hasData, int itemCount);
    }
}