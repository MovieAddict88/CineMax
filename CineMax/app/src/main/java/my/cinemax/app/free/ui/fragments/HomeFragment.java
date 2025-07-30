package my.cinemax.app.free.ui.fragments;


import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.ui.Adapters.HomeAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import es.dmoral.toasty.Toasty;

/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    private View view;
    private SwipeRefreshLayout swipe_refresh_layout_home_fragment;
    private LinearLayout linear_layout_load_home_fragment;
    private LinearLayout linear_layout_page_error_home_fragment;
    private RecyclerView recycler_view_home_fragment;
    private RelativeLayout relative_layout_load_more_home_fragment;
    private HomeAdapter homeAdapter;



    private Genre my_genre_list;
    private List<Data> dataList=new ArrayList<>();
    private GridLayoutManager gridLayoutManager;
    private Button button_try_again;


    private Integer lines_beetween_ads = 2 ;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;
    private int type_ads = 0;
    private PrefManager prefManager;
    private Integer item = 0 ;

    public HomeFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        this.view=  inflater.inflate(R.layout.fragment_home, container, false);
        prefManager= new PrefManager(getApplicationContext());

        initViews();
        initActions();
        // Don't load data here - it will be loaded by HomeActivity
        // loadData();
        return view;
    }

    private void loadData() {
        showLoadingView();
        // Use GitHub JSON API instead of old API
        apiClient.getJsonApiData(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                apiClient.FormatData(getActivity(), null); // Initialize format data
                if (response.isSuccessful() && response.body() != null) {
                    dataList.clear();
                    dataList.add(new Data().setViewType(0));
                    
                    JsonApiResponse apiResponse = response.body();
                    
                    // Load slides from GitHub JSON
                    if (apiResponse.getHome() != null && apiResponse.getHome().getSlides() != null && 
                        apiResponse.getHome().getSlides().size() > 0) {
                        Data slideData = new Data();
                        slideData.setSlides(apiResponse.getHome().getSlides());
                        dataList.add(slideData);
                    }
                    
                    // Load channels from GitHub JSON
                    if (apiResponse.getHome() != null && apiResponse.getHome().getChannels() != null && 
                        apiResponse.getHome().getChannels().size() > 0) {
                        Data channelData = new Data();
                        channelData.setChannels(apiResponse.getHome().getChannels());
                        dataList.add(channelData);
                    }
                    
                    // Load actors from GitHub JSON
                    if (apiResponse.getHome() != null && apiResponse.getHome().getActors() != null && 
                        apiResponse.getHome().getActors().size() > 0) {
                        Data actorsData = new Data();
                        actorsData.setActors(apiResponse.getHome().getActors());
                        dataList.add(actorsData);
                    }
                    
                    // Load genres from GitHub JSON
                    if (apiResponse.getHome() != null && apiResponse.getHome().getGenres() != null && 
                        apiResponse.getHome().getGenres().size() > 0) {
                        if (my_genre_list != null) {
                            Data genreDataMyList = new Data();
                            genreDataMyList.setGenre(my_genre_list);
                            dataList.add(genreDataMyList);
                        }
                        for (int i = 0; i < apiResponse.getHome().getGenres().size(); i++) {
                            Data genreData = new Data();
                            genreData.setGenre(apiResponse.getHome().getGenres().get(i));
                            dataList.add(genreData);
                            if (native_ads_enabled){
                                item++;
                                if (item == lines_beetween_ads ){
                                    item= 0;
                                    if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
                                        dataList.add(new Data().setViewType(5));
                                    }else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")){
                                        dataList.add(new Data().setViewType(6));
                                    } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")){
                                        if (type_ads == 0) {
                                            dataList.add(new Data().setViewType(5));
                                            type_ads = 1;
                                        }else if (type_ads == 1){
                                            dataList.add(new Data().setViewType(6));
                                            type_ads = 0;
                                        }
                                    }
                                }
                            }
                        }
                    }
                    showListView();
                    homeAdapter.notifyDataSetChanged();
                } else {
                    showErrorView();
                }
            }

            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                showErrorView();
            }
        });
    }
   private void showLoadingView(){
       linear_layout_load_home_fragment.setVisibility(View.VISIBLE);
       linear_layout_page_error_home_fragment.setVisibility(View.GONE);
       recycler_view_home_fragment.setVisibility(View.GONE);
   }
    private void showListView(){
        linear_layout_load_home_fragment.setVisibility(View.GONE);
        linear_layout_page_error_home_fragment.setVisibility(View.GONE);
        recycler_view_home_fragment.setVisibility(View.VISIBLE);
    }
    private void showErrorView(){
        linear_layout_load_home_fragment.setVisibility(View.GONE);
        linear_layout_page_error_home_fragment.setVisibility(View.VISIBLE);
        recycler_view_home_fragment.setVisibility(View.GONE);
    }
    private void initActions() {
        swipe_refresh_layout_home_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Don't load data here - it will be loaded by HomeActivity
                // loadData();
                swipe_refresh_layout_home_fragment.setRefreshing(false);
            }
        });
        button_try_again.setOnClickListener(v->{
            // Don't call old API - data should be loaded by HomeActivity
            // loadData();
            Toasty.info(getActivity(), "Please restart the app to reload data", Toast.LENGTH_SHORT).show();
        });
    }
    public boolean checkSUBSCRIBED(){
        if (!prefManager.getString("SUBSCRIBED").equals("TRUE") && !prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE")) {
            return false;
        }
        return true;
    }
    private void initViews() {

        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (!prefManager.getString("ADMIN_NATIVE_TYPE").equals("FALSE")){
            native_ads_enabled=true;
            if (tabletSize) {
                lines_beetween_ads=Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            }else{
                lines_beetween_ads=Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
            }
        }
        if (checkSUBSCRIBED()) {
            native_ads_enabled=false;
        }
        this.swipe_refresh_layout_home_fragment = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout_home_fragment);
        this.linear_layout_load_home_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_load_home_fragment);
        this.linear_layout_page_error_home_fragment = (LinearLayout) view.findViewById(R.id.linear_layout_page_error_home_fragment);
        this.recycler_view_home_fragment = (RecyclerView) view.findViewById(R.id.recycler_view_home_fragment);
        this.relative_layout_load_more_home_fragment = (RelativeLayout) view.findViewById(R.id.relative_layout_load_more_home_fragment);
        this.button_try_again = (Button) view.findViewById(R.id.button_try_again);

        this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),1,RecyclerView.VERTICAL,false);


        this.homeAdapter =new HomeAdapter(dataList,getActivity());
        recycler_view_home_fragment.setHasFixedSize(true);
        recycler_view_home_fragment.setAdapter(homeAdapter);
        recycler_view_home_fragment.setLayoutManager(gridLayoutManager);
    }
    
    // Method to update fragment with JSON data
    public void updateWithJsonData(my.cinemax.app.free.entity.JsonApiResponse jsonResponse) {
        if (jsonResponse != null) {
            showLoadingView();
            
            // Clear existing data
            dataList.clear();
            dataList.add(new Data().setViewType(0));
            
            // Get home data
            JsonApiResponse.HomeData homeData = jsonResponse.getHome();
            if (homeData != null) {
                // Add slides if available
                if (homeData.getSlides() != null && homeData.getSlides().size() > 0) {
                    Data slideData = new Data();
                    slideData.setSlides(homeData.getSlides());
                    dataList.add(slideData);
                }
                
                // Add channels if available
                if (homeData.getChannels() != null && homeData.getChannels().size() > 0) {
                    Data channelData = new Data();
                    channelData.setChannels(homeData.getChannels());
                    dataList.add(channelData);
                }
                
                // Add actors if available
                if (homeData.getActors() != null && homeData.getActors().size() > 0) {
                    Data actorsData = new Data();
                    actorsData.setActors(homeData.getActors());
                    dataList.add(actorsData);
                }
                
                // Add genres if available
                if (homeData.getGenres() != null && homeData.getGenres().size() > 0) {
                    if (my_genre_list != null) {
                        Data genreDataMyList = new Data();
                        genreDataMyList.setGenre(my_genre_list);
                        dataList.add(genreDataMyList);
                    }
                    
                    for (int i = 0; i < homeData.getGenres().size(); i++) {
                        Data genreData = new Data();
                        genreData.setGenre(homeData.getGenres().get(i));
                        dataList.add(genreData);
                        
                        if (native_ads_enabled) {
                            item++;
                            if (item == lines_beetween_ads) {
                                item = 0;
                                if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("FACEBOOK")) {
                                    dataList.add(new Data().setViewType(5));
                                } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("ADMOB")) {
                                    dataList.add(new Data().setViewType(6));
                                } else if (prefManager.getString("ADMIN_NATIVE_TYPE").equals("BOTH")) {
                                    if (type_ads == 0) {
                                        dataList.add(new Data().setViewType(5));
                                        type_ads = 1;
                                    } else if (type_ads == 1) {
                                        dataList.add(new Data().setViewType(6));
                                        type_ads = 0;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            
            showListView();
            homeAdapter.notifyDataSetChanged();
        }
    }

}
