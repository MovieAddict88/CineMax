package my.cinemax.app.free.database;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import my.cinemax.app.free.entity.Channel;
import my.cinemax.app.free.entity.Poster;
import java.lang.reflect.Type;
import java.util.List;

public class Converters {
    
    @TypeConverter
    public static String fromChannelList(List<Channel> channels) {
        if (channels == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Channel>>() {}.getType();
        return gson.toJson(channels, type);
    }
    
    @TypeConverter
    public static List<Channel> toChannelList(String channelsString) {
        if (channelsString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Channel>>() {}.getType();
        return gson.fromJson(channelsString, type);
    }
    
    @TypeConverter
    public static String fromPosterList(List<Poster> posters) {
        if (posters == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Poster>>() {}.getType();
        return gson.toJson(posters, type);
    }
    
    @TypeConverter
    public static List<Poster> toPosterList(String postersString) {
        if (postersString == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Poster>>() {}.getType();
        return gson.fromJson(postersString, type);
    }
}