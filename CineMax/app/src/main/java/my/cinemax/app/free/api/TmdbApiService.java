package my.cinemax.app.free.api;

import my.cinemax.app.free.entity.*;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TmdbApiService {
    
    @GET("movie/{movie_id}")
    Call<TmdbMovieResponse> getMovieDetails(
        @Path("movie_id") int movieId,
        @Query("api_key") String apiKey
    );
    
    @GET("tv/{tv_id}")
    Call<TmdbTvResponse> getTvDetails(
        @Path("tv_id") int tvId,
        @Query("api_key") String apiKey
    );
    
    @GET("search/movie")
    Call<TmdbSearchResponse> searchMovie(
        @Query("api_key") String apiKey,
        @Query("query") String query
    );
    
    @GET("search/tv")
    Call<TmdbSearchResponse> searchTv(
        @Query("api_key") String apiKey,
        @Query("query") String query
    );
}