package my.cinemax.app.free.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_episodes")
public class CachedEpisode {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "episode_number")
    private int episodeNumber;

    @ColumnInfo(name = "season_number")
    private int seasonNumber;

    @ColumnInfo(name = "serie_id")
    private String serieId;

    @ColumnInfo(name = "overview")
    private String overview;

    @ColumnInfo(name = "still_path")
    private String stillPath;

    @ColumnInfo(name = "embed_url")
    private String embedUrl;

    @ColumnInfo(name = "duration")
    private String duration;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    @ColumnInfo(name = "cache_timestamp")
    private long cacheTimestamp;

    // Constructors
    public CachedEpisode() {
        this.cacheTimestamp = System.currentTimeMillis();
    }

    public CachedEpisode(@NonNull String id, String title, String serieId) {
        this.id = id;
        this.title = title;
        this.serieId = serieId;
        this.cacheTimestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(int episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public int getSeasonNumber() {
        return seasonNumber;
    }

    public void setSeasonNumber(int seasonNumber) {
        this.seasonNumber = seasonNumber;
    }

    public String getSerieId() {
        return serieId;
    }

    public void setSerieId(String serieId) {
        this.serieId = serieId;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getStillPath() {
        return stillPath;
    }

    public void setStillPath(String stillPath) {
        this.stillPath = stillPath;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }

    public void setEmbedUrl(String embedUrl) {
        this.embedUrl = embedUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public long getCacheTimestamp() {
        return cacheTimestamp;
    }

    public void setCacheTimestamp(long cacheTimestamp) {
        this.cacheTimestamp = cacheTimestamp;
    }

    // Helper method to check if cache is still valid (24 hours)
    public boolean isCacheValid() {
        long twentyFourHours = 24 * 60 * 60 * 1000; // 24 hours in milliseconds
        return (System.currentTimeMillis() - cacheTimestamp) < twentyFourHours;
    }
}