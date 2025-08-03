package my.cinemax.app.free.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "api_cache")
public class ApiCache {

    @PrimaryKey
    @NonNull
    private String key; // Identifier for the cached payload (ex: "home_data")

    private String json; // Raw JSON string

    private long timestamp; // Epoch millis when it was cached

    public ApiCache(@NonNull String key, String json, long timestamp) {
        this.key = key;
        this.json = json;
        this.timestamp = timestamp;
    }

    @NonNull
    public String getKey() {
        return key;
    }

    public void setKey(@NonNull String key) {
        this.key = key;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}