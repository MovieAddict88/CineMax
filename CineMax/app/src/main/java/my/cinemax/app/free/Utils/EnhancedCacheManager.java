package my.cinemax.app.free.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;
import com.squareup.picasso.Picasso;

import my.cinemax.app.free.entity.*;
import my.cinemax.app.free.database.CineMaxDatabase;
import my.cinemax.app.free.database.dao.MovieDao;
import my.cinemax.app.free.database.dao.TvSeriesDao;
import my.cinemax.app.free.database.dao.ChannelDao;
import my.cinemax.app.free.database.dao.ActorDao;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Enhanced Cache Manager - Multi-Layer Caching Architecture
 * 
 * This system provides a comprehensive caching solution for CineMax with:
 * - Memory Cache: Fastest access for frequently used data
 * - Disk Cache: Persistent storage for large datasets
 * - Image Cache: Optimized image caching with compression
 * - Network Cache: Intelligent API response caching
 * - Database Layer: SQLite for complex queries and relationships
 * 
 * Designed to handle 10,000+ entries efficiently without performance degradation.
 */
public class EnhancedCacheManager {
    
    private static final String TAG = "EnhancedCacheManager";
    private static EnhancedCacheManager instance;
    
    // Cache prefixes
    private static final String MEMORY_CACHE_PREFIX = "mem_";
    private static final String DISK_CACHE_PREFIX = "disk_";
    private static final String IMAGE_CACHE_PREFIX = "img_";
    private static final String NETWORK_CACHE_PREFIX = "net_";
    private static final String METADATA_PREFIX = "meta_";
    
    // Configuration
    private static final long CACHE_EXPIRY_TIME = TimeUnit.HOURS.toMillis(24); // 24 hours
    private static final int MEMORY_CACHE_SIZE = 50; // MB
    private static final int DISK_CACHE_SIZE = 100; // MB
    private static final int IMAGE_CACHE_SIZE = 50; // MB
    private static final int CHUNK_SIZE = 500; // Items per chunk
    private static final int CURRENT_CACHE_VERSION = 4;
    
    // Layer managers
    private MemoryCacheLayer memoryCache;
    private DiskCacheLayer diskCache;
    private ImageCacheLayer imageCache;
    private NetworkCacheLayer networkCache;
    private DatabaseLayer databaseLayer;
    
    // Threading
    private ExecutorService executorService;
    private boolean isInitialized = false;
    private Context context;
    private Gson gson;
    
    // Statistics
    private CacheStats stats;
    
    private EnhancedCacheManager() {
        this.gson = new Gson();
        this.executorService = Executors.newFixedThreadPool(4);
        this.stats = new CacheStats();
    }
    
    public static synchronized EnhancedCacheManager getInstance() {
        if (instance == null) {
            instance = new EnhancedCacheManager();
        }
        return instance;
    }
    
    /**
     * Initialize all cache layers
     */
    public void initialize(Context context) {
        if (isInitialized) return;
        
        this.context = context.getApplicationContext();
        
        try {
            // Initialize Hawk if not already done
            if (!Hawk.isBuilt()) {
                Hawk.init(context).build();
            }
            
            // Initialize all cache layers
            memoryCache = new MemoryCacheLayer();
            diskCache = new DiskCacheLayer(context);
            imageCache = new ImageCacheLayer(context);
            networkCache = new NetworkCacheLayer();
            databaseLayer = new DatabaseLayer(context);
            
            // Validate cache version
            validateCacheVersion();
            
            isInitialized = true;
            Log.d(TAG, "Enhanced Cache Manager initialized successfully");
            
            // Preload essential data
            preloadEssentialData();
            
        } catch (Exception e) {
            Log.e(TAG, "Error initializing enhanced cache manager", e);
        }
    }
    
    /**
     * Store complete API response across all layers
     */
    public void storeApiResponse(JsonApiResponse response) {
        if (!isInitialized) {
            Log.e(TAG, "Enhanced Cache Manager not initialized");
            return;
        }
        
        executorService.execute(() -> {
            try {
                Log.d(TAG, "Storing API response across all cache layers");
                long startTime = System.currentTimeMillis();
                
                // Store in network cache (full response)
                networkCache.store(NETWORK_CACHE_PREFIX + "full_response", response);
                
                // Store individual components with chunking
                if (response.getMovies() != null) {
                    storeMoviesInLayers(response.getMovies());
                }
                
                if (response.getTvSeries() != null) {
                    storeTvSeriesInLayers(response.getTvSeries());
                }
                
                if (response.getChannels() != null) {
                    storeChannelsInLayers(response.getChannels());
                }
                
                if (response.getActors() != null) {
                    storeActorsInLayers(response.getActors());
                }
                
                // Store metadata
                storeMetadata(response);
                
                long endTime = System.currentTimeMillis();
                Log.d(TAG, "API response stored in " + (endTime - startTime) + "ms");
                
                // Update statistics
                updateStats(response);
                
            } catch (Exception e) {
                Log.e(TAG, "Error storing API response", e);
            }
        });
    }
    
    /**
     * Get cached API response with layer priority
     */
    public JsonApiResponse getCachedApiResponse() {
        if (!isInitialized) return null;
        
        // Try memory cache first
        JsonApiResponse response = memoryCache.get(MEMORY_CACHE_PREFIX + "full_response");
        if (response != null) {
            stats.memoryCacheHits++;
            return response;
        }
        
        // Try network cache
        response = networkCache.get(NETWORK_CACHE_PREFIX + "full_response");
        if (response != null) {
            // Store in memory for faster access
            memoryCache.store(MEMORY_CACHE_PREFIX + "full_response", response);
            stats.networkCacheHits++;
            return response;
        }
        
        // Try disk cache
        response = diskCache.get(DISK_CACHE_PREFIX + "full_response");
        if (response != null) {
            // Store in memory and network cache
            memoryCache.store(MEMORY_CACHE_PREFIX + "full_response", response);
            networkCache.store(NETWORK_CACHE_PREFIX + "full_response", response);
            stats.diskCacheHits++;
            return response;
        }
        
        stats.cacheMisses++;
        return null;
    }
    
    /**
     * Get movies with intelligent layer selection
     */
    public List<Poster> getAllMovies() {
        if (!isInitialized) return new ArrayList<>();
        
        // Try memory cache first
        List<Poster> movies = memoryCache.getMovies();
        if (movies != null && !movies.isEmpty()) {
            stats.memoryCacheHits++;
            return movies;
        }
        
        // Try database (fastest for large datasets)
        movies = databaseLayer.getAllMovies();
        if (movies != null && !movies.isEmpty()) {
            // Cache in memory for faster access
            memoryCache.storeMovies(movies);
            stats.databaseHits++;
            return movies;
        }
        
        // Try disk cache
        movies = diskCache.getMovies();
        if (movies != null && !movies.isEmpty()) {
            // Store in memory and database
            memoryCache.storeMovies(movies);
            databaseLayer.storeMovies(movies);
            stats.diskCacheHits++;
            return movies;
        }
        
        stats.cacheMisses++;
        return new ArrayList<>();
    }
    
    /**
     * Get paginated movies for efficient memory usage
     */
    public List<Poster> getMoviesPaginated(int page, int pageSize) {
        if (!isInitialized) return new ArrayList<>();
        
        // Database is most efficient for pagination
        List<Poster> movies = databaseLayer.getMoviesPaginated(page, pageSize);
        if (movies != null && !movies.isEmpty()) {
            stats.databaseHits++;
            return movies;
        }
        
        // Fallback to disk cache
        movies = diskCache.getMoviesPaginated(page, pageSize);
        if (movies != null && !movies.isEmpty()) {
            stats.diskCacheHits++;
            return movies;
        }
        
        stats.cacheMisses++;
        return new ArrayList<>();
    }
    
    /**
     * Search movies across all layers
     */
    public List<Poster> searchMovies(String query) {
        if (!isInitialized || query == null || query.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // Database search is most efficient
        List<Poster> results = databaseLayer.searchMovies(query);
        if (results != null && !results.isEmpty()) {
            stats.databaseHits++;
            return results;
        }
        
        // Fallback to memory search
        results = memoryCache.searchMovies(query);
        if (results != null && !results.isEmpty()) {
            stats.memoryCacheHits++;
            return results;
        }
        
        stats.cacheMisses++;
        return new ArrayList<>();
    }
    
    /**
     * Get movie by ID with layer priority
     */
    public Poster getMovieById(int movieId) {
        if (!isInitialized) return null;
        
        // Try memory cache first
        Poster movie = memoryCache.getMovieById(movieId);
        if (movie != null) {
            stats.memoryCacheHits++;
            return movie;
        }
        
        // Try database
        movie = databaseLayer.getMovieById(movieId);
        if (movie != null) {
            // Cache in memory
            memoryCache.storeMovie(movie);
            stats.databaseHits++;
            return movie;
        }
        
        stats.cacheMisses++;
        return null;
    }
    
    /**
     * Store movies across all layers with chunking
     */
    private void storeMoviesInLayers(List<Poster> movies) {
        if (movies == null || movies.isEmpty()) return;
        
        // Store in memory (frequently accessed items)
        memoryCache.storeMovies(movies.subList(0, Math.min(movies.size(), 100)));
        
        // Store in database (all items with indexing)
        databaseLayer.storeMovies(movies);
        
        // Store in disk cache (chunked for large datasets)
        List<List<Poster>> chunks = chunkList(movies, CHUNK_SIZE);
        for (int i = 0; i < chunks.size(); i++) {
            diskCache.store(DISK_CACHE_PREFIX + "movies_chunk_" + i, chunks.get(i));
        }
        
        // Store chunk metadata
        Hawk.put(DISK_CACHE_PREFIX + "movies_chunks_count", chunks.size());
    }
    
    /**
     * Store TV series across all layers
     */
    private void storeTvSeriesInLayers(List<Poster> tvSeries) {
        if (tvSeries == null || tvSeries.isEmpty()) return;
        
        memoryCache.storeTvSeries(tvSeries.subList(0, Math.min(tvSeries.size(), 100)));
        databaseLayer.storeTvSeries(tvSeries);
        
        List<List<Poster>> chunks = chunkList(tvSeries, CHUNK_SIZE);
        for (int i = 0; i < chunks.size(); i++) {
            diskCache.store(DISK_CACHE_PREFIX + "tv_series_chunk_" + i, chunks.get(i));
        }
        
        Hawk.put(DISK_CACHE_PREFIX + "tv_series_chunks_count", chunks.size());
    }
    
    /**
     * Store channels across all layers
     */
    private void storeChannelsInLayers(List<Channel> channels) {
        if (channels == null || channels.isEmpty()) return;
        
        memoryCache.storeChannels(channels.subList(0, Math.min(channels.size(), 50)));
        databaseLayer.storeChannels(channels);
        
        List<List<Channel>> chunks = chunkList(channels, CHUNK_SIZE);
        for (int i = 0; i < chunks.size(); i++) {
            diskCache.store(DISK_CACHE_PREFIX + "channels_chunk_" + i, chunks.get(i));
        }
        
        Hawk.put(DISK_CACHE_PREFIX + "channels_chunks_count", chunks.size());
    }
    
    /**
     * Store actors across all layers
     */
    private void storeActorsInLayers(List<Actor> actors) {
        if (actors == null || actors.isEmpty()) return;
        
        memoryCache.storeActors(actors.subList(0, Math.min(actors.size(), 200)));
        databaseLayer.storeActors(actors);
        
        List<List<Actor>> chunks = chunkList(actors, CHUNK_SIZE);
        for (int i = 0; i < chunks.size(); i++) {
            diskCache.store(DISK_CACHE_PREFIX + "actors_chunk_" + i, chunks.get(i));
        }
        
        Hawk.put(DISK_CACHE_PREFIX + "actors_chunks_count", chunks.size());
    }
    
    /**
     * Store metadata
     */
    private void storeMetadata(JsonApiResponse response) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("last_update", System.currentTimeMillis());
        metadata.put("version", CURRENT_CACHE_VERSION);
        metadata.put("movies_count", response.getMovies() != null ? response.getMovies().size() : 0);
        metadata.put("tv_series_count", response.getTvSeries() != null ? response.getTvSeries().size() : 0);
        metadata.put("channels_count", response.getChannels() != null ? response.getChannels().size() : 0);
        metadata.put("actors_count", response.getActors() != null ? response.getActors().size() : 0);
        
        Hawk.put(METADATA_PREFIX + "cache_info", metadata);
    }
    
    /**
     * Check if cache is valid
     */
    public boolean isCacheValid() {
        if (!isInitialized) return false;
        
        Map<String, Object> metadata = Hawk.get(METADATA_PREFIX + "cache_info");
        if (metadata == null) return false;
        
        long lastUpdate = (Long) metadata.get("last_update");
        int version = (Integer) metadata.get("version");
        
        return (System.currentTimeMillis() - lastUpdate) < CACHE_EXPIRY_TIME && 
               version == CURRENT_CACHE_VERSION;
    }
    
    /**
     * Clear all cache layers
     */
    public void clearAllCaches() {
        if (!isInitialized) return;
        
        executorService.execute(() -> {
            try {
                memoryCache.clear();
                diskCache.clear();
                imageCache.clear();
                networkCache.clear();
                databaseLayer.clear();
                
                Log.d(TAG, "All cache layers cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing caches", e);
            }
        });
    }
    
    /**
     * Get cache statistics
     */
    public CacheStats getStats() {
        return stats;
    }
    
    /**
     * Preload essential data
     */
    private void preloadEssentialData() {
        executorService.execute(() -> {
            try {
                // Preload first page of movies and TV series
                getMoviesPaginated(0, 20);
                getTvSeriesPaginated(0, 20);
                
                Log.d(TAG, "Essential data preloaded");
            } catch (Exception e) {
                Log.e(TAG, "Error preloading essential data", e);
            }
        });
    }
    
    /**
     * Validate cache version
     */
    private void validateCacheVersion() {
        int storedVersion = Hawk.get(METADATA_PREFIX + "version", 0);
        if (storedVersion != CURRENT_CACHE_VERSION) {
            Log.d(TAG, "Cache version mismatch, clearing old cache");
            clearAllCaches();
        }
    }
    
    /**
     * Update statistics
     */
    private void updateStats(JsonApiResponse response) {
        stats.totalItems = (response.getMovies() != null ? response.getMovies().size() : 0) +
                          (response.getTvSeries() != null ? response.getTvSeries().size() : 0) +
                          (response.getChannels() != null ? response.getChannels().size() : 0) +
                          (response.getActors() != null ? response.getActors().size() : 0);
        stats.lastUpdate = System.currentTimeMillis();
    }
    
    /**
     * Chunk list for efficient storage
     */
    private <T> List<List<T>> chunkList(List<T> list, int chunkSize) {
        List<List<T>> chunks = new ArrayList<>();
        for (int i = 0; i < list.size(); i += chunkSize) {
            chunks.add(list.subList(i, Math.min(i + chunkSize, list.size())));
        }
        return chunks;
    }
    
    /**
     * Get TV series paginated
     */
    public List<Poster> getTvSeriesPaginated(int page, int pageSize) {
        if (!isInitialized) return new ArrayList<>();
        
        List<Poster> series = databaseLayer.getTvSeriesPaginated(page, pageSize);
        if (series != null && !series.isEmpty()) {
            stats.databaseHits++;
            return series;
        }
        
        series = diskCache.getTvSeriesPaginated(page, pageSize);
        if (series != null && !series.isEmpty()) {
            stats.diskCacheHits++;
            return series;
        }
        
        stats.cacheMisses++;
        return new ArrayList<>();
    }
    
    /**
     * Get all channels
     */
    public List<Channel> getAllChannels() {
        if (!isInitialized) return new ArrayList<>();
        
        List<Channel> channels = memoryCache.getChannels();
        if (channels != null && !channels.isEmpty()) {
            stats.memoryCacheHits++;
            return channels;
        }
        
        channels = databaseLayer.getAllChannels();
        if (channels != null && !channels.isEmpty()) {
            memoryCache.storeChannels(channels);
            stats.databaseHits++;
            return channels;
        }
        
        stats.cacheMisses++;
        return new ArrayList<>();
    }
    
    /**
     * Get all actors
     */
    public List<Actor> getAllActors() {
        if (!isInitialized) return new ArrayList<>();
        
        List<Actor> actors = memoryCache.getActors();
        if (actors != null && !actors.isEmpty()) {
            stats.memoryCacheHits++;
            return actors;
        }
        
        actors = databaseLayer.getAllActors();
        if (actors != null && !actors.isEmpty()) {
            memoryCache.storeActors(actors);
            stats.databaseHits++;
            return actors;
        }
        
        stats.cacheMisses++;
        return new ArrayList<>();
    }
    
    /**
     * Cache statistics
     */
    public static class CacheStats {
        public int memoryCacheHits = 0;
        public int diskCacheHits = 0;
        public int networkCacheHits = 0;
        public int databaseHits = 0;
        public int cacheMisses = 0;
        public int totalItems = 0;
        public long lastUpdate = 0;
        
        public int getTotalHits() {
            return memoryCacheHits + diskCacheHits + networkCacheHits + databaseHits;
        }
        
        public double getHitRate() {
            int total = getTotalHits() + cacheMisses;
            return total > 0 ? (double) getTotalHits() / total : 0.0;
        }
        
        @Override
        public String toString() {
            return String.format("CacheStats{hits=%d, misses=%d, hitRate=%.2f%%, items=%d}",
                    getTotalHits(), cacheMisses, getHitRate() * 100, totalItems);
        }
    }
    
    /**
     * Shutdown cache manager
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
}