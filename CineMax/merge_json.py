#!/usr/bin/env python3
import json

# Read the original JSON file
with open('current_movie_api.json', 'r', encoding='utf-8') as f:
    original_data = json.load(f)

# Enhanced movies data with views, created_at, and actors
enhanced_movies = [
    {
        "id": 1,
        "title": "Big Buck Bunny",
        "type": "movie",
        "label": "Animation",
        "sublabel": "Short Film",
        "imdb": "8.5",
        "downloadas": "big-buck-bunny.mp4",
        "comment": True,
        "playas": "video",
        "description": "Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself. When one sunny day three rodents rudely harass him by throwing nuts, hitting his flowers, and destroying his home, he snaps and goes after them.",
        "classification": "G",
        "year": "2008",
        "duration": "10:00",
        "rating": 8.5,
        "views": 15000,
        "created_at": "2024-01-01",
        "image": "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217",
        "cover": "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217",
        "genres": [
            {
                "id": 6,
                "title": "Animation"
            }
        ],
        "actors": [
            {
                "id": 1,
                "name": "Tom Hanks",
                "type": "actor",
                "role": "Lead Actor",
                "image": "https://example.com/tom-hanks.jpg",
                "born": "1956-07-09",
                "height": "6'0\"",
                "bio": "American actor and filmmaker"
            }
        ],
        "sources": [
            {
                "id": 1,
                "type": "video",
                "title": "Big Buck Bunny 1080p",
                "quality": "1080p",
                "size": "264MB",
                "kind": "both",
                "premium": "false",
                "external": False,
                "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            },
            {
                "id": 2,
                "type": "video",
                "title": "Big Buck Bunny 720p",
                "quality": "720p",
                "size": "128MB",
                "kind": "both",
                "premium": "false",
                "external": False,
                "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            },
            {
                "id": 3,
                "type": "video",
                "title": "Big Buck Bunny 480p",
                "quality": "480p",
                "size": "64MB",
                "kind": "both",
                "premium": "false",
                "external": False,
                "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            }
        ],
        "trailer": {
            "id": 4,
            "type": "video",
            "title": "Big Buck Bunny Trailer",
            "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
        }
    },
    {
        "id": 2,
        "title": "Elephants Dream",
        "type": "movie",
        "label": "Animation",
        "sublabel": "Short Film",
        "imdb": "7.8",
        "downloadas": "elephants-dream.mp4",
        "comment": True,
        "playas": "video",
        "description": "The world's first open movie, Elephants Dream is a story about two characters exploring a capricious and seemingly infinite machine.",
        "classification": "PG",
        "year": "2006",
        "duration": "10:53",
        "rating": 7.8,
        "views": 12000,
        "created_at": "2024-01-02",
        "image": "https://upload.wikimedia.org/wikipedia/commons/e/e7/Elephants_Dream_s_blue_elephant.png",
        "cover": "https://upload.wikimedia.org/wikipedia/commons/e/e7/Elephants_Dream_s_blue_elephant.png",
        "genres": [
            {
                "id": 6,
                "title": "Animation"
            },
            {
                "id": 5,
                "title": "Sci-Fi"
            }
        ],
        "actors": [
            {
                "id": 2,
                "name": "Emma Watson",
                "type": "actress",
                "role": "Lead Actress",
                "image": "https://example.com/emma-watson.jpg",
                "born": "1990-04-15",
                "height": "5'5\"",
                "bio": "English actress and activist"
            }
        ],
        "sources": [
            {
                "id": 4,
                "type": "video",
                "title": "Elephants Dream 1080p",
                "quality": "1080p",
                "size": "300MB",
                "kind": "both",
                "premium": "false",
                "external": False,
                "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
            }
        ],
        "trailer": {
            "id": 5,
            "type": "video",
            "title": "Elephants Dream Trailer",
            "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ElephantsDream.mp4"
        }
    },
    {
        "id": 3,
        "title": "Sample Action Movie",
        "type": "movie",
        "label": "Action",
        "sublabel": "Blockbuster",
        "imdb": "8.2",
        "downloadas": "sample-action.mp4",
        "comment": True,
        "playas": "video",
        "description": "An exciting action movie with amazing stunts and thrilling sequences.",
        "classification": "PG-13",
        "year": "2023",
        "duration": "120:00",
        "rating": 8.2,
        "views": 25000,
        "created_at": "2024-01-03",
        "image": "https://example.com/sample-action.jpg",
        "cover": "https://example.com/sample-action-cover.jpg",
        "genres": [
            {
                "id": 1,
                "title": "Action"
            },
            {
                "id": 3,
                "title": "Drama"
            }
        ],
        "actors": [
            {
                "id": 1,
                "name": "Tom Hanks",
                "type": "actor",
                "role": "Lead Actor",
                "image": "https://example.com/tom-hanks.jpg",
                "born": "1956-07-09",
                "height": "6'0\"",
                "bio": "American actor and filmmaker"
            },
            {
                "id": 3,
                "name": "Leonardo DiCaprio",
                "type": "actor",
                "role": "Supporting Actor",
                "image": "https://example.com/leonardo-dicaprio.jpg",
                "born": "1974-11-11",
                "height": "6'0\"",
                "bio": "American actor, film producer, and environmentalist"
            }
        ],
        "sources": [
            {
                "id": 6,
                "type": "video",
                "title": "Sample Action Movie 1080p",
                "quality": "1080p",
                "size": "2.5GB",
                "kind": "both",
                "premium": "false",
                "external": False,
                "url": "https://example.com/sample-action-1080p.mp4"
            }
        ],
        "trailer": {
            "id": 7,
            "type": "video",
            "title": "Sample Action Movie Trailer",
            "url": "https://example.com/sample-action-trailer.mp4"
        }
    }
]

# Enhanced actors data
enhanced_actors = [
    {
        "id": 1,
        "name": "Tom Hanks",
        "type": "actor",
        "role": "Lead Actor",
        "image": "https://example.com/tom-hanks.jpg",
        "born": "1956-07-09",
        "height": "6'0\"",
        "bio": "American actor and filmmaker",
        "movies": [
            {
                "id": 1,
                "title": "Big Buck Bunny",
                "image": "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217",
                "year": "2008"
            },
            {
                "id": 3,
                "title": "Sample Action Movie",
                "image": "https://example.com/sample-action.jpg",
                "year": "2023"
            }
        ]
    },
    {
        "id": 2,
        "name": "Emma Watson",
        "type": "actress",
        "role": "Lead Actress",
        "image": "https://example.com/emma-watson.jpg",
        "born": "1990-04-15",
        "height": "5'5\"",
        "bio": "English actress and activist",
        "movies": [
            {
                "id": 2,
                "title": "Elephants Dream",
                "image": "https://upload.wikimedia.org/wikipedia/commons/e/e7/Elephants_Dream_s_blue_elephant.png",
                "year": "2006"
            }
        ]
    },
    {
        "id": 3,
        "name": "Leonardo DiCaprio",
        "type": "actor",
        "role": "Lead Actor",
        "image": "https://example.com/leonardo-dicaprio.jpg",
        "born": "1974-11-11",
        "height": "6'0\"",
        "bio": "American actor, film producer, and environmentalist",
        "movies": [
            {
                "id": 3,
                "title": "Sample Action Movie",
                "image": "https://example.com/sample-action.jpg",
                "year": "2023"
            }
        ]
    }
]

# Enhanced genres with proper movie associations
enhanced_genres = [
    {
        "id": 1,
        "title": "Action",
        "posters": [
            {
                "id": 3,
                "title": "Sample Action Movie",
                "image": "https://example.com/sample-action.jpg"
            }
        ]
    },
    {
        "id": 2,
        "title": "Comedy",
        "posters": []
    },
    {
        "id": 3,
        "title": "Drama",
        "posters": [
            {
                "id": 3,
                "title": "Sample Action Movie",
                "image": "https://example.com/sample-action.jpg"
            }
        ]
    },
    {
        "id": 4,
        "title": "Horror",
        "posters": []
    },
    {
        "id": 5,
        "title": "Sci-Fi",
        "posters": [
            {
                "id": 2,
                "title": "Elephants Dream",
                "image": "https://upload.wikimedia.org/wikipedia/commons/e/e7/Elephants_Dream_s_blue_elephant.png"
            }
        ]
    },
    {
        "id": 6,
        "title": "Animation",
        "posters": [
            {
                "id": 1,
                "title": "Big Buck Bunny",
                "image": "https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217"
            },
            {
                "id": 2,
                "title": "Elephants Dream",
                "image": "https://upload.wikimedia.org/wikipedia/commons/e/e7/Elephants_Dream_s_blue_elephant.png"
            }
        ]
    }
]

# Create the enhanced JSON structure
enhanced_data = {
    "api_info": {
        "version": "2.0",
        "description": "Enhanced Free Movie & TV Streaming JSON API with Full Actor Support",
        "last_updated": "2024-01-15",
        "total_movies": 3,
        "total_channels": 26,
        "total_actors": 3
    },
    "home": original_data["home"],
    "movies": enhanced_movies,
    "channels": original_data["channels"],  # Keep ALL original channels
    "actors": enhanced_actors,
    "genres": enhanced_genres,
    "categories": original_data["categories"],
    "countries": original_data["countries"],
    "subscription_plans": original_data["subscription_plans"],
    "video_sources": original_data["video_sources"],
    "ads_config": original_data["ads_config"]
}

# Update the home section to include enhanced movie data
enhanced_data["home"]["slides"][0]["poster"]["views"] = 15000
enhanced_data["home"]["slides"][0]["poster"]["created_at"] = "2024-01-01"
enhanced_data["home"]["slides"][0]["poster"]["actors"] = [
    {
        "id": 1,
        "name": "Tom Hanks",
        "type": "actor",
        "role": "Lead Actor",
        "image": "https://example.com/tom-hanks.jpg",
        "born": "1956-07-09",
        "height": "6'0\"",
        "bio": "American actor and filmmaker"
    }
]

# Write the enhanced JSON file
with open('enhanced_movie_api_complete.json', 'w', encoding='utf-8') as f:
    json.dump(enhanced_data, f, indent=2, ensure_ascii=False)

print("✅ Enhanced JSON file created successfully!")
print("📊 Statistics:")
print(f"   - Movies: {len(enhanced_movies)}")
print(f"   - Channels: {len(original_data['channels'])} (all preserved)")
print(f"   - Actors: {len(enhanced_actors)}")
print(f"   - Genres: {len(enhanced_genres)}")
print("\n🎯 Ready to upload to GitHub!")