package my.cinemax.app.free.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import my.cinemax.app.free.database.dao.MovieDao;
import my.cinemax.app.free.database.dao.SeriesDao;
import my.cinemax.app.free.database.dao.CategoryDao;
import my.cinemax.app.free.database.dao.DownloadDao;
import my.cinemax.app.free.database.entity.MovieEntity;
import my.cinemax.app.free.database.entity.SeriesEntity;
import my.cinemax.app.free.database.entity.CategoryEntity;
import my.cinemax.app.free.database.entity.DownloadEntity;
import my.cinemax.app.free.database.converter.DateConverter;

@Database(
    entities = {
        MovieEntity.class,
        SeriesEntity.class,
        CategoryEntity.class,
        DownloadEntity.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class CineMaxDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "cinemax_database";
    private static CineMaxDatabase instance;
    
    // DAOs
    public abstract MovieDao movieDao();
    public abstract SeriesDao seriesDao();
    public abstract CategoryDao categoryDao();
    public abstract DownloadDao downloadDao();
    
    public static synchronized CineMaxDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                context.getApplicationContext(),
                CineMaxDatabase.class,
                DATABASE_NAME
            )
            .fallbackToDestructiveMigration()
            .build();
        }
        return instance;
    }
}