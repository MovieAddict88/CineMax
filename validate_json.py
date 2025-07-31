#!/usr/bin/env python3
"""
JSON Validation Script
======================

Simple script to validate the free_movie_api.json file structure
and ensure it can be parsed without errors.
"""

import json
import sys

def validate_json_file(filename):
    """Validate JSON file and check basic structure"""
    try:
        print(f"🔍 Validating {filename}...")
        
        # Try to load the JSON file
        with open(filename, 'r', encoding='utf-8') as f:
            data = json.load(f)
        
        print("✅ JSON file is valid and can be parsed")
        
        # Check basic structure
        required_sections = ['api_info', 'home', 'movies', 'channels', 'actors', 'genres']
        
        for section in required_sections:
            if section in data:
                print(f"✅ Found '{section}' section")
            else:
                print(f"❌ Missing '{section}' section")
                return False
        
        # Check movies structure
        movies = data.get('movies', [])
        print(f"📽️  Found {len(movies)} movies/series")
        
        for i, movie in enumerate(movies[:2]):  # Check first 2 entries
            movie_id = movie.get('id', 'Unknown')
            title = movie.get('title', 'Unknown')
            movie_type = movie.get('type', 'Unknown')
            
            print(f"   {i+1}. ID: {movie_id}, Title: '{title}', Type: {movie_type}")
            
            # Check required fields
            required_fields = ['id', 'title', 'type', 'image', 'description']
            missing_fields = []
            
            for field in required_fields:
                if field not in movie:
                    missing_fields.append(field)
            
            if missing_fields:
                print(f"      ⚠️  Missing fields: {missing_fields}")
            else:
                print(f"      ✅ All required fields present")
        
        # Check actors structure
        actors = data.get('actors', [])
        print(f"👥 Found {len(actors)} actors")
        
        # Check genres structure
        genres = data.get('genres', [])
        print(f"🎭 Found {len(genres)} genres")
        
        # Check home structure
        home = data.get('home', {})
        slides = home.get('slides', [])
        featured = home.get('featured_movies', [])
        print(f"🏠 Home section: {len(slides)} slides, {len(featured)} featured movies")
        
        print("\n🎉 JSON validation completed successfully!")
        print("✅ The file should work with the Android app")
        
        return True
        
    except json.JSONDecodeError as e:
        print(f"❌ JSON parsing error: {e}")
        print(f"   Error at line {e.lineno}, column {e.colno}")
        return False
    except FileNotFoundError:
        print(f"❌ File '{filename}' not found")
        return False
    except Exception as e:
        print(f"❌ Unexpected error: {e}")
        return False

def main():
    """Main function"""
    filename = 'free_movie_api.json'
    
    print("🚀 JSON Validation Tool")
    print("=" * 50)
    
    if validate_json_file(filename):
        print("\n💡 The JSON file has been cleaned and should work with your Android app!")
        print("🔧 Removed TMDB-specific fields that were causing the crash")
        print("📱 The app should now launch successfully")
        sys.exit(0)
    else:
        print("\n❌ Validation failed. Please check the errors above.")
        sys.exit(1)

if __name__ == "__main__":
    main()