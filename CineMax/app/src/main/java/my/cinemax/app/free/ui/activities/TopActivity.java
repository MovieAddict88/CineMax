package my.cinemax.app.free.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

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
//import my.cinemax.app.free
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.ui.Adapters.PosterAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class TopActivity extends AppCompatActivity {

    private SwipeRefreshLayout swipe_refresh_layout_list_top_search;
    private Button button_try_again;
    private LinearLayout linear_layout_layout_error;
    private RecyclerView recycler_view_activity_top;
    private ImageView image_view_empty_list;
    private GridLayoutManager gridLayoutManager;
    private PosterAdapter adapter;

    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;

    private Integer page = 0;
    private Integer position = 0;
    private Integer item = 0 ;
    ArrayList<Poster> posterArrayList = new ArrayList<>();
    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_load_top_activity;

    private String order;

    private Integer lines_beetween_ads = 2 ;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;
    private int type_ads = 0;
    private PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top);
        prefManager= new PrefManager(getApplicationContext());

        getOrder();
        initView();
        initAction();
        loadPosters();
        showAdsBanner();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem itemMenu) {
        switch (itemMenu.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(itemMenu);
    }
    private void getOrder() {
        order = getIntent().getStringExtra("order");
    }

    private void loadPosters() {
        if (page == 0) {
            linear_layout_load_top_activity.setVisibility(View.VISIBLE);
        } else {
            relative_layout_load_more.setVisibility(View.VISIBLE);
        }
        swipe_refresh_layout_list_top_search.setRefreshing(false);
        
        // Use GitHub JSON API instead of old API
        apiClient.getJsonApiData(new retrofit2.Callback<my.cinemax.app.free.entity.JsonApiResponse>() {
            @Override
            public void onResponse(Call<my.cinemax.app.free.entity.JsonApiResponse> call, retrofit2.Response<my.cinemax.app.free.entity.JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    my.cinemax.app.free.entity.JsonApiResponse apiResponse = response.body();
                    
                    if (apiResponse.getMovies() != null && apiResponse.getMovies().size() > 0) {
                        List<Poster> allPosters = new ArrayList<>(apiResponse.getMovies());
                        
                        // Apply sorting based on order parameter
                        if (order != null) {
                            switch (order) {
                                case "rating":
                                    Collections.sort(allPosters, new Comparator<Poster>() {
                                        @Override
                                        public int compare(Poster p1, Poster p2) {
                                            Float rating1 = p1.getRating();
                                            Float rating2 = p2.getRating();
                                            if (rating1 == null) rating1 = 0f;
                                            if (rating2 == null) rating2 = 0f;
                                            return rating2.compareTo(rating1); // Descending (highest first)
                                        }
                                    });
                                    break;
                                case "views":
                                    Collections.sort(allPosters, new Comparator<Poster>() {
                                        @Override
                                        public int compare(Poster p1, Poster p2) {
                                            Integer views1 = p1.getViews();
                                            Integer views2 = p2.getViews();
                                            if (views1 == null) views1 = 0;
                                            if (views2 == null) views2 = 0;
                                            return views2.compareTo(views1); // Descending (most viewed first)
                                        }
                                    });
                                    break;
                                case "year":
                                    Collections.sort(allPosters, new Comparator<Poster>() {
                                        @Override
                                        public int compare(Poster p1, Poster p2) {
                                            String year1 = p1.getYear();
                                            String year2 = p2.getYear();
                                            if (year1 == null) year1 = "0";
                                            if (year2 == null) year2 = "0";
                                            return year2.compareTo(year1); // Descending (newest first)
                                        }
                                    });
                                    break;
                                case "created":
                                default:
                                    // Keep original order (newest first)
                                    break;
                            }
                        }
                        
                        if (!allPosters.isEmpty()) {
                            for (Poster poster : allPosters) {
                                posterArrayList.add(poster);
                                
                                if (native_ads_enabled) {
                                    item++;
                                    if (item == lines_beetween_ads) {
                                        item = 0;
                                        if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
                                            posterArrayList.add(new Poster().setTypeView(4));
                                        } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
                                            posterArrayList.add(new Poster().setTypeView(5));
                                        } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")) {
                                            if (type_ads == 0) {
                                                posterArrayList.add(new Poster().setTypeView(4));
                                                type_ads = 1;
                                            } else if (type_ads == 1) {
                                                posterArrayList.add(new Poster().setTypeView(5));
                                                type_ads = 0;
                                            }
                                        }
                                    }
                                }
                            }
                            linear_layout_layout_error.setVisibility(View.GONE);
                            recycler_view_activity_top.setVisibility(View.VISIBLE);
                            image_view_empty_list.setVisibility(View.GONE);
                            
                            adapter.notifyDataSetChanged();
                            page++;
                            loading = true;
                        } else {
                            if (page == 0) {
                                linear_layout_layout_error.setVisibility(View.GONE);
                                recycler_view_activity_top.setVisibility(View.GONE);
                                image_view_empty_list.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if (page == 0) {
                            linear_layout_layout_error.setVisibility(View.GONE);
                            recycler_view_activity_top.setVisibility(View.GONE);
                            image_view_empty_list.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    linear_layout_layout_error.setVisibility(View.VISIBLE);
                    recycler_view_activity_top.setVisibility(View.GONE);
                    image_view_empty_list.setVisibility(View.GONE);
                }
                relative_layout_load_more.setVisibility(View.GONE);
                swipe_refresh_layout_list_top_search.setRefreshing(false);
                linear_layout_load_top_activity.setVisibility(View.GONE);
            }
            
            @Override
            public void onFailure(Call<my.cinemax.app.free.entity.JsonApiResponse> call, Throwable t) {
                linear_layout_layout_error.setVisibility(View.VISIBLE);
                recycler_view_activity_top.setVisibility(View.GONE);
                image_view_empty_list.setVisibility(View.GONE);
                relative_layout_load_more.setVisibility(View.GONE);
                swipe_refresh_layout_list_top_search.setRefreshing(false);
                linear_layout_load_top_activity.setVisibility(View.GONE);
            }
        });
    }

    private void initAction() {



        swipe_refresh_layout_list_top_search.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                item = 0;
                page = 0;
                loading = true;
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
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
            }
        });
        recycler_view_activity_top.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {

                    visibleItemCount    = gridLayoutManager.getChildCount();
                    totalItemCount      = gridLayoutManager.getItemCount();
                    pastVisiblesItems   = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            loadPosters();
                        }
                    }
                }else{

                }
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
        String title = "Top Rated";
        if (order.equals("rating"))
            title = "Top Rated";
        else
            title = "Popular";

        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.linear_layout_load_top_activity=findViewById(R.id.linear_layout_load_top_activity);
        this.relative_layout_load_more=findViewById(R.id.relative_layout_load_more);
        this.swipe_refresh_layout_list_top_search=findViewById(R.id.swipe_refresh_layout_list_top_search);
        button_try_again            = findViewById(R.id.button_try_again);
        image_view_empty_list       = findViewById(R.id.image_view_empty_list);
        linear_layout_layout_error  = findViewById(R.id.linear_layout_layout_error);
        recycler_view_activity_top          = findViewById(R.id.recycler_view_activity_top);
        adapter = new PosterAdapter(posterArrayList, this);

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

        recycler_view_activity_top.setHasFixedSize(true);
        recycler_view_activity_top.setAdapter(adapter);
        recycler_view_activity_top.setLayoutManager(gridLayoutManager);

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
