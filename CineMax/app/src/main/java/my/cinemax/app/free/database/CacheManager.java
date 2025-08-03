package my.cinemax.app.free.database;

import android.content.Context;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import my.cinemax.app.free.repository.CineMaxRepository;
import my.cinemax.app.free.entity.*;
import java.util.List;

/**
 * Simple cache manager to handle data loading and caching
 * This replaces direct API calls with cached data
 */
public class CacheManager {
    
    private static CacheManager instance;
    private CineMaxRepository repository;
    private Context context;
    
    private CacheManager(Context context) {
        this.context = context.getApplicationContext();
        this.repository = new CineMaxRepository(this.context);
    }
    
    public static synchronized CacheManager getInstance(Context context) {
        if (instance == null) {
            instance = new CacheManager(context);
        }
        return instance;
    }
    
    // Initialize cache - call this from Application or SplashActivity
    public void initializeCache() {
        // This will trigger loading data from API if cache is empty or old
        repository.getAllPosters();
        repository.getAllChannels();
        repository.getAllGenres();
        repository.getAllCategories();
    }
    
    // Get cached data methods
    public LiveData<List<Poster>> getMovies() {
        return repository.getPostersByType("movie");
    }
    
    public LiveData<List<Poster>> getSeries() {
        return repository.getPostersByType("serie");
    }
    
    public LiveData<List<Poster>> getAllPosters() {
        return repository.getAllPosters();
    }
    
    public LiveData<List<Channel>> getChannels() {
        return repository.getAllChannels();
    }
    
    public LiveData<List<Genre>> getGenres() {
        return repository.getAllGenres();
    }
    
    public LiveData<List<Category>> getCategories() {
        return repository.getAllCategories();
    }
    
    public LiveData<Poster> getPosterById(int id) {
        return repository.getPosterById(id);
    }
    
    public LiveData<Channel> getChannelById(int id) {
        return repository.getChannelById(id);
    }
    
    // Search methods
    public LiveData<List<Poster>> searchPosters(String query) {
        return repository.searchPosters(query);
    }
    
    public LiveData<List<Channel>> searchChannels(String query) {
        return repository.searchChannels(query);
    }
    
    // Force refresh methods
    public void forceRefreshAll() {
        repository.forceRefreshAll();
    }
    
    public void forceRefreshPosters() {
        repository.forceRefreshPosters();
    }
    
    public void forceRefreshChannels() {
        repository.forceRefreshChannels();
    }
    
    // Check if data is available in cache
    public interface CacheStatusCallback {
        void onCacheReady(boolean hasData);
    }
    
    public void checkCacheStatus(CacheStatusCallback callback) {
        LiveData<List<Poster>> posters = repository.getAllPosters();
        posters.observeForever(new Observer<List<Poster>>() {
            @Override
            public void onChanged(List<Poster> posterList) {
                posters.removeObserver(this);
                callback.onCacheReady(posterList != null && !posterList.isEmpty());
            }
        });
    }
}