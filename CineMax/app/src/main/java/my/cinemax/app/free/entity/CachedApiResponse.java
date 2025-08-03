package my.cinemax.app.free.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import my.cinemax.app.free.database.Converters;
import java.util.List;

@Entity(tableName = "cached_api_response")
@TypeConverters(Converters.class)
public class CachedApiResponse {
    @PrimaryKey
    private int id = 1; // Single row for cached data
    
    private String moviesJson;
    private String seriesJson;
    private String channelsJson;
    private String homeJson;
    private long lastUpdated;
    
    public CachedApiResponse() {
        this.lastUpdated = System.currentTimeMillis();
    }
    
    // Getters and setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getMoviesJson() {
        return moviesJson;
    }
    
    public void setMoviesJson(String moviesJson) {
        this.moviesJson = moviesJson;
    }
    
    public String getSeriesJson() {
        return seriesJson;
    }
    
    public void setSeriesJson(String seriesJson) {
        this.seriesJson = seriesJson;
    }
    
    public String getChannelsJson() {
        return channelsJson;
    }
    
    public void setChannelsJson(String channelsJson) {
        this.channelsJson = channelsJson;
    }
    
    public String getHomeJson() {
        return homeJson;
    }
    
    public void setHomeJson(String homeJson) {
        this.homeJson = homeJson;
    }
    
    public long getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public boolean isExpired(long cacheExpirationTime) {
        return System.currentTimeMillis() - lastUpdated > cacheExpirationTime;
    }
}