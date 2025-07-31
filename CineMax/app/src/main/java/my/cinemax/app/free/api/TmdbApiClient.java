package my.cinemax.app.free.api;

import android.util.Log;

import my.cinemax.app.free.config.Global;
import my.cinemax.app.free.entity.*;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.util.concurrent.TimeUnit;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

public class TmdbApiClient {
    
    private static final String TAG = "TmdbApiClient";
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";
    private static final String TMDB_API_KEY = Global.TMDB_API_KEY;
    
    private static Retrofit tmdbRetrofit = null;
    private static TmdbApiService tmdbApiService = null;
    
    /**
     * Get TMDB API service instance
     */
    public static TmdbApiService getTmdbApiService() {
        if (tmdbApiService == null) {
            tmdbApiService = getTmdbRetrofit().create(TmdbApiService.class);
        }
        return tmdbApiService;
    }
    
    /**
     * Get TMDB Retrofit instance
     */
    private static Retrofit getTmdbRetrofit() {
        if (tmdbRetrofit == null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(provideHttpLoggingInterceptor())
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build();
            
            tmdbRetrofit = new Retrofit.Builder()
                    .baseUrl(TMDB_BASE_URL)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return tmdbRetrofit;
    }
    
    /**
     * HTTP logging interceptor for debugging
     */
    private static HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d(TAG, "TMDB API: " + message);
            }
        });
        httpLoggingInterceptor.setLevel(BODY);
        return httpLoggingInterceptor;
    }
    
    /**
     * Get movie details from TMDB
     */
    public static void getMovieDetails(int movieId, Callback<TmdbMovieResponse> callback) {
        Call<TmdbMovieResponse> call = getTmdbApiService().getMovieDetails(movieId, TMDB_API_KEY);
        call.enqueue(callback);
    }
    
    /**
     * Get TV series details from TMDB
     */
    public static void getTvDetails(int tvId, Callback<TmdbTvResponse> callback) {
        Call<TmdbTvResponse> call = getTmdbApiService().getTvDetails(tvId, TMDB_API_KEY);
        call.enqueue(callback);
    }
    
    /**
     * Search for movies on TMDB
     */
    public static void searchMovie(String query, Callback<TmdbSearchResponse> callback) {
        Call<TmdbSearchResponse> call = getTmdbApiService().searchMovie(TMDB_API_KEY, query);
        call.enqueue(callback);
    }
    
    /**
     * Search for TV series on TMDB
     */
    public static void searchTv(String query, Callback<TmdbSearchResponse> callback) {
        Call<TmdbSearchResponse> call = getTmdbApiService().searchTv(TMDB_API_KEY, query);
        call.enqueue(callback);
    }
}