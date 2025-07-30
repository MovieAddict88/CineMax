# CineMax Video Format Guide

## Overview
The app now has **smart format detection** - you only need to worry about a few special cases!

## ✅ Formats That Work Automatically

### Regular Video Files
These work with **any** `type` value - the player auto-detects them:

```json
{
  "type": "video",        // ← Can be anything
  "url": "https://example.com/movie.mp4",
  "kind": "both"          // Can play and download
}
```

**Supported Extensions:**
- `.mp4` - Most common, works perfectly
- `.mkv` - High quality, works automatically  
- `.avi` - Older format, works automatically
- `.webm` - Web format, works automatically
- `.mov` - Apple format, works automatically
- `.m4v` - Apple variant, works automatically

## 🎯 Special Formats (Auto-detected)

### HLS Live Streams (.m3u8)
```json
{
  "type": "live",         // ← Will auto-convert to "m3u8"
  "url": "https://example.com/stream.m3u8",
  "kind": "play"          // Live streams are play-only
}
```

### DASH Streams (.mpd) 
```json
{
  "type": "live",         // ← Will auto-convert to "dash"
  "url": "https://example.com/stream.mpd", 
  "kind": "play"          // DASH streams are play-only
}
```

## ⚠️ Formats Requiring Exact Type

### YouTube Videos
```json
{
  "type": "youtube",      // ← Must be exactly "youtube"
  "url": "https://youtube.com/watch?v=xyz",
  "kind": "play"
}
```

### Embedded Players
```json
{
  "type": "embed",        // ← Must be exactly "embed"
  "url": "https://player.vimeo.com/video/123",
  "kind": "play"
}
```

## 📊 Complete JSON Example

```json
{
  "movies": [
    {
      "id": 1,
      "title": "Sample Movie",
      "sources": [
        {
          "id": 1,
          "type": "video",
          "title": "1080p MP4", 
          "quality": "1080p",
          "size": "2.1GB",
          "kind": "both",
          "url": "https://example.com/movie.mp4"
        },
        {
          "id": 2,
          "type": "video",
          "title": "720p MKV",
          "quality": "720p", 
          "size": "1.2GB",
          "kind": "both",
          "url": "https://example.com/movie.mkv"
        },
        {
          "id": 3,
          "type": "live",
          "title": "HLS Stream",
          "quality": "1080p",
          "size": "Live", 
          "kind": "play",
          "url": "https://example.com/stream.m3u8"
        },
        {
          "id": 4,
          "type": "live", 
          "title": "DASH Stream",
          "quality": "4K",
          "size": "Live",
          "kind": "play",
          "url": "https://example.com/stream.mpd"
        },
        {
          "id": 5,
          "type": "youtube",
          "title": "Trailer",
          "quality": "HD",
          "size": "Trailer",
          "kind": "play", 
          "url": "https://youtube.com/watch?v=xyz"
        }
      ]
    }
  ]
}
```

## 🚀 What Changed

### Before (Manual):
- Had to set exact `type` for every format
- M3U8 and MPD files didn't work
- Required knowledge of player internals

### After (Smart):
- Only 2 special cases: `youtube` and `embed`
- All video files work with `type: "video"`
- Streaming formats auto-detected by URL
- Much simpler to maintain

## ✅ Summary

**You only need to worry about:**
1. **YouTube links** - Use `type: "youtube"`
2. **Embed players** - Use `type: "embed"` 
3. **Everything else** - Use `type: "video"` or `type: "live"`

The app will automatically detect and handle all video formats correctly! 🎉