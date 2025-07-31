package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * TMDB TV Series API Response Entity
 * Used to fetch TV series descriptions and details from TMDB API
 */
public class TmdbTvSeries {
    
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
    
    @SerializedName("number_of_seasons")
    @Expose
    private Integer numberOfSeasons;
    
    @SerializedName("number_of_episodes")
    @Expose
    private Integer numberOfEpisodes;
    
    @SerializedName("genres")
    @Expose
    private List<TmdbGenre> genres;
    
    @SerializedName("production_companies")
    @Expose
    private List<TmdbProductionCompany> productionCompanies;
    
    @SerializedName("production_countries")
    @Expose
    private List<TmdbProductionCountry> productionCountries;
    
    @SerializedName("spoken_languages")
    @Expose
    private List<TmdbSpokenLanguage> spokenLanguages;
    
    @SerializedName("status")
    @Expose
    private String status;
    
    @SerializedName("tagline")
    @Expose
    private String tagline;
    
    @SerializedName("episode_run_time")
    @Expose
    private List<Integer> episodeRunTime;
    
    @SerializedName("in_production")
    @Expose
    private Boolean inProduction;
    
    @SerializedName("original_name")
    @Expose
    private String originalName;
    
    @SerializedName("original_language")
    @Expose
    private String originalLanguage;
    
    @SerializedName("popularity")
    @Expose
    private Float popularity;
    
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
    
    public List<TmdbGenre> getGenres() {
        return genres;
    }
    
    public void setGenres(List<TmdbGenre> genres) {
        this.genres = genres;
    }
    
    public List<TmdbProductionCompany> getProductionCompanies() {
        return productionCompanies;
    }
    
    public void setProductionCompanies(List<TmdbProductionCompany> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }
    
    public List<TmdbProductionCountry> getProductionCountries() {
        return productionCountries;
    }
    
    public void setProductionCountries(List<TmdbProductionCountry> productionCountries) {
        this.productionCountries = productionCountries;
    }
    
    public List<TmdbSpokenLanguage> getSpokenLanguages() {
        return spokenLanguages;
    }
    
    public void setSpokenLanguages(List<TmdbSpokenLanguage> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getTagline() {
        return tagline;
    }
    
    public void setTagline(String tagline) {
        this.tagline = tagline;
    }
    
    public List<Integer> getEpisodeRunTime() {
        return episodeRunTime;
    }
    
    public void setEpisodeRunTime(List<Integer> episodeRunTime) {
        this.episodeRunTime = episodeRunTime;
    }
    
    public Boolean getInProduction() {
        return inProduction;
    }
    
    public void setInProduction(Boolean inProduction) {
        this.inProduction = inProduction;
    }
    
    public String getOriginalName() {
        return originalName;
    }
    
    public void setOriginalName(String originalName) {
        this.originalName = originalName;
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
    
    // Inner classes for nested objects (reuse from TmdbMovie)
    public static class TmdbGenre {
        @SerializedName("id")
        @Expose
        private Integer id;
        
        @SerializedName("name")
        @Expose
        private String name;
        
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
    }
    
    public static class TmdbProductionCompany {
        @SerializedName("id")
        @Expose
        private Integer id;
        
        @SerializedName("name")
        @Expose
        private String name;
        
        @SerializedName("logo_path")
        @Expose
        private String logoPath;
        
        @SerializedName("origin_country")
        @Expose
        private String originCountry;
        
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
        
        public String getLogoPath() {
            return logoPath;
        }
        
        public void setLogoPath(String logoPath) {
            this.logoPath = logoPath;
        }
        
        public String getOriginCountry() {
            return originCountry;
        }
        
        public void setOriginCountry(String originCountry) {
            this.originCountry = originCountry;
        }
    }
    
    public static class TmdbProductionCountry {
        @SerializedName("iso_3166_1")
        @Expose
        private String iso31661;
        
        @SerializedName("name")
        @Expose
        private String name;
        
        public String getIso31661() {
            return iso31661;
        }
        
        public void setIso31661(String iso31661) {
            this.iso31661 = iso31661;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
    
    public static class TmdbSpokenLanguage {
        @SerializedName("english_name")
        @Expose
        private String englishName;
        
        @SerializedName("iso_639_1")
        @Expose
        private String iso6391;
        
        @SerializedName("name")
        @Expose
        private String name;
        
        public String getEnglishName() {
            return englishName;
        }
        
        public void setEnglishName(String englishName) {
            this.englishName = englishName;
        }
        
        public String getIso6391() {
            return iso6391;
        }
        
        public void setIso6391(String iso6391) {
            this.iso6391 = iso6391;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
    }
}