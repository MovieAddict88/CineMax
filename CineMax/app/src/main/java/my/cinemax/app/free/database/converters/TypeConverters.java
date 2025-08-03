package my.cinemax.app.free.database.converters;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import my.cinemax.app.free.entity.*;
import java.lang.reflect.Type;
import java.util.List;

public class TypeConverters {
    
    private static Gson gson = new Gson();
    
    // Converters for List<Genre>
    @TypeConverter
    public static String fromGenreList(List<Genre> genres) {
        if (genres == null) {
            return null;
        }
        return gson.toJson(genres);
    }
    
    @TypeConverter
    public static List<Genre> toGenreList(String genresString) {
        if (genresString == null) {
            return null;
        }
        Type listType = new TypeToken<List<Genre>>(){}.getType();
        return gson.fromJson(genresString, listType);
    }
    
    // Converters for List<Actor>
    @TypeConverter
    public static String fromActorList(List<Actor> actors) {
        if (actors == null) {
            return null;
        }
        return gson.toJson(actors);
    }
    
    @TypeConverter
    public static List<Actor> toActorList(String actorsString) {
        if (actorsString == null) {
            return null;
        }
        Type listType = new TypeToken<List<Actor>>(){}.getType();
        return gson.fromJson(actorsString, listType);
    }
    
    // Converters for List<Source>
    @TypeConverter
    public static String fromSourceList(List<Source> sources) {
        if (sources == null) {
            return null;
        }
        return gson.toJson(sources);
    }
    
    @TypeConverter
    public static List<Source> toSourceList(String sourcesString) {
        if (sourcesString == null) {
            return null;
        }
        Type listType = new TypeToken<List<Source>>(){}.getType();
        return gson.fromJson(sourcesString, listType);
    }
    
    // Converters for Source (single object)
    @TypeConverter
    public static String fromSource(Source source) {
        if (source == null) {
            return null;
        }
        return gson.toJson(source);
    }
    
    @TypeConverter
    public static Source toSource(String sourceString) {
        if (sourceString == null) {
            return null;
        }
        return gson.fromJson(sourceString, Source.class);
    }
    
    // Converters for List<Season>
    @TypeConverter
    public static String fromSeasonList(List<Season> seasons) {
        if (seasons == null) {
            return null;
        }
        return gson.toJson(seasons);
    }
    
    @TypeConverter
    public static List<Season> toSeasonList(String seasonsString) {
        if (seasonsString == null) {
            return null;
        }
        Type listType = new TypeToken<List<Season>>(){}.getType();
        return gson.fromJson(seasonsString, listType);
    }
    
    // Converters for List<Poster> (used in Genre and other entities)
    @TypeConverter
    public static String fromPosterList(List<Poster> posters) {
        if (posters == null) {
            return null;
        }
        return gson.toJson(posters);
    }
    
    @TypeConverter
    public static List<Poster> toPosterList(String postersString) {
        if (postersString == null) {
            return null;
        }
        Type listType = new TypeToken<List<Poster>>(){}.getType();
        return gson.fromJson(postersString, listType);
    }
}