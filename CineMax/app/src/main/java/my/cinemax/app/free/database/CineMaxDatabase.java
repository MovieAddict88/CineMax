package my.cinemax.app.free.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import my.cinemax.app.free.database.dao.ChannelDao;
import my.cinemax.app.free.database.dao.PosterDao;
import my.cinemax.app.free.database.entity.CachedChannel;
import my.cinemax.app.free.database.entity.CachedPoster;

@Database(
    entities = {
        CachedPoster.class,
        CachedChannel.class
    },
    version = 1,
    exportSchema = false
)
public abstract class CineMaxDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "cinemax_database";
    private static CineMaxDatabase instance;
    
    public abstract PosterDao posterDao();
    public abstract ChannelDao channelDao();
    
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