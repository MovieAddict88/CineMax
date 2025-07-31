package my.cinemax.app.free.api;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.entity.Slide;

/**
 * TMDB Manager to integrate TMDB auto-detection with existing API
 * Enhances movies and TV series with TMDB metadata
 */
public class TMDBManager {
    private static final String TAG = "TMDBManager";
    private TMDBService tmdbService;
    
    public TMDBManager() {
        this.tmdbService = new TMDBService();
    }
    
    /**
     * Enhance API response with TMDB data
     */
    public void enhanceApiResponseWithTMDB(JsonApiResponse apiResponse, TMDBEnhancementCallback callback) {
        if (apiResponse == null || apiResponse.getHome() == null) {
            callback.onComplete(apiResponse);
            return;
        }
        
        List<Slide> slides = apiResponse.getHome().getSlides();
        if (slides == null || slides.isEmpty()) {
            callback.onComplete(apiResponse);
            return;
        }
        
        // Track completion of TMDB enhancements
        final int totalSlides = slides.size();
        final int[] completedSlides = {0};
        final List<Slide> enhancedSlides = new ArrayList<>();
        
        for (int i = 0; i < slides.size(); i++) {
            final int slideIndex = i;
            Slide slide = slides.get(i);
            
            if (slide.getPoster() != null && shouldEnhanceWithTMDB(slide.getPoster())) {
                Log.d(TAG, "Enhancing " + slide.getTitle() + " with TMDB data");
                
                tmdbService.enhancePosterWithTMDB(slide.getPoster(), new TMDBService.TMDBCallback() {
                    @Override
                    public void onSuccess(Poster enhancedPoster) {
                        Log.d(TAG, "TMDB enhancement successful for: " + enhancedPoster.getTitle());
                        
                        // Create enhanced slide
                        Slide enhancedSlide = new Slide();
                        enhancedSlide.setId(slide.getId());
                        enhancedSlide.setTitle(enhancedPoster.getTitle());
                        enhancedSlide.setType(enhancedPoster.getType());
                        enhancedSlide.setImage(""); // No images as requested
                        enhancedSlide.setUrl(slide.getUrl());
                        enhancedSlide.setPoster(enhancedPoster);
                        
                        synchronized (enhancedSlides) {
                            // Maintain order by setting at specific index
                            while (enhancedSlides.size() <= slideIndex) {
                                enhancedSlides.add(null);
                            }
                            enhancedSlides.set(slideIndex, enhancedSlide);
                            completedSlides[0]++;
                            
                            if (completedSlides[0] == totalSlides) {
                                finalizeEnhancement(apiResponse, enhancedSlides, callback);
                            }
                        }
                    }
                    
                    @Override
                    public void onError(String error) {
                        Log.w(TAG, "TMDB enhancement failed for " + slide.getTitle() + ": " + error);
                        
                        synchronized (enhancedSlides) {
                            // Keep original slide on error
                            while (enhancedSlides.size() <= slideIndex) {
                                enhancedSlides.add(null);
                            }
                            enhancedSlides.set(slideIndex, slide);
                            completedSlides[0]++;
                            
                            if (completedSlides[0] == totalSlides) {
                                finalizeEnhancement(apiResponse, enhancedSlides, callback);
                            }
                        }
                    }
                });
            } else {
                // Skip TMDB enhancement for live TV or unsupported content
                synchronized (enhancedSlides) {
                    while (enhancedSlides.size() <= slideIndex) {
                        enhancedSlides.add(null);
                    }
                    enhancedSlides.set(slideIndex, slide);
                    completedSlides[0]++;
                    
                    if (completedSlides[0] == totalSlides) {
                        finalizeEnhancement(apiResponse, enhancedSlides, callback);
                    }
                }
            }
        }
    }
    
    /**
     * Enhance a single poster with TMDB data
     */
    public void enhancePosterWithTMDB(Poster poster, TMDBService.TMDBCallback callback) {
        if (shouldEnhanceWithTMDB(poster)) {
            tmdbService.enhancePosterWithTMDB(poster, callback);
        } else {
            callback.onError("Content not suitable for TMDB enhancement");
        }
    }
    
    /**
     * Search and create new movie poster from TMDB
     */
    public void createMovieFromTMDB(String title, String year, TMDBService.TMDBCallback callback) {
        tmdbService.searchAndPopulateMovie(title, year, callback);
    }
    
    /**
     * Search and create new TV series poster from TMDB
     */
    public void createTVSeriesFromTMDB(String title, String year, TMDBService.TMDBCallback callback) {
        tmdbService.searchAndPopulateTVSeries(title, year, callback);
    }
    
    /**
     * Auto-enhance popular content with TMDB data
     * This method can be called to enhance existing content automatically
     */
    public void autoEnhancePopularContent(JsonApiResponse apiResponse, TMDBEnhancementCallback callback) {
        // List of popular movies and TV series to auto-enhance
        String[][] popularMovies = {
            {"The Avengers", "2012"},
            {"Spider-Man: No Way Home", "2021"},
            {"Avatar", "2009"},
            {"Avengers: Endgame", "2019"},
            {"Black Panther", "2018"}
        };
        
        String[][] popularTVSeries = {
            {"Breaking Bad", "2008"},
            {"Game of Thrones", "2011"},
            {"Stranger Things", "2016"},
            {"The Mandalorian", "2019"},
            {"Wednesday", "2022"}
        };
        
        // Replace existing content with popular TMDB content
        List<Slide> enhancedSlides = new ArrayList<>();
        final int totalContent = Math.min(2, popularMovies.length + popularTVSeries.length);
        final int[] completedContent = {0};
        
        // Add one popular movie
        if (popularMovies.length > 0) {
            String[] movie = popularMovies[0]; // The Avengers
            createMovieFromTMDB(movie[0], movie[1], new TMDBService.TMDBCallback() {
                @Override
                public void onSuccess(Poster poster) {
                    Slide slide = new Slide();
                    slide.setId(1);
                    slide.setTitle(poster.getTitle());
                    slide.setType("movie");
                    slide.setImage(""); // No images
                    slide.setUrl("movies/1");
                    slide.setPoster(poster);
                    
                    synchronized (enhancedSlides) {
                        enhancedSlides.add(slide);
                        completedContent[0]++;
                        
                        if (completedContent[0] == totalContent) {
                            updateApiResponseWithEnhancedContent(apiResponse, enhancedSlides, callback);
                        }
                    }
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "Failed to create movie from TMDB: " + error);
                    synchronized (enhancedSlides) {
                        completedContent[0]++;
                        if (completedContent[0] == totalContent) {
                            updateApiResponseWithEnhancedContent(apiResponse, enhancedSlides, callback);
                        }
                    }
                }
            });
        }
        
        // Add one popular TV series
        if (popularTVSeries.length > 0) {
            String[] tvSeries = popularTVSeries[0]; // Breaking Bad
            createTVSeriesFromTMDB(tvSeries[0], tvSeries[1], new TMDBService.TMDBCallback() {
                @Override
                public void onSuccess(Poster poster) {
                    Slide slide = new Slide();
                    slide.setId(2);
                    slide.setTitle(poster.getTitle());
                    slide.setType("series");
                    slide.setImage(""); // No images
                    slide.setUrl("series/2");
                    slide.setPoster(poster);
                    
                    synchronized (enhancedSlides) {
                        enhancedSlides.add(slide);
                        completedContent[0]++;
                        
                        if (completedContent[0] == totalContent) {
                            updateApiResponseWithEnhancedContent(apiResponse, enhancedSlides, callback);
                        }
                    }
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "Failed to create TV series from TMDB: " + error);
                    synchronized (enhancedSlides) {
                        completedContent[0]++;
                        if (completedContent[0] == totalContent) {
                            updateApiResponseWithEnhancedContent(apiResponse, enhancedSlides, callback);
                        }
                    }
                }
            });
        }
    }
    
    private boolean shouldEnhanceWithTMDB(Poster poster) {
        if (poster == null) return false;
        
        // Only enhance movies and TV series, not live TV
        String type = poster.getType();
        return "movie".equals(type) || "series".equals(type);
    }
    
    private void finalizeEnhancement(JsonApiResponse apiResponse, List<Slide> enhancedSlides, TMDBEnhancementCallback callback) {
        // Remove null entries and update API response
        List<Slide> finalSlides = new ArrayList<>();
        for (Slide slide : enhancedSlides) {
            if (slide != null) {
                finalSlides.add(slide);
            }
        }
        
        if (apiResponse.getHome() != null) {
            apiResponse.getHome().setSlides(finalSlides);
        }
        
        Log.d(TAG, "TMDB enhancement completed. Enhanced " + finalSlides.size() + " slides");
        callback.onComplete(apiResponse);
    }
    
    private void updateApiResponseWithEnhancedContent(JsonApiResponse apiResponse, List<Slide> enhancedSlides, TMDBEnhancementCallback callback) {
        if (apiResponse.getHome() != null) {
            // Preserve existing live TV and other content, but replace movies/series
            List<Slide> existingSlides = apiResponse.getHome().getSlides();
            List<Slide> finalSlides = new ArrayList<>();
            
            // Add enhanced content first
            finalSlides.addAll(enhancedSlides);
            
            // Add any existing live TV or other non-movie/series content
            if (existingSlides != null) {
                for (Slide slide : existingSlides) {
                    if (slide.getPoster() != null && !shouldEnhanceWithTMDB(slide.getPoster())) {
                        finalSlides.add(slide);
                    }
                }
            }
            
            apiResponse.getHome().setSlides(finalSlides);
        }
        
        Log.d(TAG, "Auto-enhancement completed with popular TMDB content");
        callback.onComplete(apiResponse);
    }
    
    /**
     * Callback interface for TMDB enhancement operations
     */
    public interface TMDBEnhancementCallback {
        void onComplete(JsonApiResponse enhancedResponse);
    }
}