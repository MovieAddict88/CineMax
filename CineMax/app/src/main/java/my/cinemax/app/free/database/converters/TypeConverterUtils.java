package my.cinemax.app.free.database.converters;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import my.cinemax.app.free.entity.Actor;
import my.cinemax.app.free.entity.Genre;
import my.cinemax.app.free.entity.Source;
import my.cinemax.app.free.entity.Subtitle;
import my.cinemax.app.free.entity.Category;

/**
 * Type converters for Room database to handle complex data types
 */
public class TypeConverterUtils {
    private static Gson gson = new Gson();

    // String List converters
    @TypeConverter
    public static String fromStringList(List<String> value) {
        return gson.toJson(value);
    }

    @TypeConverter
    public static List<String> toStringList(String value) {
        Type listType = new TypeToken<List<String>>(){}.getType();
        return gson.fromJson(value, listType);
    }

    // Genre List converters
    @TypeConverter
    public static String fromGenreList(List<Genre> value) {
        return gson.toJson(value);
    }

    @TypeConverter
    public static List<Genre> toGenreList(String value) {
        Type listType = new TypeToken<List<Genre>>(){}.getType();
        return gson.fromJson(value, listType);
    }

    // Actor List converters
    @TypeConverter
    public static String fromActorList(List<Actor> value) {
        return gson.toJson(value);
    }

    @TypeConverter
    public static List<Actor> toActorList(String value) {
        Type listType = new TypeToken<List<Actor>>(){}.getType();
        return gson.fromJson(value, listType);
    }

    // Source List converters
    @TypeConverter
    public static String fromSourceList(List<Source> value) {
        return gson.toJson(value);
    }

    @TypeConverter
    public static List<Source> toSourceList(String value) {
        Type listType = new TypeToken<List<Source>>(){}.getType();
        return gson.fromJson(value, listType);
    }

    // Subtitle List converters
    @TypeConverter
    public static String fromSubtitleList(List<Subtitle> value) {
        return gson.toJson(value);
    }

    @TypeConverter
    public static List<Subtitle> toSubtitleList(String value) {
        Type listType = new TypeToken<List<Subtitle>>(){}.getType();
        return gson.fromJson(value, listType);
    }

    // Category List converters
    @TypeConverter
    public static String fromCategoryList(List<Category> value) {
        return gson.toJson(value);
    }

    @TypeConverter
    public static List<Category> toCategoryList(String value) {
        Type listType = new TypeToken<List<Category>>(){}.getType();
        return gson.fromJson(value, listType);
    }

    // Generic JSON string conversion
    @TypeConverter
    public static String fromObject(Object value) {
        return gson.toJson(value);
    }

    @TypeConverter
    public static <T> T toObject(String value, Class<T> classOfT) {
        return gson.fromJson(value, classOfT);
    }
}