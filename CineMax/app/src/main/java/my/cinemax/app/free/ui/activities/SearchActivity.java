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
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import my.cinemax.app.free.Provider.PrefManager;
import my.cinemax.app.free.R;
import my.cinemax.app.free.MyApplication;
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
    private boolean tabletSize = false;
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
        
        try {
            setContentView(R.layout.activity_search);
            prefManager = new PrefManager(getApplicationContext());

            initView();
            initAction();
            loadSearchResultsFromJson();
            showAdsBanner();
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in onCreate: " + e.getMessage(), e);
            showError();
        }
    }

    private void initView() {
        try {
            // Initialize tablet size properly
            tabletSize = getResources().getBoolean(R.bool.isTablet);
            
            // Initialize native ads settings
            if (prefManager != null && !prefManager.getString("ADMIN_NATIVE_TYPE").equals("FALSE")) {
                native_ads_enabled = true;
                if (tabletSize) {
                    lines_beetween_ads = 6 * Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
                } else {
                    lines_beetween_ads = 3 * Integer.parseInt(prefManager.getString("ADMIN_NATIVE_LINES"));
                }
            }
            
            if (checkSUBSCRIBED()) {
                native_ads_enabled = false;
            }

            // Get query from intent with null check
            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                this.query = bundle.getString("query", "");
                Log.d("SearchActivity", "Search query from intent: '" + this.query + "'");
            } else {
                this.query = "";
                Log.w("SearchActivity", "No extras in intent, using empty query");
            }
            
            // Validate query
            if (query == null || query.trim().isEmpty()) {
                Log.w("SearchActivity", "Empty or null search query");
                query = "";
            } else {
                query = query.trim();
                Log.d("SearchActivity", "Final search query: '" + query + "'");
            }
            
            // Setup toolbar
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            if (toolbar != null) {
                toolbar.setTitle(query.isEmpty() ? "Search" : query);
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }

            // Initialize views with null checks
            this.linear_layout_load_search_activity = findViewById(R.id.linear_layout_load_search_activity);
            this.swipe_refresh_layout_list_search_search = findViewById(R.id.swipe_refresh_layout_list_search_search);
            button_try_again = findViewById(R.id.button_try_again);
            image_view_empty_list = findViewById(R.id.image_view_empty_list);
            linear_layout_layout_error = findViewById(R.id.linear_layout_layout_error);
            recycler_view_activity_search = findViewById(R.id.recycler_view_activity_search);
            
            // Initialize adapter with null check
            if (recycler_view_activity_search != null) {
                adapter = new PosterAdapter(posterArrayList, this);
                recycler_view_activity_search.setHasFixedSize(true);
                recycler_view_activity_search.setAdapter(adapter);
                Log.d("SearchActivity", "Adapter initialized successfully");
            } else {
                Log.e("SearchActivity", "RecyclerView not found in layout");
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in initView: " + e.getMessage(), e);
            showError();
        }
    }

    /**
     * Load search results from GitHub JSON API instead of old server API
     */
    private void loadSearchResultsFromJson() {
        try {
            if (swipe_refresh_layout_list_search_search != null) {
                swipe_refresh_layout_list_search_search.setRefreshing(false);
            }
            
            // Show loading state
            if (linear_layout_load_search_activity != null) {
                linear_layout_load_search_activity.setVisibility(View.VISIBLE);
            }
            
            // If we already have cached data, use it
            if (cachedJsonResponse != null && !allMovies.isEmpty()) {
                Log.d("SearchActivity", "Using cached data - Movies: " + allMovies.size() + ", Channels: " + allChannels.size());
                filterAndDisplaySearchResults();
                return;
            }
            
            // Check network connectivity first
            if (!MyApplication.hasNetwork()) {
                Log.w("SearchActivity", "No network connectivity, loading from local data");
                loadFromLocalJsonFile();
                return;
            }
            
            Log.d("SearchActivity", "Loading data from GitHub JSON API...");
            
            // Load data from GitHub JSON API
            apiClient.getJsonApiData(new apiClient.JsonApiCallback() {
                @Override
                public void onSuccess(JsonApiResponse jsonResponse) {
                    try {
                        if (jsonResponse != null) {
                            cachedJsonResponse = jsonResponse;
                            allMovies = jsonResponse.getMovies() != null ? jsonResponse.getMovies() : new ArrayList<>();
                            allChannels = jsonResponse.getChannels() != null ? jsonResponse.getChannels() : new ArrayList<>();
                            
                            Log.d("SearchActivity", "Data loaded successfully - Movies: " + allMovies.size() + ", Channels: " + allChannels.size());
                            
                            // Log some sample data for debugging
                            if (!allMovies.isEmpty()) {
                                Log.d("SearchActivity", "Sample movie: " + allMovies.get(0).getTitle());
                            }
                            if (!allChannels.isEmpty()) {
                                Log.d("SearchActivity", "Sample channel: " + allChannels.get(0).getTitle());
                            }
                            
                            filterAndDisplaySearchResults();
                        } else {
                            Log.w("SearchActivity", "Received null JSON response");
                            showError();
                        }
                    } catch (Exception e) {
                        Log.e("SearchActivity", "Error processing JSON response: " + e.getMessage(), e);
                        showError();
                    }
                }
                
                @Override
                public void onError(String error) {
                    Log.e("SearchActivity", "Error loading JSON data: " + error);
                    Log.d("SearchActivity", "Trying to load from local JSON file as fallback...");
                    
                    // Show the actual error to the user first
                    runOnUiThread(() -> {
                        Toast.makeText(SearchActivity.this, "GitHub API Error: " + error, Toast.LENGTH_LONG).show();
                    });
                    
                    // Try to load from local JSON file as fallback
                    loadFromLocalJsonFile();
                }
            });
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in loadSearchResultsFromJson: " + e.getMessage(), e);
            showError();
        }
    }
    
    /**
     * Load data from local JSON file as fallback
     */
    private void loadFromLocalJsonFile() {
        try {
            Log.d("SearchActivity", "Loading from local JSON file...");
            
            // Read JSON file from assets
            String jsonString = readJsonFromAssets("movie_data.json");
            if (jsonString != null && !jsonString.isEmpty()) {
                // Parse JSON using Gson
                com.google.gson.Gson gson = new com.google.gson.Gson();
                JsonApiResponse localResponse = gson.fromJson(jsonString, JsonApiResponse.class);
                
                if (localResponse != null) {
                    // Cache the response
                    cachedJsonResponse = localResponse;
                    allMovies = localResponse.getMovies() != null ? localResponse.getMovies() : new ArrayList<>();
                    allChannels = localResponse.getChannels() != null ? localResponse.getChannels() : new ArrayList<>();
                    
                    Log.d("SearchActivity", "Local JSON data loaded successfully - Movies: " + allMovies.size() + ", Channels: " + allChannels.size());
                    
                    // Log sample data for debugging
                    if (!allMovies.isEmpty()) {
                        Log.d("SearchActivity", "Sample movie: " + allMovies.get(0).getTitle());
                    }
                    if (!allChannels.isEmpty()) {
                        Log.d("SearchActivity", "Sample channel: " + allChannels.get(0).getTitle());
                    }
                    
                    filterAndDisplaySearchResults();
                    return;
                }
            }
            
            // Fallback to sample data if JSON parsing fails
            Log.w("SearchActivity", "Failed to parse local JSON, using sample data");
            loadSampleData();
            
        } catch (Exception e) {
            Log.e("SearchActivity", "Error loading from local JSON file: " + e.getMessage(), e);
            // Fallback to sample data
            loadSampleData();
        }
    }
    
    /**
     * Read JSON file from assets
     */
    private String readJsonFromAssets(String fileName) {
        try {
            java.io.InputStream inputStream = getAssets().open(fileName);
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, "UTF-8");
        } catch (Exception e) {
            Log.e("SearchActivity", "Error reading JSON from assets: " + e.getMessage(), e);
            return null;
        }
    }
    
    /**
     * Load sample data as fallback
     */
    private void loadSampleData() {
        try {
            Log.d("SearchActivity", "Loading sample data...");
            
            // Create a simple JsonApiResponse with sample data for testing
            JsonApiResponse localResponse = new JsonApiResponse();
            
            // Create sample channels based on actual GitHub data
            List<Channel> sampleChannels = new ArrayList<>();
            
            // Add "Cine Mo!" channel (exists in GitHub data)
            Channel cineMoChannel = new Channel();
            cineMoChannel.setId(1);
            cineMoChannel.setTitle("Cine Mo!");
            cineMoChannel.setDescription("Cine Mo! is a Filipino pay television channel owned by ABS-CBN.");
            cineMoChannel.setImage("https://i.imgur.com/KUwrSOH.png");
            sampleChannels.add(cineMoChannel);
            
            // Add "AZ2" channel (exists in GitHub data, not "A2Z")
            Channel az2Channel = new Channel();
            az2Channel.setId(2);
            az2Channel.setTitle("AZ2");
            az2Channel.setDescription("AZ2 is a Filipino entertainment channel.");
            az2Channel.setImage("https://example.com/az2.jpg");
            sampleChannels.add(az2Channel);
            
            // Add "Cinemax" channel (exists in GitHub data)
            Channel cinemaxChannel = new Channel();
            cinemaxChannel.setId(3);
            cinemaxChannel.setTitle("Cinemax");
            cinemaxChannel.setDescription("Cinemax is a premium cable and satellite television network.");
            cinemaxChannel.setImage("https://example.com/cinemax.jpg");
            sampleChannels.add(cinemaxChannel);
            
            // Add "Kapamilya Channel" (exists in GitHub data)
            Channel kapamilyaChannel = new Channel();
            kapamilyaChannel.setId(4);
            kapamilyaChannel.setTitle("Kapamilya Channel");
            kapamilyaChannel.setDescription("Kapamilya Channel is a Filipino free-to-air television network.");
            kapamilyaChannel.setImage("https://i.imgur.com/Dcys2TG.png");
            sampleChannels.add(kapamilyaChannel);
            
            // Create sample movies
            List<Poster> sampleMovies = new ArrayList<>();
            
            // Add "Big Buck Bunny" movie (exists in GitHub data)
            Poster bigBuckBunny = new Poster();
            bigBuckBunny.setId(1);
            bigBuckBunny.setTitle("Big Buck Bunny");
            bigBuckBunny.setDescription("Big Buck Bunny tells the story of a giant rabbit with a heart bigger than himself.");
            bigBuckBunny.setImage("https://peach.blender.org/wp-content/uploads/title_anouncement.jpg?x11217");
            sampleMovies.add(bigBuckBunny);
            
            // Set the data
            localResponse.setChannels(sampleChannels);
            localResponse.setMovies(sampleMovies);
            
            // Cache the response
            cachedJsonResponse = localResponse;
            allMovies = sampleMovies;
            allChannels = sampleChannels;
            
            Log.d("SearchActivity", "Sample data loaded successfully - Movies: " + allMovies.size() + ", Channels: " + allChannels.size());
            
            // Log sample data for debugging
            if (!allMovies.isEmpty()) {
                Log.d("SearchActivity", "Sample movie: " + allMovies.get(0).getTitle());
            }
            if (!allChannels.isEmpty()) {
                Log.d("SearchActivity", "Sample channel: " + allChannels.get(0).getTitle());
            }
            
            filterAndDisplaySearchResults();
            
        } catch (Exception e) {
            Log.e("SearchActivity", "Error loading sample data: " + e.getMessage(), e);
            runOnUiThread(() -> {
                showError();
                Toast.makeText(SearchActivity.this, "Failed to load search data", Toast.LENGTH_SHORT).show();
            });
        }
    }
    
    /**
     * Filter movies and channels by search query and display them
     */
    private void filterAndDisplaySearchResults() {
        try {
            // Clear existing data safely
            if (posterArrayList != null) {
                posterArrayList.clear();
            } else {
                posterArrayList = new ArrayList<>();
            }
            
            if (channelArrayList != null) {
                channelArrayList.clear();
            } else {
                channelArrayList = new ArrayList<>();
            }
            
            // Validate search query
            if (query == null || query.trim().isEmpty()) {
                Log.w("SearchActivity", "Empty search query, showing empty results");
                displayResults();
                return;
            }
            
            // Validate minimum search length to prevent crashes
            if (query.trim().length() < 1) {
                Log.w("SearchActivity", "Search query too short: '" + query + "', showing empty results");
                displayResults();
                return;
            }
            
            String searchQuery = query.toLowerCase().trim();
            Log.d("SearchActivity", "Searching for: '" + searchQuery + "'");
            
            // Filter channels by search query with improved null checks
            if (allChannels != null) {
                Log.d("SearchActivity", "Filtering " + allChannels.size() + " channels");
                for (Channel channel : allChannels) {
                    try {
                        if (channel != null) {
                            boolean matches = false;
                            
                            // Search in title
                            if (channel.getTitle() != null) {
                                String channelTitle = channel.getTitle().toLowerCase().trim();
                                if (channelTitle.contains(searchQuery)) {
                                    matches = true;
                                    Log.d("SearchActivity", "Found matching channel by title: " + channel.getTitle());
                                }
                            }
                            
                            // Search in description if title didn't match
                            if (!matches && channel.getDescription() != null) {
                                String channelDescription = channel.getDescription().toLowerCase().trim();
                                if (channelDescription.contains(searchQuery)) {
                                    matches = true;
                                    Log.d("SearchActivity", "Found matching channel by description: " + channel.getTitle());
                                }
                            }
                            
                            if (matches) {
                                channelArrayList.add(channel);
                            }
                        }
                    } catch (Exception e) {
                        Log.w("SearchActivity", "Error filtering channel: " + e.getMessage());
                    }
                }
            } else {
                Log.w("SearchActivity", "allChannels is null");
            }
            
            // Filter movies by search query with improved null checks
            if (allMovies != null) {
                Log.d("SearchActivity", "Filtering " + allMovies.size() + " movies");
                for (Poster movie : allMovies) {
                    try {
                        if (movie != null) {
                            boolean matches = false;
                            
                            // Search in title
                            if (movie.getTitle() != null) {
                                String movieTitle = movie.getTitle().toLowerCase().trim();
                                if (movieTitle.contains(searchQuery)) {
                                    matches = true;
                                    Log.d("SearchActivity", "Found matching movie by title: " + movie.getTitle());
                                }
                            }
                            
                            // Search in description if title didn't match
                            if (!matches && movie.getDescription() != null) {
                                String movieDescription = movie.getDescription().toLowerCase().trim();
                                if (movieDescription.contains(searchQuery)) {
                                    matches = true;
                                    Log.d("SearchActivity", "Found matching movie by description: " + movie.getTitle());
                                }
                            }
                            
                            if (matches) {
                                posterArrayList.add(movie.setTypeView(1));
                            }
                        }
                    } catch (Exception e) {
                        Log.w("SearchActivity", "Error filtering movie: " + e.getMessage());
                    }
                }
            } else {
                Log.w("SearchActivity", "allMovies is null");
            }
            
            Log.d("SearchActivity", "Search results - Channels: " + channelArrayList.size() + ", Movies: " + posterArrayList.size());
            
            // Add channel section header if channels found
            if (channelArrayList.size() > 0) {
                posterArrayList.add(0, new Poster().setTypeView(3));
                setupGridLayoutForChannels();
            } else {
                setupGridLayoutForMovies();
            }
            
            // Add native ads to movies
            if (native_ads_enabled) {
                addNativeAdsToMovies();
            }
            
            displayResults();
            
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in filterAndDisplaySearchResults: " + e.getMessage(), e);
            showError();
        }
    }
    
    /**
     * Display the filtered results
     */
    private void displayResults() {
        try {
            runOnUiThread(() -> {
                try {
                    Log.d("SearchActivity", "Displaying results - Channels: " + (channelArrayList != null ? channelArrayList.size() : 0) + 
                          ", Movies: " + (posterArrayList != null ? posterArrayList.size() : 0));
                    
                    // Display results based on data availability
                    if ((channelArrayList == null || channelArrayList.size() == 0) && 
                        (posterArrayList == null || posterArrayList.size() == 0)) {
                        // Show empty state
                        Log.d("SearchActivity", "No results found, showing empty state");
                        if (linear_layout_layout_error != null) {
                            linear_layout_layout_error.setVisibility(View.GONE);
                        }
                        if (recycler_view_activity_search != null) {
                            recycler_view_activity_search.setVisibility(View.GONE);
                        }
                        if (image_view_empty_list != null) {
                            image_view_empty_list.setVisibility(View.VISIBLE);
                        }
                    } else {
                        // Show results
                        Log.d("SearchActivity", "Results found, showing list");
                        if (linear_layout_layout_error != null) {
                            linear_layout_layout_error.setVisibility(View.GONE);
                        }
                        if (recycler_view_activity_search != null) {
                            recycler_view_activity_search.setVisibility(View.VISIBLE);
                        }
                        if (image_view_empty_list != null) {
                            image_view_empty_list.setVisibility(View.GONE);
                        }
                    }
                    
                    // Update UI elements
                    if (swipe_refresh_layout_list_search_search != null) {
                        swipe_refresh_layout_list_search_search.setRefreshing(false);
                    }
                    if (linear_layout_load_search_activity != null) {
                        linear_layout_load_search_activity.setVisibility(View.GONE);
                    }
                    
                    // Setup RecyclerView with null checks
                    if (recycler_view_activity_search != null && gridLayoutManager != null) {
                        recycler_view_activity_search.setLayoutManager(gridLayoutManager);
                        Log.d("SearchActivity", "RecyclerView layout manager set");
                    } else {
                        Log.w("SearchActivity", "RecyclerView or GridLayoutManager is null");
                        // Try to initialize RecyclerView if it's null
                        if (recycler_view_activity_search == null) {
                            recycler_view_activity_search = findViewById(R.id.recycler_view_activity_search);
                            if (recycler_view_activity_search != null) {
                                adapter = new PosterAdapter(posterArrayList, this);
                                recycler_view_activity_search.setHasFixedSize(true);
                                recycler_view_activity_search.setAdapter(adapter);
                                Log.d("SearchActivity", "RecyclerView initialized");
                            }
                        }
                    }
                    
                    // Notify adapter with null check
                    if (adapter != null) {
                        adapter.notifyDataSetChanged();
                        Log.d("SearchActivity", "Adapter notified of data change");
                    } else {
                        Log.w("SearchActivity", "Adapter is null");
                        // Try to create adapter if it's null
                        if (recycler_view_activity_search != null) {
                            adapter = new PosterAdapter(posterArrayList, this);
                            recycler_view_activity_search.setAdapter(adapter);
                            Log.d("SearchActivity", "Adapter created and set");
                        }
                    }
                } catch (Exception e) {
                    Log.e("SearchActivity", "Error updating UI: " + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in displayResults: " + e.getMessage(), e);
        }
    }
    
    /**
     * Setup grid layout for channels section
     */
    private void setupGridLayoutForChannels() {
        try {
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
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in setupGridLayoutForChannels: " + e.getMessage(), e);
            // Fallback to simple grid layout
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), tabletSize ? 6 : 3, RecyclerView.VERTICAL, false);
        }
    }
    
    /**
     * Setup grid layout for movies only
     */
    private void setupGridLayoutForMovies() {
        try {
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
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in setupGridLayoutForMovies: " + e.getMessage(), e);
            // Fallback to simple grid layout
            gridLayoutManager = new GridLayoutManager(getApplicationContext(), tabletSize ? 6 : 3, RecyclerView.VERTICAL, false);
        }
    }
    
    /**
     * Add native ads to movies list
     */
    private void addNativeAdsToMovies() {
        try {
            if (posterArrayList == null || prefManager == null) {
                return;
            }
            
            item = 0;
            for (int i = 0; i < posterArrayList.size(); i++) {
                try {
                    if (posterArrayList.get(i) != null && posterArrayList.get(i).getTypeView() == 1) { // Only for movies, not channels
                        item++;
                        if (item == lines_beetween_ads) {
                            item = 0;
                            String nativeType = prefManager.getString("ADMIN_NATIVE_TYPE");
                            if ("FACEBOOK".equals(nativeType)) {
                                posterArrayList.add(i + 1, new Poster().setTypeView(4));
                            } else if ("ADMOB".equals(nativeType)) {
                                posterArrayList.add(i + 1, new Poster().setTypeView(5));
                            } else if ("BOTH".equals(nativeType)) {
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
                } catch (Exception e) {
                    Log.w("SearchActivity", "Error adding native ad at position " + i + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in addNativeAdsToMovies: " + e.getMessage(), e);
        }
    }
    
    /**
     * Show error state
     */
    private void showError() {
        try {
            runOnUiThread(() -> {
                try {
                    if (linear_layout_layout_error != null) {
                        linear_layout_layout_error.setVisibility(View.VISIBLE);
                    }
                    if (recycler_view_activity_search != null) {
                        recycler_view_activity_search.setVisibility(View.GONE);
                    }
                    if (image_view_empty_list != null) {
                        image_view_empty_list.setVisibility(View.GONE);
                    }
                    if (swipe_refresh_layout_list_search_search != null) {
                        swipe_refresh_layout_list_search_search.setVisibility(View.GONE);
                    }
                    if (linear_layout_load_search_activity != null) {
                        linear_layout_load_search_activity.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    Log.e("SearchActivity", "Error updating error UI: " + e.getMessage(), e);
                }
            });
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in showError: " + e.getMessage(), e);
        }
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
        try {
            if (swipe_refresh_layout_list_search_search != null) {
                swipe_refresh_layout_list_search_search.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        try {
                            if (posterArrayList != null) {
                                posterArrayList.clear();
                            }
                            if (channelArrayList != null) {
                                channelArrayList.clear();
                            }
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            loadSearchResultsFromJson();
                        } catch (Exception e) {
                            Log.e("SearchActivity", "Error in swipe refresh: " + e.getMessage(), e);
                        }
                    }
                });
            }
            
            if (button_try_again != null) {
                button_try_again.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            if (posterArrayList != null) {
                                posterArrayList.clear();
                            }
                            if (channelArrayList != null) {
                                channelArrayList.clear();
                            }
                            if (adapter != null) {
                                adapter.notifyDataSetChanged();
                            }
                            loadSearchResultsFromJson();
                        } catch (Exception e) {
                            Log.e("SearchActivity", "Error in try again: " + e.getMessage(), e);
                        }
                    }
                });
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "Error in initAction: " + e.getMessage(), e);
        }
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
        try {
            PrefManager prefManager = new PrefManager(getApplicationContext());
            if (!prefManager.getString("SUBSCRIBED").equals("TRUE") && !prefManager.getString("NEW_SUBSCRIBE_ENABLED").equals("TRUE")) {
                return false;
            }
            return true;
        } catch (Exception e) {
            Log.e("SearchActivity", "Error checking subscription: " + e.getMessage(), e);
            return false;
        }
    }
    
    public void showAdsBanner() {
        try {
            if (!checkSUBSCRIBED()) {
                PrefManager prefManager = new PrefManager(getApplicationContext());
                if (prefManager != null && !prefManager.getString("ADMIN_BANNER_TYPE").equals("FALSE")) {
                    showAdmobBanner();
                }
            }
        } catch (Exception e) {
            Log.e("SearchActivity", "Error showing ads banner: " + e.getMessage(), e);
        }
    }
    
    public void showAdmobBanner(){
        try {
            PrefManager prefManager = new PrefManager(getApplicationContext());
            LinearLayout linear_layout_ads = (LinearLayout) findViewById(R.id.linear_layout_ads);
            
            if (linear_layout_ads != null && prefManager != null) {
                final AdView mAdView = new AdView(this);
                mAdView.setAdSize(AdSize.SMART_BANNER);
                mAdView.setAdUnitId(prefManager.getString("ADMIN_BANNER_ADMOB_ID"));
                AdRequest adRequest = new AdRequest.Builder().build();
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
        } catch (Exception e) {
            Log.e("SearchActivity", "Error showing AdMob banner: " + e.getMessage(), e);
        }
    }
}
