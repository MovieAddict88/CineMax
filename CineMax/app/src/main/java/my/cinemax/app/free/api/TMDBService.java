package my.cinemax.app.free.api;

import android.util.Log;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Season;
import my.cinemax.app.free.entity.Episode;
import my.cinemax.app.free.entity.Actor;
import my.cinemax.app.free.entity.Genre;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class TMDBService {
    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";
    private static final String BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w1280";
    private static final String API_KEY = "ec926176bf467b3f7735e3154238c161";
    private static final String TAG = "TMDBService";

    /**
     * Search for a movie by title and enrich the Poster object with TMDB data
     */
    public static boolean enrichMovieWithTMDB(Poster poster, String movieTitle) {
        try {
            // Search for the movie
            String searchUrl = BASE_URL + "/search/movie?api_key=" + API_KEY + "&query=" + 
                            movieTitle.replace(" ", "%20");
            
            JsonObject searchResponse = makeAPICall(searchUrl);
            if (searchResponse == null) return false;

            JsonArray results = searchResponse.getAsJsonArray("results");
            if (results.size() == 0) return false;

            // Get the first result (most relevant)
            JsonObject movieData = results.get(0).getAsJsonObject();
            int tmdbId = movieData.get("id").getAsInt();

            // Get detailed movie information
            String detailsUrl = BASE_URL + "/movie/" + tmdbId + "?api_key=" + API_KEY + 
                               "&append_to_response=credits,videos";
            
            JsonObject movieDetails = makeAPICall(detailsUrl);
            if (movieDetails == null) return false;

            // Populate TMDB data
            populateMovieData(poster, movieDetails);
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error enriching movie with TMDB data: " + e.getMessage());
            return false;
        }
    }

    /**
     * Search for a TV series by title and enrich the Poster object with TMDB data
     */
    public static boolean enrichTVSeriesWithTMDB(Poster poster, String seriesTitle) {
        try {
            // Search for the TV series
            String searchUrl = BASE_URL + "/search/tv?api_key=" + API_KEY + "&query=" + 
                            seriesTitle.replace(" ", "%20");
            
            JsonObject searchResponse = makeAPICall(searchUrl);
            if (searchResponse == null) return false;

            JsonArray results = searchResponse.getAsJsonArray("results");
            if (results.size() == 0) return false;

            // Get the first result (most relevant)
            JsonObject tvData = results.get(0).getAsJsonObject();
            int tmdbId = tvData.get("id").getAsInt();

            // Get detailed TV series information
            String detailsUrl = BASE_URL + "/tv/" + tmdbId + "?api_key=" + API_KEY + 
                               "&append_to_response=credits,videos";
            
            JsonObject tvDetails = makeAPICall(detailsUrl);
            if (tvDetails == null) return false;

            // Populate TMDB data for TV series
            populateTVSeriesData(poster, tvDetails);
            
            return true;
        } catch (Exception e) {
            Log.e(TAG, "Error enriching TV series with TMDB data: " + e.getMessage());
            return false;
        }
    }

    private static void populateMovieData(Poster poster, JsonObject movieDetails) {
        try {
            // Basic information
            if (movieDetails.has("id"))
                poster.setTmdbId(movieDetails.get("id").getAsInt());
                
            if (movieDetails.has("original_title"))
                poster.setOriginalTitle(movieDetails.get("original_title").getAsString());
                
            if (movieDetails.has("original_language"))
                poster.setOriginalLanguage(movieDetails.get("original_language").getAsString());
                
            if (movieDetails.has("overview"))
                poster.setDescription(movieDetails.get("overview").getAsString());
                
            if (movieDetails.has("release_date"))
                poster.setReleaseDate(movieDetails.get("release_date").getAsString());
                
            if (movieDetails.has("runtime"))
                poster.setDuration(movieDetails.get("runtime").getAsInt() + " min");
                
            if (movieDetails.has("vote_average"))
                poster.setRating(movieDetails.get("vote_average").getAsFloat());
                
            if (movieDetails.has("vote_count"))
                poster.setVoteCount(movieDetails.get("vote_count").getAsInt());
                
            if (movieDetails.has("popularity"))
                poster.setPopularity(movieDetails.get("popularity").getAsFloat());
                
            if (movieDetails.has("adult"))
                poster.setAdult(movieDetails.get("adult").getAsBoolean());

            // Images
            if (movieDetails.has("poster_path") && !movieDetails.get("poster_path").isJsonNull())
                poster.setPosterTmdb(IMAGE_BASE_URL + movieDetails.get("poster_path").getAsString());
                
            if (movieDetails.has("backdrop_path") && !movieDetails.get("backdrop_path").isJsonNull())
                poster.setBackdrop(BACKDROP_BASE_URL + movieDetails.get("backdrop_path").getAsString());

            // Production countries
            if (movieDetails.has("production_countries")) {
                JsonArray countries = movieDetails.getAsJsonArray("production_countries");
                if (countries.size() > 0) {
                    poster.setCountry(countries.get(0).getAsJsonObject().get("name").getAsString());
                }
            }

            // Genres
            if (movieDetails.has("genres")) {
                List<Genre> genres = new ArrayList<>();
                JsonArray genreArray = movieDetails.getAsJsonArray("genres");
                for (int i = 0; i < genreArray.size(); i++) {
                    JsonObject genreObj = genreArray.get(i).getAsJsonObject();
                    Genre genre = new Genre();
                    genre.setId(genreObj.get("id").getAsInt());
                    genre.setTitle(genreObj.get("name").getAsString());
                    genres.add(genre);
                }
                poster.setGenres(genres);
            }

            // Production companies
            if (movieDetails.has("production_companies")) {
                List<String> companies = new ArrayList<>();
                JsonArray companyArray = movieDetails.getAsJsonArray("production_companies");
                for (int i = 0; i < companyArray.size(); i++) {
                    JsonObject companyObj = companyArray.get(i).getAsJsonObject();
                    companies.add(companyObj.get("name").getAsString());
                }
                poster.setProductionCompanies(companies);
            }

            // Cast information
            if (movieDetails.has("credits")) {
                JsonObject credits = movieDetails.getAsJsonObject("credits");
                if (credits.has("cast")) {
                    List<Actor> actors = new ArrayList<>();
                    JsonArray castArray = credits.getAsJsonArray("cast");
                    
                    // Get top 10 actors
                    int maxActors = Math.min(10, castArray.size());
                    for (int i = 0; i < maxActors; i++) {
                        JsonObject castObj = castArray.get(i).getAsJsonObject();
                        Actor actor = new Actor();
                        actor.setId(castObj.get("id").getAsInt());
                        actor.setName(castObj.get("name").getAsString());
                        actor.setTitle(castObj.get("character").getAsString()); // Character name
                        if (castObj.has("profile_path") && !castObj.get("profile_path").isJsonNull()) {
                            actor.setImage(IMAGE_BASE_URL + castObj.get("profile_path").getAsString());
                        }
                        actors.add(actor);
                    }
                    poster.setActors(actors);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error populating movie data: " + e.getMessage());
        }
    }

    private static void populateTVSeriesData(Poster poster, JsonObject tvDetails) {
        try {
            // Basic information
            if (tvDetails.has("id"))
                poster.setTmdbId(tvDetails.get("id").getAsInt());
                
            if (tvDetails.has("original_name"))
                poster.setOriginalTitle(tvDetails.get("original_name").getAsString());
                
            if (tvDetails.has("original_language"))
                poster.setOriginalLanguage(tvDetails.get("original_language").getAsString());
                
            if (tvDetails.has("overview"))
                poster.setDescription(tvDetails.get("overview").getAsString());
                
            if (tvDetails.has("first_air_date"))
                poster.setFirstAirDate(tvDetails.get("first_air_date").getAsString());
                
            if (tvDetails.has("last_air_date"))
                poster.setLastAirDate(tvDetails.get("last_air_date").getAsString());
                
            if (tvDetails.has("number_of_seasons"))
                poster.setNumberOfSeasons(tvDetails.get("number_of_seasons").getAsInt());
                
            if (tvDetails.has("number_of_episodes"))
                poster.setNumberOfEpisodes(tvDetails.get("number_of_episodes").getAsInt());
                
            if (tvDetails.has("status"))
                poster.setStatus(tvDetails.get("status").getAsString());
                
            if (tvDetails.has("vote_average"))
                poster.setRating(tvDetails.get("vote_average").getAsFloat());
                
            if (tvDetails.has("vote_count"))
                poster.setVoteCount(tvDetails.get("vote_count").getAsInt());
                
            if (tvDetails.has("popularity"))
                poster.setPopularity(tvDetails.get("popularity").getAsFloat());

            // Images
            if (tvDetails.has("poster_path") && !tvDetails.get("poster_path").isJsonNull())
                poster.setPosterTmdb(IMAGE_BASE_URL + tvDetails.get("poster_path").getAsString());
                
            if (tvDetails.has("backdrop_path") && !tvDetails.get("backdrop_path").isJsonNull())
                poster.setBackdrop(BACKDROP_BASE_URL + tvDetails.get("backdrop_path").getAsString());

            // Origin country
            if (tvDetails.has("origin_country")) {
                JsonArray countries = tvDetails.getAsJsonArray("origin_country");
                if (countries.size() > 0) {
                    poster.setCountry(countries.get(0).getAsString());
                }
            }

            // Networks
            if (tvDetails.has("networks")) {
                List<String> networks = new ArrayList<>();
                JsonArray networkArray = tvDetails.getAsJsonArray("networks");
                for (int i = 0; i < networkArray.size(); i++) {
                    JsonObject networkObj = networkArray.get(i).getAsJsonObject();
                    networks.add(networkObj.get("name").getAsString());
                }
                poster.setNetworks(networks);
            }

            // Genres (same as movies)
            if (tvDetails.has("genres")) {
                List<Genre> genres = new ArrayList<>();
                JsonArray genreArray = tvDetails.getAsJsonArray("genres");
                for (int i = 0; i < genreArray.size(); i++) {
                    JsonObject genreObj = genreArray.get(i).getAsJsonObject();
                    Genre genre = new Genre();
                    genre.setId(genreObj.get("id").getAsInt());
                    genre.setTitle(genreObj.get("name").getAsString());
                    genres.add(genre);
                }
                poster.setGenres(genres);
            }

            // Cast information (same as movies)
            if (tvDetails.has("credits")) {
                JsonObject credits = tvDetails.getAsJsonObject("credits");
                if (credits.has("cast")) {
                    List<Actor> actors = new ArrayList<>();
                    JsonArray castArray = credits.getAsJsonArray("cast");
                    
                    int maxActors = Math.min(10, castArray.size());
                    for (int i = 0; i < maxActors; i++) {
                        JsonObject castObj = castArray.get(i).getAsJsonObject();
                        Actor actor = new Actor();
                        actor.setId(castObj.get("id").getAsInt());
                        actor.setName(castObj.get("name").getAsString());
                        actor.setTitle(castObj.get("character").getAsString());
                        if (castObj.has("profile_path") && !castObj.get("profile_path").isJsonNull()) {
                            actor.setImage(IMAGE_BASE_URL + castObj.get("profile_path").getAsString());
                        }
                        actors.add(actor);
                    }
                    poster.setActors(actors);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "Error populating TV series data: " + e.getMessage());
        }
    }

    private static JsonObject makeAPICall(String urlString) {
        try {
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                Scanner scanner = new Scanner(connection.getInputStream());
                StringBuilder response = new StringBuilder();
                while (scanner.hasNext()) {
                    response.append(scanner.nextLine());
                }
                scanner.close();

                Gson gson = new Gson();
                return gson.fromJson(response.toString(), JsonObject.class);
            } else {
                Log.e(TAG, "HTTP Error: " + responseCode);
                return null;
            }
        } catch (IOException e) {
            Log.e(TAG, "Network error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            Log.e(TAG, "Error making API call: " + e.getMessage());
            return null;
        }
    }
}