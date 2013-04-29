package com.m6.gocook.base.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.m6.gocook.R;
import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.util.cache.util.ImageFetcher;
import com.m6.gocook.util.cache.util.ImageCache.ImageCacheParams;

public class BaseFragment extends Fragment {

	private ActionBar mAction;
	protected ImageFetcher mImageFetcher;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		int imageThumbSize = getResources().getDimensionPixelSize(R.dimen.biz_popular_item_image_size);
	    ImageCacheParams cacheParams = new ImageCacheParams(getActivity(), Constants.IMAGE_CACHE_DIR);

        cacheParams.setMemCacheSizePercent(0.25f); // Set memory cache to 25% of app memory

        // The ImageFetcher takes care of loading images into our ImageView children asynchronously
        mImageFetcher = new ImageFetcher(getActivity(), imageThumbSize);
        mImageFetcher.addImageCache(getActivity().getSupportFragmentManager(), cacheParams);
        mImageFetcher.setImageFadeIn(false);
        
	}
	
	@Override
	public final View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		RelativeLayout root = (RelativeLayout) inflater.inflate(R.layout.base_fragment, container, false);
		
		View actionBarView = createDefaultActionBarView(inflater, container);
		mAction = new ActionBar(actionBarView);
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        lp.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        root.addView(actionBarView, lp);
		
		View content = onCreateFragmentView(inflater, container, savedInstanceState);
		if(content != null) {
			lp = new RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);
            lp.addRule(RelativeLayout.BELOW, actionBarView.getId());
            root.addView(content, lp);
		}
		return root;
	}
	
	/**
	 * Called to have the fragment instantiate its user interface view. This is optional.
	 * 
	 * @return Return the View for the fragment's UI, or null.
	 */
	public View onCreateFragmentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return null;
	}
	
	private View createDefaultActionBarView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.base_actionbar, container, false);
	}
	
	public ActionBar getAction() {
		return mAction;
	}
	
}
