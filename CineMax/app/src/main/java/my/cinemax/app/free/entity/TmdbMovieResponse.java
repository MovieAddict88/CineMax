package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TmdbMovieResponse {
    
    @SerializedName("id")
    @Expose
    private Integer id;
    
    @SerializedName("title")
    @Expose
    private String title;
    
    @SerializedName("overview")
    @Expose
    private String overview;
    
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    
    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;
    
    @SerializedName("popularity")
    @Expose
    private Double popularity;
    
    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    
    @SerializedName("runtime")
    @Expose
    private Integer runtime;
    
    @SerializedName("status")
    @Expose
    private String status;
    
    @SerializedName("imdb_id")
    @Expose
    private String imdbId;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
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
    
    public String getReleaseDate() {
        return releaseDate;
    }
    
    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }
    
    public Double getVoteAverage() {
        return voteAverage;
    }
    
    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }
    
    public Integer getVoteCount() {
        return voteCount;
    }
    
    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }
    
    public Double getPopularity() {
        return popularity;
    }
    
    public void setPopularity(Double popularity) {
        this.popularity = popularity;
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
    
    public Integer getRuntime() {
        return runtime;
    }
    
    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getImdbId() {
        return imdbId;
    }
    
    public void setImdbId(String imdbId) {
        this.imdbId = imdbId;
    }
}