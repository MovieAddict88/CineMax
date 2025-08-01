package my.cinemax.app.free.Provider;

import android.os.AsyncTask;
import android.util.Log;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Resolves embedded URLs to actual video stream URLs
 */
public class EmbedUrlResolver {
    
    private static final String TAG = "EmbedUrlResolver";
    
    public interface ResolverCallback {
        void onResolved(String actualVideoUrl, String videoType);
        void onError(String error);
    }
    
    public static void resolveEmbedUrl(String embedUrl, ResolverCallback callback) {
        new ResolveUrlTask(callback).execute(embedUrl);
    }
    
    private static class ResolveUrlTask extends AsyncTask<String, Void, String[]> {
        private ResolverCallback callback;
        private String error;
        
        ResolveUrlTask(ResolverCallback callback) {
            this.callback = callback;
        }
        
        @Override
        protected String[] doInBackground(String... urls) {
            String embedUrl = urls[0];
            
            try {
                if (embedUrl.contains("vidsrc.net")) {
                    return resolveVidsrcUrl(embedUrl);
                } else if (embedUrl.contains("embed")) {
                    return resolveGenericEmbedUrl(embedUrl);
                }
                
                error = "Unsupported embed provider";
                return null;
                
            } catch (Exception e) {
                Log.e(TAG, "Error resolving embed URL: " + embedUrl, e);
                error = "Failed to resolve video URL: " + e.getMessage();
                return null;
            }
        }
        
        @Override
        protected void onPostExecute(String[] result) {
            if (result != null && result.length >= 2) {
                callback.onResolved(result[0], result[1]); // [actualUrl, videoType]
            } else {
                callback.onError(error != null ? error : "Failed to resolve video URL");
            }
        }
        
        private String[] resolveVidsrcUrl(String embedUrl) throws IOException {
            Log.d(TAG, "Resolving vidsrc URL: " + embedUrl);
            
            // Add user agent to avoid blocking
            Document doc = Jsoup.connect(embedUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            // Look for video sources in the page
            // Check for m3u8 playlist URLs
            String pageHtml = doc.html();
            
            // Pattern for m3u8 URLs
            Pattern m3u8Pattern = Pattern.compile("(https?://[^\\s\"']+\\.m3u8[^\\s\"']*)");
            Matcher m3u8Matcher = m3u8Pattern.matcher(pageHtml);
            
            if (m3u8Matcher.find()) {
                String m3u8Url = m3u8Matcher.group(1);
                Log.d(TAG, "Found m3u8 URL: " + m3u8Url);
                return new String[]{m3u8Url, "m3u8"};
            }
            
            // Pattern for mp4 URLs
            Pattern mp4Pattern = Pattern.compile("(https?://[^\\s\"']+\\.mp4[^\\s\"']*)");
            Matcher mp4Matcher = mp4Pattern.matcher(pageHtml);
            
            if (mp4Matcher.find()) {
                String mp4Url = mp4Matcher.group(1);
                Log.d(TAG, "Found mp4 URL: " + mp4Url);
                return new String[]{mp4Url, "mp4"};
            }
            
            // Look for iframe sources
            Element iframe = doc.select("iframe").first();
            if (iframe != null) {
                String iframeSrc = iframe.attr("src");
                if (!iframeSrc.isEmpty()) {
                    // Recursively resolve iframe URL
                    return resolveGenericEmbedUrl(iframeSrc);
                }
            }
            
            throw new IOException("No video source found in embed page");
        }
        
        private String[] resolveGenericEmbedUrl(String embedUrl) throws IOException {
            Log.d(TAG, "Resolving generic embed URL: " + embedUrl);
            
            Document doc = Jsoup.connect(embedUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(10000)
                    .get();
            
            String pageHtml = doc.html();
            
            // Look for various video source patterns
            String[] patterns = {
                "(https?://[^\\s\"']+\\.m3u8[^\\s\"']*)",  // m3u8
                "(https?://[^\\s\"']+\\.mp4[^\\s\"']*)",   // mp4
                "(https?://[^\\s\"']+\\.mpd[^\\s\"']*)"    // dash
            };
            
            String[] types = {"m3u8", "mp4", "dash"};
            
            for (int i = 0; i < patterns.length; i++) {
                Pattern pattern = Pattern.compile(patterns[i]);
                Matcher matcher = pattern.matcher(pageHtml);
                
                if (matcher.find()) {
                    String videoUrl = matcher.group(1);
                    Log.d(TAG, "Found " + types[i] + " URL: " + videoUrl);
                    return new String[]{videoUrl, types[i]};
                }
            }
            
            throw new IOException("No video source found in generic embed page");
        }
    }
}