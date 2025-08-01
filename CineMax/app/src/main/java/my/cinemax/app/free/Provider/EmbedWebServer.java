package my.cinemax.app.free.Provider;

import android.util.Log;
import fi.iki.elonen.NanoHTTPD;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

/**
 * Enhanced web server for serving HTML embed fallback players
 * Uses NanoHTTPD to create a local web server that can serve HTML pages
 * containing embed videos when direct URL extraction fails
 */
public class EmbedWebServer extends NanoHTTPD {
    private static final String TAG = "EmbedWebServer";
    private final String embedUrl;
    private final String videoTitle;

    public EmbedWebServer(int port, String embedUrl, String videoTitle) {
        super(port);
        this.embedUrl = embedUrl;
        this.videoTitle = videoTitle != null ? videoTitle : "Video Player";
        Log.d(TAG, "Created EmbedWebServer on port " + port + " for URL: " + embedUrl);
    }

    @Override
    public Response serve(IHTTPSession session) {
        String uri = session.getUri();
        Log.d(TAG, "Serving request for URI: " + uri);

        try {
            if (uri.equals("/") || uri.equals("/player.html")) {
                // Serve the main HTML player page
                String htmlContent = createEmbedPlayerHtml();
                return newFixedLengthResponse(Response.Status.OK, "text/html", htmlContent);
            } else if (uri.equals("/embed")) {
                // Redirect to the actual embed URL (for iframe proxy)
                return newFixedLengthResponse(Response.Status.REDIRECT, "text/plain", "");
            } else {
                return newFixedLengthResponse(Response.Status.NOT_FOUND, "text/plain", "404 Not Found");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error serving request", e);
            return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, "text/plain", "Internal Server Error");
        }
    }

    /**
     * Creates an HTML page with the embedded video player
     */
    private String createEmbedPlayerHtml() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>" + escapeHtml(videoTitle) + "</title>\n" +
                "    <style>\n" +
                "        * {\n" +
                "            margin: 0;\n" +
                "            padding: 0;\n" +
                "            box-sizing: border-box;\n" +
                "        }\n" +
                "        body {\n" +
                "            background: #000;\n" +
                "            color: #fff;\n" +
                "            font-family: Arial, sans-serif;\n" +
                "            overflow: hidden;\n" +
                "        }\n" +
                "        .container {\n" +
                "            width: 100vw;\n" +
                "            height: 100vh;\n" +
                "            display: flex;\n" +
                "            flex-direction: column;\n" +
                "        }\n" +
                "        .header {\n" +
                "            padding: 10px;\n" +
                "            background: rgba(0, 0, 0, 0.8);\n" +
                "            text-align: center;\n" +
                "            font-size: 14px;\n" +
                "            z-index: 1000;\n" +
                "        }\n" +
                "        .player-container {\n" +
                "            flex: 1;\n" +
                "            position: relative;\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "        }\n" +
                "        iframe {\n" +
                "            width: 100%;\n" +
                "            height: 100%;\n" +
                "            border: none;\n" +
                "            background: #000;\n" +
                "        }\n" +
                "        .loading {\n" +
                "            position: absolute;\n" +
                "            top: 50%;\n" +
                "            left: 50%;\n" +
                "            transform: translate(-50%, -50%);\n" +
                "            color: #fff;\n" +
                "            font-size: 16px;\n" +
                "            z-index: 999;\n" +
                "        }\n" +
                "        .error {\n" +
                "            position: absolute;\n" +
                "            top: 50%;\n" +
                "            left: 50%;\n" +
                "            transform: translate(-50%, -50%);\n" +
                "            color: #ff6b6b;\n" +
                "            text-align: center;\n" +
                "            padding: 20px;\n" +
                "            background: rgba(0, 0, 0, 0.8);\n" +
                "            border-radius: 8px;\n" +
                "            display: none;\n" +
                "        }\n" +
                "        .retry-btn {\n" +
                "            margin-top: 10px;\n" +
                "            padding: 8px 16px;\n" +
                "            background: #007bff;\n" +
                "            color: white;\n" +
                "            border: none;\n" +
                "            border-radius: 4px;\n" +
                "            cursor: pointer;\n" +
                "        }\n" +
                "        .retry-btn:hover {\n" +
                "            background: #0056b3;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <div class=\"container\">\n" +
                "        <div class=\"header\">\n" +
                "            <div>" + escapeHtml(videoTitle) + "</div>\n" +
                "        </div>\n" +
                "        <div class=\"player-container\">\n" +
                "            <div class=\"loading\" id=\"loading\">Loading video...</div>\n" +
                "            <div class=\"error\" id=\"error\">\n" +
                "                <div>Failed to load video</div>\n" +
                "                <button class=\"retry-btn\" onclick=\"retryLoad()\">Retry</button>\n" +
                "            </div>\n" +
                "            <iframe id=\"videoFrame\" src=\"" + escapeHtml(embedUrl) + "\" allowfullscreen></iframe>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "\n" +
                "    <script>\n" +
                "        let loadTimeout;\n" +
                "        let hasLoaded = false;\n" +
                "\n" +
                "        function showError() {\n" +
                "            document.getElementById('loading').style.display = 'none';\n" +
                "            document.getElementById('error').style.display = 'block';\n" +
                "        }\n" +
                "\n" +
                "        function hideError() {\n" +
                "            document.getElementById('error').style.display = 'none';\n" +
                "            document.getElementById('loading').style.display = 'block';\n" +
                "        }\n" +
                "\n" +
                "        function retryLoad() {\n" +
                "            hideError();\n" +
                "            const iframe = document.getElementById('videoFrame');\n" +
                "            iframe.src = iframe.src; // Reload iframe\n" +
                "            startLoadTimeout();\n" +
                "        }\n" +
                "\n" +
                "        function startLoadTimeout() {\n" +
                "            clearTimeout(loadTimeout);\n" +
                "            loadTimeout = setTimeout(() => {\n" +
                "                if (!hasLoaded) {\n" +
                "                    console.log('Video load timeout');\n" +
                "                    showError();\n" +
                "                }\n" +
                "            }, 15000); // 15 second timeout\n" +
                "        }\n" +
                "\n" +
                "        // Listen for iframe load events\n" +
                "        document.getElementById('videoFrame').addEventListener('load', function() {\n" +
                "            console.log('Iframe loaded');\n" +
                "            hasLoaded = true;\n" +
                "            clearTimeout(loadTimeout);\n" +
                "            document.getElementById('loading').style.display = 'none';\n" +
                "        });\n" +
                "\n" +
                "        document.getElementById('videoFrame').addEventListener('error', function() {\n" +
                "            console.log('Iframe error');\n" +
                "            showError();\n" +
                "        });\n" +
                "\n" +
                "        // Start load timeout\n" +
                "        startLoadTimeout();\n" +
                "\n" +
                "        // Handle orientation changes\n" +
                "        window.addEventListener('orientationchange', function() {\n" +
                "            setTimeout(() => {\n" +
                "                const iframe = document.getElementById('videoFrame');\n" +
                "                iframe.style.height = window.innerHeight + 'px';\n" +
                "            }, 100);\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "</html>";
    }

    /**
     * Escape HTML special characters to prevent XSS
     */
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;");
    }

    /**
     * Get the local URL for this server
     */
    public String getServerUrl() {
        return "http://127.0.0.1:" + getListeningPort() + "/";
    }
}