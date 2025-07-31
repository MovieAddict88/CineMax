package my.cinemax.app.free.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import my.cinemax.app.free.database.entities.MovieEntity;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movies ORDER BY lastUpdated DESC")
    LiveData<List<MovieEntity>> getAllMovies();

    @Query("SELECT * FROM movies ORDER BY lastUpdated DESC")
    List<MovieEntity> getAllMoviesSync();

    @Query("SELECT * FROM movies WHERE type = 'movie' ORDER BY lastUpdated DESC")
    LiveData<List<MovieEntity>> getMoviesOnly();

    @Query("SELECT * FROM movies WHERE type = 'series' ORDER BY lastUpdated DESC")
    LiveData<List<MovieEntity>> getSeriesOnly();

    @Query("SELECT * FROM movies WHERE featured = 1 ORDER BY lastUpdated DESC")
    LiveData<List<MovieEntity>> getFeaturedMovies();

    @Query("SELECT * FROM movies WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    LiveData<List<MovieEntity>> searchMovies(String query);

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    LiveData<MovieEntity> getMovieById(int id);

    @Query("SELECT * FROM movies WHERE id = :id LIMIT 1")
    MovieEntity getMovieByIdSync(int id);

    @Query("SELECT * FROM movies WHERE genres LIKE '%' || :genre || '%' ORDER BY rating DESC")
    LiveData<List<MovieEntity>> getMoviesByGenre(String genre);

    @Query("SELECT * FROM movies ORDER BY rating DESC LIMIT :limit")
    LiveData<List<MovieEntity>> getTopRatedMovies(int limit);

    @Query("SELECT * FROM movies ORDER BY views DESC LIMIT :limit")
    LiveData<List<MovieEntity>> getMostWatchedMovies(int limit);

    @Query("SELECT * FROM movies WHERE year = :year ORDER BY rating DESC")
    LiveData<List<MovieEntity>> getMoviesByYear(String year);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(MovieEntity movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<MovieEntity> movies);

    @Update
    void updateMovie(MovieEntity movie);

    @Delete
    void deleteMovie(MovieEntity movie);

    @Query("DELETE FROM movies")
    void deleteAllMovies();

    @Query("DELETE FROM movies WHERE lastUpdated < :threshold")
    void deleteOldMovies(long threshold);

    @Query("SELECT COUNT(*) FROM movies")
    int getMovieCount();

    @Query("SELECT MAX(lastUpdated) FROM movies")
    long getLastUpdateTime();

    // Cache management
    @Query("UPDATE movies SET lastUpdated = :timestamp WHERE id = :id")
    void updateLastUpdated(int id, long timestamp);
}