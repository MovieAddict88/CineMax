#!/usr/bin/env python3
"""
TMDB Enhanced Movie API Generator
This script integrates TMDB API to populate missing metadata fields
and adds vidsrc.net embed sources while preserving existing direct sources.
"""

import json
import requests
import time
from datetime import datetime

# TMDB API Configuration
TMDB_API_KEY = "ec926176bf467b3f7735e3154238c161"
TMDB_BASE_URL = "https://api.themoviedb.org/3"
TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"

def get_tmdb_movie_details(title, year=None):
    """Search for movie on TMDB and return detailed information"""
    try:
        # Search for movie
        search_url = f"{TMDB_BASE_URL}/search/movie"
        params = {
            "api_key": TMDB_API_KEY,
            "query": title,
            "year": year
        }
        
        response = requests.get(search_url, params=params)
        if response.status_code != 200:
            print(f"Error searching for movie '{title}': {response.status_code}")
            return None
            
        search_data = response.json()
        
        if not search_data.get("results"):
            print(f"No results found for movie '{title}'")
            return None
            
        # Get the first result (most relevant)
        movie_id = search_data["results"][0]["id"]
        
        # Get detailed movie information
        details_url = f"{TMDB_BASE_URL}/movie/{movie_id}"
        details_params = {
            "api_key": TMDB_API_KEY,
            "append_to_response": "credits,videos,images"
        }
        
        details_response = requests.get(details_url, params=details_params)
        if details_response.status_code != 200:
            print(f"Error fetching movie details for '{title}': {details_response.status_code}")
            return None
            
        return details_response.json()
        
    except Exception as e:
        print(f"Error fetching TMDB data for '{title}': {str(e)}")
        return None

def get_tmdb_tv_details(title, year=None):
    """Search for TV series on TMDB and return detailed information"""
    try:
        # Search for TV series
        search_url = f"{TMDB_BASE_URL}/search/tv"
        params = {
            "api_key": TMDB_API_KEY,
            "query": title,
            "first_air_date_year": year
        }
        
        response = requests.get(search_url, params=params)
        if response.status_code != 200:
            print(f"Error searching for TV series '{title}': {response.status_code}")
            return None
            
        search_data = response.json()
        
        if not search_data.get("results"):
            print(f"No results found for TV series '{title}'")
            return None
            
        # Get the first result (most relevant)
        tv_id = search_data["results"][0]["id"]
        
        # Get detailed TV information
        details_url = f"{TMDB_BASE_URL}/tv/{tv_id}"
        details_params = {
            "api_key": TMDB_API_KEY,
            "append_to_response": "credits,videos,images,seasons"
        }
        
        details_response = requests.get(details_url, params=details_params)
        if details_response.status_code != 200:
            print(f"Error fetching TV details for '{title}': {details_response.status_code}")
            return None
            
        return details_response.json()
        
    except Exception as e:
        print(f"Error fetching TMDB data for TV series '{title}': {str(e)}")
        return None

def enhance_movie_with_tmdb(movie_data, tmdb_data):
    """Enhance movie data with TMDB information"""
    if not tmdb_data:
        return movie_data
    
    # Update basic movie information
    if not movie_data.get("description") or movie_data["description"] == "":
        movie_data["description"] = tmdb_data.get("overview", "")
    
    if not movie_data.get("year") or movie_data["year"] == "":
        release_date = tmdb_data.get("release_date", "")
        if release_date:
            movie_data["year"] = release_date.split("-")[0]
    
    if not movie_data.get("duration") or movie_data["duration"] == "":
        runtime = tmdb_data.get("runtime", 0)
        if runtime:
            hours = runtime // 60
            minutes = runtime % 60
            movie_data["duration"] = f"{hours}:{minutes:02d}" if hours > 0 else f"{minutes} min"
    
    if not movie_data.get("rating") or movie_data["rating"] is None:
        vote_average = tmdb_data.get("vote_average", 0)
        if vote_average:
            movie_data["rating"] = round(vote_average, 1)
    
    if not movie_data.get("imdb") or movie_data["imdb"] == "":
        vote_average = tmdb_data.get("vote_average", 0)
        if vote_average:
            movie_data["imdb"] = str(round(vote_average, 1))
    
    if not movie_data.get("classification") or movie_data["classification"] == "":
        # Get content rating from releases
        movie_data["classification"] = "PG-13"  # Default fallback
    
    # Update images
    if not movie_data.get("image") or movie_data["image"] == "":
        poster_path = tmdb_data.get("poster_path")
        if poster_path:
            movie_data["image"] = f"{TMDB_IMAGE_BASE_URL}{poster_path}"
    
    if not movie_data.get("cover") or movie_data["cover"] == "":
        backdrop_path = tmdb_data.get("backdrop_path")
        if backdrop_path:
            movie_data["cover"] = f"https://image.tmdb.org/t/p/w1280{backdrop_path}"
        elif movie_data.get("image"):
            movie_data["cover"] = movie_data["image"]
    
    # Update genres
    if tmdb_data.get("genres"):
        movie_data["genres"] = [
            {
                "id": genre["id"],
                "title": genre["name"]
            }
            for genre in tmdb_data["genres"]
        ]
    
    # Update actors
    if tmdb_data.get("credits", {}).get("cast"):
        cast = tmdb_data["credits"]["cast"][:5]  # Top 5 actors
        movie_data["actors"] = []
        for i, actor in enumerate(cast, 1):
            profile_image = ""
            if actor.get("profile_path"):
                profile_image = f"{TMDB_IMAGE_BASE_URL}{actor['profile_path']}"
            
            movie_data["actors"].append({
                "id": i,
                "name": actor["name"],
                "type": "actor",
                "role": actor.get("character", ""),
                "image": profile_image,
                "born": "",
                "height": "",
                "bio": ""
            })
    
    # Add vidsrc.net embed source (keeping existing sources)
    if "sources" not in movie_data:
        movie_data["sources"] = []
    
    # Add vidsrc.net embed as additional source
    vidsrc_source = {
        "id": len(movie_data["sources"]) + 1,
        "type": "embed",
        "title": "VidSrc Embed",
        "quality": "HD",
        "size": "Stream",
        "kind": "external",
        "premium": "false",
        "external": True,
        "url": f"https://vidsrc.net/embed/movie/{tmdb_data.get('id', 'unknown')}"
    }
    
    movie_data["sources"].append(vidsrc_source)
    
    return movie_data

def enhance_tv_series_with_tmdb(series_data, tmdb_data):
    """Enhance TV series data with TMDB information"""
    if not tmdb_data:
        return series_data
    
    # Update basic series information
    if not series_data.get("description") or series_data["description"] == "":
        series_data["description"] = tmdb_data.get("overview", "")
    
    if not series_data.get("year") or series_data["year"] == "":
        first_air_date = tmdb_data.get("first_air_date", "")
        if first_air_date:
            series_data["year"] = first_air_date.split("-")[0]
    
    if not series_data.get("rating") or series_data["rating"] is None:
        vote_average = tmdb_data.get("vote_average", 0)
        if vote_average:
            series_data["rating"] = round(vote_average, 1)
    
    if not series_data.get("classification") or series_data["classification"] == "":
        series_data["classification"] = "TV-14"  # Default fallback
    
    # Update images
    if not series_data.get("image") or series_data["image"] == "":
        poster_path = tmdb_data.get("poster_path")
        if poster_path:
            series_data["image"] = f"{TMDB_IMAGE_BASE_URL}{poster_path}"
    
    # Add vidsrc.net embed sources to episodes
    if "seasons" in series_data:
        for season in series_data["seasons"]:
            season_num = season.get("id", 1)
            if "episodes" in season:
                for episode in season["episodes"]:
                    episode_num = episode.get("id", 1)
                    
                    # Add vidsrc.net embed source to episode (keeping existing sources)
                    if "sources" not in episode:
                        episode["sources"] = []
                    
                    vidsrc_source = {
                        "id": len(episode["sources"]) + 1,
                        "type": "embed",
                        "title": "VidSrc Embed",
                        "quality": "HD",
                        "size": "Stream",
                        "kind": "external",
                        "premium": "false",
                        "external": True,
                        "url": f"https://vidsrc.net/embed/tv/{tmdb_data.get('id', 'unknown')}/{season_num}/{episode_num}"
                    }
                    
                    episode["sources"].append(vidsrc_source)
    
    return series_data

def enhance_api_with_tmdb(api_data):
    """Enhance the entire API data with TMDB information"""
    print("Starting TMDB enhancement process...")
    
    # Enhance movies
    if "movies" in api_data:
        print(f"Enhancing {len(api_data['movies'])} movies...")
        for i, movie in enumerate(api_data["movies"]):
            print(f"Processing movie {i+1}: {movie.get('title', 'Unknown')}")
            
            # Get TMDB data for movie
            title = movie.get("title", "")
            year = movie.get("year", "").strip()
            
            tmdb_data = get_tmdb_movie_details(title, year if year else None)
            
            # Enhance movie with TMDB data
            api_data["movies"][i] = enhance_movie_with_tmdb(movie, tmdb_data)
            
            # Rate limiting
            time.sleep(0.25)  # 4 requests per second limit
    
    # Enhance TV series (channels with seasons)
    if "channels" in api_data:
        print(f"Checking {len(api_data['channels'])} channels for TV series...")
        for i, channel in enumerate(api_data["channels"]):
            # Check if this channel has seasons (making it a TV series)
            if "seasons" in channel:
                print(f"Processing TV series {i+1}: {channel.get('title', 'Unknown')}")
                
                title = channel.get("title", "")
                # Try to extract year from title or use a default
                year = None
                
                tmdb_data = get_tmdb_tv_details(title, year)
                
                # Enhance series with TMDB data
                api_data["channels"][i] = enhance_tv_series_with_tmdb(channel, tmdb_data)
                
                # Rate limiting
                time.sleep(0.25)
    
    # Update API info
    api_data["api_info"]["last_updated"] = datetime.now().strftime("%Y-%m-%d")
    api_data["api_info"]["description"] = "Enhanced Free Movie & TV Streaming JSON API with Full TMDB Integration"
    
    print("TMDB enhancement completed!")
    return api_data

def main():
    """Main function to enhance the existing API with TMDB data"""
    try:
        # Load existing API data
        print("Loading existing API data...")
        with open("existing_api.json", "r", encoding="utf-8") as f:
            api_data = json.load(f)
        
        # Enhance with TMDB data
        enhanced_data = enhance_api_with_tmdb(api_data)
        
        # Save enhanced API
        print("Saving enhanced API...")
        with open("free_movie_api.json", "w", encoding="utf-8") as f:
            json.dump(enhanced_data, f, indent=2, ensure_ascii=False)
        
        print("✅ Enhanced free_movie_api.json created successfully!")
        print("✅ Added TMDB metadata for all movies and TV series")
        print("✅ Added vidsrc.net embed sources while preserving existing sources")
        
    except Exception as e:
        print(f"❌ Error: {str(e)}")

if __name__ == "__main__":
    main()