package my.cinemax.app.free.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Response class for actor and actress JSON data
 * This matches the structure of actor_actress.json
 */
public class ActorActressResponse {
    
    @SerializedName("actors")
    @Expose
    private List<Actor> actors;
    
    @SerializedName("actresses")
    @Expose
    private List<Actor> actresses;
    
    @SerializedName("total_actors")
    @Expose
    private Integer totalActors;
    
    @SerializedName("total_actresses")
    @Expose
    private Integer totalActresses;
    
    // Getters and Setters
    public List<Actor> getActors() {
        return actors;
    }
    
    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }
    
    public List<Actor> getActresses() {
        return actresses;
    }
    
    public void setActresses(List<Actor> actresses) {
        this.actresses = actresses;
    }
    
    public Integer getTotalActors() {
        return totalActors;
    }
    
    public void setTotalActors(Integer totalActors) {
        this.totalActors = totalActors;
    }
    
    public Integer getTotalActresses() {
        return totalActresses;
    }
    
    public void setTotalActresses(Integer totalActresses) {
        this.totalActresses = totalActresses;
    }
    
    /**
     * Get all cast members (actors + actresses)
     */
    public List<Actor> getAllCast() {
        List<Actor> allCast = new java.util.ArrayList<>();
        if (actors != null) {
            allCast.addAll(actors);
        }
        if (actresses != null) {
            allCast.addAll(actresses);
        }
        return allCast;
    }
    
    /**
     * Get total count of all cast members
     */
    public int getTotalCast() {
        int total = 0;
        if (totalActors != null) total += totalActors;
        if (totalActresses != null) total += totalActresses;
        return total;
    }
}