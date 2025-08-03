package my.cinemax.app.free.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import my.cinemax.app.free.database.entity.CachedChannel;

@Dao
public interface ChannelDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(CachedChannel channel);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(List<CachedChannel> channels);
    
    @Update
    void update(CachedChannel channel);
    
    @Delete
    void delete(CachedChannel channel);
    
    @Query("DELETE FROM cached_channels")
    void deleteAll();
    
    @Query("SELECT * FROM cached_channels WHERE id = :id")
    CachedChannel getById(int id);
    
    @Query("SELECT * FROM cached_channels WHERE id = :id")
    LiveData<CachedChannel> getByIdLive(int id);
    
    @Query("SELECT * FROM cached_channels ORDER BY lastUpdated DESC")
    List<CachedChannel> getAll();
    
    @Query("SELECT * FROM cached_channels ORDER BY lastUpdated DESC")
    LiveData<List<CachedChannel>> getAllLive();
    
    @Query("SELECT * FROM cached_channels WHERE category = :category ORDER BY lastUpdated DESC")
    List<CachedChannel> getByCategory(String category);
    
    @Query("SELECT * FROM cached_channels WHERE category = :category ORDER BY lastUpdated DESC")
    LiveData<List<CachedChannel>> getByCategoryLive(String category);
    
    @Query("SELECT * FROM cached_channels WHERE country = :country ORDER BY lastUpdated DESC")
    List<CachedChannel> getByCountry(String country);
    
    @Query("SELECT * FROM cached_channels WHERE country = :country ORDER BY lastUpdated DESC")
    LiveData<List<CachedChannel>> getByCountryLive(String country);
    
    @Query("SELECT * FROM cached_channels WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY lastUpdated DESC")
    List<CachedChannel> search(String query);
    
    @Query("SELECT * FROM cached_channels WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%' ORDER BY lastUpdated DESC")
    LiveData<List<CachedChannel>> searchLive(String query);
    
    @Query("SELECT * FROM cached_channels WHERE lastUpdated > :timestamp ORDER BY lastUpdated DESC")
    List<CachedChannel> getRecent(long timestamp);
    
    @Query("SELECT * FROM cached_channels WHERE lastUpdated > :timestamp ORDER BY lastUpdated DESC")
    LiveData<List<CachedChannel>> getRecentLive(long timestamp);
    
    @Query("SELECT COUNT(*) FROM cached_channels")
    int getCount();
    
    @Query("SELECT COUNT(*) FROM cached_channels WHERE category = :category")
    int getCountByCategory(String category);
    
    @Query("DELETE FROM cached_channels WHERE lastUpdated < :timestamp")
    void deleteOld(long timestamp);
}