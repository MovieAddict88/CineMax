package my.cinemax.app.free.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import my.cinemax.app.free.entity.Poster;

/**
 * Simplified SQLite database implementation for AndroidIDE compatibility
 * This replaces Room to avoid annotation processor issues
 */
public class SimpleCineMaxDatabase extends SQLiteOpenHelper {
    private static final String TAG = "SimpleCineMaxDatabase";
    private static final String DATABASE_NAME = "cinemax_simple.db";
    private static final int DATABASE_VERSION = 1;
    
    // Table names
    private static final String TABLE_MOVIES = "movies";
    
    // Movie table columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_TYPE = "type";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_RATING = "rating";
    private static final String COLUMN_IMAGE = "image";
    private static final String COLUMN_COVER = "cover";
    private static final String COLUMN_YEAR = "year";
    private static final String COLUMN_DURATION = "duration";
    private static final String COLUMN_GENRES = "genres";
    private static final String COLUMN_ACTORS = "actors";
    private static final String COLUMN_SOURCES = "sources";
    private static final String COLUMN_FEATURED = "featured";
    private static final String COLUMN_LAST_UPDATED = "last_updated";
    
    private static SimpleCineMaxDatabase instance;
    private Gson gson;
    
    private SimpleCineMaxDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        gson = new Gson();
    }
    
    public static synchronized SimpleCineMaxDatabase getInstance(Context context) {
        if (instance == null) {
            instance = new SimpleCineMaxDatabase(context.getApplicationContext());
        }
        return instance;
    }
    
    @Override
    public void onCreate(SQLiteDatabase db) {
        String createMoviesTable = "CREATE TABLE " + TABLE_MOVIES + " ("
                + COLUMN_ID + " INTEGER PRIMARY KEY, "
                + COLUMN_TITLE + " TEXT, "
                + COLUMN_TYPE + " TEXT, "
                + COLUMN_DESCRIPTION + " TEXT, "
                + COLUMN_RATING + " REAL, "
                + COLUMN_IMAGE + " TEXT, "
                + COLUMN_COVER + " TEXT, "
                + COLUMN_YEAR + " TEXT, "
                + COLUMN_DURATION + " TEXT, "
                + COLUMN_GENRES + " TEXT, "
                + COLUMN_ACTORS + " TEXT, "
                + COLUMN_SOURCES + " TEXT, "
                + COLUMN_FEATURED + " INTEGER DEFAULT 0, "
                + COLUMN_LAST_UPDATED + " INTEGER"
                + ")";
        
        db.execSQL(createMoviesTable);
        Log.d(TAG, "Database tables created successfully");
    }
    
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        onCreate(db);
    }
    
    // Insert or update movies
    public void insertMovies(List<Poster> posters) {
        SQLiteDatabase db = this.getWritableDatabase();
        long currentTime = System.currentTimeMillis();
        
        db.beginTransaction();
        try {
            for (Poster poster : posters) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_ID, poster.getId());
                values.put(COLUMN_TITLE, poster.getTitle());
                values.put(COLUMN_TYPE, poster.getType());
                values.put(COLUMN_DESCRIPTION, poster.getDescription());
                values.put(COLUMN_RATING, poster.getRating() != null ? poster.getRating() : 0.0f);
                values.put(COLUMN_IMAGE, poster.getImage());
                values.put(COLUMN_COVER, poster.getCover());
                values.put(COLUMN_YEAR, poster.getYear());
                values.put(COLUMN_DURATION, poster.getDuration());
                values.put(COLUMN_GENRES, gson.toJson(poster.getGenres()));
                values.put(COLUMN_ACTORS, gson.toJson(poster.getActors()));
                values.put(COLUMN_SOURCES, gson.toJson(poster.getSources()));
                values.put(COLUMN_FEATURED, poster.getFeatured() != null && poster.getFeatured() ? 1 : 0);
                values.put(COLUMN_LAST_UPDATED, currentTime);
                
                db.insertWithOnConflict(TABLE_MOVIES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "Inserted " + posters.size() + " movies successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error inserting movies: " + e.getMessage(), e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }
    
    // Get all movies
    public List<Poster> getAllMovies() {
        List<Poster> movies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_MOVIES, null, null, null, null, null, COLUMN_LAST_UPDATED + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Poster poster = cursorToPoster(cursor);
                if (poster != null) {
                    movies.add(poster);
                }
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        Log.d(TAG, "Retrieved " + movies.size() + " movies from database");
        return movies;
    }
    
    // Get movies by type
    public List<Poster> getMoviesByType(String type) {
        List<Poster> movies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_MOVIES, null, COLUMN_TYPE + "=?", new String[]{type}, null, null, COLUMN_LAST_UPDATED + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Poster poster = cursorToPoster(cursor);
                if (poster != null) {
                    movies.add(poster);
                }
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return movies;
    }
    
    // Get featured movies
    public List<Poster> getFeaturedMovies() {
        List<Poster> movies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor cursor = db.query(TABLE_MOVIES, null, COLUMN_FEATURED + "=1", null, null, null, COLUMN_LAST_UPDATED + " DESC");
        
        if (cursor.moveToFirst()) {
            do {
                Poster poster = cursorToPoster(cursor);
                if (poster != null) {
                    movies.add(poster);
                }
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return movies;
    }
    
    // Search movies
    public List<Poster> searchMovies(String query) {
        List<Poster> movies = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        
        String selection = COLUMN_TITLE + " LIKE ? OR " + COLUMN_DESCRIPTION + " LIKE ?";
        String[] selectionArgs = {"%" + query + "%", "%" + query + "%"};
        
        Cursor cursor = db.query(TABLE_MOVIES, null, selection, selectionArgs, null, null, COLUMN_TITLE + " ASC");
        
        if (cursor.moveToFirst()) {
            do {
                Poster poster = cursorToPoster(cursor);
                if (poster != null) {
                    movies.add(poster);
                }
            } while (cursor.moveToNext());
        }
        
        cursor.close();
        db.close();
        
        return movies;
    }
    
    // Get movie count
    public int getMovieCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MOVIES, null);
        
        int count = 0;
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        
        cursor.close();
        db.close();
        
        return count;
    }
    
    // Check if data needs refresh (older than 24 hours)
    public boolean needsRefresh() {
        SQLiteDatabase db = this.getReadableDatabase();
        long twentyFourHoursAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MOVIES + " WHERE " + COLUMN_LAST_UPDATED + " > ?", 
                new String[]{String.valueOf(twentyFourHoursAgo)});
        
        boolean needsRefresh = true;
        if (cursor.moveToFirst()) {
            needsRefresh = cursor.getInt(0) == 0;
        }
        
        cursor.close();
        db.close();
        
        return needsRefresh;
    }
    
    // Delete old data
    public void cleanOldData() {
        SQLiteDatabase db = this.getWritableDatabase();
        long fortyEightHoursAgo = System.currentTimeMillis() - (48 * 60 * 60 * 1000);
        
        int deletedRows = db.delete(TABLE_MOVIES, COLUMN_LAST_UPDATED + " < ?", new String[]{String.valueOf(fortyEightHoursAgo)});
        db.close();
        
        Log.d(TAG, "Cleaned " + deletedRows + " old records");
    }
    
    // Convert cursor to Poster object
    private Poster cursorToPoster(Cursor cursor) {
        try {
            Poster poster = new Poster();
            poster.setId(cursor.getInt(cursor.getColumnIndex(COLUMN_ID)));
            poster.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
            poster.setType(cursor.getString(cursor.getColumnIndex(COLUMN_TYPE)));
            poster.setDescription(cursor.getString(cursor.getColumnIndex(COLUMN_DESCRIPTION)));
            poster.setRating(cursor.getFloat(cursor.getColumnIndex(COLUMN_RATING)));
            poster.setImage(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE)));
            poster.setCover(cursor.getString(cursor.getColumnIndex(COLUMN_COVER)));
            poster.setYear(cursor.getString(cursor.getColumnIndex(COLUMN_YEAR)));
            poster.setDuration(cursor.getString(cursor.getColumnIndex(COLUMN_DURATION)));
            poster.setFeatured(cursor.getInt(cursor.getColumnIndex(COLUMN_FEATURED)) == 1);
            
            // Set empty lists for now (can be enhanced later)
            poster.setGenres(new ArrayList<>());
            poster.setActors(new ArrayList<>());
            poster.setSources(new ArrayList<>());
            poster.setSubtitles(new ArrayList<>());
            
            return poster;
        } catch (Exception e) {
            Log.e(TAG, "Error converting cursor to poster: " + e.getMessage(), e);
            return null;
        }
    }
}