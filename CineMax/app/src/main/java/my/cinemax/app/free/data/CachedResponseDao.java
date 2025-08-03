package my.cinemax.app.free.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

@Dao
public interface CachedResponseDao {

    @Query("SELECT * FROM cached_responses WHERE `key` = :key LIMIT 1")
    CachedResponse getResponse(String key);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CachedResponse cachedResponse);

    @Query("DELETE FROM cached_responses")
    void clearAll();
}