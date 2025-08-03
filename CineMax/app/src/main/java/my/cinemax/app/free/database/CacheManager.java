package my.cinemax.app.free.database;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import my.cinemax.app.free.database.entities.CachedEpisode;
import my.cinemax.app.free.database.entities.CachedMovie;
import my.cinemax.app.free.entity.Episode;
import my.cinemax.app.free.entity.Poster;

public class CacheManager {
    private static final String TAG = "CacheManager";
    private static CacheManager instance;
    private CineMaxDatabase database;

    private CacheManager(Context context) {
        database = CineMaxDatabase.getInstance(context);
    }

    public static CacheManager getInstance(Context context) {
        if (instance == null) {
            synchronized (CacheManager.class) {
                if (instance == null) {
                    instance = new CacheManager(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    // Movie caching methods
    public void cacheMovies(List<Poster> posters) {
        if (posters == null || posters.isEmpty()) return;

        List<CachedMovie> cachedMovies = new ArrayList<>();
        for (Poster poster : posters) {
            CachedMovie cachedMovie = convertPosterToCachedMovie(poster);
            if (cachedMovie != null) {
                cachedMovies.add(cachedMovie);
            }
        }

        if (!cachedMovies.isEmpty()) {
            database.movieDao().insertMovies(cachedMovies);
            Log.d(TAG, "Cached " + cachedMovies.size() + " movies");
        }
    }

    public void cacheMovie(Poster poster) {
        if (poster == null) return;

        CachedMovie cachedMovie = convertPosterToCachedMovie(poster);
        if (cachedMovie != null) {
            database.movieDao().insertMovie(cachedMovie);
            Log.d(TAG, "Cached movie: " + poster.getTitle());
        }
    }

    public List<CachedMovie> getCachedMovies(int type) {
        long validTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours ago
        List<CachedMovie> cachedMovies = database.movieDao().getValidCachedMovies(validTimestamp);
        
        List<CachedMovie> filteredMovies = new ArrayList<>();
        for (CachedMovie movie : cachedMovies) {
            if (movie.getType() == type) {
                filteredMovies.add(movie);
            }
        }
        
        Log.d(TAG, "Retrieved " + filteredMovies.size() + " valid cached movies of type " + type);
        return filteredMovies;
    }

    public CachedMovie getCachedMovie(String movieId) {
        CachedMovie movie = database.movieDao().getMovieById(movieId);
        if (movie != null && movie.isCacheValid()) {
            Log.d(TAG, "Retrieved cached movie: " + movie.getTitle());
            return movie;
        }
        return null;
    }

    // Episode caching methods
    public void cacheEpisodes(List<Episode> episodes, String serieId) {
        if (episodes == null || episodes.isEmpty()) return;

        List<CachedEpisode> cachedEpisodes = new ArrayList<>();
        for (Episode episode : episodes) {
            CachedEpisode cachedEpisode = convertEpisodeToCachedEpisode(episode, serieId);
            if (cachedEpisode != null) {
                cachedEpisodes.add(cachedEpisode);
            }
        }

        if (!cachedEpisodes.isEmpty()) {
            database.episodeDao().insertEpisodes(cachedEpisodes);
            Log.d(TAG, "Cached " + cachedEpisodes.size() + " episodes for serie " + serieId);
        }
    }

    public void cacheEpisode(Episode episode, String serieId) {
        if (episode == null) return;

        CachedEpisode cachedEpisode = convertEpisodeToCachedEpisode(episode, serieId);
        if (cachedEpisode != null) {
            database.episodeDao().insertEpisode(cachedEpisode);
            Log.d(TAG, "Cached episode: " + episode.getTitle());
        }
    }

    public List<CachedEpisode> getCachedEpisodes(String serieId) {
        long validTimestamp = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours ago
        List<CachedEpisode> allValidEpisodes = database.episodeDao().getValidCachedEpisodes(validTimestamp);
        
        List<CachedEpisode> serieEpisodes = new ArrayList<>();
        for (CachedEpisode episode : allValidEpisodes) {
            if (serieId.equals(episode.getSerieId())) {
                serieEpisodes.add(episode);
            }
        }
        
        Log.d(TAG, "Retrieved " + serieEpisodes.size() + " valid cached episodes for serie " + serieId);
        return serieEpisodes;
    }

    public List<CachedEpisode> getCachedEpisodesBySeason(String serieId, int seasonNumber) {
        List<CachedEpisode> episodes = database.episodeDao().getEpisodesBySerieAndSeason(serieId, seasonNumber);
        
        List<CachedEpisode> validEpisodes = new ArrayList<>();
        for (CachedEpisode episode : episodes) {
            if (episode.isCacheValid()) {
                validEpisodes.add(episode);
            }
        }
        
        Log.d(TAG, "Retrieved " + validEpisodes.size() + " valid cached episodes for serie " + serieId + " season " + seasonNumber);
        return validEpisodes;
    }

    // Cache management methods
    public void clearExpiredCache() {
        database.clearExpiredCache();
        Log.d(TAG, "Cleared expired cache");
    }

    public void clearAllCache() {
        database.clearAllCache();
        Log.d(TAG, "Cleared all cache");
    }

    public String getCacheStats() {
        return database.getCacheStats();
    }

    public boolean hasCachedMovies(int type) {
        List<CachedMovie> movies = getCachedMovies(type);
        return !movies.isEmpty();
    }

    public boolean hasCachedEpisodes(String serieId) {
        List<CachedEpisode> episodes = getCachedEpisodes(serieId);
        return !episodes.isEmpty();
    }

    // Conversion methods
    private CachedMovie convertPosterToCachedMovie(Poster poster) {
        if (poster == null || poster.getId() == null) return null;

        CachedMovie cachedMovie = new CachedMovie();
        cachedMovie.setId(poster.getId());
        cachedMovie.setTitle(poster.getTitle());
        cachedMovie.setOverview(poster.getOverview());
        cachedMovie.setPosterPath(poster.getImage());
        cachedMovie.setBackdropPath(poster.getCover());
        cachedMovie.setReleaseDate(poster.getCreatedAt());
        cachedMovie.setGenreId(poster.getGenreId());
        cachedMovie.setRating(poster.getRating());
        cachedMovie.setType(poster.getType());
        cachedMovie.setEmbedUrl(poster.getEmbed());
        cachedMovie.setTrailerUrl(poster.getTrailer());
        cachedMovie.setDuration(poster.getDuration());
        cachedMovie.setYear(poster.getYear());
        cachedMovie.setClassification(poster.getClassification());
        cachedMovie.setFeatured(poster.getFeatured());
        cachedMovie.setCreatedAt(poster.getCreatedAt());

        return cachedMovie;
    }

    private CachedEpisode convertEpisodeToCachedEpisode(Episode episode, String serieId) {
        if (episode == null || episode.getId() == null) return null;

        CachedEpisode cachedEpisode = new CachedEpisode();
        cachedEpisode.setId(episode.getId());
        cachedEpisode.setTitle(episode.getTitle());
        cachedEpisode.setEpisodeNumber(episode.getEpisodeNumber());
        cachedEpisode.setSeasonNumber(episode.getSeasonNumber());
        cachedEpisode.setSerieId(serieId);
        cachedEpisode.setOverview(episode.getOverview());
        cachedEpisode.setStillPath(episode.getStillPath());
        cachedEpisode.setEmbedUrl(episode.getEmbed());
        cachedEpisode.setDuration(episode.getDuration());
        cachedEpisode.setCreatedAt(episode.getCreatedAt());

        return cachedEpisode;
    }

    // Convert cached entities back to original entities for UI
    public List<Poster> convertCachedMoviesToPosters(List<CachedMovie> cachedMovies) {
        List<Poster> posters = new ArrayList<>();
        for (CachedMovie cachedMovie : cachedMovies) {
            Poster poster = convertCachedMovieToPoster(cachedMovie);
            if (poster != null) {
                posters.add(poster);
            }
        }
        return posters;
    }

    private Poster convertCachedMovieToPoster(CachedMovie cachedMovie) {
        if (cachedMovie == null) return null;

        Poster poster = new Poster();
        poster.setId(cachedMovie.getId());
        poster.setTitle(cachedMovie.getTitle());
        poster.setOverview(cachedMovie.getOverview());
        poster.setImage(cachedMovie.getPosterPath());
        poster.setCover(cachedMovie.getBackdropPath());
        poster.setGenreId(cachedMovie.getGenreId());
        poster.setRating(cachedMovie.getRating());
        poster.setType(cachedMovie.getType());
        poster.setEmbed(cachedMovie.getEmbedUrl());
        poster.setTrailer(cachedMovie.getTrailerUrl());
        poster.setDuration(cachedMovie.getDuration());
        poster.setYear(cachedMovie.getYear());
        poster.setClassification(cachedMovie.getClassification());
        poster.setFeatured(cachedMovie.getFeatured());
        poster.setCreatedAt(cachedMovie.getCreatedAt());

        return poster;
    }

    public List<Episode> convertCachedEpisodesToEpisodes(List<CachedEpisode> cachedEpisodes) {
        List<Episode> episodes = new ArrayList<>();
        for (CachedEpisode cachedEpisode : cachedEpisodes) {
            Episode episode = convertCachedEpisodeToEpisode(cachedEpisode);
            if (episode != null) {
                episodes.add(episode);
            }
        }
        return episodes;
    }

    private Episode convertCachedEpisodeToEpisode(CachedEpisode cachedEpisode) {
        if (cachedEpisode == null) return null;

        Episode episode = new Episode();
        episode.setId(cachedEpisode.getId());
        episode.setTitle(cachedEpisode.getTitle());
        episode.setEpisodeNumber(cachedEpisode.getEpisodeNumber());
        episode.setSeasonNumber(cachedEpisode.getSeasonNumber());
        episode.setOverview(cachedEpisode.getOverview());
        episode.setStillPath(cachedEpisode.getStillPath());
        episode.setEmbed(cachedEpisode.getEmbedUrl());
        episode.setDuration(cachedEpisode.getDuration());
        episode.setCreatedAt(cachedEpisode.getCreatedAt());

        return episode;
    }
}