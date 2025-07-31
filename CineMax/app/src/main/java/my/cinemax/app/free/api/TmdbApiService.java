package my.cinemax.app.free.api;

import my.cinemax.app.free.entity.TmdbMovie;
import my.cinemax.app.free.entity.TmdbSearchResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * TMDB API Service Interface
 * Used to fetch movie descriptions and details from The Movie Database API
 */
public interface TmdbApiService {
    
    // Base URL for TMDB API: https://api.themoviedb.org/3/
    
    /**
     * Search for movies by title
     * @param apiKey TMDB API key
     * @param query Movie title to search for
     * @param page Page number (optional, default: 1)
     * @return Search response with list of movies
     */
    @GET("search/movie")
    Call<TmdbSearchResponse> searchMovies(
        @Query("api_key") String apiKey,
        @Query("query") String query,
        @Query("page") Integer page
    );
    
    /**
     * Get movie details by TMDB ID
     * @param movieId TMDB movie ID
     * @param apiKey TMDB API key
     * @param language Language for the response (optional, default: en-US)
     * @return Movie details including description/overview
     */
    @GET("movie/{movie_id}")
    Call<TmdbMovie> getMovieDetails(
        @Path("movie_id") Integer movieId,
        @Query("api_key") String apiKey,
        @Query("language") String language
    );
    
    /**
     * Get movie details by TMDB ID with default language
     * @param movieId TMDB movie ID
     * @param apiKey TMDB API key
     * @return Movie details including description/overview
     */
    @GET("movie/{movie_id}")
    Call<TmdbMovie> getMovieDetails(
        @Path("movie_id") Integer movieId,
        @Query("api_key") String apiKey
    );
}