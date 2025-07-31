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
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.ui.Adapters.PosterAdapter;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private String query;
    private SwipeRefreshLayout swipe_refresh_layout_list_search_search;
    private Button button_try_again;
    private LinearLayout linear_layout_layout_error;
    private RecyclerView recycler_view_activity_search;
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
    private LinearLayout linear_layout_load_search_activity;

    private Integer lines_beetween_ads = 2 ;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;
    private int type_ads = 0;
    private PrefManager prefManager;

    // JSON API data cache
    private JsonApiResponse cachedJsonResponse = null;
    private List<Poster> allMovies = new ArrayList<>();
    private List<Channel> allChannels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        prefManager= new PrefManager(getApplicationContext());

        initView();
        initAction();
        loadSearchResultsFromJson();
        showAdsBanner();
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

        Bundle bundle = getIntent().getExtras() ;
        this.query =  bundle.getString("query");
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(query);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.linear_layout_load_search_activity=findViewById(R.id.linear_layout_load_search_activity);
        this.swipe_refresh_layout_list_search_search=findViewById(R.id.swipe_refresh_layout_list_search_search);
        button_try_again            = findViewById(R.id.button_try_again);
        image_view_empty_list       = findViewById(R.id.image_view_empty_list);
        linear_layout_layout_error  = findViewById(R.id.linear_layout_layout_error);
        recycler_view_activity_search          = findViewById(R.id.recycler_view_activity_search);
        adapter = new PosterAdapter(posterArrayList, this);
        recycler_view_activity_search.setHasFixedSize(true);
        recycler_view_activity_search.setAdapter(adapter);

    }

    /**
     * Load search results from GitHub JSON API instead of old server API
     */
    private void loadSearchResultsFromJson() {
        swipe_refresh_layout_list_search_search.setRefreshing(false);
        
        // If we already have cached data, use it
        if (cachedJsonResponse != null && !allMovies.isEmpty()) {
            filterAndDisplaySearchResults();
            return;
        }
        
        // Load data from GitHub JSON API
        apiClient.getJsonApiData(new apiClient.JsonApiCallback() {
            @Override
            public void onSuccess(JsonApiResponse jsonResponse) {
                if (jsonResponse != null) {
                    cachedJsonResponse = jsonResponse;
                    allMovies = jsonResponse.getMovies() != null ? jsonResponse.getMovies() : new ArrayList<>();
                    allChannels = jsonResponse.getChannels() != null ? jsonResponse.getChannels() : new ArrayList<>();
                    filterAndDisplaySearchResults();
                } else {
                    showError();
                }
            }
            
            @Override
            public void onError(String error) {
                Log.e("SearchActivity", "Error loading JSON data: " + error);
                showError();
            }
        });
    }
    
    /**
     * Filter movies and channels by search query and display them
     */
    private void filterAndDisplaySearchResults() {
        posterArrayList.clear();
        channelArrayList.clear();
        
        // Filter channels by search query
        for (Channel channel : allChannels) {
            if (channel.getTitle() != null && 
                channel.getTitle().toLowerCase().contains(query.toLowerCase())) {
                channelArrayList.add(channel);
            }
        }
        
        // Filter movies by search query
        for (Poster movie : allMovies) {
            if (movie.getTitle() != null && 
                movie.getTitle().toLowerCase().contains(query.toLowerCase())) {
                posterArrayList.add(movie.setTypeView(1));
            }
        }
        
        // Add channel section header if channels found
        if (channelArrayList.size() > 0) {
            posterArrayList.add(0, new Poster().setTypeView(3));
            
            // Add native ads for channels section
            if (native_ads_enabled) {
                setupGridLayoutForChannels();
            } else {
                setupGridLayoutForChannels();
            }
        } else {
            // Setup grid layout for movies only
            if (native_ads_enabled) {
                setupGridLayoutForMovies();
            } else {
                setupGridLayoutForMovies();
            }
        }
        
        // Add native ads to movies
        if (native_ads_enabled) {
            addNativeAdsToMovies();
        }
        
        // Display results
        if (channelArrayList.size() == 0 && posterArrayList.size() == 0) {
            linear_layout_layout_error.setVisibility(View.GONE);
            recycler_view_activity_search.setVisibility(View.GONE);
            image_view_empty_list.setVisibility(View.VISIBLE);
        } else {
            linear_layout_layout_error.setVisibility(View.GONE);
            recycler_view_activity_search.setVisibility(View.VISIBLE);
            image_view_empty_list.setVisibility(View.GONE);
        }
        
        swipe_refresh_layout_list_search_search.setRefreshing(false);
        linear_layout_load_search_activity.setVisibility(View.GONE);
        recycler_view_activity_search.setLayoutManager(gridLayoutManager);
        adapter.notifyDataSetChanged();
    }
    
    /**
     * Setup grid layout for channels section
     */
    private void setupGridLayoutForChannels() {
        if (tabletSize) {
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
            if (native_ads_enabled) {
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ((position) % (lines_beetween_ads + 1) == 0 || position == 0) ? 6 : 1;
                    }
                });
            } else {
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (position == 0) ? 6 : 1;
                    }
                });
            }
        } else {
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3, RecyclerView.VERTICAL, false);
            if (native_ads_enabled) {
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ((position) % (lines_beetween_ads + 1) == 0 || position == 0) ? 3 : 1;
                    }
                });
            } else {
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return (position == 0) ? 3 : 1;
                    }
                });
            }
        }
    }
    
    /**
     * Setup grid layout for movies only
     */
    private void setupGridLayoutForMovies() {
        if (tabletSize) {
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 6, RecyclerView.VERTICAL, false);
            if (native_ads_enabled) {
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ((position + 1) % (lines_beetween_ads + 1) == 0 && position != 0) ? 6 : 1;
                    }
                });
            }
        } else {
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3, RecyclerView.VERTICAL, false);
            if (native_ads_enabled) {
                gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return ((position + 1) % (lines_beetween_ads + 1) == 0 && position != 0) ? 3 : 1;
                    }
                });
            }
        }
    }
    
    /**
     * Add native ads to movies list
     */
    private void addNativeAdsToMovies() {
        item = 0;
        for (int i = 0; i < posterArrayList.size(); i++) {
            if (posterArrayList.get(i).getTypeView() == 1) { // Only for movies, not channels
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
                }
            }
        }
    }
    
    /**
     * Show error state
     */
    private void showError() {
        linear_layout_layout_error.setVisibility(View.VISIBLE);
        recycler_view_activity_search.setVisibility(View.GONE);
        image_view_empty_list.setVisibility(View.GONE);
        swipe_refresh_layout_list_search_search.setVisibility(View.GONE);
        linear_layout_load_search_activity.setVisibility(View.GONE);
    }

    /**
     * @deprecated Old API method - replaced with loadSearchResultsFromJson()
     */
    @Deprecated
    private void loadPosters() {
        // This method is kept for backward compatibility but now redirects to JSON API
        loadSearchResultsFromJson();
    }

    private void initAction() {
        swipe_refresh_layout_list_search_search.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                posterArrayList.clear();
                channelArrayList.clear();
                adapter.notifyDataSetChanged();
                loadSearchResultsFromJson();
            }
        });
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                posterArrayList.clear();
                channelArrayList.clear();
                adapter.notifyDataSetChanged();
                loadSearchResultsFromJson();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem itemMenu) {
        switch (itemMenu.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(itemMenu);
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
