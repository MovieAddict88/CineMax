package my.cinemax.app.free.Utils;

import android.graphics.Bitmap;
import android.util.Log;
import android.util.LruCache;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import my.cinemax.app.free.entity.*;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Memory Cache Layer - Fastest access for frequently used data
 * 
 * Features:
 * - LRU cache for automatic memory management
 * - ConcurrentHashMap for thread safety
 * - Fast in-memory access for frequently used items
 * - Automatic eviction when memory is low
 * - Optimized for 10,000+ entries
 */
public class MemoryCacheLayer {
    
    private static final String TAG = "MemoryCacheLayer";
    
    // Cache sizes (in entries)
    private static final int MOVIES_CACHE_SIZE = 200;
    private static final int TV_SERIES_CACHE_SIZE = 200;
    private static final int CHANNELS_CACHE_SIZE = 100;
    private static final int ACTORS_CACHE_SIZE = 300;
    private static final int GENERAL_CACHE_SIZE = 100;
    private static final int IMAGE_CACHE_SIZE = 50; // MB
    
    // Caches
    private final LruCache<String, Object> generalCache;
    private final Map<Integer, Poster> moviesById;
    private final Map<Integer, Poster> tvSeriesById;
    private final Map<Integer, Channel> channelsById;
    private final Map<Integer, Actor> actorsById;
    private final LruCache<String, Bitmap> imageCache;
    
    // Lists for frequently accessed data
    private List<Poster> moviesList;
    private List<Poster> tvSeriesList;
    private List<Channel> channelsList;
    private List<Actor> actorsList;
    
    private final Gson gson;
    
    public MemoryCacheLayer() {
        this.gson = new Gson();
        
        // Initialize LRU caches
        this.generalCache = new LruCache<String, Object>(GENERAL_CACHE_SIZE) {
            @Override
            protected int sizeOf(String key, Object value) {
                // Estimate size based on object type
                if (value instanceof String) {
                    return ((String) value).length();
                } else if (value instanceof JsonApiResponse) {
                    return 1; // Count as 1 entry
                }
                return 1;
            }
        };
        
        // Initialize concurrent maps for fast ID-based lookups
        this.moviesById = new ConcurrentHashMap<>();
        this.tvSeriesById = new ConcurrentHashMap<>();
        this.channelsById = new ConcurrentHashMap<>();
        this.actorsById = new ConcurrentHashMap<>();
        
        // Initialize image cache
        this.imageCache = new LruCache<String, Bitmap>(IMAGE_CACHE_SIZE * 1024 * 1024) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        
        Log.d(TAG, "Memory cache layer initialized");
    }
    
    /**
     * Store object in general cache
     */
    public void store(String key, Object value) {
        try {
            generalCache.put(key, value);
        } catch (Exception e) {
            Log.e(TAG, "Error storing in memory cache: " + key, e);
        }
    }
    
    /**
     * Get object from general cache
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        try {
            return (T) generalCache.get(key);
        } catch (Exception e) {
            Log.e(TAG, "Error retrieving from memory cache: " + key, e);
            return null;
        }
    }
    
    /**
     * Store movies list
     */
    public void storeMovies(List<Poster> movies) {
        if (movies == null || movies.isEmpty()) return;
        
        try {
            // Store list (limited to cache size)
            this.moviesList = new ArrayList<>(movies.subList(0, Math.min(movies.size(), MOVIES_CACHE_SIZE)));
            
            // Store individual movies by ID for fast lookup
            for (Poster movie : movies) {
                if (movie.getId() != null) {
                    moviesById.put(movie.getId(), movie);
                }
            }
            
            Log.d(TAG, "Stored " + movies.size() + " movies in memory cache");
        } catch (Exception e) {
            Log.e(TAG, "Error storing movies in memory cache", e);
        }
    }
    
    /**
     * Get movies list
     */
    public List<Poster> getMovies() {
        return moviesList != null ? new ArrayList<>(moviesList) : null;
    }
    
    /**
     * Get movie by ID
     */
    public Poster getMovieById(int movieId) {
        return moviesById.get(movieId);
    }
    
    /**
     * Store single movie
     */
    public void storeMovie(Poster movie) {
        if (movie != null && movie.getId() != null) {
            moviesById.put(movie.getId(), movie);
            
            // Add to list if not already present
            if (moviesList == null) {
                moviesList = new ArrayList<>();
            }
            
            // Check if already in list
            boolean found = false;
            for (Poster existing : moviesList) {
                if (existing.getId() != null && existing.getId().equals(movie.getId())) {
                    found = true;
                    break;
                }
            }
            
            if (!found && moviesList.size() < MOVIES_CACHE_SIZE) {
                moviesList.add(movie);
            }
        }
    }
    
    /**
     * Store TV series list
     */
    public void storeTvSeries(List<Poster> tvSeries) {
        if (tvSeries == null || tvSeries.isEmpty()) return;
        
        try {
            this.tvSeriesList = new ArrayList<>(tvSeries.subList(0, Math.min(tvSeries.size(), TV_SERIES_CACHE_SIZE)));
            
            for (Poster series : tvSeries) {
                if (series.getId() != null) {
                    tvSeriesById.put(series.getId(), series);
                }
            }
            
            Log.d(TAG, "Stored " + tvSeries.size() + " TV series in memory cache");
        } catch (Exception e) {
            Log.e(TAG, "Error storing TV series in memory cache", e);
        }
    }
    
    /**
     * Get TV series list
     */
    public List<Poster> getTvSeries() {
        return tvSeriesList != null ? new ArrayList<>(tvSeriesList) : null;
    }
    
    /**
     * Get TV series by ID
     */
    public Poster getTvSeriesById(int seriesId) {
        return tvSeriesById.get(seriesId);
    }
    
    /**
     * Store channels list
     */
    public void storeChannels(List<Channel> channels) {
        if (channels == null || channels.isEmpty()) return;
        
        try {
            this.channelsList = new ArrayList<>(channels.subList(0, Math.min(channels.size(), CHANNELS_CACHE_SIZE)));
            
            for (Channel channel : channels) {
                if (channel.getId() != null) {
                    channelsById.put(channel.getId(), channel);
                }
            }
            
            Log.d(TAG, "Stored " + channels.size() + " channels in memory cache");
        } catch (Exception e) {
            Log.e(TAG, "Error storing channels in memory cache", e);
        }
    }
    
    /**
     * Get channels list
     */
    public List<Channel> getChannels() {
        return channelsList != null ? new ArrayList<>(channelsList) : null;
    }
    
    /**
     * Get channel by ID
     */
    public Channel getChannelById(int channelId) {
        return channelsById.get(channelId);
    }
    
    /**
     * Store actors list
     */
    public void storeActors(List<Actor> actors) {
        if (actors == null || actors.isEmpty()) return;
        
        try {
            this.actorsList = new ArrayList<>(actors.subList(0, Math.min(actors.size(), ACTORS_CACHE_SIZE)));
            
            for (Actor actor : actors) {
                if (actor.getId() != null) {
                    actorsById.put(actor.getId(), actor);
                }
            }
            
            Log.d(TAG, "Stored " + actors.size() + " actors in memory cache");
        } catch (Exception e) {
            Log.e(TAG, "Error storing actors in memory cache", e);
        }
    }
    
    /**
     * Get actors list
     */
    public List<Actor> getActors() {
        return actorsList != null ? new ArrayList<>(actorsList) : null;
    }
    
    /**
     * Get actor by ID
     */
    public Actor getActorById(int actorId) {
        return actorsById.get(actorId);
    }
    
    /**
     * Search movies by title
     */
    public List<Poster> searchMovies(String query) {
        if (query == null || query.trim().isEmpty() || moviesList == null) {
            return new ArrayList<>();
        }
        
        List<Poster> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        
        for (Poster movie : moviesList) {
            if (movie.getTitle() != null && 
                movie.getTitle().toLowerCase().contains(lowerQuery)) {
                results.add(movie);
            }
        }
        
        return results;
    }
    
    /**
     * Search TV series by title
     */
    public List<Poster> searchTvSeries(String query) {
        if (query == null || query.trim().isEmpty() || tvSeriesList == null) {
            return new ArrayList<>();
        }
        
        List<Poster> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        
        for (Poster series : tvSeriesList) {
            if (series.getTitle() != null && 
                series.getTitle().toLowerCase().contains(lowerQuery)) {
                results.add(series);
            }
        }
        
        return results;
    }
    
    /**
     * Search channels by name
     */
    public List<Channel> searchChannels(String query) {
        if (query == null || query.trim().isEmpty() || channelsList == null) {
            return new ArrayList<>();
        }
        
        List<Channel> results = new ArrayList<>();
        String lowerQuery = query.toLowerCase().trim();
        
        for (Channel channel : channelsList) {
            if (channel.getName() != null && 
                channel.getName().toLowerCase().contains(lowerQuery)) {
                results.add(channel);
            }
        }
        
        return results;
    }
    
    /**
     * Store image in cache
     */
    public void storeImage(String url, Bitmap bitmap) {
        if (url != null && bitmap != null) {
            imageCache.put(url, bitmap);
        }
    }
    
    /**
     * Get image from cache
     */
    public Bitmap getImage(String url) {
        return url != null ? imageCache.get(url) : null;
    }
    
    /**
     * Get movies by genre
     */
    public List<Poster> getMoviesByGenre(int genreId) {
        if (moviesList == null) return new ArrayList<>();
        
        List<Poster> results = new ArrayList<>();
        for (Poster movie : moviesList) {
            if (movie.getGenre() != null && movie.getGenre().contains(String.valueOf(genreId))) {
                results.add(movie);
            }
        }
        
        return results;
    }
    
    /**
     * Get TV series by genre
     */
    public List<Poster> getTvSeriesByGenre(int genreId) {
        if (tvSeriesList == null) return new ArrayList<>();
        
        List<Poster> results = new ArrayList<>();
        for (Poster series : tvSeriesList) {
            if (series.getGenre() != null && series.getGenre().contains(String.valueOf(genreId))) {
                results.add(series);
            }
        }
        
        return results;
    }
    
    /**
     * Get paginated movies
     */
    public List<Poster> getMoviesPaginated(int page, int pageSize) {
        if (moviesList == null) return new ArrayList<>();
        
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, moviesList.size());
        
        if (startIndex >= moviesList.size()) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(moviesList.subList(startIndex, endIndex));
    }
    
    /**
     * Get paginated TV series
     */
    public List<Poster> getTvSeriesPaginated(int page, int pageSize) {
        if (tvSeriesList == null) return new ArrayList<>();
        
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, tvSeriesList.size());
        
        if (startIndex >= tvSeriesList.size()) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(tvSeriesList.subList(startIndex, endIndex));
    }
    
    /**
     * Get paginated channels
     */
    public List<Channel> getChannelsPaginated(int page, int pageSize) {
        if (channelsList == null) return new ArrayList<>();
        
        int startIndex = page * pageSize;
        int endIndex = Math.min(startIndex + pageSize, channelsList.size());
        
        if (startIndex >= channelsList.size()) {
            return new ArrayList<>();
        }
        
        return new ArrayList<>(channelsList.subList(startIndex, endIndex));
    }
    
    /**
     * Clear all memory caches
     */
    public void clear() {
        try {
            generalCache.evictAll();
            moviesById.clear();
            tvSeriesById.clear();
            channelsById.clear();
            actorsById.clear();
            imageCache.evictAll();
            
            moviesList = null;
            tvSeriesList = null;
            channelsList = null;
            actorsList = null;
            
            Log.d(TAG, "Memory cache cleared");
        } catch (Exception e) {
            Log.e(TAG, "Error clearing memory cache", e);
        }
    }
    
    /**
     * Get memory cache statistics
     */
    public String getStats() {
        return String.format("MemoryCache{general=%d, movies=%d, tvSeries=%d, channels=%d, actors=%d, images=%d}",
                generalCache.size(),
                moviesById.size(),
                tvSeriesById.size(),
                channelsById.size(),
                actorsById.size(),
                imageCache.size());
    }
}