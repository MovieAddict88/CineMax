package my.cinemax.app.free.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.database.CachedDataService;
import my.cinemax.app.free.entity.Data;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.JsonApiResponse;
import my.cinemax.app.free.ui.Adapters.HomeAdapter;

import static com.facebook.FacebookSdk.getApplicationContext;

/**
 * Example Fragment showing how to integrate caching with existing UI code
 * This is a modified version of HomeFragment that uses caching
 */
public class CachedHomeFragment extends Fragment {

    private View view;
    private SwipeRefreshLayout swipe_refresh_layout_home_fragment;
    private LinearLayout linear_layout_load_home_fragment;
    private LinearLayout linear_layout_page_error_home_fragment;
    private RecyclerView recycler_view_home_fragment;
    private RelativeLayout relative_layout_load_more_home_fragment;
    private HomeAdapter homeAdapter;
    private Button button_try_again;

    private Genre my_genre_list;
    private List<Data> dataList = new ArrayList<>();
    private GridLayoutManager gridLayoutManager;

    private Integer lines_beetween_ads = 2;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false;
    private int type_ads = 0;
    private PrefManager prefManager;
    private Integer item = 0;

    // Caching components
    private CachedDataService cachedDataService;
    private boolean isLoadingFromCache = false;

    public CachedHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_home, container, false);
        prefManager = new PrefManager(getApplicationContext());

        // Initialize caching service
        cachedDataService = new CachedDataService(getContext());

        initViews();
        initActions();
        loadDataWithCache();
        return view;
    }

    private void initViews() {
        swipe_refresh_layout_home_fragment = view.findViewById(R.id.swipe_refresh_layout_home_fragment);
        linear_layout_load_home_fragment = view.findViewById(R.id.linear_layout_load_home_fragment);
        linear_layout_page_error_home_fragment = view.findViewById(R.id.linear_layout_page_error_home_fragment);
        recycler_view_home_fragment = view.findViewById(R.id.recycler_view_home_fragment);
        relative_layout_load_more_home_fragment = view.findViewById(R.id.relative_layout_load_more_home_fragment);
        button_try_again = view.findViewById(R.id.button_try_again);

        // Set up RecyclerView
        gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        recycler_view_home_fragment.setHasFixedSize(true);
        recycler_view_home_fragment.setLayoutManager(gridLayoutManager);
    }

    private void initActions() {
        // Swipe to refresh
        swipe_refresh_layout_home_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                forceRefreshData();
            }
        });

        // Try again button
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataWithCache();
            }
        });
    }

    /**
     * Load data with caching support
     * This will first show cached data (if available) and then update with fresh data
     */
    private void loadDataWithCache() {
        showLoadingView();

        cachedDataService.getHomeDataWithCache(new CachedDataService.DataCallback<JsonApiResponse>() {
            @Override
            public void onCacheLoaded(JsonApiResponse cachedData) {
                // This is called when cached data is available
                isLoadingFromCache = true;
                processHomeData(cachedData);
                
                // Show a subtle indicator that cached data is being shown
                if (swipe_refresh_layout_home_fragment != null) {
                    swipe_refresh_layout_home_fragment.setRefreshing(true);
                }
                
                Toasty.info(getContext(), "Showing cached data, updating...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(JsonApiResponse freshData) {
                // This is called when fresh data is loaded from API
                isLoadingFromCache = false;
                processHomeData(freshData);
                hideLoadingView();
                
                if (swipe_refresh_layout_home_fragment != null) {
                    swipe_refresh_layout_home_fragment.setRefreshing(false);
                }
                
                if (freshData != null) {
                    Toasty.success(getContext(), "Data updated successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(String error) {
                // This is called when API fails and no cache is available
                if (!isLoadingFromCache) {
                    hideLoadingView();
                    showErrorView();
                    Toasty.error(getContext(), "Failed to load data: " + error, Toast.LENGTH_LONG).show();
                }
                
                if (swipe_refresh_layout_home_fragment != null) {
                    swipe_refresh_layout_home_fragment.setRefreshing(false);
                }
            }
        });
    }

    /**
     * Force refresh data (ignoring cache)
     */
    private void forceRefreshData() {
        cachedDataService.forceRefresh(new CachedDataService.DataCallback<JsonApiResponse>() {
            @Override
            public void onCacheLoaded(JsonApiResponse cachedData) {
                // Not used in force refresh
            }

            @Override
            public void onSuccess(JsonApiResponse freshData) {
                processHomeData(freshData);
                swipe_refresh_layout_home_fragment.setRefreshing(false);
                Toasty.success(getContext(), "Data refreshed successfully", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(String error) {
                swipe_refresh_layout_home_fragment.setRefreshing(false);
                Toasty.error(getContext(), "Failed to refresh: " + error, Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Process the home data (cached or fresh)
     */
    private void processHomeData(JsonApiResponse apiResponse) {
        if (apiResponse == null) return;

        dataList.clear();
        dataList.add(new Data().setViewType(0));

        // Load slides from JSON response
        if (apiResponse.getHome() != null && apiResponse.getHome().getSlides() != null &&
                apiResponse.getHome().getSlides().size() > 0) {
            Data slideData = new Data();
            slideData.setSlides(apiResponse.getHome().getSlides());
            dataList.add(slideData);
        }

        // Load channels from JSON response
        if (apiResponse.getHome() != null && apiResponse.getHome().getChannels() != null &&
                apiResponse.getHome().getChannels().size() > 0) {
            Data channelData = new Data();
            channelData.setChannels(apiResponse.getHome().getChannels());
            dataList.add(channelData);
        }

        // Load actors from JSON response
        if (apiResponse.getHome() != null && apiResponse.getHome().getActors() != null &&
                apiResponse.getHome().getActors().size() > 0) {
            Data actorsData = new Data();
            actorsData.setActors(apiResponse.getHome().getActors());
            dataList.add(actorsData);
        }

        // Load genres from JSON response
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
                
                // Add native ads if enabled
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

        // Update the adapter
        updateAdapter();
        hideLoadingView();
    }

    private void updateAdapter() {
        if (homeAdapter == null) {
            homeAdapter = new HomeAdapter(getActivity(), dataList);
            recycler_view_home_fragment.setAdapter(homeAdapter);
        } else {
            homeAdapter.notifyDataSetChanged();
        }
    }

    private void showLoadingView() {
        if (linear_layout_load_home_fragment != null) {
            linear_layout_load_home_fragment.setVisibility(View.VISIBLE);
        }
        if (linear_layout_page_error_home_fragment != null) {
            linear_layout_page_error_home_fragment.setVisibility(View.GONE);
        }
        if (recycler_view_home_fragment != null) {
            recycler_view_home_fragment.setVisibility(View.GONE);
        }
    }

    private void hideLoadingView() {
        if (linear_layout_load_home_fragment != null) {
            linear_layout_load_home_fragment.setVisibility(View.GONE);
        }
        if (linear_layout_page_error_home_fragment != null) {
            linear_layout_page_error_home_fragment.setVisibility(View.GONE);
        }
        if (recycler_view_home_fragment != null) {
            recycler_view_home_fragment.setVisibility(View.VISIBLE);
        }
    }

    private void showErrorView() {
        if (linear_layout_load_home_fragment != null) {
            linear_layout_load_home_fragment.setVisibility(View.GONE);
        }
        if (linear_layout_page_error_home_fragment != null) {
            linear_layout_page_error_home_fragment.setVisibility(View.VISIBLE);
        }
        if (recycler_view_home_fragment != null) {
            recycler_view_home_fragment.setVisibility(View.GONE);
        }
    }

    /**
     * Get cache statistics for debugging
     */
    public String getCacheStats() {
        return cachedDataService.getCacheStats();
    }

    /**
     * Clear all cached data
     */
    public void clearCache() {
        cachedDataService.clearCache();
        Toasty.info(getContext(), "Cache cleared", Toast.LENGTH_SHORT).show();
    }
}