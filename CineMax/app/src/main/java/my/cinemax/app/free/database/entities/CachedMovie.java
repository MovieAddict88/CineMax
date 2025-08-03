package my.cinemax.app.free.database.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_movies")
public class CachedMovie {
    @PrimaryKey
    @NonNull
    private String id;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "overview")
    private String overview;

    @ColumnInfo(name = "poster_path")
    private String posterPath;

    @ColumnInfo(name = "backdrop_path")
    private String backdropPath;

    @ColumnInfo(name = "release_date")
    private String releaseDate;

    @ColumnInfo(name = "genre_id")
    private String genreId;

    @ColumnInfo(name = "rating")
    private double rating;

    @ColumnInfo(name = "type")
    private int type; // 1 for movie, 2 for series

    @ColumnInfo(name = "embed_url")
    private String embedUrl;

    @ColumnInfo(name = "trailer_url")
    private String trailerUrl;

    @ColumnInfo(name = "duration")
    private String duration;

    @ColumnInfo(name = "year")
    private String year;

    @ColumnInfo(name = "classification")
    private String classification;

    @ColumnInfo(name = "featured")
    private int featured;

    @ColumnInfo(name = "created_at")
    private String createdAt;

    @ColumnInfo(name = "cache_timestamp")
    private long cacheTimestamp;

    // Constructors
    public CachedMovie() {
        this.cacheTimestamp = System.currentTimeMillis();
    }

    public CachedMovie(@NonNull String id, String title, String overview) {
        this.id = id;
        this.title = title;
        this.overview = overview;
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

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getGenreId() {
        return genreId;
    }

    public void setGenreId(String genreId) {
        this.genreId = genreId;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getEmbedUrl() {
        return embedUrl;
    }

    public void setEmbedUrl(String embedUrl) {
        this.embedUrl = embedUrl;
    }

    public String getTrailerUrl() {
        return trailerUrl;
    }

    public void setTrailerUrl(String trailerUrl) {
        this.trailerUrl = trailerUrl;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getClassification() {
        return classification;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public int getFeatured() {
        return featured;
    }

    public void setFeatured(int featured) {
        this.featured = featured;
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