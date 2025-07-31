package my.cinemax.app.free.Utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

public class TmdbMetadataUtil {
    private static final String TMDB_MOVIE_URL = "https://api.themoviedb.org/3/movie/";
    private static final String TMDB_TV_URL = "https://api.themoviedb.org/3/tv/";
    private static final String TMDB_API_KEY_PARAM = "?api_key=";
    private static final String TMDB_LANGUAGE_PARAM = "&language=en-US";

    private static final OkHttpClient client = new OkHttpClient();

    public static JSONObject fetchMovieMetadata(int tmdbId, String apiKey) throws IOException {
        String url = TMDB_MOVIE_URL + tmdbId + TMDB_API_KEY_PARAM + apiKey + TMDB_LANGUAGE_PARAM;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return new JSONObject(response.body().string());
            }
        }
        return null;
    }

    public static JSONObject fetchTvMetadata(int tmdbId, String apiKey) throws IOException {
        String url = TMDB_TV_URL + tmdbId + TMDB_API_KEY_PARAM + apiKey + TMDB_LANGUAGE_PARAM;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return new JSONObject(response.body().string());
            }
        }
        return null;
    }

    public static JSONObject fetchTvSeasonMetadata(int tmdbId, int seasonNumber, String apiKey) throws IOException {
        String url = TMDB_TV_URL + tmdbId + "/season/" + seasonNumber + TMDB_API_KEY_PARAM + apiKey + TMDB_LANGUAGE_PARAM;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return new JSONObject(response.body().string());
            }
        }
        return null;
    }

    public static JSONObject fetchTvEpisodeMetadata(int tmdbId, int seasonNumber, int episodeNumber, String apiKey) throws IOException {
        String url = TMDB_TV_URL + tmdbId + "/season/" + seasonNumber + "/episode/" + episodeNumber + TMDB_API_KEY_PARAM + apiKey + TMDB_LANGUAGE_PARAM;
        Request request = new Request.Builder().url(url).build();
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                return new JSONObject(response.body().string());
            }
        }
        return null;
    }
}