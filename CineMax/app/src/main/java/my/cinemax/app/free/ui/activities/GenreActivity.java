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

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
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
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Poster;
import my.cinemax.app.free.ui.Adapters.PosterAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class GenreActivity extends AppCompatActivity {
    private SwipeRefreshLayout swipe_refresh_layout_list_genre_search;
    private Button button_try_again;
    private LinearLayout linear_layout_layout_error;
    private RecyclerView recycler_view_activity_genre;
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
    private LinearLayout linear_layout_load_genre_activity;


    private String SelectedOrder = "created";
    private Genre genre;
    private String from;

    private Integer lines_beetween_ads = 2 ;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;
    private int type_ads = 0;
    private PrefManager prefManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);
        prefManager= new PrefManager(getApplicationContext());

        getGenre();
        initView();
        initAction();
        loadPosters();
        showAdsBanner();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_order, menu);
        return true;
    }
    @Override
    public void onBackPressed(){
        if (from!=null){
            Intent intent =  new Intent(getApplicationContext(),HomeActivity.class);
            startActivity(intent);
        }else{
            super.onBackPressed();
        }
        return;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem itemMenu) {
        switch (itemMenu.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                if (from!=null){
                    Intent intent =  new Intent(getApplicationContext(),HomeActivity.class);
                    startActivity(intent);
                }else{
                    super.onBackPressed();
                }
                return true;
            case R.id.nav_created:
                SelectedOrder = "created";
                item = 0;
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
                return true;
            case R.id.nav_rating:
                SelectedOrder = "rating";
                item = 0;
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
                return true;
            case R.id.nav_views:
                SelectedOrder = "views";
                item = 0;
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
                return true;
            case R.id.nav_year:
                SelectedOrder = "year";
                item = 0;
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
                return true;
            case R.id.nav_title:
                SelectedOrder = "title";
                item = 0;
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
                return true;
            case R.id.nav_imdb:
                SelectedOrder = "imdb";
                item = 0;
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();

                return true;
        }
        return super.onOptionsItemSelected(itemMenu);
    }
    private void getGenre() {
        genre = getIntent().getParcelableExtra("genre");
        from = getIntent().getStringExtra("from");
        
        // Add null safety check for genre
        if (genre == null) {
            Log.e("GenreActivity", "Genre object is null, finishing activity");
            finish();
            return;
        }
        
        // Ensure genre has valid ID and title
        if (genre.getId() == null) {
            Log.e("GenreActivity", "Genre ID is null, setting default ID");
            genre.setId(0); // Set default ID
        }
        
        if (genre.getTitle() == null || genre.getTitle().isEmpty()) {
            Log.e("GenreActivity", "Genre title is null or empty, setting default title");
            genre.setTitle("Unknown Category"); // Set default title
        }
    }


    private void loadPosters() {
        // Safety check: ensure genre is not null
        if (genre == null) {
            Log.e("GenreActivity", "Cannot load posters: genre is null");
            linear_layout_layout_error.setVisibility(View.VISIBLE);
            recycler_view_activity_genre.setVisibility(View.GONE);
            image_view_empty_list.setVisibility(View.GONE);
            return;
        }
        
        if (page == 0) {
            linear_layout_load_genre_activity.setVisibility(View.VISIBLE);
        } else {
            relative_layout_load_more.setVisibility(View.VISIBLE);
        }
        swipe_refresh_layout_list_genre_search.setRefreshing(false);
        
        // Use GitHub JSON API instead of old API
        apiClient.getJsonApiData(new retrofit2.Callback<my.cinemax.app.free.entity.JsonApiResponse>() {
            @Override
            public void onResponse(Call<my.cinemax.app.free.entity.JsonApiResponse> call, retrofit2.Response<my.cinemax.app.free.entity.JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    my.cinemax.app.free.entity.JsonApiResponse apiResponse = response.body();
                    
                    if (apiResponse.getMovies() != null && apiResponse.getMovies().size() > 0) {
                        List<Poster> filteredPosters = new ArrayList<>();
                        
                        // Filter content by the selected genre
                        for (Poster poster : apiResponse.getMovies()) {
                            boolean matchesGenre = false;
                            
                            Log.d("GenreActivity", "Checking poster: " + (poster != null ? poster.getTitle() : "null"));
                            Log.d("GenreActivity", "Looking for genre ID: " + (genre != null ? genre.getId() : "null genre"));
                            
                            // Check if poster has the selected genre
                            if (poster != null && poster.getGenres() != null && !poster.getGenres().isEmpty()) {
                                Log.d("GenreActivity", "Poster has " + poster.getGenres().size() + " genres");
                                
                                for (my.cinemax.app.free.entity.Genre posterGenre : poster.getGenres()) {
                                    if (posterGenre != null && posterGenre.getId() != null) {
                                        Log.d("GenreActivity", "Poster genre ID: " + posterGenre.getId() + ", title: " + posterGenre.getTitle());
                                        
                                        if (genre != null && genre.getId() != null && 
                                            posterGenre.getId().equals(genre.getId())) {
                                            Log.d("GenreActivity", "MATCH FOUND! Poster '" + poster.getTitle() + "' matches genre '" + genre.getTitle() + "'");
                                            matchesGenre = true;
                                            break;
                                        }
                                    } else {
                                        Log.w("GenreActivity", "Poster genre is null or has null ID");
                                    }
                                }
                            } else {
                                Log.w("GenreActivity", "Poster is null or has no genres");
                            }
                            
                            // Special handling for special genre IDs
                            if (!matchesGenre && genre != null && genre.getId() != null) {
                                // Handle special cases where genre ID might be 0 or negative
                                if (genre.getId() == 0) {
                                    Log.d("GenreActivity", "Genre ID is 0, showing all content");
                                    // Show all content if genre ID is 0
                                    matchesGenre = true;
                                } else if (genre.getId() == -1) {
                                    Log.d("GenreActivity", "Genre ID is -1, showing top rated content");
                                    // Top rated content
                                    matchesGenre = true;
                                } else if (genre.getId() == -2) {
                                    Log.d("GenreActivity", "Genre ID is -2, showing my list content");
                                    // My list content
                                    matchesGenre = true;
                                }
                            }
                            
                            if (matchesGenre) {
                                Log.d("GenreActivity", "Adding poster to filtered list: " + poster.getTitle());
                                filteredPosters.add(poster);
                            } else {
                                Log.d("GenreActivity", "Poster '" + (poster != null ? poster.getTitle() : "null") + "' does not match genre");
                            }
                        }
                        
                        Log.d("GenreActivity", "Total filtered posters: " + filteredPosters.size());
                        
                        // Apply sorting
                        if (SelectedOrder != null) {
                            switch (SelectedOrder) {
                                case "created":
                                    // Keep original order (newest first)
                                    break;
                                case "rating":
                                    Collections.sort(filteredPosters, new Comparator<Poster>() {
                                        @Override
                                        public int compare(Poster p1, Poster p2) {
                                            Float rating1 = p1.getRating();
                                            Float rating2 = p2.getRating();
                                            if (rating1 == null) rating1 = 0f;
                                            if (rating2 == null) rating2 = 0f;
                                            return rating2.compareTo(rating1); // Descending
                                        }
                                    });
                                    break;
                                case "year":
                                    Collections.sort(filteredPosters, new Comparator<Poster>() {
                                        @Override
                                        public int compare(Poster p1, Poster p2) {
                                            String year1 = p1.getYear();
                                            String year2 = p2.getYear();
                                            if (year1 == null) year1 = "0";
                                            if (year2 == null) year2 = "0";
                                            return year2.compareTo(year1); // Descending
                                        }
                                    });
                                    break;
                                case "name":
                                    Collections.sort(filteredPosters, new Comparator<Poster>() {
                                        @Override
                                        public int compare(Poster p1, Poster p2) {
                                            String title1 = p1.getTitle();
                                            String title2 = p2.getTitle();
                                            if (title1 == null) title1 = "";
                                            if (title2 == null) title2 = "";
                                            return title1.compareToIgnoreCase(title2); // Ascending
                                        }
                                    });
                                    break;
                            }
                        }
                        
                        if (!filteredPosters.isEmpty()) {
                            for (Poster poster : filteredPosters) {
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
                            recycler_view_activity_genre.setVisibility(View.VISIBLE);
                            image_view_empty_list.setVisibility(View.GONE);
                            
                            adapter.notifyDataSetChanged();
                            page++;
                            loading = true;
                        } else {
                            if (page == 0) {
                                linear_layout_layout_error.setVisibility(View.GONE);
                                recycler_view_activity_genre.setVisibility(View.GONE);
                                image_view_empty_list.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if (page == 0) {
                            linear_layout_layout_error.setVisibility(View.GONE);
                            recycler_view_activity_genre.setVisibility(View.GONE);
                            image_view_empty_list.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    linear_layout_layout_error.setVisibility(View.VISIBLE);
                    recycler_view_activity_genre.setVisibility(View.GONE);
                    image_view_empty_list.setVisibility(View.GONE);
                }
                relative_layout_load_more.setVisibility(View.GONE);
                swipe_refresh_layout_list_genre_search.setRefreshing(false);
                linear_layout_load_genre_activity.setVisibility(View.GONE);
            }
            
            @Override
            public void onFailure(Call<my.cinemax.app.free.entity.JsonApiResponse> call, Throwable t) {
                Log.e("GenreActivity", "Failed to load data from GitHub JSON API", t);
                
                // Show error layout
                linear_layout_layout_error.setVisibility(View.VISIBLE);
                recycler_view_activity_genre.setVisibility(View.GONE);
                image_view_empty_list.setVisibility(View.GONE);
                relative_layout_load_more.setVisibility(View.GONE);
                swipe_refresh_layout_list_genre_search.setRefreshing(false);
                linear_layout_load_genre_activity.setVisibility(View.GONE);
                
                // Log the error details for debugging
                Log.e("GenreActivity", "Error details: " + t.getMessage());
            }
        });
    }

    private void initAction() {



        swipe_refresh_layout_list_genre_search.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
        recycler_view_activity_genre.addOnScrollListener(new RecyclerView.OnScrollListener()
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
        // Add null safety check for genre before setting title
        if (genre != null && genre.getTitle() != null) {
            toolbar.setTitle(genre.getTitle());
        } else {
            toolbar.setTitle("Category");
            Log.e("GenreActivity", "Genre or genre title is null, using default title");
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        this.linear_layout_load_genre_activity=findViewById(R.id.linear_layout_load_genre_activity);
        this.relative_layout_load_more=findViewById(R.id.relative_layout_load_more);
        this.swipe_refresh_layout_list_genre_search=findViewById(R.id.swipe_refresh_layout_list_genre_search);
        button_try_again            = findViewById(R.id.button_try_again);
        image_view_empty_list       = findViewById(R.id.image_view_empty_list);
        linear_layout_layout_error  = findViewById(R.id.linear_layout_layout_error);
        recycler_view_activity_genre          = findViewById(R.id.recycler_view_activity_genre);
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

        recycler_view_activity_genre.setHasFixedSize(true);
        recycler_view_activity_genre.setAdapter(adapter);
        recycler_view_activity_genre.setLayoutManager(gridLayoutManager);

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
