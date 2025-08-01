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
        new ExtractVideoTask(listener).execute(embedUrl);
    }
    
    private static class ExtractVideoTask extends AsyncTask<String, Void, VideoResult> {
        private ExtractionListener listener;
        
        public ExtractVideoTask(ExtractionListener listener) {
            this.listener = listener;
        }
        
        @Override
        protected VideoResult doInBackground(String... urls) {
            String embedUrl = urls[0];
            
            try {
                // Determine the extraction method based on the URL
                if (embedUrl.contains("vidsrc.net") || embedUrl.contains("vidsrc.to")) {
                    return extractFromVidsrc(embedUrl);
                } else if (embedUrl.contains("streamtape.com")) {
                    return extractFromStreamtape(embedUrl);
                } else if (embedUrl.contains("mixdrop.co")) {
                    return extractFromMixdrop(embedUrl);
                } else if (embedUrl.contains("upvid.tv")) {
                    return extractFromUpvid(embedUrl);
                } else if (embedUrl.contains("mystream.to")) {
                    return extractFromMystream(embedUrl);
                } else {
                    // Generic extraction for unknown hosts
                    return extractGeneric(embedUrl);
                }
            } catch (Exception e) {
                Log.e(TAG, "Error extracting video URL: " + e.getMessage(), e);
                return new VideoResult(null, "Extraction failed: " + e.getMessage(), true);
            }
        }
        
        @Override
        protected void onPostExecute(VideoResult result) {
            if (result.url != null) {
                listener.onSuccess(result.url, result.quality);
            } else {
                listener.onFailure(result.error);
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
    
    private static VideoResult extractGeneric(String embedUrl) throws IOException {
        Log.d(TAG, "Generic extraction for: " + embedUrl);
        
        OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .followRedirects(true)
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
            
            // Generic patterns to find video URLs
            List<String> patterns = new ArrayList<>();
            patterns.add("file\\s*:\\s*[\"']([^\"']+\\.m3u8[^\"']*)[\"']");
            patterns.add("src\\s*:\\s*[\"']([^\"']+\\.m3u8[^\"']*)[\"']");
            patterns.add("source\\s*:\\s*[\"']([^\"']+\\.m3u8[^\"']*)[\"']");
            patterns.add("file\\s*:\\s*[\"']([^\"']+\\.mp4[^\"']*)[\"']");
            patterns.add("src\\s*:\\s*[\"']([^\"']+\\.mp4[^\"']*)[\"']");
            patterns.add("source\\s*:\\s*[\"']([^\"']+\\.mp4[^\"']*)[\"']");
            patterns.add("[\"']([^\"']*\\.m3u8[^\"']*)[\"']");
            patterns.add("[\"']([^\"']*\\.mp4[^\"']*)[\"']");
            
            for (String patternStr : patterns) {
                Pattern pattern = Pattern.compile(patternStr, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(html);
                if (matcher.find()) {
                    String videoUrl = matcher.group(1);
                    
                    // Skip if it's not a valid video URL
                    if (videoUrl.contains("javascript:") || videoUrl.contains("data:") || 
                        videoUrl.length() < 10 || videoUrl.contains("placeholder")) {
                        continue;
                    }
                    
                    // Fix relative URLs
                    if (videoUrl.startsWith("//")) {
                        videoUrl = "https:" + videoUrl;
                    } else if (videoUrl.startsWith("/")) {
                        String baseUrl = embedUrl.substring(0, embedUrl.indexOf("/", 8));
                        videoUrl = baseUrl + videoUrl;
                    }
                    
                    Log.d(TAG, "Found video URL: " + videoUrl);
                    return new VideoResult(videoUrl, "720p");
                }
            }
            
            // Look for video tags
            Document doc = Jsoup.parse(html);
            Elements videos = doc.select("video source, video");
            for (Element video : videos) {
                String src = video.attr("src");
                if (src == null || src.isEmpty()) {
                    src = video.attr("data-src");
                }
                
                if (src != null && !src.isEmpty() && (src.contains(".mp4") || src.contains(".m3u8"))) {
                    if (src.startsWith("//")) src = "https:" + src;
                    else if (src.startsWith("/")) {
                        String baseUrl = embedUrl.substring(0, embedUrl.indexOf("/", 8));
                        src = baseUrl + src;
                    }
                    return new VideoResult(src, "720p");
                }
            }
        }
        
        return new VideoResult(null, "No video URL found in generic extraction", true);
    }
}