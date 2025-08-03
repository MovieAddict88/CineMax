package my.cinemax.app.free.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import my.cinemax.app.free.entity.Genre;
import java.util.List;

@Dao
public interface GenreDao {
    
    @Query("SELECT * FROM genres ORDER BY id ASC")
    LiveData<List<Genre>> getAllGenres();
    
    @Query("SELECT * FROM genres WHERE id = :id")
    LiveData<Genre> getGenreById(int id);
    
    @Query("SELECT COUNT(*) FROM genres")
    int getGenresCount();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGenre(Genre genre);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGenres(List<Genre> genres);
    
    @Update
    void updateGenre(Genre genre);
    
    @Delete
    void deleteGenre(Genre genre);
    
    @Query("DELETE FROM genres")
    void deleteAllGenres();
}