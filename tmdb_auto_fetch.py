#!/usr/bin/env python3
"""
TMDB Auto-Fetch System for Movies and TV Series
==============================================

This system automatically fetches ALL metadata from TMDB API for movies and TV series.
You only need to provide:
- Title (or TMDB ID)
- Sources (manual)

Everything else is automatically fetched:
- Images, posters, backdrops
- Cast and crew information
- Ratings, descriptions, genres
- Release dates, runtime
- Production companies
- Trailers from YouTube
- Season/episode data for TV series

API Key: ec926176bf467b3f7735e3154238c161
"""

import requests
import json
from typing import Dict, List, Optional, Any, Union

class TMDBAutoFetch:
    def __init__(self, api_key: str):
        self.api_key = api_key
        self.base_url = "https://api.themoviedb.org/3"
        self.image_base_url = "https://image.tmdb.org/t/p/w500"
        self.backdrop_base_url = "https://image.tmdb.org/t/p/w1280"
        self.actor_image_base_url = "https://image.tmdb.org/t/p/w185"
        
    def search_content(self, title: str, content_type: str = "multi") -> Optional[Dict[str, Any]]:
        """
        Search for content by title
        content_type: 'movie', 'tv', or 'multi'
        """
        url = f"{self.base_url}/search/{content_type}"
        params = {
            'api_key': self.api_key,
            'query': title,
            'language': 'en-US'
        }
        
        try:
            response = requests.get(url, params=params)
            response.raise_for_status()
            data = response.json()
            
            if data.get('results'):
                # Return the first (most relevant) result
                return data['results'][0]
            return None
        except requests.RequestException as e:
            print(f"Error searching for '{title}': {e}")
            return None
    
    def get_person_details(self, person_id: int) -> Optional[Dict[str, Any]]:
        """Get detailed person information"""
        url = f"{self.base_url}/person/{person_id}"
        params = {'api_key': self.api_key}
        
        try:
            response = requests.get(url, params=params)
            response.raise_for_status()
            return response.json()
        except requests.RequestException as e:
            print(f"Error fetching person {person_id}: {e}")
            return None
    
    def auto_fetch_movie(self, title_or_id: Union[str, int], manual_sources: List[Dict[str, Any]]) -> Optional[Dict[str, Any]]:
        """
        Automatically fetch complete movie data from TMDB
        
        Args:
            title_or_id: Movie title (string) or TMDB ID (integer)
            manual_sources: List of manually provided sources
        
        Returns:
            Complete movie data formatted for free_movie_api.json
        """
        
        # Step 1: Get basic movie info
        if isinstance(title_or_id, str):
            # Search by title
            search_result = self.search_content(title_or_id, "movie")
            if not search_result:
                print(f"❌ Movie '{title_or_id}' not found in TMDB")
                return None
            tmdb_id = search_result['id']
        else:
            # Use provided TMDB ID
            tmdb_id = title_or_id
        
        # Step 2: Fetch complete movie details
        movie_url = f"{self.base_url}/movie/{tmdb_id}"
        params = {
            'api_key': self.api_key,
            'append_to_response': 'credits,videos,images,release_dates'
        }
        
        try:
            response = requests.get(movie_url, params=params)
            response.raise_for_status()
            movie_data = response.json()
        except requests.RequestException as e:
            print(f"❌ Error fetching movie details: {e}")
            return None
        
        # Step 3: Process cast with detailed information
        cast = movie_data.get('credits', {}).get('cast', [])[:6]
        actors = []
        
        for i, actor in enumerate(cast, 1):
            # Get detailed actor information
            person_details = self.get_person_details(actor['id'])
            
            actors.append({
                "id": i,
                "name": actor.get('name', ''),
                "type": "actor" if actor.get('gender') == 2 else "actress",
                "role": actor.get('character', ''),
                "image": f"{self.actor_image_base_url}{actor.get('profile_path')}" if actor.get('profile_path') else "",
                "born": person_details.get('birthday', '') if person_details else '',
                "height": "",  # TMDB doesn't provide height
                "bio": (person_details.get('biography', '')[:200] + '...') if person_details and person_details.get('biography') else '',
                "tmdb_id": actor.get('id')
            })
        
        # Step 4: Extract genres
        genres = []
        for genre in movie_data.get('genres', []):
            genres.append({
                "id": genre.get('id'),
                "title": genre.get('name')
            })
        
        # Step 5: Find trailer
        videos = movie_data.get('videos', {}).get('results', [])
        trailer_url = ""
        for video in videos:
            if video.get('type') == 'Trailer' and video.get('site') == 'YouTube':
                trailer_url = f"https://www.youtube.com/watch?v={video.get('key')}"
                break
        
        # Step 6: Get certification/rating
        release_dates = movie_data.get('release_dates', {}).get('results', [])
        certification = "PG-13"  # Default
        for release in release_dates:
            if release.get('iso_3166_1') == 'US':
                for date_info in release.get('release_dates', []):
                    if date_info.get('certification'):
                        certification = date_info['certification']
                        break
                break
        
        # Step 7: Format complete movie data
        formatted_movie = {
            "id": 1,  # This should be set externally
            "title": movie_data.get('title', ''),
            "type": "movie",
            "label": genres[0].get('title', '') if genres else '',
            "sublabel": "Movie",
            "imdb": str(round(movie_data.get('vote_average', 0), 1)),
            "tmdb_id": movie_data.get('id'),
            "tmdb_rating": movie_data.get('vote_average', 0),
            "downloadas": f"{movie_data.get('title', '').lower().replace(' ', '-')}-{movie_data.get('release_date', '')[:4]}.mp4",
            "comment": True,
            "playas": "video",
            "description": movie_data.get('overview', ''),
            "classification": certification,
            "year": movie_data.get('release_date', '')[:4],
            "duration": f"{movie_data.get('runtime', 0)}:00",
            "rating": movie_data.get('vote_average', 0),
            "views": 0,  # Manual tracking
            "created_at": "2024-01-01",
            "image": f"{self.image_base_url}{movie_data.get('poster_path')}" if movie_data.get('poster_path') else "",
            "cover": f"{self.backdrop_base_url}{movie_data.get('backdrop_path')}" if movie_data.get('backdrop_path') else "",
            "backdrop_path": f"{self.backdrop_base_url}{movie_data.get('backdrop_path')}" if movie_data.get('backdrop_path') else "",
            "genres": genres,
            "sources": manual_sources,  # USER PROVIDED
            "trailer": {
                "id": 4,
                "type": "video",
                "title": f"{movie_data.get('title', '')} Official Trailer",
                "url": trailer_url
            } if trailer_url else None,
            "actors": actors,
            "subtitles": [
                {
                    "id": 1,
                    "title": "English",
                    "language": "en",
                    "url": f"https://example.com/subtitles/{movie_data.get('title', '').lower().replace(' ', '-')}-en.vtt"
                },
                {
                    "id": 2,
                    "title": "Spanish",
                    "language": "es",
                    "url": f"https://example.com/subtitles/{movie_data.get('title', '').lower().replace(' ', '-')}-es.vtt"
                }
            ],
            "comments": [],
            "downloads": 0,
            "shares": 0,
            "tmdb_metadata": {
                "adult": movie_data.get('adult', False),
                "budget": movie_data.get('budget', 0),
                "revenue": movie_data.get('revenue', 0),
                "runtime": movie_data.get('runtime', 0),
                "status": movie_data.get('status', ''),
                "tagline": movie_data.get('tagline', ''),
                "vote_average": movie_data.get('vote_average', 0),
                "vote_count": movie_data.get('vote_count', 0),
                "production_companies": movie_data.get('production_companies', []),
                "production_countries": movie_data.get('production_countries', []),
                "spoken_languages": movie_data.get('spoken_languages', []),
                "homepage": movie_data.get('homepage', ''),
                "original_title": movie_data.get('original_title', ''),
                "popularity": movie_data.get('popularity', 0)
            }
        }
        
        return formatted_movie
    
    def auto_fetch_tv_series(self, title_or_id: Union[str, int], seasons_sources: Dict[int, Dict[int, List[Dict[str, Any]]]]) -> Optional[Dict[str, Any]]:
        """
        Automatically fetch complete TV series data from TMDB
        
        Args:
            title_or_id: TV series title (string) or TMDB ID (integer)
            seasons_sources: Dictionary of manual sources in format:
                {season_number: {episode_number: [sources_list]}}
        
        Returns:
            Complete TV series data formatted for free_movie_api.json
        """
        
        # Step 1: Get basic TV series info
        if isinstance(title_or_id, str):
            # Search by title
            search_result = self.search_content(title_or_id, "tv")
            if not search_result:
                print(f"❌ TV Series '{title_or_id}' not found in TMDB")
                return None
            tmdb_id = search_result['id']
        else:
            # Use provided TMDB ID
            tmdb_id = title_or_id
        
        # Step 2: Fetch complete TV series details
        tv_url = f"{self.base_url}/tv/{tmdb_id}"
        params = {
            'api_key': self.api_key,
            'append_to_response': 'credits,videos,images'
        }
        
        try:
            response = requests.get(tv_url, params=params)
            response.raise_for_status()
            tv_data = response.json()
        except requests.RequestException as e:
            print(f"❌ Error fetching TV series details: {e}")
            return None
        
        # Step 3: Process main cast
        cast = tv_data.get('credits', {}).get('cast', [])[:6]
        actors = []
        
        for i, actor in enumerate(cast, 1):
            person_details = self.get_person_details(actor['id'])
            
            actors.append({
                "id": i + 10,  # Offset to avoid conflicts
                "name": actor.get('name', ''),
                "type": "actor" if actor.get('gender') == 2 else "actress",
                "role": actor.get('character', ''),
                "image": f"{self.actor_image_base_url}{actor.get('profile_path')}" if actor.get('profile_path') else "",
                "born": person_details.get('birthday', '') if person_details else '',
                "height": "",
                "bio": (person_details.get('biography', '')[:200] + '...') if person_details and person_details.get('biography') else '',
                "tmdb_id": actor.get('id')
            })
        
        # Step 4: Extract genres
        genres = []
        for genre in tv_data.get('genres', []):
            genres.append({
                "id": genre.get('id'),
                "title": genre.get('name')
            })
        
        # Step 5: Find trailer
        videos = tv_data.get('videos', {}).get('results', [])
        trailer_url = ""
        for video in videos:
            if video.get('type') == 'Trailer' and video.get('site') == 'YouTube':
                trailer_url = f"https://www.youtube.com/watch?v={video.get('key')}"
                break
        
        # Step 6: Fetch seasons data
        formatted_seasons = []
        for season_num in seasons_sources.keys():
            season_url = f"{self.base_url}/tv/{tmdb_id}/season/{season_num}"
            season_params = {'api_key': self.api_key}
            
            try:
                season_response = requests.get(season_url, params=season_params)
                season_response.raise_for_status()
                season_data = season_response.json()
                
                # Process episodes
                episodes = []
                for episode_num, episode_sources in seasons_sources[season_num].items():
                    # Find the episode data
                    episode_data = None
                    for ep in season_data.get('episodes', []):
                        if ep.get('episode_number') == episode_num:
                            episode_data = ep
                            break
                    
                    if episode_data:
                        episodes.append({
                            "id": episode_num,
                            "title": episode_data.get('name', ''),
                            "episode_number": episode_data.get('episode_number', episode_num),
                            "season_number": season_num,
                            "tmdb_episode_id": episode_data.get('id'),
                            "air_date": episode_data.get('air_date', ''),
                            "overview": episode_data.get('overview', ''),
                            "image": f"{self.image_base_url}{episode_data.get('still_path')}" if episode_data.get('still_path') else "",
                            "duration": f"{episode_data.get('runtime', 45)}:00",
                            "rating": episode_data.get('vote_average', 0),
                            "sources": episode_sources,  # USER PROVIDED
                            "subtitles": [
                                {
                                    "id": episode_num * 2 - 1,
                                    "title": "English",
                                    "language": "en",
                                    "url": f"https://example.com/subtitles/{tv_data.get('name', '').lower().replace(' ', '-')}-s{season_num}e{episode_num}-en.vtt"
                                },
                                {
                                    "id": episode_num * 2,
                                    "title": "Spanish",
                                    "language": "es",
                                    "url": f"https://example.com/subtitles/{tv_data.get('name', '').lower().replace(' ', '-')}-s{season_num}e{episode_num}-es.vtt"
                                }
                            ],
                            "views": 0,
                            "downloads": 0
                        })
                
                formatted_seasons.append({
                    "id": season_num,
                    "title": f"Season {season_num}",
                    "season_number": season_num,
                    "tmdb_season_id": season_data.get('id'),
                    "air_date": season_data.get('air_date', ''),
                    "overview": season_data.get('overview', ''),
                    "poster_path": f"{self.image_base_url}{season_data.get('poster_path')}" if season_data.get('poster_path') else "",
                    "episodes": episodes
                })
                
            except requests.RequestException as e:
                print(f"❌ Error fetching season {season_num}: {e}")
                continue
        
        # Step 7: Format complete TV series data
        formatted_series = {
            "id": 2,  # This should be set externally
            "title": tv_data.get('name', ''),
            "type": "series",
            "label": genres[0].get('title', '') if genres else '',
            "sublabel": f"{len(formatted_seasons)} Seasons",
            "imdb": str(round(tv_data.get('vote_average', 0), 1)),
            "tmdb_id": tv_data.get('id'),
            "tmdb_rating": tv_data.get('vote_average', 0),
            "downloadas": tv_data.get('name', '').lower().replace(' ', '-'),
            "comment": True,
            "playas": "video",
            "description": tv_data.get('overview', ''),
            "classification": "TV-14",  # Default for TV series
            "year": tv_data.get('first_air_date', '')[:4],
            "duration": f"{tv_data.get('episode_run_time', [45])[0] if tv_data.get('episode_run_time') else 45}:00",
            "rating": tv_data.get('vote_average', 0),
            "views": 0,
            "created_at": "2024-01-02",
            "image": f"{self.image_base_url}{tv_data.get('poster_path')}" if tv_data.get('poster_path') else "",
            "cover": f"{self.backdrop_base_url}{tv_data.get('backdrop_path')}" if tv_data.get('backdrop_path') else "",
            "backdrop_path": f"{self.backdrop_base_url}{tv_data.get('backdrop_path')}" if tv_data.get('backdrop_path') else "",
            "genres": genres,
            "sources": [],  # TV series don't have direct sources
            "trailer": {
                "id": 5,
                "type": "video",
                "title": f"{tv_data.get('name', '')} Official Trailer",
                "url": trailer_url
            } if trailer_url else None,
            "actors": actors,
            "subtitles": [],
            "comments": [],
            "downloads": 0,
            "shares": 0,
            "seasons": formatted_seasons,
            "tmdb_metadata": {
                "adult": tv_data.get('adult', False),
                "first_air_date": tv_data.get('first_air_date', ''),
                "last_air_date": tv_data.get('last_air_date', ''),
                "number_of_episodes": tv_data.get('number_of_episodes', 0),
                "number_of_seasons": tv_data.get('number_of_seasons', 0),
                "status": tv_data.get('status', ''),
                "tagline": tv_data.get('tagline', ''),
                "type": tv_data.get('type', ''),
                "vote_average": tv_data.get('vote_average', 0),
                "vote_count": tv_data.get('vote_count', 0),
                "created_by": tv_data.get('created_by', []),
                "networks": tv_data.get('networks', []),
                "production_companies": tv_data.get('production_companies', []),
                "production_countries": tv_data.get('production_countries', []),
                "spoken_languages": tv_data.get('spoken_languages', []),
                "homepage": tv_data.get('homepage', ''),
                "original_name": tv_data.get('original_name', ''),
                "popularity": tv_data.get('popularity', 0)
            }
        }
        
        return formatted_series

def demo_auto_fetch():
    """Demonstrate the auto-fetch functionality"""
    api_key = "ec926176bf467b3f7735e3154238c161"
    tmdb = TMDBAutoFetch(api_key)
    
    print("🚀 TMDB Auto-Fetch Demo")
    print("=" * 50)
    
    # Example 1: Auto-fetch movie by title
    print("\n📽️  Auto-fetching movie: 'Inception'")
    
    # Manual sources (only thing you need to provide)
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
            "url": "https://vidsrc.net/embed/movie/27205"  # You provide this
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
            "url": "https://example.com/inception-720p.mp4"  # You provide this
        }
    ]
    
    # Everything else is automatically fetched
    movie_data = tmdb.auto_fetch_movie("Inception", movie_sources)
    if movie_data:
        print(f"✅ Auto-fetched: {movie_data['title']} ({movie_data['year']})")
        print(f"   TMDB ID: {movie_data['tmdb_id']}")
        print(f"   Poster: {movie_data['image']}")
        print(f"   Cast: {', '.join([actor['name'] for actor in movie_data['actors'][:3]])}")
        print(f"   Sources: {len(movie_data['sources'])} (manually provided)")
    
    # Example 2: Auto-fetch TV series by title
    print("\n📺 Auto-fetching TV series: 'Breaking Bad'")
    
    # Manual sources for specific episodes (only thing you need to provide)
    tv_sources = {
        1: {  # Season 1
            1: [  # Episode 1
                {
                    "id": 1,
                    "type": "embed",
                    "title": "VidSrc 1080p",
                    "quality": "1080p",
                    "url": "https://vidsrc.net/embed/tv/1396/1/1"  # You provide this
                }
            ]
        },
        2: {  # Season 2
            1: [  # Episode 1
                {
                    "id": 2,
                    "type": "embed",
                    "title": "VidSrc 1080p",
                    "quality": "1080p",
                    "url": "https://vidsrc.net/embed/tv/1396/2/1"  # You provide this
                }
            ]
        }
    }
    
    # Everything else is automatically fetched
    tv_data = tmdb.auto_fetch_tv_series("Breaking Bad", tv_sources)
    if tv_data:
        print(f"✅ Auto-fetched: {tv_data['title']} ({tv_data['year']})")
        print(f"   TMDB ID: {tv_data['tmdb_id']}")
        print(f"   Poster: {tv_data['image']}")
        print(f"   Seasons: {len(tv_data['seasons'])}")
        print(f"   Cast: {', '.join([actor['name'] for actor in tv_data['actors'][:3]])}")
    
    print("\n🎯 What You Need to Provide:")
    print("   • Title or TMDB ID")
    print("   • Sources (streaming links)")
    print("\n🤖 What's Automatically Fetched:")
    print("   • All images (posters, backdrops, actor photos)")
    print("   • Complete cast with biographies")
    print("   • Ratings, descriptions, genres")
    print("   • Release dates, runtime")
    print("   • Production companies")
    print("   • Trailers from YouTube")
    print("   • Season/episode data for TV series")
    print("   • ALL metadata except sources!")

if __name__ == "__main__":
    demo_auto_fetch()