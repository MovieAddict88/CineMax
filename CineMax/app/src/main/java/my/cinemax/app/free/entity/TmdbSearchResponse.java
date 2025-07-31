package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * TMDB Search API Response Entity
 * Used to search for movies by title and get their TMDB IDs
 */
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
    
    // Inner class for search results
    public static class TmdbSearchResult {
        @SerializedName("id")
        @Expose
        private Integer id;
        
        @SerializedName("title")
        @Expose
        private String title;
        
        @SerializedName("original_title")
        @Expose
        private String originalTitle;
        
        @SerializedName("overview")
        @Expose
        private String overview;
        
        @SerializedName("release_date")
        @Expose
        private String releaseDate;
        
        @SerializedName("vote_average")
        @Expose
        private Float voteAverage;
        
        @SerializedName("vote_count")
        @Expose
        private Integer voteCount;
        
        @SerializedName("poster_path")
        @Expose
        private String posterPath;
        
        @SerializedName("backdrop_path")
        @Expose
        private String backdropPath;
        
        @SerializedName("adult")
        @Expose
        private Boolean adult;
        
        @SerializedName("genre_ids")
        @Expose
        private List<Integer> genreIds;
        
        @SerializedName("original_language")
        @Expose
        private String originalLanguage;
        
        @SerializedName("popularity")
        @Expose
        private Float popularity;
        
        @SerializedName("video")
        @Expose
        private Boolean video;
        
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
        
        public String getOriginalTitle() {
            return originalTitle;
        }
        
        public void setOriginalTitle(String originalTitle) {
            this.originalTitle = originalTitle;
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
        
        public Float getVoteAverage() {
            return voteAverage;
        }
        
        public void setVoteAverage(Float voteAverage) {
            this.voteAverage = voteAverage;
        }
        
        public Integer getVoteCount() {
            return voteCount;
        }
        
        public void setVoteCount(Integer voteCount) {
            this.voteCount = voteCount;
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
        
        public Boolean getAdult() {
            return adult;
        }
        
        public void setAdult(Boolean adult) {
            this.adult = adult;
        }
        
        public List<Integer> getGenreIds() {
            return genreIds;
        }
        
        public void setGenreIds(List<Integer> genreIds) {
            this.genreIds = genreIds;
        }
        
        public String getOriginalLanguage() {
            return originalLanguage;
        }
        
        public void setOriginalLanguage(String originalLanguage) {
            this.originalLanguage = originalLanguage;
        }
        
        public Float getPopularity() {
            return popularity;
        }
        
        public void setPopularity(Float popularity) {
            this.popularity = popularity;
        }
        
        public Boolean getVideo() {
            return video;
        }
        
        public void setVideo(Boolean video) {
            this.video = video;
        }
    }
}