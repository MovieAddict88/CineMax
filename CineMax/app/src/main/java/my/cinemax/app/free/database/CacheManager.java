package my.cinemax.app.free.database;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

// Using simplified database instead of Room entities
import my.cinemax.app.free.entity.Episode;
import my.cinemax.app.free.entity.Poster;

public class CacheManager {
    private static final String TAG = "CacheManager";
    private static CacheManager instance;
    private SimpleCacheDatabase database;

    private CacheManager(Context context) {
        database = new SimpleCacheDatabase(context);
    }

    public static CacheManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    // Movie caching methods
    public void cacheMovies(List<Poster> posters) {
        if (posters == null || posters.isEmpty()) return;
        database.insertMovies(posters);
    }

    public void cacheMovie(Poster poster) {
        if (poster == null) return;
        database.insertMovie(poster);
    }

    public List<Poster> getCachedMovies(int type) {
        return database.getValidMovies(type);
    }

    public Poster getCachedMovie(String movieId) {
        return database.getMovieById(movieId);
    }

    // Episode caching methods
    public void cacheEpisodes(List<Episode> episodes, String serieId) {
        if (episodes == null || episodes.isEmpty()) return;
        for (Episode episode : episodes) {
            database.insertEpisode(episode, serieId);
        }
    }

    public void cacheEpisode(Episode episode, String serieId) {
        if (episode == null) return;
        database.insertEpisode(episode, serieId);
    }

    public List<Episode> getCachedEpisodes(String serieId) {
        return database.getValidEpisodes(serieId);
    }

    // Cache management methods
    public void clearExpiredCache() {
        database.clearExpiredCache();
        Log.d(TAG, "Cleared expired cache");
    }

    public void clearAllCache() {
        database.clearAllCache();
        Log.d(TAG, "Cleared all cache");
    }

    public String getCacheStats() {
        return database.getCacheStats();
    }

    public boolean hasCachedMovies(int type) {
        return database.hasValidMovies(type);
    }

    public boolean hasCachedEpisodes(String serieId) {
        return database.hasValidEpisodes(serieId);
    }

    // No conversion methods needed - working directly with original entities
}