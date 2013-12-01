package com.m6.gocook.base.image;

import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.m6.gocook.R;
import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.util.cache.util.ImageCache.ImageCacheParams;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class HeaderImageFetcher {

	public static ImageFetcher getInstance(Context context) {
		ImageCacheParams cacheParams = new ImageCacheParams(context, Constants.IMAGE_CACHE_DIR);
        cacheParams.setMemCacheSizePercent(0.25f);
        
		ImageFetcher headerImageFetcher = new ImageFetcher(context, 
				context.getResources().getDimensionPixelSize(R.dimen.biz_popular_item_header_image_width),
				context.getResources().getDimensionPixelSize(R.dimen.biz_popular_item_header_image_height));
		headerImageFetcher.addImageCache(((FragmentActivity) context).getSupportFragmentManager(), cacheParams);
		headerImageFetcher.setImageFadeIn(false);
		
		return headerImageFetcher;
	}
}
