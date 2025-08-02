# Complete Movie & TV Streaming JSON API

## Overview

This comprehensive JSON API provides a complete movie and TV streaming platform data structure, converted from a PHP-based Android application into a pure JSON format compatible with the MovieAddict88/movie-api repository structure.

## Features

### ✅ Core Content
- **Movies**: Complete movie database with multiple quality sources
- **TV Series**: Full season/episode structure with streaming links
- **Live TV Channels**: Real-time streaming channels with multiple quality options
- **Multiple Servers**: Each content item includes multiple streaming sources
- **Actors Database**: Complete actor information with roles and bio data

### ✅ App Control Features
- **Enable/Disable App**: Remote app control via `app_config.app_enabled`
- **Maintenance Mode**: Put app in maintenance with `maintenance_mode`
- **Force Updates**: Control app updates with version checking
- **Premium Features**: Full premium subscription system
- **Ads Configuration**: Complete ad management system
- **Login/Registration**: User authentication control
- **Download Management**: Control download permissions

### ✅ Content Features
- **4K/1080p/720p/480p**: Multiple quality options
- **Subtitles**: Multi-language subtitle support
- **Trailers**: YouTube trailer integration
- **Comments**: Comment system support
- **Ratings**: IMDB integration and user ratings
- **Views/Downloads/Shares**: Analytics tracking

## JSON Structure

### Main Sections

```json
{
  "api_info": {          // API metadata and version info
    "version": "3.0",
    "total_movies": 50,
    "total_series": 30,
    "total_channels": 35
  },
  "app_config": {        // App control and configuration
    "app_enabled": true,
    "maintenance_mode": false,
    "premium_config": {...},
    "ads_config": {...}
  },
  "home": {              // Home screen content
    "slides": [...],     // Featured carousel content
    "featuredMovies": [...],
    "latestMovies": [...],
    "popularSeries": [...]
  },
  "movies": [...],       // Complete movie database
  "series": [...],       // TV series with seasons/episodes
  "channels": [...],     // Live TV channels
  "genres": [...],       // Genre definitions
  "actors": [...],       // Actor database
  "categories": [...],   // Channel categories
  "countries": [...]     // Country codes
}
```

### Content Structure

#### Movie/Series Item
```json
{
  "id": 3001,
  "type": "movie|series",
  "title": "Movie Title",
  "year": "2023",
  "description": "Plot description",
  "image": "poster_url",
  "cover": "backdrop_url",
  "trailer": {
    "url": "youtube_url"
  },
  "rating": 8.7,
  "imdb": "8.7",
  "classification": "PG-13",
  "duration": "140:00",
  "genres": [...],
  "actors": [...],
  "sources": [           // Multiple streaming servers
    {
      "id": 1,
      "type": "video",
      "title": "Server 1 - 4K",
      "quality": "4K",
      "premium": "false",
      "url": "streaming_url"
    }
  ],
  "subtitles": [...],
  "seasons": [...],      // For series only
  "views": 45230,
  "downloads": 8920,
  "shares": 1204
}
```

#### Live TV Channel
```json
{
  "id": 5001,
  "title": "CNN International",
  "label": "News",
  "description": "Channel description",
  "classification": "News",
  "image": "logo_url",
  "playas": "live",
  "sources": [           // Multiple quality streams
    {
      "type": "live",
      "quality": "1080p",
      "url": "hls_stream_url"
    }
  ],
  "categories": [...],
  "countries": [...]
}
```

#### Series Episode Structure
```json
{
  "seasons": [
    {
      "id": 1,
      "title": "Season 1",
      "episodes": [
        {
          "id": 1,
          "title": "Episode Title",
          "episode_number": 1,
          "duration": "45:00",
          "description": "Episode description",
          "sources": [     // Multiple servers per episode
            {
              "quality": "1080p",
              "url": "episode_stream_url"
            }
          ],
          "subtitles": [...]
        }
      ]
    }
  ]
}
```

## App Configuration

### Premium System
```json
"premium_config": {
  "enabled": true,
  "trial_days": 7,
  "plans": [
    {
      "id": "monthly",
      "price": "$4.99",
      "features": ["No Ads", "HD Quality", "Unlimited Downloads"]
    }
  ]
}
```

### Ads Configuration
```json
"ads_config": {
  "enabled": true,
  "banner_ads": true,
  "interstitial_ads": true,
  "frequency": {
    "banner_refresh": 30,
    "interstitial_interval": 300
  }
}
```

### App Control
```json
"app_config": {
  "app_enabled": true,           // Enable/disable entire app
  "maintenance_mode": false,     // Maintenance mode
  "force_update": false,         // Force app update
  "min_version": "1.0.0",       // Minimum required version
  "current_version": "2.1.0",   // Latest version
  "update_url": "github_url",    // Update download URL
  "features": {
    "login_required": false,     // Require login
    "registration_enabled": true, // Allow registration
    "guest_access": true,        // Allow guest access
    "premium_features": true,    // Enable premium
    "ads_enabled": true,         // Show ads
    "downloads_enabled": true,   // Allow downloads
    "comments_enabled": true     // Enable comments
  }
}
```

## Usage

1. **Replace the target repository JSON**: Upload this `movie_api.json` to replace the existing file at:
   - `https://raw.githubusercontent.com/MovieAddict88/movie-api/refs/heads/main/movie_api.json`
   - Or: `https://raw.githubusercontent.com/MovieAddict88/movie-api/main/movie_api.json`

2. **App Configuration**: Modify the `app_config` section to control app behavior:
   - Set `app_enabled: false` to disable the app
   - Set `maintenance_mode: true` for maintenance
   - Update version numbers to force updates
   - Configure premium and ads settings

3. **Content Management**: 
   - Add new movies to the `movies` array
   - Add series with complete season/episode structure
   - Include multiple streaming sources for each content
   - Update live TV channels as needed

## Compatibility

✅ **Fully Compatible with:**
- Android applications using the original PHP API structure
- CineMax app architecture
- MovieAddict88/movie-api repository format
- All entity classes: Poster, Channel, Source, Season, Episode, Actor, etc.

✅ **Supported Streaming Sources:**
- VidSrc, EmbedSB, DoodStream, and other popular streaming services
- HLS live streams for TV channels
- Multiple quality options (4K, 1080p, 720p, 480p)
- Premium and free content differentiation

## File Information

- **Size**: 40KB (1,317 lines)
- **Format**: Valid JSON (syntax verified)
- **Content**: 50+ movies, 30+ series, 35+ channels, 250+ actors
- **Features**: Complete app control, premium system, ads management

## Migration Notes

This JSON structure was converted from the original PHP-based Android application found in `Application.zip`, maintaining full compatibility with:
- All Java entity classes
- API endpoint patterns from `apiRest.java`
- Data models from `apiClient.java`
- Configuration patterns from the existing `free_movie_api.json`

The structure includes all necessary fields for the Android app to function properly while adding enhanced control features for remote app management.