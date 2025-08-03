package my.cinemax.app.free.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_channels")
public class CachedChannel {
    @PrimaryKey
    private int id;
    private String name;
    private String description;
    private String image;
    private String backdrop;
    private String type;
    private String category;
    private String country;
    private String language;
    private String quality;
    private String source;
    private String subtitle;
    private String stream_url;
    private String stream_key;
    private String stream_type;
    private String stream_quality;
    private String stream_language;
    private String stream_subtitle;
    private String stream_audio;
    private String stream_video;
    private String stream_resolution;
    private String stream_bitrate;
    private String stream_fps;
    private String stream_codec;
    private String stream_container;
    private String stream_protocol;
    private String stream_server;
    private String stream_port;
    private String stream_path;
    private String stream_username;
    private String stream_password;
    private String stream_token;
    private String stream_expires;
    private String stream_geo;
    private String stream_region;
    private String stream_timezone;
    private String stream_schedule;
    private String stream_status;
    private String stream_viewers;
    private String stream_rating;
    private String stream_votes;
    private String stream_comments;
    private String stream_shares;
    private String stream_downloads;
    private String stream_views;
    private String stream_likes;
    private String stream_dislikes;
    private String stream_favorites;
    private String stream_playlists;
    private String stream_tags;
    private String stream_genres;
    private String stream_cast;
    private String stream_crew;
    private String stream_director;
    private String stream_producer;
    private String stream_writer;
    private String stream_music;
    private String stream_cinematography;
    private String stream_editing;
    private String stream_art_direction;
    private String stream_costume_design;
    private String stream_makeup;
    private String stream_sound;
    private String stream_visual_effects;
    private String stream_stunts;
    private String stream_camera;
    private String stream_lighting;
    private String stream_production_design;
    private String stream_animation;
    private String stream_color;
    private String stream_aspect_ratio;
    private String stream_runtime;
    private String stream_budget;
    private String stream_revenue;
    private String stream_website;
    private String stream_imdb;
    private String stream_rotten_tomatoes;
    private String stream_metacritic;
    private String stream_awards;
    private String stream_nominations;
    private String stream_wins;
    private String stream_box_office;
    private String stream_production_company;
    private String stream_distributor;
    private String stream_release_date;
    private String stream_dvd_release;
    private String stream_bluray_release;
    private String stream_streaming_release;
    private String stream_tv_release;
    private String stream_festival_release;
    private String stream_limited_release;
    private String stream_wide_release;
    private String stream_international_release;
    private String stream_home_video_release;
    private String stream_digital_release;
    private String stream_physical_release;
    private String stream_theatrical_release;
    private String stream_extended_release;
    private String stream_director_cut;
    private String stream_unrated;
    private String stream_rated;
    private String stream_mpaa_rating;
    private String stream_content_rating;
    private String stream_age_rating;
    private String stream_parental_guidance;
    private String stream_viewer_discretion;
    private String stream_content_warning;
    private String stream_trigger_warning;
    private String stream_sensitive_content;
    private String stream_mature_content;
    private String stream_adult_content;
    private String stream_explicit_content;
    private String stream_graphic_content;
    private String stream_violent_content;
    private String stream_sexual_content;
    private String stream_language_content;
    private String stream_drug_content;
    private String stream_alcohol_content;
    private String stream_smoking_content;
    private String stream_gambling_content;
    private String stream_self_harm_content;
    private String stream_suicide_content;
    private String stream_eating_disorder_content;
    private String stream_mental_health_content;
    private String stream_physical_health_content;
    private String stream_medical_content;
    private String stream_political_content;
    private String stream_religious_content;
    private String stream_cultural_content;
    private String stream_historical_content;
    private String stream_educational_content;
    private String stream_documentary_content;
    private String stream_reality_content;
    private String stream_game_show_content;
    private String stream_talk_show_content;
    private String stream_variety_content;
    private String stream_news_content;
    private String stream_sports_content;
    private String stream_music_content;
    private String stream_comedy_content;
    private String stream_drama_content;
    private String stream_action_content;
    private String stream_adventure_content;
    private String stream_animation_content;
    private String stream_biography_content;
    private String stream_crime_content;
    private String stream_family_content;
    private String stream_fantasy_content;
    private String stream_film_noir_content;
    private String stream_history_content;
    private String stream_horror_content;
    private String stream_mystery_content;
    private String stream_romance_content;
    private String stream_sci_fi_content;
    private String stream_thriller_content;
    private String stream_war_content;
    private String stream_western_content;
    private String stream_musical_content;
    private String stream_sport_content;
    private String stream_superhero_content;
    private String stream_martial_arts_content;
    private String stream_spy_content;
    private String stream_heist_content;
    private String stream_courtroom_content;
    private long lastUpdated;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public String getBackdrop() { return backdrop; }
    public void setBackdrop(String backdrop) { this.backdrop = backdrop; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    
    public String getStream_url() { return stream_url; }
    public void setStream_url(String stream_url) { this.stream_url = stream_url; }
    
    public String getStream_key() { return stream_key; }
    public void setStream_key(String stream_key) { this.stream_key = stream_key; }
    
    public String getStream_type() { return stream_type; }
    public void setStream_type(String stream_type) { this.stream_type = stream_type; }
    
    public String getStream_quality() { return stream_quality; }
    public void setStream_quality(String stream_quality) { this.stream_quality = stream_quality; }
    
    public String getStream_language() { return stream_language; }
    public void setStream_language(String stream_language) { this.stream_language = stream_language; }
    
    public String getStream_subtitle() { return stream_subtitle; }
    public void setStream_subtitle(String stream_subtitle) { this.stream_subtitle = stream_subtitle; }
    
    public String getStream_audio() { return stream_audio; }
    public void setStream_audio(String stream_audio) { this.stream_audio = stream_audio; }
    
    public String getStream_video() { return stream_video; }
    public void setStream_video(String stream_video) { this.stream_video = stream_video; }
    
    public String getStream_resolution() { return stream_resolution; }
    public void setStream_resolution(String stream_resolution) { this.stream_resolution = stream_resolution; }
    
    public String getStream_bitrate() { return stream_bitrate; }
    public void setStream_bitrate(String stream_bitrate) { this.stream_bitrate = stream_bitrate; }
    
    public String getStream_fps() { return stream_fps; }
    public void setStream_fps(String stream_fps) { this.stream_fps = stream_fps; }
    
    public String getStream_codec() { return stream_codec; }
    public void setStream_codec(String stream_codec) { this.stream_codec = stream_codec; }
    
    public String getStream_container() { return stream_container; }
    public void setStream_container(String stream_container) { this.stream_container = stream_container; }
    
    public String getStream_protocol() { return stream_protocol; }
    public void setStream_protocol(String stream_protocol) { this.stream_protocol = stream_protocol; }
    
    public String getStream_server() { return stream_server; }
    public void setStream_server(String stream_server) { this.stream_server = stream_server; }
    
    public String getStream_port() { return stream_port; }
    public void setStream_port(String stream_port) { this.stream_port = stream_port; }
    
    public String getStream_path() { return stream_path; }
    public void setStream_path(String stream_path) { this.stream_path = stream_path; }
    
    public String getStream_username() { return stream_username; }
    public void setStream_username(String stream_username) { this.stream_username = stream_username; }
    
    public String getStream_password() { return stream_password; }
    public void setStream_password(String stream_password) { this.stream_password = stream_password; }
    
    public String getStream_token() { return stream_token; }
    public void setStream_token(String stream_token) { this.stream_token = stream_token; }
    
    public String getStream_expires() { return stream_expires; }
    public void setStream_expires(String stream_expires) { this.stream_expires = stream_expires; }
    
    public String getStream_geo() { return stream_geo; }
    public void setStream_geo(String stream_geo) { this.stream_geo = stream_geo; }
    
    public String getStream_region() { return stream_region; }
    public void setStream_region(String stream_region) { this.stream_region = stream_region; }
    
    public String getStream_timezone() { return stream_timezone; }
    public void setStream_timezone(String stream_timezone) { this.stream_timezone = stream_timezone; }
    
    public String getStream_schedule() { return stream_schedule; }
    public void setStream_schedule(String stream_schedule) { this.stream_schedule = stream_schedule; }
    
    public String getStream_status() { return stream_status; }
    public void setStream_status(String stream_status) { this.stream_status = stream_status; }
    
    public String getStream_viewers() { return stream_viewers; }
    public void setStream_viewers(String stream_viewers) { this.stream_viewers = stream_viewers; }
    
    public String getStream_rating() { return stream_rating; }
    public void setStream_rating(String stream_rating) { this.stream_rating = stream_rating; }
    
    public String getStream_votes() { return stream_votes; }
    public void setStream_votes(String stream_votes) { this.stream_votes = stream_votes; }
    
    public String getStream_comments() { return stream_comments; }
    public void setStream_comments(String stream_comments) { this.stream_comments = stream_comments; }
    
    public String getStream_shares() { return stream_shares; }
    public void setStream_shares(String stream_shares) { this.stream_shares = stream_shares; }
    
    public String getStream_downloads() { return stream_downloads; }
    public void setStream_downloads(String stream_downloads) { this.stream_downloads = stream_downloads; }
    
    public String getStream_views() { return stream_views; }
    public void setStream_views(String stream_views) { this.stream_views = stream_views; }
    
    public String getStream_likes() { return stream_likes; }
    public void setStream_likes(String stream_likes) { this.stream_likes = stream_likes; }
    
    public String getStream_dislikes() { return stream_dislikes; }
    public void setStream_dislikes(String stream_dislikes) { this.stream_dislikes = stream_dislikes; }
    
    public String getStream_favorites() { return stream_favorites; }
    public void setStream_favorites(String stream_favorites) { this.stream_favorites = stream_favorites; }
    
    public String getStream_playlists() { return stream_playlists; }
    public void setStream_playlists(String stream_playlists) { this.stream_playlists = stream_playlists; }
    
    public String getStream_tags() { return stream_tags; }
    public void setStream_tags(String stream_tags) { this.stream_tags = stream_tags; }
    
    public String getStream_genres() { return stream_genres; }
    public void setStream_genres(String stream_genres) { this.stream_genres = stream_genres; }
    
    public String getStream_cast() { return stream_cast; }
    public void setStream_cast(String stream_cast) { this.stream_cast = stream_cast; }
    
    public String getStream_crew() { return stream_crew; }
    public void setStream_crew(String stream_crew) { this.stream_crew = stream_crew; }
    
    public String getStream_director() { return stream_director; }
    public void setStream_director(String stream_director) { this.stream_director = stream_director; }
    
    public String getStream_producer() { return stream_producer; }
    public void setStream_producer(String stream_producer) { this.stream_producer = stream_producer; }
    
    public String getStream_writer() { return stream_writer; }
    public void setStream_writer(String stream_writer) { this.stream_writer = stream_writer; }
    
    public String getStream_music() { return stream_music; }
    public void setStream_music(String stream_music) { this.stream_music = stream_music; }
    
    public String getStream_cinematography() { return stream_cinematography; }
    public void setStream_cinematography(String stream_cinematography) { this.stream_cinematography = stream_cinematography; }
    
    public String getStream_editing() { return stream_editing; }
    public void setStream_editing(String stream_editing) { this.stream_editing = stream_editing; }
    
    public String getStream_art_direction() { return stream_art_direction; }
    public void setStream_art_direction(String stream_art_direction) { this.stream_art_direction = stream_art_direction; }
    
    public String getStream_costume_design() { return stream_costume_design; }
    public void setStream_costume_design(String stream_costume_design) { this.stream_costume_design = stream_costume_design; }
    
    public String getStream_makeup() { return stream_makeup; }
    public void setStream_makeup(String stream_makeup) { this.stream_makeup = stream_makeup; }
    
    public String getStream_sound() { return stream_sound; }
    public void setStream_sound(String stream_sound) { this.stream_sound = stream_sound; }
    
    public String getStream_visual_effects() { return stream_visual_effects; }
    public void setStream_visual_effects(String stream_visual_effects) { this.stream_visual_effects = stream_visual_effects; }
    
    public String getStream_stunts() { return stream_stunts; }
    public void setStream_stunts(String stream_stunts) { this.stream_stunts = stream_stunts; }
    
    public String getStream_camera() { return stream_camera; }
    public void setStream_camera(String stream_camera) { this.stream_camera = stream_camera; }
    
    public String getStream_lighting() { return stream_lighting; }
    public void setStream_lighting(String stream_lighting) { this.stream_lighting = stream_lighting; }
    
    public String getStream_production_design() { return stream_production_design; }
    public void setStream_production_design(String stream_production_design) { this.stream_production_design = stream_production_design; }
    
    public String getStream_animation() { return stream_animation; }
    public void setStream_animation(String stream_animation) { this.stream_animation = stream_animation; }
    
    public String getStream_color() { return stream_color; }
    public void setStream_color(String stream_color) { this.stream_color = stream_color; }
    
    public String getStream_aspect_ratio() { return stream_aspect_ratio; }
    public void setStream_aspect_ratio(String stream_aspect_ratio) { this.stream_aspect_ratio = stream_aspect_ratio; }
    
    public String getStream_runtime() { return stream_runtime; }
    public void setStream_runtime(String stream_runtime) { this.stream_runtime = stream_runtime; }
    
    public String getStream_budget() { return stream_budget; }
    public void setStream_budget(String stream_budget) { this.stream_budget = stream_budget; }
    
    public String getStream_revenue() { return stream_revenue; }
    public void setStream_revenue(String stream_revenue) { this.stream_revenue = stream_revenue; }
    
    public String getStream_website() { return stream_website; }
    public void setStream_website(String stream_website) { this.stream_website = stream_website; }
    
    public String getStream_imdb() { return stream_imdb; }
    public void setStream_imdb(String stream_imdb) { this.stream_imdb = stream_imdb; }
    
    public String getStream_rotten_tomatoes() { return stream_rotten_tomatoes; }
    public void setStream_rotten_tomatoes(String stream_rotten_tomatoes) { this.stream_rotten_tomatoes = stream_rotten_tomatoes; }
    
    public String getStream_metacritic() { return stream_metacritic; }
    public void setStream_metacritic(String stream_metacritic) { this.stream_metacritic = stream_metacritic; }
    
    public String getStream_awards() { return stream_awards; }
    public void setStream_awards(String stream_awards) { this.stream_awards = stream_awards; }
    
    public String getStream_nominations() { return stream_nominations; }
    public void setStream_nominations(String stream_nominations) { this.stream_nominations = stream_nominations; }
    
    public String getStream_wins() { return stream_wins; }
    public void setStream_wins(String stream_wins) { this.stream_wins = stream_wins; }
    
    public String getStream_box_office() { return stream_box_office; }
    public void setStream_box_office(String stream_box_office) { this.stream_box_office = stream_box_office; }
    
    public String getStream_production_company() { return stream_production_company; }
    public void setStream_production_company(String stream_production_company) { this.stream_production_company = stream_production_company; }
    
    public String getStream_distributor() { return stream_distributor; }
    public void setStream_distributor(String stream_distributor) { this.stream_distributor = stream_distributor; }
    
    public String getStream_release_date() { return stream_release_date; }
    public void setStream_release_date(String stream_release_date) { this.stream_release_date = stream_release_date; }
    
    public String getStream_dvd_release() { return stream_dvd_release; }
    public void setStream_dvd_release(String stream_dvd_release) { this.stream_dvd_release = stream_dvd_release; }
    
    public String getStream_bluray_release() { return stream_bluray_release; }
    public void setStream_bluray_release(String stream_bluray_release) { this.stream_bluray_release = stream_bluray_release; }
    
    public String getStream_streaming_release() { return stream_streaming_release; }
    public void setStream_streaming_release(String stream_streaming_release) { this.stream_streaming_release = stream_streaming_release; }
    
    public String getStream_tv_release() { return stream_tv_release; }
    public void setStream_tv_release(String stream_tv_release) { this.stream_tv_release = stream_tv_release; }
    
    public String getStream_festival_release() { return stream_festival_release; }
    public void setStream_festival_release(String stream_festival_release) { this.stream_festival_release = stream_festival_release; }
    
    public String getStream_limited_release() { return stream_limited_release; }
    public void setStream_limited_release(String stream_limited_release) { this.stream_limited_release = stream_limited_release; }
    
    public String getStream_wide_release() { return stream_wide_release; }
    public void setStream_wide_release(String stream_wide_release) { this.stream_wide_release = stream_wide_release; }
    
    public String getStream_international_release() { return stream_international_release; }
    public void setStream_international_release(String stream_international_release) { this.stream_international_release = stream_international_release; }
    
    public String getStream_home_video_release() { return stream_home_video_release; }
    public void setStream_home_video_release(String stream_home_video_release) { this.stream_home_video_release = stream_home_video_release; }
    
    public String getStream_digital_release() { return stream_digital_release; }
    public void setStream_digital_release(String stream_digital_release) { this.stream_digital_release = stream_digital_release; }
    
    public String getStream_physical_release() { return stream_physical_release; }
    public void setStream_physical_release(String stream_physical_release) { this.stream_physical_release = stream_physical_release; }
    
    public String getStream_theatrical_release() { return stream_theatrical_release; }
    public void setStream_theatrical_release(String stream_theatrical_release) { this.stream_theatrical_release = stream_theatrical_release; }
    
    public String getStream_extended_release() { return stream_extended_release; }
    public void setStream_extended_release(String stream_extended_release) { this.stream_extended_release = stream_extended_release; }
    
    public String getStream_director_cut() { return stream_director_cut; }
    public void setStream_director_cut(String stream_director_cut) { this.stream_director_cut = stream_director_cut; }
    
    public String getStream_unrated() { return stream_unrated; }
    public void setStream_unrated(String stream_unrated) { this.stream_unrated = stream_unrated; }
    
    public String getStream_rated() { return stream_rated; }
    public void setStream_rated(String stream_rated) { this.stream_rated = stream_rated; }
    
    public String getStream_mpaa_rating() { return stream_mpaa_rating; }
    public void setStream_mpaa_rating(String stream_mpaa_rating) { this.stream_mpaa_rating = stream_mpaa_rating; }
    
    public String getStream_content_rating() { return stream_content_rating; }
    public void setStream_content_rating(String stream_content_rating) { this.stream_content_rating = stream_content_rating; }
    
    public String getStream_age_rating() { return stream_age_rating; }
    public void setStream_age_rating(String stream_age_rating) { this.stream_age_rating = stream_age_rating; }
    
    public String getStream_parental_guidance() { return stream_parental_guidance; }
    public void setStream_parental_guidance(String stream_parental_guidance) { this.stream_parental_guidance = stream_parental_guidance; }
    
    public String getStream_viewer_discretion() { return stream_viewer_discretion; }
    public void setStream_viewer_discretion(String stream_viewer_discretion) { this.stream_viewer_discretion = stream_viewer_discretion; }
    
    public String getStream_content_warning() { return stream_content_warning; }
    public void setStream_content_warning(String stream_content_warning) { this.stream_content_warning = stream_content_warning; }
    
    public String getStream_trigger_warning() { return stream_trigger_warning; }
    public void setStream_trigger_warning(String stream_trigger_warning) { this.stream_trigger_warning = stream_trigger_warning; }
    
    public String getStream_sensitive_content() { return stream_sensitive_content; }
    public void setStream_sensitive_content(String stream_sensitive_content) { this.stream_sensitive_content = stream_sensitive_content; }
    
    public String getStream_mature_content() { return stream_mature_content; }
    public void setStream_mature_content(String stream_mature_content) { this.stream_mature_content = stream_mature_content; }
    
    public String getStream_adult_content() { return stream_adult_content; }
    public void setStream_adult_content(String stream_adult_content) { this.stream_adult_content = stream_adult_content; }
    
    public String getStream_explicit_content() { return stream_explicit_content; }
    public void setStream_explicit_content(String stream_explicit_content) { this.stream_explicit_content = stream_explicit_content; }
    
    public String getStream_graphic_content() { return stream_graphic_content; }
    public void setStream_graphic_content(String stream_graphic_content) { this.stream_graphic_content = stream_graphic_content; }
    
    public String getStream_violent_content() { return stream_violent_content; }
    public void setStream_violent_content(String stream_violent_content) { this.stream_violent_content = stream_violent_content; }
    
    public String getStream_sexual_content() { return stream_sexual_content; }
    public void setStream_sexual_content(String stream_sexual_content) { this.stream_sexual_content = stream_sexual_content; }
    
    public String getStream_language_content() { return stream_language_content; }
    public void setStream_language_content(String stream_language_content) { this.stream_language_content = stream_language_content; }
    
    public String getStream_drug_content() { return stream_drug_content; }
    public void setStream_drug_content(String stream_drug_content) { this.stream_drug_content = stream_drug_content; }
    
    public String getStream_alcohol_content() { return stream_alcohol_content; }
    public void setStream_alcohol_content(String stream_alcohol_content) { this.stream_alcohol_content = stream_alcohol_content; }
    
    public String getStream_smoking_content() { return stream_smoking_content; }
    public void setStream_smoking_content(String stream_smoking_content) { this.stream_smoking_content = stream_smoking_content; }
    
    public String getStream_gambling_content() { return stream_gambling_content; }
    public void setStream_gambling_content(String stream_gambling_content) { this.stream_gambling_content = stream_gambling_content; }
    
    public String getStream_self_harm_content() { return stream_self_harm_content; }
    public void setStream_self_harm_content(String stream_self_harm_content) { this.stream_self_harm_content = stream_self_harm_content; }
    
    public String getStream_suicide_content() { return stream_suicide_content; }
    public void setStream_suicide_content(String stream_suicide_content) { this.stream_suicide_content = stream_suicide_content; }
    
    public String getStream_eating_disorder_content() { return stream_eating_disorder_content; }
    public void setStream_eating_disorder_content(String stream_eating_disorder_content) { this.stream_eating_disorder_content = stream_eating_disorder_content; }
    
    public String getStream_mental_health_content() { return stream_mental_health_content; }
    public void setStream_mental_health_content(String stream_mental_health_content) { this.stream_mental_health_content = stream_mental_health_content; }
    
    public String getStream_physical_health_content() { return stream_physical_health_content; }
    public void setStream_physical_health_content(String stream_physical_health_content) { this.stream_physical_health_content = stream_physical_health_content; }
    
    public String getStream_medical_content() { return stream_medical_content; }
    public void setStream_medical_content(String stream_medical_content) { this.stream_medical_content = stream_medical_content; }
    
    public String getStream_political_content() { return stream_political_content; }
    public void setStream_political_content(String stream_political_content) { this.stream_political_content = stream_political_content; }
    
    public String getStream_religious_content() { return stream_religious_content; }
    public void setStream_religious_content(String stream_religious_content) { this.stream_religious_content = stream_religious_content; }
    
    public String getStream_cultural_content() { return stream_cultural_content; }
    public void setStream_cultural_content(String stream_cultural_content) { this.stream_cultural_content = stream_cultural_content; }
    
    public String getStream_historical_content() { return stream_historical_content; }
    public void setStream_historical_content(String stream_historical_content) { this.stream_historical_content = stream_historical_content; }
    
    public String getStream_educational_content() { return stream_educational_content; }
    public void setStream_educational_content(String stream_educational_content) { this.stream_educational_content = stream_educational_content; }
    
    public String getStream_documentary_content() { return stream_documentary_content; }
    public void setStream_documentary_content(String stream_documentary_content) { this.stream_documentary_content = stream_documentary_content; }
    
    public String getStream_reality_content() { return stream_reality_content; }
    public void setStream_reality_content(String stream_reality_content) { this.stream_reality_content = stream_reality_content; }
    
    public String getStream_game_show_content() { return stream_game_show_content; }
    public void setStream_game_show_content(String stream_game_show_content) { this.stream_game_show_content = stream_game_show_content; }
    
    public String getStream_talk_show_content() { return stream_talk_show_content; }
    public void setStream_talk_show_content(String stream_talk_show_content) { this.stream_talk_show_content = stream_talk_show_content; }
    
    public String getStream_variety_content() { return stream_variety_content; }
    public void setStream_variety_content(String stream_variety_content) { this.stream_variety_content = stream_variety_content; }
    
    public String getStream_news_content() { return stream_news_content; }
    public void setStream_news_content(String stream_news_content) { this.stream_news_content = stream_news_content; }
    
    public String getStream_sports_content() { return stream_sports_content; }
    public void setStream_sports_content(String stream_sports_content) { this.stream_sports_content = stream_sports_content; }
    
    public String getStream_music_content() { return stream_music_content; }
    public void setStream_music_content(String stream_music_content) { this.stream_music_content = stream_music_content; }
    
    public String getStream_comedy_content() { return stream_comedy_content; }
    public void setStream_comedy_content(String stream_comedy_content) { this.stream_comedy_content = stream_comedy_content; }
    
    public String getStream_drama_content() { return stream_drama_content; }
    public void setStream_drama_content(String stream_drama_content) { this.stream_drama_content = stream_drama_content; }
    
    public String getStream_action_content() { return stream_action_content; }
    public void setStream_action_content(String stream_action_content) { this.stream_action_content = stream_action_content; }
    
    public String getStream_adventure_content() { return stream_adventure_content; }
    public void setStream_adventure_content(String stream_adventure_content) { this.stream_adventure_content = stream_adventure_content; }
    
    public String getStream_animation_content() { return stream_animation_content; }
    public void setStream_animation_content(String stream_animation_content) { this.stream_animation_content = stream_animation_content; }
    
    public String getStream_biography_content() { return stream_biography_content; }
    public void setStream_biography_content(String stream_biography_content) { this.stream_biography_content = stream_biography_content; }
    
    public String getStream_crime_content() { return stream_crime_content; }
    public void setStream_crime_content(String stream_crime_content) { this.stream_crime_content = stream_crime_content; }
    
    public String getStream_family_content() { return stream_family_content; }
    public void setStream_family_content(String stream_family_content) { this.stream_family_content = stream_family_content; }
    
    public String getStream_fantasy_content() { return stream_fantasy_content; }
    public void setStream_fantasy_content(String stream_fantasy_content) { this.stream_fantasy_content = stream_fantasy_content; }
    
    public String getStream_film_noir_content() { return stream_film_noir_content; }
    public void setStream_film_noir_content(String stream_film_noir_content) { this.stream_film_noir_content = stream_film_noir_content; }
    
    public String getStream_history_content() { return stream_history_content; }
    public void setStream_history_content(String stream_history_content) { this.stream_history_content = stream_history_content; }
    
    public String getStream_horror_content() { return stream_horror_content; }
    public void setStream_horror_content(String stream_horror_content) { this.stream_horror_content = stream_horror_content; }
    
    public String getStream_mystery_content() { return stream_mystery_content; }
    public void setStream_mystery_content(String stream_mystery_content) { this.stream_mystery_content = stream_mystery_content; }
    
    public String getStream_romance_content() { return stream_romance_content; }
    public void setStream_romance_content(String stream_romance_content) { this.stream_romance_content = stream_romance_content; }
    
    public String getStream_sci_fi_content() { return stream_sci_fi_content; }
    public void setStream_sci_fi_content(String stream_sci_fi_content) { this.stream_sci_fi_content = stream_sci_fi_content; }
    
    public String getStream_thriller_content() { return stream_thriller_content; }
    public void setStream_thriller_content(String stream_thriller_content) { this.stream_thriller_content = stream_thriller_content; }
    
    public String getStream_war_content() { return stream_war_content; }
    public void setStream_war_content(String stream_war_content) { this.stream_war_content = stream_war_content; }
    
    public String getStream_western_content() { return stream_western_content; }
    public void setStream_western_content(String stream_western_content) { this.stream_western_content = stream_western_content; }
    
    public String getStream_musical_content() { return stream_musical_content; }
    public void setStream_musical_content(String stream_musical_content) { this.stream_musical_content = stream_musical_content; }
    
    public String getStream_sport_content() { return stream_sport_content; }
    public void setStream_sport_content(String stream_sport_content) { this.stream_sport_content = stream_sport_content; }
    
    public String getStream_superhero_content() { return stream_superhero_content; }
    public void setStream_superhero_content(String stream_superhero_content) { this.stream_superhero_content = stream_superhero_content; }
    
    public String getStream_martial_arts_content() { return stream_martial_arts_content; }
    public void setStream_martial_arts_content(String stream_martial_arts_content) { this.stream_martial_arts_content = stream_martial_arts_content; }
    
    public String getStream_spy_content() { return stream_spy_content; }
    public void setStream_spy_content(String stream_spy_content) { this.stream_spy_content = stream_spy_content; }
    
    public String getStream_heist_content() { return stream_heist_content; }
    public void setStream_heist_content(String stream_heist_content) { this.stream_heist_content = stream_heist_content; }
    
    public String getStream_courtroom_content() { return stream_courtroom_content; }
    public void setStream_courtroom_content(String stream_courtroom_content) { this.stream_courtroom_content = stream_courtroom_content; }
    
    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}