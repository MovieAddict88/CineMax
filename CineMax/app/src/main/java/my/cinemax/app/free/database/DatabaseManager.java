package my.cinemax.app.free.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import my.cinemax.app.free.entity.CachedApiResponse;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Channel;
import java.lang.reflect.Type;
import java.util.List;

public class DatabaseManager {
    private static final String TAG = "DatabaseManager";
    private static final long CACHE_EXPIRATION_TIME = 24 * 60 * 60 * 1000; // 24 hours
    
    private AppDatabase database;
    private Context context;
    
    public DatabaseManager(Context context) {
        this.context = context;
        this.database = AppDatabase.getDatabase(context);
    }
    
    public interface CacheCallback {
        void onSuccess(JsonApiResponse cachedData);
        void onError(String error);
        void onEmpty();
    }
    
    public interface SaveCallback {
        void onSuccess();
        void onError(String error);
    }
    
    public void getCachedData(CacheCallback callback) {
        new AsyncTask<Void, Void, CachedApiResponse>() {
            @Override
            protected CachedApiResponse doInBackground(Void... voids) {
                try {
                    return database.cacheDao().getCachedData();
                } catch (Exception e) {
                    Log.e(TAG, "Error getting cached data: " + e.getMessage());
                    return null;
                }
            }
            
            @Override
            protected void onPostExecute(CachedApiResponse cachedApiResponse) {
                if (cachedApiResponse == null) {
                    callback.onEmpty();
                    return;
                }
                
                if (cachedApiResponse.isExpired(CACHE_EXPIRATION_TIME)) {
                    Log.d(TAG, "Cache expired, need to refresh");
                    callback.onEmpty();
                    return;
                }
                
                try {
                    // Convert cached data back to JsonApiResponse
                    JsonApiResponse jsonApiResponse = convertToJsonApiResponse(cachedApiResponse);
                    Log.d(TAG, "Cache hit - returning cached data");
                    callback.onSuccess(jsonApiResponse);
                } catch (Exception e) {
                    Log.e(TAG, "Error converting cached data: " + e.getMessage());
                    callback.onError("Error loading cached data");
                }
            }
        }.execute();
    }
    
    public void saveCachedData(JsonApiResponse jsonApiResponse, SaveCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    CachedApiResponse cachedData = convertToCachedApiResponse(jsonApiResponse);
                    database.cacheDao().insertCachedData(cachedData);
                    Log.d(TAG, "Data cached successfully");
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error saving cached data: " + e.getMessage());
                    return false;
                }
            }
            
            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to cache data");
                }
            }
        }.execute();
    }
    
    public void clearCache(SaveCallback callback) {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... voids) {
                try {
                    database.cacheDao().clearCache();
                    Log.d(TAG, "Cache cleared successfully");
                    return true;
                } catch (Exception e) {
                    Log.e(TAG, "Error clearing cache: " + e.getMessage());
                    return false;
                }
            }
            
            @Override
            protected void onPostExecute(Boolean success) {
                if (success) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to clear cache");
                }
            }
        }.execute();
    }
    
    private JsonApiResponse convertToJsonApiResponse(CachedApiResponse cachedData) {
        JsonApiResponse response = new JsonApiResponse();
        Gson gson = new Gson();
        
        try {
            if (cachedData.getMoviesJson() != null) {
                Type listType = new com.google.gson.reflect.TypeToken<List<Poster>>(){}.getType();
                List<Poster> movies = gson.fromJson(cachedData.getMoviesJson(), listType);
                response.setMovies(movies);
            }
            
            if (cachedData.getChannelsJson() != null) {
                Type listType = new com.google.gson.reflect.TypeToken<List<Channel>>(){}.getType();
                List<Channel> channels = gson.fromJson(cachedData.getChannelsJson(), listType);
                response.setChannels(channels);
            }
            
            if (cachedData.getHomeJson() != null) {
                JsonApiResponse.HomeData home = gson.fromJson(cachedData.getHomeJson(), JsonApiResponse.HomeData.class);
                response.setHome(home);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error converting cached data to JsonApiResponse: " + e.getMessage());
        }
        
        return response;
    }
    
    private CachedApiResponse convertToCachedApiResponse(JsonApiResponse jsonResponse) {
        CachedApiResponse cachedData = new CachedApiResponse();
        Gson gson = new Gson();
        
        try {
            if (jsonResponse.getMovies() != null) {
                cachedData.setMoviesJson(gson.toJson(jsonResponse.getMovies()));
            }
            
            if (jsonResponse.getChannels() != null) {
                cachedData.setChannelsJson(gson.toJson(jsonResponse.getChannels()));
            }
            
            if (jsonResponse.getHome() != null) {
                cachedData.setHomeJson(gson.toJson(jsonResponse.getHome()));
            }
        } catch (Exception e) {
            Log.e(TAG, "Error converting JsonApiResponse to cached data: " + e.getMessage());
        }
        
        return cachedData;
    }
}