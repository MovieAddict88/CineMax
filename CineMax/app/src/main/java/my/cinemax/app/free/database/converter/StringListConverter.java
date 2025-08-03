package my.cinemax.app.free.database.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class StringListConverter {
    
    @TypeConverter
    public static String fromList(List<String> value) {
        if (value == null) {
            return null;
        }
        Gson gson = new Gson();
        return gson.toJson(value);
    }
    
    @TypeConverter
    public static List<String> toList(String value) {
        if (value == null) {
            return null;
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(value, type);
    }
}