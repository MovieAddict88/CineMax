#!/usr/bin/env python3
"""
TMDB Integration Script for Movie & TV Series Metadata Auto-Detection
This script fetches metadata from TMDB API and integrates it with the existing JSON structure.
Sources are replaced with vidsrc.net embed links for movies and TV series only.
Live TV channels remain untouched.
"""

import json
import requests
import time
from datetime import datetime

class TMDBIntegrator:
    def __init__(self, api_key):
        self.api_key = api_key
        self.base_url = "https://api.themoviedb.org/3"
        self.image_base_url = "https://image.tmdb.org/t/p"
        self.session = requests.Session()
        self.headers = {
            'Authorization': f'Bearer {api_key}',
            'Content-Type': 'application/json'
        }
        
    def search_movie(self, title, year=None):
        """Search for a movie in TMDB"""
        params = {
            'query': title,
            'api_key': self.api_key
        }
        if year:
            params['year'] = year
            
        response = self.session.get(f"{self.base_url}/search/movie", params=params, headers=self.headers)
        if response.status_code == 200:
            results = response.json().get('results', [])
            return results[0] if results else None
        return None
    
    def search_tv(self, title, year=None):
        """Search for a TV series in TMDB"""
        params = {
            'query': title,
            'api_key': self.api_key
        }
        if year:
            params['first_air_date_year'] = year
            
        response = self.session.get(f"{self.base_url}/search/tv", params=params, headers=self.headers)
        if response.status_code == 200:
            results = response.json().get('results', [])
            return results[0] if results else None
        return None
    
    def get_movie_details(self, movie_id):
        """Get detailed movie information"""
        params = {
            'api_key': self.api_key,
            'append_to_response': 'credits,videos,images,release_dates,keywords'
        }
        response = self.session.get(f"{self.base_url}/movie/{movie_id}", params=params, headers=self.headers)
        if response.status_code == 200:
            return response.json()
        return None
    
    def get_tv_details(self, tv_id):
        """Get detailed TV series information"""
        params = {
            'api_key': self.api_key,
            'append_to_response': 'credits,videos,images,content_ratings,keywords'
        }
        response = self.session.get(f"{self.base_url}/tv/{tv_id}", params=params, headers=self.headers)
        if response.status_code == 200:
            return response.json()
        return None
    
    def get_tv_season_details(self, tv_id, season_number):
        """Get TV season details"""
        params = {'api_key': self.api_key}
        response = self.session.get(f"{self.base_url}/tv/{tv_id}/season/{season_number}", params=params, headers=self.headers)
        if response.status_code == 200:
            return response.json()
        return None
    
    def create_vidsrc_sources(self, tmdb_id, media_type, title):
        """Create vidsrc.net embed sources"""
        if media_type == "movie":
            embed_url = f"https://vidsrc.net/embed/movie/{tmdb_id}"
        else:
            embed_url = f"https://vidsrc.net/embed/tv/{tmdb_id}"
            
        return [
            {
                "id": 1,
                "type": "embed",
                "title": f"{title} - VidSrc",
                "quality": "Auto",
                "size": "Stream",
                "kind": "both",
                "premium": "false",
                "external": True,
                "url": embed_url
            }
        ]
    
    def create_vidsrc_episode_sources(self, tmdb_id, season_num, episode_num, title):
        """Create vidsrc.net embed sources for TV episodes"""
        embed_url = f"https://vidsrc.net/embed/tv/{tmdb_id}/{season_num}/{episode_num}"
        return [
            {
                "id": 1,
                "type": "embed",
                "title": f"{title} S{season_num}E{episode_num} - VidSrc",
                "quality": "Auto",
                "size": "Stream",
                "kind": "both",
                "premium": "false",
                "external": True,
                "url": embed_url
            }
        ]
    
    def format_runtime(self, minutes):
        """Convert minutes to HH:MM format"""
        if not minutes:
            return "00:00"
        hours = minutes // 60
        mins = minutes % 60
        return f"{hours:02d}:{mins:02d}"
    
    def get_certification(self, release_dates_data, country="US"):
        """Extract certification/rating from release dates"""
        if not release_dates_data or 'results' not in release_dates_data:
            return "NR"
            
        for result in release_dates_data['results']:
            if result['iso_3166_1'] == country:
                for release_date in result['release_dates']:
                    if release_date.get('certification'):
                        return release_date['certification']
        return "NR"
    
    def enhance_movie_metadata(self, movie_data):
        """Enhance movie with TMDB metadata"""
        # Search for the movie
        search_result = self.search_movie(movie_data['title'], movie_data.get('year'))
        if not search_result:
            print(f"Movie '{movie_data['title']}' not found in TMDB")
            return movie_data
        
        # Get detailed information
        details = self.get_movie_details(search_result['id'])
        if not details:
            return movie_data
        
        print(f"Enhancing movie: {details['title']} ({details.get('release_date', '')[:4]})")
        
        # Extract genres
        genres = [{"id": genre['id'], "title": genre['name']} for genre in details.get('genres', [])]
        
        # Extract cast
        cast = []
        if 'credits' in details and 'cast' in details['credits']:
            for i, actor in enumerate(details['credits']['cast'][:5]):  # Top 5 actors
                cast.append({
                    "id": actor['id'],
                    "name": actor['name'],
                    "type": "actor" if actor['gender'] != 1 else "actress",
                    "role": actor.get('character', 'Unknown'),
                    "image": f"{self.image_base_url}/w300{actor['profile_path']}" if actor.get('profile_path') else "",
                    "born": "",
                    "height": "",
                    "bio": ""
                })
        
        # Extract production countries
        countries = [country['name'] for country in details.get('production_countries', [])]
        
        # Get certification
        certification = self.get_certification(details.get('release_dates'))
        
        # Update movie data with TMDB metadata
        enhanced_data = {
            **movie_data,
            "title": details['title'],
            "label": genres[0]['title'] if genres else movie_data.get('label', ''),
            "sublabel": f"{details.get('release_date', '')[:4]} • {self.format_runtime(details.get('runtime'))}",
            "imdb": str(details.get('vote_average', 0)),
            "description": details.get('overview', movie_data.get('description', '')),
            "classification": certification,
            "year": details.get('release_date', '')[:4],
            "duration": self.format_runtime(details.get('runtime')),
            "rating": details.get('vote_average', 0),
            "cover": f"{self.image_base_url}/w1280{details['backdrop_path']}" if details.get('backdrop_path') else movie_data.get('cover', ''),
            "image": f"{self.image_base_url}/w500{details['poster_path']}" if details.get('poster_path') else movie_data.get('image', ''),
            "genres": genres,
            "actors": cast,
            "countries": countries,
            "views": details.get('popularity', movie_data.get('views', 0)),
            "tmdb_id": details['id'],
            "sources": self.create_vidsrc_sources(details['id'], "movie", details['title'])
        }
        
        return enhanced_data
    
    def enhance_tv_metadata(self, tv_data):
        """Enhance TV series with TMDB metadata"""
        # Search for the TV series
        search_result = self.search_tv(tv_data['title'], tv_data.get('year'))
        if not search_result:
            print(f"TV Series '{tv_data['title']}' not found in TMDB")
            return tv_data
        
        # Get detailed information
        details = self.get_tv_details(search_result['id'])
        if not details:
            return tv_data
        
        print(f"Enhancing TV series: {details['name']} ({details.get('first_air_date', '')[:4]})")
        
        # Extract genres
        genres = [{"id": genre['id'], "title": genre['name']} for genre in details.get('genres', [])]
        
        # Extract cast
        cast = []
        if 'credits' in details and 'cast' in details['credits']:
            for i, actor in enumerate(details['credits']['cast'][:5]):  # Top 5 actors
                cast.append({
                    "id": actor['id'],
                    "name": actor['name'],
                    "type": "actor" if actor['gender'] != 1 else "actress",
                    "role": actor.get('character', 'Unknown'),
                    "image": f"{self.image_base_url}/w300{actor['profile_path']}" if actor.get('profile_path') else "",
                    "born": "",
                    "height": "",
                    "bio": ""
                })
        
        # Extract production countries
        countries = [country['name'] for country in details.get('origin_country', [])]
        
        # Get content rating
        content_rating = "TV-14"  # Default
        if 'content_ratings' in details and 'results' in details['content_ratings']:
            for rating in details['content_ratings']['results']:
                if rating['iso_3166_1'] == 'US':
                    content_rating = rating.get('rating', 'TV-14')
                    break
        
        # Enhance seasons with TMDB data
        enhanced_seasons = []
        for i, season_data in enumerate(tv_data.get('seasons', [])):
            season_num = i + 1
            season_details = self.get_tv_season_details(details['id'], season_num)
            
            enhanced_episodes = []
            for j, episode_data in enumerate(season_data.get('episodes', [])):
                episode_num = j + 1
                episode_title = f"Episode {episode_num}"
                episode_overview = ""
                episode_image = ""
                
                if season_details and 'episodes' in season_details:
                    if j < len(season_details['episodes']):
                        ep_detail = season_details['episodes'][j]
                        episode_title = ep_detail.get('name', episode_title)
                        episode_overview = ep_detail.get('overview', '')
                        if ep_detail.get('still_path'):
                            episode_image = f"{self.image_base_url}/w500{ep_detail['still_path']}"
                
                enhanced_episode = {
                    **episode_data,
                    "title": episode_title,
                    "description": episode_overview,
                    "image": episode_image or episode_data.get('image', ''),
                    "sources": self.create_vidsrc_episode_sources(
                        details['id'], season_num, episode_num, details['name']
                    )
                }
                enhanced_episodes.append(enhanced_episode)
            
            enhanced_season = {
                **season_data,
                "episodes": enhanced_episodes
            }
            enhanced_seasons.append(enhanced_season)
        
        # Update TV data with TMDB metadata
        enhanced_data = {
            **tv_data,
            "title": details['name'],
            "label": genres[0]['title'] if genres else tv_data.get('label', ''),
            "sublabel": f"{len(enhanced_seasons)} Season{'s' if len(enhanced_seasons) > 1 else ''}",
            "imdb": str(details.get('vote_average', 0)),
            "description": details.get('overview', tv_data.get('description', '')),
            "classification": content_rating,
            "year": details.get('first_air_date', '')[:4],
            "duration": f"{details.get('episode_run_time', [45])[0] if details.get('episode_run_time') else 45}:00",
            "rating": details.get('vote_average', 0),
            "cover": f"{self.image_base_url}/w1280{details['backdrop_path']}" if details.get('backdrop_path') else tv_data.get('cover', ''),
            "image": f"{self.image_base_url}/w500{details['poster_path']}" if details.get('poster_path') else tv_data.get('image', ''),
            "genres": genres,
            "actors": cast,
            "countries": countries,
            "views": details.get('popularity', tv_data.get('views', 0)),
            "tmdb_id": details['id'],
            "sources": self.create_vidsrc_sources(details['id'], "tv", details['name']),
            "seasons": enhanced_seasons
        }
        
        return enhanced_data
    
    def process_json_file(self, input_file, output_file):
        """Process the JSON file and enhance movies/TV series with TMDB data"""
        print("Loading JSON file...")
        with open(input_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        print("Processing movies and TV series...")
        
        # Process movies in the main movies array
        if 'movies' in data:
            enhanced_movies = []
            for movie in data['movies']:
                if movie.get('type') == 'movie':
                    enhanced_movie = self.enhance_movie_metadata(movie)
                    enhanced_movies.append(enhanced_movie)
                elif movie.get('type') == 'series':
                    enhanced_series = self.enhance_tv_metadata(movie)
                    enhanced_movies.append(enhanced_series)
                else:
                    enhanced_movies.append(movie)  # Keep as is for other types
                time.sleep(0.5)  # Rate limiting
            
            data['movies'] = enhanced_movies
        
        # Process featured movies in home section
        if 'home' in data and 'featured_movies' in data['home']:
            enhanced_featured = []
            for movie in data['home']['featured_movies']:
                if movie.get('type') == 'movie':
                    enhanced_movie = self.enhance_movie_metadata(movie)
                    enhanced_featured.append(enhanced_movie)
                elif movie.get('type') == 'series':
                    enhanced_series = self.enhance_tv_metadata(movie)
                    enhanced_featured.append(enhanced_series)
                else:
                    enhanced_featured.append(movie)
                time.sleep(0.5)  # Rate limiting
            
            data['home']['featured_movies'] = enhanced_featured
        
        # Process slides in home section
        if 'home' in data and 'slides' in data['home']:
            enhanced_slides = []
            for slide in data['home']['slides']:
                if slide.get('type') == 'movie' and 'poster' in slide:
                    enhanced_poster = self.enhance_movie_metadata(slide['poster'])
                    slide['poster'] = enhanced_poster
                elif slide.get('type') == 'series' and 'poster' in slide:
                    enhanced_poster = self.enhance_tv_metadata(slide['poster'])
                    slide['poster'] = enhanced_poster
                # Keep channels untouched
                enhanced_slides.append(slide)
                time.sleep(0.5)  # Rate limiting
            
            data['home']['slides'] = enhanced_slides
        
        # Update metadata
        data['api_info']['last_updated'] = datetime.now().strftime('%Y-%m-%d')
        data['api_info']['description'] = "Enhanced Free Movie & TV Streaming JSON API with TMDB Auto-Detection"
        
        print(f"Saving enhanced JSON to {output_file}...")
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        
        print("✅ TMDB integration completed successfully!")
        print(f"📁 Enhanced JSON saved as: {output_file}")

def main():
    # TMDB API Key
    API_KEY = "ec926176bf467b3f7735e3154238c161"
    
    # Initialize TMDB integrator
    integrator = TMDBIntegrator(API_KEY)
    
    # Process the JSON file
    input_file = "current_api.json"
    output_file = "enhanced_movie_api.json"
    
    try:
        integrator.process_json_file(input_file, output_file)
    except Exception as e:
        print(f"❌ Error: {str(e)}")

if __name__ == "__main__":
    main()