package my.cinemax.app.free.api;

import android.util.Log;

import my.cinemax.app.free.entity.TmdbMovie;
import my.cinemax.app.free.entity.TmdbSearchResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * TMDB API Client
 * Manages TMDB API requests and provides methods to fetch movie descriptions
 */
public class TmdbApiClient {
    
    private static final String TAG = "TmdbApiClient";
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3/";
    
    // Use the API key provided by the user
    private static final String TMDB_API_KEY = "ec926176bf467b3f7735e3154238c161";
    
    private static TmdbApiClient instance;
    private TmdbApiService apiService;
    
    private TmdbApiClient() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(TMDB_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
                
        apiService = retrofit.create(TmdbApiService.class);
    }
    
    public static synchronized TmdbApiClient getInstance() {
        if (instance == null) {
            instance = new TmdbApiClient();
        }
        return instance;
    }
    
    /**
     * Interface for handling movie description callbacks
     */
    public interface MovieDescriptionCallback {
        void onSuccess(String description);
        void onError(String error);
    }
    
    /**
     * Interface for handling movie rating callbacks
     */
    public interface MovieRatingCallback {
        void onSuccess(Float rating);
        void onError(String error);
    }
    
    /**
     * Interface for handling movie description and rating callbacks together
     */
    public interface MovieDescriptionAndRatingCallback {
        void onSuccess(String description, Float rating);
        void onError(String error);
    }
    
    /**
     * Interface for handling movie search callbacks
     */
    public interface MovieSearchCallback {
        void onSuccess(TmdbSearchResponse response);
        void onError(String error);
    }
    
    /**
     * Interface for handling movie details callbacks
     */
    public interface MovieDetailsCallback {
        void onSuccess(TmdbMovie movie);
        void onError(String error);
    }
    
    /**
     * Get movie description by searching for the movie title
     * This method will search for the movie and return the description of the first result
     * 
     * @param movieTitle The title of the movie to search for
     * @param callback Callback to handle the result
     */
    public void getMovieDescription(String movieTitle, MovieDescriptionCallback callback) {
        Log.d(TAG, "Searching for movie: " + movieTitle);
        
        Call<TmdbSearchResponse> call = apiService.searchMovies(TMDB_API_KEY, movieTitle, 1);
        call.enqueue(new Callback<TmdbSearchResponse>() {
            @Override
            public void onResponse(Call<TmdbSearchResponse> call, Response<TmdbSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TmdbSearchResponse searchResponse = response.body();
                    
                    if (searchResponse.getResults() != null && !searchResponse.getResults().isEmpty()) {
                        // Get the first result (most relevant match)
                        TmdbSearchResponse.TmdbSearchResult firstResult = searchResponse.getResults().get(0);
                        String overview = firstResult.getOverview();
                        
                        if (overview != null && !overview.trim().isEmpty()) {
                            Log.d(TAG, "Found description for: " + movieTitle);
                            callback.onSuccess(overview);
                        } else {
                            // If no overview in search results, try to get detailed info
                            getMovieDetailsById(firstResult.getId(), new MovieDetailsCallback() {
                                @Override
                                public void onSuccess(TmdbMovie movie) {
                                    String detailedOverview = movie.getOverview();
                                    if (detailedOverview != null && !detailedOverview.trim().isEmpty()) {
                                        callback.onSuccess(detailedOverview);
                                    } else {
                                        callback.onError("No description available for this movie");
                                    }
                                }
                                
                                @Override
                                public void onError(String error) {
                                    callback.onError("Failed to get detailed movie information: " + error);
                                }
                            });
                        }
                    } else {
                        Log.w(TAG, "No search results found for: " + movieTitle);
                        callback.onError("Movie not found in TMDB database");
                    }
                } else {
                    Log.e(TAG, "Search request failed: " + response.code() + " - " + response.message());
                    callback.onError("Failed to search for movie: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<TmdbSearchResponse> call, Throwable t) {
                Log.e(TAG, "Search request failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Search for movies by title
     * 
     * @param movieTitle The title to search for
     * @param callback Callback to handle the result
     */
    public void searchMovies(String movieTitle, MovieSearchCallback callback) {
        Log.d(TAG, "Searching movies for: " + movieTitle);
        
        Call<TmdbSearchResponse> call = apiService.searchMovies(TMDB_API_KEY, movieTitle, 1);
        call.enqueue(new Callback<TmdbSearchResponse>() {
            @Override
            public void onResponse(Call<TmdbSearchResponse> call, Response<TmdbSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Search successful for: " + movieTitle);
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "Search failed: " + response.code() + " - " + response.message());
                    callback.onError("Search failed: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<TmdbSearchResponse> call, Throwable t) {
                Log.e(TAG, "Search request failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Get detailed movie information by TMDB ID
     * 
     * @param movieId TMDB movie ID
     * @param callback Callback to handle the result
     */
    public void getMovieDetailsById(Integer movieId, MovieDetailsCallback callback) {
        Log.d(TAG, "Getting movie details for ID: " + movieId);
        
        Call<TmdbMovie> call = apiService.getMovieDetails(movieId, TMDB_API_KEY);
        call.enqueue(new Callback<TmdbMovie>() {
            @Override
            public void onResponse(Call<TmdbMovie> call, Response<TmdbMovie> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Log.d(TAG, "Movie details retrieved for ID: " + movieId);
                    callback.onSuccess(response.body());
                } else {
                    Log.e(TAG, "Failed to get movie details: " + response.code() + " - " + response.message());
                    callback.onError("Failed to get movie details: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<TmdbMovie> call, Throwable t) {
                Log.e(TAG, "Movie details request failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Get movie description by TMDB ID directly
     * 
     * @param movieId TMDB movie ID
     * @param callback Callback to handle the result
     */
    public void getMovieDescriptionById(Integer movieId, MovieDescriptionCallback callback) {
        getMovieDetailsById(movieId, new MovieDetailsCallback() {
            @Override
            public void onSuccess(TmdbMovie movie) {
                String overview = movie.getOverview();
                if (overview != null && !overview.trim().isEmpty()) {
                    callback.onSuccess(overview);
                } else {
                    callback.onError("No description available for this movie");
                }
            }
            
            @Override
            public void onError(String error) {
                callback.onError(error);
            }
        });
    }
    
    /**
     * Get movie rating by searching for the movie title
     * 
     * @param movieTitle The title of the movie to search for
     * @param callback Callback to handle the result
     */
    public void getMovieRating(String movieTitle, MovieRatingCallback callback) {
        Log.d(TAG, "Fetching rating from TMDB for: " + movieTitle);
        
        Call<TmdbSearchResponse> call = apiService.searchMovies(TMDB_API_KEY, movieTitle, 1);
        call.enqueue(new Callback<TmdbSearchResponse>() {
            @Override
            public void onResponse(Call<TmdbSearchResponse> call, Response<TmdbSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TmdbSearchResponse searchResponse = response.body();
                    
                    if (searchResponse.getResults() != null && !searchResponse.getResults().isEmpty()) {
                        TmdbSearchResponse.TmdbSearchResult firstResult = searchResponse.getResults().get(0);
                        Float rating = firstResult.getVoteAverage();
                        
                        if (rating != null && rating > 0) {
                            Log.d(TAG, "Found rating for: " + movieTitle + " - " + rating);
                            callback.onSuccess(rating);
                        } else {
                            getMovieDetailsById(firstResult.getId(), new MovieDetailsCallback() {
                                @Override
                                public void onSuccess(TmdbMovie movie) {
                                    Float detailedRating = movie.getVoteAverage();
                                    if (detailedRating != null && detailedRating > 0) {
                                        callback.onSuccess(detailedRating);
                                    } else {
                                        callback.onError("No rating available for this movie");
                                    }
                                }
                                
                                @Override
                                public void onError(String error) {
                                    callback.onError("Failed to get detailed movie rating: " + error);
                                }
                            });
                        }
                    } else {
                        callback.onError("Movie not found in TMDB database");
                    }
                } else {
                    Log.e(TAG, "Rating search request failed: " + response.code() + " - " + response.message());
                    callback.onError("Failed to search for movie rating: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<TmdbSearchResponse> call, Throwable t) {
                Log.e(TAG, "Rating search request failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
    
    /**
     * Get both movie description and rating by searching for the movie title
     * This is more efficient than making separate calls
     * 
     * @param movieTitle The title of the movie to search for
     * @param callback Callback to handle the result
     */
    public void getMovieDescriptionAndRating(String movieTitle, MovieDescriptionAndRatingCallback callback) {
        Log.d(TAG, "Fetching description and rating from TMDB for: " + movieTitle);
        
        Call<TmdbSearchResponse> call = apiService.searchMovies(TMDB_API_KEY, movieTitle, 1);
        call.enqueue(new Callback<TmdbSearchResponse>() {
            @Override
            public void onResponse(Call<TmdbSearchResponse> call, Response<TmdbSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    TmdbSearchResponse searchResponse = response.body();
                    
                    if (searchResponse.getResults() != null && !searchResponse.getResults().isEmpty()) {
                        TmdbSearchResponse.TmdbSearchResult firstResult = searchResponse.getResults().get(0);
                        String overview = firstResult.getOverview();
                        Float rating = firstResult.getVoteAverage();
                        
                        // If we have both description and rating from search, use them
                        if (overview != null && !overview.trim().isEmpty() && rating != null && rating > 0) {
                            Log.d(TAG, "Found description and rating for: " + movieTitle);
                            callback.onSuccess(overview, rating);
                        } else {
                            // Get detailed info if search results are incomplete
                            getMovieDetailsById(firstResult.getId(), new MovieDetailsCallback() {
                                @Override
                                public void onSuccess(TmdbMovie movie) {
                                    String detailedOverview = movie.getOverview();
                                    Float detailedRating = movie.getVoteAverage();
                                    
                                    // Use the best available data
                                    String finalOverview = (detailedOverview != null && !detailedOverview.trim().isEmpty()) 
                                        ? detailedOverview 
                                        : (overview != null && !overview.trim().isEmpty()) ? overview : null;
                                    
                                    Float finalRating = (detailedRating != null && detailedRating > 0) 
                                        ? detailedRating 
                                        : (rating != null && rating > 0) ? rating : null;
                                    
                                    if (finalOverview != null || finalRating != null) {
                                        callback.onSuccess(finalOverview, finalRating);
                                    } else {
                                        callback.onError("No description or rating available for this movie");
                                    }
                                }
                                
                                @Override
                                public void onError(String error) {
                                    // Fallback to search results if detail fetch fails
                                    if (overview != null && !overview.trim().isEmpty() || rating != null && rating > 0) {
                                        callback.onSuccess(overview, rating);
                                    } else {
                                        callback.onError("Failed to get detailed movie information: " + error);
                                    }
                                }
                            });
                        }
                    } else {
                        Log.w(TAG, "No search results found for: " + movieTitle);
                        callback.onError("Movie not found in TMDB database");
                    }
                } else {
                    Log.e(TAG, "Search request failed: " + response.code() + " - " + response.message());
                    callback.onError("Failed to search for movie: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<TmdbSearchResponse> call, Throwable t) {
                Log.e(TAG, "Search request failed", t);
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }
}