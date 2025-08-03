package my.cinemax.app.free.repository;

import android.content.Context;
import android.os.AsyncTask;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.database.CineMaxDatabase;
import my.cinemax.app.free.database.dao.*;
import my.cinemax.app.free.entity.*;
import my.cinemax.app.free.Provider.PrefManager;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class CineMaxRepository {
    
    private PosterDao posterDao;
    private ChannelDao channelDao;
    private GenreDao genreDao;
    private CategoryDao categoryDao;
    private apiRest apiService;
    private Context context;
    private PrefManager prefManager;
    private Executor executor;
    
    public CineMaxRepository(Context context) {
        this.context = context;
        CineMaxDatabase database = CineMaxDatabase.getInstance(context);
        
        posterDao = database.posterDao();
        channelDao = database.channelDao();
        genreDao = database.genreDao();
        categoryDao = database.categoryDao();
        
        apiService = apiClient.getClient().create(apiRest.class);
        prefManager = new PrefManager(context);
        executor = Executors.newFixedThreadPool(4);
    }
    
    // Poster methods
    public LiveData<List<Poster>> getAllPosters() {
        refreshPostersFromApi();
        return posterDao.getAllPosters();
    }
    
    public LiveData<List<Poster>> getPostersByType(String type) {
        refreshPostersFromApi();
        return posterDao.getPostersByType(type);
    }
    
    public LiveData<Poster> getPosterById(int id) {
        return posterDao.getPosterById(id);
    }
    
    // Channel methods
    public LiveData<List<Channel>> getAllChannels() {
        refreshChannelsFromApi();
        return channelDao.getAllChannels();
    }
    
    public LiveData<Channel> getChannelById(int id) {
        return channelDao.getChannelById(id);
    }
    
    // Genre methods
    public LiveData<List<Genre>> getAllGenres() {
        refreshGenresFromApi();
        return genreDao.getAllGenres();
    }
    
    // Category methods
    public LiveData<List<Category>> getAllCategories() {
        refreshCategoriesFromApi();
        return categoryDao.getAllCategories();
    }
    
    // Search methods
    public LiveData<List<Poster>> searchPosters(String query) {
        return posterDao.searchPosters(query);
    }
    
    public LiveData<List<Channel>> searchChannels(String query) {
        return channelDao.searchChannels(query);
    }
    
    // Refresh data from API methods
    private void refreshPostersFromApi() {
        // Only refresh if we don't have cached data or cache is old
        executor.execute(() -> {
            int cachedCount = posterDao.getPostersCount();
            if (cachedCount == 0 || shouldRefreshCache("posters")) {
                fetchPostersFromApi();
            }
        });
    }
    
    private void refreshChannelsFromApi() {
        executor.execute(() -> {
            int cachedCount = channelDao.getChannelsCount();
            if (cachedCount == 0 || shouldRefreshCache("channels")) {
                fetchChannelsFromApi();
            }
        });
    }
    
    private void refreshGenresFromApi() {
        executor.execute(() -> {
            int cachedCount = genreDao.getGenresCount();
            if (cachedCount == 0 || shouldRefreshCache("genres")) {
                fetchGenresFromApi();
            }
        });
    }
    
    private void refreshCategoriesFromApi() {
        executor.execute(() -> {
            int cachedCount = categoryDao.getCategoriesCount();
            if (cachedCount == 0 || shouldRefreshCache("categories")) {
                fetchCategoriesFromApi();
            }
        });
    }
    
    // API fetch methods
    private void fetchPostersFromApi() {
        // This will be implemented to use GitHub JSON API
        Call<JsonApiResponse> call = apiService.getMoviesFromJson();
        call.enqueue(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse apiResponse = response.body();
                    executor.execute(() -> {
                        if (apiResponse.getMovies() != null) {
                            posterDao.insertPosters(apiResponse.getMovies());
                        }
                        updateCacheTimestamp("posters");
                    });
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                // Handle error - data will come from cache
            }
        });
    }
    
    private void fetchChannelsFromApi() {
        Call<JsonApiResponse> call = apiService.getChannelsFromJson();
        call.enqueue(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse apiResponse = response.body();
                    executor.execute(() -> {
                        if (apiResponse.getChannels() != null) {
                            channelDao.insertChannels(apiResponse.getChannels());
                        }
                        updateCacheTimestamp("channels");
                    });
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                // Handle error - data will come from cache
            }
        });
    }
    
    private void fetchGenresFromApi() {
        Call<JsonApiResponse> call = apiService.getGenresFromJson();
        call.enqueue(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse apiResponse = response.body();
                    executor.execute(() -> {
                        if (apiResponse.getGenres() != null) {
                            genreDao.insertGenres(apiResponse.getGenres());
                        }
                        updateCacheTimestamp("genres");
                    });
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                // Handle error - data will come from cache
            }
        });
    }
    
    private void fetchCategoriesFromApi() {
        Call<JsonApiResponse> call = apiService.getJsonApiData();
        call.enqueue(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse apiResponse = response.body();
                    executor.execute(() -> {
                        if (apiResponse.getCategories() != null) {
                            categoryDao.insertCategories(apiResponse.getCategories());
                        }
                        updateCacheTimestamp("categories");
                    });
                }
            }
            
            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                // Handle error - data will come from cache
            }
        });
    }
    
    // Cache management methods
    private boolean shouldRefreshCache(String dataType) {
        long lastUpdate = prefManager.getLong("cache_" + dataType + "_timestamp", 0);
        long currentTime = System.currentTimeMillis();
        long cacheValidityPeriod = 30 * 60 * 1000; // 30 minutes
        
        return (currentTime - lastUpdate) > cacheValidityPeriod;
    }
    
    private void updateCacheTimestamp(String dataType) {
        prefManager.setLong("cache_" + dataType + "_timestamp", System.currentTimeMillis());
    }
    
    // Force refresh methods (for manual refresh)
    public void forceRefreshPosters() {
        executor.execute(() -> {
            posterDao.deleteAllPosters();
            fetchPostersFromApi();
        });
    }
    
    public void forceRefreshChannels() {
        executor.execute(() -> {
            channelDao.deleteAllChannels();
            fetchChannelsFromApi();
        });
    }
    
    public void forceRefreshAll() {
        forceRefreshPosters();
        forceRefreshChannels();
        executor.execute(() -> {
            genreDao.deleteAllGenres();
            categoryDao.deleteAllCategories();
            fetchGenresFromApi();
            fetchCategoriesFromApi();
        });
    }
}