package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response class for thriller-specific JSON data
 * This matches the structure of thriller.json
 */
public class ThrillerResponse {
    
    @SerializedName("genre_info")
    @Expose
    private GenreInfo genreInfo;
    
    @SerializedName("thriller_movies")
    @Expose
    private List<Poster> thrillerMovies;
    
    // Getters and Setters
    public GenreInfo getGenreInfo() {
        return genreInfo;
    }
    
    public void setGenreInfo(GenreInfo genreInfo) {
        this.genreInfo = genreInfo;
    }
    
    public List<Poster> getThrillerMovies() {
        return thrillerMovies;
    }
    
    public void setThrillerMovies(List<Poster> thrillerMovies) {
        this.thrillerMovies = thrillerMovies;
    }
    
    // Inner class for genre information
    public static class GenreInfo {
        @SerializedName("id")
        @Expose
        private Integer id;
        
        @SerializedName("title")
        @Expose
        private String title;
        
        @SerializedName("description")
        @Expose
        private String description;
        
        @SerializedName("total_movies")
        @Expose
        private Integer totalMovies;
        
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
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Integer getTotalMovies() {
            return totalMovies;
        }
        
        public void setTotalMovies(Integer totalMovies) {
            this.totalMovies = totalMovies;
        }
    }
}