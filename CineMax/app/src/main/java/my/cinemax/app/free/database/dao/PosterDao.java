package my.cinemax.app.free.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import my.cinemax.app.free.entity.Poster;
import java.util.List;

@Dao
public interface PosterDao {
    
    @Query("SELECT * FROM posters ORDER BY id DESC")
    LiveData<List<Poster>> getAllPosters();
    
    @Query("SELECT * FROM posters WHERE type = :type ORDER BY id DESC")
    LiveData<List<Poster>> getPostersByType(String type);
    
    @Query("SELECT * FROM posters WHERE id = :id")
    LiveData<Poster> getPosterById(int id);
    
    @Query("SELECT * FROM posters WHERE title LIKE '%' || :searchQuery || '%'")
    LiveData<List<Poster>> searchPosters(String searchQuery);
    
    @Query("SELECT COUNT(*) FROM posters")
    int getPostersCount();
    
    @Query("SELECT COUNT(*) FROM posters WHERE type = :type")
    int getPostersCountByType(String type);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPoster(Poster poster);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPosters(List<Poster> posters);
    
    @Update
    void updatePoster(Poster poster);
    
    @Delete
    void deletePoster(Poster poster);
    
    @Query("DELETE FROM posters")
    void deleteAllPosters();
    
    @Query("DELETE FROM posters WHERE type = :type")
    void deletePostersType(String type);
}