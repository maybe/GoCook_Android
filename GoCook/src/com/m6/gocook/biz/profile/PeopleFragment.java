package com.m6.gocook.biz.profile;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
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
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.util.net.NetUtils;

public class PeopleFragment extends BaseListFragment implements OnActivityAction {

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
		MainActivityHelper.registerOnActivityActionListener(this);
		Bundle bundle = getArguments();
		if (bundle != null) {
			mPeopleListType = bundle.getInt(TYPE);
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		MainActivityHelper.unRegisterOnActivityActionListener(this);
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
								ProfileFragment.PROFILE_OTHERS, mAdapter.getItem(position).getId(), true);
					}
				}
				
			});
		}
	}
	
	@Override
	protected String getURL() {
		if (mPeopleListType == PeopleFragment.FOLLOWS) {
			return Protocol.URL_PROFILE_MY_FOLLOWS;
		} else {
			return Protocol.URL_PROFILE_MY_FANS;
		}
	}

	@Override
	protected void executeTask(int pageIndex) {
		if(mPeopleListTask == null) {
			mPeopleListTask = new PeopleListTask(getActivity(), getURLWithPageNum());
			mPeopleListTask.execute((Void) null); 
		}
	}

	@Override
	protected BaseAdapter getAdapter() {
		return mAdapter;
	}
	
	@Override
	protected void refresh() {
		if (mPeoples != null) {
			mPeoples.clear();
			mPeoples = null;
		}
		super.refresh();
	}
	
	@Override
	public void onCustomActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == MainActivityHelper.REQUEST_CODE_FOLLOW && resultCode == MainActivityHelper.RESULT_CODE_FOLLOW) {
			refresh();
		}
	}

	private class PeopleListTask extends AsyncTask<Void, Void, ArrayList<People>> {

		private Context mContext;
		private int mPeopleType;
		private String mUrl;
		
		public PeopleListTask(Context context, String url) {
			mContext = context.getApplicationContext();
			mUrl = url;
		}
		
		@Override
		protected ArrayList<People> doInBackground(Void... params) {
			return ProfileModel.getPeoples(mContext, mUrl);
		}
		
		@Override
		protected void onPostExecute(ArrayList<People> result) {
			mPeopleListTask = null;
			if (!isAdded()) {
				return;
			}
			
			showProgress(false);
			hideFooter();
			
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
			} else {
				setEmptyMessage(getString(R.string.base_empty_text));
				showEmpty(true);
			}
		}
		
		@Override
		protected void onCancelled() {
			mPeopleListTask = null;
			showProgress(false);
		}
	}
}
