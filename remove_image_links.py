#!/usr/bin/env python3
"""
Remove ALL Image Links from Movies and TV Series
This script removes all image URLs from movies and TV series to achieve 100% auto-detection
without any hardcoded image links in the JSON data.
Live TV channels remain untouched.
"""

import json
from datetime import datetime

def remove_image_links_from_movies_and_series():
    """Remove all image links from movies and TV series only"""
    
    # Load the current JSON
    with open('final_real_content_api.json', 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    print("🚫 Removing ALL image links from movies and TV series...")
    
    def clean_movie_or_series_entry(entry):
        """Remove image links from a movie or series entry"""
        if entry.get('type') in ['movie', 'series']:
            # Remove main image links
            if 'image' in entry:
                del entry['image']
            if 'cover' in entry:
                del entry['cover']
            
            # Remove actor images
            if 'actors' in entry:
                for actor in entry['actors']:
                    if 'image' in actor:
                        del actor['image']
            
            # Remove trailer image if it exists
            if 'trailer' in entry and entry['trailer'] and 'image' in entry['trailer']:
                del entry['trailer']['image']
            
            # For TV series, remove episode images
            if entry.get('type') == 'series' and 'seasons' in entry:
                for season in entry['seasons']:
                    if 'episodes' in season:
                        for episode in season['episodes']:
                            if 'image' in episode:
                                del episode['image']
        
        return entry
    
    # Clean movies array
    if 'movies' in data:
        for i, movie in enumerate(data['movies']):
            data['movies'][i] = clean_movie_or_series_entry(movie)
            if movie.get('type') == 'movie':
                print(f"   🎬 Cleaned movie: {movie.get('title', 'Unknown')}")
            elif movie.get('type') == 'series':
                print(f"   📺 Cleaned series: {movie.get('title', 'Unknown')}")
    
    # Clean home featured_movies
    if 'home' in data and 'featured_movies' in data['home']:
        for i, movie in enumerate(data['home']['featured_movies']):
            data['home']['featured_movies'][i] = clean_movie_or_series_entry(movie)
            print(f"   ⭐ Cleaned featured: {movie.get('title', 'Unknown')}")
    
    # Clean home slides (only movies/series, keep channels untouched)
    if 'home' in data and 'slides' in data['home']:
        for slide in data['home']['slides']:
            if slide.get('type') == 'movie':
                # Remove slide image for movies
                if 'image' in slide:
                    del slide['image']
                # Clean the poster object
                if 'poster' in slide:
                    slide['poster'] = clean_movie_or_series_entry(slide['poster'])
                print(f"   🎞️ Cleaned slide: {slide.get('title', 'Unknown')}")
            elif slide.get('type') == 'channel':
                # Keep channels completely untouched
                print(f"   📡 Kept channel untouched: {slide.get('title', 'Unknown')}")
    
    # Update API description
    data['api_info']['description'] = "Enhanced Free Movie & TV Streaming JSON API with 100% TMDB Auto-Detection (No Image Links)"
    data['api_info']['last_updated'] = datetime.now().strftime('%Y-%m-%d')
    
    # Save the cleaned JSON
    output_file = 'clean_no_images_api.json'
    with open(output_file, 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2, ensure_ascii=False)
    
    print("✅ Successfully removed ALL image links from movies and TV series!")
    print(f"📁 Clean JSON saved as: {output_file}")
    print("🎯 Now it's 100% auto-detection - no hardcoded image links!")
    print("🔴 Live TV channels remain completely untouched")

if __name__ == "__main__":
    remove_image_links_from_movies_and_series()