package my.cinemax.app.free.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import my.cinemax.app.free.database.dao.*;
import my.cinemax.app.free.entity.*;

@Database(
    entities = {
        Poster.class,
        Channel.class,
        Genre.class,
        Category.class
    },
    version = 1,
    exportSchema = false
)
@TypeConverters(my.cinemax.app.free.database.converters.TypeConverters.class)
public abstract class CineMaxDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "cinemax_database";
    private static volatile CineMaxDatabase INSTANCE;
    
    // DAO declarations
    public abstract PosterDao posterDao();
    public abstract ChannelDao channelDao();
    public abstract GenreDao genreDao();
    public abstract CategoryDao categoryDao();
    
    public static CineMaxDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (CineMaxDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                        context.getApplicationContext(),
                        CineMaxDatabase.class,
                        DATABASE_NAME
                    )
                    .addCallback(roomCallback)
                    .build();
                }
            }
        }
        return INSTANCE;
    }
    
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // Database created for the first time
        }
        
        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            // Database opened
        }
    };
}