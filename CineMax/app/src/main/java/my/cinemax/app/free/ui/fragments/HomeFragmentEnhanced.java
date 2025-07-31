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
import my.cinemax.app.free.api.TMDBManager;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.ui.Adapters.HomeAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import es.dmoral.toasty.Toasty;

/**
 * Enhanced HomeFragment with TMDB Integration
 * Auto-detects and enhances movie and TV series metadata
 */
public class HomeFragmentEnhanced extends Fragment {

    private RecyclerView recyclerView;
    private HomeAdapter homeAdapter;
    private List<Data> dataList = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout loadingView;
    private RelativeLayout errorView;
    private Button retryButton;
    private TMDBManager tmdbManager;

    public HomeFragmentEnhanced() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        
        // Initialize TMDB Manager
        tmdbManager = new TMDBManager();
        
        initViews(view);
        setupRecyclerView();
        setupSwipeRefresh();
        setupRetryButton();
        
        loadData();
        
        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        loadingView = view.findViewById(R.id.loadingView);
        errorView = view.findViewById(R.id.errorView);
        retryButton = view.findViewById(R.id.retryButton);
    }

    private void setupRecyclerView() {
        homeAdapter = new HomeAdapter(getActivity(), dataList);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        recyclerView.setAdapter(homeAdapter);
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            dataList.clear();
            homeAdapter.notifyDataSetChanged();
            loadData();
        });
    }

    private void setupRetryButton() {
        retryButton.setOnClickListener(v -> {
            hideErrorView();
            loadData();
        });
    }

    private void loadData() {
        showLoadingView();
        
        // First, load data from GitHub JSON API
        apiClient.getJsonApiData(new Callback<JsonApiResponse>() {
            @Override
            public void onResponse(Call<JsonApiResponse> call, Response<JsonApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    JsonApiResponse apiResponse = response.body();
                    
                    // Enhance with TMDB data
                    enhanceWithTMDB(apiResponse);
                } else {
                    // If GitHub API fails, create content from TMDB directly
                    createContentFromTMDB();
                }
            }

            @Override
            public void onFailure(Call<JsonApiResponse> call, Throwable t) {
                // If GitHub API fails, create content from TMDB directly
                createContentFromTMDB();
            }
        });
    }

    private void enhanceWithTMDB(JsonApiResponse apiResponse) {
        // Use TMDB Manager to enhance the API response
        tmdbManager.enhanceApiResponseWithTMDB(apiResponse, new TMDBManager.TMDBEnhancementCallback() {
            @Override
            public void onComplete(JsonApiResponse enhancedResponse) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        updateUIWithData(enhancedResponse);
                    });
                }
            }
        });
    }

    private void createContentFromTMDB() {
        // If JSON API is not available, create content directly from TMDB
        JsonApiResponse emptyResponse = new JsonApiResponse();
        
        tmdbManager.autoEnhancePopularContent(emptyResponse, new TMDBManager.TMDBEnhancementCallback() {
            @Override
            public void onComplete(JsonApiResponse enhancedResponse) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        updateUIWithData(enhancedResponse);
                    });
                }
            }
        });
    }

    private void updateUIWithData(JsonApiResponse jsonResponse) {
        try {
            dataList.clear();
            dataList.add(new Data().setViewType(0));

            // Load enhanced slides with TMDB data
            if (jsonResponse.getHome() != null && jsonResponse.getHome().getSlides() != null && 
                jsonResponse.getHome().getSlides().size() > 0) {
                Data slideData = new Data();
                slideData.setSlides(jsonResponse.getHome().getSlides());
                dataList.add(slideData);
            }

            // Load channels (live TV - preserved unchanged)
            if (jsonResponse.getHome() != null && jsonResponse.getHome().getChannels() != null && 
                jsonResponse.getHome().getChannels().size() > 0) {
                Data channelData = new Data();
                channelData.setChannels(jsonResponse.getHome().getChannels());
                dataList.add(channelData);
            }

            // Load actors (enhanced with TMDB data)
            if (jsonResponse.getHome() != null && jsonResponse.getHome().getActors() != null && 
                jsonResponse.getHome().getActors().size() > 0) {
                Data actorsData = new Data();
                actorsData.setActors(jsonResponse.getHome().getActors());
                dataList.add(actorsData);
            }

            // Load genres
            if (jsonResponse.getHome() != null && jsonResponse.getHome().getGenres() != null && 
                jsonResponse.getHome().getGenres().size() > 0) {
                Data genreData = new Data();
                genreData.setGenres(jsonResponse.getHome().getGenres());
                dataList.add(genreData);
            }

            homeAdapter.notifyDataSetChanged();
            hideLoadingView();
            
            // Show success message
            if (getActivity() != null) {
                Toasty.success(getActivity(), "Content enhanced with TMDB data!", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            showErrorView();
        }
    }

    private void showLoadingView() {
        if (loadingView != null) {
            loadingView.setVisibility(View.VISIBLE);
        }
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void hideLoadingView() {
        if (loadingView != null) {
            loadingView.setVisibility(View.GONE);
        }
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void showErrorView() {
        hideLoadingView();
        if (errorView != null) {
            errorView.setVisibility(View.VISIBLE);
        }
        if (getActivity() != null) {
            Toasty.error(getActivity(), "Failed to load content. Check your internet connection.", Toast.LENGTH_LONG).show();
        }
    }

    private void hideErrorView() {
        if (errorView != null) {
            errorView.setVisibility(View.GONE);
        }
    }

    // Method to update with JSON data (for compatibility)
    public void updateWithJsonData(JsonApiResponse jsonResponse) {
        // Enhance the provided JSON response with TMDB data
        enhanceWithTMDB(jsonResponse);
    }
}