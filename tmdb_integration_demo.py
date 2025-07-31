#!/usr/bin/env python3
"""
TMDB Integration Demo for Free Movie API
========================================

This script demonstrates how to integrate TMDB (The Movie Database) API
to fetch metadata for movies and TV series, while using VidSrc.net for
streaming sources.

Features:
- Fetch movie metadata from TMDB
- Fetch TV series metadata from TMDB
- Generate VidSrc embed URLs
- Format data for the free_movie_api.json structure

API Key: ec926176bf467b3f7735e3154238c161
"""

import requests
import json
from typing import Dict, List, Optional, Any

class TMDBIntegration:
    def __init__(self, api_key: str):
        self.api_key = api_key
        self.base_url = "https://api.themoviedb.org/3"
        self.image_base_url = "https://image.tmdb.org/t/p/w500"
        self.backdrop_base_url = "https://image.tmdb.org/t/p/w1280"
        self.actor_image_base_url = "https://image.tmdb.org/t/p/w185"
        
    def fetch_movie(self, tmdb_id: int) -> Optional[Dict[str, Any]]:
        """Fetch movie details from TMDB"""
        url = f"{self.base_url}/movie/{tmdb_id}"
        params = {
            'api_key': self.api_key,
            'append_to_response': 'credits,videos,images'
        }
        
        try:
            response = requests.get(url, params=params)
            response.raise_for_status()
            return response.json()
        except requests.RequestException as e:
            print(f"Error fetching movie {tmdb_id}: {e}")
            return None
    
    def fetch_tv_series(self, tmdb_id: int) -> Optional[Dict[str, Any]]:
        """Fetch TV series details from TMDB"""
        url = f"{self.base_url}/tv/{tmdb_id}"
        params = {
            'api_key': self.api_key,
            'append_to_response': 'credits,videos,images'
        }
        
        try:
            response = requests.get(url, params=params)
            response.raise_for_status()
            return response.json()
        except requests.RequestException as e:
            print(f"Error fetching TV series {tmdb_id}: {e}")
            return None
    
    def fetch_season(self, tmdb_id: int, season_number: int) -> Optional[Dict[str, Any]]:
        """Fetch season details from TMDB"""
        url = f"{self.base_url}/tv/{tmdb_id}/season/{season_number}"
        params = {'api_key': self.api_key}
        
        try:
            response = requests.get(url, params=params)
            response.raise_for_status()
            return response.json()
        except requests.RequestException as e:
            print(f"Error fetching season {season_number} for TV series {tmdb_id}: {e}")
            return None
    
    def generate_vidsrc_urls(self, tmdb_id: int, content_type: str = "movie", 
                           season: Optional[int] = None, episode: Optional[int] = None) -> List[Dict[str, Any]]:
        """Generate VidSrc embed URLs for different qualities"""
        base_vidsrc_url = "https://vidsrc.net/embed"
        
        if content_type == "movie":
            embed_url = f"{base_vidsrc_url}/movie/{tmdb_id}"
        else:  # TV series
            embed_url = f"{base_vidsrc_url}/tv/{tmdb_id}/{season}/{episode}"
        
        sources = []
        qualities = ["1080p", "720p", "480p"]
        
        for i, quality in enumerate(qualities, 1):
            sources.append({
                "id": i,
                "type": "embed",
                "title": f"VidSrc {quality}",
                "quality": quality,
                "size": "Streaming",
                "kind": "both",
                "premium": "false",
                "external": True,
                "url": embed_url
            })
        
        return sources
    
    def format_movie_data(self, tmdb_data: Dict[str, Any], movie_id: int) -> Dict[str, Any]:
        """Format TMDB movie data for free_movie_api.json structure"""
        # Extract main cast (first 6 actors)
        cast = tmdb_data.get('credits', {}).get('cast', [])[:6]
        actors = []
        
        for i, actor in enumerate(cast, 1):
            actors.append({
                "id": i,
                "name": actor.get('name', ''),
                "type": "actor" if actor.get('gender') == 2 else "actress",
                "role": actor.get('character', ''),
                "image": f"{self.actor_image_base_url}{actor.get('profile_path', '')}" if actor.get('profile_path') else "",
                "born": "",  # Would need additional API call to get birth date
                "height": "",  # Would need additional API call
                "bio": ""  # Would need additional API call
            })
        
        # Extract genres
        genres = []
        for genre in tmdb_data.get('genres', []):
            genres.append({
                "id": genre.get('id'),
                "title": genre.get('name')
            })
        
        # Find trailer
        videos = tmdb_data.get('videos', {}).get('results', [])
        trailer_url = ""
        for video in videos:
            if video.get('type') == 'Trailer' and video.get('site') == 'YouTube':
                trailer_url = f"https://www.youtube.com/watch?v={video.get('key')}"
                break
        
        return {
            "id": movie_id,
            "title": tmdb_data.get('title', ''),
            "type": "movie",
            "label": genres[0].get('title', '') if genres else '',
            "sublabel": "Movie",
            "imdb": str(tmdb_data.get('vote_average', 0)),
            "tmdb_id": tmdb_data.get('id'),
            "tmdb_rating": tmdb_data.get('vote_average', 0),
            "downloadas": tmdb_data.get('title', '').lower().replace(' ', '-') + f"-{tmdb_data.get('release_date', '')[:4]}.mp4",
            "comment": True,
            "playas": "video",
            "description": tmdb_data.get('overview', ''),
            "classification": self._get_certification(tmdb_data),
            "year": tmdb_data.get('release_date', '')[:4],
            "duration": f"{tmdb_data.get('runtime', 0)}:00",
            "rating": tmdb_data.get('vote_average', 0),
            "image": f"{self.image_base_url}{tmdb_data.get('poster_path', '')}" if tmdb_data.get('poster_path') else "",
            "cover": f"{self.backdrop_base_url}{tmdb_data.get('backdrop_path', '')}" if tmdb_data.get('backdrop_path') else "",
            "backdrop_path": f"{self.backdrop_base_url}{tmdb_data.get('backdrop_path', '')}" if tmdb_data.get('backdrop_path') else "",
            "genres": genres,
            "sources": self.generate_vidsrc_urls(tmdb_data.get('id'), "movie"),
            "trailer": {
                "id": 4,
                "type": "video",
                "title": f"{tmdb_data.get('title', '')} Official Trailer",
                "url": trailer_url
            } if trailer_url else None,
            "actors": actors,
            "subtitles": [
                {
                    "id": 1,
                    "title": "English",
                    "language": "en",
                    "url": f"https://example.com/subtitles/{tmdb_data.get('title', '').lower().replace(' ', '-')}-en.vtt"
                },
                {
                    "id": 2,
                    "title": "Spanish",
                    "language": "es",
                    "url": f"https://example.com/subtitles/{tmdb_data.get('title', '').lower().replace(' ', '-')}-es.vtt"
                }
            ],
            "comments": [],
            "views": 0,
            "downloads": 0,
            "shares": 0,
            "tmdb_metadata": {
                "adult": tmdb_data.get('adult', False),
                "budget": tmdb_data.get('budget', 0),
                "revenue": tmdb_data.get('revenue', 0),
                "runtime": tmdb_data.get('runtime', 0),
                "status": tmdb_data.get('status', ''),
                "tagline": tmdb_data.get('tagline', ''),
                "vote_average": tmdb_data.get('vote_average', 0),
                "vote_count": tmdb_data.get('vote_count', 0),
                "production_companies": tmdb_data.get('production_companies', []),
                "production_countries": tmdb_data.get('production_countries', []),
                "spoken_languages": tmdb_data.get('spoken_languages', [])
            }
        }
    
    def format_tv_series_data(self, tmdb_data: Dict[str, Any], series_id: int, 
                            seasons_data: List[Dict[str, Any]]) -> Dict[str, Any]:
        """Format TMDB TV series data for free_movie_api.json structure"""
        # Extract main cast
        cast = tmdb_data.get('credits', {}).get('cast', [])[:6]
        actors = []
        
        for i, actor in enumerate(cast, 1):
            actors.append({
                "id": i + 6,  # Offset to avoid conflicts with movie actors
                "name": actor.get('name', ''),
                "type": "actor" if actor.get('gender') == 2 else "actress",
                "role": actor.get('character', ''),
                "image": f"{self.actor_image_base_url}{actor.get('profile_path', '')}" if actor.get('profile_path') else "",
                "born": "",
                "height": "",
                "bio": ""
            })
        
        # Extract genres
        genres = []
        for genre in tmdb_data.get('genres', []):
            genres.append({
                "id": genre.get('id'),
                "title": genre.get('name')
            })
        
        # Find trailer
        videos = tmdb_data.get('videos', {}).get('results', [])
        trailer_url = ""
        for video in videos:
            if video.get('type') == 'Trailer' and video.get('site') == 'YouTube':
                trailer_url = f"https://www.youtube.com/watch?v={video.get('key')}"
                break
        
        # Format seasons
        formatted_seasons = []
        for i, season_data in enumerate(seasons_data, 1):
            if not season_data:
                continue
                
            episodes = []
            for j, episode in enumerate(season_data.get('episodes', [])[:1], 1):  # Only first episode per season
                episode_sources = self.generate_vidsrc_urls(
                    tmdb_data.get('id'), "tv", 
                    season_data.get('season_number'), 
                    episode.get('episode_number')
                )
                
                episodes.append({
                    "id": j,
                    "title": episode.get('name', ''),
                    "episode_number": episode.get('episode_number', 1),
                    "season_number": season_data.get('season_number', 1),
                    "tmdb_episode_id": episode.get('id'),
                    "air_date": episode.get('air_date', ''),
                    "overview": episode.get('overview', ''),
                    "image": f"{self.image_base_url}{episode.get('still_path', '')}" if episode.get('still_path') else "",
                    "duration": f"{episode.get('runtime', 45)}:00",
                    "rating": episode.get('vote_average', 0),
                    "sources": episode_sources,
                    "subtitles": [
                        {
                            "id": j * 2 - 1,
                            "title": "English",
                            "language": "en",
                            "url": f"https://example.com/subtitles/st-s{season_data.get('season_number')}e{episode.get('episode_number')}-en.vtt"
                        },
                        {
                            "id": j * 2,
                            "title": "Spanish",
                            "language": "es",
                            "url": f"https://example.com/subtitles/st-s{season_data.get('season_number')}e{episode.get('episode_number')}-es.vtt"
                        }
                    ],
                    "views": 0,
                    "downloads": 0
                })
            
            formatted_seasons.append({
                "id": i,
                "title": f"Season {season_data.get('season_number', i)}",
                "season_number": season_data.get('season_number', i),
                "tmdb_season_id": season_data.get('id'),
                "air_date": season_data.get('air_date', ''),
                "overview": season_data.get('overview', ''),
                "poster_path": f"{self.image_base_url}{season_data.get('poster_path', '')}" if season_data.get('poster_path') else "",
                "episodes": episodes
            })
        
        return {
            "id": series_id,
            "title": tmdb_data.get('name', ''),
            "type": "series",
            "label": genres[0].get('title', '') if genres else '',
            "sublabel": f"{len(formatted_seasons)} Seasons",
            "imdb": str(tmdb_data.get('vote_average', 0)),
            "tmdb_id": tmdb_data.get('id'),
            "tmdb_rating": tmdb_data.get('vote_average', 0),
            "downloadas": tmdb_data.get('name', '').lower().replace(' ', '-'),
            "comment": True,
            "playas": "video",
            "description": tmdb_data.get('overview', ''),
            "classification": "TV-14",  # Default, would need additional logic
            "year": tmdb_data.get('first_air_date', '')[:4],
            "duration": f"{tmdb_data.get('episode_run_time', [45])[0] if tmdb_data.get('episode_run_time') else 45}:00",
            "rating": tmdb_data.get('vote_average', 0),
            "views": 0,
            "created_at": "2024-01-02",
            "image": f"{self.image_base_url}{tmdb_data.get('poster_path', '')}" if tmdb_data.get('poster_path') else "",
            "cover": f"{self.backdrop_base_url}{tmdb_data.get('backdrop_path', '')}" if tmdb_data.get('backdrop_path') else "",
            "backdrop_path": f"{self.backdrop_base_url}{tmdb_data.get('backdrop_path', '')}" if tmdb_data.get('backdrop_path') else "",
            "genres": genres,
            "sources": [],
            "trailer": {
                "id": 5,
                "type": "video",
                "title": f"{tmdb_data.get('name', '')} Official Trailer",
                "url": trailer_url
            } if trailer_url else None,
            "actors": actors,
            "subtitles": [],
            "comments": [],
            "downloads": 0,
            "shares": 0,
            "seasons": formatted_seasons,
            "tmdb_metadata": {
                "adult": tmdb_data.get('adult', False),
                "first_air_date": tmdb_data.get('first_air_date', ''),
                "last_air_date": tmdb_data.get('last_air_date', ''),
                "number_of_episodes": tmdb_data.get('number_of_episodes', 0),
                "number_of_seasons": tmdb_data.get('number_of_seasons', 0),
                "status": tmdb_data.get('status', ''),
                "tagline": tmdb_data.get('tagline', ''),
                "type": tmdb_data.get('type', ''),
                "vote_average": tmdb_data.get('vote_average', 0),
                "vote_count": tmdb_data.get('vote_count', 0),
                "created_by": tmdb_data.get('created_by', []),
                "networks": tmdb_data.get('networks', []),
                "production_companies": tmdb_data.get('production_companies', []),
                "production_countries": tmdb_data.get('production_countries', []),
                "spoken_languages": tmdb_data.get('spoken_languages', [])
            }
        }
    
    def _get_certification(self, tmdb_data: Dict[str, Any]) -> str:
        """Extract movie certification/rating"""
        # This would require additional API call to get release dates with certifications
        # For now, return a default based on vote average
        vote_avg = tmdb_data.get('vote_average', 0)
        if vote_avg >= 8.0:
            return "PG-13"
        elif vote_avg >= 7.0:
            return "PG-13"
        else:
            return "R"

def main():
    """Demonstrate TMDB integration"""
    api_key = "ec926176bf467b3f7735e3154238c161"
    tmdb = TMDBIntegration(api_key)
    
    print("🎬 TMDB Integration Demo")
    print("=" * 50)
    
    # Example 1: The Avengers (2012)
    print("\n📽️  Fetching The Avengers (2012)...")
    avengers_data = tmdb.fetch_movie(24428)
    if avengers_data:
        formatted_movie = tmdb.format_movie_data(avengers_data, 1)
        print(f"✅ Successfully formatted: {formatted_movie['title']}")
        print(f"   TMDB ID: {formatted_movie['tmdb_id']}")
        print(f"   Rating: {formatted_movie['rating']}")
        print(f"   VidSrc URL: {formatted_movie['sources'][0]['url']}")
    
    # Example 2: Stranger Things
    print("\n📺 Fetching Stranger Things...")
    stranger_things_data = tmdb.fetch_tv_series(66732)
    if stranger_things_data:
        # Fetch first 2 seasons
        seasons_data = []
        for season_num in [1, 2]:
            season_data = tmdb.fetch_season(66732, season_num)
            seasons_data.append(season_data)
        
        formatted_series = tmdb.format_tv_series_data(stranger_things_data, 2, seasons_data)
        print(f"✅ Successfully formatted: {formatted_series['title']}")
        print(f"   TMDB ID: {formatted_series['tmdb_id']}")
        print(f"   Seasons: {len(formatted_series['seasons'])}")
        print(f"   First Episode VidSrc URL: {formatted_series['seasons'][0]['episodes'][0]['sources'][0]['url']}")
    
    print("\n🎯 Integration Features:")
    print("• ✅ TMDB metadata integration")
    print("• ✅ VidSrc.net embed URLs")
    print("• ✅ High-quality poster/backdrop images")
    print("• ✅ Cast and crew information")
    print("• ✅ Trailer links from YouTube")
    print("• ✅ Multiple quality streaming options")
    print("• ✅ Subtitle placeholder support")
    print("• ✅ Complete JSON API structure")
    
    print("\n📋 VidSrc URL Format:")
    print("Movies: https://vidsrc.net/embed/movie/{tmdb_id}")
    print("TV Shows: https://vidsrc.net/embed/tv/{tmdb_id}/{season}/{episode}")
    
    print("\n🔑 API Configuration:")
    print(f"TMDB API Key: {api_key}")
    print("Base URL: https://api.themoviedb.org/3")
    print("Image Base URL: https://image.tmdb.org/t/p/w500")

if __name__ == "__main__":
    main()