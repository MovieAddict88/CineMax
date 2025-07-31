#!/usr/bin/env python3
"""
Create Enhanced Example JSON with Real TMDB Data
This script creates an example with Big Buck Bunny (movie) and Breaking Bad (TV series)
with proper TMDB metadata and vidsrc.net sources.
"""

import json
import requests
from datetime import datetime

def get_breaking_bad_tmdb_data():
    """Get Breaking Bad data from TMDB"""
    api_key = "ec926176bf467b3f7735e3154238c161"
    
    # Breaking Bad TMDB ID is 1396
    tv_id = 1396
    
    # Get TV series details
    url = f"https://api.themoviedb.org/3/tv/{tv_id}"
    params = {
        'api_key': api_key,
        'append_to_response': 'credits,videos,images,content_ratings'
    }
    
    response = requests.get(url, params=params)
    if response.status_code == 200:
        return response.json()
    return None

def get_season_details(tv_id, season_num):
    """Get season details from TMDB"""
    api_key = "ec926176bf467b3f7735e3154238c161"
    
    url = f"https://api.themoviedb.org/3/tv/{tv_id}/season/{season_num}"
    params = {'api_key': api_key}
    
    response = requests.get(url, params=params)
    if response.status_code == 200:
        return response.json()
    return None

def create_enhanced_example():
    """Create enhanced example JSON with real TMDB data"""
    
    # Load the current enhanced JSON
    with open('enhanced_movie_api.json', 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    # Get Breaking Bad data from TMDB
    print("Fetching Breaking Bad data from TMDB...")
    bb_data = get_breaking_bad_tmdb_data()
    
    if not bb_data:
        print("Failed to fetch Breaking Bad data")
        return
    
    print(f"Successfully fetched: {bb_data['name']} ({bb_data['first_air_date'][:4]})")
    
    # Create Breaking Bad entry with TMDB data
    breaking_bad = {
        "id": 3,
        "title": bb_data['name'],
        "type": "series",
        "label": bb_data['genres'][0]['name'] if bb_data['genres'] else 'Drama',
        "sublabel": f"{bb_data['number_of_seasons']} Seasons",
        "imdb": str(bb_data['vote_average']),
        "downloadas": "breaking-bad",
        "comment": True,
        "playas": "video",
        "description": bb_data['overview'],
        "classification": "TV-MA",
        "year": bb_data['first_air_date'][:4],
        "duration": f"{bb_data['episode_run_time'][0] if bb_data['episode_run_time'] else 47}:00",
        "rating": bb_data['vote_average'],
        "views": int(bb_data['popularity']),
        "created_at": "2024-01-03",
        "image": f"https://image.tmdb.org/t/p/w500{bb_data['poster_path']}" if bb_data.get('poster_path') else "",
        "cover": f"https://image.tmdb.org/t/p/w1280{bb_data['backdrop_path']}" if bb_data.get('backdrop_path') else "",
        "genres": [{"id": genre['id'], "title": genre['name']} for genre in bb_data['genres']],
        "sources": [{
            "id": 1,
            "type": "embed",
            "title": f"{bb_data['name']} - VidSrc",
            "quality": "Auto",
            "size": "Stream",
            "kind": "both",
            "premium": "false",
            "external": True,
            "url": f"https://vidsrc.net/embed/tv/{bb_data['id']}"
        }],
        "trailer": None,
        "actors": [],
        "subtitles": [],
        "comments": [{
            "id": 1,
            "user": "TV Fan",
            "comment": "One of the greatest TV series ever made!",
            "created_at": "2024-01-15T14:30:00Z"
        }],
        "downloads": 15000,
        "shares": 8500,
        "tmdb_id": bb_data['id'],
        "countries": bb_data.get('origin_country', []),
        "seasons": []
    }
    
    # Add cast information
    if 'credits' in bb_data and 'cast' in bb_data['credits']:
        for i, actor in enumerate(bb_data['credits']['cast'][:5]):
            breaking_bad['actors'].append({
                "id": actor['id'],
                "name": actor['name'],
                "type": "actor" if actor['gender'] != 1 else "actress",
                "role": actor.get('character', 'Unknown'),
                "image": f"https://image.tmdb.org/t/p/w300{actor['profile_path']}" if actor.get('profile_path') else "",
                "born": "",
                "height": "",
                "bio": ""
            })
    
    # Create 2 seasons with 1 episode each (as requested)
    print("Creating seasons with vidsrc.net sources...")
    for season_num in [1, 2]:
        season_data = get_season_details(bb_data['id'], season_num)
        
        if season_data and season_data.get('episodes'):
            episode = season_data['episodes'][0]  # Get first episode
            
            season = {
                "id": season_num,
                "title": f"Season {season_num}",
                "episodes": [{
                    "id": season_num,
                    "title": episode.get('name', f"Episode 1"),
                    "episode_number": 1,
                    "description": episode.get('overview', ''),
                    "image": f"https://image.tmdb.org/t/p/w500{episode['still_path']}" if episode.get('still_path') else "",
                    "duration": f"{bb_data['episode_run_time'][0] if bb_data['episode_run_time'] else 47}:00",
                    "sources": [{
                        "id": 1,
                        "type": "embed",
                        "title": f"{bb_data['name']} S{season_num}E1 - VidSrc",
                        "quality": "Auto",
                        "size": "Stream",
                        "kind": "both",
                        "premium": "false",
                        "external": True,
                        "url": f"https://vidsrc.net/embed/tv/{bb_data['id']}/{season_num}/1"
                    }],
                    "subtitles": [{
                        "id": 1,
                        "title": "English",
                        "language": "en",
                        "url": f"https://example.com/subtitles/breaking-bad-s{season_num}e1-en.vtt"
                    }],
                    "views": 25000 - (season_num * 5000),
                    "downloads": 8000 - (season_num * 2000)
                }]
            }
            breaking_bad['seasons'].append(season)
    
    # Add Breaking Bad to the movies array (it contains both movies and series)
    data['movies'].append(breaking_bad)
    
    # Update API info
    data['api_info']['total_movies'] = len(data['movies'])
    data['api_info']['last_updated'] = datetime.now().strftime('%Y-%m-%d')
    data['api_info']['description'] = "Enhanced Free Movie & TV Streaming JSON API with TMDB Auto-Detection - Example with Breaking Bad"
    
    # Save the enhanced example
    output_file = 'enhanced_example_with_breaking_bad.json'
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    
    print(f"✅ Enhanced example created successfully!")
    print(f"📁 File saved as: {output_file}")
    print(f"📺 Added: {breaking_bad['title']} with {len(breaking_bad['seasons'])} seasons")
    print(f"🎬 Movie sources: vidsrc.net/embed/movie/10378")
    print(f"📺 TV sources: vidsrc.net/embed/tv/{bb_data['id']}/[season]/[episode]")

if __name__ == "__main__":
    create_enhanced_example()