package my.cinemax.app.free;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import my.cinemax.app.free.Utils.EnhancedCacheManager;

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
     * Initialize the enhanced multi-layer caching system for large datasets
     */
    private void initCacheSystem() {
        try {
            // Initialize Enhanced Cache Manager (new multi-layer system)
            EnhancedCacheManager.getInstance().initialize(this);
            
            // Initialize legacy CacheManager for backward compatibility
            CacheManager.getInstance().initialize(this);
            
            // Initialize DataRepository
            DataRepository.getInstance().initialize(this);
            
            Log.d("MyApplication", "Enhanced multi-layer caching system initialized successfully");
            
            // Preload essential data in background
            DataRepository.getInstance().preloadEssentialData();
            
        } catch (Exception e) {
            Log.e("MyApplication", "Error initializing enhanced cache system", e);
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
