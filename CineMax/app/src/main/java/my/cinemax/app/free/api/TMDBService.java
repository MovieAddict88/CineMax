package my.cinemax.app.free.api;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * TMDB API Service for auto-detecting movie and series metadata
 */
public class TMDBService {
    
    private static final String TAG = "TMDBService";
    private static final String TMDB_API_KEY = "ec926176bf467b3f7735e3154238c161";
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";
    private static TMDBService instance;
    private TMDBApiInterface apiInterface;
    
    private TMDBService() {
        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDB_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        
        apiInterface = retrofit.create(TMDBApiInterface.class);
    }
    
    public static TMDBService getInstance() {
        if (instance == null) {
            instance = new TMDBService();
        }
        return instance;
    }
    
    /**
     * Search for a movie by title
     */
    public void searchMovie(String title, TMDBMovieCallback callback) {
        Call<JsonObject> call = apiInterface.searchMovie(TMDB_API_KEY, title);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject jsonResponse = response.body();
                        if (jsonResponse.has("results") && jsonResponse.getAsJsonArray("results").size() > 0) {
                            JsonObject movie = jsonResponse.getAsJsonArray("results").get(0).getAsJsonObject();
                            callback.onSuccess(parseMovieData(movie));
                        } else {
                            callback.onError("No movie found for: " + title);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing movie response", e);
                        callback.onError("Error parsing movie data");
                    }
                } else {
                    callback.onError("Failed to fetch movie data");
                }
            }
            
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Movie search failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Search for a TV series by title
     */
    public void searchTVSeries(String title, TMDBTVCallback callback) {
        Call<JsonObject> call = apiInterface.searchTVSeries(TMDB_API_KEY, title);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject jsonResponse = response.body();
                        if (jsonResponse.has("results") && jsonResponse.getAsJsonArray("results").size() > 0) {
                            JsonObject series = jsonResponse.getAsJsonArray("results").get(0).getAsJsonObject();
                            callback.onSuccess(parseTVData(series));
                        } else {
                            callback.onError("No TV series found for: " + title);
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing TV series response", e);
                        callback.onError("Error parsing TV series data");
                    }
                } else {
                    callback.onError("Failed to fetch TV series data");
                }
            }
            
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "TV series search failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Get detailed movie information including credits
     */
    public void getMovieDetails(int movieId, TMDBMovieCallback callback) {
        Call<JsonObject> call = apiInterface.getMovieDetails(movieId, TMDB_API_KEY, "credits");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject movie = response.body();
                        callback.onSuccess(parseMovieData(movie));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing movie details", e);
                        callback.onError("Error parsing movie details");
                    }
                } else {
                    callback.onError("Failed to fetch movie details");
                }
            }
            
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "Movie details fetch failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Get detailed TV series information including credits
     */
    public void getTVSeriesDetails(int seriesId, TMDBTVCallback callback) {
        Call<JsonObject> call = apiInterface.getTVSeriesDetails(seriesId, TMDB_API_KEY, "credits");
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, retrofit2.Response<JsonObject> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        JsonObject series = response.body();
                        callback.onSuccess(parseTVData(series));
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing TV series details", e);
                        callback.onError("Error parsing TV series details");
                    }
                } else {
                    callback.onError("Failed to fetch TV series details");
                }
            }
            
            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.e(TAG, "TV series details fetch failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Parse movie data from TMDB response
     */
    private TMDBMovieData parseMovieData(JsonObject movie) {
        TMDBMovieData data = new TMDBMovieData();
        
        try {
            data.id = movie.has("id") ? movie.get("id").getAsInt() : 0;
            data.title = movie.has("title") ? movie.get("title").getAsString() : "";
            data.originalTitle = movie.has("original_title") ? movie.get("original_title").getAsString() : "";
            data.overview = movie.has("overview") ? movie.get("overview").getAsString() : "";
            data.releaseDate = movie.has("release_date") ? movie.get("release_date").getAsString() : "";
            data.runtime = movie.has("runtime") ? movie.get("runtime").getAsInt() : 0;
            data.voteAverage = movie.has("vote_average") ? movie.get("vote_average").getAsDouble() : 0.0;
            data.voteCount = movie.has("vote_count") ? movie.get("vote_count").getAsInt() : 0;
            data.imdbId = movie.has("imdb_id") ? movie.get("imdb_id").getAsString() : "";
            
            // Parse genres
            if (movie.has("genres") && movie.get("genres").isJsonArray()) {
                for (int i = 0; i < movie.getAsJsonArray("genres").size(); i++) {
                    JsonObject genre = movie.getAsJsonArray("genres").get(i).getAsJsonObject();
                    data.genres.add(genre.get("name").getAsString());
                }
            }
            
            // Parse cast
            if (movie.has("credits") && movie.get("credits").isJsonObject()) {
                JsonObject credits = movie.getAsJsonObject("credits");
                if (credits.has("cast") && credits.get("cast").isJsonArray()) {
                    for (int i = 0; i < Math.min(credits.getAsJsonArray("cast").size(), 10); i++) {
                        JsonObject actor = credits.getAsJsonArray("cast").get(i).getAsJsonObject();
                        TMDBMovieData.Actor actorData = new TMDBMovieData.Actor();
                        actorData.name = actor.has("name") ? actor.get("name").getAsString() : "";
                        actorData.character = actor.has("character") ? actor.get("character").getAsString() : "";
                        actorData.profilePath = actor.has("profile_path") ? actor.get("profile_path").getAsString() : "";
                        data.cast.add(actorData);
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing movie data", e);
        }
        
        return data;
    }
    
    /**
     * Parse TV series data from TMDB response
     */
    private TMDBTVData parseTVData(JsonObject series) {
        TMDBTVData data = new TMDBTVData();
        
        try {
            data.id = series.has("id") ? series.get("id").getAsInt() : 0;
            data.name = series.has("name") ? series.get("name").getAsString() : "";
            data.originalName = series.has("original_name") ? series.get("original_name").getAsString() : "";
            data.overview = series.has("overview") ? series.get("overview").getAsString() : "";
            data.firstAirDate = series.has("first_air_date") ? series.get("first_air_date").getAsString() : "";
            data.voteAverage = series.has("vote_average") ? series.get("vote_average").getAsDouble() : 0.0;
            data.voteCount = series.has("vote_count") ? series.get("vote_count").getAsInt() : 0;
            data.numberOfSeasons = series.has("number_of_seasons") ? series.get("number_of_seasons").getAsInt() : 0;
            data.numberOfEpisodes = series.has("number_of_episodes") ? series.get("number_of_episodes").getAsInt() : 0;
            
            // Parse genres
            if (series.has("genres") && series.get("genres").isJsonArray()) {
                for (int i = 0; i < series.getAsJsonArray("genres").size(); i++) {
                    JsonObject genre = series.getAsJsonArray("genres").get(i).getAsJsonObject();
                    data.genres.add(genre.get("name").getAsString());
                }
            }
            
            // Parse cast
            if (series.has("credits") && series.get("credits").isJsonObject()) {
                JsonObject credits = series.getAsJsonObject("credits");
                if (credits.has("cast") && credits.get("cast").isJsonArray()) {
                    for (int i = 0; i < Math.min(credits.getAsJsonArray("cast").size(), 10); i++) {
                        JsonObject actor = credits.getAsJsonArray("cast").get(i).getAsJsonObject();
                        TMDBTVData.Actor actorData = new TMDBTVData.Actor();
                        actorData.name = actor.has("name") ? actor.get("name").getAsString() : "";
                        actorData.character = actor.has("character") ? actor.get("character").getAsString() : "";
                        actorData.profilePath = actor.has("profile_path") ? actor.get("profile_path").getAsString() : "";
                        data.cast.add(actorData);
                    }
                }
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Error parsing TV series data", e);
        }
        
        return data;
    }
    
    /**
     * TMDB API Interface
     */
    public interface TMDBApiInterface {
        @GET("search/movie")
        Call<JsonObject> searchMovie(@Query("api_key") String apiKey, @Query("query") String query);
        
        @GET("search/tv")
        Call<JsonObject> searchTVSeries(@Query("api_key") String apiKey, @Query("query") String query);
        
        @GET("movie/{movie_id}")
        Call<JsonObject> getMovieDetails(@Path("movie_id") int movieId, @Query("api_key") String apiKey, @Query("append_to_response") String appendToResponse);
        
        @GET("tv/{tv_id}")
        Call<JsonObject> getTVSeriesDetails(@Path("tv_id") int seriesId, @Query("api_key") String apiKey, @Query("append_to_response") String appendToResponse);
    }
    
    /**
     * Callback interfaces
     */
    public interface TMDBMovieCallback {
        void onSuccess(TMDBMovieData movieData);
        void onError(String error);
    }
    
    public interface TMDBTVCallback {
        void onSuccess(TMDBTVData tvData);
        void onError(String error);
    }
    
    /**
     * Movie data class
     */
    public static class TMDBMovieData {
        public int id;
        public String title;
        public String originalTitle;
        public String overview;
        public String releaseDate;
        public int runtime;
        public double voteAverage;
        public int voteCount;
        public String imdbId;
        public java.util.List<String> genres = new java.util.ArrayList<>();
        public java.util.List<Actor> cast = new java.util.ArrayList<>();
        
        public static class Actor {
            public String name;
            public String character;
            public String profilePath;
        }
    }
    
    /**
     * TV series data class
     */
    public static class TMDBTVData {
        public int id;
        public String name;
        public String originalName;
        public String overview;
        public String firstAirDate;
        public double voteAverage;
        public int voteCount;
        public int numberOfSeasons;
        public int numberOfEpisodes;
        public java.util.List<String> genres = new java.util.ArrayList<>();
        public java.util.List<Actor> cast = new java.util.ArrayList<>();
        
        public static class Actor {
            public String name;
            public String character;
            public String profilePath;
        }
    }
}