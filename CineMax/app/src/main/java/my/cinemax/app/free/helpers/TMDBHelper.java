package my.cinemax.app.free.helpers;

import android.util.Log;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Actor;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Source;

public class TMDBHelper {
    private static final String TAG = "TMDBHelper";
    private static final String TMDB_API_KEY = "ec926176bf467b3f7735e3154238c161";
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3";
    private static final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    
    private OkHttpClient client;
    
    public TMDBHelper() {
        client = new OkHttpClient();
    }
    
    public interface TMDBCallback {
        void onSuccess(Poster poster);
        void onError(String error);
    }
    
    /**
     * Enhance a poster with TMDB metadata
     */
    public void enhancePosterWithTMDB(Poster poster, TMDBCallback callback) {
        if (poster.getTmdbId() == null) {
            callback.onError("No TMDB ID found for poster: " + poster.getTitle());
            return;
        }
        
        String url = TMDB_BASE_URL + "/movie/" + poster.getTmdbId() + 
                    "?api_key=" + TMDB_API_KEY + "&append_to_response=credits";
        
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Failed to fetch TMDB data", e);
                callback.onError("Network error: " + e.getMessage());
            }
            
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("TMDB API error: " + response.code());
                    return;
                }
                
                try {
                    String responseBody = response.body().string();
                    JsonObject movieData = JsonParser.parseString(responseBody).getAsJsonObject();
                    
                    // Update poster with TMDB data
                    updatePosterWithTMDBData(poster, movieData);
                    
                    // Add vidsrc.net embed source
                    addVidSrcEmbedSource(poster);
                    
                    callback.onSuccess(poster);
                    
                } catch (Exception e) {
                    Log.e(TAG, "Failed to parse TMDB response", e);
                    callback.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }
    
    private void updatePosterWithTMDBData(Poster poster, JsonObject movieData) {
        // Update basic movie information
        if (movieData.has("overview") && !movieData.get("overview").isJsonNull()) {
            poster.setDescription(movieData.get("overview").getAsString());
        }
        
        if (movieData.has("original_title") && !movieData.get("original_title").isJsonNull()) {
            poster.setOriginalTitle(movieData.get("original_title").getAsString());
        }
        
        if (movieData.has("release_date") && !movieData.get("release_date").isJsonNull()) {
            String releaseDate = movieData.get("release_date").getAsString();
            if (!releaseDate.isEmpty()) {
                poster.setYear(releaseDate.substring(0, 4));
            }
        }
        
        if (movieData.has("runtime") && !movieData.get("runtime").isJsonNull()) {
            int runtime = movieData.get("runtime").getAsInt();
            int hours = runtime / 60;
            int minutes = runtime % 60;
            poster.setDuration(hours > 0 ? hours + ":" + String.format("%02d", minutes) : minutes + " min");
        }
        
        if (movieData.has("vote_average") && !movieData.get("vote_average").isJsonNull()) {
            float rating = movieData.get("vote_average").getAsFloat();
            poster.setRating(rating);
            poster.setImdb(String.valueOf(Math.round(rating * 10) / 10.0));
        }
        
        if (movieData.has("popularity") && !movieData.get("popularity").isJsonNull()) {
            poster.setPopularity(movieData.get("popularity").getAsFloat());
        }
        
        if (movieData.has("poster_path") && !movieData.get("poster_path").isJsonNull()) {
            poster.setPosterPath(movieData.get("poster_path").getAsString());
            // Only update image if it's empty
            if (poster.getImage() == null || poster.getImage().isEmpty()) {
                poster.setImage(TMDB_IMAGE_BASE_URL + movieData.get("poster_path").getAsString());
            }
        }
        
        if (movieData.has("backdrop_path") && !movieData.get("backdrop_path").isJsonNull()) {
            poster.setBackdropPath(movieData.get("backdrop_path").getAsString());
            // Only update cover if it's empty
            if (poster.getCover() == null || poster.getCover().isEmpty()) {
                poster.setCover("https://image.tmdb.org/t/p/w1280" + movieData.get("backdrop_path").getAsString());
            }
        }
        
        // Update genres
        if (movieData.has("genres")) {
            JsonArray genresArray = movieData.getAsJsonArray("genres");
            List<Genre> genres = new ArrayList<>();
            for (int i = 0; i < genresArray.size(); i++) {
                JsonObject genreObj = genresArray.get(i).getAsJsonObject();
                Genre genre = new Genre();
                genre.setId(genreObj.get("id").getAsInt());
                genre.setTitle(genreObj.get("name").getAsString());
                genres.add(genre);
            }
            poster.setGenres(genres);
        }
        
        // Update actors from credits
        if (movieData.has("credits") && movieData.getAsJsonObject("credits").has("cast")) {
            JsonArray castArray = movieData.getAsJsonObject("credits").getAsJsonArray("cast");
            List<Actor> actors = new ArrayList<>();
            
            // Get top 5 actors
            int maxActors = Math.min(5, castArray.size());
            for (int i = 0; i < maxActors; i++) {
                JsonObject actorObj = castArray.get(i).getAsJsonObject();
                Actor actor = new Actor();
                actor.setId(i + 1);
                actor.setName(actorObj.get("name").getAsString());
                actor.setType("actor");
                if (actorObj.has("character") && !actorObj.get("character").isJsonNull()) {
                    actor.setRole(actorObj.get("character").getAsString());
                }
                if (actorObj.has("profile_path") && !actorObj.get("profile_path").isJsonNull()) {
                    actor.setImage(TMDB_IMAGE_BASE_URL + actorObj.get("profile_path").getAsString());
                }
                actors.add(actor);
            }
            poster.setActors(actors);
        }
        
        // Set classification based on rating
        if (poster.getRating() != null) {
            float rating = poster.getRating();
            if (rating >= 8.0) {
                poster.setClassification("PG");
            } else if (rating >= 6.0) {
                poster.setClassification("PG-13");
            } else {
                poster.setClassification("R");
            }
        }
    }
    
    private void addVidSrcEmbedSource(Poster poster) {
        if (poster.getTmdbId() == null) return;
        
        // Create vidsrc.net embed source
        Source vidsrcSource = new Source();
        vidsrcSource.setId(poster.getSources().size() + 1);
        vidsrcSource.setType("embed");
        vidsrcSource.setTitle("VidSrc Embed");
        vidsrcSource.setQuality("HD");
        vidsrcSource.setSize("Stream");
        vidsrcSource.setKind("external");
        vidsrcSource.setPremium("false");
        vidsrcSource.setExternal(true);
        vidsrcSource.setUrl("https://vidsrc.net/embed/movie/" + poster.getTmdbId());
        
        // Add to sources list
        poster.getSources().add(vidsrcSource);
        
        Log.d(TAG, "Added VidSrc embed source for movie: " + poster.getTitle());
    }
    
    /**
     * Quick method to enhance The Avengers movie (TMDB ID: 24428)
     */
    public void enhanceAvengersMovie(Poster poster, TMDBCallback callback) {
        poster.setTmdbId(24428);
        poster.setTitle("The Avengers");
        enhancePosterWithTMDB(poster, callback);
    }
}