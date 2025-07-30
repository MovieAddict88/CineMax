package my.cinemax.app.free.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.ui.Adapters.PosterAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyListActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipe_refresh_layout_list_my_list_search;
    private Button button_try_again;
    private LinearLayout linear_layout_layout_error;
    private RecyclerView recycler_view_activity_my_list;
    private ImageView image_view_empty_list;
    private GridLayoutManager gridLayoutManager;
    private PosterAdapter adapter;

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private Integer page = 0;
    private Integer position = 0;
    private Integer item = 0 ;
    ArrayList<Poster> posterArrayList = new ArrayList<>();
    ArrayList<Channel> channelArrayList = new ArrayList<>();

    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_load_my_list_activity;

    private Integer lines_beetween_ads = 2 ;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;
    private int type_ads = 0;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        prefManager= new PrefManager(getApplicationContext());

        initView();
        initAction();
        loadPosters();
        showAdsBanner();
    }




    private void loadPosters() {
        // Use local storage for My List instead of old API
        swipe_refresh_layout_list_my_list_search.setRefreshing(true);
        linear_layout_load_my_list_activity.setVisibility(View.VISIBLE);
        
        // Load user's saved list from local storage
        PrefManager prf = new PrefManager(MyListActivity.this.getApplicationContext());
        
        // Get saved movie/series IDs from SharedPreferences
        String savedMoviesIds = prf.getString("MY_LIST_MOVIES");
        String savedChannelsIds = prf.getString("MY_LIST_CHANNELS");
        
        // Load data from GitHub JSON API
        apiClient.getJsonApiData(new retrofit2.Callback<my.cinemax.app.free.entity.JsonApiResponse>() {
            @Override
            public void onResponse(Call<my.cinemax.app.free.entity.JsonApiResponse> call, retrofit2.Response<my.cinemax.app.free.entity.JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    my.cinemax.app.free.entity.JsonApiResponse apiResponse = response.body();
                    
                    // Parse saved IDs
                    List<Integer> savedMovieIds = new ArrayList<>();
                    List<Integer> savedChannelIds = new ArrayList<>();
                    
                    if (!savedMoviesIds.isEmpty()) {
                        String[] movieIds = savedMoviesIds.split(",");
                        for (String id : movieIds) {
                            try {
                                savedMovieIds.add(Integer.parseInt(id.trim()));
                            } catch (NumberFormatException e) {
                                // Ignore invalid IDs
                            }
                        }
                    }
                    
                    if (!savedChannelsIds.isEmpty()) {
                        String[] channelIds = savedChannelsIds.split(",");
                        for (String id : channelIds) {
                            try {
                                savedChannelIds.add(Integer.parseInt(id.trim()));
                            } catch (NumberFormatException e) {
                                // Ignore invalid IDs
                            }
                        }
                    }
                    
                    // Find matching channels
                    if (apiResponse.getChannels() != null && !savedChannelIds.isEmpty()) {
                        for (Channel channel : apiResponse.getChannels()) {
                            if (channel.getId() != null && savedChannelIds.contains(channel.getId())) {
                                channelArrayList.add(channel);
                            }
                        }
                    }
                    
                    // Find matching movies/series
                    if (apiResponse.getMovies() != null && !savedMovieIds.isEmpty()) {
                        for (Poster poster : apiResponse.getMovies()) {
                            if (poster.getId() != null && savedMovieIds.contains(poster.getId())) {
                                posterArrayList.add(poster.setTypeView(1));
                            }
                        }
                    }
                    
                    // Set up layout based on content
                    if (channelArrayList.size() > 0) {
                        posterArrayList.add(0, new Poster().setTypeView(3)); // Add channels header
                        
                        if (native_ads_enabled) {
                            if (tabletSize) {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        return ((position) % (lines_beetween_ads + 1) == 0 || position == 0) ? 6 : 1;
                                    }
                                });
                            } else {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3, RecyclerView.VERTICAL, false);
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        return (position == 0) ? 6 : 1;
                                    }
                                });
                            }
                        }
                    } else {
                        if (native_ads_enabled) {
                            if (tabletSize) {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        return ((position + 1) % (lines_beetween_ads + 1) == 0 && position != 0) ? 6 : 1;
                                    }
                                });
                            } else {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3, RecyclerView.VERTICAL, false);
                                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                                    @Override
                                    public int getSpanSize(int position) {
                                        return ((position + 1) % (lines_beetween_ads + 1) == 0 && position != 0) ? 3 : 1;
                                    }
                                });
                            }
                        } else {
                            if (tabletSize) {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
                            } else {
                                gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3, RecyclerView.VERTICAL, false);
                            }
                        }
                    }
                    
                    // Add ads if enabled
                    if (native_ads_enabled && posterArrayList.size() > 1) {
                        for (int i = 1; i < posterArrayList.size(); i++) {
                            if (posterArrayList.get(i).getTypeView() == 1) { // Only for content items, not headers
                                item++;
                                if (item == lines_beetween_ads) {
                                    item = 0;
                                    if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
                                        posterArrayList.add(i + 1, new Poster().setTypeView(4));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
                                        posterArrayList.add(i + 1, new Poster().setTypeView(5));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")) {
                                        if (type_ads == 0) {
                                            posterArrayList.add(i + 1, new Poster().setTypeView(4));
                                            type_ads = 1;
                                        } else if (type_ads == 1) {
                                            posterArrayList.add(i + 1, new Poster().setTypeView(5));
                                            type_ads = 0;
                                        }
                                    }
                                    i++; // Skip the ad we just added
                                }
                            }
                        }
                    }
                    
                    // Show results or empty state
                    if (posterArrayList.size() > 0 && (posterArrayList.size() > 1 || posterArrayList.get(0).getTypeView() != 3)) {
                        linear_layout_layout_error.setVisibility(View.GONE);
                        recycler_view_activity_my_list.setVisibility(View.VISIBLE);
                        image_view_empty_list.setVisibility(View.GONE);
                        
                        recycler_view_activity_my_list.setHasFixedSize(true);
                        recycler_view_activity_my_list.setLayoutManager(gridLayoutManager);
                        adapter = new PosterAdapter(posterArrayList, channelArrayList, MyListActivity.this, true);
                        recycler_view_activity_my_list.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                    } else {
                        linear_layout_layout_error.setVisibility(View.GONE);
                        recycler_view_activity_my_list.setVisibility(View.GONE);
                        image_view_empty_list.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Show empty state on API failure
                    linear_layout_layout_error.setVisibility(View.GONE);
                    recycler_view_activity_my_list.setVisibility(View.GONE);
                    image_view_empty_list.setVisibility(View.VISIBLE);
                }
                
                swipe_refresh_layout_list_my_list_search.setRefreshing(false);
                linear_layout_load_my_list_activity.setVisibility(View.GONE);
            }
            
            @Override
            public void onFailure(Call<my.cinemax.app.free.entity.JsonApiResponse> call, Throwable t) {
                // Show empty state on network failure
                linear_layout_layout_error.setVisibility(View.GONE);
                recycler_view_activity_my_list.setVisibility(View.GONE);
                image_view_empty_list.setVisibility(View.VISIBLE);
                
                swipe_refresh_layout_list_my_list_search.setRefreshing(false);
                linear_layout_load_my_list_activity.setVisibility(View.GONE);
            }
        });
    }


    private void initAction() {



        swipe_refresh_layout_list_my_list_search.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                item = 0;
                page = 0;
                loading = true;
                channelArrayList.clear();
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
            }
        });
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = 0;
                page = 0;
                loading = true;
                channelArrayList.clear();
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
            }
        });
    }

    private void initView() {

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!prefManager.getString("ADMIN_NATIVE_TYPE").equals("FALSE")){
            native_ads_enabled=true;
            if (tabletSize) {
                lines_beetween_ads=6*Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            }else{
                lines_beetween_ads=3*Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            }
        }
        if (checkSUBSCRIBED()) {
            native_ads_enabled=false;
        }

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("My list");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.linear_layout_load_my_list_activity=findViewById(R.id.linear_layout_load_my_list_activity);
        this.relative_layout_load_more=findViewById(R.id.relative_layout_load_more);
        this.swipe_refresh_layout_list_my_list_search=findViewById(R.id.swipe_refresh_layout_list_my_list_search);
        button_try_again            = findViewById(R.id.button_try_again);
        image_view_empty_list       = findViewById(R.id.image_view_empty_list);
        linear_layout_layout_error  = findViewById(R.id.linear_layout_layout_error);
        recycler_view_activity_my_list          = findViewById(R.id.recycler_view_activity_my_list);
        adapter = new PosterAdapter(posterArrayList,channelArrayList, this,true);

        if (native_ads_enabled){
            Log.v("MYADS","ENABLED");
            if (tabletSize) {
                this.gridLayoutManager=  new GridLayoutManager(getApplicationContext(),6,RecyclerView.VERTICAL,false);
                Log.v("MYADS","tabletSize");
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ((position  + 1) % (lines_beetween_ads  + 1  ) == 0 && position!=0) ? 6 : 1;
                    }
                });
            } else {
                this.gridLayoutManager=  new GridLayoutManager(getApplicationContext(),3,RecyclerView.VERTICAL,false);
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ((position  + 1) % (lines_beetween_ads + 1 ) == 0  && position!=0)  ? 3 : 1;
                    }
                });
            }
        }else {
            if (tabletSize) {
                this.gridLayoutManager=  new GridLayoutManager(getApplicationContext(),6,RecyclerView.VERTICAL,false);
            } else {
                this.gridLayoutManager=  new GridLayoutManager(getApplicationContext(),3,RecyclerView.VERTICAL,false);
            }
        }
        recycler_view_activity_my_list.setHasFixedSize(true);
        recycler_view_activity_my_list.setAdapter(adapter);
        recycler_view_activity_my_list.setLayoutManager(gridLayoutManager);

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem itemMenu) {
        switch (itemMenu.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
            }
            return true;
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
            if (!prefManager.getString("ADMIN_BANNER_TYPE").equals("FALSE")){
                showAdmobBanner();
            }
        }
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
