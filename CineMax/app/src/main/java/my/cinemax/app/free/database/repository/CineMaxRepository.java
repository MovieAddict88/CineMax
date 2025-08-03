package my.cinemax.app.free.database.repository;

import android.content.Context;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import my.cinemax.app.free.database.CineMaxDatabase;
import my.cinemax.app.free.database.dao.ChannelDao;
import my.cinemax.app.free.database.dao.PosterDao;
import my.cinemax.app.free.database.entity.CachedChannel;
import my.cinemax.app.free.database.entity.CachedPoster;

public class CineMaxRepository {
    
    private PosterDao posterDao;
    private ChannelDao channelDao;
    private LiveData<List<CachedPoster>> allPosters;
    private LiveData<List<CachedChannel>> allChannels;
    private ExecutorService executorService;
    
    public CineMaxRepository(Context context) {
        CineMaxDatabase database = CineMaxDatabase.getInstance(context);
        posterDao = database.posterDao();
        channelDao = database.channelDao();
        allPosters = posterDao.getAllLive();
        allChannels = channelDao.getAllLive();
        executorService = Executors.newFixedThreadPool(4);
    }
    
    // Poster operations
    public void insert(CachedPoster poster) {
        executorService.execute(() -> posterDao.insert(poster));
    }
    
    public void insertAll(List<CachedPoster> posters) {
        executorService.execute(() -> posterDao.insertAll(posters));
    }
    
    public void update(CachedPoster poster) {
        executorService.execute(() -> posterDao.update(poster));
    }
    
    public void delete(CachedPoster poster) {
        executorService.execute(() -> posterDao.delete(poster));
    }
    
    public void deleteAllPosters() {
        executorService.execute(() -> posterDao.deleteAll());
    }
    
    public LiveData<List<CachedPoster>> getAllPosters() {
        return allPosters;
    }
    
    public LiveData<CachedPoster> getPosterById(int id) {
        return posterDao.getByIdLive(id);
    }
    
    public LiveData<List<CachedPoster>> getPostersByType(String type) {
        return posterDao.getByTypeLive(type);
    }
    
    public LiveData<List<CachedPoster>> getPostersByGenre(String genre) {
        return posterDao.getByGenreLive(genre);
    }
    
    public LiveData<List<CachedPoster>> searchPosters(String query) {
        return posterDao.searchLive(query);
    }
    
    public LiveData<List<CachedPoster>> getRecentPosters(long timestamp) {
        return posterDao.getRecentLive(timestamp);
    }
    
    public void deleteOldPosters(long timestamp) {
        executorService.execute(() -> posterDao.deleteOld(timestamp));
    }
    
    // Channel operations
    public void insert(CachedChannel channel) {
        executorService.execute(() -> channelDao.insert(channel));
    }
    
    public void insertAll(List<CachedChannel> channels) {
        executorService.execute(() -> channelDao.insertAll(channels));
    }
    
    public void update(CachedChannel channel) {
        executorService.execute(() -> channelDao.update(channel));
    }
    
    public void delete(CachedChannel channel) {
        executorService.execute(() -> channelDao.delete(channel));
    }
    
    public void deleteAllChannels() {
        executorService.execute(() -> channelDao.deleteAll());
    }
    
    public LiveData<List<CachedChannel>> getAllChannels() {
        return allChannels;
    }
    
    public LiveData<CachedChannel> getChannelById(int id) {
        return channelDao.getByIdLive(id);
    }
    
    public LiveData<List<CachedChannel>> getChannelsByCategory(String category) {
        return channelDao.getByCategoryLive(category);
    }
    
    public LiveData<List<CachedChannel>> getChannelsByCountry(String country) {
        return channelDao.getByCountryLive(country);
    }
    
    public LiveData<List<CachedChannel>> searchChannels(String query) {
        return channelDao.searchLive(query);
    }
    
    public LiveData<List<CachedChannel>> getRecentChannels(long timestamp) {
        return channelDao.getRecentLive(timestamp);
    }
    
    public void deleteOldChannels(long timestamp) {
        executorService.execute(() -> channelDao.deleteOld(timestamp));
    }
    
    // Utility methods
    public void cleanup() {
        executorService.shutdown();
    }
    
    public boolean isDataFresh(long lastUpdateTime, long maxAge) {
        return System.currentTimeMillis() - lastUpdateTime < maxAge;
    }
}