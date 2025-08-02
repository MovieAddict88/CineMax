package my.cinemax.app.free.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.Session;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import my.cinemax.app.free.R;
import my.cinemax.app.free.cast.ExpandedControlsActivity;
import my.cinemax.app.free.event.CastSessionEndedEvent;
import my.cinemax.app.free.event.CastSessionStartedEvent;
import my.cinemax.app.free.ui.player.CustomPlayerFragment;

import org.greenrobot.eventbus.EventBus;


/**
 * Created by Thomas Ostrowski
 * on 02/11/2016.
 */

public class PlayerActivity extends AppCompatActivity {


    private CastContext mCastContext;
    private SessionManager mSessionManager;
    private CastSession mCastSession;
    private final SessionManagerListener mSessionManagerListener =
            new SessionManagerListenerImpl();
    private ScaleGestureDetector mScaleGestureDetector;
    private CustomPlayerFragment customPlayerFragment;
    private String videoUrl;
    private Boolean isLive = false;
    private String videoType;
    private String videoTitle;
    private String videoImage;
    private String videoSubTile;
    private int vodeoId ;
    private String videoKind;

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        mScaleGestureDetector.onTouchEvent(motionEvent);
        return true;
    }
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScale(ScaleGestureDetector scaleGestureDetector){
            if (scaleGestureDetector.getScaleFactor()>1){
                CustomPlayerFragment myFragment = (CustomPlayerFragment)getSupportFragmentManager().findFragmentByTag("CustomPlayerFragment");
                myFragment.setFull();
            }
            if (scaleGestureDetector.getScaleFactor()<1){
                CustomPlayerFragment myFragment = (CustomPlayerFragment)getSupportFragmentManager().findFragmentByTag("CustomPlayerFragment");
                myFragment.setNormal();

            }
            return true;
        }
    }
    private class SessionManagerListenerImpl implements SessionManagerListener {
        @Override
        public void onSessionStarting(Session session) {
            Log.d("MYAPP","onSessionStarting");
        }

        @Override
        public void onSessionStarted(Session session, String s) {
            Log.d("MYAPP","onSessionStarted");
            invalidateOptionsMenu();
            EventBus.getDefault().post(new CastSessionStartedEvent());
            startActivity(new Intent(PlayerActivity.this,ExpandedControlsActivity.class));
            finish();
        }

        @Override
        public void onSessionStartFailed(Session session, int i) {
            Log.d("MYAPP","onSessionStartFailed");
        }

        @Override
        public void onSessionEnding(Session session) {
            Log.d("MYAPP","onSessionEnding");
            EventBus.getDefault().post(new CastSessionEndedEvent(session.getSessionRemainingTimeMs()));
        }

        @Override
        public void onSessionEnded(Session session, int i) {
            Log.d("MYAPP","onSessionEnded");
        }

        @Override
        public void onSessionResuming(Session session, String s) {
            Log.d("MYAPP","onSessionResuming");
        }

        @Override
        public void onSessionResumed(Session session, boolean b) {
            Log.d("MYAPP","onSessionResumed");
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumeFailed(Session session, int i) {
            Log.d("MYAPP","onSessionResumeFailed");
        }

        @Override
        public void onSessionSuspended(Session session, int i) {
            Log.d("MYAPP","onSessionSuspended");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        
        try {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleListener());
            
            // Initialize Cast Context safely
            try {
                mCastContext = CastContext.getSharedInstance(this);
                mSessionManager = mCastContext.getSessionManager();
            } catch (Exception e) {
                Log.w("PlayerActivity", "Cast initialization failed: " + e.getMessage());
                // Continue without cast support
            }
            
            Bundle bundle = getIntent().getExtras();
            if (bundle == null) {
                Log.e("PlayerActivity", "No bundle data received");
                android.widget.Toast.makeText(this, "No video data provided", android.widget.Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            vodeoId = bundle.getInt("id", 0);
            videoUrl = bundle.getString("url");
            videoKind = bundle.getString("kind");
            isLive = bundle.getBoolean("isLive", false);
            videoType = bundle.getString("type");
            videoTitle = bundle.getString("title");
            videoSubTile = bundle.getString("subtitle");
            videoImage = bundle.getString("image");
            
            // Validate required data
            if (videoUrl == null || videoUrl.trim().isEmpty()) {
                Log.e("PlayerActivity", "Video URL is null or empty");
                android.widget.Toast.makeText(this, "Video URL not available", android.widget.Toast.LENGTH_LONG).show();
                finish();
                return;
            }
            
            // Validate video type
            if (videoType == null || videoType.trim().isEmpty()) {
                Log.w("PlayerActivity", "Video type is null, defaulting to mp4");
                videoType = "mp4";
            }
            
            // Clean up the video URL
            videoUrl = videoUrl.trim();
            Log.d("PlayerActivity", "Initializing player with URL: " + videoUrl + " Type: " + videoType);
            
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

            if (savedInstanceState == null) {
                try {
                    customPlayerFragment = CustomPlayerFragment.newInstance(
                        getVideoUrl(), isLive, videoType, videoTitle, videoSubTile, videoImage, vodeoId, videoKind);
                    launchFragment(customPlayerFragment);
                } catch (Exception e) {
                    Log.e("PlayerActivity", "Error creating player fragment: " + e.getMessage(), e);
                    android.widget.Toast.makeText(this, "Error initializing video player", android.widget.Toast.LENGTH_LONG).show();
                    finish();
                    return;
                }
            }
        } catch (Exception e) {
            Log.e("PlayerActivity", "Critical error in onCreate: " + e.getMessage(), e);
            android.widget.Toast.makeText(this, "Error starting video player", android.widget.Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        
        try {
            if (mSessionManager != null) {
                mCastSession = mSessionManager.getCurrentCastSession();
                mSessionManager.addSessionManagerListener(mSessionManagerListener);
            }
        } catch (Exception e) {
            Log.w("PlayerActivity", "Error handling cast session in onResume: " + e.getMessage());
        }

        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }


    @Override
    protected void onPause() {
        try {
            if (mSessionManager != null) {
                mSessionManager.removeSessionManagerListener(mSessionManagerListener);
            }
        } catch (Exception e) {
            Log.w("PlayerActivity", "Error handling cast session in onPause: " + e.getMessage());
        }
        mCastSession = null;
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_cast, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(),
                                                menu,
                                                R.id.media_route_menu_item);

        return true;
    }

    private void launchFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.main_fragment_container, fragment, "CustomPlayerFragment");
        fragmentTransaction.commit();


    }

    private String getVideoUrl() {
        return videoUrl;
    }

    public CastSession getCastSession() {
        return mCastSession;
    }

    public SessionManager getSessionManager() {
        return mSessionManager;
    }
}
