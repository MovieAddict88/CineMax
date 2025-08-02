package my.cinemax.app.free.Utils;

import android.os.AsyncTask;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class VideoExtractor {
    
    private static final String TAG = "VideoExtractor";
    private static final int TIMEOUT_SECONDS = 15;
    
    public interface ExtractionListener {
        void onSuccess(String directUrl, String quality);
        void onFailure(String error);
    }
    
    public static void extractVideoUrl(String embedUrl, ExtractionListener listener) {
        if (embedUrl == null || embedUrl.trim().isEmpty()) {
            listener.onFailure("Empty or null embed URL provided");
            return;
        }
        
        if (listener == null) {
            Log.e(TAG, "ExtractionListener is null");
            return;
        }
        
        try {
            new ExtractVideoTask(listener).execute(embedUrl.trim());
        } catch (Exception e) {
            Log.e(TAG, "Error starting extraction task: " + e.getMessage(), e);
            listener.onFailure("Failed to start video extraction: " + e.getMessage());
        }
    }
    
    private static class ExtractVideoTask extends AsyncTask<String, Void, VideoResult> {
        private ExtractionListener listener;
        
        public ExtractVideoTask(ExtractionListener listener) {
            this.listener = listener;
        }
        
        @Override
        protected VideoResult doInBackground(String... urls) {
            if (urls == null || urls.length == 0 || urls[0] == null) {
                return new VideoResult(null, "No URL provided for extraction", true);
            }
            
            String embedUrl = urls[0].trim();
            
            if (embedUrl.isEmpty()) {
                return new VideoResult(null, "Empty URL provided for extraction", true);
            }
            
            try {
                Log.d(TAG, "Starting video extraction for: " + embedUrl);
                
                // Determine the extraction method based on the URL
                if (embedUrl.contains("vidsrc.net") || embedUrl.contains("vidsrc.to") || embedUrl.contains("vidsrc.me")) {
                    return extractFromVidsrc(embedUrl);
                } else if (embedUrl.contains("streamtape.com") || embedUrl.contains("streamtape.cc")) {
                    return extractFromStreamtape(embedUrl);
                } else if (embedUrl.contains("mixdrop.co") || embedUrl.contains("mixdrop.to")) {
                    return extractFromMixdrop(embedUrl);
                } else if (embedUrl.contains("upvid.tv") || embedUrl.contains("upvid.cc")) {
                    return extractFromUpvid(embedUrl);
                } else if (embedUrl.contains("mystream.to") || embedUrl.contains("mystream.cc")) {
                    return extractFromMystream(embedUrl);
                } else if (embedUrl.contains("doodstream.com") || embedUrl.contains("dood.to")) {
                    return extractFromDoodstream(embedUrl);
                } else if (embedUrl.contains("streamlab.to") || embedUrl.contains("streamlabs.to")) {
                    return extractFromStreamlab(embedUrl);
                } else if (embedUrl.contains("vidcloud.co") || embedUrl.contains("vidcloud9.com")) {
                    return extractFromVidcloud(embedUrl);
                } else {
                    // Generic extraction for unknown hosts
                    return extractGeneric(embedUrl);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error extracting video URL from " + embedUrl + ": " + e.getMessage(), e);
                return new VideoResult(null, "Extraction failed: " + e.getMessage(), true);
            }
        }
        
        @Override
        protected void onPostExecute(VideoResult result) {
            if (listener == null) {
                Log.e(TAG, "ExtractionListener is null in onPostExecute");
                return;
            }
            
            try {
                if (result != null && result.url != null && !result.url.trim().isEmpty()) {
                    Log.d(TAG, "Extraction successful: " + result.url);
                    listener.onSuccess(result.url.trim(), result.quality != null ? result.quality : "720p");
                } else {
                    String error = result != null ? result.error : "Unknown extraction error";
                    Log.w(TAG, "Extraction failed: " + error);
                    listener.onFailure(error);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error in onPostExecute: " + e.getMessage(), e);
                listener.onFailure("Error processing extraction result: " + e.getMessage());
            }
        }
    }
    
    private static class VideoResult {
        String url;
        String quality;
        String error;
        
        VideoResult(String url, String quality) {
            this.url = url;
            this.quality = quality;
        }
        
        VideoResult(String url, String error, boolean isError) {
            this.url = url;
            this.error = error;
        }
    }
    
    private static VideoResult extractFromVidsrc(String embedUrl) throws IOException {
        Log.d(TAG, "Extracting from Vidsrc: " + embedUrl);
        
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .followRedirects(true)
            .build();
        
        Request request = new Request.Builder()
            .url(embedUrl)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .addHeader("Referer", "https://vidsrc.net/")
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return new VideoResult(null, "HTTP error: " + response.code(), true);
            }
            
            String html = response.body().string();
            
            // Look for video sources in various formats
            List<String> patterns = new ArrayList<>();
            patterns.add("file:\\s*[\"']([^\"']+\\.m3u8[^\"']*)[\"']");
            patterns.add("src:\\s*[\"']([^\"']+\\.m3u8[^\"']*)[\"']");
            patterns.add("[\"']([^\"']*\\.m3u8[^\"']*)[\"']");
            patterns.add("file:\\s*[\"']([^\"']+\\.mp4[^\"']*)[\"']");
            patterns.add("src:\\s*[\"']([^\"']+\\.mp4[^\"']*)[\"']");
            
            for (String patternStr : patterns) {
                Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(html);
                if (matcher.find()) {
                    String videoUrl = matcher.group(1);
                    if (videoUrl.startsWith("//")) {
                        videoUrl = "https:" + videoUrl;
                    } else if (videoUrl.startsWith("/")) {
                        videoUrl = "https://vidsrc.net" + videoUrl;
                    }
                    Log.d(TAG, "Found video URL: " + videoUrl);
                    return new VideoResult(videoUrl, "720p");
                }
            }
            
            // Look for iframe redirects
            Document doc = Jsoup.parse(html);
            Elements iframes = doc.select("iframe");
            for (Element iframe : iframes) {
                String src = iframe.attr("src");
                if (src != null && !src.isEmpty()) {
                    if (src.startsWith("//")) src = "https:" + src;
                    else if (src.startsWith("/")) src = "https://vidsrc.net" + src;
                    
                    // Recursively extract from iframe
                    VideoResult iframeResult = extractGeneric(src);
                    if (iframeResult.url != null) {
                        return iframeResult;
                    }
                }
            }
        }
        
        return new VideoResult(null, "No video URL found in Vidsrc", true);
    }
    
    private static VideoResult extractFromStreamtape(String embedUrl) throws IOException {
        Log.d(TAG, "Extracting from Streamtape: " + embedUrl);
        
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build();
        
        Request request = new Request.Builder()
            .url(embedUrl)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return new VideoResult(null, "HTTP error: " + response.code(), true);
            }
            
            String html = response.body().string();
            
            // Streamtape specific extraction
            Pattern tokenPattern = Pattern.compile("token=([^&\"'\\s]+)");
            Pattern idPattern = Pattern.compile("id=([^&\"'\\s]+)");
            
            Matcher tokenMatcher = tokenPattern.matcher(html);
            Matcher idMatcher = idPattern.matcher(html);
            
            if (tokenMatcher.find() && idMatcher.find()) {
                String token = tokenMatcher.group(1);
                String id = idMatcher.group(1);
                String videoUrl = "https://streamtape.com/get_video?id=" + id + "&token=" + token;
                return new VideoResult(videoUrl, "720p");
            }
            
            // Alternative pattern for Streamtape
            Pattern directPattern = Pattern.compile("'robotlink'\\)\\s*\\+\\s*'([^']+)'");
            Matcher directMatcher = directPattern.matcher(html);
            if (directMatcher.find()) {
                String path = directMatcher.group(1);
                String videoUrl = "https://streamtape.com/get_video" + path;
                return new VideoResult(videoUrl, "720p");
            }
        }
        
        return new VideoResult(null, "No video URL found in Streamtape", true);
    }
    
    private static VideoResult extractFromMixdrop(String embedUrl) throws IOException {
        Log.d(TAG, "Extracting from Mixdrop: " + embedUrl);
        
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build();
        
        Request request = new Request.Builder()
            .url(embedUrl)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
            .build();
        
        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                return new VideoResult(null, "HTTP error: " + response.code(), true);
            }
            
            String html = response.body().string();
            
            // Mixdrop specific patterns
            Pattern pattern = Pattern.compile("wurl\\s*=\\s*\"([^\"]+)\"");
            Matcher matcher = pattern.matcher(html);
            if (matcher.find()) {
                String encodedUrl = matcher.group(1);
                try {
                    String decodedUrl = URLDecoder.decode(encodedUrl, "UTF-8");
                    if (decodedUrl.startsWith("//")) {
                        decodedUrl = "https:" + decodedUrl;
                    }
                    return new VideoResult(decodedUrl, "720p");
                } catch (Exception e) {
                    Log.e(TAG, "Error decoding Mixdrop URL", e);
                }
            }
        }
        
        return new VideoResult(null, "No video URL found in Mixdrop", true);
    }
    
    private static VideoResult extractFromUpvid(String embedUrl) throws IOException {
        Log.d(TAG, "Extracting from Upvid: " + embedUrl);
        return extractGeneric(embedUrl);
    }
    
    private static VideoResult extractFromMystream(String embedUrl) throws IOException {
        Log.d(TAG, "Extracting from Mystream: " + embedUrl);
        return extractGeneric(embedUrl);
    }
    
    private static VideoResult extractFromDoodstream(String embedUrl) throws IOException {
        Log.d(TAG, "Extracting from Doodstream: " + embedUrl);
        return extractGeneric(embedUrl);
    }

    private static VideoResult extractFromStreamlab(String embedUrl) throws IOException {
        Log.d(TAG, "Extracting from Streamlab: " + embedUrl);
        return extractGeneric(embedUrl);
    }

    private static VideoResult extractFromVidcloud(String embedUrl) throws IOException {
        Log.d(TAG, "Extracting from Vidcloud: " + embedUrl);
        return extractGeneric(embedUrl);
    }
    
    private static VideoResult extractGeneric(String embedUrl) throws IOException {
        Log.d(TAG, "Generic extraction for: " + embedUrl);
        
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .followRedirects(true)
            .build();

        Request request = new Request.Builder()
            .url(embedUrl)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
            .addHeader("Referer", "https://google.com")
            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8")
            .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            return new VideoResult(null, "HTTP error: " + response.code(), true);
        }

        String html = response.body().string();
        Document doc = Jsoup.parse(html);
        
        // Try multiple patterns to find video URLs
        List<String> patterns = new ArrayList<>();
        
        // Common video URL patterns
        patterns.add("file[\"']?:\\s*[\"']([^\"']*\\.(mp4|m3u8|mpd)[^\"']*)");
        patterns.add("src[\"']?:\\s*[\"']([^\"']*\\.(mp4|m3u8|mpd)[^\"']*)");
        patterns.add("url[\"']?:\\s*[\"']([^\"']*\\.(mp4|m3u8|mpd)[^\"']*)");
        patterns.add("source[\"']?:\\s*[\"']([^\"']*\\.(mp4|m3u8|mpd)[^\"']*)");
        patterns.add("video_url[\"']?:\\s*[\"']([^\"']*\\.(mp4|m3u8|mpd)[^\"']*)");
        patterns.add("videoUrl[\"']?:\\s*[\"']([^\"']*\\.(mp4|m3u8|mpd)[^\"']*)");
        
        // HLS specific patterns
        patterns.add("[\"']([^\"']*\\.m3u8[^\"']*)");
        patterns.add("hls[\"']?:\\s*[\"']([^\"']*\\.m3u8[^\"']*)");
        patterns.add("playlist[\"']?:\\s*[\"']([^\"']*\\.m3u8[^\"']*)");
        
        // DASH specific patterns
        patterns.add("[\"']([^\"']*\\.mpd[^\"']*)");
        patterns.add("dash[\"']?:\\s*[\"']([^\"']*\\.mpd[^\"']*)");
        patterns.add("manifest[\"']?:\\s*[\"']([^\"']*\\.mpd[^\"']*)");
        
        // MP4 specific patterns
        patterns.add("[\"']([^\"']*\\.mp4[^\"']*)");
        patterns.add("mp4[\"']?:\\s*[\"']([^\"']*\\.mp4[^\"']*)");
        
        // Try each pattern
        for (String patternStr : patterns) {
            Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(html);
            
            while (matcher.find()) {
                String videoUrl = matcher.group(1);
                
                // Skip if it's not a valid video URL
                if (videoUrl.contains("javascript:") || videoUrl.contains("data:") ||
                    videoUrl.length() < 10 || videoUrl.contains("placeholder") ||
                    videoUrl.contains("thumb") || videoUrl.contains("poster")) {
                    continue;
                }
                
                // Handle relative URLs
                if (videoUrl.startsWith("//")) {
                    videoUrl = "https:" + videoUrl;
                } else if (videoUrl.startsWith("/")) {
                    String baseUrl = embedUrl.substring(0, embedUrl.indexOf("/", 8));
                    videoUrl = baseUrl + videoUrl;
                } else if (!videoUrl.startsWith("http")) {
                    // Skip invalid URLs
                    continue;
                }
                
                Log.d(TAG, "Found potential video URL: " + videoUrl);
                
                // Validate the URL by checking if it's accessible
                try {
                    Request testRequest = new Request.Builder()
                        .url(videoUrl)
                        .head() // Use HEAD request to check if URL is valid
                        .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                        .addHeader("Referer", embedUrl)
                        .build();
                    
                    Response testResponse = client.newCall(testRequest).execute();
                    if (testResponse.isSuccessful()) {
                        testResponse.close();
                        Log.d(TAG, "Successfully validated video URL: " + videoUrl);
                        return new VideoResult(videoUrl, "720p");
                    }
                    testResponse.close();
                } catch (Exception e) {
                    Log.d(TAG, "Failed to validate URL: " + videoUrl + " - " + e.getMessage());
                    // Continue trying other URLs
                }
            }
        }
        
        // Try to find iframes and extract recursively (limited depth)
        Elements iframes = doc.select("iframe");
        for (Element iframe : iframes) {
            String src = iframe.attr("src");
            if (!src.isEmpty() && !src.equals(embedUrl)) {
                // Prevent infinite recursion by checking if we've seen this URL before
                if (src.startsWith("//")) {
                    src = "https:" + src;
                } else if (src.startsWith("/")) {
                    String baseUrl = embedUrl.substring(0, embedUrl.indexOf("/", 8));
                    src = baseUrl + src;
                }
                
                try {
                    Log.d(TAG, "Trying iframe source: " + src);
                    VideoResult iframeResult = extractGeneric(src);
                    if (iframeResult.url != null) {
                        return iframeResult;
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Failed to extract from iframe: " + e.getMessage());
                }
            }
        }

        // Look for video tags as last resort
        Elements videos = doc.select("video source, video");
        for (Element video : videos) {
            String src = video.attr("src");
            if (src.isEmpty()) {
                src = video.attr("data-src");
            }
            
            if (!src.isEmpty()) {
                if (src.startsWith("//")) {
                    src = "https:" + src;
                } else if (src.startsWith("/")) {
                    String baseUrl = embedUrl.substring(0, embedUrl.indexOf("/", 8));
                    src = baseUrl + src;
                }
                
                Log.d(TAG, "Found video tag source: " + src);
                return new VideoResult(src, "720p");
            }
        }

        return new VideoResult(null, "No video URL found in generic extraction", true);
    }
}