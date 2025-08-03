package my.cinemax.app.free.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import my.cinemax.app.free.database.entities.CachedEpisode;

@Dao
public interface EpisodeDao {

    @Query("SELECT * FROM cached_episodes")
    List<CachedEpisode> getAllEpisodes();

    @Query("SELECT * FROM cached_episodes WHERE id = :episodeId")
    CachedEpisode getEpisodeById(String episodeId);

    @Query("SELECT * FROM cached_episodes WHERE serie_id = :serieId")
    List<CachedEpisode> getEpisodesBySerieId(String serieId);

    @Query("SELECT * FROM cached_episodes WHERE serie_id = :serieId AND season_number = :seasonNumber")
    List<CachedEpisode> getEpisodesBySerieAndSeason(String serieId, int seasonNumber);

    @Query("SELECT * FROM cached_episodes WHERE serie_id = :serieId AND season_number = :seasonNumber AND episode_number = :episodeNumber")
    CachedEpisode getSpecificEpisode(String serieId, int seasonNumber, int episodeNumber);

    @Query("SELECT * FROM cached_episodes WHERE cache_timestamp > :timestampThreshold")
    List<CachedEpisode> getValidCachedEpisodes(long timestampThreshold);

    @Query("SELECT DISTINCT season_number FROM cached_episodes WHERE serie_id = :serieId ORDER BY season_number ASC")
    List<Integer> getSeasonNumbersBySerieId(String serieId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEpisode(CachedEpisode episode);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertEpisodes(List<CachedEpisode> episodes);

    @Update
    void updateEpisode(CachedEpisode episode);

    @Delete
    void deleteEpisode(CachedEpisode episode);

    @Query("DELETE FROM cached_episodes WHERE id = :episodeId")
    void deleteEpisodeById(String episodeId);

    @Query("DELETE FROM cached_episodes WHERE serie_id = :serieId")
    void deleteEpisodesBySerieId(String serieId);

    @Query("DELETE FROM cached_episodes")
    void deleteAllEpisodes();

    @Query("DELETE FROM cached_episodes WHERE cache_timestamp < :expiredTimestamp")
    void deleteExpiredEpisodes(long expiredTimestamp);

    @Query("SELECT COUNT(*) FROM cached_episodes")
    int getEpisodeCount();

    @Query("SELECT COUNT(*) FROM cached_episodes WHERE serie_id = :serieId")
    int getEpisodeCountBySerieId(String serieId);
}