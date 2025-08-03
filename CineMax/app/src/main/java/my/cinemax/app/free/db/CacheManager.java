package my.cinemax.app.free.db;

import com.google.gson.Gson;

import my.cinemax.app.free.MyApplication;
import my.cinemax.app.free.entity.JsonApiResponse;

public class CacheManager {

    private static final String KEY_GLOBAL_JSON = "global_json";
    private static final long MAX_CACHE_AGE_MS = 12 * 60 * 60 * 1000; // 12 hours

    private static ApiCacheDao getDao() {
        return AppDatabase.getInstance(MyApplication.getInstance()).apiCacheDao();
    }

    /**
     * Return cached JsonApiResponse if available and not stale.
     */
    public static JsonApiResponse getCachedJsonResponse() {
        ApiCacheEntity entity = getDao().getCache(KEY_GLOBAL_JSON);
        if (entity != null) {
            long now = System.currentTimeMillis();
            if (now - entity.timestamp < MAX_CACHE_AGE_MS && entity.json != null) {
                try {
                    return new Gson().fromJson(entity.json, JsonApiResponse.class);
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }

    /**
     * Save JsonApiResponse to local database for future offline use.
     */
    public static void saveJsonResponse(JsonApiResponse response) {
        if (response == null) return;
        String json = new Gson().toJson(response);
        ApiCacheEntity entity = new ApiCacheEntity(KEY_GLOBAL_JSON, json, System.currentTimeMillis());
        getDao().insert(entity);
    }
}