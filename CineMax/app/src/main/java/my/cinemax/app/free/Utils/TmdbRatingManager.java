package my.cinemax.app.free.Utils;

import android.util.Log;

import my.cinemax.app.free.api.TmdbApiClient;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.TmdbMovieResponse;
import my.cinemax.app.free.entity.TmdbSearchResponse;
import my.cinemax.app.free.entity.TmdbTvResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TmdbRatingManager {
    
    private static final String TAG = "TmdbRatingManager";
    
    /**
     * Update movie rating from TMDB
     */
    public static void updateMovieRating(Poster movie, RatingUpdateCallback callback) {
        if (movie == null || movie.getTitle() == null) {
            if (callback != null) {
                callback.onError("Invalid movie data");
            }
            return;
        }
        
        // First search for the movie
        TmdbApiClient.searchMovie(movie.getTitle(), new Callback<TmdbSearchResponse>() {
            @Override
            public void onResponse(Call<TmdbSearchResponse> call, Response<TmdbSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null && 
                    response.body().getResults() != null && !response.body().getResults().isEmpty()) {
                    
                    TmdbSearchResponse.TmdbSearchResult result = response.body().getResults().get(0);
                    
                    // Update movie with TMDB data
                    if (result.getVoteAverage() != null) {
                        movie.setRating(result.getVoteAverage().floatValue());
                    }
                    
                    if (result.getOverview() != null && !result.getOverview().isEmpty()) {
                        movie.setDescription(result.getOverview());
                    }
                    
                    if (result.getReleaseDate() != null && !result.getReleaseDate().isEmpty()) {
                        movie.setYear(result.getReleaseDate().substring(0, 4));
                    }
                    
                    if (result.getPosterPath() != null) {
                        String posterUrl = "https://image.tmdb.org/t/p/w500" + result.getPosterPath();
                        movie.setImage(posterUrl);
                    }
                    
                    if (callback != null) {
                        callback.onSuccess(movie);
                    }
                    
                    Log.d(TAG, "Updated movie rating for: " + movie.getTitle() + " - Rating: " + movie.getRating());
                    
                } else {
                    if (callback != null) {
                        callback.onError("No movie found for: " + movie.getTitle());
                    }
                }
            }
            
            @Override
            public void onFailure(Call<TmdbSearchResponse> call, Throwable t) {
                Log.e(TAG, "Failed to search movie: " + movie.getTitle(), t);
                if (callback != null) {
                    callback.onError("Failed to search movie: " + t.getMessage());
                }
            }
        });
    }
    
    /**
     * Update TV series rating from TMDB
     */
    public static void updateTvRating(Channel tvSeries, RatingUpdateCallback callback) {
        if (tvSeries == null || tvSeries.getTitle() == null) {
            if (callback != null) {
                callback.onError("Invalid TV series data");
            }
            return;
        }
        
        // First search for the TV series
        TmdbApiClient.searchTv(tvSeries.getTitle(), new Callback<TmdbSearchResponse>() {
            @Override
            public void onResponse(Call<TmdbSearchResponse> call, Response<TmdbSearchResponse> response) {
                if (response.isSuccessful() && response.body() != null && 
                    response.body().getResults() != null && !response.body().getResults().isEmpty()) {
                    
                    TmdbSearchResponse.TmdbSearchResult result = response.body().getResults().get(0);
                    
                    // Update TV series with TMDB data
                    if (result.getVoteAverage() != null) {
                        tvSeries.setRating(result.getVoteAverage().floatValue());
                    }
                    
                    if (result.getOverview() != null && !result.getOverview().isEmpty()) {
                        tvSeries.setDescription(result.getOverview());
                    }
                    
                    if (result.getFirstAirDate() != null && !result.getFirstAirDate().isEmpty()) {
                        // Extract year from first air date
                        String year = result.getFirstAirDate().substring(0, 4);
                        // You might want to add a year field to Channel entity
                    }
                    
                    if (result.getPosterPath() != null) {
                        String posterUrl = "https://image.tmdb.org/t/p/w500" + result.getPosterPath();
                        tvSeries.setImage(posterUrl);
                    }
                    
                    if (callback != null) {
                        callback.onSuccess(tvSeries);
                    }
                    
                    Log.d(TAG, "Updated TV series rating for: " + tvSeries.getTitle() + " - Rating: " + tvSeries.getRating());
                    
                } else {
                    if (callback != null) {
                        callback.onError("No TV series found for: " + tvSeries.getTitle());
                    }
                }
            }
            
            @Override
            public void onFailure(Call<TmdbSearchResponse> call, Throwable t) {
                Log.e(TAG, "Failed to search TV series: " + tvSeries.getTitle(), t);
                if (callback != null) {
                    callback.onError("Failed to search TV series: " + t.getMessage());
                }
            }
        });
    }
    
    /**
     * Update multiple movies ratings
     */
    public static void updateMoviesRatings(java.util.List<Poster> movies, RatingUpdateCallback callback) {
        if (movies == null || movies.isEmpty()) {
            if (callback != null) {
                callback.onError("No movies to update");
            }
            return;
        }
        
        final int[] updatedCount = {0};
        final int totalCount = movies.size();
        
        for (Poster movie : movies) {
            updateMovieRating(movie, new RatingUpdateCallback() {
                @Override
                public void onSuccess(Object item) {
                    updatedCount[0]++;
                    if (updatedCount[0] == totalCount) {
                        if (callback != null) {
                            callback.onSuccess(movies);
                        }
                    }
                }
                
                @Override
                public void onError(String error) {
                    Log.w(TAG, "Failed to update movie rating: " + error);
                    updatedCount[0]++;
                    if (updatedCount[0] == totalCount) {
                        if (callback != null) {
                            callback.onSuccess(movies);
                        }
                    }
                }
            });
        }
    }
    
    /**
     * Update multiple TV series ratings
     */
    public static void updateTvSeriesRatings(java.util.List<Channel> tvSeries, RatingUpdateCallback callback) {
        if (tvSeries == null || tvSeries.isEmpty()) {
            if (callback != null) {
                callback.onError("No TV series to update");
            }
            return;
        }
        
        final int[] updatedCount = {0};
        final int totalCount = tvSeries.size();
        
        for (Channel series : tvSeries) {
            updateTvRating(series, new RatingUpdateCallback() {
                @Override
                public void onSuccess(Object item) {
                    updatedCount[0]++;
                    if (updatedCount[0] == totalCount) {
                        if (callback != null) {
                            callback.onSuccess(tvSeries);
                        }
                    }
                }
                
                @Override
                public void onError(String error) {
                    Log.w(TAG, "Failed to update TV series rating: " + error);
                    updatedCount[0]++;
                    if (updatedCount[0] == totalCount) {
                        if (callback != null) {
                            callback.onSuccess(tvSeries);
                        }
                    }
                }
            });
        }
    }
    
    /**
     * Callback interface for rating updates
     */
    public interface RatingUpdateCallback {
        void onSuccess(Object item);
        void onError(String error);
    }
}