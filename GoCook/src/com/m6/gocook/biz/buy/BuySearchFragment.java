package com.m6.gocook.biz.buy;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.response.CKeywordQueryResult;
import com.m6.gocook.base.fragment.BaseListFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.view.ActionBar;

public class BuySearchFragment extends BaseListFragment {

	public static final String PARAM_KEYWORD = "param_keyword";
	
	private static final int ROWS_PER_PAGE = 10;
	
	private BuySearchTask mBuySearchTask;
	private BuySearchAdapter mAdapter;
	private CKeywordQueryResult mCKeywordQueryResult;
	
	private String mKeyword;
	private int mPageIndex;
	private int mPageRows;
	
	private boolean mHaveNext = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if (args != null) {
			mKeyword = args.getString(PARAM_KEYWORD);
			mPageIndex = 1;
			mPageRows = ROWS_PER_PAGE;
		}
		mAdapter = new BuySearchAdapter(getActivity(), mCKeywordQueryResult);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.biz_buy_search_fragment_title, mKeyword));
	}
	
	@Override
	protected void executeTask() {
		if (mBuySearchTask == null) {
			mBuySearchTask = new BuySearchTask(getActivity(), mKeyword, mPageIndex, mPageRows);
			mBuySearchTask.execute((Void) null); 
		}
		
	}
	
	@Override
	protected boolean haveNext() {
		return mHaveNext;
	}

	@Override
	protected BaseAdapter getAdapter() {
		return mAdapter;
	}
	
	@Override
	public void onListItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (mAdapter != null) {
			Bundle bundle = new Bundle();
			bundle.putSerializable(BuyDetailsFragment.PARAM_RESULT, mAdapter.getItem(arg2));
			Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class,
					BuyDetailsFragment.class.getName(), BuyDetailsFragment.class.getName(), bundle);
			startActivity(intent);
		}
	}
	
	private class BuySearchTask extends AsyncTask<Void, Void, CKeywordQueryResult> {
		
		private String mKeyword;
		private int mPageIndex;
		private int mPageRows;
		
		private Context mContext;
		
		public BuySearchTask(Context context, String keyword, int pageIndex, int pageRows) {
			mContext = context.getApplicationContext();
			mKeyword = keyword;
			mPageIndex = pageIndex;
			mPageRows = pageRows;
		}

		@Override
		protected CKeywordQueryResult doInBackground(Void... params) {
			return BuyModel.getBuySearchResult(mContext, mKeyword, mPageIndex, mPageRows);
		}
		
		@Override
		protected void onPostExecute(CKeywordQueryResult result) {
			mBuySearchTask = null;
			if (isAdded()) {
				showProgress(false);
				if (result != null && result.getRows() != null && !result.getRows().isEmpty() && mAdapter != null) {
					mAdapter.setData(result);
					mAdapter.notifyDataSetChanged();
					
					if (result.getRows().size() >= ROWS_PER_PAGE) {
						mHaveNext = true;
					}
				} else {
					setEmptyMessage(R.string.biz_buy_search_fragment_empty);
					showEmpty(true);
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mBuySearchTask = null;
			if (isAdded()) {
				showProgress(false);
			}
		}
	}

}
