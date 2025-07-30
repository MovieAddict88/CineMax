# Separated JSON API Implementation

This document explains the implementation of separated JSON files for the CineMax Android app, allowing for better organization and performance of movie data.

## Overview

The implementation separates the original `free_movie_api.json` into three distinct JSON files:

1. **thriller.json** - Thriller-specific movie data
2. **actor_actress.json** - Actor and actress information
3. **actual_content.json** - Main content (movies, channels, genres, etc.)

## File Structure

### 1. thriller.json
```json
{
  "genre_info": {
    "id": 1,
    "title": "Thriller",
    "description": "Suspenseful and exciting movies",
    "total_movies": 50
  },
  "thriller_movies": [
    {
      "id": 1,
      "title": "Movie Title",
      "type": "movie",
      "year": "2024",
      "genres": [{"id": 1, "title": "Thriller"}],
      "sources": [...],
      "actors": [...]
    }
  ]
}
```

### 2. actor_actress.json
```json
{
  "actors": [
    {
      "id": 1,
      "name": "Actor Name",
      "type": "actor",
      "role": "Character Name",
      "image": "image_url",
      "born": "Birth date",
      "height": "Height",
      "bio": "Biography"
    }
  ],
  "actresses": [
    {
      "id": 2,
      "name": "Actress Name",
      "type": "actress",
      "role": "Character Name",
      "image": "image_url",
      "born": "Birth date",
      "height": "Height",
      "bio": "Biography"
    }
  ],
  "total_actors": 100,
  "total_actresses": 80
}
```

### 3. actual_content.json
```json
{
  "api_info": {
    "version": "1.0",
    "description": "Movie API",
    "last_updated": "2024-01-01",
    "total_movies": 100,
    "total_channels": 50
  },
  "home": {
    "slides": [...],
    "featured_movies": [...],
    "channels": [...],
    "genres": [...]
  },
  "movies": [...],
  "channels": [...],
  "genres": [...],
  "categories": [...],
  "countries": [...],
  "subscription_plans": [...],
  "video_sources": {...},
  "ads_config": {...}
}
```

## Implementation Details

### New Response Classes

1. **ThrillerResponse.java**
   - Handles thriller-specific data
   - Contains `GenreInfo` and `List<Poster>` for thriller movies

2. **ActorActressResponse.java**
   - Handles actor and actress data
   - Contains separate lists for actors and actresses
   - Provides utility methods like `getAllCast()` and `getTotalCast()`

3. **ContentResponse.java**
   - Handles main content data
   - Contains all the original data structures from `JsonApiResponse`
   - Maintains backward compatibility

### Updated Configuration

**Global.java** now includes separate URLs:
```java
public static final String THRILLER_API_URL = "https://raw.githubusercontent.com/MovieAddict88/movie-api/main/thriller.json";
public static final String ACTOR_ACTRESS_API_URL = "https://raw.githubusercontent.com/MovieAddict88/movie-api/main/actor_actress.json";
public static final String CONTENT_API_URL = "https://raw.githubusercontent.com/MovieAddict88/movie-api/main/actual_content.json";
public static final String ADS_API_URL = "https://raw.githubusercontent.com/MovieAddict88/movie-api/main/ads_config.json";
```

### New API Endpoints

**apiRest.java** includes new endpoints:
```java
@GET("thriller.json")
Call<ThrillerResponse> getThrillerData();

@GET("actor_actress.json")
Call<ActorActressResponse> getActorActressData();

@GET("actual_content.json")
Call<ContentResponse> getContentData();
```

### New API Client Methods

**apiClient.java** includes new methods:
```java
public static void getThrillerData(Callback<ThrillerResponse> callback);
public static void getActorActressData(Callback<ActorActressResponse> callback);
public static void getContentData(Callback<ContentResponse> callback);
public static void getAdsConfigData(Callback<ContentResponse> callback);
```

### New Callback Interfaces

```java
public interface ThrillerCallback {
    void onSuccess(ThrillerResponse thrillerResponse);
    void onError(String error);
}

public interface ActorActressCallback {
    void onSuccess(ActorActressResponse actorActressResponse);
    void onError(String error);
}

public interface ContentCallback {
    void onSuccess(ContentResponse contentResponse);
    void onError(String error);
}
```

## Usage Examples

### Loading Thriller Data
```java
apiClient.getThrillerData(new apiClient.ThrillerCallback() {
    @Override
    public void onSuccess(ThrillerResponse thrillerResponse) {
        // Process thriller movies
        List<Poster> thrillerMovies = thrillerResponse.getThrillerMovies();
        GenreInfo genreInfo = thrillerResponse.getGenreInfo();
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});
```

### Loading Actor/Actress Data
```java
apiClient.getActorActressData(new apiClient.ActorActressCallback() {
    @Override
    public void onSuccess(ActorActressResponse actorActressResponse) {
        // Process actors and actresses
        List<Actor> actors = actorActressResponse.getActors();
        List<Actor> actresses = actorActressResponse.getActresses();
        List<Actor> allCast = actorActressResponse.getAllCast();
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});
```

### Loading Main Content
```java
apiClient.getContentData(new apiClient.ContentCallback() {
    @Override
    public void onSuccess(ContentResponse contentResponse) {
        // Process main content
        List<Poster> movies = contentResponse.getMovies();
        List<Channel> channels = contentResponse.getChannels();
        List<Genre> genres = contentResponse.getGenres();
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});
```

## Benefits

1. **Modularity**: Each JSON file serves a specific purpose
2. **Performance**: Smaller file sizes, faster loading
3. **Maintainability**: Easier to update specific data types
4. **Scalability**: Can add more specialized JSON files as needed
5. **Caching**: Better caching strategies for different data types
6. **Backward Compatibility**: Legacy endpoints still work

## Migration Guide

### For Existing Code

The implementation maintains backward compatibility. Existing code using `JsonApiResponse` will continue to work:

```java
// Legacy approach (still works)
apiClient.getJsonApiData(new apiClient.JsonApiCallback() {
    @Override
    public void onSuccess(JsonApiResponse jsonResponse) {
        // Existing code continues to work
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});
```

### For New Features

Use the new separated APIs for better performance:

```java
// New approach (recommended)
apiClient.getThrillerData(new apiClient.ThrillerCallback() {
    @Override
    public void onSuccess(ThrillerResponse thrillerResponse) {
        // Process thriller-specific data
    }
    
    @Override
    public void onError(String error) {
        // Handle error
    }
});
```

## Testing

Use the `SeparatedJsonApiExample.java` utility class to test the implementation:

```java
// Test all separated JSON APIs
SeparatedJsonApiExample.loadAllSeparatedData();

// Compare performance
SeparatedJsonApiExample.comparePerformance();
```

## GitHub Repository Setup

To use this implementation, create the following files in your GitHub repository:

1. `thriller.json` - Thriller movie data ✅ (Sample provided)
2. `actor_actress.json` - Actor and actress data ✅ (Sample provided)
3. `actual_content.json` - Main content data ✅ (Sample provided)
4. `ads_config.json` - Ads configuration (existing)

The URLs should point to:
- `https://raw.githubusercontent.com/YOUR_USERNAME/YOUR_REPO/main/thriller.json`
- `https://raw.githubusercontent.com/YOUR_USERNAME/YOUR_REPO/main/actor_actress.json`
- `https://raw.githubusercontent.com/YOUR_USERNAME/YOUR_REPO/main/actual_content.json`

## Performance Considerations

1. **Parallel Loading**: Load multiple JSON files in parallel for better performance
2. **Caching**: Each JSON file can be cached independently
3. **Error Handling**: Handle failures gracefully when individual files fail to load
4. **Fallback**: Use legacy single JSON file as fallback if separated files fail

## Future Enhancements

1. **More Specialized Files**: Add files for specific genres (action.json, comedy.json, etc.)
2. **Dynamic Loading**: Load only required data based on user preferences
3. **Incremental Updates**: Update only changed JSON files
4. **Compression**: Compress JSON files for faster downloads
5. **CDN Integration**: Use CDN for better global performance