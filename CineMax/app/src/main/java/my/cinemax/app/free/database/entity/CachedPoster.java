package my.cinemax.app.free.database.entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cached_posters")
public class CachedPoster {
    @PrimaryKey
    private int id;
    private String title;
    private String description;
    private String image;
    private String backdrop;
    private String type;
    private String year;
    private String duration;
    private String rating;
    private String trailer;
    private String source;
    private String subtitle;
    private String quality;
    private String size;
    private String language;
    private String country;
    private String genre;
    private String cast;
    private String director;
    private String writer;
    private String producer;
    private String music;
    private String cinematography;
    private String editing;
    private String art_direction;
    private String costume_design;
    private String makeup;
    private String sound;
    private String visual_effects;
    private String stunts;
    private String camera;
    private String lighting;
    private String production_design;
    private String animation;
    private String color;
    private String aspect_ratio;
    private String runtime;
    private String budget;
    private String revenue;
    private String website;
    private String imdb;
    private String rotten_tomatoes;
    private String metacritic;
    private String awards;
    private String nominations;
    private String wins;
    private String box_office;
    private String production_company;
    private String distributor;
    private String release_date;
    private String dvd_release;
    private String bluray_release;
    private String streaming_release;
    private String tv_release;
    private String festival_release;
    private String limited_release;
    private String wide_release;
    private String international_release;
    private String home_video_release;
    private String digital_release;
    private String physical_release;
    private String theatrical_release;
    private String extended_release;
    private String director_cut;
    private String unrated;
    private String rated;
    private String mpaa_rating;
    private String content_rating;
    private String age_rating;
    private String parental_guidance;
    private String viewer_discretion;
    private String content_warning;
    private String trigger_warning;
    private String sensitive_content;
    private String mature_content;
    private String adult_content;
    private String explicit_content;
    private String graphic_content;
    private String violent_content;
    private String sexual_content;
    private String language_content;
    private String drug_content;
    private String alcohol_content;
    private String smoking_content;
    private String gambling_content;
    private String self_harm_content;
    private String suicide_content;
    private String eating_disorder_content;
    private String mental_health_content;
    private String physical_health_content;
    private String medical_content;
    private String political_content;
    private String religious_content;
    private String cultural_content;
    private String historical_content;
    private String educational_content;
    private String documentary_content;
    private String reality_content;
    private String game_show_content;
    private String talk_show_content;
    private String variety_content;
    private String news_content;
    private String sports_content;
    private String music_content;
    private String comedy_content;
    private String drama_content;
    private String action_content;
    private String adventure_content;
    private String animation_content;
    private String biography_content;
    private String crime_content;
    private String family_content;
    private String fantasy_content;
    private String film_noir_content;
    private String history_content;
    private String horror_content;
    private String mystery_content;
    private String romance_content;
    private String sci_fi_content;
    private String thriller_content;
    private String war_content;
    private String western_content;
    private String musical_content;
    private String sport_content;
    private String superhero_content;
    private String martial_arts_content;
    private String spy_content;
    private String heist_content;
    private String courtroom_content;
    private String medical_content_2;
    private String political_content_2;
    private String religious_content_2;
    private String cultural_content_2;
    private String historical_content_2;
    private String educational_content_2;
    private String documentary_content_2;
    private String reality_content_2;
    private String game_show_content_2;
    private String talk_show_content_2;
    private String variety_content_2;
    private String news_content_2;
    private String sports_content_2;
    private String music_content_2;
    private String comedy_content_2;
    private String drama_content_2;
    private String action_content_2;
    private String adventure_content_2;
    private String animation_content_2;
    private String biography_content_2;
    private String crime_content_2;
    private String family_content_2;
    private String fantasy_content_2;
    private String film_noir_content_2;
    private String history_content_2;
    private String horror_content_2;
    private String mystery_content_2;
    private String romance_content_2;
    private String sci_fi_content_2;
    private String thriller_content_2;
    private String war_content_2;
    private String western_content_2;
    private String musical_content_2;
    private String sport_content_2;
    private String superhero_content_2;
    private String martial_arts_content_2;
    private String spy_content_2;
    private String heist_content_2;
    private String courtroom_content_2;
    private long lastUpdated;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
    
    public String getBackdrop() { return backdrop; }
    public void setBackdrop(String backdrop) { this.backdrop = backdrop; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getYear() { return year; }
    public void setYear(String year) { this.year = year; }
    
    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }
    
    public String getRating() { return rating; }
    public void setRating(String rating) { this.rating = rating; }
    
    public String getTrailer() { return trailer; }
    public void setTrailer(String trailer) { this.trailer = trailer; }
    
    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
    
    public String getSubtitle() { return subtitle; }
    public void setSubtitle(String subtitle) { this.subtitle = subtitle; }
    
    public String getQuality() { return quality; }
    public void setQuality(String quality) { this.quality = quality; }
    
    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }
    
    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
    
    public String getCast() { return cast; }
    public void setCast(String cast) { this.cast = cast; }
    
    public String getDirector() { return director; }
    public void setDirector(String director) { this.director = director; }
    
    public String getWriter() { return writer; }
    public void setWriter(String writer) { this.writer = writer; }
    
    public String getProducer() { return producer; }
    public void setProducer(String producer) { this.producer = producer; }
    
    public String getMusic() { return music; }
    public void setMusic(String music) { this.music = music; }
    
    public String getCinematography() { return cinematography; }
    public void setCinematography(String cinematography) { this.cinematography = cinematography; }
    
    public String getEditing() { return editing; }
    public void setEditing(String editing) { this.editing = editing; }
    
    public String getArt_direction() { return art_direction; }
    public void setArt_direction(String art_direction) { this.art_direction = art_direction; }
    
    public String getCostume_design() { return costume_design; }
    public void setCostume_design(String costume_design) { this.costume_design = costume_design; }
    
    public String getMakeup() { return makeup; }
    public void setMakeup(String makeup) { this.makeup = makeup; }
    
    public String getSound() { return sound; }
    public void setSound(String sound) { this.sound = sound; }
    
    public String getVisual_effects() { return visual_effects; }
    public void setVisual_effects(String visual_effects) { this.visual_effects = visual_effects; }
    
    public String getStunts() { return stunts; }
    public void setStunts(String stunts) { this.stunts = stunts; }
    
    public String getCamera() { return camera; }
    public void setCamera(String camera) { this.camera = camera; }
    
    public String getLighting() { return lighting; }
    public void setLighting(String lighting) { this.lighting = lighting; }
    
    public String getProduction_design() { return production_design; }
    public void setProduction_design(String production_design) { this.production_design = production_design; }
    
    public String getAnimation() { return animation; }
    public void setAnimation(String animation) { this.animation = animation; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getAspect_ratio() { return aspect_ratio; }
    public void setAspect_ratio(String aspect_ratio) { this.aspect_ratio = aspect_ratio; }
    
    public String getRuntime() { return runtime; }
    public void setRuntime(String runtime) { this.runtime = runtime; }
    
    public String getBudget() { return budget; }
    public void setBudget(String budget) { this.budget = budget; }
    
    public String getRevenue() { return revenue; }
    public void setRevenue(String revenue) { this.revenue = revenue; }
    
    public String getWebsite() { return website; }
    public void setWebsite(String website) { this.website = website; }
    
    public String getImdb() { return imdb; }
    public void setImdb(String imdb) { this.imdb = imdb; }
    
    public String getRotten_tomatoes() { return rotten_tomatoes; }
    public void setRotten_tomatoes(String rotten_tomatoes) { this.rotten_tomatoes = rotten_tomatoes; }
    
    public String getMetacritic() { return metacritic; }
    public void setMetacritic(String metacritic) { this.metacritic = metacritic; }
    
    public String getAwards() { return awards; }
    public void setAwards(String awards) { this.awards = awards; }
    
    public String getNominations() { return nominations; }
    public void setNominations(String nominations) { this.nominations = nominations; }
    
    public String getWins() { return wins; }
    public void setWins(String wins) { this.wins = wins; }
    
    public String getBox_office() { return box_office; }
    public void setBox_office(String box_office) { this.box_office = box_office; }
    
    public String getProduction_company() { return production_company; }
    public void setProduction_company(String production_company) { this.production_company = production_company; }
    
    public String getDistributor() { return distributor; }
    public void setDistributor(String distributor) { this.distributor = distributor; }
    
    public String getRelease_date() { return release_date; }
    public void setRelease_date(String release_date) { this.release_date = release_date; }
    
    public String getDvd_release() { return dvd_release; }
    public void setDvd_release(String dvd_release) { this.dvd_release = dvd_release; }
    
    public String getBluray_release() { return bluray_release; }
    public void setBluray_release(String bluray_release) { this.bluray_release = bluray_release; }
    
    public String getStreaming_release() { return streaming_release; }
    public void setStreaming_release(String streaming_release) { this.streaming_release = streaming_release; }
    
    public String getTv_release() { return tv_release; }
    public void setTv_release(String tv_release) { this.tv_release = tv_release; }
    
    public String getFestival_release() { return festival_release; }
    public void setFestival_release(String festival_release) { this.festival_release = festival_release; }
    
    public String getLimited_release() { return limited_release; }
    public void setLimited_release(String limited_release) { this.limited_release = limited_release; }
    
    public String getWide_release() { return wide_release; }
    public void setWide_release(String wide_release) { this.wide_release = wide_release; }
    
    public String getInternational_release() { return international_release; }
    public void setInternational_release(String international_release) { this.international_release = international_release; }
    
    public String getHome_video_release() { return home_video_release; }
    public void setHome_video_release(String home_video_release) { this.home_video_release = home_video_release; }
    
    public String getDigital_release() { return digital_release; }
    public void setDigital_release(String digital_release) { this.digital_release = digital_release; }
    
    public String getPhysical_release() { return physical_release; }
    public void setPhysical_release(String physical_release) { this.physical_release = physical_release; }
    
    public String getTheatrical_release() { return theatrical_release; }
    public void setTheatrical_release(String theatrical_release) { this.theatrical_release = theatrical_release; }
    
    public String getExtended_release() { return extended_release; }
    public void setExtended_release(String extended_release) { this.extended_release = extended_release; }
    
    public String getDirector_cut() { return director_cut; }
    public void setDirector_cut(String director_cut) { this.director_cut = director_cut; }
    
    public String getUnrated() { return unrated; }
    public void setUnrated(String unrated) { this.unrated = unrated; }
    
    public String getRated() { return rated; }
    public void setRated(String rated) { this.rated = rated; }
    
    public String getMpaa_rating() { return mpaa_rating; }
    public void setMpaa_rating(String mpaa_rating) { this.mpaa_rating = mpaa_rating; }
    
    public String getContent_rating() { return content_rating; }
    public void setContent_rating(String content_rating) { this.content_rating = content_rating; }
    
    public String getAge_rating() { return age_rating; }
    public void setAge_rating(String age_rating) { this.age_rating = age_rating; }
    
    public String getParental_guidance() { return parental_guidance; }
    public void setParental_guidance(String parental_guidance) { this.parental_guidance = parental_guidance; }
    
    public String getViewer_discretion() { return viewer_discretion; }
    public void setViewer_discretion(String viewer_discretion) { this.viewer_discretion = viewer_discretion; }
    
    public String getContent_warning() { return content_warning; }
    public void setContent_warning(String content_warning) { this.content_warning = content_warning; }
    
    public String getTrigger_warning() { return trigger_warning; }
    public void setTrigger_warning(String trigger_warning) { this.trigger_warning = trigger_warning; }
    
    public String getSensitive_content() { return sensitive_content; }
    public void setSensitive_content(String sensitive_content) { this.sensitive_content = sensitive_content; }
    
    public String getMature_content() { return mature_content; }
    public void setMature_content(String mature_content) { this.mature_content = mature_content; }
    
    public String getAdult_content() { return adult_content; }
    public void setAdult_content(String adult_content) { this.adult_content = adult_content; }
    
    public String getExplicit_content() { return explicit_content; }
    public void setExplicit_content(String explicit_content) { this.explicit_content = explicit_content; }
    
    public String getGraphic_content() { return graphic_content; }
    public void setGraphic_content(String graphic_content) { this.graphic_content = graphic_content; }
    
    public String getViolent_content() { return violent_content; }
    public void setViolent_content(String violent_content) { this.violent_content = violent_content; }
    
    public String getSexual_content() { return sexual_content; }
    public void setSexual_content(String sexual_content) { this.sexual_content = sexual_content; }
    
    public String getLanguage_content() { return language_content; }
    public void setLanguage_content(String language_content) { this.language_content = language_content; }
    
    public String getDrug_content() { return drug_content; }
    public void setDrug_content(String drug_content) { this.drug_content = drug_content; }
    
    public String getAlcohol_content() { return alcohol_content; }
    public void setAlcohol_content(String alcohol_content) { this.alcohol_content = alcohol_content; }
    
    public String getSmoking_content() { return smoking_content; }
    public void setSmoking_content(String smoking_content) { this.smoking_content = smoking_content; }
    
    public String getGambling_content() { return gambling_content; }
    public void setGambling_content(String gambling_content) { this.gambling_content = gambling_content; }
    
    public String getSelf_harm_content() { return self_harm_content; }
    public void setSelf_harm_content(String self_harm_content) { this.self_harm_content = self_harm_content; }
    
    public String getSuicide_content() { return suicide_content; }
    public void setSuicide_content(String suicide_content) { this.suicide_content = suicide_content; }
    
    public String getEating_disorder_content() { return eating_disorder_content; }
    public void setEating_disorder_content(String eating_disorder_content) { this.eating_disorder_content = eating_disorder_content; }
    
    public String getMental_health_content() { return mental_health_content; }
    public void setMental_health_content(String mental_health_content) { this.mental_health_content = mental_health_content; }
    
    public String getPhysical_health_content() { return physical_health_content; }
    public void setPhysical_health_content(String physical_health_content) { this.physical_health_content = physical_health_content; }
    
    public String getMedical_content() { return medical_content; }
    public void setMedical_content(String medical_content) { this.medical_content = medical_content; }
    
    public String getPolitical_content() { return political_content; }
    public void setPolitical_content(String political_content) { this.political_content = political_content; }
    
    public String getReligious_content() { return religious_content; }
    public void setReligious_content(String religious_content) { this.religious_content = religious_content; }
    
    public String getCultural_content() { return cultural_content; }
    public void setCultural_content(String cultural_content) { this.cultural_content = cultural_content; }
    
    public String getHistorical_content() { return historical_content; }
    public void setHistorical_content(String historical_content) { this.historical_content = historical_content; }
    
    public String getEducational_content() { return educational_content; }
    public void setEducational_content(String educational_content) { this.educational_content = educational_content; }
    
    public String getDocumentary_content() { return documentary_content; }
    public void setDocumentary_content(String documentary_content) { this.documentary_content = documentary_content; }
    
    public String getReality_content() { return reality_content; }
    public void setReality_content(String reality_content) { this.reality_content = reality_content; }
    
    public String getGame_show_content() { return game_show_content; }
    public void setGame_show_content(String game_show_content) { this.game_show_content = game_show_content; }
    
    public String getTalk_show_content() { return talk_show_content; }
    public void setTalk_show_content(String talk_show_content) { this.talk_show_content = talk_show_content; }
    
    public String getVariety_content() { return variety_content; }
    public void setVariety_content(String variety_content) { this.variety_content = variety_content; }
    
    public String getNews_content() { return news_content; }
    public void setNews_content(String news_content) { this.news_content = news_content; }
    
    public String getSports_content() { return sports_content; }
    public void setSports_content(String sports_content) { this.sports_content = sports_content; }
    
    public String getMusic_content() { return music_content; }
    public void setMusic_content(String music_content) { this.music_content = music_content; }
    
    public String getComedy_content() { return comedy_content; }
    public void setComedy_content(String comedy_content) { this.comedy_content = comedy_content; }
    
    public String getDrama_content() { return drama_content; }
    public void setDrama_content(String drama_content) { this.drama_content = drama_content; }
    
    public String getAction_content() { return action_content; }
    public void setAction_content(String action_content) { this.action_content = action_content; }
    
    public String getAdventure_content() { return adventure_content; }
    public void setAdventure_content(String adventure_content) { this.adventure_content = adventure_content; }
    
    public String getAnimation_content() { return animation_content; }
    public void setAnimation_content(String animation_content) { this.animation_content = animation_content; }
    
    public String getBiography_content() { return biography_content; }
    public void setBiography_content(String biography_content) { this.biography_content = biography_content; }
    
    public String getCrime_content() { return crime_content; }
    public void setCrime_content(String crime_content) { this.crime_content = crime_content; }
    
    public String getFamily_content() { return family_content; }
    public void setFamily_content(String family_content) { this.family_content = family_content; }
    
    public String getFantasy_content() { return fantasy_content; }
    public void setFantasy_content(String fantasy_content) { this.fantasy_content = fantasy_content; }
    
    public String getFilm_noir_content() { return film_noir_content; }
    public void setFilm_noir_content(String film_noir_content) { this.film_noir_content = film_noir_content; }
    
    public String getHistory_content() { return history_content; }
    public void setHistory_content(String history_content) { this.history_content = history_content; }
    
    public String getHorror_content() { return horror_content; }
    public void setHorror_content(String horror_content) { this.horror_content = horror_content; }
    
    public String getMystery_content() { return mystery_content; }
    public void setMystery_content(String mystery_content) { this.mystery_content = mystery_content; }
    
    public String getRomance_content() { return romance_content; }
    public void setRomance_content(String romance_content) { this.romance_content = romance_content; }
    
    public String getSci_fi_content() { return sci_fi_content; }
    public void setSci_fi_content(String sci_fi_content) { this.sci_fi_content = sci_fi_content; }
    
    public String getThriller_content() { return thriller_content; }
    public void setThriller_content(String thriller_content) { this.thriller_content = thriller_content; }
    
    public String getWar_content() { return war_content; }
    public void setWar_content(String war_content) { this.war_content = war_content; }
    
    public String getWestern_content() { return western_content; }
    public void setWestern_content(String western_content) { this.western_content = western_content; }
    
    public String getMusical_content() { return musical_content; }
    public void setMusical_content(String musical_content) { this.musical_content = musical_content; }
    
    public String getSport_content() { return sport_content; }
    public void setSport_content(String sport_content) { this.sport_content = sport_content; }
    
    public String getSuperhero_content() { return superhero_content; }
    public void setSuperhero_content(String superhero_content) { this.superhero_content = superhero_content; }
    
    public String getMartial_arts_content() { return martial_arts_content; }
    public void setMartial_arts_content(String martial_arts_content) { this.martial_arts_content = martial_arts_content; }
    
    public String getSpy_content() { return spy_content; }
    public void setSpy_content(String spy_content) { this.spy_content = spy_content; }
    
    public String getHeist_content() { return heist_content; }
    public void setHeist_content(String heist_content) { this.heist_content = heist_content; }
    
    public String getCourtroom_content() { return courtroom_content; }
    public void setCourtroom_content(String courtroom_content) { this.courtroom_content = courtroom_content; }
    
    public String getMedical_content_2() { return medical_content_2; }
    public void setMedical_content_2(String medical_content_2) { this.medical_content_2 = medical_content_2; }
    
    public String getPolitical_content_2() { return political_content_2; }
    public void setPolitical_content_2(String political_content_2) { this.political_content_2 = political_content_2; }
    
    public String getReligious_content_2() { return religious_content_2; }
    public void setReligious_content_2(String religious_content_2) { this.religious_content_2 = religious_content_2; }
    
    public String getCultural_content_2() { return cultural_content_2; }
    public void setCultural_content_2(String cultural_content_2) { this.cultural_content_2 = cultural_content_2; }
    
    public String getHistorical_content_2() { return historical_content_2; }
    public void setHistorical_content_2(String historical_content_2) { this.historical_content_2 = historical_content_2; }
    
    public String getEducational_content_2() { return educational_content_2; }
    public void setEducational_content_2(String educational_content_2) { this.educational_content_2 = educational_content_2; }
    
    public String getDocumentary_content_2() { return documentary_content_2; }
    public void setDocumentary_content_2(String documentary_content_2) { this.documentary_content_2 = documentary_content_2; }
    
    public String getReality_content_2() { return reality_content_2; }
    public void setReality_content_2(String reality_content_2) { this.reality_content_2 = reality_content_2; }
    
    public String getGame_show_content_2() { return game_show_content_2; }
    public void setGame_show_content_2(String game_show_content_2) { this.game_show_content_2 = game_show_content_2; }
    
    public String getTalk_show_content_2() { return talk_show_content_2; }
    public void setTalk_show_content_2(String talk_show_content_2) { this.talk_show_content_2 = talk_show_content_2; }
    
    public String getVariety_content_2() { return variety_content_2; }
    public void setVariety_content_2(String variety_content_2) { this.variety_content_2 = variety_content_2; }
    
    public String getNews_content_2() { return news_content_2; }
    public void setNews_content_2(String news_content_2) { this.news_content_2 = news_content_2; }
    
    public String getSports_content_2() { return sports_content_2; }
    public void setSports_content_2(String sports_content_2) { this.sports_content_2 = sports_content_2; }
    
    public String getMusic_content_2() { return music_content_2; }
    public void setMusic_content_2(String music_content_2) { this.music_content_2 = music_content_2; }
    
    public String getComedy_content_2() { return comedy_content_2; }
    public void setComedy_content_2(String comedy_content_2) { this.comedy_content_2 = comedy_content_2; }
    
    public String getDrama_content_2() { return drama_content_2; }
    public void setDrama_content_2(String drama_content_2) { this.drama_content_2 = drama_content_2; }
    
    public String getAction_content_2() { return action_content_2; }
    public void setAction_content_2(String action_content_2) { this.action_content_2 = action_content_2; }
    
    public String getAdventure_content_2() { return adventure_content_2; }
    public void setAdventure_content_2(String adventure_content_2) { this.adventure_content_2 = adventure_content_2; }
    
    public String getAnimation_content_2() { return animation_content_2; }
    public void setAnimation_content_2(String animation_content_2) { this.animation_content_2 = animation_content_2; }
    
    public String getBiography_content_2() { return biography_content_2; }
    public void setBiography_content_2(String biography_content_2) { this.biography_content_2 = biography_content_2; }
    
    public String getCrime_content_2() { return crime_content_2; }
    public void setCrime_content_2(String crime_content_2) { this.crime_content_2 = crime_content_2; }
    
    public String getFamily_content_2() { return family_content_2; }
    public void setFamily_content_2(String family_content_2) { this.family_content_2 = family_content_2; }
    
    public String getFantasy_content_2() { return fantasy_content_2; }
    public void setFantasy_content_2(String fantasy_content_2) { this.fantasy_content_2 = fantasy_content_2; }
    
    public String getFilm_noir_content_2() { return film_noir_content_2; }
    public void setFilm_noir_content_2(String film_noir_content_2) { this.film_noir_content_2 = film_noir_content_2; }
    
    public String getHistory_content_2() { return history_content_2; }
    public void setHistory_content_2(String history_content_2) { this.history_content_2 = history_content_2; }
    
    public String getHorror_content_2() { return horror_content_2; }
    public void setHorror_content_2(String horror_content_2) { this.horror_content_2 = horror_content_2; }
    
    public String getMystery_content_2() { return mystery_content_2; }
    public void setMystery_content_2(String mystery_content_2) { this.mystery_content_2 = mystery_content_2; }
    
    public String getRomance_content_2() { return romance_content_2; }
    public void setRomance_content_2(String romance_content_2) { this.romance_content_2 = romance_content_2; }
    
    public String getSci_fi_content_2() { return sci_fi_content_2; }
    public void setSci_fi_content_2(String sci_fi_content_2) { this.sci_fi_content_2 = sci_fi_content_2; }
    
    public String getThriller_content_2() { return thriller_content_2; }
    public void setThriller_content_2(String thriller_content_2) { this.thriller_content_2 = thriller_content_2; }
    
    public String getWar_content_2() { return war_content_2; }
    public void setWar_content_2(String war_content_2) { this.war_content_2 = war_content_2; }
    
    public String getWestern_content_2() { return western_content_2; }
    public void setWestern_content_2(String western_content_2) { this.western_content_2 = western_content_2; }
    
    public String getMusical_content_2() { return musical_content_2; }
    public void setMusical_content_2(String musical_content_2) { this.musical_content_2 = musical_content_2; }
    
    public String getSport_content_2() { return sport_content_2; }
    public void setSport_content_2(String sport_content_2) { this.sport_content_2 = sport_content_2; }
    
    public String getSuperhero_content_2() { return superhero_content_2; }
    public void setSuperhero_content_2(String superhero_content_2) { this.superhero_content_2 = superhero_content_2; }
    
    public String getMartial_arts_content_2() { return martial_arts_content_2; }
    public void setMartial_arts_content_2(String martial_arts_content_2) { this.martial_arts_content_2 = martial_arts_content_2; }
    
    public String getSpy_content_2() { return spy_content_2; }
    public void setSpy_content_2(String spy_content_2) { this.spy_content_2 = spy_content_2; }
    
    public String getHeist_content_2() { return heist_content_2; }
    public void setHeist_content_2(String heist_content_2) { this.heist_content_2 = heist_content_2; }
    
    public String getCourtroom_content_2() { return courtroom_content_2; }
    public void setCourtroom_content_2(String courtroom_content_2) { this.courtroom_content_2 = courtroom_content_2; }
    
    public long getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(long lastUpdated) { this.lastUpdated = lastUpdated; }
}