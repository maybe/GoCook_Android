package com.m6.gocook.biz.popular;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
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
import com.m6.gocook.base.fragment.OnKeyDown;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.recipe.search.SearchFragment;
import com.m6.gocook.util.cache.util.ImageFetcher;

public class PopularFragment extends BaseFragment implements OnKeyDown {
	
	private PopularTask mTask;
	
	/** 搜索框是否处于输入状态 */
	private boolean mInputMode = false;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivityHelper.registerOnKeyDownListener(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		MainActivityHelper.unRegisterOnKeyDownListener(this);
	}
	
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
		
		if(mTask == null) {
			showProgress(true);
			mTask = new PopularTask(getActivity(), mImageFetcher);
			mTask.execute((Void) null);
		}
		
		final View maskView = getView().findViewById(R.id.mask);
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
		
		searchEditText.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(!mInputMode) {
					mInputMode = true;
					maskView.setVisibility(View.VISIBLE);
				}
			}
		});
		
		searchEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if(!mInputMode) {
					mInputMode = true;
					maskView.setVisibility(View.VISIBLE);
				}
			}
		});
		
		maskView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if(mInputMode) {
					mInputMode = false;
					maskView.setVisibility(View.GONE);
					hideSoftKeyBoard();
				}
				return true;
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
	
	@Override
	public void onResume() {
		super.onResume();
		
		mInputMode = false;
		getView().findViewById(R.id.mask).setVisibility(View.GONE);
		hideSoftKeyBoard();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			if(mInputMode) {
				mInputMode = false;
				getView().findViewById(R.id.mask).setVisibility(View.GONE);
				return true;
			}
		}
		return false;
	}
	
	private void hideSoftKeyBoard() {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(getActivity().INPUT_METHOD_SERVICE);
		if (null != imm) {
			imm.hideSoftInputFromWindow(
					((EditText) getActivity().findViewById(R.id.search_box)).getWindowToken(), 0);
		}
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
