#!/usr/bin/env python3
"""
Clean JSON for Android App Compatibility
========================================

This script removes all TMDB-specific fields from free_movie_api.json
that cause the Android app to crash, while keeping all the enhanced
TMDB content (images, descriptions, cast, etc.)
"""

import json
import sys

def clean_api_info(api_info):
    """Remove TMDB integration from api_info"""
    if 'tmdb_integration' in api_info:
        del api_info['tmdb_integration']
        print("✅ Removed tmdb_integration from api_info")
    return api_info

def clean_movie_or_series(item):
    """Remove TMDB-specific fields from movie or series"""
    fields_to_remove = ['tmdb_id', 'tmdb_rating', 'tmdb_metadata', 'backdrop_path']
    
    for field in fields_to_remove:
        if field in item:
            del item[field]
    
    # Clean seasons if it's a TV series
    if 'seasons' in item:
        for season in item['seasons']:
            clean_season(season)
    
    return item

def clean_season(season):
    """Remove TMDB fields from season"""
    fields_to_remove = ['tmdb_season_id']
    
    for field in fields_to_remove:
        if field in season:
            del season[field]
    
    # Clean episodes
    if 'episodes' in season:
        for episode in season['episodes']:
            clean_episode(episode)
    
    return season

def clean_episode(episode):
    """Remove TMDB fields from episode"""
    fields_to_remove = ['tmdb_episode_id']
    
    for field in fields_to_remove:
        if field in episode:
            del episode[field]
    
    return episode

def clean_actor(actor):
    """Remove TMDB fields from actor"""
    fields_to_remove = ['tmdb_id']
    
    for field in fields_to_remove:
        if field in actor:
            del actor[field]
    
    return actor

def clean_poster_in_nested_structures(data):
    """Clean poster objects in home slides, featured_movies, and genres"""
    
    # Clean home slides
    if 'home' in data and 'slides' in data['home']:
        for slide in data['home']['slides']:
            if 'poster' in slide:
                clean_movie_or_series(slide['poster'])
    
    # Clean featured movies
    if 'home' in data and 'featured_movies' in data['home']:
        for movie in data['home']['featured_movies']:
            clean_movie_or_series(movie)
    
    # Clean genres posters
    if 'genres' in data:
        for genre in data['genres']:
            if 'posters' in genre:
                for poster in genre['posters']:
                    clean_movie_or_series(poster)

def clean_json_file(input_file, output_file):
    """Clean the JSON file by removing TMDB-specific fields"""
    
    print(f"🔧 Loading {input_file}...")
    
    try:
        with open(input_file, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        print("✅ JSON loaded successfully")
        
        # Clean api_info
        if 'api_info' in data:
            data['api_info'] = clean_api_info(data['api_info'])
        
        # Clean main movies array
        if 'movies' in data:
            print(f"🎬 Cleaning {len(data['movies'])} movies/series...")
            for i, movie in enumerate(data['movies']):
                data['movies'][i] = clean_movie_or_series(movie)
                print(f"   ✅ Cleaned: {movie.get('title', 'Unknown')}")
        
        # Clean actors
        if 'actors' in data:
            print(f"👥 Cleaning {len(data['actors'])} actors...")
            for i, actor in enumerate(data['actors']):
                data['actors'][i] = clean_actor(actor)
        
        # Clean nested poster structures
        print("🏠 Cleaning nested poster structures...")
        clean_poster_in_nested_structures(data)
        
        # Save cleaned file
        print(f"💾 Saving cleaned file to {output_file}...")
        with open(output_file, 'w', encoding='utf-8') as f:
            json.dump(data, f, indent=2, ensure_ascii=False)
        
        print("✅ File cleaned and saved successfully!")
        return True
        
    except Exception as e:
        print(f"❌ Error: {e}")
        return False

def main():
    """Main function"""
    input_file = 'original_free_movie_api.json'
    output_file = 'free_movie_api.json'
    
    print("🚀 Android App JSON Cleaner")
    print("=" * 50)
    print("This will remove TMDB-specific fields that cause app crashes")
    print("while keeping all enhanced content (images, descriptions, cast)")
    print()
    
    if clean_json_file(input_file, output_file):
        print("\n🎉 SUCCESS!")
        print("✅ free_movie_api.json has been cleaned for Android compatibility")
        print("📱 Your app should now launch without crashing")
        print("🎬 All enhanced TMDB content is preserved")
        
        # Validate the cleaned file
        try:
            with open(output_file, 'r', encoding='utf-8') as f:
                json.load(f)
            print("✅ Cleaned file is valid JSON")
        except:
            print("❌ Warning: Cleaned file has JSON errors")
            
    else:
        print("\n❌ FAILED!")
        print("Could not clean the JSON file")
        sys.exit(1)

if __name__ == "__main__":
    main()