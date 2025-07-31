package my.cinemax.app.free;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

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
import my.cinemax.app.free.database.DataManager;
import my.cinemax.app.free.database.DatabaseTestHelper;

/**
 * Created by Tamim on 28/09/2019.

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
        
        // Initialize database and data manager
        initializeDatabase();
    }

    private void initLogger() {
        if (BuildConfig.DEBUG) {

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
    
    /**
     * Initialize database and data manager for the application
     */
    private void initializeDatabase() {
        try {
            // Initialize DataManager which will create the database
            DataManager dataManager = DataManager.getInstance(this);
            
            // Clean old data on app start (optional)
            dataManager.cleanOldData();
            
            // Test database functionality (only in debug mode)
            if (BuildConfig.DEBUG) {
                DatabaseTestHelper.testDatabaseConnection(this);
            }
            
            android.util.Log.d("MyApplication", "Database initialized successfully");
        } catch (Exception e) {
            android.util.Log.e("MyApplication", "Error initializing database: " + e.getMessage(), e);
        }
    }

}
