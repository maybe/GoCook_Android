package com.m6.gocook.biz.buy;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.BaseAdapter;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.BaseListFragment;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;

public class OrderListFragment extends BaseListFragment {

	private OrdersTask mOrdersTask;
	private OrderListAdapter mAdapter;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		mAdapter = new OrderListAdapter(getActivity());
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.biz_buy_order_list_title, AccountModel.getUsername(getActivity())));
	}
	
	@Override
	protected void executeTask() {
		if (mOrdersTask == null) {
			mOrdersTask = new OrdersTask(getActivity());
			mOrdersTask.execute((Void) null); 
		}
	}

	@Override
	protected BaseAdapter getAdapter() {
		return mAdapter;
	}
	
	private class OrdersTask extends AsyncTask<Void, Void, Void> {

		private Context mContext;
		
		public OrdersTask(Context context) {
			mContext = context.getApplicationContext();
		}
		
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			mOrdersTask = null;
			if (isAdded()) {
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
