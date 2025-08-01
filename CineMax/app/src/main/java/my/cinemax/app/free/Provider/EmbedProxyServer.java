package my.cinemax.app.free.Provider;

import android.util.Log;
import fi.iki.elonen.NanoHTTPD;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Enhanced NanoHTTPD server for proxying embedded video content
 * This server can fetch embedded video streams and serve them locally
 */
public class EmbedProxyServer extends NanoHTTPD {
    
    private static final String TAG = "EmbedProxyServer";
    private static EmbedProxyServer instance;
    private Map<String, String> urlMappings = new HashMap<>();
    
    public EmbedProxyServer(int port) {
        super(port);
    }
    
    public static synchronized EmbedProxyServer getInstance() {
        if (instance == null) {
            instance = new EmbedProxyServer(8888);
        }
        return instance;
    }
    
    /**
     * Register an embedded URL to be proxied
     * @param embedUrl The original embedded URL
     * @return Local proxy URL that can be played by ExoPlayer
     */
    public String registerEmbedUrl(String embedUrl) {
        String localPath = "/embed/" + System.currentTimeMillis();
        urlMappings.put(localPath, embedUrl);
        return "http://localhost:8888" + localPath;
    }
    
    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Method method = session.getMethod();
        
        Log.d(TAG, "Serving request: " + method + " " + uri);
        
        if (uri.startsWith("/embed/")) {
            String embedUrl = urlMappings.get(uri);
            if (embedUrl != null) {
                return proxyEmbedContent(embedUrl, session);
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Embed URL not found");
            }
        }
        
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not found");
    }
    
    private Response proxyEmbedContent(String embedUrl, IHTTPSession session) {
        try {
            Log.d(TAG, "Proxying embed URL: " + embedUrl);
            
            // First, try to resolve the embed URL to a direct video stream
            EmbedUrlResolver.resolveEmbedUrl(embedUrl, new EmbedUrlResolver.ResolverCallback() {
                @Override
                public void onResolved(String actualVideoUrl, String videoType) {
                    // Cache the resolved URL for future requests
                    urlMappings.put(session.getUri() + "_resolved", actualVideoUrl);
                }
                
                @Override
                public void onError(String error) {
                    Log.e(TAG, "Failed to resolve embed URL: " + error);
                }
            });
            
            // Check if we have a resolved URL
            String resolvedUrl = urlMappings.get(session.getUri() + "_resolved");
            if (resolvedUrl != null) {
                return proxyDirectUrl(resolvedUrl, session);
            }
            
            // Fallback: serve the embed page content
            return serveEmbedPage(embedUrl, session);
            
        } catch (Exception e) {
            Log.e(TAG, "Error proxying embed content", e);
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, 
                "Error proxying content: " + e.getMessage());
        }
    }
    
    private Response proxyDirectUrl(String videoUrl, IHTTPSession session) {
        try {
            URL url = new URL(videoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", 
                "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36");
            
            // Handle range requests for video streaming
            Map<String, String> headers = session.getHeaders();
            String range = headers.get("range");
            if (range != null) {
                connection.setRequestProperty("Range", range);
            }
            
            connection.connect();
            
            int responseCode = connection.getResponseCode();
            String contentType = connection.getContentType();
            if (contentType == null) {
                contentType = "video/mp4"; // Default for video content
            }
            
            InputStream inputStream = connection.getInputStream();
            
            Response response = newChunkedResponse(
                responseCode == 206 ? Response.Status.PARTIAL_CONTENT : Response.Status.OK,
                contentType, 
                inputStream
            );
            
            // Copy relevant headers
            String contentLength = connection.getHeaderField("Content-Length");
            if (contentLength != null) {
                response.addHeader("Content-Length", contentLength);
            }
            
            String contentRange = connection.getHeaderField("Content-Range");
            if (contentRange != null) {
                response.addHeader("Content-Range", contentRange);
            }
            
            response.addHeader("Accept-Ranges", "bytes");
            
            return response;
            
        } catch (IOException e) {
            Log.e(TAG, "Error proxying direct URL: " + videoUrl, e);
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, 
                "Error accessing video stream: " + e.getMessage());
        }
    }
    
    private Response serveEmbedPage(String embedUrl, IHTTPSession session) {
        try {
            URL url = new URL(embedUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", 
                "Mozilla/5.0 (Linux; Android 10; SM-G975F) AppleWebKit/537.36");
            
            connection.connect();
            InputStream inputStream = connection.getInputStream();
            
            return newChunkedResponse(Response.Status.OK, "text/html", inputStream);
            
        } catch (IOException e) {
            Log.e(TAG, "Error serving embed page: " + embedUrl, e);
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT, 
                "Error loading embed page: " + e.getMessage());
        }
    }
    
    public void startProxy() {
        try {
            start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
            Log.d(TAG, "Embed proxy server started on port 8888");
        } catch (IOException e) {
            Log.e(TAG, "Failed to start embed proxy server", e);
        }
    }
    
    public void stopProxy() {
        stop();
        Log.d(TAG, "Embed proxy server stopped");
    }
}