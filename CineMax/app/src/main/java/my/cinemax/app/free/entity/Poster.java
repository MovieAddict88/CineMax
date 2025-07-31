package my.cinemax.app.free.entity;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Poster implements Parcelable {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("label")
    @Expose
    private String label;

    @SerializedName("sublabel")
    @Expose
    private String sublabel;

    @SerializedName("imdb")
    @Expose
    private String imdb;

    @SerializedName("downloadas")
    @Expose
    private String downloadas;

    @SerializedName("comment")
    @Expose
    private Boolean comment;

    @SerializedName("playas")
    @Expose
    private String playas;

    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("classification")
    @Expose
    private String classification;

    @SerializedName("year")
    @Expose
    private String year;

    @SerializedName("duration")
    @Expose
    private String duration;

    @SerializedName("rating")
    @Expose
    private Float rating;

    @SerializedName("image")
    @Expose
    private String image;

    @SerializedName("cover")
    @Expose
    private String cover;

    @SerializedName("genres")
    @Expose
    private List<Genre> genres = new ArrayList<>();

    @SerializedName("actors")
    @Expose
    private List<Actor> actors = new ArrayList<>();

    @SerializedName("views")
    @Expose
    private Integer views;

    @SerializedName("created_at")
    @Expose
    private String createdAt;

    @SerializedName("sources")
    @Expose
    private List<Source> sources = new ArrayList<>();

    @SerializedName("trailer")
    @Expose
    private Source trailer ;

    // TMDB Integration Fields
    @SerializedName("tmdb_id")
    @Expose
    private Integer tmdbId;

    @SerializedName("country")
    @Expose
    private String country;

    @SerializedName("backdrop")
    @Expose
    private String backdrop;

    @SerializedName("poster_tmdb")
    @Expose
    private String posterTmdb;

    @SerializedName("original_title")
    @Expose
    private String originalTitle;

    @SerializedName("original_language")
    @Expose
    private String originalLanguage;

    @SerializedName("popularity")
    @Expose
    private Float popularity;

    @SerializedName("vote_count")
    @Expose
    private Integer voteCount;

    @SerializedName("adult")
    @Expose
    private Boolean adult;

    @SerializedName("release_date")
    @Expose
    private String releaseDate;

    @SerializedName("first_air_date")
    @Expose
    private String firstAirDate;

    @SerializedName("last_air_date")
    @Expose
    private String lastAirDate;

    @SerializedName("number_of_seasons")
    @Expose
    private Integer numberOfSeasons;

    @SerializedName("number_of_episodes")
    @Expose
    private Integer numberOfEpisodes;

    @SerializedName("status")
    @Expose
    private String status;

    @SerializedName("seasons")
    @Expose
    private List<Season> seasons = new ArrayList<>();

    @SerializedName("production_companies")
    @Expose
    private List<String> productionCompanies = new ArrayList<>();

    @SerializedName("networks")
    @Expose
    private List<String> networks = new ArrayList<>();

    @SerializedName("spoken_languages")
    @Expose
    private List<String> spokenLanguages = new ArrayList<>();

    private int typeView = 1;

    public Poster() {
    }


    protected Poster(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        type = in.readString();
        label = in.readString();
        sublabel = in.readString();
        imdb = in.readString();
        downloadas = in.readString();
        byte tmpComment = in.readByte();
        comment = tmpComment == 0 ? null : tmpComment == 1;
        playas = in.readString();
        description = in.readString();
        classification = in.readString();
        year = in.readString();
        duration = in.readString();
        if (in.readByte() == 0) {
            rating = null;
        } else {
            rating = in.readFloat();
        }
        image = in.readString();
        cover = in.readString();
        genres = in.createTypedArrayList(Genre.CREATOR);
        actors = in.createTypedArrayList(Actor.CREATOR);
        if (in.readByte() == 0) {
            views = null;
        } else {
            views = in.readInt();
        }
        createdAt = in.readString();
        sources = in.createTypedArrayList(Source.CREATOR);
        trailer = in.readParcelable(Source.class.getClassLoader());
        typeView = in.readInt();
        
        // TMDB fields
        if (in.readByte() == 0) {
            tmdbId = null;
        } else {
            tmdbId = in.readInt();
        }
        country = in.readString();
        backdrop = in.readString();
        posterTmdb = in.readString();
        originalTitle = in.readString();
        originalLanguage = in.readString();
        if (in.readByte() == 0) {
            popularity = null;
        } else {
            popularity = in.readFloat();
        }
        if (in.readByte() == 0) {
            voteCount = null;
        } else {
            voteCount = in.readInt();
        }
        byte tmpAdult = in.readByte();
        adult = tmpAdult == 0 ? null : tmpAdult == 1;
        releaseDate = in.readString();
        firstAirDate = in.readString();
        lastAirDate = in.readString();
        if (in.readByte() == 0) {
            numberOfSeasons = null;
        } else {
            numberOfSeasons = in.readInt();
        }
        if (in.readByte() == 0) {
            numberOfEpisodes = null;
        } else {
            numberOfEpisodes = in.readInt();
        }
        status = in.readString();
        seasons = in.createTypedArrayList(Season.CREATOR);
        productionCompanies = in.createStringArrayList();
        networks = in.createStringArrayList();
        spokenLanguages = in.createStringArrayList();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        dest.writeString(title);
        dest.writeString(type);
        dest.writeString(label);
        dest.writeString(sublabel);
        dest.writeString(imdb);
        dest.writeString(downloadas);
        dest.writeByte((byte) (comment == null ? 0 : comment ? 1 : 2));
        dest.writeString(playas);
        dest.writeString(description);
        dest.writeString(classification);
        dest.writeString(year);
        dest.writeString(duration);
        if (rating == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(rating);
        }
        dest.writeString(image);
        dest.writeString(cover);
        dest.writeTypedList(genres);
        dest.writeTypedList(actors);
        if (views == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(views);
        }
        dest.writeString(createdAt);
        dest.writeTypedList(sources);
        dest.writeParcelable(trailer, flags);
        dest.writeInt(typeView);
        
        // TMDB fields
        if (tmdbId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(tmdbId);
        }
        dest.writeString(country);
        dest.writeString(backdrop);
        dest.writeString(posterTmdb);
        dest.writeString(originalTitle);
        dest.writeString(originalLanguage);
        if (popularity == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeFloat(popularity);
        }
        if (voteCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(voteCount);
        }
        dest.writeByte((byte) (adult == null ? 0 : adult ? 1 : 2));
        dest.writeString(releaseDate);
        dest.writeString(firstAirDate);
        dest.writeString(lastAirDate);
        if (numberOfSeasons == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(numberOfSeasons);
        }
        if (numberOfEpisodes == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(numberOfEpisodes);
        }
        dest.writeString(status);
        dest.writeTypedList(seasons);
        dest.writeStringList(productionCompanies);
        dest.writeStringList(networks);
        dest.writeStringList(spokenLanguages);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Poster> CREATOR = new Creator<Poster>() {
        @Override
        public Poster createFromParcel(Parcel in) {
            return new Poster(in);
        }

        @Override
        public Poster[] newArray(int size) {
            return new Poster[size];
        }
    };

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public String getClassification() {
        return classification;
    }

    public String getYear() {
        return year;
    }

    public void setClassification(String classification) {
        this.classification = classification;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public Float getRating() {
        return rating;
    }

    public void setRating(Float rating) {
        this.rating = rating;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setGenres(List<Genre> genres) {
        this.genres = genres;
    }

    public List<Genre> getGenres() {
        return genres;
    }

    public List<Actor> getActors() {
        return actors;
    }

    public void setActors(List<Actor> actors) {
        this.actors = actors;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }
    public List<Source> getSources() {
        return sources;
    }

    public Source getTrailer() {
        return trailer;
    }

    public void setTrailer(Source trailer) {
        this.trailer = trailer;
    }

    public int getTypeView() {
        return typeView;
    }

    public Poster setTypeView(int typeView) {
        this.typeView = typeView;
        return this;
    }

    public String getImdb() {
        if (this.imdb == null)
            return "";
        double d = Double.parseDouble(this.imdb);
        DecimalFormat f = new DecimalFormat("##.0");
        return f.format(d);
    }

    public void setImdb(String imdb) {
        this.imdb = imdb;
    }

    public String getDownloadas() {
        return downloadas;
    }

    public void setDownloadas(String downloadas) {
        this.downloadas = downloadas;
    }

    public String getPlayas() {
        return playas;
    }

    public void setPlayas(String playas) {
        this.playas = playas;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Boolean getComment() {
        return comment;
    }

    public void setComment(Boolean comment) {
        this.comment = comment;
    }


    public void setLabel(String label) {
        this.label = label;
    }

    public void setSublabel(String sublabel) {
        this.sublabel = sublabel;
    }

    public String getLabel() {
        return label;
    }

    public String getSublabel() {
        return sublabel;
    }

    // TMDB Integration Getters and Setters
    public Integer getTmdbId() {
        return tmdbId;
    }

    public void setTmdbId(Integer tmdbId) {
        this.tmdbId = tmdbId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getBackdrop() {
        return backdrop;
    }

    public void setBackdrop(String backdrop) {
        this.backdrop = backdrop;
    }

    public String getPosterTmdb() {
        return posterTmdb;
    }

    public void setPosterTmdb(String posterTmdb) {
        this.posterTmdb = posterTmdb;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public Float getPopularity() {
        return popularity;
    }

    public void setPopularity(Float popularity) {
        this.popularity = popularity;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getFirstAirDate() {
        return firstAirDate;
    }

    public void setFirstAirDate(String firstAirDate) {
        this.firstAirDate = firstAirDate;
    }

    public String getLastAirDate() {
        return lastAirDate;
    }

    public void setLastAirDate(String lastAirDate) {
        this.lastAirDate = lastAirDate;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(Integer numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public Integer getNumberOfEpisodes() {
        return numberOfEpisodes;
    }

    public void setNumberOfEpisodes(Integer numberOfEpisodes) {
        this.numberOfEpisodes = numberOfEpisodes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Season> getSeasons() {
        return seasons;
    }

    public void setSeasons(List<Season> seasons) {
        this.seasons = seasons;
    }

    public List<String> getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(List<String> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public List<String> getNetworks() {
        return networks;
    }

    public void setNetworks(List<String> networks) {
        this.networks = networks;
    }

    public List<String> getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(List<String> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }
}


