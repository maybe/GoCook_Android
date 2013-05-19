package com.m6.gocook.biz.popular;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.Popular;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.biz.recipe.search.SearchFragment;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class PopularFragment extends BaseFragment {
	
	private PopularTask mTask;

	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_popular, container, false);
	}
	
	@Override
	protected View onCreateActionBarView(LayoutInflater inflater,
			ViewGroup container) {
		return null;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		showProgress(true);
		
		mTask = new PopularTask(getActivity(), mImageFetcher);
		mTask.execute((Void) null);
		
		final EditText searchEditText = (EditText) getActivity().findViewById(R.id.search_box);
		searchEditText.setOnEditorActionListener(new OnEditorActionListener() {
			
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH) {
                    performSearch(searchEditText.getText().toString());
                    return true;
                }
                return false;
			}
		});
		
		final ListView listView = (ListView) getView().findViewById(R.id.list);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if(position == 0) {
					return;
				}
				
				Pair<String, String[]> data = (Pair<String, String[]>) listView.getAdapter().getItem(position);
                performSearch(data.first);
			}
		});
	}
	
	/**
	 * 执行搜索，跳转到搜索结果页
	 * 
	 * @param keyWords
	 */
	private void performSearch(String keyWords) {
		Bundle args = new Bundle();
		args.putString(SearchFragment.PARAM_KEYWORDS, keyWords);
        Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, SearchFragment.class.getName(), SearchFragment.class.getName(), args);
        startActivity(intent);
	}
	
    private class PopularTask extends AsyncTask<Void, Void, Popular> {

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
			mTask = null;
			showProgress(false);
			
			if(mContext != null && result != null) {
				PopularAdapter adapter = new PopularAdapter(mContext, mImageFetcher, result);
				ListView list = (ListView) mContext.findViewById(R.id.list);
				list.setAdapter(adapter);
			}
		}
		
		@Override
		protected void onCancelled() {
			mTask = null;
			showProgress(false);
		}
    }
    
    
}
