#!/usr/bin/env python3
"""
TMDB Integration Script for Movie API Enhancement
Automatically fetches metadata from TMDB API for movies and TV series
"""

import json
import requests
import time
from typing import Dict, List, Any, Optional

class TMDBIntegrator:
    def __init__(self, api_key: str):
        self.api_key = api_key
        self.base_url = "https://api.themoviedb.org/3"
        self.image_base_url = "https://image.tmdb.org/t/p/w500"
        self.session = requests.Session()
        
    def search_movie(self, title: str, year: Optional[str] = None) -> Optional[Dict]:
        """Search for a movie on TMDB"""
        params = {
            'api_key': self.api_key,
            'query': title,
            'language': 'en-US'
        }
        if year:
            params['year'] = year
            
        try:
            response = self.session.get(f"{self.base_url}/search/movie", params=params)
            response.raise_for_status()
            data = response.json()
            
            if data['results']:
                return data['results'][0]  # Return first result
            return None
        except Exception as e:
            print(f"Error searching movie {title}: {e}")
            return None
    
    def search_tv(self, title: str, year: Optional[str] = None) -> Optional[Dict]:
        """Search for a TV series on TMDB"""
        params = {
            'api_key': self.api_key,
            'query': title,
            'language': 'en-US'
        }
        if year:
            params['first_air_date_year'] = year
            
        try:
            response = self.session.get(f"{self.base_url}/search/tv", params=params)
            response.raise_for_status()
            data = response.json()
            
            if data['results']:
                return data['results'][0]
            return None
        except Exception as e:
            print(f"Error searching TV series {title}: {e}")
            return None
    
    def get_movie_details(self, movie_id: int) -> Optional[Dict]:
        """Get detailed movie information"""
        params = {
            'api_key': self.api_key,
            'language': 'en-US',
            'append_to_response': 'credits,videos,genres'
        }
        
        try:
            response = self.session.get(f"{self.base_url}/movie/{movie_id}", params=params)
            response.raise_for_status()
            return response.json()
        except Exception as e:
            print(f"Error getting movie details for ID {movie_id}: {e}")
            return None
    
    def get_tv_details(self, tv_id: int) -> Optional[Dict]:
        """Get detailed TV series information"""
        params = {
            'api_key': self.api_key,
            'language': 'en-US',
            'append_to_response': 'credits,videos,genres'
        }
        
        try:
            response = self.session.get(f"{self.base_url}/tv/{tv_id}", params=params)
            response.raise_for_status()
            return response.json()
        except Exception as e:
            print(f"Error getting TV details for ID {tv_id}: {e}")
            return None
    
    def get_tv_season_details(self, tv_id: int, season_number: int) -> Optional[Dict]:
        """Get TV season details"""
        params = {
            'api_key': self.api_key,
            'language': 'en-US'
        }
        
        try:
            response = self.session.get(f"{self.base_url}/tv/{tv_id}/season/{season_number}", params=params)
            response.raise_for_status()
            return response.json()
        except Exception as e:
            print(f"Error getting TV season details: {e}")
            return None
    
    def format_movie_data(self, tmdb_data: Dict, original_sources: List[Dict]) -> Dict:
        """Format TMDB movie data to match the API structure"""
        # Get trailer from videos
        trailer_url = ""
        if 'videos' in tmdb_data and tmdb_data['videos']['results']:
            for video in tmdb_data['videos']['results']:
                if video['type'] == 'Trailer' and video['site'] == 'YouTube':
                    trailer_url = f"https://www.youtube.com/watch?v={video['key']}"
                    break
        
        # Get main actors
        actors = []
        if 'credits' in tmdb_data and tmdb_data['credits']['cast']:
            for i, actor in enumerate(tmdb_data['credits']['cast'][:5]):  # Top 5 actors
                actors.append({
                    "id": i + 1,
                    "name": actor['name'],
                    "type": "actor",
                    "role": actor['character'],
                    "image": "",  # As requested, no image links
                    "born": "",
                    "height": "",
                    "bio": ""
                })
        
        # Get genres
        genres = []
        if tmdb_data.get('genres'):
            for genre in tmdb_data['genres']:
                genres.append({
                    "id": genre['id'],
                    "title": genre['name']
                })
        
        # Add VidSrc sources
        enhanced_sources = []
        enhanced_sources.extend(original_sources)  # Keep original sources
        
        # Add VidSrc source
        enhanced_sources.append({
            "id": len(original_sources) + 1,
            "type": "video",
            "title": f"{tmdb_data['title']} - VidSrc",
            "quality": "HD",
            "size": "Auto",
            "kind": "both",
            "premium": "false",
            "external": True,
            "url": f"https://vidsrc.net/embed/movie/{tmdb_data['id']}"
        })
        
        return {
            "id": 1,
            "title": tmdb_data['title'],
            "type": "movie",
            "label": genres[0]['title'] if genres else "Movie",
            "sublabel": f"TMDB Rating: {tmdb_data.get('vote_average', 0):.1f}",
            "imdb": str(tmdb_data.get('vote_average', 0)),
            "downloadas": f"{tmdb_data['title'].lower().replace(' ', '-')}.mp4",
            "comment": True,
            "playas": "video",
            "description": tmdb_data.get('overview', ''),
            "classification": "NR",  # Not rated by default
            "year": tmdb_data.get('release_date', '')[:4] if tmdb_data.get('release_date') else "",
            "duration": f"{tmdb_data.get('runtime', 0)}:00" if tmdb_data.get('runtime') else "120:00",
            "rating": tmdb_data.get('vote_average', 0),
            "image": "",  # As requested, no image links
            "cover": "",  # As requested, no image links
            "genres": genres,
            "sources": enhanced_sources,
            "trailer": {
                "id": len(enhanced_sources) + 1,
                "type": "video",
                "title": f"{tmdb_data['title']} Trailer",
                "url": trailer_url
            },
            "actors": actors,
            "subtitles": [
                {
                    "id": 1,
                    "title": "English",
                    "language": "en",
                    "url": f"https://example.com/subtitles/{tmdb_data['title'].lower().replace(' ', '-')}-en.vtt"
                }
            ]
        }
    
    def format_tv_data(self, tmdb_data: Dict, season_data: List[Dict], original_sources: List[Dict]) -> Dict:
        """Format TMDB TV series data to match the API structure"""
        # Get trailer from videos
        trailer_url = ""
        if 'videos' in tmdb_data and tmdb_data['videos']['results']:
            for video in tmdb_data['videos']['results']:
                if video['type'] == 'Trailer' and video['site'] == 'YouTube':
                    trailer_url = f"https://www.youtube.com/watch?v={video['key']}"
                    break
        
        # Get main actors
        actors = []
        if 'credits' in tmdb_data and tmdb_data['credits']['cast']:
            for i, actor in enumerate(tmdb_data['credits']['cast'][:5]):
                actors.append({
                    "id": i + 1,
                    "name": actor['name'],
                    "type": "actor",
                    "role": actor['character'],
                    "image": "",
                    "born": "",
                    "height": "",
                    "bio": ""
                })
        
        # Get genres
        genres = []
        if tmdb_data.get('genres'):
            for genre in tmdb_data['genres']:
                genres.append({
                    "id": genre['id'],
                    "title": genre['name']
                })
        
        # Format seasons
        seasons = []
        for season_num, season_info in enumerate(season_data, 1):
            episodes = []
            if season_info and season_info.get('episodes'):
                for ep_num, episode in enumerate(season_info['episodes'][:1], 1):  # 1 episode per season as requested
                    # Add VidSrc source for episode
                    episode_sources = []
                    episode_sources.extend(original_sources)
                    episode_sources.append({
                        "id": len(original_sources) + 1,
                        "type": "video",
                        "title": f"{episode['name']} - VidSrc",
                        "quality": "HD",
                        "size": "Auto",
                        "kind": "both",
                        "premium": "false",
                        "external": True,
                        "url": f"https://vidsrc.net/embed/tv/{tmdb_data['id']}/{season_num}/{ep_num}"
                    })
                    
                    episodes.append({
                        "id": ep_num,
                        "title": episode['name'],
                        "description": episode.get('overview', ''),
                        "duration": "45:00",  # Default duration
                        "rating": episode.get('vote_average', 0),
                        "air_date": episode.get('air_date', ''),
                        "image": "",
                        "sources": episode_sources
                    })
            
            seasons.append({
                "id": season_num,
                "title": f"Season {season_num}",
                "description": f"Season {season_num} of {tmdb_data['name']}",
                "episodes": episodes
            })
        
        return {
            "id": 2,
            "title": tmdb_data['name'],
            "type": "series",
            "label": genres[0]['title'] if genres else "TV Series",
            "sublabel": f"TMDB Rating: {tmdb_data.get('vote_average', 0):.1f}",
            "imdb": str(tmdb_data.get('vote_average', 0)),
            "comment": True,
            "playas": "video",
            "description": tmdb_data.get('overview', ''),
            "classification": "NR",
            "year": tmdb_data.get('first_air_date', '')[:4] if tmdb_data.get('first_air_date') else "",
            "rating": tmdb_data.get('vote_average', 0),
            "image": "",
            "cover": "",
            "genres": genres,
            "trailer": {
                "id": 999,
                "type": "video",
                "title": f"{tmdb_data['name']} Trailer",
                "url": trailer_url
            },
            "actors": actors,
            "seasons": seasons
        }

def main():
    # TMDB API Key
    API_KEY = "ec926176bf467b3f7735e3154238c161"
    
    # Initialize TMDB integrator
    tmdb = TMDBIntegrator(API_KEY)
    
    # Load original JSON
    with open('original_api.json', 'r') as f:
        original_data = json.load(f)
    
    print("Starting TMDB integration...")
    
    # Search for Avengers movie
    print("Searching for Avengers movie...")
    avengers_search = tmdb.search_movie("The Avengers", "2012")
    
    if avengers_search:
        print(f"Found: {avengers_search['title']} ({avengers_search['release_date'][:4]})")
        avengers_details = tmdb.get_movie_details(avengers_search['id'])
        
        if avengers_details:
            # Get original sources from Big Buck Bunny
            original_sources = original_data['home']['slides'][0]['poster']['sources']
            
            # Format Avengers data
            avengers_formatted = tmdb.format_movie_data(avengers_details, original_sources)
            
            # Replace Big Buck Bunny with Avengers
            original_data['home']['slides'][0]['title'] = avengers_formatted['title']
            original_data['home']['slides'][0]['poster'] = avengers_formatted
            
            print("✓ Avengers movie data integrated successfully!")
    
    # Search for Breaking Bad TV series
    print("\nSearching for Breaking Bad TV series...")
    bb_search = tmdb.search_tv("Breaking Bad", "2008")
    
    if bb_search:
        print(f"Found: {bb_search['name']} ({bb_search['first_air_date'][:4]})")
        bb_details = tmdb.get_tv_details(bb_search['id'])
        
        if bb_details:
            # Get season details for first 2 seasons
            season_data = []
            for season_num in [1, 2]:
                season_info = tmdb.get_tv_season_details(bb_search['id'], season_num)
                season_data.append(season_info)
                time.sleep(0.25)  # Rate limiting
            
            # Get original sources
            original_sources = original_data['home']['slides'][0]['poster']['sources']
            
            # Format Breaking Bad data
            bb_formatted = tmdb.format_tv_data(bb_details, season_data, original_sources)
            
            # Add Breaking Bad as second slide
            bb_slide = {
                "id": 2,
                "title": bb_formatted['title'],
                "type": "series",
                "image": "",
                "url": "series/2",
                "poster": bb_formatted
            }
            
            # Add to slides if not already there
            if len(original_data['home']['slides']) < 2:
                original_data['home']['slides'].append(bb_slide)
            else:
                original_data['home']['slides'][1] = bb_slide
            
            print("✓ Breaking Bad TV series data integrated successfully!")
    
    # Update API info
    original_data['api_info']['description'] = "Enhanced Free Movie & TV Streaming JSON API with TMDB Integration"
    original_data['api_info']['last_updated'] = "2024-12-19"
    original_data['api_info']['total_movies'] = 1
    original_data['api_info']['total_series'] = 1
    
    # Save enhanced JSON
    with open('enhanced_api.json', 'w') as f:
        json.dump(original_data, f, indent=2)
    
    print("\n✓ TMDB integration completed!")
    print("✓ Enhanced API saved as 'enhanced_api.json'")
    print("\nKey features added:")
    print("- Real movie metadata from TMDB (Avengers)")
    print("- Real TV series metadata from TMDB (Breaking Bad)")
    print("- VidSrc.net sources auto-added")
    print("- YouTube trailers integrated")
    print("- Actor information populated")
    print("- No image links added (as requested)")
    print("- Live TV channels preserved unchanged")

if __name__ == "__main__":
    main()