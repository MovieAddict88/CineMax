#!/usr/bin/env python3
"""
TMDB Auto-Detection API
This creates a clean JSON structure with TMDB IDs and provides dynamic metadata fetching
"""

import json
import requests
from datetime import datetime

# TMDB API Configuration
TMDB_API_KEY = "ec926176bf467b3f7735e3154238c161"
TMDB_BASE_URL = "https://api.themoviedb.org/3"
TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

def get_tmdb_movie_metadata(tmdb_id):
    """Fetch movie metadata from TMDB by ID"""
    try:
        url = f"{TMDB_BASE_URL}/movie/{tmdb_id}"
        params = {
            "api_key": TMDB_API_KEY,
            "append_to_response": "credits,videos,images"
        }
        
        response = requests.get(url, params=params)
        if response.status_code == 200:
            return response.json()
        return None
    except Exception as e:
        print(f"Error fetching TMDB data: {e}")
        return None

def get_tmdb_tv_metadata(tmdb_id):
    """Fetch TV series metadata from TMDB by ID"""
    try:
        url = f"{TMDB_BASE_URL}/tv/{tmdb_id}"
        params = {
            "api_key": TMDB_API_KEY,
            "append_to_response": "credits,videos,images"
        }
        
        response = requests.get(url, params=params)
        if response.status_code == 200:
            return response.json()
        return None
    except Exception as e:
        print(f"Error fetching TMDB data: {e}")
        return None

def create_clean_api_structure():
    """Create a clean JSON structure with TMDB IDs for auto-detection"""
    
    # Load existing API
    with open("existing_api.json", "r", encoding="utf-8") as f:
        api_data = json.load(f)
    
    # Clean up the movie entry - just add TMDB ID and clear hardcoded metadata
    for movie in api_data.get("movies", []):
        if movie["title"] == "Big Buck Bunny":
            # Replace Big Buck Bunny with The Avengers but keep sources
            movie.update({
                "title": "The Avengers",
                "tmdb_id": 24428,
                "type": "movie",
                "label": "",
                "sublabel": "",
                "imdb": "",
                "description": "",
                "classification": "",
                "year": "",
                "duration": "",
                "rating": None,
                "image": "",
                "cover": "",
                "genres": [],
                "actors": []
            })
            
            # Add vidsrc.net embed to sources while keeping existing ones
            vidsrc_source = {
                "id": len(movie["sources"]) + 1,
                "type": "embed",
                "title": "VidSrc Embed",
                "quality": "HD",
                "size": "Stream",
                "kind": "external",
                "premium": "false",
                "external": True,
                "url": f"https://vidsrc.net/embed/movie/24428"
            }
            movie["sources"].append(vidsrc_source)
    
    # Clean up TV series - add TMDB ID for a popular series
    for channel in api_data.get("channels", []):
        if "seasons" in channel and channel["title"] == "Sample TV Series":
            # Replace with Breaking Bad as example
            channel.update({
                "title": "Breaking Bad",
                "tmdb_id": 1396,
                "description": "",
                "classification": "",
                "rating": None,
                "image": ""
            })
            
            # Add vidsrc.net embeds to episodes
            for season in channel.get("seasons", []):
                season_num = season.get("id", 1)
                for episode in season.get("episodes", []):
                    episode_num = episode.get("id", 1)
                    
                    vidsrc_source = {
                        "id": len(episode.get("sources", [])) + 1,
                        "type": "embed",
                        "title": "VidSrc Embed",
                        "quality": "HD",
                        "size": "Stream",
                        "kind": "external",
                        "premium": "false",
                        "external": True,
                        "url": f"https://vidsrc.net/embed/tv/1396/{season_num}/{episode_num}"
                    }
                    
                    if "sources" not in episode:
                        episode["sources"] = []
                    episode["sources"].append(vidsrc_source)
    
    # Update home sections to reference TMDB IDs instead of hardcoded data
    for slide in api_data.get("home", {}).get("slides", []):
        if slide.get("title") == "Big Buck Bunny":
            slide.update({
                "title": "The Avengers",
                "tmdb_id": 24428,
                "image": "",
                "url": "movies/1"
            })
    
    for movie in api_data.get("home", {}).get("featured_movies", []):
        if movie.get("title") == "Big Buck Bunny":
            movie.update({
                "title": "The Avengers",
                "tmdb_id": 24428,
                "type": "movie",
                "image": "",
                "cover": "",
                "description": "",
                "year": "",
                "rating": None,
                "genres": []
            })
    
    # Update API info
    api_data["api_info"].update({
        "description": "Free Movie & TV Streaming JSON API with TMDB Auto-Detection",
        "last_updated": datetime.now().strftime("%Y-%m-%d"),
        "tmdb_integration": True,
        "auto_detect_metadata": True
    })
    
    return api_data

def create_metadata_api_endpoint():
    """Create a separate API endpoint for fetching TMDB metadata"""
    
    api_structure = {
        "endpoints": {
            "get_movie_metadata": "/api/tmdb/movie/{tmdb_id}",
            "get_tv_metadata": "/api/tmdb/tv/{tmdb_id}",
            "get_images": "/api/tmdb/images/{type}/{tmdb_id}"
        },
        "image_sizes": {
            "poster": ["w92", "w154", "w185", "w342", "w500", "w780", "original"],
            "backdrop": ["w300", "w780", "w1280", "original"],
            "profile": ["w45", "w185", "h632", "original"]
        },
        "usage": {
            "description": "Use TMDB IDs from the main API to fetch metadata dynamically",
            "example_movie": "GET /api/tmdb/movie/24428",
            "example_tv": "GET /api/tmdb/tv/1396",
            "example_images": "GET /api/tmdb/images/movie/24428"
        }
    }
    
    return api_structure

def main():
    """Create clean API structure with TMDB auto-detection"""
    print("Creating clean API structure with TMDB auto-detection...")
    
    # Create clean structure
    clean_api = create_clean_api_structure()
    
    # Save clean API
    with open("free_movie_api.json", "w", encoding="utf-8") as f:
        json.dump(clean_api, f, indent=2, ensure_ascii=False)
    
    # Create metadata API documentation
    metadata_api = create_metadata_api_endpoint()
    with open("tmdb_metadata_api.json", "w", encoding="utf-8") as f:
        json.dump(metadata_api, f, indent=2, ensure_ascii=False)
    
    print("✅ Created free_movie_api.json with TMDB auto-detection!")
    print("✅ Movies and TV series now use TMDB IDs instead of hardcoded metadata")
    print("✅ Images, descriptions, actors, etc. should be fetched dynamically using TMDB ID")
    print("✅ Added vidsrc.net embeds while preserving existing sources")
    print("✅ Created tmdb_metadata_api.json with API documentation")
    print("\n📝 How it works now:")
    print("   - JSON contains TMDB IDs (e.g., tmdb_id: 24428 for The Avengers)")
    print("   - App should fetch metadata dynamically: https://api.themoviedb.org/3/movie/24428")
    print("   - Images are fetched as: https://image.tmdb.org/t/p/w500{poster_path}")
    print("   - This ensures always up-to-date metadata from TMDB")

if __name__ == "__main__":
    main()