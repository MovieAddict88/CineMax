package my.cinemax.app.free.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import my.cinemax.app.free.database.entity.CachedPoster;

@Dao
public interface PosterDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CachedPoster poster);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CachedPoster> posters);
    
    @Update
    void update(CachedPoster poster);
    
    @Delete
    void delete(CachedPoster poster);
    
    @Query("DELETE FROM cached_posters")
    void deleteAll();
    
    @Query("SELECT * FROM cached_posters WHERE id = :id")
    CachedPoster getById(int id);
    
    @Query("SELECT * FROM cached_posters WHERE id = :id")
    LiveData<CachedPoster> getByIdLive(int id);
    
    @Query("SELECT * FROM cached_posters ORDER BY lastUpdated DESC")
    List<CachedPoster> getAll();
    
    @Query("SELECT * FROM cached_posters ORDER BY lastUpdated DESC")
    LiveData<List<CachedPoster>> getAllLive();
    
    @Query("SELECT * FROM cached_posters WHERE type = :type ORDER BY lastUpdated DESC")
    List<CachedPoster> getByType(String type);
    
    @Query("SELECT * FROM cached_posters WHERE type = :type ORDER BY lastUpdated DESC")
    LiveData<List<CachedPoster>> getByTypeLive(String type);
    
    @Query("SELECT * FROM cached_posters WHERE genre LIKE '%' || :genre || '%' ORDER BY lastUpdated DESC")
    List<CachedPoster> getByGenre(String genre);
    
    @Query("SELECT * FROM cached_posters WHERE genre LIKE '%' || :genre || '%' ORDER BY lastUpdated DESC")
    LiveData<List<CachedPoster>> getByGenreLive(String genre);
    
    @Query("SELECT * FROM cached_posters WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY lastUpdated DESC")
    List<CachedPoster> search(String query);
    
    @Query("SELECT * FROM cached_posters WHERE title LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY lastUpdated DESC")
    LiveData<List<CachedPoster>> searchLive(String query);
    
    @Query("SELECT * FROM cached_posters WHERE lastUpdated > :timestamp ORDER BY lastUpdated DESC")
    List<CachedPoster> getRecent(long timestamp);
    
    @Query("SELECT * FROM cached_posters WHERE lastUpdated > :timestamp ORDER BY lastUpdated DESC")
    LiveData<List<CachedPoster>> getRecentLive(long timestamp);
    
    @Query("SELECT COUNT(*) FROM cached_posters")
    int getCount();
    
    @Query("SELECT COUNT(*) FROM cached_posters WHERE type = :type")
    int getCountByType(String type);
    
    @Query("DELETE FROM cached_posters WHERE lastUpdated < :timestamp")
    void deleteOld(long timestamp);
}