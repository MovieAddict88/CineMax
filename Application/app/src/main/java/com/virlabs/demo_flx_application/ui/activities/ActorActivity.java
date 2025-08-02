package com.virlabs.demo_flx_application.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.applovin.mediation.MaxAd;
import com.applovin.mediation.MaxAdFormat;
import com.applovin.mediation.MaxAdViewAdListener;
import com.applovin.mediation.MaxError;
import com.applovin.mediation.ads.MaxAdView;
import com.applovin.sdk.AppLovinSdkUtils;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.ironsource.mediationsdk.ISBannerSize;
import com.ironsource.mediationsdk.IronSource;
import com.ironsource.mediationsdk.IronSourceBannerLayout;
import com.ironsource.mediationsdk.logger.IronSourceError;
import com.ironsource.mediationsdk.sdk.BannerListener;
import com.virlabs.demo_flx_application.Provider.PrefManager;
import com.virlabs.demo_flx_application.R;
import com.virlabs.demo_flx_application.api.apiClient;
import com.virlabs.demo_flx_application.api.apiRest;
import com.virlabs.demo_flx_application.entity.Actor;
import com.virlabs.demo_flx_application.entity.Poster;
import com.virlabs.demo_flx_application.ui.Adapters.PosterAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ActorActivity extends AppCompatActivity {

    private LinearLayout linear_layout_activity_actor_movies;
    private TextView text_view_activity_actor_height;
    private TextView text_view_activity_actor_born;
    private TextView text_view_activity_actor_full_name;
    private TextView text_view_activity_actor_bio;
    private ImageView image_view_activity_actor_image;
    private ImageView image_view_activity_actor_background;
    private Actor actor;
    private TextView text_view_activity_actor_type;
    private String backable;
    private PosterAdapter posterAdapter;
    private LinearLayoutManager linearLayoutManagerMoreMovies;
    private RecyclerView recycle_view_activity_activity_actor_movies;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actor);
        initView();
        initAction();
        getMovie();
        setActor();

        getRandomMovies();
        showAdsBanner();

    }

    private void setActor() {

        Picasso.get().load(actor.getImage()).into(image_view_activity_actor_image);
        final com.squareup.picasso.Target target = new com.squareup.picasso.Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
              //  BlurImage.get().load(bitmap).intensity(25).Async(true).into(image_view_activity_actor_background);
                Picasso.get().load(actor.getImage())
                        .transform(new BlurTransformation(getApplicationContext(),25))
                        .into(image_view_activity_actor_background);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {

            }


            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) { }
        };
        Picasso.get().load(actor.getImage()).into(target);
        image_view_activity_actor_background.setTag(target);

        ViewCompat.setTransitionName(image_view_activity_actor_image, "imageMain");

        text_view_activity_actor_full_name.setText(actor.getName());
        text_view_activity_actor_bio.setText(actor.getBio());
        text_view_activity_actor_height.setText(actor.getHeight());
        text_view_activity_actor_born.setText(actor.getBorn());
        text_view_activity_actor_type.setText("("+actor.getType()+")");
    }
    private void getMovie() {
        actor = getIntent().getParcelableExtra("actor");
        backable = getIntent().getStringExtra("backable");
    }
    private void initAction() {

    }

    private void initView() {

        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        this.image_view_activity_actor_background= findViewById(R.id.image_view_activity_actor_background);
        this.image_view_activity_actor_image= findViewById(R.id.image_view_activity_actor_image);
        this.text_view_activity_actor_type= findViewById(R.id.text_view_activity_actor_type);
        this.text_view_activity_actor_bio= findViewById(R.id.text_view_activity_actor_bio);
        this.text_view_activity_actor_born= findViewById(R.id.text_view_activity_actor_born);
        this.text_view_activity_actor_full_name= findViewById(R.id.text_view_activity_actor_full_name);
        this.text_view_activity_actor_height= findViewById(R.id.text_view_activity_actor_height);
        this.linear_layout_activity_actor_movies= findViewById(R.id.linear_layout_activity_actor_movies);
        this.recycle_view_activity_activity_actor_movies= findViewById(R.id.recycle_view_activity_activity_actor_movies);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        return;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getRandomMovies() {

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);

        Call<List<Poster>> call = service.getPosterByActor(actor.getId());
        call.enqueue(new Callback<List<Poster>>() {
            @Override
            public void onResponse(Call<List<Poster>> call, Response<List<Poster>> response) {
                if (response.isSuccessful()){
                    if (response.body().size()>0) {
                        linearLayoutManagerMoreMovies = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
                        posterAdapter  = new PosterAdapter(response.body(), ActorActivity.this);
                        recycle_view_activity_activity_actor_movies.setHasFixedSize(true);
                        recycle_view_activity_activity_actor_movies.setAdapter(posterAdapter);
                        recycle_view_activity_activity_actor_movies.setLayoutManager(linearLayoutManagerMoreMovies);
                        linear_layout_activity_actor_movies.setVisibility(View.VISIBLE);
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Poster>> call, Throwable t) {
            }
        });
    }
    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE") && !prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE")) {
            return false;
        }
        return true;
    }
    public void showAdsBanner() {
        if (!checkSUBSCRIBED()) {
            PrefManager prefManager= new PrefManager(getApplicationContext());
            if (prefManager.getString("ADMIN_BANNER_TYPE").equals("ADMOB")){
                showAdmobBanner();
            }
            if (prefManager.getString("ADMIN_BANNER_TYPE").equals("MAX")){
                showAppLovinBanner();
            }
            if (prefManager.getString("ADMIN_BANNER_TYPE").equals("IS")) {
                showISBanner();
            }
        }

    }
    @Override
    protected void onResume() {
        super.onResume();
        IronSource.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        IronSource.onPause(this);
    }
    public void showISBanner(){

        String userId = IronSource.getAdvertiserId(this);
        PrefManager prefManager= new PrefManager(getApplicationContext());
        IronSource.setUserId( userId);

        IronSource.init(this, prefManager.getString("ADMIN_BANNER_ADMOB_ID"), IronSource.AD_UNIT.BANNER);
        LinearLayout linear_layout_ads =  (LinearLayout) findViewById(R.id.linear_layout_ads);
        IronSourceBannerLayout banner = IronSource.createBanner(this, ISBannerSize.BANNER);
        linear_layout_ads.addView(banner);
        banner.setBannerListener(new BannerListener() {
            @Override
            public void onBannerAdLoaded() {
                Log.v("IROUNSOURCE","loaded");
                banner.setVisibility(View.VISIBLE);

            }
            @Override
            public void onBannerAdLoadFailed(IronSourceError error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        linear_layout_ads.removeAllViews();
                    }
                });
                Log.v("IROUNSOURCE",error.getErrorMessage());

            }
            @Override
            public void onBannerAdClicked() {
                Log.v("IROUNSOURCE","onBannerAdClicked");

            }
            @Override
            public void onBannerAdScreenPresented() {
                Log.v("IROUNSOURCE","onBannerAdScreenPresented");

            }
            @Override
            public void onBannerAdScreenDismissed() {
                Log.v("IROUNSOURCE","onBannerAdScreenDismissed");

            }
            @Override
            public void onBannerAdLeftApplication() {
                Log.v("IROUNSOURCE","onBannerAdLeftApplication");

            }
        });
        IronSource.loadBanner(banner);

    }
    public void showAppLovinBanner(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        MaxAdView adView = new MaxAdView( prefManager.getString("ADMIN_BANNER_ADMOB_ID"), this );
        adView.setListener(new MaxAdViewAdListener() {
            @Override
            public void onAdExpanded(MaxAd ad) {

            }

            @Override
            public void onAdCollapsed(MaxAd ad) {

            }

            @Override
            public void onAdLoaded(MaxAd ad) {
                adView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAdDisplayed(MaxAd ad) {

            }

            @Override
            public void onAdHidden(MaxAd ad) {

            }

            @Override
            public void onAdClicked(MaxAd ad) {

            }

            @Override
            public void onAdLoadFailed(String adUnitId, MaxError error) {

            }

            @Override
            public void onAdDisplayFailed(MaxAd ad, MaxError error) {

            }
        });

        adView.setVisibility(View.GONE);
        int width = ViewGroup.LayoutParams.MATCH_PARENT;

        int heightDp = MaxAdFormat.BANNER.getAdaptiveSize( this ).getHeight();
        int heightPx = AppLovinSdkUtils.dpToPx( this, heightDp );

        adView.setLayoutParams( new FrameLayout.LayoutParams( width, heightPx ) );






        LinearLayout linear_layout_ads =  (LinearLayout) findViewById(R.id.linear_layout_ads);


        linear_layout_ads.addView(adView);



        // Load the ad
        adView.loadAd();
    }
    public void showAdmobBanner(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        LinearLayout linear_layout_ads =  (LinearLayout) findViewById(R.id.linear_layout_ads);
        final AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(prefManager.getString("ADMIN_BANNER_ADMOB_ID"));
        AdRequest adRequest = new AdRequest.Builder()
                .build();
        mAdView.loadAd(adRequest);
        linear_layout_ads.addView(mAdView);

        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                mAdView.setVisibility(View.VISIBLE);
            }
        });
    }
}
