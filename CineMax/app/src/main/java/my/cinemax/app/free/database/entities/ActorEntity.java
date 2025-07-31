package my.cinemax.app.free.database.entities;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "actors")
public class ActorEntity {
    @PrimaryKey
    private int id;
    private String name;
    private String image;
    private String biography;
    private String birthDate;
    private String birthPlace;
    private String nationality;
    private long lastUpdated; // Timestamp for cache management

    // Constructors
    public ActorEntity() {}

    @Ignore
    public ActorEntity(int id, String name, String image, String biography, 
                      String birthDate, String birthPlace, String nationality, long lastUpdated) {
        this.id = id;
        this.name = name;
        this.image = image;
        this.biography = biography;
        this.birthDate = birthDate;
        this.birthPlace = birthPlace;
        this.nationality = nationality;
        this.lastUpdated = lastUpdated;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public String getBiography() { return biography; }
    public void setBiography(String biography) { this.biography = biography; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getBirthPlace() { return birthPlace; }
    public void setBirthPlace(String birthPlace) { this.birthPlace = birthPlace; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}