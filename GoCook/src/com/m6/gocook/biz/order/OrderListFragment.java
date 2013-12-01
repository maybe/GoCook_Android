package com.m6.gocook.biz.order;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.response.COrderQueryResult;
import com.m6.gocook.base.fragment.BaseListFragment;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;

public class OrderListFragment extends BaseListFragment {

	private OrdersTask mOrdersTask;
	private OrderListAdapter mAdapter;
	
	private String mStartDate;
	private String mEndDate;
	
	private static final int QUERY_MONTH_COUNT = 6;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		mAdapter = new OrderListAdapter(getActivity());
		
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		mEndDate = df.format(new Date());
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MONTH, -QUERY_MONTH_COUNT);
		calendar.set(Calendar.DATE, 1);
		mStartDate = df.format(calendar.getTime());
		
		setEmptyMessage(getString(R.string.biz_buy_order_empty_message, QUERY_MONTH_COUNT));
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.biz_buy_order_list_title, AccountModel.getUsername(getActivity())));
	}
	
	@Override
	protected void executeTask(int pageIndex) {
		if (mOrdersTask == null) {
			mOrdersTask = new OrdersTask(getActivity(), mStartDate, mEndDate, pageIndex);
			mOrdersTask.execute((Void) null); 
		}
	}

	@Override
	protected BaseAdapter getAdapter() {
		return mAdapter;
	}
	
	@Override
	public void onListItemClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		OrderDetailsFragment.goToOrderDetails(getActivity(), mAdapter.getItem(arg2));
	}
	
	private class OrdersTask extends AsyncTask<Void, Void, COrderQueryResult> {

		private Context mContext;
		private int mPage;
		private String mStartDate;
		private String mEndDate;
		
		public OrdersTask(Context context, String startDate, String endDate, int pageIndex) {
			mContext = context.getApplicationContext();
			mStartDate = startDate;
			mEndDate = endDate;
			mPage = pageIndex;
		}
		
		@Override
		protected COrderQueryResult doInBackground(Void... params) {
			return OrderModel.getOrderQueryResult(mContext, mStartDate, mEndDate, mPage);
		}
		
		@Override
		protected void onPostExecute(COrderQueryResult result) {
			mOrdersTask = null;
			if (isAdded()) {
				if (mAdapter != null && result != null) {
					mAdapter.setData(result);
				} else {
					showEmpty(true);
				}
				showProgress(false);
			}
		}
		
		@Override
		protected void onCancelled() {
			mOrdersTask = null;
			if (isAdded()) {
				showProgress(false);
			}
		}
		
	}
}
