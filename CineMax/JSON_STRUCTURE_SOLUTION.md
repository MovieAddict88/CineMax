# CineMax JSON Structure Solution - Recommended Approach

## Overview
Instead of modifying the app code to handle different JSON formats, the cleaner solution is to **update the JSON structure** to use the `kind` values that the app was originally designed to expect.

## Current Problem
Your GitHub JSON uses:
```json
{
  "kind": "mp4",    // ❌ App doesn't recognize this
  "type": "video"
}
```

But the app expects:
```json
{
  "kind": "both",   // ✅ App recognizes this
  "type": "video"
}
```

## Recommended JSON Changes

### Update Your `free_movie_api.json` File

Change the `kind` values in all sources from format types to semantic actions:

#### For MP4 Video Files (Downloadable & Playable):
```json
{
  "id": 1,
  "type": "video",
  "title": "Big Buck Bunny 1080p",
  "quality": "1080p",
  "size": "264MB",
  "kind": "both",        // ✅ Changed from "mp4" to "both"
  "premium": "false",
  "external": false,
  "url": "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
}
```

#### For HLS Live Streams (Playable Only):
```json
{
  "id": 6,
  "type": "live",
  "title": "720p Live Stream",
  "quality": "720p", 
  "size": "Live",
  "kind": "play",        // ✅ Changed from "hls" to "play"
  "premium": "false",
  "external": false,
  "url": "https://qp-pldt-live-grp-02-prod.akamaized.net/out/u/tv5_5.m3u8"
}
```

### Kind Values Reference

| **kind Value** | **Behavior** | **Use Cases** |
|----------------|--------------|---------------|
| `"both"` | Can be played AND downloaded | MP4 video files, offline content |
| `"play"` | Can only be played | HLS streams, YouTube, live TV |
| `"download"` | Can only be downloaded | Audio files, documents |

## Migration Steps

1. **Backup your current JSON file**
2. **Update all `kind` values** in `free_movie_api.json`:
   - Change `"kind": "mp4"` → `"kind": "both"`
   - Change `"kind": "hls"` → `"kind": "play"`
3. **Keep `type` field unchanged** (it serves a different purpose)
4. **Test the app** - sources should now appear and be playable

## Example Updated JSON Structure

See `recommended_json_structure.json` for a complete example of the corrected format.

## Benefits of This Approach

✅ **Cleaner Code**: App logic remains simple and maintainable  
✅ **Semantic Clarity**: `kind` describes what you can DO with the source  
✅ **Original Design**: Aligns with app's intended architecture  
✅ **Future Proof**: Easy to add new kinds like "stream-only" or "premium"  

## Quick Fix Summary

**Instead of changing 100+ lines of code, just update your JSON:**

- `"kind": "mp4"` → `"kind": "both"` (for downloadable videos)
- `"kind": "hls"` → `"kind": "play"` (for live streams)

The app will immediately recognize these sources and allow playback/download as intended!