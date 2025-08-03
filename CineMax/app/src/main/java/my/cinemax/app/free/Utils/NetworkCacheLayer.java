package my.cinemax.app.free.Utils;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.orhanobut.hawk.Hawk;

import my.cinemax.app.free.entity.*;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Network Cache Layer - Intelligent API response caching
 * 
 * Features:
 * - API response caching with expiration
 * - Version control for cache invalidation
 * - Background cache management
 * - Efficient JSON storage and retrieval
 * - Cache statistics and monitoring
 * - Automatic cleanup of expired entries
 */
public class NetworkCacheLayer {
    
    private static final String TAG = "NetworkCacheLayer";
    
    // Cache configuration
    private static final String NETWORK_CACHE_PREFIX = "net_cache_";
    private static final String NETWORK_METADATA_PREFIX = "net_meta_";
    private static final long DEFAULT_CACHE_EXPIRY = TimeUnit.HOURS.toMillis(6); // 6 hours
    private static final long SHORT_CACHE_EXPIRY = TimeUnit.MINUTES.toMillis(30); // 30 minutes
    private static final long LONG_CACHE_EXPIRY = TimeUnit.HOURS.toMillis(24); // 24 hours
    
    private final Gson gson;
    private final ExecutorService executorService;
    private final Map<String, CacheEntry> memoryCache;
    
    public NetworkCacheLayer() {
        this.gson = new Gson();
        this.executorService = Executors.newFixedThreadPool(2);
        this.memoryCache = new ConcurrentHashMap<>();
        
        Log.d(TAG, "Network cache layer initialized");
    }
    
    /**
     * Store API response in network cache
     */
    public void store(String key, Object response) {
        store(key, response, DEFAULT_CACHE_EXPIRY);
    }
    
    /**
     * Store API response with custom expiration
     */
    public void store(String key, Object response, long expirationTime) {
        if (response == null) return;
        
        executorService.execute(() -> {
            try {
                String fullKey = NETWORK_CACHE_PREFIX + key;
                String metadataKey = NETWORK_METADATA_PREFIX + key;
                
                // Convert to JSON
                String json = gson.toJson(response);
                
                // Store response
                Hawk.put(fullKey, json);
                
                // Store metadata
                CacheMetadata metadata = new CacheMetadata();
                metadata.key = key;
                metadata.timestamp = System.currentTimeMillis();
                metadata.expirationTime = expirationTime;
                metadata.size = json.length();
                metadata.type = response.getClass().getSimpleName();
                
                String metadataJson = gson.toJson(metadata);
                Hawk.put(metadataKey, metadataJson);
                
                // Store in memory cache for fast access
                CacheEntry entry = new CacheEntry();
                entry.response = response;
                entry.metadata = metadata;
                memoryCache.put(key, entry);
                
                Log.d(TAG, "Stored network response: " + key + " (expires in " + (expirationTime / 60000) + " minutes)");
                
            } catch (Exception e) {
                Log.e(TAG, "Error storing network response: " + key, e);
            }
        });
    }
    
    /**
     * Get API response from network cache
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return get(key, null);
    }
    
    /**
     * Get API response with type information
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        try {
            // Try memory cache first
            CacheEntry memoryEntry = memoryCache.get(key);
            if (memoryEntry != null && !isExpired(memoryEntry.metadata)) {
                Log.d(TAG, "Network response loaded from memory cache: " + key);
                return (T) memoryEntry.response;
            }
            
            // Try persistent cache
            String fullKey = NETWORK_CACHE_PREFIX + key;
            String metadataKey = NETWORK_METADATA_PREFIX + key;
            
            String json = Hawk.get(fullKey);
            String metadataJson = Hawk.get(metadataKey);
            
            if (json != null && metadataJson != null) {
                CacheMetadata metadata = gson.fromJson(metadataJson, CacheMetadata.class);
                
                if (!isExpired(metadata)) {
                    // Parse response
                    T response;
                    if (type != null) {
                        response = gson.fromJson(json, type);
                    } else {
                        Type genericType = new TypeToken<T>(){}.getType();
                        response = gson.fromJson(json, genericType);
                    }
                    
                    // Store in memory cache
                    CacheEntry entry = new CacheEntry();
                    entry.response = response;
                    entry.metadata = metadata;
                    memoryCache.put(key, entry);
                    
                    Log.d(TAG, "Network response loaded from persistent cache: " + key);
                    return response;
                } else {
                    // Remove expired entry
                    remove(key);
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving network response: " + key, e);
        }
        
        return null;
    }
    
    /**
     * Check if cache entry is expired
     */
    private boolean isExpired(CacheMetadata metadata) {
        if (metadata == null) return true;
        
        long currentTime = System.currentTimeMillis();
        long expirationTime = metadata.timestamp + metadata.expirationTime;
        
        return currentTime > expirationTime;
    }
    
    /**
     * Remove cache entry
     */
    public void remove(String key) {
        try {
            String fullKey = NETWORK_CACHE_PREFIX + key;
            String metadataKey = NETWORK_METADATA_PREFIX + key;
            
            Hawk.delete(fullKey);
            Hawk.delete(metadataKey);
            memoryCache.remove(key);
            
            Log.d(TAG, "Removed network cache entry: " + key);
        } catch (Exception e) {
            Log.e(TAG, "Error removing network cache entry: " + key, e);
        }
    }
    
    /**
     * Store full API response
     */
    public void storeFullApiResponse(JsonApiResponse response) {
        store("full_api_response", response, LONG_CACHE_EXPIRY);
    }
    
    /**
     * Get full API response
     */
    public JsonApiResponse getFullApiResponse() {
        return get("full_api_response", JsonApiResponse.class);
    }
    
    /**
     * Store movies API response
     */
    public void storeMoviesResponse(List<Poster> movies) {
        store("movies_response", movies, DEFAULT_CACHE_EXPIRY);
    }
    
    /**
     * Get movies API response
     */
    public List<Poster> getMoviesResponse() {
        return get("movies_response", new TypeToken<List<Poster>>(){}.getType());
    }
    
    /**
     * Store TV series API response
     */
    public void storeTvSeriesResponse(List<Poster> tvSeries) {
        store("tv_series_response", tvSeries, DEFAULT_CACHE_EXPIRY);
    }
    
    /**
     * Get TV series API response
     */
    public List<Poster> getTvSeriesResponse() {
        return get("tv_series_response", new TypeToken<List<Poster>>(){}.getType());
    }
    
    /**
     * Store channels API response
     */
    public void storeChannelsResponse(List<Channel> channels) {
        store("channels_response", channels, DEFAULT_CACHE_EXPIRY);
    }
    
    /**
     * Get channels API response
     */
    public List<Channel> getChannelsResponse() {
        return get("channels_response", new TypeToken<List<Channel>>(){}.getType());
    }
    
    /**
     * Store actors API response
     */
    public void storeActorsResponse(List<Actor> actors) {
        store("actors_response", actors, DEFAULT_CACHE_EXPIRY);
    }
    
    /**
     * Get actors API response
     */
    public List<Actor> getActorsResponse() {
        return get("actors_response", new TypeToken<List<Actor>>(){}.getType());
    }
    
    /**
     * Store search results
     */
    public void storeSearchResults(String query, List<Poster> results) {
        String key = "search_" + query.toLowerCase().replaceAll("[^a-z0-9]", "_");
        store(key, results, SHORT_CACHE_EXPIRY);
    }
    
    /**
     * Get search results
     */
    public List<Poster> getSearchResults(String query) {
        String key = "search_" + query.toLowerCase().replaceAll("[^a-z0-9]", "_");
        return get(key, new TypeToken<List<Poster>>(){}.getType());
    }
    
    /**
     * Store genre filtered results
     */
    public void storeGenreResults(int genreId, List<Poster> results) {
        String key = "genre_" + genreId;
        store(key, results, DEFAULT_CACHE_EXPIRY);
    }
    
    /**
     * Get genre filtered results
     */
    public List<Poster> getGenreResults(int genreId) {
        String key = "genre_" + genreId;
        return get(key, new TypeToken<List<Poster>>(){}.getType());
    }
    
    /**
     * Check if cache entry exists and is valid
     */
    public boolean hasValidEntry(String key) {
        try {
            // Check memory cache first
            CacheEntry memoryEntry = memoryCache.get(key);
            if (memoryEntry != null && !isExpired(memoryEntry.metadata)) {
                return true;
            }
            
            // Check persistent cache
            String metadataKey = NETWORK_METADATA_PREFIX + key;
            String metadataJson = Hawk.get(metadataKey);
            
            if (metadataJson != null) {
                CacheMetadata metadata = gson.fromJson(metadataJson, CacheMetadata.class);
                return !isExpired(metadata);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error checking cache entry validity: " + key, e);
        }
        
        return false;
    }
    
    /**
     * Get cache entry metadata
     */
    public CacheMetadata getMetadata(String key) {
        try {
            // Check memory cache first
            CacheEntry memoryEntry = memoryCache.get(key);
            if (memoryEntry != null) {
                return memoryEntry.metadata;
            }
            
            // Check persistent cache
            String metadataKey = NETWORK_METADATA_PREFIX + key;
            String metadataJson = Hawk.get(metadataKey);
            
            if (metadataJson != null) {
                return gson.fromJson(metadataJson, CacheMetadata.class);
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting cache metadata: " + key, e);
        }
        
        return null;
    }
    
    /**
     * Clear expired entries
     */
    public void clearExpiredEntries() {
        executorService.execute(() -> {
            try {
                List<String> keysToRemove = new ArrayList<>();
                
                // Check all metadata entries
                for (String key : Hawk.getAll().keySet()) {
                    if (key.startsWith(NETWORK_METADATA_PREFIX)) {
                        String cacheKey = key.substring(NETWORK_METADATA_PREFIX.length());
                        String metadataJson = Hawk.get(key);
                        
                        if (metadataJson != null) {
                            CacheMetadata metadata = gson.fromJson(metadataJson, CacheMetadata.class);
                            if (isExpired(metadata)) {
                                keysToRemove.add(cacheKey);
                            }
                        }
                    }
                }
                
                // Remove expired entries
                for (String key : keysToRemove) {
                    remove(key);
                }
                
                Log.d(TAG, "Cleared " + keysToRemove.size() + " expired network cache entries");
                
            } catch (Exception e) {
                Log.e(TAG, "Error clearing expired network cache entries", e);
            }
        });
    }
    
    /**
     * Clear all network cache
     */
    public void clear() {
        try {
            // Clear memory cache
            memoryCache.clear();
            
            // Clear persistent cache
            List<String> keysToRemove = new ArrayList<>();
            for (String key : Hawk.getAll().keySet()) {
                if (key.startsWith(NETWORK_CACHE_PREFIX) || key.startsWith(NETWORK_METADATA_PREFIX)) {
                    keysToRemove.add(key);
                }
            }
            
            for (String key : keysToRemove) {
                Hawk.delete(key);
            }
            
            Log.d(TAG, "Network cache cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing network cache", e);
        }
    }
    
    /**
     * Get cache statistics
     */
    public NetworkCacheStats getStats() {
        NetworkCacheStats stats = new NetworkCacheStats();
        
        try {
            // Count entries
            int totalEntries = 0;
            int expiredEntries = 0;
            long totalSize = 0;
            
            for (String key : Hawk.getAll().keySet()) {
                if (key.startsWith(NETWORK_METADATA_PREFIX)) {
                    totalEntries++;
                    String metadataJson = Hawk.get(key);
                    
                    if (metadataJson != null) {
                        CacheMetadata metadata = gson.fromJson(metadataJson, CacheMetadata.class);
                        totalSize += metadata.size;
                        
                        if (isExpired(metadata)) {
                            expiredEntries++;
                        }
                    }
                }
            }
            
            stats.totalEntries = totalEntries;
            stats.expiredEntries = expiredEntries;
            stats.validEntries = totalEntries - expiredEntries;
            stats.memoryCacheSize = memoryCache.size();
            stats.totalSizeBytes = totalSize;
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting network cache stats", e);
        }
        
        return stats;
    }
    
    /**
     * Shutdown network cache layer
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    /**
     * Cache entry class
     */
    private static class CacheEntry {
        Object response;
        CacheMetadata metadata;
    }
    
    /**
     * Cache metadata class
     */
    public static class CacheMetadata {
        public String key;
        public long timestamp;
        public long expirationTime;
        public int size;
        public String type;
        
        public boolean isExpired() {
            long currentTime = System.currentTimeMillis();
            long expirationTime = this.timestamp + this.expirationTime;
            return currentTime > expirationTime;
        }
        
        public long getTimeUntilExpiration() {
            long currentTime = System.currentTimeMillis();
            long expirationTime = this.timestamp + this.expirationTime;
            return Math.max(0, expirationTime - currentTime);
        }
    }
    
    /**
     * Network cache statistics
     */
    public static class NetworkCacheStats {
        public int totalEntries;
        public int validEntries;
        public int expiredEntries;
        public int memoryCacheSize;
        public long totalSizeBytes;
        
        public double getValidEntriesPercent() {
            return totalEntries > 0 ? (double) validEntries / totalEntries * 100 : 0;
        }
        
        public double getTotalSizeMB() {
            return totalSizeBytes / (1024.0 * 1024.0);
        }
        
        @Override
        public String toString() {
            return String.format("NetworkCacheStats{total=%d, valid=%d (%.1f%%), expired=%d, memory=%d, size=%.2f MB}",
                    totalEntries, validEntries, getValidEntriesPercent(), expiredEntries, memoryCacheSize, getTotalSizeMB());
        }
    }
}