package com.m6.gocook.util.cache.util;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.util.cache.util.ImageCache.ImageCacheParams;
import com.m6.gocook.util.preference.PrefHelper;

public class CacheUtils {
	
	private static final String CACHE_FLAG_TIME = "cache_flag_time";
	private static final long CACHE_CLEAR_INTERVAL = 1000 * 60 * 60 * 24 * 7; // 7天

	/**
     * 清除缓存(内存+外存)
     * @param context
     */
    public static void clearCache(Context context) {
    	if (needClearCache(context)) {
    		try {
    			ImageFetcher imageFetcher = new ImageFetcher(context, 0);
    			ImageCacheParams cacheParams = new ImageCacheParams(context, Constants.IMAGE_CACHE_DIR);
    			imageFetcher.addImageCache(((FragmentActivity) context).getSupportFragmentManager(), cacheParams);
    			imageFetcher.clearCache();
    		} catch (Exception e) {
    		}
    	}
    }
    
    private static boolean needClearCache(Context context) {
    	long lastClearTime = PrefHelper.getLong(context, CACHE_FLAG_TIME, 0);
    	long curTime = System.currentTimeMillis();
    	if (curTime - lastClearTime < CACHE_CLEAR_INTERVAL) {
    		return false;
    	}
    	PrefHelper.putLong(context, CACHE_FLAG_TIME, curTime);
    	return true;
    }
}
