package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TmdbTvResponse {
    
    @SerializedName("id")
    @Expose
    private Integer id;
    
    @SerializedName("name")
    @Expose
    private String name;
    
    @SerializedName("overview")
    @Expose
    private String overview;
    
    @SerializedName("first_air_date")
    @Expose
    private String firstAirDate;
    
    @SerializedName("last_air_date")
    @Expose
    private String lastAirDate;
    
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
    
    @SerializedName("status")
    @Expose
    private String status;
    
    @SerializedName("number_of_seasons")
    @Expose
    private Integer numberOfSeasons;
    
    @SerializedName("number_of_episodes")
    @Expose
    private Integer numberOfEpisodes;
    
    @SerializedName("seasons")
    @Expose
    private List<TmdbSeason> seasons;
    
    @SerializedName("external_ids")
    @Expose
    private TmdbExternalIds externalIds;
    
    // Getters and Setters
    public Integer getId() {
        return id;
    }
    
    public void setId(Integer id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getOverview() {
        return overview;
    }
    
    public void setOverview(String overview) {
        this.overview = overview;
    }
    
    public String getFirstAirDate() {
        return firstAirDate;
    }
    
    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }
    
    public String getLastAirDate() {
        return lastAirDate;
    }
    
    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
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
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }
    
    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }
    
    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }
    
    public void setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }
    
    public List<TmdbSeason> getSeasons() {
        return seasons;
    }
    
    public void setSeasons(List<TmdbSeason> seasons) {
        this.seasons = seasons;
    }
    
    public TmdbExternalIds getExternalIds() {
        return externalIds;
    }
    
    public void setExternalIds(TmdbExternalIds externalIds) {
        this.externalIds = externalIds;
    }
    
    // Inner classes
    public static class TmdbSeason {
        @SerializedName("id")
        @Expose
        private Integer id;
        
        @SerializedName("name")
        @Expose
        private String name;
        
        @SerializedName("season_number")
        @Expose
        private Integer seasonNumber;
        
        @SerializedName("air_date")
        @Expose
        private String airDate;
        
        @SerializedName("episode_count")
        @Expose
        private Integer episodeCount;
        
        @SerializedName("poster_path")
        @Expose
        private String posterPath;
        
        // Getters and Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public Integer getSeasonNumber() { return seasonNumber; }
        public void setSeasonNumber(Integer seasonNumber) { this.seasonNumber = seasonNumber; }
        
        public String getAirDate() { return airDate; }
        public void setAirDate(String airDate) { this.airDate = airDate; }
        
        public Integer getEpisodeCount() { return episodeCount; }
        public void setEpisodeCount(Integer episodeCount) { this.episodeCount = episodeCount; }
        
        public String getPosterPath() { return posterPath; }
        public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
    }
    
    public static class TmdbExternalIds {
        @SerializedName("imdb_id")
        @Expose
        private String imdbId;
        
        @SerializedName("tvdb_id")
        @Expose
        private Integer tvdbId;
        
        // Getters and Setters
        public String getImdbId() { return imdbId; }
        public void setImdbId(String imdbId) { this.imdbId = imdbId; }
        
        public Integer getTvdbId() { return tvdbId; }
        public void setTvdbId(Integer tvdbId) { this.tvdbId = tvdbId; }
    }
}