package my.cinemax.app.free.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "movies")
public class MovieEntity {
    
    @PrimaryKey
    private int id;
    private String title;
    private String description;
    private String poster;
    private String backdrop;
    private String releaseDate;
    private String runtime;
    private String rating;
    private String voteAverage;
    private String voteCount;
    private String popularity;
    private String status;
    private String type;
    private String trailer;
    private String videoUrl;
    private String subtitleUrl;
    private String genres;
    private String cast;
    private String director;
    private String country;
    private String language;
    private String budget;
    private String revenue;
    private String adult;
    private String video;
    private String originalTitle;
    private String originalLanguage;
    private String overview;
    private String tagline;
    private String homepage;
    private String imdbId;
    private String tmdbId;
    private String createdAt;
    private String updatedAt;
    
    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getPoster() { return poster; }
    public void setPoster(String poster) { this.poster = poster; }
    
    public String getBackdrop() { return backdrop; }
    public void setBackdrop(String backdrop) { this.backdrop = backdrop; }
    
    public String getReleaseDate() { return releaseDate; }
    public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
    
    public String getRuntime() { return runtime; }
    public void setRuntime(String runtime) { this.runtime = runtime; }
    
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    
    public String getVoteAverage() { return voteAverage; }
    public void setVoteAverage(String voteAverage) { this.voteAverage = voteAverage; }
    
    public String getVoteCount() { return voteCount; }
    public void setVoteCount(String voteCount) { this.voteCount = voteCount; }
    
    public String getPopularity() { return popularity; }
    public void setPopularity(String popularity) { this.popularity = popularity; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getTrailer() { return trailer; }
    public void setTrailer(String trailer) { this.trailer = trailer; }
    
    public String getVideoUrl() { return videoUrl; }
    public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
    
    public String getSubtitleUrl() { return subtitleUrl; }
    public void setSubtitleUrl(String subtitleUrl) { this.subtitleUrl = subtitleUrl; }
    
    public String getGenres() { return genres; }
    public void setGenres(String genres) { this.genres = genres; }
    
    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }
    
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }
    
    public String getRevenue() { return revenue; }
    public void setRevenue(String revenue) { this.revenue = revenue; }
    
    public String getAdult() { return adult; }
    public void setAdult(String adult) { this.adult = adult; }
    
    public String getVideo() { return video; }
    public void setVideo(String video) { this.video = video; }
    
    public String getOriginalTitle() { return originalTitle; }
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
    
    public String getOriginalLanguage() { return originalLanguage; }
    public void setOriginalLanguage(String originalLanguage) { this.originalLanguage = originalLanguage; }
    
    public String getOverview() { return overview; }
    public void setOverview(String overview) { this.overview = overview; }
    
    public String getTagline() { return tagline; }
    public void setTagline(String tagline) { this.tagline = tagline; }
    
    public String getHomepage() { return homepage; }
    public void setHomepage(String homepage) { this.homepage = homepage; }
    
    public String getImdbId() { return imdbId; }
    public void setImdbId(String imdbId) { this.imdbId = imdbId; }
    
    public String getTmdbId() { return tmdbId; }
    public void setTmdbId(String tmdbId) { this.tmdbId = tmdbId; }
    
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}