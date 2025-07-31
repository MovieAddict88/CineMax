# TMDB Auto-Fetch Usage Guide

## 🚀 How to Add Movies and TV Series with Auto-Metadata

This guide shows you how to add movies and TV series to your `free_movie_api.json` where **everything is automatically fetched from TMDB** except the sources (which you provide manually).

## 🎬 Adding a Movie

### What You Provide:
- **Title** (or TMDB ID)
- **Sources** (your streaming links)

### What's Auto-Fetched:
- All images (poster, backdrop)
- Complete cast with photos and bios
- Ratings, description, genres
- Release date, runtime
- Production companies
- YouTube trailer
- ALL other metadata

### Example: Adding "Inception"

```python
from tmdb_auto_fetch import TMDBAutoFetch

# Initialize with your API key
tmdb = TMDBAutoFetch("ec926176bf467b3f7735e3154238c161")

# 1. Provide your sources (only thing you need to manually create)
movie_sources = [
    {
        "id": 1,
        "type": "embed",
        "title": "VidSrc 1080p",
        "quality": "1080p",
        "size": "Streaming",
        "kind": "both",
        "premium": "false",
        "external": True,
        "url": "https://vidsrc.net/embed/movie/27205"  # Your link
    },
    {
        "id": 2,
        "type": "direct",
        "title": "Direct Link 720p",
        "quality": "720p",
        "size": "1.2GB",
        "kind": "both",
        "premium": "false",
        "external": False,
        "url": "https://example.com/inception-720p.mp4"  # Your link
    }
]

# 2. Auto-fetch everything else from TMDB
movie_data = tmdb.auto_fetch_movie("Inception", movie_sources)

# 3. movie_data now contains COMPLETE metadata!
print(f"Title: {movie_data['title']}")
print(f"Poster: {movie_data['image']}")  # Auto-fetched from TMDB
print(f"Cast: {[actor['name'] for actor in movie_data['actors']]}")  # Auto-fetched
```

### Result:
```json
{
  "id": 1,
  "title": "Inception",
  "tmdb_id": 27205,
  "image": "https://image.tmdb.org/t/p/w500/9gk7adHYeDvHkCSEqAvQNLV5Uge.jpg",
  "backdrop_path": "https://image.tmdb.org/t/p/w1280/s3TBrRGB1iav7gFOCNx3H31MoES.jpg",
  "description": "Cobb, a skilled thief who commits corporate espionage...",
  "actors": [
    {
      "name": "Leonardo DiCaprio",
      "role": "Dom Cobb",
      "image": "https://image.tmdb.org/t/p/w185/wo2hJpn04vbtmh0B9utCFdsQhxM.jpg",
      "born": "1974-11-11",
      "bio": "Leonardo Wilhelm DiCaprio is an American actor and producer..."
    }
  ],
  "sources": [
    {
      "url": "https://vidsrc.net/embed/movie/27205"  // YOUR PROVIDED SOURCE
    }
  ]
}
```

## 📺 Adding a TV Series

### What You Provide:
- **Title** (or TMDB ID)
- **Episode Sources** (your streaming links for specific episodes)

### What's Auto-Fetched:
- All images (poster, backdrop, episode stills)
- Complete cast with photos and bios
- Season/episode information
- Episode titles, descriptions, air dates
- Ratings, genres, production info
- YouTube trailer
- ALL other metadata

### Example: Adding "Breaking Bad"

```python
# 1. Provide your episode sources (only thing you need to manually create)
tv_sources = {
    1: {  # Season 1
        1: [  # Episode 1
            {
                "id": 1,
                "type": "embed",
                "title": "VidSrc 1080p",
                "quality": "1080p",
                "url": "https://vidsrc.net/embed/tv/1396/1/1"  # Your link
            }
        ],
        2: [  # Episode 2
            {
                "id": 2,
                "type": "embed",
                "title": "VidSrc 1080p",
                "quality": "1080p",
                "url": "https://vidsrc.net/embed/tv/1396/1/2"  # Your link
            }
        ]
    },
    2: {  # Season 2
        1: [  # Episode 1
            {
                "id": 3,
                "type": "embed",
                "title": "VidSrc 1080p",
                "quality": "1080p",
                "url": "https://vidsrc.net/embed/tv/1396/2/1"  # Your link
            }
        ]
    }
}

# 2. Auto-fetch everything else from TMDB
tv_data = tmdb.auto_fetch_tv_series("Breaking Bad", tv_sources)

# 3. tv_data now contains COMPLETE metadata!
print(f"Title: {tv_data['title']}")
print(f"Seasons: {len(tv_data['seasons'])}")
print(f"Episodes: {[len(season['episodes']) for season in tv_data['seasons']]}")
```

### Result:
```json
{
  "id": 2,
  "title": "Breaking Bad",
  "tmdb_id": 1396,
  "image": "https://image.tmdb.org/t/p/w500/ggFHVNu6YYI5L9pCfOacjizRGt.jpg",
  "backdrop_path": "https://image.tmdb.org/t/p/w1280/tsRy63Mu5cu8etL1X7ZLyf7UP1M.jpg",
  "description": "A high school chemistry teacher diagnosed with cancer...",
  "actors": [
    {
      "name": "Bryan Cranston",
      "role": "Walter White",
      "image": "https://image.tmdb.org/t/p/w185/7Jahy5LZX2Fo8fGJltMreAI49hC.jpg",
      "born": "1956-03-07",
      "bio": "Bryan Lee Cranston is an American actor, director..."
    }
  ],
  "seasons": [
    {
      "season_number": 1,
      "episodes": [
        {
          "title": "Pilot",
          "episode_number": 1,
          "overview": "Walter White, a struggling high school chemistry teacher...",
          "image": "https://image.tmdb.org/t/p/w500/...",
          "sources": [
            {
              "url": "https://vidsrc.net/embed/tv/1396/1/1"  // YOUR PROVIDED SOURCE
            }
          ]
        }
      ]
    }
  ]
}
```

## 🛠️ Simple Usage Examples

### Method 1: By Title (Recommended)
```python
# For movies
movie = tmdb.auto_fetch_movie("The Dark Knight", your_sources)

# For TV series
tv_show = tmdb.auto_fetch_tv_series("Game of Thrones", your_episode_sources)
```

### Method 2: By TMDB ID (More Precise)
```python
# For movies (if you know the TMDB ID)
movie = tmdb.auto_fetch_movie(155, your_sources)  # The Dark Knight

# For TV series (if you know the TMDB ID)
tv_show = tmdb.auto_fetch_tv_series(1399, your_episode_sources)  # Game of Thrones
```

## 📋 Source Format Examples

### Movie Sources
```python
movie_sources = [
    {
        "id": 1,
        "type": "embed",           # embed, direct, torrent, etc.
        "title": "VidSrc 1080p",
        "quality": "1080p",        # 1080p, 720p, 480p, etc.
        "size": "Streaming",       # File size or "Streaming"
        "kind": "both",            # both, download, stream
        "premium": "false",        # "true" or "false"
        "external": True,          # True for external embeds
        "url": "YOUR_STREAMING_URL_HERE"
    }
]
```

### TV Series Episode Sources
```python
tv_sources = {
    1: {  # Season 1
        1: [  # Episode 1 sources
            {
                "id": 1,
                "type": "embed",
                "title": "VidSrc 1080p",
                "quality": "1080p",
                "url": "YOUR_EPISODE_URL_HERE"
            }
        ],
        2: [  # Episode 2 sources
            {
                "id": 2,
                "type": "embed",
                "title": "VidSrc 720p",
                "quality": "720p",
                "url": "YOUR_EPISODE_URL_HERE"
            }
        ]
    },
    2: {  # Season 2
        1: [  # Episode 1 sources
            {
                "id": 3,
                "type": "embed",
                "title": "VidSrc 1080p",
                "quality": "1080p",
                "url": "YOUR_EPISODE_URL_HERE"
            }
        ]
    }
}
```

## 🎯 What Gets Auto-Fetched

### For Movies:
- ✅ **Title, Description, Tagline**
- ✅ **Poster Image** (500px high quality)
- ✅ **Backdrop Image** (1280px high quality)
- ✅ **Cast & Crew** (with photos and biographies)
- ✅ **Genres** (Action, Drama, etc.)
- ✅ **Ratings** (TMDB rating)
- ✅ **Release Date & Year**
- ✅ **Runtime/Duration**
- ✅ **Production Companies**
- ✅ **Budget & Revenue**
- ✅ **Certification** (G, PG, PG-13, R)
- ✅ **YouTube Trailer**
- ✅ **All TMDB Metadata**

### For TV Series:
- ✅ **All movie features above, plus:**
- ✅ **Season Information** (posters, air dates, overviews)
- ✅ **Episode Details** (titles, descriptions, stills)
- ✅ **Episode Air Dates**
- ✅ **Episode Runtime**
- ✅ **Created By Information**
- ✅ **Network Information**
- ✅ **Series Status** (Ended, Continuing, etc.)

## ⚡ Quick Start Workflow

1. **Install Dependencies**:
   ```bash
   pip install requests
   ```

2. **Import and Initialize**:
   ```python
   from tmdb_auto_fetch import TMDBAutoFetch
   tmdb = TMDBAutoFetch("ec926176bf467b3f7735e3154238c161")
   ```

3. **For Movies**:
   ```python
   sources = [{"id": 1, "type": "embed", "url": "YOUR_URL"}]
   movie = tmdb.auto_fetch_movie("Movie Title", sources)
   ```

4. **For TV Series**:
   ```python
   sources = {1: {1: [{"id": 1, "type": "embed", "url": "YOUR_URL"}]}}
   tv_show = tmdb.auto_fetch_tv_series("TV Show Title", sources)
   ```

5. **Add to Your JSON**:
   ```python
   # Add movie to your movies array
   your_api_data["movies"].append(movie)
   
   # Add TV series to your movies array
   your_api_data["movies"].append(tv_show)
   ```

## 🎉 Benefits

- **⚡ Super Fast**: Just provide title + sources, get everything else
- **🎯 100% Accurate**: All data comes directly from TMDB
- **🖼️ High Quality Images**: Professional posters and backdrops
- **👥 Complete Cast Info**: Actor photos and biographies
- **📺 Full TV Support**: Seasons, episodes, air dates
- **🎬 YouTube Trailers**: Automatic trailer detection
- **💰 Production Data**: Budgets, revenue, companies
- **🌍 Multi-Language**: Supports multiple languages

## 💡 Pro Tips

1. **Use Exact Titles**: "The Dark Knight" works better than "Dark Knight"
2. **Use TMDB ID for Precision**: If title search fails, find the TMDB ID
3. **Batch Processing**: Process multiple movies/shows in a loop
4. **Error Handling**: Always check if the result is not None
5. **Rate Limiting**: Add delays between requests for large batches

This system makes adding content incredibly easy - you focus on finding good streaming sources, and TMDB handles all the metadata automatically!