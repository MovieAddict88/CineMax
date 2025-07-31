#!/usr/bin/env python3
import json

# Simple TMDB integration - just update Big Buck Bunny to The Avengers
def update_movie_api():
    # Load the existing API
    with open('existing_api.json', 'r') as f:
        data = json.load(f)
    
    # The Avengers TMDB data (manually added for simplicity)
    avengers_data = {
        "title": "The Avengers",
        "description": "When an unexpected enemy emerges and threatens global safety and security, Nick Fury, director of the international peacekeeping agency known as S.H.I.E.L.D., finds himself in need of a team to pull the world back from the brink of disaster.",
        "year": "2012",
        "duration": "2:23",
        "rating": 7.8,
        "imdb": "7.8",
        "classification": "PG-13",
        "image": "https://image.tmdb.org/t/p/w500/RYMX2wcKCBAr24UyPD7xwmjaTn.jpg",
        "cover": "https://image.tmdb.org/t/p/w1280/9BBTo63ANSmhC4e6r62OJFuK2GL.jpg"
    }
    
    # Update all Big Buck Bunny references to The Avengers
    # 1. Update main movies array
    for movie in data.get('movies', []):
        if 'Big Buck Bunny' in movie.get('title', ''):
            movie.update(avengers_data)
            # Add vidsrc.net embed source
            vidsrc_source = {
                "id": len(movie.get('sources', [])) + 1,
                "type": "embed", 
                "title": "VidSrc Embed",
                "quality": "HD",
                "size": "Stream",
                "kind": "external",
                "premium": "false",
                "external": True,
                "url": "https://vidsrc.net/embed/movie/24428"
            }
            movie['sources'].append(vidsrc_source)
    
    # 2. Update home slides
    for slide in data.get('home', {}).get('slides', []):
        if 'Big Buck Bunny' in slide.get('title', ''):
            slide['title'] = "The Avengers"
            slide['image'] = avengers_data['image']
            if 'poster' in slide:
                slide['poster'].update(avengers_data)
    
    # 3. Update featured movies
    for movie in data.get('home', {}).get('featured_movies', []):
        if 'Big Buck Bunny' in movie.get('title', ''):
            movie.update(avengers_data)
    
    # Save the updated API
    with open('free_movie_api.json', 'w') as f:
        json.dump(data, f, indent=2)
    
    print("✅ Successfully updated Big Buck Bunny to The Avengers")
    print("✅ Added vidsrc.net embed source")
    print("✅ Kept all original video sources")

if __name__ == "__main__":
    update_movie_api()