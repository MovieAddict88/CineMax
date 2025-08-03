package my.cinemax.app.free.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import my.cinemax.app.free.entity.Episode;
import my.cinemax.app.free.entity.Poster;

public class SimpleCacheDatabase extends SQLiteOpenHelper {
    private static final String TAG = "SimpleCacheDatabase";
    private static final String DATABASE_NAME = "cinemax_cache.db";
    private static final int DATABASE_VERSION = 1;

    // Movies table
    private static final String TABLE_MOVIES = "cached_movies";
    private static final String MOVIE_ID = "id";
    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_OVERVIEW = "overview";
    private static final String MOVIE_POSTER = "poster_path";
    private static final String MOVIE_BACKDROP = "backdrop_path";
    private static final String MOVIE_GENRE_ID = "genre_id";
    private static final String MOVIE_RATING = "rating";
    private static final String MOVIE_TYPE = "type";
    private static final String MOVIE_EMBED_URL = "embed_url";
    private static final String MOVIE_TRAILER_URL = "trailer_url";
    private static final String MOVIE_DURATION = "duration";
    private static final String MOVIE_YEAR = "year";
    private static final String MOVIE_CLASSIFICATION = "classification";
    private static final String MOVIE_FEATURED = "featured";
    private static final String MOVIE_CREATED_AT = "created_at";
    private static final String MOVIE_CACHE_TIMESTAMP = "cache_timestamp";

    // Episodes table
    private static final String TABLE_EPISODES = "cached_episodes";
    private static final String EPISODE_ID = "id";
    private static final String EPISODE_TITLE = "title";
    private static final String EPISODE_NUMBER = "episode_number";
    private static final String EPISODE_SEASON_NUMBER = "season_number";
    private static final String EPISODE_SERIE_ID = "serie_id";
    private static final String EPISODE_OVERVIEW = "overview";
    private static final String EPISODE_STILL_PATH = "still_path";
    private static final String EPISODE_EMBED_URL = "embed_url";
    private static final String EPISODE_DURATION = "duration";
    private static final String EPISODE_CREATED_AT = "created_at";
    private static final String EPISODE_CACHE_TIMESTAMP = "cache_timestamp";

    // Cache validity (24 hours in milliseconds)
    private static final long CACHE_VALIDITY_PERIOD = 24 * 60 * 60 * 1000;

    public SimpleCacheDatabase(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create movies table
        String createMoviesTable = "CREATE TABLE " + TABLE_MOVIES + " (" +
                MOVIE_ID + " TEXT PRIMARY KEY, " +
                MOVIE_TITLE + " TEXT, " +
                MOVIE_OVERVIEW + " TEXT, " +
                MOVIE_POSTER + " TEXT, " +
                MOVIE_BACKDROP + " TEXT, " +
                MOVIE_GENRE_ID + " TEXT, " +
                MOVIE_RATING + " REAL, " +
                MOVIE_TYPE + " INTEGER, " +
                MOVIE_EMBED_URL + " TEXT, " +
                MOVIE_TRAILER_URL + " TEXT, " +
                MOVIE_DURATION + " TEXT, " +
                MOVIE_YEAR + " TEXT, " +
                MOVIE_CLASSIFICATION + " TEXT, " +
                MOVIE_FEATURED + " INTEGER, " +
                MOVIE_CREATED_AT + " TEXT, " +
                MOVIE_CACHE_TIMESTAMP + " INTEGER" +
                ")";

        // Create episodes table
        String createEpisodesTable = "CREATE TABLE " + TABLE_EPISODES + " (" +
                EPISODE_ID + " TEXT PRIMARY KEY, " +
                EPISODE_TITLE + " TEXT, " +
                EPISODE_NUMBER + " INTEGER, " +
                EPISODE_SEASON_NUMBER + " INTEGER, " +
                EPISODE_SERIE_ID + " TEXT, " +
                EPISODE_OVERVIEW + " TEXT, " +
                EPISODE_STILL_PATH + " TEXT, " +
                EPISODE_EMBED_URL + " TEXT, " +
                EPISODE_DURATION + " TEXT, " +
                EPISODE_CREATED_AT + " TEXT, " +
                EPISODE_CACHE_TIMESTAMP + " INTEGER" +
                ")";

        db.execSQL(createMoviesTable);
        db.execSQL(createEpisodesTable);

        Log.d(TAG, "Database created successfully");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EPISODES);
        onCreate(db);
    }

    // Movie operations
    public void insertMovie(Poster poster) {
        if (poster == null || poster.getId() == null) return;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(MOVIE_ID, poster.getId());
        values.put(MOVIE_TITLE, poster.getTitle());
        values.put(MOVIE_OVERVIEW, poster.getOverview());
        values.put(MOVIE_POSTER, poster.getImage());
        values.put(MOVIE_BACKDROP, poster.getCover());
        values.put(MOVIE_GENRE_ID, poster.getGenreId());
        values.put(MOVIE_RATING, poster.getRating());
        values.put(MOVIE_TYPE, poster.getType());
        values.put(MOVIE_EMBED_URL, poster.getEmbed());
        values.put(MOVIE_TRAILER_URL, poster.getTrailer());
        values.put(MOVIE_DURATION, poster.getDuration());
        values.put(MOVIE_YEAR, poster.getYear());
        values.put(MOVIE_CLASSIFICATION, poster.getClassification());
        values.put(MOVIE_FEATURED, poster.getFeatured());
        values.put(MOVIE_CREATED_AT, poster.getCreatedAt());
        values.put(MOVIE_CACHE_TIMESTAMP, System.currentTimeMillis());

        db.insertWithOnConflict(TABLE_MOVIES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        Log.d(TAG, "Cached movie: " + poster.getTitle());
    }

    public void insertMovies(List<Poster> posters) {
        if (posters == null || posters.isEmpty()) return;

        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            for (Poster poster : posters) {
                if (poster != null && poster.getId() != null) {
                    ContentValues values = new ContentValues();
                    values.put(MOVIE_ID, poster.getId());
                    values.put(MOVIE_TITLE, poster.getTitle());
                    values.put(MOVIE_OVERVIEW, poster.getOverview());
                    values.put(MOVIE_POSTER, poster.getImage());
                    values.put(MOVIE_BACKDROP, poster.getCover());
                    values.put(MOVIE_GENRE_ID, poster.getGenreId());
                    values.put(MOVIE_RATING, poster.getRating());
                    values.put(MOVIE_TYPE, poster.getType());
                    values.put(MOVIE_EMBED_URL, poster.getEmbed());
                    values.put(MOVIE_TRAILER_URL, poster.getTrailer());
                    values.put(MOVIE_DURATION, poster.getDuration());
                    values.put(MOVIE_YEAR, poster.getYear());
                    values.put(MOVIE_CLASSIFICATION, poster.getClassification());
                    values.put(MOVIE_FEATURED, poster.getFeatured());
                    values.put(MOVIE_CREATED_AT, poster.getCreatedAt());
                    values.put(MOVIE_CACHE_TIMESTAMP, System.currentTimeMillis());

                    db.insertWithOnConflict(TABLE_MOVIES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
                }
            }
            db.setTransactionSuccessful();
            Log.d(TAG, "Cached " + posters.size() + " movies");
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<Poster> getValidMovies(int type) {
        List<Poster> movies = new ArrayList<>();
        long validTimestamp = System.currentTimeMillis() - CACHE_VALIDITY_PERIOD;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_MOVIES + 
                      " WHERE " + MOVIE_TYPE + " = ? AND " + MOVIE_CACHE_TIMESTAMP + " > ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(type), String.valueOf(validTimestamp)});

        if (cursor.moveToFirst()) {
            do {
                Poster poster = new Poster();
                poster.setId(cursor.getString(cursor.getColumnIndex(MOVIE_ID)));
                poster.setTitle(cursor.getString(cursor.getColumnIndex(MOVIE_TITLE)));
                poster.setOverview(cursor.getString(cursor.getColumnIndex(MOVIE_OVERVIEW)));
                poster.setImage(cursor.getString(cursor.getColumnIndex(MOVIE_POSTER)));
                poster.setCover(cursor.getString(cursor.getColumnIndex(MOVIE_BACKDROP)));
                poster.setGenreId(cursor.getString(cursor.getColumnIndex(MOVIE_GENRE_ID)));
                poster.setRating(cursor.getDouble(cursor.getColumnIndex(MOVIE_RATING)));
                poster.setType(cursor.getInt(cursor.getColumnIndex(MOVIE_TYPE)));
                poster.setEmbed(cursor.getString(cursor.getColumnIndex(MOVIE_EMBED_URL)));
                poster.setTrailer(cursor.getString(cursor.getColumnIndex(MOVIE_TRAILER_URL)));
                poster.setDuration(cursor.getString(cursor.getColumnIndex(MOVIE_DURATION)));
                poster.setYear(cursor.getString(cursor.getColumnIndex(MOVIE_YEAR)));
                poster.setClassification(cursor.getString(cursor.getColumnIndex(MOVIE_CLASSIFICATION)));
                poster.setFeatured(cursor.getInt(cursor.getColumnIndex(MOVIE_FEATURED)));
                poster.setCreatedAt(cursor.getString(cursor.getColumnIndex(MOVIE_CREATED_AT)));
                
                movies.add(poster);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Log.d(TAG, "Retrieved " + movies.size() + " valid cached movies of type " + type);
        return movies;
    }

    public Poster getMovieById(String movieId) {
        if (movieId == null) return null;

        SQLiteDatabase db = this.getReadableDatabase();
        long validTimestamp = System.currentTimeMillis() - CACHE_VALIDITY_PERIOD;
        
        String query = "SELECT * FROM " + TABLE_MOVIES + 
                      " WHERE " + MOVIE_ID + " = ? AND " + MOVIE_CACHE_TIMESTAMP + " > ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{movieId, String.valueOf(validTimestamp)});
        
        Poster poster = null;
        if (cursor.moveToFirst()) {
            poster = new Poster();
            poster.setId(cursor.getString(cursor.getColumnIndex(MOVIE_ID)));
            poster.setTitle(cursor.getString(cursor.getColumnIndex(MOVIE_TITLE)));
            poster.setOverview(cursor.getString(cursor.getColumnIndex(MOVIE_OVERVIEW)));
            poster.setImage(cursor.getString(cursor.getColumnIndex(MOVIE_POSTER)));
            poster.setCover(cursor.getString(cursor.getColumnIndex(MOVIE_BACKDROP)));
            poster.setGenreId(cursor.getString(cursor.getColumnIndex(MOVIE_GENRE_ID)));
            poster.setRating(cursor.getDouble(cursor.getColumnIndex(MOVIE_RATING)));
            poster.setType(cursor.getInt(cursor.getColumnIndex(MOVIE_TYPE)));
            poster.setEmbed(cursor.getString(cursor.getColumnIndex(MOVIE_EMBED_URL)));
            poster.setTrailer(cursor.getString(cursor.getColumnIndex(MOVIE_TRAILER_URL)));
            poster.setDuration(cursor.getString(cursor.getColumnIndex(MOVIE_DURATION)));
            poster.setYear(cursor.getString(cursor.getColumnIndex(MOVIE_YEAR)));
            poster.setClassification(cursor.getString(cursor.getColumnIndex(MOVIE_CLASSIFICATION)));
            poster.setFeatured(cursor.getInt(cursor.getColumnIndex(MOVIE_FEATURED)));
            poster.setCreatedAt(cursor.getString(cursor.getColumnIndex(MOVIE_CREATED_AT)));
        }

        cursor.close();
        db.close();

        return poster;
    }

    // Episode operations
    public void insertEpisode(Episode episode, String serieId) {
        if (episode == null || episode.getId() == null) return;

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(EPISODE_ID, episode.getId());
        values.put(EPISODE_TITLE, episode.getTitle());
        values.put(EPISODE_NUMBER, episode.getEpisodeNumber());
        values.put(EPISODE_SEASON_NUMBER, episode.getSeasonNumber());
        values.put(EPISODE_SERIE_ID, serieId);
        values.put(EPISODE_OVERVIEW, episode.getOverview());
        values.put(EPISODE_STILL_PATH, episode.getStillPath());
        values.put(EPISODE_EMBED_URL, episode.getEmbed());
        values.put(EPISODE_DURATION, episode.getDuration());
        values.put(EPISODE_CREATED_AT, episode.getCreatedAt());
        values.put(EPISODE_CACHE_TIMESTAMP, System.currentTimeMillis());

        db.insertWithOnConflict(TABLE_EPISODES, null, values, SQLiteDatabase.CONFLICT_REPLACE);
        db.close();

        Log.d(TAG, "Cached episode: " + episode.getTitle());
    }

    public List<Episode> getValidEpisodes(String serieId) {
        List<Episode> episodes = new ArrayList<>();
        if (serieId == null) return episodes;

        long validTimestamp = System.currentTimeMillis() - CACHE_VALIDITY_PERIOD;

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM " + TABLE_EPISODES + 
                      " WHERE " + EPISODE_SERIE_ID + " = ? AND " + EPISODE_CACHE_TIMESTAMP + " > ?";
        
        Cursor cursor = db.rawQuery(query, new String[]{serieId, String.valueOf(validTimestamp)});

        if (cursor.moveToFirst()) {
            do {
                Episode episode = new Episode();
                episode.setId(cursor.getString(cursor.getColumnIndex(EPISODE_ID)));
                episode.setTitle(cursor.getString(cursor.getColumnIndex(EPISODE_TITLE)));
                episode.setEpisodeNumber(cursor.getInt(cursor.getColumnIndex(EPISODE_NUMBER)));
                episode.setSeasonNumber(cursor.getInt(cursor.getColumnIndex(EPISODE_SEASON_NUMBER)));
                episode.setOverview(cursor.getString(cursor.getColumnIndex(EPISODE_OVERVIEW)));
                episode.setStillPath(cursor.getString(cursor.getColumnIndex(EPISODE_STILL_PATH)));
                episode.setEmbed(cursor.getString(cursor.getColumnIndex(EPISODE_EMBED_URL)));
                episode.setDuration(cursor.getString(cursor.getColumnIndex(EPISODE_DURATION)));
                episode.setCreatedAt(cursor.getString(cursor.getColumnIndex(EPISODE_CREATED_AT)));
                
                episodes.add(episode);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        Log.d(TAG, "Retrieved " + episodes.size() + " valid cached episodes for serie " + serieId);
        return episodes;
    }

    // Cache management
    public void clearExpiredCache() {
        long expiredTimestamp = System.currentTimeMillis() - CACHE_VALIDITY_PERIOD;
        
        SQLiteDatabase db = this.getWritableDatabase();
        int deletedMovies = db.delete(TABLE_MOVIES, MOVIE_CACHE_TIMESTAMP + " < ?", 
                                     new String[]{String.valueOf(expiredTimestamp)});
        int deletedEpisodes = db.delete(TABLE_EPISODES, EPISODE_CACHE_TIMESTAMP + " < ?", 
                                       new String[]{String.valueOf(expiredTimestamp)});
        db.close();

        Log.d(TAG, "Cleared expired cache: " + deletedMovies + " movies, " + deletedEpisodes + " episodes");
    }

    public void clearAllCache() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_MOVIES, null, null);
        db.delete(TABLE_EPISODES, null, null);
        db.close();

        Log.d(TAG, "Cleared all cache");
    }

    public String getCacheStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        
        Cursor movieCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_MOVIES, null);
        movieCursor.moveToFirst();
        int movieCount = movieCursor.getInt(0);
        movieCursor.close();
        
        Cursor episodeCursor = db.rawQuery("SELECT COUNT(*) FROM " + TABLE_EPISODES, null);
        episodeCursor.moveToFirst();
        int episodeCount = episodeCursor.getInt(0);
        episodeCursor.close();
        
        db.close();

        return "Movies: " + movieCount + ", Episodes: " + episodeCount;
    }

    public boolean hasValidMovies(int type) {
        return !getValidMovies(type).isEmpty();
    }

    public boolean hasValidEpisodes(String serieId) {
        return !getValidEpisodes(serieId).isEmpty();
    }
}