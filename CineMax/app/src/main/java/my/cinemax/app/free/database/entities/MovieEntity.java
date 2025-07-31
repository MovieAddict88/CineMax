package my.cinemax.app.free.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import my.cinemax.app.free.database.converters.TypeConverterUtils;

import java.util.List;

@Entity(tableName = "movies")
@TypeConverters(TypeConverterUtils.class)
public class MovieEntity {
    @PrimaryKey
    private int id;
    private String title;
    private String type;
    private String label;
    private String sublabel;
    private String imdb;
    private String downloadas;
    private boolean comment;
    private String playas;
    private String description;
    private String classification;
    private String year;
    private String duration;
    private float rating;
    private String image;
    private String cover;
    private String genres; // JSON string
    private String actors; // JSON string
    private int views;
    private String createdAt;
    private String sources; // JSON string
    private String trailer;
    private String subtitles; // JSON string
    private boolean featured;
    private long lastUpdated; // Timestamp for cache management

    // Constructors
    public MovieEntity() {}

    public MovieEntity(int id, String title, String type, String label, String sublabel, 
                      String imdb, String downloadas, boolean comment, String playas, 
                      String description, String classification, String year, String duration, 
                      float rating, String image, String cover, String genres, String actors, 
                      int views, String createdAt, String sources, String trailer, 
                      String subtitles, boolean featured, long lastUpdated) {
        this.id = id;
        this.title = title;
        this.type = type;
        this.label = label;
        this.sublabel = sublabel;
        this.imdb = imdb;
        this.downloadas = downloadas;
        this.comment = comment;
        this.playas = playas;
        this.description = description;
        this.classification = classification;
        this.year = year;
        this.duration = duration;
        this.rating = rating;
        this.image = image;
        this.cover = cover;
        this.genres = genres;
        this.actors = actors;
        this.views = views;
        this.createdAt = createdAt;
        this.sources = sources;
        this.trailer = trailer;
        this.subtitles = subtitles;
        this.featured = featured;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

    public String getSublabel() { return sublabel; }
    public void setSublabel(String sublabel) { this.sublabel = sublabel; }

    public String getImdb() { return imdb; }
    public void setImdb(String imdb) { this.imdb = imdb; }

    public String getDownloadas() { return downloadas; }
    public void setDownloadas(String downloadas) { this.downloadas = downloadas; }

    public boolean isComment() { return comment; }
    public void setComment(boolean comment) { this.comment = comment; }

    public String getPlayas() { return playas; }
    public void setPlayas(String playas) { this.playas = playas; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getGenres() { return genres; }
    public void setGenres(String genres) { this.genres = genres; }

    public String getActors() { return actors; }
    public void setActors(String actors) { this.actors = actors; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public String getSources() { return sources; }
    public void setSources(String sources) { this.sources = sources; }

    public String getTrailer() { return trailer; }
    public void setTrailer(String trailer) { this.trailer = trailer; }

    public String getSubtitles() { return subtitles; }
    public void setSubtitles(String subtitles) { this.subtitles = subtitles; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}