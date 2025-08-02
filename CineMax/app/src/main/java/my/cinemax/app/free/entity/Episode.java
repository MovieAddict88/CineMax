package my.cinemax.app.free.entity;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Episode implements Parcelable {

    @SerializedName("id")
    @Expose
    private Integer id;
    
    @SerializedName("title")
    @Expose
    private String title;
    
    @SerializedName("episode_number")
    @Expose
    private Integer episodeNumber;
    
    @SerializedName("description")
    @Expose
    private String description;

    @SerializedName("duration")
    @Expose
    private String duration;

    @SerializedName("image")
    @Expose
    private String image;
    
    @SerializedName("views")
    @Expose
    private Integer views;
    
    @SerializedName("downloads")
    @Expose
    private Integer downloads;
    
    @SerializedName("sources")
    @Expose
    private List<Source> sources = new ArrayList<>();
    
    @SerializedName("subtitles")
    @Expose
    private List<Subtitle> subtitles = new ArrayList<>();

    public Episode() {
    }

    protected Episode(Parcel in) {
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        title = in.readString();
        if (in.readByte() == 0) {
            episodeNumber = null;
        } else {
            episodeNumber = in.readInt();
        }
        description = in.readString();
        duration = in.readString();
        image = in.readString();
        if (in.readByte() == 0) {
            views = null;
        } else {
            views = in.readInt();
        }
        if (in.readByte() == 0) {
            downloads = null;
        } else {
            downloads = in.readInt();
        }
        sources = in.createTypedArrayList(Source.CREATOR);
        subtitles = in.createTypedArrayList(Subtitle.CREATOR);
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
        if (episodeNumber == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(episodeNumber);
        }
        dest.writeString(description);
        dest.writeString(duration);
        dest.writeString(image);
        if (views == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(views);
        }
        if (downloads == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(downloads);
        }
        dest.writeTypedList(sources);
        dest.writeTypedList(subtitles);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Episode> CREATOR = new Creator<Episode>() {
        @Override
        public Episode createFromParcel(Parcel in) {
            return new Episode(in);
        }

        @Override
        public Episode[] newArray(int size) {
            return new Episode[size];
        }
    };

    // Getters and Setters
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

    public Integer getEpisodeNumber() {
        return episodeNumber;
    }

    public void setEpisodeNumber(Integer episodeNumber) {
        this.episodeNumber = episodeNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getViews() {
        return views;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public List<Source> getSources() {
        return sources;
    }

    public void setSources(List<Source> sources) {
        this.sources = sources;
    }

    public List<Subtitle> getSubtitles() {
        return subtitles;
    }

    public void setSubtitles(List<Subtitle> subtitles) {
        this.subtitles = subtitles;
    }
}
