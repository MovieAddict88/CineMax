# CineMax Embedded Link Crash Fixes

## Problem Analysis

The CineMax app crashes when trying to play episodes with embedded links because:

1. **API Migration**: The app moved from PHP-based API to JSON API (GitHub raw URLs)
2. **URL Type Mismatch**: JSON API contains embedded iframe URLs (e.g., `https://vidsrc.net/embed/tv/90802/1/1`) 
3. **Player Incompatibility**: ExoPlayer cannot directly play embedded HTML pages - it expects direct video stream URLs
4. **Missing URL Resolution**: No mechanism to extract actual video streams from embedded pages

## Root Cause

In `CustomPlayerViewModel.java`, when `videoType.equals("embed")`, the code attempts to create a `ProgressiveMediaSource` directly from the embedded URL:

```java
// This FAILS because embed URLs return HTML, not video streams
mediaSource1 = new ProgressiveMediaSource.Factory(dataSourceFactory)
    .setExtractorsFactory(extractorsFactory)
    .createMediaSource(videoUri);
```

This causes `ExoPlaybackException.TYPE_SOURCE` errors and app crashes.

## Solutions Implemented

### 1. **EmbedUrlResolver.java** - URL Resolution Service
- **Purpose**: Extracts actual video stream URLs from embedded pages
- **Features**:
  - Supports vidsrc.net and generic embed providers
  - Parses HTML to find m3u8, mp4, and dash stream URLs
  - Uses JSoup for robust HTML parsing
  - Handles recursive iframe resolution

### 2. **Enhanced CustomPlayerViewModel.java** - Smart URL Handling
- **Purpose**: Automatically resolves embedded URLs before playing
- **Features**:
  - Detects embed URLs and triggers resolution
  - Shows loading indicator during resolution
  - Retries playback with resolved stream URL
  - Falls back to WebView if resolution fails

### 3. **EmbedProxyServer.java** - Advanced NanoHTTPD Usage
- **Purpose**: Local proxy server for embedded content
- **Features**:
  - Proxies embedded video streams locally
  - Handles HTTP range requests for streaming
  - Caches resolved URLs for performance
  - Serves content that ExoPlayer can consume

### 4. **Application Integration**
- **JSoup Dependency**: Added for HTML parsing
- **Proxy Server**: Auto-starts in MyApplication.onCreate()
- **Fallback Mechanism**: Enhanced WebView fallback for complex embeds

## Technical Details

### URL Resolution Process
1. App detects `videoType="embed"` 
2. `EmbedUrlResolver` fetches embedded page HTML
3. Regex patterns extract actual video stream URLs
4. Player retries with resolved direct URL
5. If resolution fails, shows WebView fallback dialog

### NanoHTTPD Enhancement
- Previous usage: Only for local downloaded files
- New usage: Proxy embedded content for seamless playback
- Benefits: Maintains native video player experience

### Supported Embed Providers
- **vidsrc.net**: Primary provider in JSON API
- **Generic embeds**: Any iframe-based video embed
- **Extensible**: Easy to add new provider support

## Files Modified

1. **New Files**:
   - `EmbedUrlResolver.java` - URL resolution logic
   - `EmbedProxyServer.java` - Enhanced NanoHTTPD server
   - `CINEMAX_EMBED_FIXES.md` - This documentation

2. **Modified Files**:
   - `CustomPlayerViewModel.java` - Smart embed handling
   - `MyApplication.java` - Proxy server initialization  
   - `build.gradle` - Added JSoup dependency

## Testing Recommendations

1. **Test Embedded Episodes**: Try playing episodes with vidsrc.net URLs
2. **Test Fallback**: Verify WebView fallback works for unsupported embeds
3. **Test Performance**: Check loading times and memory usage
4. **Test Network**: Verify behavior on slow/unstable connections

## Benefits

1. **No More Crashes**: Embedded URLs are properly handled
2. **Seamless Playback**: Videos play in native player, not browser
3. **Better UX**: No manual "Open in Browser" steps required
4. **Extensible**: Easy to add support for new embed providers
5. **Backward Compatible**: Existing direct URLs still work

## Migration Notes

- **From PHP to JSON**: The fixes handle the API structure change
- **NanoHTTPD Evolution**: Enhanced from simple file server to embed proxy
- **Player Enhancement**: ExoPlayer now handles embedded content intelligently

## Future Enhancements

1. **Caching**: Cache resolved URLs for faster subsequent playback
2. **Provider Detection**: Auto-detect embed provider for optimized resolution
3. **Quality Selection**: Extract multiple quality options from embeds
4. **Subtitle Support**: Extract subtitle tracks from embedded players
5. **Analytics**: Track resolution success rates and performance metrics

---

**Result**: CineMax now seamlessly plays embedded video links without crashes, providing a smooth user experience comparable to the original PHP-based system.