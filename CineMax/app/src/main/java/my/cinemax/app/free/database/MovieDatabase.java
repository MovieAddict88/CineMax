package my.cinemax.app.free.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import my.cinemax.app.free.database.dao.MovieDao;
import my.cinemax.app.free.database.dao.SeriesDao;
import my.cinemax.app.free.database.dao.ChannelDao;
import my.cinemax.app.free.database.dao.GenreDao;
import my.cinemax.app.free.database.dao.ActorDao;
import my.cinemax.app.free.database.dao.CategoryDao;
import my.cinemax.app.free.database.dao.SlideDao;
import my.cinemax.app.free.database.entity.MovieEntity;
import my.cinemax.app.free.database.entity.SeriesEntity;
import my.cinemax.app.free.database.entity.ChannelEntity;
import my.cinemax.app.free.database.entity.GenreEntity;
import my.cinemax.app.free.database.entity.ActorEntity;
import my.cinemax.app.free.database.entity.CategoryEntity;
import my.cinemax.app.free.database.entity.SlideEntity;
import my.cinemax.app.free.database.converter.DateConverter;
import my.cinemax.app.free.database.converter.StringListConverter;

@Database(
    entities = {
        MovieEntity.class,
        SeriesEntity.class,
        ChannelEntity.class,
        GenreEntity.class,
        ActorEntity.class,
        CategoryEntity.class,
        SlideEntity.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class, StringListConverter.class})
public abstract class MovieDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "cinemax_database";
    private static MovieDatabase instance;
    
    public abstract MovieDao movieDao();
    public abstract SeriesDao seriesDao();
    public abstract ChannelDao channelDao();
    public abstract GenreDao genreDao();
    public abstract ActorDao actorDao();
    public abstract CategoryDao categoryDao();
    public abstract SlideDao slideDao();
    
    public static synchronized MovieDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                MovieDatabase.class,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
}