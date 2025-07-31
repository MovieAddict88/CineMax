package my.cinemax.app.free.api;

import my.cinemax.app.free.entity.TmdbMovieResponse;
import my.cinemax.app.free.entity.TmdbTvResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * TMDB API Service for fetching movie and TV series ratings
 */
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
}