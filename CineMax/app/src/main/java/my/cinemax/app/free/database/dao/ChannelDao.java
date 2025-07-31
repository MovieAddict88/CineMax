package my.cinemax.app.free.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import my.cinemax.app.free.database.entities.ChannelEntity;

@Dao
public interface ChannelDao {

    @Query("SELECT * FROM channels ORDER BY lastUpdated DESC")
    LiveData<List<ChannelEntity>> getAllChannels();

    @Query("SELECT * FROM channels ORDER BY lastUpdated DESC")
    List<ChannelEntity> getAllChannelsSync();

    @Query("SELECT * FROM channels WHERE featured = 1 ORDER BY lastUpdated DESC")
    LiveData<List<ChannelEntity>> getFeaturedChannels();

    @Query("SELECT * FROM channels WHERE name LIKE '%' || :query || '%' OR description LIKE '%' || :query || '%'")
    LiveData<List<ChannelEntity>> searchChannels(String query);

    @Query("SELECT * FROM channels WHERE id = :id LIMIT 1")
    LiveData<ChannelEntity> getChannelById(int id);

    @Query("SELECT * FROM channels WHERE id = :id LIMIT 1")
    ChannelEntity getChannelByIdSync(int id);

    @Query("SELECT * FROM channels WHERE categories LIKE '%' || :category || '%' ORDER BY rating DESC")
    LiveData<List<ChannelEntity>> getChannelsByCategory(String category);

    @Query("SELECT * FROM channels WHERE country = :country ORDER BY rating DESC")
    LiveData<List<ChannelEntity>> getChannelsByCountry(String country);

    @Query("SELECT * FROM channels WHERE language = :language ORDER BY rating DESC")
    LiveData<List<ChannelEntity>> getChannelsByLanguage(String language);

    @Query("SELECT * FROM channels ORDER BY rating DESC LIMIT :limit")
    LiveData<List<ChannelEntity>> getTopRatedChannels(int limit);

    @Query("SELECT * FROM channels ORDER BY views DESC LIMIT :limit")
    LiveData<List<ChannelEntity>> getMostWatchedChannels(int limit);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChannel(ChannelEntity channel);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChannels(List<ChannelEntity> channels);

    @Update
    void updateChannel(ChannelEntity channel);

    @Delete
    void deleteChannel(ChannelEntity channel);

    @Query("DELETE FROM channels")
    void deleteAllChannels();

    @Query("DELETE FROM channels WHERE lastUpdated < :threshold")
    void deleteOldChannels(long threshold);

    @Query("SELECT COUNT(*) FROM channels")
    int getChannelCount();

    @Query("SELECT MAX(lastUpdated) FROM channels")
    long getLastUpdateTime();

    // Cache management
    @Query("UPDATE channels SET lastUpdated = :timestamp WHERE id = :id")
    void updateLastUpdated(int id, long timestamp);
}