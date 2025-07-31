#!/usr/bin/env python3
"""
TMDB API Integration Script
Automatically detects and fills missing metadata for movies and TV series
"""

import json
import requests
import time
from typing import Dict, List, Optional, Any

class TMDBIntegrator:
    def __init__(self, api_key: str):
        self.api_key = api_key
        self.base_url = "https://api.themoviedb.org/3"
        self.image_base_url = "https://image.tmdb.org/t/p/"
        self.session = requests.Session()
        self.session.headers.update({
            'Authorization': f'Bearer {api_key}',
            'Content-Type': 'application/json'
        })
    
    def search_movie(self, title: str, year: Optional[str] = None) -> Optional[Dict]:
        """Search for a movie by title and optional year"""
        params = {'query': title}
        if year:
            params['year'] = year
            
        try:
            response = self.session.get(f"{self.base_url}/search/movie", params=params)
            response.raise_for_status()
            data = response.json()
            
            if data['results']:
                return data['results'][0]  # Return first match
            return None
        except requests.RequestException as e:
            print(f"Error searching movie '{title}': {e}")
            return None
    
    def search_tv(self, title: str, year: Optional[str] = None) -> Optional[Dict]:
        """Search for a TV series by title and optional year"""
        params = {'query': title}
        if year:
            params['first_air_date_year'] = year
            
        try:
            response = self.session.get(f"{self.base_url}/search/tv", params=params)
            response.raise_for_status()
            data = response.json()
            
            if data['results']:
                return data['results'][0]  # Return first match
            return None
        except requests.RequestException as e:
            print(f"Error searching TV series '{title}': {e}")
            return None
    
    def get_movie_details(self, movie_id: int) -> Optional[Dict]:
        """Get detailed movie information"""
        try:
            response = self.session.get(
                f"{self.base_url}/movie/{movie_id}",
                params={'append_to_response': 'credits,images,videos,release_dates'}
            )
            response.raise_for_status()
            return response.json()
        except requests.RequestException as e:
            print(f"Error getting movie details for ID {movie_id}: {e}")
            return None
    
    def get_tv_details(self, tv_id: int) -> Optional[Dict]:
        """Get detailed TV series information"""
        try:
            response = self.session.get(
                f"{self.base_url}/tv/{tv_id}",
                params={'append_to_response': 'credits,images,videos,content_ratings'}
            )
            response.raise_for_status()
            return response.json()
        except requests.RequestException as e:
            print(f"Error getting TV details for ID {tv_id}: {e}")
            return None
    
    def get_tv_season_details(self, tv_id: int, season_number: int) -> Optional[Dict]:
        """Get detailed TV season information"""
        try:
            response = self.session.get(
                f"{self.base_url}/tv/{tv_id}/season/{season_number}",
                params={'append_to_response': 'credits,images,videos'}
            )
            response.raise_for_status()
            return response.json()
        except requests.RequestException as e:
            print(f"Error getting season {season_number} details for TV ID {tv_id}: {e}")
            return None
    
    def enhance_movie_metadata(self, movie_data: Dict) -> Dict:
        """Enhance movie metadata using TMDB API"""
        title = movie_data.get('title', '')
        year = movie_data.get('year', '')
        
        print(f"Enhancing movie: {title} ({year})")
        
        # Search for movie
        search_result = self.search_movie(title, str(year) if year else None)
        if not search_result:
            print(f"No TMDB match found for movie: {title}")
            return movie_data
        
        # Get detailed information
        movie_details = self.get_movie_details(search_result['id'])
        if not movie_details:
            return movie_data
        
        # Update metadata
        enhanced_data = movie_data.copy()
        
        # Basic info
        if movie_details.get('overview'):
            enhanced_data['description'] = movie_details['overview']
        
        if movie_details.get('vote_average'):
            enhanced_data['rating'] = round(movie_details['vote_average'], 1)
            enhanced_data['imdb'] = str(round(movie_details['vote_average'], 1))
        
        if movie_details.get('runtime'):
            hours = movie_details['runtime'] // 60
            minutes = movie_details['runtime'] % 60
            enhanced_data['duration'] = f"{hours}:{minutes:02d}:00" if hours > 0 else f"{minutes}:00"
        
        if movie_details.get('release_date'):
            enhanced_data['year'] = movie_details['release_date'][:4]
        
        # Images
        if movie_details.get('poster_path'):
            enhanced_data['image'] = f"{self.image_base_url}w500{movie_details['poster_path']}"
            enhanced_data['cover'] = f"{self.image_base_url}w1280{movie_details['poster_path']}"
        
        if movie_details.get('backdrop_path'):
            enhanced_data['backdrop'] = f"{self.image_base_url}w1280{movie_details['backdrop_path']}"
        
        # Genres
        if movie_details.get('genres'):
            enhanced_data['genres'] = [
                {'id': genre['id'], 'title': genre['name']} 
                for genre in movie_details['genres']
            ]
            if movie_details['genres']:
                enhanced_data['label'] = movie_details['genres'][0]['name']
        
        # Production info
        if movie_details.get('production_companies'):
            enhanced_data['production_companies'] = [
                {'id': company['id'], 'name': company['name']}
                for company in movie_details['production_companies']
            ]
        
        if movie_details.get('production_countries'):
            enhanced_data['countries'] = [
                {'id': idx + 1, 'title': country['iso_3166_1']}
                for idx, country in enumerate(movie_details['production_countries'])
            ]
        
        if movie_details.get('spoken_languages'):
            enhanced_data['spoken_languages'] = [
                {'id': lang['iso_639_1'], 'name': lang['english_name']}
                for lang in movie_details['spoken_languages']
            ]
        
        # Cast and crew
        if movie_details.get('credits', {}).get('cast'):
            enhanced_data['actors'] = []
            for idx, actor in enumerate(movie_details['credits']['cast'][:10]):  # Top 10 actors
                actor_data = {
                    'id': actor['id'],
                    'name': actor['name'],
                    'type': 'actor' if actor['gender'] != 1 else 'actress',
                    'role': actor.get('character', 'Unknown'),
                    'image': f"{self.image_base_url}w185{actor['profile_path']}" if actor.get('profile_path') else "https://via.placeholder.com/185x278?text=No+Image",
                    'born': '',  # Would need additional API call
                    'height': '',  # Would need additional API call
                    'bio': ''  # Would need additional API call
                }
                enhanced_data['actors'].append(actor_data)
        
        # Classification/Rating
        if movie_details.get('release_dates', {}).get('results'):
            for country_data in movie_details['release_dates']['results']:
                if country_data['iso_3166_1'] == 'US':
                    for release in country_data['release_dates']:
                        if release.get('certification'):
                            enhanced_data['classification'] = release['certification']
                            break
                    break
        
        # Add vidsrc.net embed source as requested
        vidsrc_sources = [
            {
                "id": 1001,
                "type": "embed",
                "title": f"{title} - VidSrc 1080p",
                "quality": "1080p",
                "size": "Embed",
                "kind": "both",
                "premium": "false",
                "external": True,
                "url": f"https://vidsrc.net/embed/movie/{search_result['id']}"
            },
            {
                "id": 1002,
                "type": "embed", 
                "title": f"{title} - VidSrc 720p",
                "quality": "720p",
                "size": "Embed",
                "kind": "both",
                "premium": "false",
                "external": True,
                "url": f"https://vidsrc.net/embed/movie/{search_result['id']}"
            }
        ]
        
        # Keep existing sources and add new ones
        if 'sources' in enhanced_data:
            enhanced_data['sources'].extend(vidsrc_sources)
        else:
            enhanced_data['sources'] = vidsrc_sources
        
        return enhanced_data
    
    def enhance_tv_metadata(self, tv_data: Dict) -> Dict:
        """Enhance TV series metadata using TMDB API"""
        title = tv_data.get('title', '')
        year = tv_data.get('year', '')
        
        print(f"Enhancing TV series: {title} ({year})")
        
        # Search for TV series
        search_result = self.search_tv(title, str(year) if year else None)
        if not search_result:
            print(f"No TMDB match found for TV series: {title}")
            return tv_data
        
        # Get detailed information
        tv_details = self.get_tv_details(search_result['id'])
        if not tv_details:
            return tv_data
        
        # Update metadata
        enhanced_data = tv_data.copy()
        
        # Basic info
        if tv_details.get('overview'):
            enhanced_data['description'] = tv_details['overview']
        
        if tv_details.get('vote_average'):
            enhanced_data['rating'] = round(tv_details['vote_average'], 1)
            enhanced_data['imdb'] = str(round(tv_details['vote_average'], 1))
        
        if tv_details.get('first_air_date'):
            enhanced_data['year'] = tv_details['first_air_date'][:4]
        
        if tv_details.get('episode_run_time') and tv_details['episode_run_time']:
            runtime = tv_details['episode_run_time'][0]
            hours = runtime // 60
            minutes = runtime % 60
            enhanced_data['duration'] = f"{hours}:{minutes:02d}:00" if hours > 0 else f"{minutes}:00"
        
        # Images
        if tv_details.get('poster_path'):
            enhanced_data['image'] = f"{self.image_base_url}w500{tv_details['poster_path']}"
            enhanced_data['cover'] = f"{self.image_base_url}w1280{tv_details['poster_path']}"
        
        if tv_details.get('backdrop_path'):
            enhanced_data['backdrop'] = f"{self.image_base_url}w1280{tv_details['backdrop_path']}"
        
        # Genres
        if tv_details.get('genres'):
            enhanced_data['genres'] = [
                {'id': genre['id'], 'title': genre['name']} 
                for genre in tv_details['genres']
            ]
            if tv_details['genres']:
                enhanced_data['label'] = tv_details['genres'][0]['name']
        
        # Production info
        if tv_details.get('production_companies'):
            enhanced_data['production_companies'] = [
                {'id': company['id'], 'name': company['name']}
                for company in tv_details['production_companies']
            ]
        
        if tv_details.get('networks'):
            enhanced_data['networks'] = [
                {'id': network['id'], 'name': network['name']}
                for network in tv_details['networks']
            ]
        
        if tv_details.get('origin_country'):
            enhanced_data['countries'] = [
                {'id': idx + 1, 'title': country}
                for idx, country in enumerate(tv_details['origin_country'])
            ]
        
        if tv_details.get('spoken_languages'):
            enhanced_data['spoken_languages'] = [
                {'id': lang['iso_639_1'], 'name': lang['english_name']}
                for lang in tv_details['spoken_languages']
            ]
        
        # Cast and crew
        if tv_details.get('credits', {}).get('cast'):
            enhanced_data['actors'] = []
            for idx, actor in enumerate(tv_details['credits']['cast'][:10]):  # Top 10 actors
                actor_data = {
                    'id': actor['id'],
                    'name': actor['name'],
                    'type': 'actor' if actor['gender'] != 1 else 'actress',
                    'role': actor.get('character', 'Unknown'),
                    'image': f"{self.image_base_url}w185{actor['profile_path']}" if actor.get('profile_path') else "https://via.placeholder.com/185x278?text=No+Image",
                    'born': '',  # Would need additional API call
                    'height': '',  # Would need additional API call
                    'bio': ''  # Would need additional API call
                }
                enhanced_data['actors'].append(actor_data)
        
        # Classification/Rating
        if tv_details.get('content_ratings', {}).get('results'):
            for rating_data in tv_details['content_ratings']['results']:
                if rating_data['iso_3166_1'] == 'US':
                    enhanced_data['classification'] = rating_data['rating']
                    break
        
        # Enhance seasons and episodes
        if 'seasons' in enhanced_data and tv_details.get('seasons'):
            for season_data in enhanced_data['seasons']:
                season_num = season_data['id']
                if season_num <= len(tv_details['seasons']):
                    tmdb_season = tv_details['seasons'][season_num - 1]
                    
                    # Update season info
                    if tmdb_season.get('overview'):
                        season_data['description'] = tmdb_season['overview']
                    
                    if tmdb_season.get('poster_path'):
                        season_data['image'] = f"{self.image_base_url}w500{tmdb_season['poster_path']}"
                    
                    # Enhance episodes with vidsrc.net embed
                    if 'episodes' in season_data:
                        for episode in season_data['episodes']:
                            episode_sources = [
                                {
                                    "id": 2001 + episode['id'] * 10,
                                    "type": "embed",
                                    "title": f"Episode {episode['episode_number']} - VidSrc 1080p",
                                    "quality": "1080p",
                                    "url": f"https://vidsrc.net/embed/tv/{search_result['id']}/{season_num}/{episode['episode_number']}"
                                },
                                {
                                    "id": 2002 + episode['id'] * 10,
                                    "type": "embed",
                                    "title": f"Episode {episode['episode_number']} - VidSrc 720p", 
                                    "quality": "720p",
                                    "url": f"https://vidsrc.net/embed/tv/{search_result['id']}/{season_num}/{episode['episode_number']}"
                                }
                            ]
                            
                            if 'sources' in episode:
                                episode['sources'].extend(episode_sources)
                            else:
                                episode['sources'] = episode_sources
        
        return enhanced_data
    
    def process_json_file(self, input_file: str, output_file: str):
        """Process the entire JSON file and enhance metadata"""
        try:
            with open(input_file, 'r', encoding='utf-8') as f:
                data = json.load(f)
            
            print("Starting TMDB metadata enhancement...")
            
            # Process movies
            if 'movies' in data:
                for i, movie in enumerate(data['movies']):
                    if movie.get('type') == 'movie':
                        print(f"Processing movie {i+1}/{len(data['movies'])}: {movie.get('title', 'Unknown')}")
                        data['movies'][i] = self.enhance_movie_metadata(movie)
                        time.sleep(0.25)  # Rate limiting
                    elif movie.get('type') == 'series':
                        print(f"Processing TV series {i+1}/{len(data['movies'])}: {movie.get('title', 'Unknown')}")
                        data['movies'][i] = self.enhance_tv_metadata(movie)
                        time.sleep(0.25)  # Rate limiting
            
            # Process featured movies in home section
            if 'home' in data and 'featured_movies' in data['home']:
                for i, movie in enumerate(data['home']['featured_movies']):
                    print(f"Processing featured movie {i+1}: {movie.get('title', 'Unknown')}")
                    data['home']['featured_movies'][i] = self.enhance_movie_metadata(movie)
                    time.sleep(0.25)  # Rate limiting
            
            # Process slides in home section
            if 'home' in data and 'slides' in data['home']:
                for i, slide in enumerate(data['home']['slides']):
                    if slide.get('type') == 'movie' and 'poster' in slide:
                        print(f"Processing slide movie {i+1}: {slide['poster'].get('title', 'Unknown')}")
                        data['home']['slides'][i]['poster'] = self.enhance_movie_metadata(slide['poster'])
                        time.sleep(0.25)  # Rate limiting
            
            # Update API info
            data['api_info']['last_updated'] = time.strftime('%Y-%m-%d')
            data['api_info']['description'] = "Enhanced Free Movie & TV Streaming JSON API with Full TMDB Integration"
            
            # Save enhanced data
            with open(output_file, 'w', encoding='utf-8') as f:
                json.dump(data, f, indent=2, ensure_ascii=False)
            
            print(f"✅ Enhanced JSON saved to: {output_file}")
            
        except Exception as e:
            print(f"❌ Error processing JSON file: {e}")

def main():
    # Your TMDB API key
    API_KEY = "ec926176bf467b3f7735e3154238c161"
    
    # Initialize TMDB integrator
    integrator = TMDBIntegrator(API_KEY)
    
    # Process the JSON file
    integrator.process_json_file('original_api.json', 'enhanced_movie_api.json')
    
    print("\n🎬 TMDB Integration Complete!")
    print("📝 Summary of enhancements:")
    print("   • Added comprehensive movie metadata from TMDB")
    print("   • Enhanced TV series information")
    print("   • Added high-quality poster and backdrop images")
    print("   • Integrated cast and crew information")
    print("   • Added production companies and networks")
    print("   • Enhanced genres and classifications")
    print("   • Added VidSrc.net embed sources as requested")
    print("   • Maintained all existing stream sources")

if __name__ == "__main__":
    main()