package my.cinemax.app.free.ui.activities;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import my.cinemax.app.free.R;
import es.dmoral.toasty.Toasty;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmbedActivity extends AppCompatActivity {
    private static final String TAG = "EmbedActivity";
    private WebView webView;
    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private myWebChromeClient mWebChromeClient;
    private myWebViewClient mWebViewClient;
    private String url;
    private ProgressBar progressBar;
    private TextView errorText;
    private Handler retryHandler;
    private int retryCount = 0;
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY = 3000; // 3 seconds
    
    // Backup servers for fallback
    private List<String> backupServers = new ArrayList<>();
    private int currentServerIndex = 0;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_embed);

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString("url");
        }
        
        if (url == null || url.isEmpty()) {
            Toasty.error(this, "Invalid video URL", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initializeViews();
        setupBackupServers();
        setupWebView();
        loadUrl(url);
    }
    
    private void initializeViews() {
        customViewContainer = findViewById(R.id.customViewContainer);
        webView = findViewById(R.id.webView);
        
        // Add progress bar and error text if not in layout
        progressBar = new ProgressBar(this);
        errorText = new TextView(this);
        
        retryHandler = new Handler();
    }
    
    private void setupBackupServers() {
        // Generate backup servers based on the original URL
        backupServers.add(url); // Original server first
        
        if (url.contains("vidjoy")) {
            // VidJoy backup servers with proper URL handling
            String baseUrl = url.replace("vidjoy.pro", "vidjoy.me")
                               .replace("vidjoy.me", "vidjoy.me"); // Keep if already vidjoy.me
            backupServers.add(baseUrl);
            
            // Additional VidJoy backups
            backupServers.add(url.replace("vidjoy.pro", "embedsito.com/v")
                                .replace("vidjoy.me", "embedsito.com/v"));
            
            // Convert to VidSrc as fallback
            String vidsrcUrl = url.replace("vidjoy.pro/embed", "vidsrc.net/embed")
                                 .replace("vidjoy.me/embed", "vidsrc.net/embed")
                                 .replace("embedsito.com/v", "vidsrc.net/embed");
            backupServers.add(vidsrcUrl);
            
            // Add more VidSrc variations
            backupServers.add(vidsrcUrl.replace("vidsrc.net", "vidsrc.me"));
            backupServers.add(vidsrcUrl.replace("vidsrc.net", "2embed.to"));
            
        } else if (url.contains("vidsrc")) {
            // VidSrc backup servers
            backupServers.add(url.replace("vidsrc.net", "vidsrc.me"));
            backupServers.add(url.replace("vidsrc.net", "2embed.to"));
            backupServers.add(url.replace("vidsrc.net", "www.2embed.to"));
            
            // Convert to VidJoy as backup
            String vidjoyUrl = url.replace("vidsrc.net/embed", "vidjoy.pro/embed")
                                 .replace("vidsrc.me/embed", "vidjoy.pro/embed")
                                 .replace("2embed.to/embed", "vidjoy.pro/embed");
            backupServers.add(vidjoyUrl);
        }
        
        // Remove duplicates while preserving order
        List<String> uniqueServers = new ArrayList<>();
        for (String server : backupServers) {
            if (!uniqueServers.contains(server)) {
                uniqueServers.add(server);
            }
        }
        backupServers = uniqueServers;
        
        Log.d(TAG, "Setup backup servers: " + backupServers.toString());
    }
    
    private void setupWebView() {
        mWebViewClient = new myWebViewClient();
        webView.setWebViewClient(mWebViewClient);

        mWebChromeClient = new myWebChromeClient();
        webView.setWebChromeClient(mWebChromeClient);
        
        // Enhanced WebView settings for better compatibility
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSaveFormData(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        
        // Enable video playback
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.getSettings().setSupportMultipleWindows(true);
        
        // VidJoy and streaming server specific settings
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);
        webView.getSettings().setAllowFileAccessFromFileURLs(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setBlockNetworkImage(false);
        webView.getSettings().setBlockNetworkLoads(false);
        
        // Set a custom user agent for better server compatibility
        String userAgent = webView.getSettings().getUserAgentString();
        webView.getSettings().setUserAgentString(userAgent + " CineMaxPlayer/2.0");
        
        // Enable mixed content for HTTPS/HTTP compatibility
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            webView.getSettings().setMixedContentMode(android.webkit.WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
    }
    
    private void loadUrl(String urlToLoad) {
        Log.d(TAG, "Loading URL: " + urlToLoad);
        retryCount = 0;
        webView.loadUrl(urlToLoad);
    }
    
    private void retryWithNextServer() {
        if (currentServerIndex < backupServers.size() - 1) {
            currentServerIndex++;
            String nextServer = backupServers.get(currentServerIndex);
            Log.d(TAG, "Retrying with backup server: " + nextServer);
            Toasty.info(this, "Trying backup server...", Toast.LENGTH_SHORT).show();
            
            retryHandler.postDelayed(() -> {
                webView.loadUrl(nextServer);
                retryCount = 0;
            }, RETRY_DELAY);
        } else {
            // All servers failed
            Log.e(TAG, "All backup servers failed");
            Toasty.error(this, "All video servers are unavailable. Please try again later.", Toast.LENGTH_LONG).show();
            
            // Show error message to user
            runOnUiThread(() -> {
                webView.setVisibility(View.GONE);
                // Could add an error layout here
            });
        }
    }
    
    private void handleLoadError() {
        retryCount++;
        Log.w(TAG, "Load error, retry count: " + retryCount);
        
        if (retryCount < MAX_RETRIES) {
            // Retry current server
            retryHandler.postDelayed(() -> {
                String currentUrl = backupServers.get(currentServerIndex);
                Log.d(TAG, "Retrying current server: " + currentUrl);
                webView.loadUrl(currentUrl);
            }, RETRY_DELAY);
        } else {
            // Try next backup server
            retryWithNextServer();
        }
    }

    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (inCustomView()) {
            hideCustomView();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (retryHandler != null) {
            retryHandler.removeCallbacksAndMessages(null);
        }
        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (inCustomView()) {
                hideCustomView();
                return true;
            }

            if ((mCustomView == null) && webView.canGoBack()) {
                webView.goBack();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    class myWebChromeClient extends WebChromeClient {
        private Bitmap mDefaultVideoPoster;
        private View mVideoProgressView;

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            // Update progress bar if available
            if (progressBar != null) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    progressBar.setProgress(newProgress);
                }
            }
        }

        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            onShowCustomView(view, callback);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // if a view already exists then immediately terminate the new one
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            webView.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;
        }

        @Override
        public View getVideoLoadingProgressView() {
            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(EmbedActivity.this);
                mVideoProgressView = inflater.inflate(R.layout.video_progress, null);
            }
            return mVideoProgressView;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mCustomView == null)
                return;

            webView.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);

            // Hide the custom view.
            mCustomView.setVisibility(View.GONE);

            // Remove the custom view from its container.
            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();

            mCustomView = null;
        }
    }

    class myWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
        
        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
            Log.e(TAG, "WebView error: " + error.getDescription());
            
            // Only handle main frame errors
            if (request.isForMainFrame()) {
                handleLoadError();
            }
        }
        
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
            Log.d(TAG, "Page started loading: " + url);
        }
        
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.d(TAG, "Page finished loading: " + url);
            
            // Special handling for VidJoy servers
            if (url.contains("vidjoy")) {
                // Inject JavaScript to detect and handle VidJoy player issues
                String vidjoyScript = 
                    "(function() {" +
                    "  var checkPlayer = function() {" +
                    "    var video = document.querySelector('video');" +
                    "    var iframe = document.querySelector('iframe');" +
                    "    if (video) {" +
                    "      console.log('Video element found');" +
                    "      if (video.readyState >= 2) return 'video_ready';" +
                    "      video.addEventListener('loadeddata', function() { console.log('Video data loaded'); });" +
                    "      video.addEventListener('error', function() { console.log('Video error'); });" +
                    "    }" +
                    "    if (iframe) {" +
                    "      console.log('Iframe found');" +
                    "      return 'iframe_found';" +
                    "    }" +
                    "    return 'no_player';" +
                    "  };" +
                    "  setTimeout(checkPlayer, 2000);" +
                    "  return checkPlayer();" +
                    "})();";
                
                view.evaluateJavascript(vidjoyScript, result -> {
                    Log.d(TAG, "VidJoy script result: " + result);
                    if ("\"no_player\"".equals(result)) {
                        // No player found, try backup server
                        retryHandler.postDelayed(() -> handleLoadError(), 5000);
                    }
                });
            }
            
            // General check for page content
            view.evaluateJavascript(
                "(function() { " +
                "  var body = document.body;" +
                "  if (!body || body.innerHTML.length < 100) return 'empty';" +
                "  var error = document.querySelector('.error, #error, [class*=\"error\"]');" +
                "  if (error && error.offsetWidth > 0) return 'error_found';" +
                "  return 'ok';" +
                "})();",
                value -> {
                    Log.d(TAG, "Page content check: " + value);
                    if ("\"empty\"".equals(value) || "\"error_found\"".equals(value)) {
                        Log.w(TAG, "Page appears to have issues, treating as error");
                        retryHandler.postDelayed(() -> handleLoadError(), 3000);
                    }
                }
            );
        }
    }
}
