package com.m6.gocook.biz.coupon;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.Coupon;
import com.m6.gocook.base.fragment.BaseListFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.base.view.ActionBar.OnActionBarClick;
import com.m6.gocook.biz.main.MainActivityHelper;

public class CouponListFragment extends BaseListFragment implements OnActionBarClick, OnActivityAction {

	private CouponListAdapter mAdapter;
	
	private CouponsTask mCouponsTask;
	
	private List<Coupon> mData = new ArrayList<Coupon>();
	
	private boolean mHaveNext = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivityHelper.registerOnActivityActionListener(this);
		mAdapter = new CouponListAdapter(getActivity(), mData);
	}
	
	@Override
	public void onDestroy() {
		MainActivityHelper.unRegisterOnActivityActionListener(this);
		super.onDestroy();
	}
	
	@Override
	public View onCreateListView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_coupon_listview, container, false);
	}
	
	@Override
	public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container) {
		return inflater.inflate(R.layout.fragment_coupon_list_header, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setActionBarClickListener(this);
		actionBar.setTitle(R.string.biz_coupon_list_title);
		actionBar.setRightButton(null, R.drawable.actionbar_refresh_selector);
		
		view.findViewById(R.id.shake).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
						ShakeFragment.class.getName(), ShakeFragment.class.getName(), null);
				startActivity(intent);
			}
		});
	}
	
	@Override
	protected void refresh() {
		mData.clear();
		super.refresh();
	}
	
	@Override
	public void onActionBarRightButtonClick(View v) {
		refresh();
	}
	
	@Override
	public void onListItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		FragmentHelper.startActivity(getActivity(), CouponDetailsFragment.newInstance(mAdapter.getItem(arg2)));
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}
	
	@Override
	protected boolean haveNext() {
		return mHaveNext;
	}
	
	@Override
	protected void executeTask(int pageIndex) {
		if (mCouponsTask == null) {
			mCouponsTask = new CouponsTask(getActivity(), pageIndex);
			mCouponsTask.execute((Void) null);
		}
		
	}

	@Override
	protected BaseAdapter getAdapter() {
		return mAdapter;
	}
	
	private class CouponsTask extends AsyncTask<Void, Void, List<Coupon>> {

		private Context mContext;
		private int mPage;
		
		public CouponsTask(Context context, int page) {
			mContext = context.getApplicationContext();
			mPage = page;
		}
		
		@Override
		protected List<Coupon> doInBackground(Void... params) {
			return CouponModel.getCoupons(mContext, mPage);
		}
		
		@Override
		protected void onPostExecute(List<Coupon> result) {
			mCouponsTask = null;
			if (result != null) {
				mData.addAll(result);
				if (result.size() >= COUNT_PER_PAGE) {
					mHaveNext = true;
				}
			}
			if (isAdded()) {
				showProgress(false);
				if (mAdapter != null) {
					mAdapter.notifyDataSetChanged();
				}
			}
		}
		
		@Override
		protected void onCancelled() {
			mCouponsTask = null;
			if (isAdded()) {
				showProgress(false);
			}
		}
	}

	@Override
	public void onCustomActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (resultCode == MainActivityHelper.RESULT_CODE_COUPON) {
			refresh();
		}
	}
}
