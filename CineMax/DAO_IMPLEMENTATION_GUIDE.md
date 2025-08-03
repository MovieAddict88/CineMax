# CineMax DAO Implementation Guide

## Overview
This implementation adds Room database caching to CineMax app to prevent reloading data every time the app opens. The data is cached locally and only refreshed when needed (every 30 minutes or when forced).

## What's Been Added

### 1. Room Database Dependencies
- Added to `app/build.gradle` (keeping root gradle 7.0.2 as requested)
- Room runtime, compiler, and RxJava support
- Lifecycle components for ViewModel and LiveData

### 2. Database Structure
- **Entities**: Poster, Channel, Genre, Category (with Room annotations)
- **DAOs**: PosterDao, ChannelDao, GenreDao, CategoryDao
- **Database**: CineMaxDatabase (singleton pattern)
- **Type Converters**: For complex objects (List<Genre>, List<Actor>, etc.)

### 3. Repository Pattern
- `CineMaxRepository`: Handles API calls with DAO caching
- `CacheManager`: Simple interface for activities to use cached data
- Automatic cache refresh (30-minute expiry)
- Offline support (data available even without internet)

### 4. Android 12 Compatibility
- Updated `AndroidManifest.xml` with `android:exported` attributes
- Updated `targetSdkVersion` to 31 (Android 12)

## How It Works

### Cache Flow
1. App starts → `CacheManager.initializeCache()` is called
2. Repository checks if cache is empty or older than 30 minutes
3. If refresh needed: API call → Save to Room database
4. Activities get data from Room database (LiveData)
5. UI updates automatically when data changes

### Key Benefits
- **No More Reloading**: Data persists between app sessions
- **Offline Support**: Works without internet connection
- **Better Performance**: Database queries are faster than API calls
- **Automatic Updates**: Fresh data every 30 minutes
- **Memory Efficient**: Room handles memory management

## Usage in Activities

### For existing code, replace direct API calls with CacheManager:

```java
// OLD WAY (direct API calls):
apiService.getMovies().enqueue(new Callback<ApiResponse>() {
    @Override
    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
        // Handle response
    }
});

// NEW WAY (cached data):
CacheManager cacheManager = CacheManager.getInstance(this);
cacheManager.getMovies().observe(this, movies -> {
    if (movies != null) {
        // Update UI with cached data
        // Data is automatically refreshed in background if needed
    }
});
```

### Examples for different data types:

```java
CacheManager cacheManager = CacheManager.getInstance(this);

// Get movies
cacheManager.getMovies().observe(this, movies -> {
    // Handle movies list
});

// Get TV series
cacheManager.getSeries().observe(this, series -> {
    // Handle series list
});

// Get channels
cacheManager.getChannels().observe(this, channels -> {
    // Handle channels list
});

// Get genres
cacheManager.getGenres().observe(this, genres -> {
    // Handle genres list
});

// Search functionality
cacheManager.searchPosters("search query").observe(this, results -> {
    // Handle search results
});

// Force refresh (for pull-to-refresh)
cacheManager.forceRefreshAll();
```

## Files Modified/Added

### New Files:
- `database/dao/PosterDao.java`
- `database/dao/ChannelDao.java` 
- `database/dao/GenreDao.java`
- `database/dao/CategoryDao.java`
- `database/converters/TypeConverters.java`
- `database/CineMaxDatabase.java`
- `database/CacheManager.java`
- `repository/CineMaxRepository.java`

### Modified Files:
- `AndroidManifest.xml` (Android 12 compatibility)
- `app/build.gradle` (Room dependencies)
- `entity/Poster.java` (Room annotations)
- `entity/Channel.java` (Room annotations)
- `entity/Genre.java` (Room annotations)
- `entity/Category.java` (Room annotations)
- `Provider/PrefManager.java` (added long support)
- `MyApplication.java` (cache initialization)
- `ui/activities/SplashActivity.java` (cache initialization)

## Migration for Activities

### HomeActivity Example:
```java
// In onCreate() or onResume()
CacheManager cacheManager = CacheManager.getInstance(this);

// Replace existing API calls with:
cacheManager.getMovies().observe(this, movies -> {
    if (movies != null && !movies.isEmpty()) {
        // Update recyclerView adapter
        movieAdapter.updateData(movies);
    }
});

cacheManager.getChannels().observe(this, channels -> {
    if (channels != null && !channels.isEmpty()) {
        // Update channels adapter
        channelAdapter.updateData(channels);
    }
});
```

### Search Activity Example:
```java
// For search functionality
private void performSearch(String query) {
    CacheManager cacheManager = CacheManager.getInstance(this);
    cacheManager.searchPosters(query).observe(this, results -> {
        if (results != null) {
            searchAdapter.updateData(results);
        }
    });
}
```

## Cache Management

### Cache Duration
- Default: 30 minutes
- Can be modified in `CineMaxRepository.shouldRefreshCache()`

### Force Refresh Options
```java
cacheManager.forceRefreshAll();        // Refresh everything
cacheManager.forceRefreshPosters();    // Refresh only movies/series
cacheManager.forceRefreshChannels();   // Refresh only channels
```

### Check Cache Status
```java
cacheManager.checkCacheStatus(hasData -> {
    if (hasData) {
        // Cache has data, proceed normally
    } else {
        // Show loading indicator, data is being fetched
    }
});
```

## Database Schema

### Tables Created:
- `posters` (movies and series)
- `channels` (live TV channels)
- `genres` (movie/series genres)
- `categories` (content categories)

### Relationships:
- Complex objects stored as JSON strings using TypeConverters
- Each entity maintains its API structure for compatibility

## Performance Benefits

1. **Faster App Startup**: Data loads from local database
2. **Reduced API Calls**: Only refresh when necessary
3. **Offline Functionality**: Works without internet
4. **Automatic Updates**: Background refresh keeps data current
5. **Memory Efficient**: Room handles memory optimization

## Troubleshooting

### Common Issues:
1. **Build Errors**: Ensure all imports are correct
2. **Database Migration**: Clear app data if schema changes
3. **Cache Not Updating**: Check network connectivity for API calls
4. **Memory Issues**: Room handles this automatically

### Debug Options:
- Enable SQL logging in debug builds
- Check cache timestamps in SharedPreferences
- Monitor network requests in logs

## Future Enhancements

Possible improvements:
1. Add pagination support for large datasets
2. Implement incremental updates instead of full refresh
3. Add user-specific data caching
4. Implement cache size limits
5. Add background sync workers

## Compatibility

- **Minimum SDK**: 19 (unchanged)
- **Target SDK**: 31 (Android 12)
- **Gradle**: 7.0.2 (unchanged as requested)
- **Room Version**: 2.4.3
- **Lifecycle**: 2.5.1

This implementation maintains full compatibility with existing code while adding powerful caching capabilities.