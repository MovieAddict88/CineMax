package my.cinemax.app.free.database.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import my.cinemax.app.free.database.converters.TypeConverterUtils;

@Entity(tableName = "channels")
@TypeConverters(TypeConverterUtils.class)
public class ChannelEntity {
    @PrimaryKey
    private int id;
    private String name;
    private String description;
    private String image;
    private String cover;
    private String streamUrl;
    private String type;
    private String classification;
    private float rating;
    private int views;
    private String categories; // JSON string
    private String country;
    private String language;
    private boolean featured;
    private String createdAt;
    private long lastUpdated; // Timestamp for cache management

    // Constructors
    public ChannelEntity() {}

    @Ignore
    public ChannelEntity(int id, String name, String description, String image, String cover,
                        String streamUrl, String type, String classification, float rating,
                        int views, String categories, String country, String language,
                        boolean featured, String createdAt, long lastUpdated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.image = image;
        this.cover = cover;
        this.streamUrl = streamUrl;
        this.type = type;
        this.classification = classification;
        this.rating = rating;
        this.views = views;
        this.categories = categories;
        this.country = country;
        this.language = language;
        this.featured = featured;
        this.createdAt = createdAt;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getCover() { return cover; }
    public void setCover(String cover) { this.cover = cover; }

    public String getStreamUrl() { return streamUrl; }
    public void setStreamUrl(String streamUrl) { this.streamUrl = streamUrl; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    public float getRating() { return rating; }
    public void setRating(float rating) { this.rating = rating; }

    public int getViews() { return views; }
    public void setViews(int views) { this.views = views; }

    public String getCategories() { return categories; }
    public void setCategories(String categories) { this.categories = categories; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public boolean isFeatured() { return featured; }
    public void setFeatured(boolean featured) { this.featured = featured; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}