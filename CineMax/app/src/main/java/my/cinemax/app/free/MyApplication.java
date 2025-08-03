package my.cinemax.app.free;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.applovin.sdk.AppLovinSdk;
import com.facebook.FacebookSdk;
import com.facebook.ads.AdSettings;
import com.facebook.appevents.AppEventsLogger;
import com.google.ads.consent.ConsentInformation;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.util.Util;
import com.google.android.gms.ads.MobileAds;
import com.orhanobut.hawk.Hawk;
import com.unity3d.ads.UnityAds;

import my.cinemax.app.free.BuildConfig;
import my.cinemax.app.free.R;
import my.cinemax.app.free.Provider.DataRepository;
import my.cinemax.app.free.Utils.CacheManager;
import my.cinemax.app.free.Utils.UnifiedCacheManager;

/**
 * Created by Tamim on 28/09/2019.
 * Updated to include advanced caching system for large datasets
 */

public class MyApplication extends MultiDexApplication {
    private static MyApplication instance;

    protected String mUserAgent;

    @Override
    public void onCreate() {
        MultiDex.install(this);
        Hawk.init(this).build();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        MobileAds.initialize(this, initializationStatus -> {});
        super.onCreate();
        instance = this;
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
        initLogger();
        AppLovinSdk.initializeSdk(instance);
        UnityAds.initialize (this, getResources().getString(R.string.unity_ads_app_id));
//        initCast();
        mUserAgent = Util.getUserAgent(this, "MyApplication");
        
        // Initialize advanced caching system for large datasets
        initCacheSystem();
    }

    private void initLogger() {
        if (BuildConfig.DEBUG) {

        }
    }
    
    /**
     * Initialize the advanced caching system for large datasets
     */
    private void initCacheSystem() {
        // Check if cache system should be enabled (can be disabled for debugging)
        if (BuildConfig.DEBUG && false) { // Set to true to disable cache system
            Log.d("MyApplication", "Cache system disabled for debugging");
            return;
        }
        
        // Initialize cache system in background to prevent blocking main thread
        new Thread(() -> {
            try {
                Log.d("MyApplication", "Starting cache system initialization...");
                
                // Initialize Unified Cache Manager (coordinates all layers)
                UnifiedCacheManager.getInstance().initialize(this);
                
                // Initialize DataRepository (uses unified cache)
                DataRepository.getInstance().initialize(this);
                
                Log.d("MyApplication", "Advanced multi-layer caching system initialized successfully");
                
                // Preload essential data in background (with delay to let app stabilize)
                new Handler().postDelayed(() -> {
                    try {
                        DataRepository.getInstance().preloadEssentialData();
                        logCacheStatistics();
                    } catch (Exception e) {
                        Log.e("MyApplication", "Error in background preload", e);
                    }
                }, 3000); // 3 second delay
                
            } catch (Exception e) {
                Log.e("MyApplication", "Error initializing cache system", e);
                // Don't crash the app, just log the error
            }
        }).start();
    }
    
    /**
     * Log cache statistics for monitoring
     */
    private void logCacheStatistics() {
        try {
            UnifiedCacheManager.UnifiedCacheStats stats = 
                UnifiedCacheManager.getInstance().getCacheStats();
            Log.d("MyApplication", "Cache Statistics: " + stats.toString());
        } catch (Exception e) {
            Log.e("MyApplication", "Error getting cache statistics", e);
        }
    }
    public static MyApplication getInstance ()
    {
        return instance;
    }
//    private void initCast() {
//         Cast Coach staging : CC1AD845
//        CastConfiguration options = new CastConfiguration.Builder("CC1AD845")
//                .enableAutoReconnect()
//                .enableCaptionManagement()
//                .enableDebug()
//                .enableLockScreen()
//                .enableWifiReconnection()
//                .enableNotification()
//                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_PLAY_PAUSE, true)
//                .addNotificationAction(CastConfiguration.NOTIFICATION_ACTION_DISCONNECT, true)
//                .build();
//
//        VideoCastManager.initialize(this, options);
//    }

    public DataSource.Factory buildDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultDataSourceFactory(this, bandwidthMeter,
                buildHttpDataSourceFactory(bandwidthMeter));
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory(DefaultBandwidthMeter bandwidthMeter) {
        return new DefaultHttpDataSourceFactory(mUserAgent, bandwidthMeter);
    }
    @Override
    protected void attachBaseContext(Context context) {
        super.attachBaseContext(context);
        MultiDex.install(this);
    }
    public boolean checkIfHasNetwork()
    {
        ConnectivityManager cm = (ConnectivityManager) getSystemService( Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }
    public static boolean hasNetwork ()
    {
        return instance.checkIfHasNetwork();
    }

}
