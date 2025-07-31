package my.cinemax.app.free.Utils;

import my.cinemax.app.free.entity.TmdbMovieResponse;
import my.cinemax.app.free.entity.TmdbTvResponse;
import my.cinemax.app.free.entity.TmdbSearchResponse;

/**
 * Temporary test helper to verify TMDB imports are working
 * This class can be deleted after compilation is successful
 */
public class TmdbTestHelper {
    
    public static boolean testImports() {
        try {
            // Test that we can create instances of the TMDB classes
            TmdbMovieResponse movieResponse = new TmdbMovieResponse();
            TmdbTvResponse tvResponse = new TmdbTvResponse();
            TmdbSearchResponse searchResponse = new TmdbSearchResponse();
            
            // Test that we can access the classes
            if (movieResponse != null && tvResponse != null && searchResponse != null) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }
}