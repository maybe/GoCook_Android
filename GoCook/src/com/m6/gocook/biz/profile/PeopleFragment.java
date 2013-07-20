package com.m6.gocook.biz.profile;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.People;
import com.m6.gocook.base.fragment.BaseListFragment;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;

public class PeopleFragment extends BaseListFragment {

	private PeopleListTask mPeopleListTask;
	private PeopleAdapter mAdapter;
	private ArrayList<People> mPeoples;
	
	public static int FANS = 0;
	public static int FOLLOWS = 1;
	public static String TYPE = "type";
	
	private int mPeopleListType;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getArguments();
		if (bundle != null) {
			mPeopleListType = bundle.getInt(TYPE);
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mAdapter = new PeopleAdapter(getActivity(), mImageFetcher, mPeoples);
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		if (mPeopleListType == FOLLOWS) {
			actionBar.setTitle(getString(R.string.biz_profile_myaccount_follows_title, AccountModel.getUsername(getActivity())));
		} else {
			actionBar.setTitle(getString(R.string.biz_profile_myaccount_fans_title, AccountModel.getUsername(getActivity())));
		}
		
		final ListView listView = getListView();
		if(listView != null) {
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
					if (mAdapter != null) {
						ProfileFragment.startProfileFragment(getActivity(), 
								ProfileFragment.PROFILE_OTHERS, mAdapter.getItem(position).getId());
					}
				}
				
			});
		}
	}
	
	@Override
	protected String getURL() {
		return Protocol.URL_PROFILE_MY_FOLLOWS;
	}

	@Override
	protected void executeTask() {
		if(mPeopleListTask == null) {
			mPeopleListTask = new PeopleListTask(getActivity(), mPeopleListType);
			mPeopleListTask.execute((Void) null); 
		}
	}

	@Override
	protected BaseAdapter getAdapter() {
		return mAdapter;
	}

	private class PeopleListTask extends AsyncTask<Void, Void, ArrayList<People>> {

		private Context mContext;
		private int mPeopleType;
		
		public PeopleListTask(Context context, int type) {
			mContext = context.getApplicationContext();
			mPeopleType = type;
		}
		
		@Override
		protected ArrayList<People> doInBackground(Void... params) {
			return ProfileModel.getPeoples(mContext, mPeopleType);
		}
		
		@Override
		protected void onPostExecute(ArrayList<People> result) {
			mPeopleListTask = null;
			if (!isAdded()) {
				return;
			}
			
			showProgress(false);
			mFooterView.setVisibility(View.GONE);
			
			if (result != null && mAdapter != null) {
				if (result.isEmpty() && mAdapter.getCount() == 0) {
					if (mPeopleListType == FOLLOWS) {
						setEmptyMessage(getString(R.string.biz_profile_follows_empty_message));
					} else if (mPeopleListType == FANS) {
						setEmptyMessage(getString(R.string.biz_profile_fans_empty_message));
					}
					showEmpty(true);
					return;
				}
				
				if(mPeoples == null) { // 第一页
					mPeoples = result;
				} else {
					if(mPeoples != null) {
						mPeoples.addAll(result);
					}
				}
				mAdapter.setPeoples(mPeoples);
				mAdapter.notifyDataSetChanged();
			}
		}
		
		@Override
		protected void onCancelled() {
			mPeopleListTask = null;
			showProgress(false);
		}
	}
}
