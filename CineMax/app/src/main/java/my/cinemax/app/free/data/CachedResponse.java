package my.cinemax.app.free.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_responses")
public class CachedResponse {

    @PrimaryKey
    @NonNull
    private String key;

    private String json;

    private long timestamp;

    public CachedResponse(@NonNull String key, String json, long timestamp) {
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