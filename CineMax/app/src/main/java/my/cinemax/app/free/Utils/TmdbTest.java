package my.cinemax.app.free.Utils;

import my.cinemax.app.free.entity.TmdbMovieResponse;
import my.cinemax.app.free.entity.TmdbTvResponse;
import my.cinemax.app.free.entity.TmdbSearchResponse;

public class TmdbTest {
    
    public static void testImports() {
        // Test that we can create instances of the TMDB classes
        TmdbMovieResponse movieResponse = new TmdbMovieResponse();
        TmdbTvResponse tvResponse = new TmdbTvResponse();
        TmdbSearchResponse searchResponse = new TmdbSearchResponse();
        
        System.out.println("TMDB imports working correctly!");
    }
}