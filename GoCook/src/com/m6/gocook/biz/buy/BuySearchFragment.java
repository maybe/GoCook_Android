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
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.main.MainActivityHelper;

public class BuySearchFragment extends BaseListFragment implements OnActivityAction {

	public static final String PARAM_KEYWORD = "param_keyword";
	public static final String PARAM_RECORD_ID = "param_record_id";
	
	private static final int ROWS_PER_PAGE = 10;
	
	private BuySearchTask mBuySearchTask;
	private BuySearchAdapter mAdapter;
	private CKeywordQueryResult mCKeywordQueryResult;
	
	private String mKeyword;
	private int mPageIndex;
	private int mPageRows;
	private String mRecordId;
	
	private boolean mHaveNext = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivityHelper.registerOnActivityActionListener(this);
		
		Bundle args = getArguments();
		if (args != null) {
			mKeyword = args.getString(PARAM_KEYWORD);
			mRecordId = args.getString(PARAM_RECORD_ID);
			mPageIndex = 1;
			mPageRows = ROWS_PER_PAGE;
		}
		mAdapter = new BuySearchAdapter(getActivity(), mCKeywordQueryResult);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		MainActivityHelper.unRegisterOnActivityActionListener(this);
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
			bundle.putString(PARAM_RECORD_ID, mRecordId);
			Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class,
					BuyDetailsFragment.class.getName(), BuyDetailsFragment.class.getName(), bundle);
			startActivityForResult(intent, MainActivityHelper.REQUEST_CODE_INPUT);
		}
	}
	
	@Override
	public void onCustomActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == MainActivityHelper.RESULT_CODE_INPUT) {
			getActivity().setResult(MainActivityHelper.RESULT_CODE_INPUT, data);
			getActivity().finish();
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
