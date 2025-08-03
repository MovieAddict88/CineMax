package my.cinemax.app.free.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface ApiCacheDao {

    @Query("SELECT * FROM api_cache WHERE `key` = :key LIMIT 1")
    ApiCacheEntity getCache(String key);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ApiCacheEntity cache);
}