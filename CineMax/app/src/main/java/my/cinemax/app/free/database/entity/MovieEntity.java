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
    private String genre;
    private String cast;
    private String director;
    private String trailer;
    private String type;
    private long lastUpdated;

    public MovieEntity() {}

    public MovieEntity(int id, String title, String description, String poster, String backdrop, 
                      String releaseDate, String runtime, String rating, String genre, 
                      String cast, String director, String trailer, String type) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.poster = poster;
        this.backdrop = backdrop;
        this.releaseDate = releaseDate;
        this.runtime = runtime;
        this.rating = rating;
        this.genre = genre;
        this.cast = cast;
        this.director = director;
        this.trailer = trailer;
        this.type = type;
        this.lastUpdated = System.currentTimeMillis();
    }

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

    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }

    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }

    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }

    public String getTrailer() { return trailer; }
    public void setTrailer(String trailer) { this.trailer = trailer; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}