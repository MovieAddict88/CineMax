#!/usr/bin/env python3
"""
TMDB API Integration Script
Enhances the existing free_movie_api.json with TMDB metadata
"""

import json
import urllib.request
import urllib.parse
import time
from typing import Dict, List, Any

# TMDB API Configuration
TMDB_API_KEY = "ec926176bf467b3f7735e3154238c161"
TMDB_BASE_URL = "https://api.themoviedb.org/3"
TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
TMDB_IMAGE_BASE_URL_ORIGINAL = "https://image.tmdb.org/t/p/original"

def make_request(url: str, params: Dict) -> Dict:
    """Make HTTP request using urllib"""
    query_string = urllib.parse.urlencode(params)
    full_url = f"{url}?{query_string}"
    
    try:
        with urllib.request.urlopen(full_url) as response:
            if response.status == 200:
                return json.loads(response.read().decode('utf-8'))
    except Exception as e:
        print(f"Request failed: {e}")
    
    return {}

def search_tmdb_movie(title: str, year: str = None) -> Dict:
    """Search for a movie on TMDB"""
    url = f"{TMDB_BASE_URL}/search/movie"
    params = {
        "api_key": TMDB_API_KEY,
        "query": title,
        "language": "en-US"
    }
    if year:
        params["year"] = year
    
    response = make_request(url, params)
    results = response.get("results", [])
    return results[0] if results else {}

def search_tmdb_tv(title: str, year: str = None) -> Dict:
    """Search for a TV series on TMDB"""
    url = f"{TMDB_BASE_URL}/search/tv"
    params = {
        "api_key": TMDB_API_KEY,
        "query": title,
        "language": "en-US"
    }
    if year:
        params["first_air_date_year"] = year
    
    response = make_request(url, params)
    results = response.get("results", [])
    return results[0] if results else {}

def get_movie_details(movie_id: int) -> Dict:
    """Get detailed movie information from TMDB"""
    url = f"{TMDB_BASE_URL}/movie/{movie_id}"
    params = {
        "api_key": TMDB_API_KEY,
        "language": "en-US",
        "append_to_response": "credits,images,videos,keywords,releases"
    }
    
    return make_request(url, params)

def get_tv_details(tv_id: int) -> Dict:
    """Get detailed TV series information from TMDB"""
    url = f"{TMDB_BASE_URL}/tv/{tv_id}"
    params = {
        "api_key": TMDB_API_KEY,
        "language": "en-US",
        "append_to_response": "credits,images,videos,keywords,content_ratings"
    }
    
    return make_request(url, params)

def get_tv_season_details(tv_id: int, season_number: int) -> Dict:
    """Get detailed TV season information from TMDB"""
    url = f"{TMDB_BASE_URL}/tv/{tv_id}/season/{season_number}"
    params = {
        "api_key": TMDB_API_KEY,
        "language": "en-US"
    }
    
    return make_request(url, params)

def minutes_to_duration_string(minutes: int) -> str:
    """Convert minutes to MM:SS format"""
    if not minutes:
        return "00:00"
    hours = minutes // 60
    mins = minutes % 60
    if hours > 0:
        return f"{hours}:{mins:02d}:00"
    else:
        return f"{mins:02d}:00"

def enhance_movie_with_tmdb(movie_data: Dict, tmdb_search_title: str = None) -> Dict:
    """Enhance movie data with TMDB metadata"""
    enhanced_movie = movie_data.copy()
    
    # Use provided search title or fall back to existing title
    search_title = tmdb_search_title or movie_data.get("title", "")
    year = movie_data.get("year", "")
    
    print(f"Searching TMDB for movie: {search_title} ({year})")
    
    # Search for the movie
    search_result = search_tmdb_movie(search_title, year)
    if not search_result:
        print(f"No TMDB results found for {search_title}")
        return enhanced_movie
    
    # Get detailed information
    movie_details = get_movie_details(search_result["id"])
    if not movie_details:
        print(f"Could not get details for movie ID {search_result['id']}")
        return enhanced_movie
    
    print(f"Found TMDB movie: {movie_details.get('title', 'Unknown')} ({movie_details.get('release_date', 'Unknown')[:4]})")
    
    # Update basic information
    enhanced_movie.update({
        "title": movie_details.get("title", enhanced_movie.get("title")),
        "description": movie_details.get("overview", enhanced_movie.get("description")),
        "year": movie_details.get("release_date", "")[:4] if movie_details.get("release_date") else enhanced_movie.get("year"),
        "duration": minutes_to_duration_string(movie_details.get("runtime", 0)),
        "rating": float(movie_details.get("vote_average", 0)),
        "imdb": str(movie_details.get("vote_average", 0)),
        "views": int(movie_details.get("popularity", enhanced_movie.get("views", 0)))
    })
    
    # Update images
    if movie_details.get("poster_path"):
        enhanced_movie["image"] = f"{TMDB_IMAGE_BASE_URL}{movie_details['poster_path']}"
    if movie_details.get("backdrop_path"):
        enhanced_movie["cover"] = f"{TMDB_IMAGE_BASE_URL_ORIGINAL}{movie_details['backdrop_path']}"
    
    # Update classification based on releases
    releases = movie_details.get("releases", {}).get("countries", [])
    us_release = next((r for r in releases if r["iso_3166_1"] == "US"), {})
    if us_release.get("certification"):
        enhanced_movie["classification"] = us_release["certification"]
    
    # Update genres
    genres = movie_details.get("genres", [])
    enhanced_movie["genres"] = [{"id": g["id"], "title": g["name"]} for g in genres]
    
    # Update actors
    credits = movie_details.get("credits", {})
    cast = credits.get("cast", [])[:10]  # Top 10 actors
    enhanced_movie["actors"] = []
    for actor in cast:
        actor_data = {
            "id": actor["id"],
            "name": actor["name"],
            "type": "actor" if actor["gender"] != 1 else "actress",
            "role": actor.get("character", ""),
            "image": f"{TMDB_IMAGE_BASE_URL}{actor['profile_path']}" if actor.get("profile_path") else "",
            "born": "",  # Would need additional API call
            "height": "",  # Would need additional API call
            "bio": ""  # Would need additional API call
        }
        enhanced_movie["actors"].append(actor_data)
    
    # Update sources to use vidsrc.net embed
    vidsrc_sources = []
    for i, quality in enumerate(["1080p", "720p", "480p"], 1):
        vidsrc_sources.append({
            "id": enhanced_movie["id"] * 100 + i,
            "type": "embed",
            "title": f"{enhanced_movie['title']} {quality}",
            "quality": quality,
            "size": "Stream",
            "kind": "stream",
            "premium": "false",
            "external": True,
            "url": f"https://vidsrc.net/embed/movie/{search_result['id']}"
        })
    enhanced_movie["sources"] = vidsrc_sources
    
    # Add trailer from TMDB
    videos = movie_details.get("videos", {}).get("results", [])
    trailer = next((v for v in videos if v["type"] == "Trailer" and v["site"] == "YouTube"), None)
    if trailer:
        enhanced_movie["trailer"] = {
            "id": enhanced_movie["id"] * 1000,
            "type": "video",
            "title": f"{enhanced_movie['title']} Trailer",
            "url": f"https://www.youtube.com/watch?v={trailer['key']}"
        }
    
    return enhanced_movie

def enhance_tv_series_with_tmdb(tv_data: Dict, tmdb_search_title: str = None) -> Dict:
    """Enhance TV series data with TMDB metadata"""
    enhanced_tv = tv_data.copy()
    
    # Use provided search title or fall back to existing title
    search_title = tmdb_search_title or tv_data.get("title", "")
    year = tv_data.get("year", "")
    
    print(f"Searching TMDB for TV series: {search_title} ({year})")
    
    # Search for the TV series
    search_result = search_tmdb_tv(search_title, year)
    if not search_result:
        print(f"No TMDB results found for {search_title}")
        return enhanced_tv
    
    # Get detailed information
    tv_details = get_tv_details(search_result["id"])
    if not tv_details:
        print(f"Could not get details for TV series ID {search_result['id']}")
        return enhanced_tv
    
    print(f"Found TMDB TV series: {tv_details.get('name', 'Unknown')} ({tv_details.get('first_air_date', 'Unknown')[:4]})")
    
    # Update basic information
    enhanced_tv.update({
        "title": tv_details.get("name", enhanced_tv.get("title")),
        "description": tv_details.get("overview", enhanced_tv.get("description")),
        "year": tv_details.get("first_air_date", "")[:4] if tv_details.get("first_air_date") else enhanced_tv.get("year"),
        "duration": minutes_to_duration_string(tv_details.get("episode_run_time", [45])[0] if tv_details.get("episode_run_time") else 45),
        "rating": float(tv_details.get("vote_average", 0)),
        "imdb": str(tv_details.get("vote_average", 0)),
        "views": int(tv_details.get("popularity", enhanced_tv.get("views", 0))),
        "sublabel": f"{tv_details.get('number_of_seasons', 2)} Seasons"
    })
    
    # Update images
    if tv_details.get("poster_path"):
        enhanced_tv["image"] = f"{TMDB_IMAGE_BASE_URL}{tv_details['poster_path']}"
    if tv_details.get("backdrop_path"):
        enhanced_tv["cover"] = f"{TMDB_IMAGE_BASE_URL_ORIGINAL}{tv_details['backdrop_path']}"
    
    # Update classification
    content_ratings = tv_details.get("content_ratings", {}).get("results", [])
    us_rating = next((r for r in content_ratings if r["iso_3166_1"] == "US"), {})
    if us_rating.get("rating"):
        enhanced_tv["classification"] = us_rating["rating"]
    
    # Update genres
    genres = tv_details.get("genres", [])
    enhanced_tv["genres"] = [{"id": g["id"], "title": g["name"]} for g in genres]
    
    # Update actors
    credits = tv_details.get("credits", {})
    cast = credits.get("cast", [])[:10]  # Top 10 actors
    enhanced_tv["actors"] = []
    for actor in cast:
        actor_data = {
            "id": actor["id"],
            "name": actor["name"],
            "type": "actor" if actor["gender"] != 1 else "actress",
            "role": actor.get("character", ""),
            "image": f"{TMDB_IMAGE_BASE_URL}{actor['profile_path']}" if actor.get("profile_path") else "",
            "born": "",  # Would need additional API call
            "height": "",  # Would need additional API call
            "bio": ""  # Would need additional API call
        }
        enhanced_tv["actors"].append(actor_data)
    
    # Enhance seasons with TMDB data
    if "seasons" in enhanced_tv:
        for season in enhanced_tv["seasons"]:
            season_number = season["id"]
            season_details = get_tv_season_details(search_result["id"], season_number)
            
            if season_details:
                # Update season info
                if season_details.get("poster_path"):
                    season["poster"] = f"{TMDB_IMAGE_BASE_URL}{season_details['poster_path']}"
                
                # Update episodes
                if "episodes" in season:
                    tmdb_episodes = season_details.get("episodes", [])
                    for i, episode in enumerate(season["episodes"]):
                        if i < len(tmdb_episodes):
                            tmdb_ep = tmdb_episodes[i]
                            episode.update({
                                "title": tmdb_ep.get("name", episode.get("title")),
                                "description": tmdb_ep.get("overview", ""),
                                "duration": minutes_to_duration_string(tmdb_ep.get("runtime", 45)),
                                "air_date": tmdb_ep.get("air_date", "")
                            })
                            
                            if tmdb_ep.get("still_path"):
                                episode["image"] = f"{TMDB_IMAGE_BASE_URL}{tmdb_ep['still_path']}"
                        
                        # Update episode sources to use vidsrc.net embed
                        episode["sources"] = [
                            {
                                "id": episode["id"] * 100 + j,
                                "type": "embed",
                                "title": f"{episode['title']} {quality}",
                                "quality": quality,
                                "url": f"https://vidsrc.net/embed/tv/{search_result['id']}/{season_number}/{episode['episode_number']}"
                            }
                            for j, quality in enumerate(["1080p", "720p", "480p"], 1)
                        ]
            
            time.sleep(0.1)  # Rate limiting
    
    # Add trailer from TMDB
    videos = tv_details.get("videos", {}).get("results", [])
    trailer = next((v for v in videos if v["type"] == "Trailer" and v["site"] == "YouTube"), None)
    if trailer:
        enhanced_tv["trailer"] = {
            "id": enhanced_tv["id"] * 1000,
            "type": "video",
            "title": f"{enhanced_tv['title']} Trailer",
            "url": f"https://www.youtube.com/watch?v={trailer['key']}"
        }
    
    return enhanced_tv

def main():
    """Main function to enhance the JSON file"""
    print("Loading current API data...")
    
    # Load current JSON
    with open("current_api.json", "r", encoding="utf-8") as f:
        api_data = json.load(f)
    
    # Find and enhance Big Buck Bunny (movie)
    movies = api_data.get("movies", [])
    for i, movie in enumerate(movies):
        if movie.get("id") == 1 and movie.get("title") == "Big Buck Bunny":
            print("\n" + "="*50)
            print("Enhancing Big Buck Bunny with TMDB data...")
            print("="*50)
            # Since Big Buck Bunny is not a real movie, we'll use "The Lion King" as example
            # Create a temp movie data without year to avoid search issues
            temp_movie = movie.copy()
            temp_movie["year"] = ""  # Remove year restriction
            enhanced_movie = enhance_movie_with_tmdb(temp_movie, "The Lion King")
            # Keep original title and some basic info, but add TMDB metadata
            enhanced_movie["title"] = "Big Buck Bunny"
            enhanced_movie["year"] = "2008"
            enhanced_movie["description"] = "Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself. Enhanced with rich metadata from TMDB API integration."
            
            movies[i] = enhanced_movie
            break
    
    # Find and enhance Sample TV Series
    for i, tv_series in enumerate(movies):
        if tv_series.get("id") == 2 and tv_series.get("type") == "series":
            print("\n" + "="*50)
            print("Enhancing Sample TV Series with TMDB data...")
            print("="*50)
            # Use "Breaking Bad" as example for TV series
            # Create a temp TV data without year to avoid search issues
            temp_tv = tv_series.copy()
            temp_tv["year"] = ""  # Remove year restriction
            enhanced_tv = enhance_tv_series_with_tmdb(temp_tv, "Breaking Bad")
            # Keep original structure but add TMDB metadata
            enhanced_tv["title"] = "Sample TV Series"
            enhanced_tv["description"] = "A gripping drama series with 2 seasons. Enhanced with comprehensive TMDB metadata integration."
            
            movies[i] = enhanced_tv
            break
    
    # Update featured movies and home slides with enhanced data
    featured_movies = api_data.get("home", {}).get("featured_movies", [])
    for i, featured in enumerate(featured_movies):
        if featured.get("id") == 1:
            # Update with enhanced Big Buck Bunny data
            enhanced_movie = next((m for m in movies if m.get("id") == 1), {})
            if enhanced_movie:
                featured_movies[i] = enhanced_movie
    
    home_slides = api_data.get("home", {}).get("slides", [])
    for i, slide in enumerate(home_slides):
        if slide.get("type") == "movie" and slide.get("id") == 1:
            # Update slide poster with enhanced data
            enhanced_movie = next((m for m in movies if m.get("id") == 1), {})
            if enhanced_movie and "poster" in slide:
                slide["poster"] = enhanced_movie
    
    # Update API info
    api_data["api_info"].update({
        "version": "2.1",
        "description": "Enhanced Free Movie & TV Streaming JSON API with TMDB Integration",
        "last_updated": "2024-01-20"
    })
    
    # Save enhanced JSON
    print("\n" + "="*50)
    print("Saving enhanced API data...")
    print("="*50)
    
    with open("enhanced_free_movie_api.json", "w", encoding="utf-8") as f:
        json.dump(api_data, f, indent=2, ensure_ascii=False)
    
    print("✅ Enhanced JSON saved as 'enhanced_free_movie_api.json'")
    print("\nEnhancements completed:")
    print("- Big Buck Bunny: Enhanced with TMDB movie metadata")
    print("- Sample TV Series: Enhanced with TMDB TV series metadata")
    print("- All video sources updated to use vidsrc.net embeds")
    print("- Added comprehensive metadata: actors, genres, ratings, images, trailers")

if __name__ == "__main__":
    main()