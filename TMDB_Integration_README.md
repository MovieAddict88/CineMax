# TMDB Integration for CineMax App

## Overview

This implementation integrates **The Movie Database (TMDB) API** with the CineMax app to automatically fetch and populate rich metadata for movies and TV series while preserving streaming sources from **vidsrc.net** and other platforms.

## Features Implemented

### ✅ Enhanced Metadata Fields
- **TMDB ID**: Unique identifier from TMDB
- **Rating & Reviews**: TMDB ratings, vote counts, popularity scores
- **Cast & Crew**: Actor/actress information with photos
- **Countries**: Production countries and origin countries
- **Duration**: Runtime for movies, episode duration for TV series
- **Posters & Backdrops**: High-quality TMDB images
- **Thumbnails**: Episode and season thumbnails
- **Seasons & Episodes**: Complete TV series structure
- **Descriptions**: Rich, detailed plot summaries
- **Release Dates**: Accurate release information
- **Genres**: Complete genre categorization
- **Production Info**: Companies, networks, languages
- **Status**: Released, upcoming, ended, etc.

### ✅ Streaming Integration
- **VidSrc.net Support**: Embedded player integration
- **Multiple Sources**: Direct links, embedded players
- **Quality Options**: 1080p, 720p, 480p support
- **TV Series Episodes**: Individual episode streaming

## Implementation Structure

### 1. Enhanced Entity Classes

#### `Poster.java` - Main Content Entity
```java
// New TMDB fields added:
@SerializedName("tmdb_id")
private Integer tmdbId;

@SerializedName("country") 
private String country;

@SerializedName("backdrop")
private String backdrop;

@SerializedName("seasons")
private List<Season> seasons;

// ... and many more TMDB fields
```

#### `Season.java` & `Episode.java` - TV Series Structure
- Parcelable implementation for Android compatibility
- Support for multiple seasons with episodes
- Individual episode metadata and streaming sources

### 2. TMDB Service Layer

#### `TMDBService.java` - Core TMDB Integration
```java
// Enrich movie with TMDB data
TMDBService.enrichMovieWithTMDB(poster, movieTitle)

// Enrich TV series with TMDB data  
TMDBService.enrichTVSeriesWithTMDB(poster, seriesTitle)
```

**Key Features:**
- Automatic metadata fetching from TMDB API
- Cast and crew information with photos
- High-quality poster and backdrop images
- Production company and network details
- Genre and language information

#### `TMDBHelper.java` - Usage Examples
```java
// Example: Enrich any movie
TMDBHelper.enrichAnyMovie("Avengers", new TMDBEnrichmentCallback() {
    @Override
    public void onSuccess(Poster enrichedPoster) {
        // Movie now has full TMDB metadata + vidsrc.net sources
        TMDBHelper.logEnrichedPosterInfo(enrichedPoster);
    }
    
    @Override
    public void onError(String error) {
        Log.e(TAG, "Failed to enrich: " + error);
    }
});
```

### 3. Complete JSON Structure

#### `free_movie_api.json` - Enhanced API Response
```json
{
  "movies": [
    {
      "id": 1,
      "title": "Big Buck Bunny",
      "tmdb_id": null,
      "country": "Netherlands", 
      "backdrop": "https://image.tmdb.org/t/p/w1280/...",
      "poster_tmdb": "https://image.tmdb.org/t/p/w500/...",
      "rating": 7.2,
      "vote_count": 125,
      "popularity": 15.5,
      "actors": [...],
      "genres": [...],
      "sources": [
        {
          "type": "embed",
          "title": "VidSrc Player", 
          "url": "https://vidsrc.net/embed/movie/bigbuckbunny"
        }
      ]
    }
  ]
}
```

## Usage Examples

### 1. Movie Integration
```java
// Create basic movie poster with minimal data
Poster movie = new Poster();
movie.setTitle("The Dark Knight");
movie.setType("movie");

// Enrich with TMDB data
TMDBService.enrichMovieWithTMDB(movie, "The Dark Knight");

// Result: Full metadata + streaming sources
// - TMDB ID, rating, cast, crew
// - High-quality posters and backdrops  
// - VidSrc.net streaming URL: https://vidsrc.net/embed/movie/{tmdb_id}
```

### 2. TV Series Integration  
```java
// Create basic TV series poster
Poster series = new Poster();
series.setTitle("Breaking Bad");
series.setType("tv");

// Enrich with TMDB data
TMDBService.enrichTVSeriesWithTMDB(series, "Breaking Bad");

// Result: Complete TV series structure
// - Season and episode information
// - Cast, crew, network details
// - Episode streaming: https://vidsrc.net/embed/tv/{tmdb_id}/{season}/{episode}
```

### 3. Integration Workflow
```java
// 1. Parse basic content from your source
Poster content = parseContentFromSource();

// 2. Enrich with TMDB metadata
if (content.getType().equals("movie")) {
    TMDBService.enrichMovieWithTMDB(content, content.getTitle());
} else {
    TMDBService.enrichTVSeriesWithTMDB(content, content.getTitle());
}

// 3. Add streaming sources
addStreamingSources(content);

// 4. Content now has rich metadata + streaming capability
```

## API Configuration

### TMDB API Setup
```java
// In TMDBService.java
private static final String API_KEY = "ec926176bf467b3f7735e3154238c161";
private static final String BASE_URL = "https://api.themoviedb.org/3";
```

### VidSrc.net Integration
```java
// Movie URL format
https://vidsrc.net/embed/movie/{tmdb_id}

// TV Series URL format  
https://vidsrc.net/embed/tv/{tmdb_id}/{season}/{episode}
```

## JSON Structure Examples

### Movie Example (Big Buck Bunny)
```json
{
  "id": 1,
  "title": "Big Buck Bunny",
  "type": "movie",
  "tmdb_id": null,
  "country": "Netherlands",
  "backdrop": "https://peach.blender.org/wp-content/uploads/bbb-splash.png",
  "poster_tmdb": "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg",
  "rating": 7.2,
  "vote_count": 125,
  "popularity": 15.5,
  "duration": "10 min",
  "release_date": "2008-04-10",
  "description": "Big Buck Bunny is a comedy about a well-tempered rabbit...",
  "genres": [
    {"id": 16, "title": "Animation"},
    {"id": 35, "title": "Comedy"}
  ],
  "actors": [
    {
      "id": 1,
      "name": "Sacha Goedegebure", 
      "title": "Big Buck Bunny (voice)",
      "image": "https://image.tmdb.org/t/p/w500/default_actor.jpg"
    }
  ],
  "sources": [
    {
      "type": "embed",
      "title": "VidSrc Player",
      "url": "https://vidsrc.net/embed/movie/bigbuckbunny",
      "external": true
    }
  ]
}
```

### TV Series Example (Breaking Bad)
```json
{
  "id": 2,
  "title": "Breaking Bad",
  "type": "tv", 
  "tmdb_id": 1396,
  "country": "United States",
  "first_air_date": "2008-01-20",
  "last_air_date": "2013-09-29",
  "number_of_seasons": 2,
  "number_of_episodes": 2,
  "status": "Ended",
  "networks": ["AMC"],
  "seasons": [
    {
      "id": 1,
      "title": "Season 1",
      "episodes": [
        {
          "id": 1,
          "title": "Pilot",
          "description": "Walter White, a struggling high school chemistry teacher...",
          "duration": "58 min",
          "sources": [
            {
              "type": "embed",
              "url": "https://vidsrc.net/embed/tv/1396/1/1"
            }
          ]
        }
      ]
    }
  ]
}
```

## Benefits

### 🎯 Rich Metadata
- **Accurate Information**: TMDB provides verified, comprehensive metadata
- **High-Quality Images**: Professional posters, backdrops, and actor photos
- **Complete Cast Info**: Actor names, character roles, and profile images
- **Detailed Descriptions**: Rich plot summaries and episode descriptions

### 🎬 Streaming Integration
- **VidSrc.net Compatibility**: Seamless embed player integration
- **Multiple Sources**: Support for various streaming platforms
- **TV Series Support**: Individual episode streaming with season/episode structure
- **Quality Options**: Multiple resolution support (1080p, 720p, 480p)

### 📱 App Enhancement
- **Better User Experience**: Rich content discovery with detailed information
- **Professional Appearance**: High-quality images and consistent data formatting
- **Search & Filter**: Enhanced search capabilities with genre, actor, and country filters
- **Content Organization**: Proper categorization and rating system

## Implementation Notes

### Error Handling
- Graceful fallback when TMDB data is unavailable
- Preserve existing metadata if TMDB enrichment fails
- Network timeout handling for API calls

### Performance Considerations
- Asynchronous TMDB API calls to prevent UI blocking
- Image URL caching recommendations
- Batch processing for multiple content items

### Data Preservation
- Original streaming sources are always preserved
- Existing metadata is only enhanced, not replaced
- Multiple source support maintains compatibility

## Future Enhancements

1. **Caching System**: Implement local TMDB data caching
2. **Batch Processing**: Bulk metadata enrichment
3. **User Preferences**: Customizable metadata sources
4. **Offline Support**: Cached metadata for offline viewing
5. **Additional Providers**: Support for more streaming platforms

## API Key Security

**Important**: The TMDB API key included (`ec926176bf467b3f7735e3154238c161`) is for demonstration purposes. In production:

1. Store API keys securely (Android Keystore, encrypted preferences)
2. Consider server-side proxy for API calls
3. Implement rate limiting and usage monitoring
4. Rotate keys regularly for security

## Conclusion

This TMDB integration provides a complete solution for enriching movie and TV series metadata while maintaining compatibility with existing streaming sources. The implementation supports both movies and TV series with comprehensive metadata, high-quality images, and seamless vidsrc.net integration for streaming.