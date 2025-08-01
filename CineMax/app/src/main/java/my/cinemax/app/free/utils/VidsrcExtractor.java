package my.cinemax.app.free.utils;

import android.util.Log;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * Utility class to extract direct video URLs from vidsrc.net embed pages
 * This allows vidsrc.net URLs to be played in ExoPlayer instead of requiring WebView
 */
public class VidsrcExtractor {
    private static final String TAG = "VidsrcExtractor";
    
    public interface ExtractionListener {
        void onSuccess(String directUrl, String videoType);
        void onError(String error);
    }
    
    private static OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .followRedirects(true)
            .followSslRedirects(true)
            .build();
    
    /**
     * Extract direct video URL from vidsrc.net embed URL
     * @param embedUrl The vidsrc.net embed URL
     * @param listener Callback for success/error
     */
    public static void extractDirectUrl(String embedUrl, ExtractionListener listener) {
        if (embedUrl == null || listener == null) {
            if (listener != null) {
                listener.onError("Invalid parameters");
            }
            return;
        }
        
        Log.d(TAG, "Extracting direct URL from: " + embedUrl);
        
        // Execute in background thread
        new Thread(() -> {
            try {
                String directUrl = extractUrlSync(embedUrl);
                if (directUrl != null && !directUrl.isEmpty()) {
                    // Determine video type from URL
                    String videoType = determineVideoType(directUrl);
                    Log.d(TAG, "Extracted direct URL: " + directUrl + " (Type: " + videoType + ")");
                    listener.onSuccess(directUrl, videoType);
                } else {
                    Log.w(TAG, "Could not extract direct URL from: " + embedUrl);
                    listener.onError("Could not extract video URL");
                }
            } catch (Exception e) {
                Log.e(TAG, "Error extracting URL: " + embedUrl, e);
                listener.onError("Extraction failed: " + e.getMessage());
            }
        }).start();
    }
    
    private static String extractUrlSync(String embedUrl) throws IOException {
        // Add proper headers to mimic browser request
        Request request = new Request.Builder()
                .url(embedUrl)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                .addHeader("Referer", "https://vidsrc.net/")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
                .addHeader("Accept-Language", "en-US,en;q=0.5")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("DNT", "1")
                .addHeader("Connection", "keep-alive")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                Log.w(TAG, "HTTP error: " + response.code() + " for URL: " + embedUrl);
                return null;
            }
            
            ResponseBody body = response.body();
            if (body == null) {
                Log.w(TAG, "Empty response body for URL: " + embedUrl);
                return null;
            }
            
            String html = body.string();
            return parseVideoUrl(html);
        }
    }
    
    private static String parseVideoUrl(String html) {
        // Common patterns to look for video URLs in vidsrc.net pages
        String[] patterns = {
            // M3U8 HLS streams
            "(?:file|src)\\s*[=:]\\s*[\"']([^\"']*\\.m3u8[^\"']*)[\"']",
            // MP4 streams  
            "(?:file|src)\\s*[=:]\\s*[\"']([^\"']*\\.mp4[^\"']*)[\"']",
            // DASH streams
            "(?:file|src)\\s*[=:]\\s*[\"']([^\"']*\\.mpd[^\"']*)[\"']",
            // Generic video URLs
            "(?:file|src)\\s*[=:]\\s*[\"'](https?://[^\"']*(?:mp4|m3u8|mpd)[^\"']*)[\"']",
            // iframe src patterns
            "<iframe[^>]*src\\s*=\\s*[\"']([^\"']*)[\"']",
            // source tags
            "<source[^>]*src\\s*=\\s*[\"']([^\"']*)[\"']",
            // video tags
            "<video[^>]*src\\s*=\\s*[\"']([^\"']*)[\"']"
        };
        
        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(html);
            
            while (matcher.find()) {
                String url = matcher.group(1);
                if (url != null && isValidVideoUrl(url)) {
                    // Clean up the URL
                    url = url.trim();
                    if (url.startsWith("//")) {
                        url = "https:" + url;
                    }
                    return url;
                }
            }
        }
        
        Log.w(TAG, "No video URL patterns found in HTML");
        return null;
    }
    
    private static boolean isValidVideoUrl(String url) {
        if (url == null || url.trim().isEmpty()) {
            return false;
        }
        
        url = url.toLowerCase();
        
        // Check for valid video formats
        return url.contains(".m3u8") || 
               url.contains(".mp4") || 
               url.contains(".mpd") ||
               url.contains("manifest") ||
               (url.startsWith("http") && (url.contains("stream") || url.contains("video")));
    }
    
    private static String determineVideoType(String url) {
        if (url == null) return "mp4";
        
        url = url.toLowerCase();
        
        if (url.contains(".m3u8") || url.contains("hls")) {
            return "m3u8";
        } else if (url.contains(".mpd") || url.contains("dash")) {
            return "dash";
        } else {
            return "mp4";
        }
    }
    
    /**
     * Quick check if URL is a vidsrc.net embed URL that can be extracted
     */
    public static boolean isVidsrcUrl(String url) {
        return url != null && url.contains("vidsrc.net");
    }
}