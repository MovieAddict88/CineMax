package my.cinemax.app.free.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import my.cinemax.app.free.database.dao.EpisodeDao;
import my.cinemax.app.free.database.dao.MovieDao;
import my.cinemax.app.free.database.entities.CachedEpisode;
import my.cinemax.app.free.database.entities.CachedMovie;

@Database(
        entities = {CachedMovie.class, CachedEpisode.class},
        version = 1,
        exportSchema = false
)
public abstract class CineMaxDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "cinemax_cache_db";
    private static volatile CineMaxDatabase INSTANCE;

    public abstract MovieDao movieDao();
    public abstract EpisodeDao episodeDao();

    public static CineMaxDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CineMaxDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            CineMaxDatabase.class,
                            DATABASE_NAME
                    )
                    .allowMainThreadQueries() // For simplicity, but consider using background threads
                    .fallbackToDestructiveMigration() // For development, removes need for migrations
                    .build();
                }
            }
        }
        return INSTANCE;
    }

    // Migration from version 1 to 2 (for future use)
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // Add migration logic here when needed
        }
    };

    // Method to clear all cached data
    public void clearAllCache() {
        movieDao().deleteAllMovies();
        episodeDao().deleteAllEpisodes();
    }

    // Method to clear expired cache (older than 24 hours)
    public void clearExpiredCache() {
        long expiredTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours ago
        movieDao().deleteExpiredMovies(expiredTimestamp);
        episodeDao().deleteExpiredEpisodes(expiredTimestamp);
    }

    // Method to get cache statistics
    public String getCacheStats() {
        int movieCount = movieDao().getMovieCount();
        int episodeCount = episodeDao().getEpisodeCount();
        return "Movies: " + movieCount + ", Episodes: " + episodeCount;
    }
}