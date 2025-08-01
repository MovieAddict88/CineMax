package my.cinemax.app.free.Provider;

import android.util.Log;
import fi.iki.elonen.NanoHTTPD;

/**
 * HTML embed fallback player using NanoHTTPD
 * Creates a local web server that serves HTML pages with embedded video players
 */
public class EmbedWebServer extends NanoHTTPD {
    private static final String TAG = "EmbedWebServer";
    private final String embedUrl;
    private final String videoTitle;

    public EmbedWebServer(int port, String embedUrl, String videoTitle) {
        super(port);
        this.embedUrl = embedUrl;
        this.videoTitle = videoTitle != null ? videoTitle : "Video Player";
        Log.d(TAG, "Created EmbedWebServer on port " + port);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Log.d(TAG, "Serving request for: " + uri);

        if (uri.equals("/") || uri.equals("/player.html")) {
            String htmlContent = createPlayerHtml();
            return newFixedLengthResponse(Response.Status.OK, "text/html", htmlContent);
        }
        
        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found");
    }

    private String createPlayerHtml() {
        return "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>" + escapeHtml(videoTitle) + "</title>\n" +
                "    <style>\n" +
                "        body { margin: 0; padding: 0; background: #000; }\n" +
                "        iframe { width: 100vw; height: 100vh; border: none; }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <iframe src=\"" + escapeHtml(embedUrl) + "\" allowfullscreen></iframe>\n" +
                "</body>\n" +
                "</html>";
    }

    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    public String getServerUrl() {
        return "http://127.0.0.1:" + getListeningPort() + "/";
    }
}