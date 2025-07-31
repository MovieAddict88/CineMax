package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TmdbSearchResponse {
    
    @SerializedName("page")
    @Expose
    private Integer page;
    
    @SerializedName("results")
    @Expose
    private List<TmdbSearchResult> results;
    
    @SerializedName("total_pages")
    @Expose
    private Integer totalPages;
    
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;
    
    // Getters and Setters
    public Integer getPage() {
        return page;
    }
    
    public void setPage(Integer page) {
        this.page = page;
    }
    
    public List<TmdbSearchResult> getResults() {
        return results;
    }
    
    public void setResults(List<TmdbSearchResult> results) {
        this.results = results;
    }
    
    public Integer getTotalPages() {
        return totalPages;
    }
    
    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
    
    public Integer getTotalResults() {
        return totalResults;
    }
    
    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }
    
    public static class TmdbSearchResult {
        @SerializedName("id")
        @Expose
        private Integer id;
        
        @SerializedName("title")
        @Expose
        private String title;
        
        @SerializedName("name")
        @Expose
        private String name;
        
        @SerializedName("overview")
        @Expose
        private String overview;
        
        @SerializedName("release_date")
        @Expose
        private String releaseDate;
        
        @SerializedName("first_air_date")
        @Expose
        private String firstAirDate;
        
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
        
        @SerializedName("media_type")
        @Expose
        private String mediaType;
        
        // Getters and Setters
        public Integer getId() { return id; }
        public void setId(Integer id) { this.id = id; }
        
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getOverview() { return overview; }
        public void setOverview(String overview) { this.overview = overview; }
        
        public String getReleaseDate() { return releaseDate; }
        public void setReleaseDate(String releaseDate) { this.releaseDate = releaseDate; }
        
        public String getFirstAirDate() { return firstAirDate; }
        public void setFirstAirDate(String firstAirDate) { this.firstAirDate = firstAirDate; }
        
        public Double getVoteAverage() { return voteAverage; }
        public void setVoteAverage(Double voteAverage) { this.voteAverage = voteAverage; }
        
        public Integer getVoteCount() { return voteCount; }
        public void setVoteCount(Integer voteCount) { this.voteCount = voteCount; }
        
        public Double getPopularity() { return popularity; }
        public void setPopularity(Double popularity) { this.popularity = popularity; }
        
        public String getPosterPath() { return posterPath; }
        public void setPosterPath(String posterPath) { this.posterPath = posterPath; }
        
        public String getBackdropPath() { return backdropPath; }
        public void setBackdropPath(String backdropPath) { this.backdropPath = backdropPath; }
        
        public String getMediaType() { return mediaType; }
        public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    }
}