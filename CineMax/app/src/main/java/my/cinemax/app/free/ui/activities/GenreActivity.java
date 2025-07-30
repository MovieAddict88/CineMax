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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.ui.Adapters.PosterAdapter;

import java.util.ArrayList;
import java.util.List;

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
                SelectedOrder = "name";
                item = 0;
                page = 0;
                loading = true;
                posterArrayList.clear();
                adapter.notifyDataSetChanged();
                loadPosters();
                return true;
            default:
                return super.onOptionsItemSelected(itemMenu);
        }
    }

    private void getGenre() {
        genre = getIntent().getParcelableExtra("genre");
        from = getIntent().getStringExtra("from");
    }

    /**
     * Check if network is available
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadPosters() {
        // Check network connectivity first
        if (!isNetworkAvailable()) {
            linear_layout_layout_error.setVisibility(View.VISIBLE);
            recycler_view_activity_genre.setVisibility(View.GONE);
            image_view_empty_list.setVisibility(View.GONE);
            relative_layout_load_more.setVisibility(View.GONE);
            swipe_refresh_layout_list_genre_search.setRefreshing(false);
            linear_layout_load_genre_activity.setVisibility(View.GONE);
            Toast.makeText(this, "Check your internet connection", Toast.LENGTH_SHORT).show();
            return;
        }

        if (page==0){
            linear_layout_load_genre_activity.setVisibility(View.VISIBLE);
        }else{
            relative_layout_load_more.setVisibility(View.VISIBLE);
        }
        swipe_refresh_layout_list_genre_search.setRefreshing(false);
        
        // Use the new JSON API system instead of old API endpoints
        apiClient.getJsonApiData(new apiClient.JsonApiCallback() {
            @Override
            public void onSuccess(JsonApiResponse jsonResponse) {
                if (jsonResponse != null && jsonResponse.getMovies() != null) {
                    List<Poster> allMovies = jsonResponse.getMovies();
                    List<Poster> filteredMovies = new ArrayList<>();
                    
                    // Filter movies by genre
                    for (Poster movie : allMovies) {
                        if (genre.getId() == -1) {
                            // Top rated movies
                            if (movie.getRating() != null && movie.getRating() >= 4.0) {
                                filteredMovies.add(movie);
                            }
                        } else if (genre.getId() == 0) {
                            // Most viewed movies
                            if (movie.getViews() != null && movie.getViews() > 1000) {
                                filteredMovies.add(movie);
                            }
                        } else if (genre.getId() == -2) {
                            // My list - this should be handled differently
                            // For now, show all movies
                            filteredMovies.add(movie);
                        } else {
                            // Filter by specific genre
                            if (movie.getGenres() != null) {
                                for (Genre movieGenre : movie.getGenres()) {
                                    if (movieGenre.getId().equals(genre.getId())) {
                                        filteredMovies.add(movie);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                    
                    // Apply sorting based on SelectedOrder
                    if (SelectedOrder.equals("rating")) {
                        filteredMovies.sort((a, b) -> {
                            Float ratingA = a.getRating() != null ? a.getRating() : 0f;
                            Float ratingB = b.getRating() != null ? b.getRating() : 0f;
                            return ratingB.compareTo(ratingA);
                        });
                    } else if (SelectedOrder.equals("views")) {
                        filteredMovies.sort((a, b) -> {
                            Integer viewsA = a.getViews() != null ? a.getViews() : 0;
                            Integer viewsB = b.getViews() != null ? b.getViews() : 0;
                            return viewsB.compareTo(viewsA);
                        });
                    } else if (SelectedOrder.equals("year")) {
                        filteredMovies.sort((a, b) -> {
                            Integer yearA = a.getYearAsInteger() != null ? a.getYearAsInteger() : 0;
                            Integer yearB = b.getYearAsInteger() != null ? b.getYearAsInteger() : 0;
                            return yearB.compareTo(yearA);
                        });
                    } else if (SelectedOrder.equals("name")) {
                        filteredMovies.sort((a, b) -> {
                            String titleA = a.getTitle() != null ? a.getTitle() : "";
                            String titleB = b.getTitle() != null ? b.getTitle() : "";
                            return titleA.compareToIgnoreCase(titleB);
                        });
                    }
                    
                    // Add movies to the list with pagination
                    int startIndex = page * 20; // 20 items per page
                    int endIndex = Math.min(startIndex + 20, filteredMovies.size());
                    
                    if (startIndex < filteredMovies.size()) {
                        for (int i = startIndex; i < endIndex; i++) {
                            posterArrayList.add(filteredMovies.get(i));
                            if (native_ads_enabled){
                                item++;
                                if (item == lines_beetween_ads ){
                                    item= 0;
                                    if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
                                        posterArrayList.add(new Poster().setTypeView(4));
                                    }else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")){
                                        posterArrayList.add(new Poster().setTypeView(5));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")){
                                        if (type_ads == 0) {
                                            posterArrayList.add(new Poster().setTypeView(4));
                                            type_ads = 1;
                                        }else if (type_ads == 1){
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
                        loading=true;
                    } else {
                        if (page==0) {
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
            public void onError(String error) {
                Log.e("GenreActivity", "Error loading data: " + error);
                linear_layout_layout_error.setVisibility(View.VISIBLE);
                recycler_view_activity_genre.setVisibility(View.GONE);
                image_view_empty_list.setVisibility(View.GONE);
                relative_layout_load_more.setVisibility(View.GONE);
                swipe_refresh_layout_list_genre_search.setRefreshing(false);
                linear_layout_load_genre_activity.setVisibility(View.GONE);
                Toast.makeText(GenreActivity.this, "Network error: " + error, Toast.LENGTH_SHORT).show();
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
                    visibleItemCount = gridLayoutManager.getChildCount();
                    totalItemCount = gridLayoutManager.getItemCount();
                    pastVisiblesItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            Log.v("...", "Last Item Wow !");
                            loadPosters();
                        }
                    }
                }
            }
        });
    }

    private void initView() {
        this.swipe_refresh_layout_list_genre_search = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout_list_genre_search);
        this.linear_layout_layout_error = (LinearLayout) findViewById(R.id.linear_layout_layout_error);
        this.recycler_view_activity_genre = (RecyclerView) findViewById(R.id.recycler_view_activity_genre);
        this.image_view_empty_list = (ImageView) findViewById(R.id.image_view_empty_list);
        this.relative_layout_load_more = (RelativeLayout) findViewById(R.id.relative_layout_load_more);
        this.linear_layout_load_genre_activity = (LinearLayout) findViewById(R.id.linear_layout_load_genre_activity);
        this.button_try_again = (Button) findViewById(R.id.button_try_again);

        this.tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (this.tabletSize) {
            this.lines_beetween_ads=Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
        }else{
            this.lines_beetween_ads=Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
        }
        if (checkSUBSCRIBED()) {
            native_ads_enabled=false;
        }

        this.gridLayoutManager=  new GridLayoutManager(this,1,RecyclerView.VERTICAL,false);
        this.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (posterArrayList.get(position).getTypeView()==1){
                    return 1;
                }else{
                    return 2;
                }
            }
        });
        this.gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (posterArrayList.get(position).getTypeView()==1){
                    return 1;
                }else{
                    return 2;
                }
            }
        });
        this.adapter =new PosterAdapter(posterArrayList,this);
        this.recycler_view_activity_genre.setHasFixedSize(true);
        this.recycler_view_activity_genre.setAdapter(this.adapter);
        this.recycler_view_activity_genre.setLayoutManager(this.gridLayoutManager);
    }

    public boolean checkSUBSCRIBED(){
        PrefManager prf= new PrefManager(getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            if (!prf.getString("SUBSCRIBED").toString().equals("TRUE")) {
                return false;
            }
        }
        return true;
    }

    public void showAdsBanner() {
        if (checkSUBSCRIBED()) {
            findViewById(R.id.linear_layout_ads).setVisibility(View.GONE);
        }else{
            showAdmobBanner();
        }
    }

    public void showAdmobBanner(){
        LinearLayout adContainer = findViewById(R.id.linear_layout_ads);
        adContainer.removeAllViews();
        AdView mAdView = new AdView(this);
        mAdView.setAdSize(AdSize.SMART_BANNER);
        mAdView.setAdUnitId(prefManager.getString("ADMIN_BANNER_ADMOB_ID"));
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
        mAdView.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                super.onAdLoaded();
                adContainer.setVisibility(View.VISIBLE);
            }
        });
        adContainer.addView(mAdView);
    }
}
