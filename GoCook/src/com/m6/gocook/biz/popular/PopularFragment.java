package com.m6.gocook.biz.popular;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.entity.Popular;
import com.m6.gocook.base.entity.RecipeListItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.biz.recipe.search.SearchFragment;
import com.m6.gocook.util.cache.util.ImageCache.ImageCacheParams;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class PopularFragment extends Fragment {

	private ImageFetcher mImageFetcher;
	
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
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_popular, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		final EditText searchEditText = (EditText) getActivity().findViewById(R.id.search_box);
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH) {
					Bundle args = new Bundle();
					args.putString(SearchFragment.PARAM_KEYWORDS, searchEditText.getText().toString());
                    Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, SearchFragment.class.getName(), SearchFragment.class.getName(), args);
                    startActivity(intent);
                    return true;
                }
                return false;
			}
		});
		
		new PopularTask(getActivity(), mImageFetcher).execute((Void) null);
	}
	
	@Override
    public void onResume() {
        super.onResume();
        mImageFetcher.setExitTasksEarly(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mImageFetcher.setPauseWork(false);
        mImageFetcher.setExitTasksEarly(true);
        mImageFetcher.flushCache();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mImageFetcher.closeCache();
    }
    
    private static class PopularTask extends AsyncTask<Void, Void, Popular> {

    	private FragmentActivity mContext;
    	private ImageFetcher mImageFetcher;
    	
    	public PopularTask(FragmentActivity context, ImageFetcher imageFetcher) {
    		mContext = context;
    		mImageFetcher = imageFetcher;
		}
		@Override
		protected Popular doInBackground(Void... params) {
			return PopularModel.getPopularData();
		}
    	
		@Override
		protected void onPostExecute(Popular result) {
			if(mContext != null && result != null) {
				PopularAdapter adapter = new PopularAdapter(mContext, mImageFetcher, result);
				ListView list = (ListView) mContext.findViewById(R.id.list);
				list.setAdapter(adapter);
			}
		}
    }
    
    
}
