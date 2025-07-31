package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * TMDB Movie API Response Entity
 * Used to fetch movie descriptions and details from TMDB API
 */
public class TmdbMovie {
    
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
    
    @SerializedName("runtime")
    @Expose
    private Integer runtime;
    
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
    
    public Integer getRuntime() {
        return runtime;
    }
    
    public void setRuntime(Integer runtime) {
        this.runtime = runtime;
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
    
    // Inner classes for nested objects
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