package my.cinemax.app.free.db;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.google.gson.Gson;

import java.io.IOException;

import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.entity.JsonApiResponse;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Simple helper that fetches data once from network and caches it so that
 * subsequent app launches can re-use the local DB copy, avoiding repeat network calls.
 */
public class ApiRepository {

    private static final String HOME_CACHE_KEY = "home_data";

    private final ApiCacheDao cacheDao;
    private final apiRest apiService;
    private final Gson gson = new Gson();

    public ApiRepository(@NonNull Context context) {
        AppDatabase db = AppDatabase.getInstance(context);
        cacheDao = db.apiCacheDao();
        Retrofit retrofit = apiClient.getClient();
        apiService = retrofit.create(apiRest.class);
    }

    /**
     * Returns home data, using cache when available.
     */
    public JsonApiResponse getHomeData() {
        // 1) Try cache first
        ApiCache cached = cacheDao.get(HOME_CACHE_KEY);
        if (cached != null && !TextUtils.isEmpty(cached.getJson())) {
            try {
                return gson.fromJson(cached.getJson(), JsonApiResponse.class);
            } catch (Exception ignored) {
            }
        }

        // 2) Fetch from network
        try {
            Response<JsonApiResponse> response = apiService.getHomeDataFromJson().execute();
            if (response.isSuccessful() && response.body() != null) {
                String jsonString = gson.toJson(response.body());
                cacheDao.insert(new ApiCache(HOME_CACHE_KEY, jsonString, System.currentTimeMillis()));
                return response.body();
            }
        } catch (IOException ignored) {
        }
        return null;
    }
}