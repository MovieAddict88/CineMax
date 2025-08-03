package my.cinemax.app.free.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ApiCacheDao {

    @Query("SELECT * FROM api_cache WHERE `key` = :key LIMIT 1")
    ApiCache get(String key);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ApiCache cache);

    @Query("DELETE FROM api_cache WHERE `key` = :key")
    void delete(String key);

    @Query("DELETE FROM api_cache")
    void clear();
}