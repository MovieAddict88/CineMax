import json

with open('existing_api.json', 'r') as f:
    data = json.load(f)

# Find and update Big Buck Bunny movie
for movie in data.get('movies', []):
    if movie.get('title') == 'Big Buck Bunny':
        # Update to The Avengers
        movie['title'] = 'The Avengers'
        movie['description'] = 'When an unexpected enemy emerges and threatens global safety and security, Nick Fury, director of the international peacekeeping agency known as S.H.I.E.L.D., finds himself in need of a team to pull the world back from the brink of disaster.'
        movie['year'] = '2012'
        movie['duration'] = '2:23'
        movie['rating'] = 7.8
        movie['imdb'] = '7.8'
        movie['classification'] = 'PG-13'
        
        # Add vidsrc.net embed (keep existing sources)
        vidsrc = {
            "id": 4,
            "type": "embed",
            "title": "VidSrc Embed", 
            "quality": "HD",
            "size": "Stream",
            "kind": "external",
            "premium": "false",
            "external": True,
            "url": "https://vidsrc.net/embed/movie/24428"
        }
        movie['sources'].append(vidsrc)
        break

with open('free_movie_api.json', 'w') as f:
    json.dump(data, f, indent=2)

print("Done. Big Buck Bunny is now The Avengers with vidsrc.net embed.")