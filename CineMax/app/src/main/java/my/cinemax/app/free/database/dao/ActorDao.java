package my.cinemax.app.free.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import my.cinemax.app.free.database.entities.ActorEntity;

@Dao
public interface ActorDao {

    @Query("SELECT * FROM actors ORDER BY name ASC")
    LiveData<List<ActorEntity>> getAllActors();

    @Query("SELECT * FROM actors ORDER BY name ASC")
    List<ActorEntity> getAllActorsSync();

    @Query("SELECT * FROM actors WHERE name LIKE '%' || :query || '%'")
    LiveData<List<ActorEntity>> searchActors(String query);

    @Query("SELECT * FROM actors WHERE id = :id LIMIT 1")
    LiveData<ActorEntity> getActorById(int id);

    @Query("SELECT * FROM actors WHERE id = :id LIMIT 1")
    ActorEntity getActorByIdSync(int id);

    @Query("SELECT * FROM actors WHERE nationality = :nationality ORDER BY name ASC")
    LiveData<List<ActorEntity>> getActorsByNationality(String nationality);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActor(ActorEntity actor);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertActors(List<ActorEntity> actors);

    @Update
    void updateActor(ActorEntity actor);

    @Delete
    void deleteActor(ActorEntity actor);

    @Query("DELETE FROM actors")
    void deleteAllActors();

    @Query("DELETE FROM actors WHERE lastUpdated < :threshold")
    void deleteOldActors(long threshold);

    @Query("SELECT COUNT(*) FROM actors")
    int getActorCount();

    @Query("SELECT MAX(lastUpdated) FROM actors")
    long getLastUpdateTime();

    // Cache management
    @Query("UPDATE actors SET lastUpdated = :timestamp WHERE id = :id")
    void updateLastUpdated(int id, long timestamp);
}