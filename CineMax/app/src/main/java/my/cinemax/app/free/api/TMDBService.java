package my.cinemax.app.free.api;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import my.cinemax.app.free.entity.Actor;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Source;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * TMDB Service for auto-detecting movie and TV series metadata
 * Integrates with The Movie Database API
 */
public class TMDBService {
    private static final String TAG = "TMDBService";
    private static final String TMDB_API_KEY = "ec926176bf467b3f7735e3154238c161";
    private static final String TMDB_BASE_URL = "https://api.themoviedb.org/3";
    private static final String VIDSRC_BASE_URL = "https://vidsrc.net/embed";
    
    private OkHttpClient client;
    private Gson gson;
    
    public TMDBService() {
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();
        this.gson = new Gson();
    }
    
    /**
     * Search for a movie on TMDB and auto-populate metadata
     */
    public void searchAndPopulateMovie(String title, String year, TMDBCallback callback) {
        new Thread(() -> {
            try {
                // Search for movie
                String searchUrl = TMDB_BASE_URL + "/search/movie?api_key=" + TMDB_API_KEY + 
                                 "&query=" + title.replace(" ", "%20");
                if (year != null && !year.isEmpty()) {
                    searchUrl += "&year=" + year;
                }
                
                JsonObject searchResult = makeRequest(searchUrl);
                if (searchResult != null && searchResult.has("results")) {
                    JsonArray results = searchResult.getAsJsonArray("results");
                    if (results.size() > 0) {
                        JsonObject movie = results.get(0).getAsJsonObject();
                        int movieId = movie.get("id").getAsInt();
                        
                        // Get detailed movie information
                        String detailsUrl = TMDB_BASE_URL + "/movie/" + movieId + 
                                          "?api_key=" + TMDB_API_KEY + 
                                          "&append_to_response=credits,videos";
                        
                        JsonObject movieDetails = makeRequest(detailsUrl);
                        if (movieDetails != null) {
                            Poster poster = createMoviePoster(movieDetails);
                            callback.onSuccess(poster);
                            return;
                        }
                    }
                }
                callback.onError("Movie not found in TMDB");
            } catch (Exception e) {
                Log.e(TAG, "Error searching movie: " + e.getMessage());
                callback.onError(e.getMessage());
            }
        }).start();
    }
    
    /**
     * Search for a TV series on TMDB and auto-populate metadata
     */
    public void searchAndPopulateTVSeries(String title, String year, TMDBCallback callback) {
        new Thread(() -> {
            try {
                // Search for TV series
                String searchUrl = TMDB_BASE_URL + "/search/tv?api_key=" + TMDB_API_KEY + 
                                 "&query=" + title.replace(" ", "%20");
                if (year != null && !year.isEmpty()) {
                    searchUrl += "&first_air_date_year=" + year;
                }
                
                JsonObject searchResult = makeRequest(searchUrl);
                if (searchResult != null && searchResult.has("results")) {
                    JsonArray results = searchResult.getAsJsonArray("results");
                    if (results.size() > 0) {
                        JsonObject tvShow = results.get(0).getAsJsonObject();
                        int tvId = tvShow.get("id").getAsInt();
                        
                        // Get detailed TV information
                        String detailsUrl = TMDB_BASE_URL + "/tv/" + tvId + 
                                          "?api_key=" + TMDB_API_KEY + 
                                          "&append_to_response=credits,videos";
                        
                        JsonObject tvDetails = makeRequest(detailsUrl);
                        if (tvDetails != null) {
                            Poster poster = createTVPoster(tvDetails);
                            callback.onSuccess(poster);
                            return;
                        }
                    }
                }
                callback.onError("TV series not found in TMDB");
            } catch (Exception e) {
                Log.e(TAG, "Error searching TV series: " + e.getMessage());
                callback.onError(e.getMessage());
            }
        }).start();
    }
    
    /**
     * Auto-detect and enhance existing poster with TMDB data
     */
    public void enhancePosterWithTMDB(Poster originalPoster, TMDBCallback callback) {
        if (originalPoster.getType().equals("movie")) {
            searchAndPopulateMovie(originalPoster.getTitle(), originalPoster.getYear(), callback);
        } else if (originalPoster.getType().equals("series")) {
            searchAndPopulateTVSeries(originalPoster.getTitle(), originalPoster.getYear(), callback);
        } else {
            callback.onError("Unsupported content type");
        }
    }
    
    private JsonObject makeRequest(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String jsonString = response.body().string();
                return JsonParser.parseString(jsonString).getAsJsonObject();
            }
        }
        return null;
    }
    
    private Poster createMoviePoster(JsonObject movieDetails) {
        Poster poster = new Poster();
        
        try {
            // Basic information with null checks
            if (movieDetails.has("id") && !movieDetails.get("id").isJsonNull()) {
                poster.setId(movieDetails.get("id").getAsInt());
            }
            
            if (movieDetails.has("title") && !movieDetails.get("title").isJsonNull()) {
                poster.setTitle(movieDetails.get("title").getAsString());
            } else {
                poster.setTitle("Unknown Movie");
            }
            
            poster.setType("movie");
            
            if (movieDetails.has("overview") && !movieDetails.get("overview").isJsonNull()) {
                poster.setDescription(movieDetails.get("overview").getAsString());
            } else {
                poster.setDescription("");
            }
            
            if (movieDetails.has("release_date") && !movieDetails.get("release_date").isJsonNull()) {
                String releaseDate = movieDetails.get("release_date").getAsString();
                if (releaseDate.length() >= 4) {
                    poster.setYear(releaseDate.substring(0, 4));
                }
            } else {
                poster.setYear("");
            }
            
            if (movieDetails.has("vote_average") && !movieDetails.get("vote_average").isJsonNull()) {
                double rating = movieDetails.get("vote_average").getAsDouble();
                poster.setImdb(String.valueOf(rating));
                poster.setRating(Float.valueOf((float) rating));
            } else {
                poster.setImdb("0.0");
                poster.setRating(Float.valueOf(0.0f));
            }
        
            // Runtime
            if (movieDetails.has("runtime") && !movieDetails.get("runtime").isJsonNull()) {
                int runtime = movieDetails.get("runtime").getAsInt();
                poster.setDuration(runtime + ":00");
            } else {
                poster.setDuration("120:00"); // Default duration
            }
            
            // Genres
            if (movieDetails.has("genres") && !movieDetails.get("genres").isJsonNull()) {
                JsonArray genresArray = movieDetails.getAsJsonArray("genres");
                List<Genre> genres = new ArrayList<>();
                for (int i = 0; i < genresArray.size(); i++) {
                    JsonObject genreObj = genresArray.get(i).getAsJsonObject();
                    if (genreObj != null) {
                        Genre genre = new Genre();
                        if (genreObj.has("id") && !genreObj.get("id").isJsonNull()) {
                            genre.setId(genreObj.get("id").getAsInt());
                        }
                        if (genreObj.has("name") && !genreObj.get("name").isJsonNull()) {
                            genre.setTitle(genreObj.get("name").getAsString());
                        }
                        genres.add(genre);
                    }
                }
                poster.setGenres(genres);
                
                // Set label as primary genre
                if (!genres.isEmpty() && genres.get(0).getTitle() != null) {
                    poster.setLabel(genres.get(0).getTitle());
                } else {
                    poster.setLabel("Movie");
                }
            } else {
                poster.setLabel("Movie");
            }
        
            // Cast/Actors
            if (movieDetails.has("credits") && !movieDetails.get("credits").isJsonNull()) {
                JsonObject credits = movieDetails.getAsJsonObject("credits");
                if (credits.has("cast") && !credits.get("cast").isJsonNull()) {
                    JsonArray castArray = credits.getAsJsonArray("cast");
                    List<Actor> actors = new ArrayList<>();
                    
                    // Get top 5 actors
                    int maxActors = Math.min(5, castArray.size());
                    for (int i = 0; i < maxActors; i++) {
                        JsonObject castMember = castArray.get(i).getAsJsonObject();
                        if (castMember != null) {
                            Actor actor = new Actor();
                            if (castMember.has("id") && !castMember.get("id").isJsonNull()) {
                                actor.setId(castMember.get("id").getAsInt());
                            }
                            if (castMember.has("name") && !castMember.get("name").isJsonNull()) {
                                actor.setName(castMember.get("name").getAsString());
                            } else {
                                actor.setName("Unknown Actor");
                            }
                            actor.setType("actor");
                            if (castMember.has("character") && !castMember.get("character").isJsonNull()) {
                                actor.setRole(castMember.get("character").getAsString());
                            } else {
                                actor.setRole("");
                            }
                            // No image links as requested
                            actor.setImage("");
                            actor.setBorn("");
                            actor.setHeight("");
                            actor.setBio("");
                            actors.add(actor);
                        }
                    }
                    poster.setActors(actors);
                }
            }
        
        // Trailer
        if (movieDetails.has("videos")) {
            JsonObject videos = movieDetails.getAsJsonObject("videos");
            if (videos.has("results")) {
                JsonArray videosArray = videos.getAsJsonArray("results");
                for (int i = 0; i < videosArray.size(); i++) {
                    JsonObject video = videosArray.get(i).getAsJsonObject();
                    if (video.get("type").getAsString().equals("Trailer") && 
                        video.get("site").getAsString().equals("YouTube")) {
                        String youtubeKey = video.get("key").getAsString();
                        String trailerUrl = "https://www.youtube.com/watch?v=" + youtubeKey;
                        
                        Source trailer = new Source();
                        trailer.setTitle(poster.getTitle() + " Trailer");
                        trailer.setUrl(trailerUrl);
                        trailer.setType("video");
                        poster.setTrailer(trailer);
                        break;
                    }
                }
            }
        }
        
        // Add VidSrc source
        List<Source> sources = new ArrayList<>();
        Source vidsrcSource = new Source();
        vidsrcSource.setTitle(poster.getTitle() + " - VidSrc");
        vidsrcSource.setUrl(VIDSRC_BASE_URL + "/movie/" + poster.getId());
        vidsrcSource.setQuality("HD");
        vidsrcSource.setType("video");
        vidsrcSource.setExternal(true);
        sources.add(vidsrcSource);
        poster.setSources(sources);
        
        // No image links as requested
        poster.setImage("");
        poster.setCover("");
        
        // Additional metadata
        poster.setComment(true);
        poster.setPlayas("video");
        poster.setSublabel("TMDB Rating: " + String.format("%.1f", poster.getRating()));
        poster.setClassification("PG-13"); // Default classification
        
        return poster;
    }
    
    private Poster createTVPoster(JsonObject tvDetails) {
        Poster poster = new Poster();
        
        // Basic information
        poster.setId(tvDetails.get("id").getAsInt());
        poster.setTitle(tvDetails.get("name").getAsString());
        poster.setType("series");
        poster.setDescription(tvDetails.has("overview") ? tvDetails.get("overview").getAsString() : "");
        poster.setYear(tvDetails.has("first_air_date") ? 
                      tvDetails.get("first_air_date").getAsString().substring(0, 4) : "");
        poster.setImdb(String.valueOf(tvDetails.get("vote_average").getAsDouble()));
        poster.setRating(Float.valueOf(tvDetails.get("vote_average").getAsFloat()));
        
        // Genres
        if (tvDetails.has("genres")) {
            JsonArray genresArray = tvDetails.getAsJsonArray("genres");
            List<Genre> genres = new ArrayList<>();
            for (int i = 0; i < genresArray.size(); i++) {
                JsonObject genreObj = genresArray.get(i).getAsJsonObject();
                Genre genre = new Genre();
                genre.setId(genreObj.get("id").getAsInt());
                genre.setTitle(genreObj.get("name").getAsString());
                genres.add(genre);
            }
            poster.setGenres(genres);
            
            // Set label as primary genre
            if (!genres.isEmpty()) {
                poster.setLabel(genres.get(0).getTitle());
            }
        }
        
        // Cast/Actors
        if (tvDetails.has("credits")) {
            JsonObject credits = tvDetails.getAsJsonObject("credits");
            if (credits.has("cast")) {
                JsonArray castArray = credits.getAsJsonArray("cast");
                List<Actor> actors = new ArrayList<>();
                
                // Get top 5 actors
                int maxActors = Math.min(5, castArray.size());
                for (int i = 0; i < maxActors; i++) {
                    JsonObject castMember = castArray.get(i).getAsJsonObject();
                    Actor actor = new Actor();
                    actor.setId(castMember.get("id").getAsInt());
                    actor.setName(castMember.get("name").getAsString());
                    actor.setType("actor");
                    if (castMember.has("character")) {
                        actor.setRole(castMember.get("character").getAsString());
                    }
                    // No image links as requested
                    actor.setImage("");
                    actors.add(actor);
                }
                poster.setActors(actors);
            }
        }
        
        // Trailer
        if (tvDetails.has("videos")) {
            JsonObject videos = tvDetails.getAsJsonObject("videos");
            if (videos.has("results")) {
                JsonArray videosArray = videos.getAsJsonArray("results");
                for (int i = 0; i < videosArray.size(); i++) {
                    JsonObject video = videosArray.get(i).getAsJsonObject();
                    if (video.get("type").getAsString().equals("Trailer") && 
                        video.get("site").getAsString().equals("YouTube")) {
                        String youtubeKey = video.get("key").getAsString();
                        String trailerUrl = "https://www.youtube.com/watch?v=" + youtubeKey;
                        
                        Source trailer = new Source();
                        trailer.setTitle(poster.getTitle() + " Trailer");
                        trailer.setUrl(trailerUrl);
                        trailer.setType("video");
                        poster.setTrailer(trailer);
                        break;
                    }
                }
            }
        }
        
        // No image links as requested
        poster.setImage("");
        poster.setCover("");
        
        // Additional metadata
        poster.setComment(true);
        poster.setPlayas("video");
        poster.setSublabel("TMDB Rating: " + String.format("%.1f", poster.getRating()));
        poster.setClassification("TV-MA"); // Default for TV series
        
        return poster;
    }
    
    /**
     * Generate VidSrc source for TV episode
     */
    public Source generateTVEpisodeSource(int tmdbId, int season, int episode, String title) {
        Source source = new Source();
        source.setTitle(title + " - VidSrc");
        source.setUrl(VIDSRC_BASE_URL + "/tv/" + tmdbId + "/" + season + "/" + episode);
        source.setQuality("HD");
        source.setType("video");
        source.setExternal(true);
        return source;
    }
    
    /**
     * Callback interface for TMDB operations
     */
    public interface TMDBCallback {
        void onSuccess(Poster poster);
        void onError(String error);
    }
}