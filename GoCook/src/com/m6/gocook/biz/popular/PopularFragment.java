package com.m6.gocook.biz.popular;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.Popular;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.fragment.OnKeyDown;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.recipe.search.SearchFragment;

public class PopularFragment extends BaseFragment implements OnKeyDown {
	
	private PopularTask mTask;
	private Popular mPopular;
	private PopularAdapter mAdapter;
	
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
		
		mAdapter = null;
		mPopular = null;
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
				if (data != null) {
					performSearch(data.first);
				}
			}
		});
		
		mAdapter = new PopularAdapter(getActivity(), mImageFetcher, mPopular);
		listView.setAdapter(mAdapter);
		
		if(mPopular == null) {
			if(mTask == null) {
				showProgress(true);
				mTask = new PopularTask(getActivity());
				mTask.execute((Void) null);
			}
		} else {
			updateView(mPopular);
		}
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
				if (isAdded()) {
					getView().findViewById(R.id.mask).setVisibility(View.GONE);
				}
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
		if (TextUtils.isEmpty(keyWords)) {
			Toast.makeText(getActivity(), R.string.biz_search_empty_tip, Toast.LENGTH_SHORT).show();
			return;
		}
		Bundle args = new Bundle();
		args.putString(SearchFragment.PARAM_KEYWORDS, keyWords);
        Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, SearchFragment.class.getName(), SearchFragment.class.getName(), args);
        startActivity(intent);
	}
	
	/**
	 * 取得数据更新view
	 * 
	 * @param popular
	 */
	private void updateView(Popular popular) {
		if(mAdapter != null) {
			mAdapter.setPopularData(popular);
			mAdapter.notifyDataSetChanged();
		}
	}
	
    private class PopularTask extends AsyncTask<Void, Void, Popular> {

    	private Context mContext;
    	
    	public PopularTask(FragmentActivity context) {
    		mContext = context.getApplicationContext();
		}
    	
		@Override
		protected Popular doInBackground(Void... params) {
			return PopularModel.getPopularData();
		}
    	
		@Override
		protected void onPostExecute(Popular result) {
			mTask = null;
			if (isAdded()) {
				showProgress(false);
			}
			
			if(mContext != null && result != null) {
				mPopular = result;
				updateView(result);
			}
		}
		
		@Override
		protected void onCancelled() {
			mTask = null;
			showProgress(false);
		}
    }

}
