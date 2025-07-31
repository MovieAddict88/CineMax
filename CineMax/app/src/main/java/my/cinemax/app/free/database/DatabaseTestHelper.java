package my.cinemax.app.free.database;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.cinemax.app.free.database.entities.MovieEntity;

/**
 * Helper class to test database functionality
 */
public class DatabaseTestHelper {
    private static final String TAG = "DatabaseTestHelper";
    
    public static void testDatabaseConnection(Context context) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                // Test database initialization
                CineMaxDatabase database = CineMaxDatabase.getInstance(context);
                
                // Test basic operations
                int movieCount = database.movieDao().getMovieCount();
                int channelCount = database.channelDao().getChannelCount();
                int actorCount = database.actorDao().getActorCount();
                
                Log.d(TAG, "Database test successful:");
                Log.d(TAG, "Movies: " + movieCount);
                Log.d(TAG, "Channels: " + channelCount);
                Log.d(TAG, "Actors: " + actorCount);
                
                // Test inserting a sample movie
                MovieEntity testMovie = new MovieEntity();
                testMovie.setId(999999);
                testMovie.setTitle("Database Test Movie");
                testMovie.setType("movie");
                testMovie.setDescription("Test movie for database verification");
                testMovie.setRating(5.0f);
                testMovie.setLastUpdated(System.currentTimeMillis());
                testMovie.setGenres("[]");
                testMovie.setActors("[]");
                testMovie.setSources("[]");
                testMovie.setSubtitles("[]");
                
                database.movieDao().insertMovie(testMovie);
                
                // Verify insertion
                MovieEntity retrievedMovie = database.movieDao().getMovieByIdSync(999999);
                if (retrievedMovie != null) {
                    Log.d(TAG, "Test movie inserted and retrieved successfully: " + retrievedMovie.getTitle());
                    
                    // Clean up test data
                    database.movieDao().deleteMovie(retrievedMovie);
                    Log.d(TAG, "Test movie cleaned up");
                } else {
                    Log.e(TAG, "Failed to retrieve test movie");
                }
                
                Log.d(TAG, "Database test completed successfully");
                
            } catch (Exception e) {
                Log.e(TAG, "Database test failed: " + e.getMessage(), e);
            }
        });
    }
    
    public static void logDatabaseStatus(Context context) {
        try {
            DataManager dataManager = DataManager.getInstance(context);
            dataManager.checkDatabaseStatus(new DataManager.DatabaseStatusCallback() {
                @Override
                public void onStatus(boolean hasData, int itemCount) {
                    Log.d(TAG, "Database Status - Has Data: " + hasData + ", Item Count: " + itemCount);
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "Error checking database status: " + e.getMessage(), e);
        }
    }
}