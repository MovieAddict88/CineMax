package my.cinemax.app.free.Utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import my.cinemax.app.free.entity.*;

import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Database Layer - SQLite-based storage for complex queries and relationships
 * 
 * Features:
 * - SQLite database for structured data storage
 * - Complex queries and relationships
 * - Efficient indexing for fast searches
 * - Background database operations
 * - Transaction support for data integrity
 * - Optimized for 10,000+ entries
 */
public class DatabaseLayer {
    
    private static final String TAG = "DatabaseLayer";
    private static final String DATABASE_NAME = "cinemax_cache.db";
    private static final int DATABASE_VERSION = 1;
    
    private final Context context;
    private final DatabaseHelper databaseHelper;
    private final ExecutorService executorService;
    private final Gson gson;
    
    public DatabaseLayer(Context context) {
        this.context = context.getApplicationContext();
        this.databaseHelper = new DatabaseHelper(context);
        this.executorService = Executors.newFixedThreadPool(2);
        this.gson = new Gson();
        
        Log.d(TAG, "Database layer initialized");
    }
    
    /**
     * Store movies in database
     */
    public void storeMovies(List<Poster> movies) {
        if (movies == null || movies.isEmpty()) return;
        
        executorService.execute(() -> {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.beginTransaction();
            
            try {
                // Clear existing movies
                db.delete("movies", null, null);
                
                // Insert new movies
                for (Poster movie : movies) {
                    insertMovie(db, movie);
                }
                
                db.setTransactionSuccessful();
                Log.d(TAG, "Stored " + movies.size() + " movies in database");
                
            } catch (Exception e) {
                Log.e(TAG, "Error storing movies in database", e);
            } finally {
                db.endTransaction();
            }
        });
    }
    
    /**
     * Insert single movie
     */
    private void insertMovie(SQLiteDatabase db, Poster movie) {
        try {
            String sql = "INSERT INTO movies (id, title, description, poster, backdrop, rating, year, genre, duration, type, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            Object[] values = {
                movie.getId(),
                movie.getTitle(),
                movie.getDescription(),
                movie.getPoster(),
                movie.getBackdrop(),
                movie.getRating(),
                movie.getYear(),
                movie.getGenre(),
                movie.getDuration(),
                "movie",
                System.currentTimeMillis()
            };
            
            db.execSQL(sql, values);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting movie: " + movie.getTitle(), e);
        }
    }
    
    /**
     * Get all movies from database
     */
    public List<Poster> getAllMovies() {
        List<Poster> movies = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            String sql = "SELECT * FROM movies ORDER BY created_at DESC";
            Cursor cursor = db.rawQuery(sql, null);
            
            while (cursor.moveToNext()) {
                Poster movie = cursorToMovie(cursor);
                if (movie != null) {
                    movies.add(movie);
                }
            }
            
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting movies from database", e);
        }
        
        return movies;
    }
    
    /**
     * Get paginated movies
     */
    public List<Poster> getMoviesPaginated(int page, int pageSize) {
        List<Poster> movies = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            int offset = page * pageSize;
            String sql = "SELECT * FROM movies ORDER BY created_at DESC LIMIT ? OFFSET ?";
            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(pageSize), String.valueOf(offset)});
            
            while (cursor.moveToNext()) {
                Poster movie = cursorToMovie(cursor);
                if (movie != null) {
                    movies.add(movie);
                }
            }
            
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting paginated movies from database", e);
        }
        
        return movies;
    }
    
    /**
     * Get movie by ID
     */
    public Poster getMovieById(int movieId) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            String sql = "SELECT * FROM movies WHERE id = ?";
            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(movieId)});
            
            if (cursor.moveToFirst()) {
                Poster movie = cursorToMovie(cursor);
                cursor.close();
                return movie;
            }
            
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting movie by ID: " + movieId, e);
        }
        
        return null;
    }
    
    /**
     * Search movies by title
     */
    public List<Poster> searchMovies(String query) {
        List<Poster> movies = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            String searchQuery = "%" + query.toLowerCase() + "%";
            String sql = "SELECT * FROM movies WHERE LOWER(title) LIKE ? ORDER BY rating DESC";
            Cursor cursor = db.rawQuery(sql, new String[]{searchQuery});
            
            while (cursor.moveToNext()) {
                Poster movie = cursorToMovie(cursor);
                if (movie != null) {
                    movies.add(movie);
                }
            }
            
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error searching movies in database", e);
        }
        
        return movies;
    }
    
    /**
     * Get movies by genre
     */
    public List<Poster> getMoviesByGenre(int genreId) {
        List<Poster> movies = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            String genreStr = "%" + genreId + "%";
            String sql = "SELECT * FROM movies WHERE genre LIKE ? ORDER BY rating DESC";
            Cursor cursor = db.rawQuery(sql, new String[]{genreStr});
            
            while (cursor.moveToNext()) {
                Poster movie = cursorToMovie(cursor);
                if (movie != null) {
                    movies.add(movie);
                }
            }
            
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting movies by genre from database", e);
        }
        
        return movies;
    }
    
    /**
     * Convert cursor to movie object
     */
    private Poster cursorToMovie(Cursor cursor) {
        try {
            Poster movie = new Poster();
            movie.setId(cursor.getInt(cursor.getColumnIndex("id")));
            movie.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            movie.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            movie.setPoster(cursor.getString(cursor.getColumnIndex("poster")));
            movie.setBackdrop(cursor.getString(cursor.getColumnIndex("backdrop")));
            movie.setRating(cursor.getString(cursor.getColumnIndex("rating")));
            movie.setYear(cursor.getString(cursor.getColumnIndex("year")));
            movie.setGenre(cursor.getString(cursor.getColumnIndex("genre")));
            movie.setDuration(cursor.getString(cursor.getColumnIndex("duration")));
            return movie;
        } catch (Exception e) {
            Log.e(TAG, "Error converting cursor to movie", e);
            return null;
        }
    }
    
    /**
     * Store TV series in database
     */
    public void storeTvSeries(List<Poster> tvSeries) {
        if (tvSeries == null || tvSeries.isEmpty()) return;
        
        executorService.execute(() -> {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.beginTransaction();
            
            try {
                // Clear existing TV series
                db.delete("tv_series", null, null);
                
                // Insert new TV series
                for (Poster series : tvSeries) {
                    insertTvSeries(db, series);
                }
                
                db.setTransactionSuccessful();
                Log.d(TAG, "Stored " + tvSeries.size() + " TV series in database");
                
            } catch (Exception e) {
                Log.e(TAG, "Error storing TV series in database", e);
            } finally {
                db.endTransaction();
            }
        });
    }
    
    /**
     * Insert single TV series
     */
    private void insertTvSeries(SQLiteDatabase db, Poster series) {
        try {
            String sql = "INSERT INTO tv_series (id, title, description, poster, backdrop, rating, year, genre, duration, type, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            
            Object[] values = {
                series.getId(),
                series.getTitle(),
                series.getDescription(),
                series.getPoster(),
                series.getBackdrop(),
                series.getRating(),
                series.getYear(),
                series.getGenre(),
                series.getDuration(),
                "tv_series",
                System.currentTimeMillis()
            };
            
            db.execSQL(sql, values);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting TV series: " + series.getTitle(), e);
        }
    }
    
    /**
     * Get all TV series from database
     */
    public List<Poster> getAllTvSeries() {
        List<Poster> tvSeries = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            String sql = "SELECT * FROM tv_series ORDER BY created_at DESC";
            Cursor cursor = db.rawQuery(sql, null);
            
            while (cursor.moveToNext()) {
                Poster series = cursorToTvSeries(cursor);
                if (series != null) {
                    tvSeries.add(series);
                }
            }
            
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting TV series from database", e);
        }
        
        return tvSeries;
    }
    
    /**
     * Get paginated TV series
     */
    public List<Poster> getTvSeriesPaginated(int page, int pageSize) {
        List<Poster> tvSeries = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            int offset = page * pageSize;
            String sql = "SELECT * FROM tv_series ORDER BY created_at DESC LIMIT ? OFFSET ?";
            Cursor cursor = db.rawQuery(sql, new String[]{String.valueOf(pageSize), String.valueOf(offset)});
            
            while (cursor.moveToNext()) {
                Poster series = cursorToTvSeries(cursor);
                if (series != null) {
                    tvSeries.add(series);
                }
            }
            
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting paginated TV series from database", e);
        }
        
        return tvSeries;
    }
    
    /**
     * Convert cursor to TV series object
     */
    private Poster cursorToTvSeries(Cursor cursor) {
        try {
            Poster series = new Poster();
            series.setId(cursor.getInt(cursor.getColumnIndex("id")));
            series.setTitle(cursor.getString(cursor.getColumnIndex("title")));
            series.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            series.setPoster(cursor.getString(cursor.getColumnIndex("poster")));
            series.setBackdrop(cursor.getString(cursor.getColumnIndex("backdrop")));
            series.setRating(cursor.getString(cursor.getColumnIndex("rating")));
            series.setYear(cursor.getString(cursor.getColumnIndex("year")));
            series.setGenre(cursor.getString(cursor.getColumnIndex("genre")));
            series.setDuration(cursor.getString(cursor.getColumnIndex("duration")));
            return series;
        } catch (Exception e) {
            Log.e(TAG, "Error converting cursor to TV series", e);
            return null;
        }
    }
    
    /**
     * Store channels in database
     */
    public void storeChannels(List<Channel> channels) {
        if (channels == null || channels.isEmpty()) return;
        
        executorService.execute(() -> {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.beginTransaction();
            
            try {
                // Clear existing channels
                db.delete("channels", null, null);
                
                // Insert new channels
                for (Channel channel : channels) {
                    insertChannel(db, channel);
                }
                
                db.setTransactionSuccessful();
                Log.d(TAG, "Stored " + channels.size() + " channels in database");
                
            } catch (Exception e) {
                Log.e(TAG, "Error storing channels in database", e);
            } finally {
                db.endTransaction();
            }
        });
    }
    
    /**
     * Insert single channel
     */
    private void insertChannel(SQLiteDatabase db, Channel channel) {
        try {
            String sql = "INSERT INTO channels (id, name, description, poster, stream_url, category, created_at) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?)";
            
            Object[] values = {
                channel.getId(),
                channel.getName(),
                channel.getDescription(),
                channel.getPoster(),
                channel.getStreamUrl(),
                channel.getCategory(),
                System.currentTimeMillis()
            };
            
            db.execSQL(sql, values);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting channel: " + channel.getName(), e);
        }
    }
    
    /**
     * Get all channels from database
     */
    public List<Channel> getAllChannels() {
        List<Channel> channels = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            String sql = "SELECT * FROM channels ORDER BY name ASC";
            Cursor cursor = db.rawQuery(sql, null);
            
            while (cursor.moveToNext()) {
                Channel channel = cursorToChannel(cursor);
                if (channel != null) {
                    channels.add(channel);
                }
            }
            
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting channels from database", e);
        }
        
        return channels;
    }
    
    /**
     * Convert cursor to channel object
     */
    private Channel cursorToChannel(Cursor cursor) {
        try {
            Channel channel = new Channel();
            channel.setId(cursor.getInt(cursor.getColumnIndex("id")));
            channel.setName(cursor.getString(cursor.getColumnIndex("name")));
            channel.setDescription(cursor.getString(cursor.getColumnIndex("description")));
            channel.setPoster(cursor.getString(cursor.getColumnIndex("poster")));
            channel.setStreamUrl(cursor.getString(cursor.getColumnIndex("stream_url")));
            channel.setCategory(cursor.getString(cursor.getColumnIndex("category")));
            return channel;
        } catch (Exception e) {
            Log.e(TAG, "Error converting cursor to channel", e);
            return null;
        }
    }
    
    /**
     * Store actors in database
     */
    public void storeActors(List<Actor> actors) {
        if (actors == null || actors.isEmpty()) return;
        
        executorService.execute(() -> {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            db.beginTransaction();
            
            try {
                // Clear existing actors
                db.delete("actors", null, null);
                
                // Insert new actors
                for (Actor actor : actors) {
                    insertActor(db, actor);
                }
                
                db.setTransactionSuccessful();
                Log.d(TAG, "Stored " + actors.size() + " actors in database");
                
            } catch (Exception e) {
                Log.e(TAG, "Error storing actors in database", e);
            } finally {
                db.endTransaction();
            }
        });
    }
    
    /**
     * Insert single actor
     */
    private void insertActor(SQLiteDatabase db, Actor actor) {
        try {
            String sql = "INSERT INTO actors (id, name, biography, photo, created_at) " +
                        "VALUES (?, ?, ?, ?, ?)";
            
            Object[] values = {
                actor.getId(),
                actor.getName(),
                actor.getBiography(),
                actor.getPhoto(),
                System.currentTimeMillis()
            };
            
            db.execSQL(sql, values);
        } catch (Exception e) {
            Log.e(TAG, "Error inserting actor: " + actor.getName(), e);
        }
    }
    
    /**
     * Get all actors from database
     */
    public List<Actor> getAllActors() {
        List<Actor> actors = new ArrayList<>();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            String sql = "SELECT * FROM actors ORDER BY name ASC";
            Cursor cursor = db.rawQuery(sql, null);
            
            while (cursor.moveToNext()) {
                Actor actor = cursorToActor(cursor);
                if (actor != null) {
                    actors.add(actor);
                }
            }
            
            cursor.close();
        } catch (Exception e) {
            Log.e(TAG, "Error getting actors from database", e);
        }
        
        return actors;
    }
    
    /**
     * Convert cursor to actor object
     */
    private Actor cursorToActor(Cursor cursor) {
        try {
            Actor actor = new Actor();
            actor.setId(cursor.getInt(cursor.getColumnIndex("id")));
            actor.setName(cursor.getString(cursor.getColumnIndex("name")));
            actor.setBiography(cursor.getString(cursor.getColumnIndex("biography")));
            actor.setPhoto(cursor.getString(cursor.getColumnIndex("photo")));
            return actor;
        } catch (Exception e) {
            Log.e(TAG, "Error converting cursor to actor", e);
            return null;
        }
    }
    
    /**
     * Get database statistics
     */
    public DatabaseStats getStats() {
        DatabaseStats stats = new DatabaseStats();
        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        
        try {
            // Count movies
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM movies", null);
            if (cursor.moveToFirst()) {
                stats.moviesCount = cursor.getInt(0);
            }
            cursor.close();
            
            // Count TV series
            cursor = db.rawQuery("SELECT COUNT(*) FROM tv_series", null);
            if (cursor.moveToFirst()) {
                stats.tvSeriesCount = cursor.getInt(0);
            }
            cursor.close();
            
            // Count channels
            cursor = db.rawQuery("SELECT COUNT(*) FROM channels", null);
            if (cursor.moveToFirst()) {
                stats.channelsCount = cursor.getInt(0);
            }
            cursor.close();
            
            // Count actors
            cursor = db.rawQuery("SELECT COUNT(*) FROM actors", null);
            if (cursor.moveToFirst()) {
                stats.actorsCount = cursor.getInt(0);
            }
            cursor.close();
            
            // Get database size
            File dbFile = context.getDatabasePath(DATABASE_NAME);
            if (dbFile.exists()) {
                stats.databaseSizeBytes = dbFile.length();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting database stats", e);
        }
        
        return stats;
    }
    
    /**
     * Clear all database data
     */
    public void clear() {
        executorService.execute(() -> {
            SQLiteDatabase db = databaseHelper.getWritableDatabase();
            
            try {
                db.delete("movies", null, null);
                db.delete("tv_series", null, null);
                db.delete("channels", null, null);
                db.delete("actors", null, null);
                
                Log.d(TAG, "Database cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing database", e);
            }
        });
    }
    
    /**
     * Shutdown database layer
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
    
    /**
     * Database helper class
     */
    private static class DatabaseHelper extends SQLiteOpenHelper {
        
        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }
        
        @Override
        public void onCreate(SQLiteDatabase db) {
            // Create movies table
            db.execSQL("CREATE TABLE movies (" +
                    "id INTEGER PRIMARY KEY, " +
                    "title TEXT, " +
                    "description TEXT, " +
                    "poster TEXT, " +
                    "backdrop TEXT, " +
                    "rating TEXT, " +
                    "year TEXT, " +
                    "genre TEXT, " +
                    "duration TEXT, " +
                    "type TEXT, " +
                    "created_at INTEGER" +
                    ")");
            
            // Create TV series table
            db.execSQL("CREATE TABLE tv_series (" +
                    "id INTEGER PRIMARY KEY, " +
                    "title TEXT, " +
                    "description TEXT, " +
                    "poster TEXT, " +
                    "backdrop TEXT, " +
                    "rating TEXT, " +
                    "year TEXT, " +
                    "genre TEXT, " +
                    "duration TEXT, " +
                    "type TEXT, " +
                    "created_at INTEGER" +
                    ")");
            
            // Create channels table
            db.execSQL("CREATE TABLE channels (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "description TEXT, " +
                    "poster TEXT, " +
                    "stream_url TEXT, " +
                    "category TEXT, " +
                    "created_at INTEGER" +
                    ")");
            
            // Create actors table
            db.execSQL("CREATE TABLE actors (" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "biography TEXT, " +
                    "photo TEXT, " +
                    "created_at INTEGER" +
                    ")");
            
            // Create indexes for better performance
            db.execSQL("CREATE INDEX idx_movies_title ON movies(title)");
            db.execSQL("CREATE INDEX idx_movies_genre ON movies(genre)");
            db.execSQL("CREATE INDEX idx_movies_rating ON movies(rating)");
            db.execSQL("CREATE INDEX idx_tv_series_title ON tv_series(title)");
            db.execSQL("CREATE INDEX idx_tv_series_genre ON tv_series(genre)");
            db.execSQL("CREATE INDEX idx_channels_name ON channels(name)");
            db.execSQL("CREATE INDEX idx_actors_name ON actors(name)");
            
            Log.d(TAG, "Database created successfully");
        }
        
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Drop existing tables
            db.execSQL("DROP TABLE IF EXISTS movies");
            db.execSQL("DROP TABLE IF EXISTS tv_series");
            db.execSQL("DROP TABLE IF EXISTS channels");
            db.execSQL("DROP TABLE IF EXISTS actors");
            
            // Recreate tables
            onCreate(db);
        }
    }
    
    /**
     * Database statistics
     */
    public static class DatabaseStats {
        public int moviesCount;
        public int tvSeriesCount;
        public int channelsCount;
        public int actorsCount;
        public long databaseSizeBytes;
        
        public int getTotalItems() {
            return moviesCount + tvSeriesCount + channelsCount + actorsCount;
        }
        
        public double getDatabaseSizeMB() {
            return databaseSizeBytes / (1024.0 * 1024.0);
        }
        
        @Override
        public String toString() {
            return String.format("DatabaseStats{movies=%d, tvSeries=%d, channels=%d, actors=%d, total=%d, size=%.2f MB}",
                    moviesCount, tvSeriesCount, channelsCount, actorsCount, getTotalItems(), getDatabaseSizeMB());
        }
    }
}