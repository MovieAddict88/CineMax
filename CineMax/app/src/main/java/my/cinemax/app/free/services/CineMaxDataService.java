package my.cinemax.app.free.services;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.cinemax.app.free.api.apiClient;
import my.cinemax.app.free.api.apiRest;
import my.cinemax.app.free.database.entity.CachedChannel;
import my.cinemax.app.free.database.entity.CachedPoster;
import my.cinemax.app.free.database.repository.CineMaxRepository;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Poster;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CineMaxDataService {
    
    private static final String TAG = "CineMaxDataService";
    private static final long CACHE_DURATION = 30 * 60 * 1000; // 30 minutes
    
    private CineMaxRepository repository;
    private apiRest apiService;
    private ExecutorService executorService;
    
    public CineMaxDataService(Context context) {
        repository = new CineMaxRepository(context);
        apiService = apiClient.getClient().create(apiRest.class);
        executorService = Executors.newFixedThreadPool(4);
    }
    
    // Get movies with caching
    public LiveData<List<CachedPoster>> getMovies() {
        MutableLiveData<List<CachedPoster>> result = new MutableLiveData<>();
        
        // First, try to get from cache
        LiveData<List<CachedPoster>> cachedData = repository.getAllPosters();
        cachedData.observeForever(posters -> {
            if (posters != null && !posters.isEmpty()) {
                // Check if data is fresh
                if (repository.isDataFresh(posters.get(0).getLastUpdated(), CACHE_DURATION)) {
                    result.setValue(posters);
                    return;
                }
            }
            
            // If cache is empty or stale, fetch from API
            fetchMoviesFromApi(result);
        });
        
        return result;
    }
    
    private void fetchMoviesFromApi(MutableLiveData<List<CachedPoster>> result) {
        executorService.execute(() -> {
            try {
                Call<List<Poster>> call = apiService.getMoviesFromJson();
                call.enqueue(new Callback<List<Poster>>() {
                    @Override
                    public void onResponse(Call<List<Poster>> call, Response<List<Poster>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Convert API response to cached entities
                            List<CachedPoster> cachedPosters = convertToCachedPosters(response.body());
                            
                            // Save to database
                            repository.insertAll(cachedPosters);
                            
                            // Return the cached data
                            result.postValue(cachedPosters);
                        } else {
                            Log.e(TAG, "API call failed: " + response.code());
                            result.postValue(null);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<List<Poster>> call, Throwable t) {
                        Log.e(TAG, "API call failed", t);
                        result.postValue(null);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error fetching movies", e);
                result.postValue(null);
            }
        });
    }
    
    // Get channels with caching
    public LiveData<List<CachedChannel>> getChannels() {
        MutableLiveData<List<CachedChannel>> result = new MutableLiveData<>();
        
        // First, try to get from cache
        LiveData<List<CachedChannel>> cachedData = repository.getAllChannels();
        cachedData.observeForever(channels -> {
            if (channels != null && !channels.isEmpty()) {
                // Check if data is fresh
                if (repository.isDataFresh(channels.get(0).getLastUpdated(), CACHE_DURATION)) {
                    result.setValue(channels);
                    return;
                }
            }
            
            // If cache is empty or stale, fetch from API
            fetchChannelsFromApi(result);
        });
        
        return result;
    }
    
    private void fetchChannelsFromApi(MutableLiveData<List<CachedChannel>> result) {
        executorService.execute(() -> {
            try {
                Call<List<Channel>> call = apiService.getChannelsFromJson();
                call.enqueue(new Callback<List<Channel>>() {
                    @Override
                    public void onResponse(Call<List<Channel>> call, Response<List<Channel>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Convert API response to cached entities
                            List<CachedChannel> cachedChannels = convertToCachedChannels(response.body());
                            
                            // Save to database
                            repository.insertAll(cachedChannels);
                            
                            // Return the cached data
                            result.postValue(cachedChannels);
                        } else {
                            Log.e(TAG, "API call failed: " + response.code());
                            result.postValue(null);
                        }
                    }
                    
                    @Override
                    public void onFailure(Call<List<Channel>> call, Throwable t) {
                        Log.e(TAG, "API call failed", t);
                        result.postValue(null);
                    }
                });
            } catch (Exception e) {
                Log.e(TAG, "Error fetching channels", e);
                result.postValue(null);
            }
        });
    }
    
    // Search movies with caching
    public LiveData<List<CachedPoster>> searchMovies(String query) {
        return repository.searchPosters(query);
    }
    
    // Get movie by ID with caching
    public LiveData<CachedPoster> getMovieById(int id) {
        return repository.getPosterById(id);
    }
    
    // Get channel by ID with caching
    public LiveData<CachedChannel> getChannelById(int id) {
        return repository.getChannelById(id);
    }
    
    // Convert API Poster to CachedPoster
    private List<CachedPoster> convertToCachedPosters(List<Poster> posters) {
        // This is a simplified conversion - you would need to map all fields
        List<CachedPoster> cachedPosters = new java.util.ArrayList<>();
        
        for (Poster poster : posters) {
            CachedPoster cachedPoster = new CachedPoster();
            cachedPoster.setId(poster.getId());
            cachedPoster.setTitle(poster.getTitle());
            cachedPoster.setDescription(poster.getDescription());
            cachedPoster.setImage(poster.getImage());
            cachedPoster.setBackdrop(poster.getBackdrop());
            cachedPoster.setType(poster.getType());
            cachedPoster.setYear(poster.getYear());
            cachedPoster.setDuration(poster.getDuration());
            cachedPoster.setRating(poster.getRating());
            cachedPoster.setTrailer(poster.getTrailer());
            cachedPoster.setSource(poster.getSource());
            cachedPoster.setSubtitle(poster.getSubtitle());
            cachedPoster.setQuality(poster.getQuality());
            cachedPoster.setSize(poster.getSize());
            cachedPoster.setLanguage(poster.getLanguage());
            cachedPoster.setCountry(poster.getCountry());
            cachedPoster.setGenre(poster.getGenre());
            cachedPoster.setCast(poster.getCast());
            cachedPoster.setDirector(poster.getDirector());
            cachedPoster.setWriter(poster.getWriter());
            cachedPoster.setProducer(poster.getProducer());
            cachedPoster.setMusic(poster.getMusic());
            cachedPoster.setCinematography(poster.getCinematography());
            cachedPoster.setEditing(poster.getEditing());
            cachedPoster.setArt_direction(poster.getArt_direction());
            cachedPoster.setCostume_design(poster.getCostume_design());
            cachedPoster.setMakeup(poster.getMakeup());
            cachedPoster.setSound(poster.getSound());
            cachedPoster.setVisual_effects(poster.getVisual_effects());
            cachedPoster.setStunts(poster.getStunts());
            cachedPoster.setCamera(poster.getCamera());
            cachedPoster.setLighting(poster.getLighting());
            cachedPoster.setProduction_design(poster.getProduction_design());
            cachedPoster.setAnimation(poster.getAnimation());
            cachedPoster.setColor(poster.getColor());
            cachedPoster.setAspect_ratio(poster.getAspect_ratio());
            cachedPoster.setRuntime(poster.getRuntime());
            cachedPoster.setBudget(poster.getBudget());
            cachedPoster.setRevenue(poster.getRevenue());
            cachedPoster.setWebsite(poster.getWebsite());
            cachedPoster.setImdb(poster.getImdb());
            cachedPoster.setRotten_tomatoes(poster.getRotten_tomatoes());
            cachedPoster.setMetacritic(poster.getMetacritic());
            cachedPoster.setAwards(poster.getAwards());
            cachedPoster.setNominations(poster.getNominations());
            cachedPoster.setWins(poster.getWins());
            cachedPoster.setBox_office(poster.getBox_office());
            cachedPoster.setProduction_company(poster.getProduction_company());
            cachedPoster.setDistributor(poster.getDistributor());
            cachedPoster.setRelease_date(poster.getRelease_date());
            cachedPoster.setDvd_release(poster.getDvd_release());
            cachedPoster.setBluray_release(poster.getBluray_release());
            cachedPoster.setStreaming_release(poster.getStreaming_release());
            cachedPoster.setTv_release(poster.getTv_release());
            cachedPoster.setFestival_release(poster.getFestival_release());
            cachedPoster.setLimited_release(poster.getLimited_release());
            cachedPoster.setWide_release(poster.getWide_release());
            cachedPoster.setInternational_release(poster.getInternational_release());
            cachedPoster.setHome_video_release(poster.getHome_video_release());
            cachedPoster.setDigital_release(poster.getDigital_release());
            cachedPoster.setPhysical_release(poster.getPhysical_release());
            cachedPoster.setTheatrical_release(poster.getTheatrical_release());
            cachedPoster.setExtended_release(poster.getExtended_release());
            cachedPoster.setDirector_cut(poster.getDirector_cut());
            cachedPoster.setUnrated(poster.getUnrated());
            cachedPoster.setRated(poster.getRated());
            cachedPoster.setMpaa_rating(poster.getMpaa_rating());
            cachedPoster.setContent_rating(poster.getContent_rating());
            cachedPoster.setAge_rating(poster.getAge_rating());
            cachedPoster.setParental_guidance(poster.getParental_guidance());
            cachedPoster.setViewer_discretion(poster.getViewer_discretion());
            cachedPoster.setContent_warning(poster.getContent_warning());
            cachedPoster.setTrigger_warning(poster.getTrigger_warning());
            cachedPoster.setSensitive_content(poster.getSensitive_content());
            cachedPoster.setMature_content(poster.getMature_content());
            cachedPoster.setAdult_content(poster.getAdult_content());
            cachedPoster.setExplicit_content(poster.getExplicit_content());
            cachedPoster.setGraphic_content(poster.getGraphic_content());
            cachedPoster.setViolent_content(poster.getViolent_content());
            cachedPoster.setSexual_content(poster.getSexual_content());
            cachedPoster.setLanguage_content(poster.getLanguage_content());
            cachedPoster.setDrug_content(poster.getDrug_content());
            cachedPoster.setAlcohol_content(poster.getAlcohol_content());
            cachedPoster.setSmoking_content(poster.getSmoking_content());
            cachedPoster.setGambling_content(poster.getGambling_content());
            cachedPoster.setSelf_harm_content(poster.getSelf_harm_content());
            cachedPoster.setSuicide_content(poster.getSuicide_content());
            cachedPoster.setEating_disorder_content(poster.getEating_disorder_content());
            cachedPoster.setMental_health_content(poster.getMental_health_content());
            cachedPoster.setPhysical_health_content(poster.getPhysical_health_content());
            cachedPoster.setMedical_content(poster.getMedical_content());
            cachedPoster.setPolitical_content(poster.getPolitical_content());
            cachedPoster.setReligious_content(poster.getReligious_content());
            cachedPoster.setCultural_content(poster.getCultural_content());
            cachedPoster.setHistorical_content(poster.getHistorical_content());
            cachedPoster.setEducational_content(poster.getEducational_content());
            cachedPoster.setDocumentary_content(poster.getDocumentary_content());
            cachedPoster.setReality_content(poster.getReality_content());
            cachedPoster.setGame_show_content(poster.getGame_show_content());
            cachedPoster.setTalk_show_content(poster.getTalk_show_content());
            cachedPoster.setVariety_content(poster.getVariety_content());
            cachedPoster.setNews_content(poster.getNews_content());
            cachedPoster.setSports_content(poster.getSports_content());
            cachedPoster.setMusic_content(poster.getMusic_content());
            cachedPoster.setComedy_content(poster.getComedy_content());
            cachedPoster.setDrama_content(poster.getDrama_content());
            cachedPoster.setAction_content(poster.getAction_content());
            cachedPoster.setAdventure_content(poster.getAdventure_content());
            cachedPoster.setAnimation_content(poster.getAnimation_content());
            cachedPoster.setBiography_content(poster.getBiography_content());
            cachedPoster.setCrime_content(poster.getCrime_content());
            cachedPoster.setFamily_content(poster.getFamily_content());
            cachedPoster.setFantasy_content(poster.getFantasy_content());
            cachedPoster.setFilm_noir_content(poster.getFilm_noir_content());
            cachedPoster.setHistory_content(poster.getHistory_content());
            cachedPoster.setHorror_content(poster.getHorror_content());
            cachedPoster.setMystery_content(poster.getMystery_content());
            cachedPoster.setRomance_content(poster.getRomance_content());
            cachedPoster.setSci_fi_content(poster.getSci_fi_content());
            cachedPoster.setThriller_content(poster.getThriller_content());
            cachedPoster.setWar_content(poster.getWar_content());
            cachedPoster.setWestern_content(poster.getWestern_content());
            cachedPoster.setMusical_content(poster.getMusical_content());
            cachedPoster.setSport_content(poster.getSport_content());
            cachedPoster.setSuperhero_content(poster.getSuperhero_content());
            cachedPoster.setMartial_arts_content(poster.getMartial_arts_content());
            cachedPoster.setSpy_content(poster.getSpy_content());
            cachedPoster.setHeist_content(poster.getHeist_content());
            cachedPoster.setCourtroom_content(poster.getCourtroom_content());
            cachedPoster.setMedical_content_2(poster.getMedical_content_2());
            cachedPoster.setPolitical_content_2(poster.getPolitical_content_2());
            cachedPoster.setReligious_content_2(poster.getReligious_content_2());
            cachedPoster.setCultural_content_2(poster.getCultural_content_2());
            cachedPoster.setHistorical_content_2(poster.getHistorical_content_2());
            cachedPoster.setEducational_content_2(poster.getEducational_content_2());
            cachedPoster.setDocumentary_content_2(poster.getDocumentary_content_2());
            cachedPoster.setReality_content_2(poster.getReality_content_2());
            cachedPoster.setGame_show_content_2(poster.getGame_show_content_2());
            cachedPoster.setTalk_show_content_2(poster.getTalk_show_content_2());
            cachedPoster.setVariety_content_2(poster.getVariety_content_2());
            cachedPoster.setNews_content_2(poster.getNews_content_2());
            cachedPoster.setSports_content_2(poster.getSports_content_2());
            cachedPoster.setMusic_content_2(poster.getMusic_content_2());
            cachedPoster.setComedy_content_2(poster.getComedy_content_2());
            cachedPoster.setDrama_content_2(poster.getDrama_content_2());
            cachedPoster.setAction_content_2(poster.getAction_content_2());
            cachedPoster.setAdventure_content_2(poster.getAdventure_content_2());
            cachedPoster.setAnimation_content_2(poster.getAnimation_content_2());
            cachedPoster.setBiography_content_2(poster.getBiography_content_2());
            cachedPoster.setCrime_content_2(poster.getCrime_content_2());
            cachedPoster.setFamily_content_2(poster.getFamily_content_2());
            cachedPoster.setFantasy_content_2(poster.getFantasy_content_2());
            cachedPoster.setFilm_noir_content_2(poster.getFilm_noir_content_2());
            cachedPoster.setHistory_content_2(poster.getHistory_content_2());
            cachedPoster.setHorror_content_2(poster.getHorror_content_2());
            cachedPoster.setMystery_content_2(poster.getMystery_content_2());
            cachedPoster.setRomance_content_2(poster.getRomance_content_2());
            cachedPoster.setSci_fi_content_2(poster.getSci_fi_content_2());
            cachedPoster.setThriller_content_2(poster.getThriller_content_2());
            cachedPoster.setWar_content_2(poster.getWar_content_2());
            cachedPoster.setWestern_content_2(poster.getWestern_content_2());
            cachedPoster.setMusical_content_2(poster.getMusical_content_2());
            cachedPoster.setSport_content_2(poster.getSport_content_2());
            cachedPoster.setSuperhero_content_2(poster.getSuperhero_content_2());
            cachedPoster.setMartial_arts_content_2(poster.getMartial_arts_content_2());
            cachedPoster.setSpy_content_2(poster.getSpy_content_2());
            cachedPoster.setHeist_content_2(poster.getHeist_content_2());
            cachedPoster.setCourtroom_content_2(poster.getCourtroom_content_2());
            cachedPoster.setLastUpdated(System.currentTimeMillis());
            
            cachedPosters.add(cachedPoster);
        }
        
        return cachedPosters;
    }
    
    // Convert API Channel to CachedChannel
    private List<CachedChannel> convertToCachedChannels(List<Channel> channels) {
        // This is a simplified conversion - you would need to map all fields
        List<CachedChannel> cachedChannels = new java.util.ArrayList<>();
        
        for (Channel channel : channels) {
            CachedChannel cachedChannel = new CachedChannel();
            cachedChannel.setId(channel.getId());
            cachedChannel.setName(channel.getName());
            cachedChannel.setDescription(channel.getDescription());
            cachedChannel.setImage(channel.getImage());
            cachedChannel.setBackdrop(channel.getBackdrop());
            cachedChannel.setType(channel.getType());
            cachedChannel.setCategory(channel.getCategory());
            cachedChannel.setCountry(channel.getCountry());
            cachedChannel.setLanguage(channel.getLanguage());
            cachedChannel.setQuality(channel.getQuality());
            cachedChannel.setSource(channel.getSource());
            cachedChannel.setSubtitle(channel.getSubtitle());
            cachedChannel.setStream_url(channel.getStream_url());
            cachedChannel.setStream_key(channel.getStream_key());
            cachedChannel.setStream_type(channel.getStream_type());
            cachedChannel.setStream_quality(channel.getStream_quality());
            cachedChannel.setStream_language(channel.getStream_language());
            cachedChannel.setStream_subtitle(channel.getStream_subtitle());
            cachedChannel.setStream_audio(channel.getStream_audio());
            cachedChannel.setStream_video(channel.getStream_video());
            cachedChannel.setStream_resolution(channel.getStream_resolution());
            cachedChannel.setStream_bitrate(channel.getStream_bitrate());
            cachedChannel.setStream_fps(channel.getStream_fps());
            cachedChannel.setStream_codec(channel.getStream_codec());
            cachedChannel.setStream_container(channel.getStream_container());
            cachedChannel.setStream_protocol(channel.getStream_protocol());
            cachedChannel.setStream_server(channel.getStream_server());
            cachedChannel.setStream_port(channel.getStream_port());
            cachedChannel.setStream_path(channel.getStream_path());
            cachedChannel.setStream_username(channel.getStream_username());
            cachedChannel.setStream_password(channel.getStream_password());
            cachedChannel.setStream_token(channel.getStream_token());
            cachedChannel.setStream_expires(channel.getStream_expires());
            cachedChannel.setStream_geo(channel.getStream_geo());
            cachedChannel.setStream_region(channel.getStream_region());
            cachedChannel.setStream_timezone(channel.getStream_timezone());
            cachedChannel.setStream_schedule(channel.getStream_schedule());
            cachedChannel.setStream_status(channel.getStream_status());
            cachedChannel.setStream_viewers(channel.getStream_viewers());
            cachedChannel.setStream_rating(channel.getStream_rating());
            cachedChannel.setStream_votes(channel.getStream_votes());
            cachedChannel.setStream_comments(channel.getStream_comments());
            cachedChannel.setStream_shares(channel.getStream_shares());
            cachedChannel.setStream_downloads(channel.getStream_downloads());
            cachedChannel.setStream_views(channel.getStream_views());
            cachedChannel.setStream_likes(channel.getStream_likes());
            cachedChannel.setStream_dislikes(channel.getStream_dislikes());
            cachedChannel.setStream_favorites(channel.getStream_favorites());
            cachedChannel.setStream_playlists(channel.getStream_playlists());
            cachedChannel.setStream_tags(channel.getStream_tags());
            cachedChannel.setStream_genres(channel.getStream_genres());
            cachedChannel.setStream_cast(channel.getStream_cast());
            cachedChannel.setStream_crew(channel.getStream_crew());
            cachedChannel.setStream_director(channel.getStream_director());
            cachedChannel.setStream_producer(channel.getStream_producer());
            cachedChannel.setStream_writer(channel.getStream_writer());
            cachedChannel.setStream_music(channel.getStream_music());
            cachedChannel.setStream_cinematography(channel.getStream_cinematography());
            cachedChannel.setStream_editing(channel.getStream_editing());
            cachedChannel.setStream_art_direction(channel.getStream_art_direction());
            cachedChannel.setStream_costume_design(channel.getStream_costume_design());
            cachedChannel.setStream_makeup(channel.getStream_makeup());
            cachedChannel.setStream_sound(channel.getStream_sound());
            cachedChannel.setStream_visual_effects(channel.getStream_visual_effects());
            cachedChannel.setStream_stunts(channel.getStream_stunts());
            cachedChannel.setStream_camera(channel.getStream_camera());
            cachedChannel.setStream_lighting(channel.getStream_lighting());
            cachedChannel.setStream_production_design(channel.getStream_production_design());
            cachedChannel.setStream_animation(channel.getStream_animation());
            cachedChannel.setStream_color(channel.getStream_color());
            cachedChannel.setStream_aspect_ratio(channel.getStream_aspect_ratio());
            cachedChannel.setStream_runtime(channel.getStream_runtime());
            cachedChannel.setStream_budget(channel.getStream_budget());
            cachedChannel.setStream_revenue(channel.getStream_revenue());
            cachedChannel.setStream_website(channel.getStream_website());
            cachedChannel.setStream_imdb(channel.getStream_imdb());
            cachedChannel.setStream_rotten_tomatoes(channel.getStream_rotten_tomatoes());
            cachedChannel.setStream_metacritic(channel.getStream_metacritic());
            cachedChannel.setStream_awards(channel.getStream_awards());
            cachedChannel.setStream_nominations(channel.getStream_nominations());
            cachedChannel.setStream_wins(channel.getStream_wins());
            cachedChannel.setStream_box_office(channel.getStream_box_office());
            cachedChannel.setStream_production_company(channel.getStream_production_company());
            cachedChannel.setStream_distributor(channel.getStream_distributor());
            cachedChannel.setStream_release_date(channel.getStream_release_date());
            cachedChannel.setStream_dvd_release(channel.getStream_dvd_release());
            cachedChannel.setStream_bluray_release(channel.getStream_bluray_release());
            cachedChannel.setStream_streaming_release(channel.getStream_streaming_release());
            cachedChannel.setStream_tv_release(channel.getStream_tv_release());
            cachedChannel.setStream_festival_release(channel.getStream_festival_release());
            cachedChannel.setStream_limited_release(channel.getStream_limited_release());
            cachedChannel.setStream_wide_release(channel.getStream_wide_release());
            cachedChannel.setStream_international_release(channel.getStream_international_release());
            cachedChannel.setStream_home_video_release(channel.getStream_home_video_release());
            cachedChannel.setStream_digital_release(channel.getStream_digital_release());
            cachedChannel.setStream_physical_release(channel.getStream_physical_release());
            cachedChannel.setStream_theatrical_release(channel.getStream_theatrical_release());
            cachedChannel.setStream_extended_release(channel.getStream_extended_release());
            cachedChannel.setStream_director_cut(channel.getStream_director_cut());
            cachedChannel.setStream_unrated(channel.getStream_unrated());
            cachedChannel.setStream_rated(channel.getStream_rated());
            cachedChannel.setStream_mpaa_rating(channel.getStream_mpaa_rating());
            cachedChannel.setStream_content_rating(channel.getStream_content_rating());
            cachedChannel.setStream_age_rating(channel.getStream_age_rating());
            cachedChannel.setStream_parental_guidance(channel.getStream_parental_guidance());
            cachedChannel.setStream_viewer_discretion(channel.getStream_viewer_discretion());
            cachedChannel.setStream_content_warning(channel.getStream_content_warning());
            cachedChannel.setStream_trigger_warning(channel.getStream_trigger_warning());
            cachedChannel.setStream_sensitive_content(channel.getStream_sensitive_content());
            cachedChannel.setStream_mature_content(channel.getStream_mature_content());
            cachedChannel.setStream_adult_content(channel.getStream_adult_content());
            cachedChannel.setStream_explicit_content(channel.getStream_explicit_content());
            cachedChannel.setStream_graphic_content(channel.getStream_graphic_content());
            cachedChannel.setStream_violent_content(channel.getStream_violent_content());
            cachedChannel.setStream_sexual_content(channel.getStream_sexual_content());
            cachedChannel.setStream_language_content(channel.getStream_language_content());
            cachedChannel.setStream_drug_content(channel.getStream_drug_content());
            cachedChannel.setStream_alcohol_content(channel.getStream_alcohol_content());
            cachedChannel.setStream_smoking_content(channel.getStream_smoking_content());
            cachedChannel.setStream_gambling_content(channel.getStream_gambling_content());
            cachedChannel.setStream_self_harm_content(channel.getStream_self_harm_content());
            cachedChannel.setStream_suicide_content(channel.getStream_suicide_content());
            cachedChannel.setStream_eating_disorder_content(channel.getStream_eating_disorder_content());
            cachedChannel.setStream_mental_health_content(channel.getStream_mental_health_content());
            cachedChannel.setStream_physical_health_content(channel.getStream_physical_health_content());
            cachedChannel.setStream_medical_content(channel.getStream_medical_content());
            cachedChannel.setStream_political_content(channel.getStream_political_content());
            cachedChannel.setStream_religious_content(channel.getStream_religious_content());
            cachedChannel.setStream_cultural_content(channel.getStream_cultural_content());
            cachedChannel.setStream_historical_content(channel.getStream_historical_content());
            cachedChannel.setStream_educational_content(channel.getStream_educational_content());
            cachedChannel.setStream_documentary_content(channel.getStream_documentary_content());
            cachedChannel.setStream_reality_content(channel.getStream_reality_content());
            cachedChannel.setStream_game_show_content(channel.getStream_game_show_content());
            cachedChannel.setStream_talk_show_content(channel.getStream_talk_show_content());
            cachedChannel.setStream_variety_content(channel.getStream_variety_content());
            cachedChannel.setStream_news_content(channel.getStream_news_content());
            cachedChannel.setStream_sports_content(channel.getStream_sports_content());
            cachedChannel.setStream_music_content(channel.getStream_music_content());
            cachedChannel.setStream_comedy_content(channel.getStream_comedy_content());
            cachedChannel.setStream_drama_content(channel.getStream_drama_content());
            cachedChannel.setStream_action_content(channel.getStream_action_content());
            cachedChannel.setStream_adventure_content(channel.getStream_adventure_content());
            cachedChannel.setStream_animation_content(channel.getStream_animation_content());
            cachedChannel.setStream_biography_content(channel.getStream_biography_content());
            cachedChannel.setStream_crime_content(channel.getStream_crime_content());
            cachedChannel.setStream_family_content(channel.getStream_family_content());
            cachedChannel.setStream_fantasy_content(channel.getStream_fantasy_content());
            cachedChannel.setStream_film_noir_content(channel.getStream_film_noir_content());
            cachedChannel.setStream_history_content(channel.getStream_history_content());
            cachedChannel.setStream_horror_content(channel.getStream_horror_content());
            cachedChannel.setStream_mystery_content(channel.getStream_mystery_content());
            cachedChannel.setStream_romance_content(channel.getStream_romance_content());
            cachedChannel.setStream_sci_fi_content(channel.getStream_sci_fi_content());
            cachedChannel.setStream_thriller_content(channel.getStream_thriller_content());
            cachedChannel.setStream_war_content(channel.getStream_war_content());
            cachedChannel.setStream_western_content(channel.getStream_western_content());
            cachedChannel.setStream_musical_content(channel.getStream_musical_content());
            cachedChannel.setStream_sport_content(channel.getStream_sport_content());
            cachedChannel.setStream_superhero_content(channel.getStream_superhero_content());
            cachedChannel.setStream_martial_arts_content(channel.getStream_martial_arts_content());
            cachedChannel.setStream_spy_content(channel.getStream_spy_content());
            cachedChannel.setStream_heist_content(channel.getStream_heist_content());
            cachedChannel.setStream_courtroom_content(channel.getStream_courtroom_content());
            cachedChannel.setLastUpdated(System.currentTimeMillis());
            
            cachedChannels.add(cachedChannel);
        }
        
        return cachedChannels;
    }
    
    // Cleanup old data
    public void cleanupOldData() {
        long cutoffTime = System.currentTimeMillis() - (24 * 60 * 60 * 1000); // 24 hours ago
        repository.deleteOldPosters(cutoffTime);
        repository.deleteOldChannels(cutoffTime);
    }
    
    // Shutdown service
    public void shutdown() {
        repository.cleanup();
        executorService.shutdown();
    }
}