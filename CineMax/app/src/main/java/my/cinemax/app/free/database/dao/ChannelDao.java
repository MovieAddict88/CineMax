package my.cinemax.app.free.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import my.cinemax.app.free.entity.Channel;
import java.util.List;

@Dao
public interface ChannelDao {
    
    @Query("SELECT * FROM channels ORDER BY id DESC")
    LiveData<List<Channel>> getAllChannels();
    
    @Query("SELECT * FROM channels WHERE id = :id")
    LiveData<Channel> getChannelById(int id);
    
    @Query("SELECT * FROM channels WHERE title LIKE '%' || :searchQuery || '%'")
    LiveData<List<Channel>> searchChannels(String searchQuery);
    
    @Query("SELECT COUNT(*) FROM channels")
    int getChannelsCount();
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChannel(Channel channel);
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChannels(List<Channel> channels);
    
    @Update
    void updateChannel(Channel channel);
    
    @Delete
    void deleteChannel(Channel channel);
    
    @Query("DELETE FROM channels")
    void deleteAllChannels();
}