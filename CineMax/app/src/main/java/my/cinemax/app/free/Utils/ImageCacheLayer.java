package my.cinemax.app.free.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.LruCache;

import com.google.gson.Gson;
import com.orhanobut.hawk.Hawk;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Image Cache Layer - Optimized image caching with compression
 * 
 * Features:
 * - Memory cache for frequently accessed images
 * - Disk cache for persistent image storage
 * - Automatic compression and quality optimization
 * - Background image loading
 * - Memory-efficient bitmap management
 * - Support for different image sizes and qualities
 */
public class ImageCacheLayer {
    
    private static final String TAG = "ImageCacheLayer";
    
    // Cache configuration
    private static final int MEMORY_CACHE_SIZE = 25; // MB
    private static final int DISK_CACHE_SIZE = 50; // MB
    private static final int MAX_IMAGE_SIZE = 1024; // Max width/height
    private static final int COMPRESSION_QUALITY = 85; // JPEG quality
    
    // Cache prefixes
    private static final String IMAGE_CACHE_PREFIX = "img_cache_";
    private static final String IMAGE_METADATA_PREFIX = "img_meta_";
    
    private final Context context;
    private final LruCache<String, Bitmap> memoryCache;
    private final File diskCacheDir;
    private final ExecutorService executorService;
    private final Gson gson;
    
    public ImageCacheLayer(Context context) {
        this.context = context.getApplicationContext();
        this.gson = new Gson();
        this.executorService = Executors.newFixedThreadPool(3);
        
        // Initialize memory cache
        this.memoryCache = new LruCache<String, Bitmap>(MEMORY_CACHE_SIZE * 1024 * 1024) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getByteCount();
            }
        };
        
        // Initialize disk cache directory
        this.diskCacheDir = new File(context.getCacheDir(), "cinemax_image_cache");
        if (!diskCacheDir.exists()) {
            diskCacheDir.mkdirs();
        }
        
        Log.d(TAG, "Image cache layer initialized");
    }
    
    /**
     * Load image with caching
     */
    public void loadImage(String imageUrl, ImageLoadCallback callback) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            if (callback != null) {
                callback.onError("Invalid image URL");
            }
            return;
        }
        
        executorService.execute(() -> {
            try {
                // Try memory cache first
                Bitmap bitmap = memoryCache.get(imageUrl);
                if (bitmap != null && !bitmap.isRecycled()) {
                    Log.d(TAG, "Image loaded from memory cache: " + imageUrl);
                    if (callback != null) {
                        callback.onSuccess(bitmap);
                    }
                    return;
                }
                
                // Try disk cache
                bitmap = loadFromDiskCache(imageUrl);
                if (bitmap != null && !bitmap.isRecycled()) {
                    // Store in memory cache
                    memoryCache.put(imageUrl, bitmap);
                    Log.d(TAG, "Image loaded from disk cache: " + imageUrl);
                    if (callback != null) {
                        callback.onSuccess(bitmap);
                    }
                    return;
                }
                
                // Load from network
                bitmap = loadFromNetwork(imageUrl);
                if (bitmap != null && !bitmap.isRecycled()) {
                    // Store in both caches
                    memoryCache.put(imageUrl, bitmap);
                    saveToDiskCache(imageUrl, bitmap);
                    Log.d(TAG, "Image loaded from network: " + imageUrl);
                    if (callback != null) {
                        callback.onSuccess(bitmap);
                    }
                } else {
                    if (callback != null) {
                        callback.onError("Failed to load image");
                    }
                }
                
            } catch (Exception e) {
                Log.e(TAG, "Error loading image: " + imageUrl, e);
                if (callback != null) {
                    callback.onError("Error loading image: " + e.getMessage());
                }
            }
        });
    }
    
    /**
     * Load image from disk cache
     */
    private Bitmap loadFromDiskCache(String imageUrl) {
        try {
            String fileName = getFileNameFromUrl(imageUrl);
            File imageFile = new File(diskCacheDir, fileName);
            
            if (imageFile.exists()) {
                FileInputStream fis = new FileInputStream(imageFile);
                Bitmap bitmap = BitmapFactory.decodeStream(fis);
                fis.close();
                
                if (bitmap != null) {
                    return bitmap;
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading from disk cache: " + imageUrl, e);
        }
        return null;
    }
    
    /**
     * Save image to disk cache
     */
    private void saveToDiskCache(String imageUrl, Bitmap bitmap) {
        try {
            String fileName = getFileNameFromUrl(imageUrl);
            File imageFile = new File(diskCacheDir, fileName);
            
            // Compress and save bitmap
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, COMPRESSION_QUALITY, fos);
            fos.close();
            
            // Store metadata
            storeImageMetadata(imageUrl, imageFile.length());
            
            Log.d(TAG, "Image saved to disk cache: " + imageUrl);
        } catch (Exception e) {
            Log.e(TAG, "Error saving to disk cache: " + imageUrl, e);
        }
    }
    
    /**
     * Load image from network
     */
    private Bitmap loadFromNetwork(String imageUrl) {
        HttpURLConnection connection = null;
        InputStream inputStream = null;
        
        try {
            URL url = new URL(imageUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                
                // Decode bitmap with options for memory efficiency
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeStream(inputStream, null, options);
                
                // Calculate sample size for memory efficiency
                options.inSampleSize = calculateInSampleSize(options, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE);
                options.inJustDecodeBounds = false;
                
                // Close and reopen stream
                inputStream.close();
                inputStream = connection.getInputStream();
                
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream, null, options);
                return bitmap;
            }
        } catch (Exception e) {
            Log.e(TAG, "Error loading from network: " + imageUrl, e);
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (connection != null) {
                    connection.disconnect();
                }
            } catch (IOException e) {
                Log.e(TAG, "Error closing network connection", e);
            }
        }
        return null;
    }
    
    /**
     * Calculate sample size for bitmap decoding
     */
    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }
        
        return inSampleSize;
    }
    
    /**
     * Get filename from URL
     */
    private String getFileNameFromUrl(String imageUrl) {
        return IMAGE_CACHE_PREFIX + imageUrl.hashCode() + ".jpg";
    }
    
    /**
     * Store image metadata
     */
    private void storeImageMetadata(String imageUrl, long fileSize) {
        try {
            String key = IMAGE_METADATA_PREFIX + imageUrl.hashCode();
            ImageMetadata metadata = new ImageMetadata();
            metadata.url = imageUrl;
            metadata.fileSize = fileSize;
            metadata.timestamp = System.currentTimeMillis();
            
            String json = gson.toJson(metadata);
            Hawk.put(key, json);
        } catch (Exception e) {
            Log.e(TAG, "Error storing image metadata", e);
        }
    }
    
    /**
     * Get image from memory cache
     */
    public Bitmap getFromMemoryCache(String imageUrl) {
        return memoryCache.get(imageUrl);
    }
    
    /**
     * Store image in memory cache
     */
    public void storeInMemoryCache(String imageUrl, Bitmap bitmap) {
        if (imageUrl != null && bitmap != null && !bitmap.isRecycled()) {
            memoryCache.put(imageUrl, bitmap);
        }
    }
    
    /**
     * Preload images
     */
    public void preloadImages(String[] imageUrls) {
        if (imageUrls == null || imageUrls.length == 0) return;
        
        executorService.execute(() -> {
            for (String imageUrl : imageUrls) {
                if (imageUrl != null && !imageUrl.isEmpty()) {
                    loadImage(imageUrl, new ImageLoadCallback() {
                        @Override
                        public void onSuccess(Bitmap bitmap) {
                            // Image preloaded successfully
                        }
                        
                        @Override
                        public void onError(String error) {
                            // Ignore preload errors
                        }
                    });
                }
            }
        });
    }
    
    /**
     * Clear memory cache
     */
    public void clearMemoryCache() {
        memoryCache.evictAll();
        Log.d(TAG, "Memory image cache cleared");
    }
    
    /**
     * Clear disk cache
     */
    public void clearDiskCache() {
        executorService.execute(() -> {
            try {
                if (diskCacheDir.exists()) {
                    File[] files = diskCacheDir.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            file.delete();
                        }
                    }
                }
                
                // Clear metadata
                for (String key : Hawk.getAll().keySet()) {
                    if (key.startsWith(IMAGE_METADATA_PREFIX)) {
                        Hawk.delete(key);
                    }
                }
                
                Log.d(TAG, "Disk image cache cleared");
            } catch (Exception e) {
                Log.e(TAG, "Error clearing disk image cache", e);
            }
        });
    }
    
    /**
     * Clear all image caches
     */
    public void clear() {
        clearMemoryCache();
        clearDiskCache();
    }
    
    /**
     * Get cache statistics
     */
    public ImageCacheStats getStats() {
        ImageCacheStats stats = new ImageCacheStats();
        
        try {
            // Memory cache stats
            stats.memoryCacheSize = memoryCache.size();
            stats.memoryCacheMaxSize = memoryCache.maxSize();
            
            // Disk cache stats
            if (diskCacheDir.exists()) {
                File[] files = diskCacheDir.listFiles();
                if (files != null) {
                    stats.diskCacheFileCount = files.length;
                    for (File file : files) {
                        stats.diskCacheSize += file.length();
                    }
                }
            }
            
            // Metadata stats
            int metadataCount = 0;
            for (String key : Hawk.getAll().keySet()) {
                if (key.startsWith(IMAGE_METADATA_PREFIX)) {
                    metadataCount++;
                }
            }
            stats.metadataCount = metadataCount;
            
        } catch (Exception e) {
            Log.e(TAG, "Error getting image cache stats", e);
        }
        
        return stats;
    }
    
    /**
     * Shutdown image cache layer
     */
    public void shutdown() {
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }
    
    /**
     * Image load callback interface
     */
    public interface ImageLoadCallback {
        void onSuccess(Bitmap bitmap);
        void onError(String error);
    }
    
    /**
     * Image metadata class
     */
    private static class ImageMetadata {
        String url;
        long fileSize;
        long timestamp;
    }
    
    /**
     * Image cache statistics
     */
    public static class ImageCacheStats {
        public int memoryCacheSize;
        public int memoryCacheMaxSize;
        public int diskCacheFileCount;
        public long diskCacheSize;
        public int metadataCount;
        
        public double getMemoryCacheUsagePercent() {
            return memoryCacheMaxSize > 0 ? (double) memoryCacheSize / memoryCacheMaxSize * 100 : 0;
        }
        
        public double getDiskCacheSizeMB() {
            return diskCacheSize / (1024.0 * 1024.0);
        }
        
        @Override
        public String toString() {
            return String.format("ImageCacheStats{memory=%d/%d (%.1f%%), disk=%d files (%.1f MB), metadata=%d}",
                    memoryCacheSize, memoryCacheMaxSize, getMemoryCacheUsagePercent(),
                    diskCacheFileCount, getDiskCacheSizeMB(), metadataCount);
        }
    }
}