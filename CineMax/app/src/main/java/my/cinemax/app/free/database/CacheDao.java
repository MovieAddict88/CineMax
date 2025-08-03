package my.cinemax.app.free.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import my.cinemax.app.free.entity.CachedApiResponse;

@Dao
public interface CacheDao {
    
    @Query("SELECT * FROM cached_api_response WHERE id = 1")
    CachedApiResponse getCachedData();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertCachedData(CachedApiResponse cachedData);
    
    @Update
    void updateCachedData(CachedApiResponse cachedData);
    
    @Query("DELETE FROM cached_api_response")
    void clearCache();
    
    @Query("SELECT COUNT(*) FROM cached_api_response")
    int getCacheCount();
}