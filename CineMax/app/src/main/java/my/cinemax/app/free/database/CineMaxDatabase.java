package my.cinemax.app.free.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import my.cinemax.app.free.database.converters.TypeConverterUtils;
import my.cinemax.app.free.database.dao.ActorDao;
import my.cinemax.app.free.database.dao.ChannelDao;
import my.cinemax.app.free.database.dao.MovieDao;
import my.cinemax.app.free.database.entities.ActorEntity;
import my.cinemax.app.free.database.entities.ChannelEntity;
import my.cinemax.app.free.database.entities.MovieEntity;

@Database(
    entities = {MovieEntity.class, ChannelEntity.class, ActorEntity.class},
    version = 1,
    exportSchema = false
)
@TypeConverters(TypeConverterUtils.class)
public abstract class CineMaxDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "cinemax_database";
    private static CineMaxDatabase instance;

    // Abstract methods to get DAOs
    public abstract MovieDao movieDao();
    public abstract ChannelDao channelDao();
    public abstract ActorDao actorDao();

    // Singleton pattern to ensure only one instance of database
    public static synchronized CineMaxDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(
                    context.getApplicationContext(),
                    CineMaxDatabase.class,
                    DATABASE_NAME
            )
            .fallbackToDestructiveMigration() // In production, use proper migrations
            .build();
        }
        return instance;
    }

    // Method to close database
    public static void closeDatabase() {
        if (instance != null && instance.isOpen()) {
            instance.close();
            instance = null;
        }
    }
}