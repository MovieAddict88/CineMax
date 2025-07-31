#!/usr/bin/env python3
"""
Fix JSON with Real Movie and TV Series
Replace Big Buck Bunny with a real movie (e.g., The Dark Knight)
Replace Sample TV Series with a real series (e.g., Breaking Bad)
Both with full TMDB metadata and vidsrc.net sources
"""

import json
import requests
from datetime import datetime

class RealContentReplacer:
    def __init__(self, api_key):
        self.api_key = api_key
        self.base_url = "https://api.themoviedb.org/3"
        self.image_base_url = "https://image.tmdb.org/t/p"
    
    def get_movie_details(self, movie_id):
        """Get movie details from TMDB"""
        url = f"{self.base_url}/movie/{movie_id}"
        params = {
            'api_key': self.api_key,
            'append_to_response': 'credits,videos,images,release_dates'
        }
        response = requests.get(url, params=params)
        if response.status_code == 200:
            return response.json()
        return None
    
    def get_tv_details(self, tv_id):
        """Get TV series details from TMDB"""
        url = f"{self.base_url}/tv/{tv_id}"
        params = {
            'api_key': self.api_key,
            'append_to_response': 'credits,videos,images,content_ratings'
        }
        response = requests.get(url, params=params)
        if response.status_code == 200:
            return response.json()
        return None
    
    def get_season_details(self, tv_id, season_num):
        """Get season details from TMDB"""
        url = f"{self.base_url}/tv/{tv_id}/season/{season_num}"
        params = {'api_key': self.api_key}
        response = requests.get(url, params=params)
        if response.status_code == 200:
            return response.json()
        return None
    
    def create_real_movie_entry(self, movie_id, entry_id=1):
        """Create a real movie entry with TMDB data"""
        movie_data = self.get_movie_details(movie_id)
        if not movie_data:
            return None
        
        print(f"Creating movie: {movie_data['title']} ({movie_data['release_date'][:4]})")
        
        # Get certification
        certification = "PG-13"
        if 'release_dates' in movie_data and 'results' in movie_data['release_dates']:
            for result in movie_data['release_dates']['results']:
                if result['iso_3166_1'] == 'US':
                    for release_date in result['release_dates']:
                        if release_date.get('certification'):
                            certification = release_date['certification']
                            break
                    break
        
        # Extract cast
        cast = []
        if 'credits' in movie_data and 'cast' in movie_data['credits']:
            for actor in movie_data['credits']['cast'][:5]:
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
        
        return {
            "id": entry_id,
            "title": movie_data['title'],
            "type": "movie",
            "label": movie_data['genres'][0]['name'] if movie_data['genres'] else 'Action',
            "sublabel": f"{movie_data['release_date'][:4]} • {self.format_runtime(movie_data.get('runtime'))}",
            "imdb": str(movie_data.get('vote_average', 0)),
            "downloadas": movie_data['title'].lower().replace(' ', '-'),
            "comment": True,
            "playas": "video",
            "description": movie_data.get('overview', ''),
            "classification": certification,
            "year": movie_data['release_date'][:4],
            "duration": self.format_runtime(movie_data.get('runtime')),
            "rating": movie_data.get('vote_average', 0),
            "image": f"{self.image_base_url}/w500{movie_data['poster_path']}" if movie_data.get('poster_path') else "",
            "cover": f"{self.image_base_url}/w1280{movie_data['backdrop_path']}" if movie_data.get('backdrop_path') else "",
            "genres": [{"id": genre['id'], "title": genre['name']} for genre in movie_data.get('genres', [])],
            "sources": [{
                "id": 1,
                "type": "embed",
                "title": f"{movie_data['title']} - VidSrc",
                "quality": "Auto",
                "size": "Stream",
                "kind": "both",
                "premium": "false",
                "external": True,
                "url": f"https://vidsrc.net/embed/movie/{movie_data['id']}"
            }],
            "trailer": {
                "id": 1,
                "type": "video",
                "title": f"{movie_data['title']} Trailer",
                "url": f"https://vidsrc.net/embed/movie/{movie_data['id']}"
            },
            "actors": cast,
            "subtitles": [
                {
                    "id": 1,
                    "title": "English",
                    "language": "en",
                    "url": f"https://example.com/subtitles/{movie_data['title'].lower().replace(' ', '-')}-en.vtt"
                }
            ],
            "comments": [
                {
                    "id": 1,
                    "user": "Movie Fan",
                    "comment": f"Great movie! {movie_data['title']} is amazing.",
                    "created_at": "2024-01-15T10:30:00Z"
                }
            ],
            "views": int(movie_data.get('popularity', 100)),
            "downloads": 5000,
            "shares": 1200,
            "countries": [country['name'] for country in movie_data.get('production_countries', [])],
            "tmdb_id": movie_data['id']
        }
    
    def create_real_tv_entry(self, tv_id, entry_id=2):
        """Create a real TV series entry with TMDB data"""
        tv_data = self.get_tv_details(tv_id)
        if not tv_data:
            return None
        
        print(f"Creating TV series: {tv_data['name']} ({tv_data['first_air_date'][:4]})")
        
        # Get content rating
        content_rating = "TV-14"
        if 'content_ratings' in tv_data and 'results' in tv_data['content_ratings']:
            for rating in tv_data['content_ratings']['results']:
                if rating['iso_3166_1'] == 'US':
                    content_rating = rating.get('rating', 'TV-14')
                    break
        
        # Extract cast
        cast = []
        if 'credits' in tv_data and 'cast' in tv_data['credits']:
            for actor in tv_data['credits']['cast'][:5]:
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
        
        # Create 2 seasons with 1 episode each as requested
        seasons = []
        for season_num in [1, 2]:
            season_data = self.get_season_details(tv_data['id'], season_num)
            
            if season_data and season_data.get('episodes'):
                episode = season_data['episodes'][0]  # First episode
                
                season = {
                    "id": season_num,
                    "title": f"Season {season_num}",
                    "episodes": [{
                        "id": season_num,
                        "title": episode.get('name', f"Episode 1"),
                        "episode_number": 1,
                        "description": episode.get('overview', ''),
                        "image": f"{self.image_base_url}/w500{episode['still_path']}" if episode.get('still_path') else "",
                        "duration": f"{tv_data['episode_run_time'][0] if tv_data['episode_run_time'] else 45}:00",
                        "sources": [{
                            "id": 1,
                            "type": "embed",
                            "title": f"{tv_data['name']} S{season_num}E1 - VidSrc",
                            "quality": "Auto",
                            "size": "Stream",
                            "kind": "both",
                            "premium": "false",
                            "external": True,
                            "url": f"https://vidsrc.net/embed/tv/{tv_data['id']}/{season_num}/1"
                        }],
                        "subtitles": [{
                            "id": 1,
                            "title": "English",
                            "language": "en",
                            "url": f"https://example.com/subtitles/{tv_data['name'].lower().replace(' ', '-')}-s{season_num}e1-en.vtt"
                        }],
                        "views": 25000 - (season_num * 5000),
                        "downloads": 8000 - (season_num * 2000)
                    }]
                }
                seasons.append(season)
        
        return {
            "id": entry_id,
            "title": tv_data['name'],
            "type": "series",
            "label": tv_data['genres'][0]['name'] if tv_data['genres'] else 'Drama',
            "sublabel": "2 Seasons",
            "imdb": str(tv_data.get('vote_average', 0)),
            "downloadas": tv_data['name'].lower().replace(' ', '-'),
            "comment": True,
            "playas": "video",
            "description": tv_data.get('overview', ''),
            "classification": content_rating,
            "year": tv_data['first_air_date'][:4],
            "duration": f"{tv_data['episode_run_time'][0] if tv_data['episode_run_time'] else 45}:00",
            "rating": tv_data.get('vote_average', 0),
            "views": int(tv_data.get('popularity', 100)),
            "created_at": "2024-01-02",
            "image": f"{self.image_base_url}/w500{tv_data['poster_path']}" if tv_data.get('poster_path') else "",
            "cover": f"{self.image_base_url}/w1280{tv_data['backdrop_path']}" if tv_data.get('backdrop_path') else "",
            "genres": [{"id": genre['id'], "title": genre['name']} for genre in tv_data.get('genres', [])],
            "sources": [{
                "id": 1,
                "type": "embed",
                "title": f"{tv_data['name']} - VidSrc",
                "quality": "Auto",
                "size": "Stream",
                "kind": "both",
                "premium": "false",
                "external": True,
                "url": f"https://vidsrc.net/embed/tv/{tv_data['id']}"
            }],
            "trailer": None,
            "actors": cast,
            "subtitles": [],
            "comments": [{
                "id": 1,
                "user": "TV Fan",
                "comment": f"Amazing series! {tv_data['name']} is fantastic.",
                "created_at": "2024-01-15T14:30:00Z"
            }],
            "downloads": 15000,
            "shares": 8500,
            "countries": tv_data.get('origin_country', []),
            "tmdb_id": tv_data['id'],
            "seasons": seasons
        }
    
    def format_runtime(self, minutes):
        """Convert minutes to HH:MM format"""
        if not minutes:
            return "00:00"
        hours = minutes // 60
        mins = minutes % 60
        return f"{hours:02d}:{mins:02d}"
    
    def replace_content(self):
        """Replace Big Buck Bunny and Sample TV Series with real content"""
        # Load original JSON
        with open('current_api.json', 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        # The Dark Knight (TMDB ID: 155) - Popular movie
        # Breaking Bad (TMDB ID: 1396) - Popular TV series
        
        print("🎬 Replacing Big Buck Bunny with The Dark Knight...")
        real_movie = self.create_real_movie_entry(155, 1)
        
        print("📺 Replacing Sample TV Series with Breaking Bad...")
        real_tv = self.create_real_tv_entry(1396, 2)
        
        if not real_movie or not real_tv:
            print("❌ Failed to fetch data from TMDB")
            return
        
        # Replace in movies array
        data['movies'] = [real_movie, real_tv]
        
        # Replace in home featured_movies
        data['home']['featured_movies'] = [real_movie]
        
        # Replace in home slides (keep the movie slide, keep channels untouched)
        new_slides = []
        for slide in data['home']['slides']:
            if slide.get('type') == 'movie':
                slide['title'] = real_movie['title']
                slide['image'] = real_movie['image']
                slide['poster'] = real_movie
                new_slides.append(slide)
            elif slide.get('type') == 'channel':
                # Keep all channels untouched as requested
                new_slides.append(slide)
        
        data['home']['slides'] = new_slides
        
        # Update API info
        data['api_info']['total_movies'] = 2
        data['api_info']['last_updated'] = datetime.now().strftime('%Y-%m-%d')
        data['api_info']['description'] = "Enhanced Free Movie & TV Streaming JSON API with TMDB Auto-Detection - Real Content"
        
        # Save the final result
        output_file = 'final_real_content_api.json'
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        
        print("✅ Successfully replaced with real content!")
        print(f"📁 Final JSON saved as: {output_file}")
        print(f"🎬 Movie: {real_movie['title']} - vidsrc.net/embed/movie/{real_movie['tmdb_id']}")
        print(f"📺 TV Series: {real_tv['title']} - vidsrc.net/embed/tv/{real_tv['tmdb_id']}")
        print("🔴 Live TV channels remain untouched as requested")

def main():
    api_key = "ec926176bf467b3f7735e3154238c161"
    replacer = RealContentReplacer(api_key)
    replacer.replace_content()

if __name__ == "__main__":
    main()