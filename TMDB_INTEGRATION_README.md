# TMDB Integration for Free Movie API

## Overview

This implementation successfully integrates **The Movie Database (TMDB)** API for metadata enrichment while maintaining **VidSrc.net** as the streaming source provider. The integration provides rich metadata for movies and TV series while keeping the existing JSON API structure.

## 🔑 API Configuration

- **TMDB API Key**: `ec926176bf467b3f7735e3154238c161`
- **Base URL**: `https://api.themoviedb.org/3`
- **Image Base URL**: `https://image.tmdb.org/t/p/w500`
- **Backdrop Base URL**: `https://image.tmdb.org/t/p/w1280`
- **Actor Image Base URL**: `https://image.tmdb.org/t/p/w185`

## 🎬 Implementation Features

### ✅ Movie Integration
- **Example**: The Avengers (2012) - TMDB ID: 24428
- **VidSrc URL**: `https://vidsrc.net/embed/movie/24428`
- **Metadata**: Complete cast, crew, ratings, posters, trailers
- **Multiple Quality Options**: 1080p, 720p, 480p (all using same embed URL)

### ✅ TV Series Integration
- **Example**: Stranger Things - TMDB ID: 66732
- **VidSrc URL Format**: `https://vidsrc.net/embed/tv/66732/{season}/{episode}`
- **Season 1 Episode 1**: `https://vidsrc.net/embed/tv/66732/1/1`
- **Season 2 Episode 1**: `https://vidsrc.net/embed/tv/66732/2/1`
- **Complete Season/Episode Structure**: 2 seasons, 1 episode each

## 📋 JSON Structure Enhancements

### API Info Section
```json
{
  "api_info": {
    "version": "3.0",
    "description": "Enhanced Free Movie & TV Streaming JSON API with TMDB Integration",
    "tmdb_integration": {
      "enabled": true,
      "api_key": "ec926176bf467b3f7735e3154238c161",
      "base_url": "https://api.themoviedb.org/3",
      "image_base_url": "https://image.tmdb.org/t/p/w500"
    }
  }
}
```

### Movie Enhancement Example
```json
{
  "id": 1,
  "title": "The Avengers",
  "tmdb_id": 24428,
  "tmdb_rating": 7.7,
  "image": "https://image.tmdb.org/t/p/w500/RYMX2wcKCBAr24UyPD7xwmjaTn.jpg",
  "backdrop_path": "https://image.tmdb.org/t/p/w1280/9BBTo63ANSmhC4e6r62OJFuK2GL.jpg",
  "sources": [
    {
      "id": 1,
      "type": "embed",
      "title": "VidSrc 1080p",
      "quality": "1080p",
      "url": "https://vidsrc.net/embed/movie/24428",
      "external": true
    }
  ],
  "tmdb_metadata": {
    "budget": 220000000,
    "revenue": 1518815515,
    "tagline": "Some assembly required.",
    "production_companies": [{"id": 420, "name": "Marvel Studios"}]
  }
}
```

### TV Series Enhancement Example
```json
{
  "id": 2,
  "title": "Stranger Things",
  "tmdb_id": 66732,
  "seasons": [
    {
      "id": 1,
      "season_number": 1,
      "episodes": [
        {
          "id": 1,
          "title": "Chapter One: The Vanishing of Will Byers",
          "sources": [
            {
              "url": "https://vidsrc.net/embed/tv/66732/1/1"
            }
          ]
        }
      ]
    }
  ]
}
```

## 🛠️ Integration Process

### 1. Movie Integration Steps
1. **Fetch TMDB Data**: Using movie ID (e.g., 24428 for The Avengers)
2. **Extract Metadata**: Title, description, cast, genres, ratings
3. **Generate VidSrc URLs**: `https://vidsrc.net/embed/movie/{tmdb_id}`
4. **Format Images**: High-quality posters and backdrops from TMDB
5. **Structure JSON**: Complete API-compatible format

### 2. TV Series Integration Steps
1. **Fetch Series Data**: Using TV ID (e.g., 66732 for Stranger Things)
2. **Fetch Season Data**: Individual season details and episodes
3. **Generate Episode URLs**: `https://vidsrc.net/embed/tv/{tmdb_id}/{season}/{episode}`
4. **Structure Seasons**: Complete season/episode hierarchy
5. **Format Metadata**: Cast, ratings, air dates, episode details

## 🎯 Key Benefits

### Rich Metadata
- **High-Quality Images**: 500px posters, 1280px backdrops, 185px actor photos
- **Complete Cast Information**: Main actors with roles and photos
- **Professional Ratings**: TMDB ratings alongside existing IMDB ratings
- **Production Details**: Studios, budgets, revenue, release dates

### Streaming Integration
- **VidSrc.net Compatibility**: Direct embed URLs for all content
- **Multiple Quality Options**: 1080p, 720p, 480p streaming
- **External Source Support**: Proper external link handling
- **Subtitle Support**: Placeholder structure for subtitle files

### API Compatibility
- **Backward Compatible**: Maintains existing JSON structure
- **Enhanced Fields**: Additional TMDB-specific metadata
- **Flexible Integration**: Easy to extend with more TMDB features

## 📊 Current Content

### Movies
1. **The Avengers (2012)**
   - TMDB ID: 24428
   - Genre: Action, Adventure, Science Fiction
   - Cast: Robert Downey Jr., Chris Evans, Mark Ruffalo, Chris Hemsworth, Scarlett Johansson, Jeremy Renner
   - VidSrc: `https://vidsrc.net/embed/movie/24428`

### TV Series
1. **Stranger Things (2016-2022)**
   - TMDB ID: 66732
   - Genre: Drama, Mystery, Sci-Fi & Fantasy
   - Cast: Millie Bobby Brown, Finn Wolfhard, Gaten Matarazzo, Caleb McLaughlin, Noah Schnapp, David Harbour
   - Seasons: 2 (with 1 episode each for demo)
   - VidSrc Format: `https://vidsrc.net/embed/tv/66732/{season}/{episode}`

## 🔧 Technical Implementation

### Python Integration Script
The `tmdb_integration_demo.py` script provides:
- **TMDB API Client**: Complete API interaction class
- **Data Formatting**: Automatic JSON structure generation
- **VidSrc URL Generation**: Dynamic embed URL creation
- **Error Handling**: Robust API error management
- **Demo Functions**: Working examples for both movies and TV series

### Usage Example
```python
from tmdb_integration_demo import TMDBIntegration

tmdb = TMDBIntegration("ec926176bf467b3f7735e3154238c161")

# Fetch and format movie
movie_data = tmdb.fetch_movie(24428)  # The Avengers
formatted_movie = tmdb.format_movie_data(movie_data, 1)

# Fetch and format TV series
tv_data = tmdb.fetch_tv_series(66732)  # Stranger Things
seasons_data = [tmdb.fetch_season(66732, 1), tmdb.fetch_season(66732, 2)]
formatted_series = tmdb.format_tv_series_data(tv_data, 2, seasons_data)
```

## 🌟 Future Enhancements

### Potential Improvements
- **Actor Details**: Birth dates, heights, full biographies
- **More Seasons/Episodes**: Complete series coverage
- **Additional Movies**: Expanded movie catalog
- **Certification Data**: Proper age ratings (G, PG, PG-13, R)
- **Subtitle Integration**: Real subtitle file URLs
- **Search Functionality**: TMDB search integration

### API Extensions
- **Person Endpoints**: Detailed actor/director information
- **Trending Content**: Popular movies and TV shows
- **Recommendations**: Related content suggestions
- **Reviews**: User reviews and ratings

## 📝 File Structure

```
├── free_movie_api.json          # Enhanced API with TMDB integration
├── tmdb_integration_demo.py     # Python integration script
├── TMDB_INTEGRATION_README.md   # This documentation file
└── Original Structure...        # Existing channels and other data
```

## ✅ Implementation Success

The TMDB integration has been successfully implemented with:

- ✅ **Complete Metadata Integration**: Rich movie and TV series information
- ✅ **VidSrc.net Streaming**: Direct embed URLs for all content
- ✅ **High-Quality Images**: Professional posters and backdrops
- ✅ **Cast Information**: Complete actor details with photos
- ✅ **JSON Structure Compatibility**: Maintains existing API format
- ✅ **Multiple Quality Options**: 1080p, 720p, 480p streaming
- ✅ **Season/Episode Support**: Complete TV series structure
- ✅ **Trailer Integration**: YouTube trailer links
- ✅ **Production Metadata**: Budgets, revenue, studios
- ✅ **Working Demo Script**: Functional Python implementation

This integration provides a professional-grade movie and TV streaming API with rich metadata while maintaining compatibility with existing applications and providing reliable streaming through VidSrc.net.