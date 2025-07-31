package my.cinemax.app.free.api;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

import my.cinemax.app.free.config.Global;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.TmdbMovieResponse;
import my.cinemax.app.free.entity.TmdbTvResponse;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * TMDB Rating Manager for fetching and caching movie/TV ratings
 */
public class TmdbRatingManager {
    private static final String TAG = "TmdbRatingManager";
    private static TmdbRatingManager instance;
    private TmdbApiService tmdbService;
    private Map<String, Float> ratingCache;

    private TmdbRatingManager() {
        tmdbService = apiClient.getTmdbClient().create(TmdbApiService.class);
        ratingCache = new HashMap<>();
    }

    public static synchronized TmdbRatingManager getInstance() {
        if (instance == null) {
            instance = new TmdbRatingManager();
        }
        return instance;
    }

    /**
     * Fetch and set rating for a movie
     */
    public void fetchMovieRating(Poster movie, RatingCallback callback) {
        if (movie.getId() == null) {
            callback.onError("Movie ID is null");
            return;
        }

        String cacheKey = "movie_" + movie.getId();
        if (ratingCache.containsKey(cacheKey)) {
            Float cachedRating = ratingCache.get(cacheKey);
            movie.setRating(cachedRating);
            callback.onSuccess(cachedRating);
            return;
        }

        Call<TmdbMovieResponse> call = tmdbService.getMovieDetails(movie.getId(), Global.TMDB_API_KEY);
        call.enqueue(new Callback<TmdbMovieResponse>() {
            @Override
            public void onResponse(Call<TmdbMovieResponse> call, Response<TmdbMovieResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Float rating = response.body().getVoteAverage();
                    if (rating != null) {
                        ratingCache.put(cacheKey, rating);
                        movie.setRating(rating);
                        callback.onSuccess(rating);
                        Log.d(TAG, "Fetched rating for movie " + movie.getTitle() + ": " + rating);
                    } else {
                        callback.onError("No rating available");
                    }
                } else {
                    callback.onError("Failed to fetch movie rating: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TmdbMovieResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching movie rating: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Fetch and set rating for a TV series
     */
    public void fetchTvRating(Channel tvSeries, RatingCallback callback) {
        if (tvSeries.getId() == null) {
            callback.onError("TV series ID is null");
            return;
        }

        String cacheKey = "tv_" + tvSeries.getId();
        if (ratingCache.containsKey(cacheKey)) {
            Float cachedRating = ratingCache.get(cacheKey);
            tvSeries.setRating(cachedRating);
            callback.onSuccess(cachedRating);
            return;
        }

        Call<TmdbTvResponse> call = tmdbService.getTvDetails(tvSeries.getId(), Global.TMDB_API_KEY);
        call.enqueue(new Callback<TmdbTvResponse>() {
            @Override
            public void onResponse(Call<TmdbTvResponse> call, Response<TmdbTvResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Float rating = response.body().getVoteAverage();
                    if (rating != null) {
                        ratingCache.put(cacheKey, rating);
                        tvSeries.setRating(rating);
                        callback.onSuccess(rating);
                        Log.d(TAG, "Fetched rating for TV series " + tvSeries.getTitle() + ": " + rating);
                    } else {
                        callback.onError("No rating available");
                    }
                } else {
                    callback.onError("Failed to fetch TV rating: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<TmdbTvResponse> call, Throwable t) {
                Log.e(TAG, "Error fetching TV rating: " + t.getMessage());
                callback.onError("Network error: " + t.getMessage());
            }
        });
    }

    /**
     * Callback interface for rating operations
     */
    public interface RatingCallback {
        void onSuccess(Float rating);
        void onError(String error);
    }
}