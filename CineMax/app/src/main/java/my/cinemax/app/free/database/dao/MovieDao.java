package my.cinemax.app.free.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import my.cinemax.app.free.database.entities.CachedMovie;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM cached_movies")
    List<CachedMovie> getAllMovies();

    @Query("SELECT * FROM cached_movies WHERE type = :type")
    List<CachedMovie> getMoviesByType(int type);

    @Query("SELECT * FROM cached_movies WHERE id = :movieId")
    CachedMovie getMovieById(String movieId);

    @Query("SELECT * FROM cached_movies WHERE genre_id = :genreId")
    List<CachedMovie> getMoviesByGenre(String genreId);

    @Query("SELECT * FROM cached_movies WHERE featured = 1")
    List<CachedMovie> getFeaturedMovies();

    @Query("SELECT * FROM cached_movies WHERE title LIKE :searchQuery")
    List<CachedMovie> searchMovies(String searchQuery);

    @Query("SELECT * FROM cached_movies WHERE cache_timestamp > :timestampThreshold")
    List<CachedMovie> getValidCachedMovies(long timestampThreshold);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovie(CachedMovie movie);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMovies(List<CachedMovie> movies);

    @Update
    void updateMovie(CachedMovie movie);

    @Delete
    void deleteMovie(CachedMovie movie);

    @Query("DELETE FROM cached_movies WHERE id = :movieId")
    void deleteMovieById(String movieId);

    @Query("DELETE FROM cached_movies")
    void deleteAllMovies();

    @Query("DELETE FROM cached_movies WHERE cache_timestamp < :expiredTimestamp")
    void deleteExpiredMovies(long expiredTimestamp);

    @Query("SELECT COUNT(*) FROM cached_movies")
    int getMovieCount();

    @Query("SELECT COUNT(*) FROM cached_movies WHERE type = :type")
    int getMovieCountByType(int type);
}