package my.cinemax.app.free.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "api_cache")
public class ApiCacheEntity {

    @PrimaryKey
    @NonNull
    public String key; // We will store only one key for the global json response

    public String json;

    public long timestamp;

    public ApiCacheEntity(@NonNull String key, String json, long timestamp) {
        this.key = key;
        this.json = json;
        this.timestamp = timestamp;
    }
}