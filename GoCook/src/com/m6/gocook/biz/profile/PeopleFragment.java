package com.m6.gocook.biz.profile;

import java.util.ArrayList;

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
import com.m6.gocook.base.view.ActionBar;

public class PeopleFragment extends BaseListFragment {

	private PeopleListTask mPeopleListTask;
	private PeopleAdapter mAdapter;
	private ArrayList<People> mPeoples;
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		mAdapter = new PeopleAdapter(getActivity(), mImageFetcher, mPeoples);
		super.onActivityCreated(savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(getString(R.string.biz_profile_myaccount_fans_title, "卖萌蜀黎喵呜桑"));
		
		ListView listView = getListView();
		if(listView != null) {
			listView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					
				}
				
			});
		}
	}
	@Override
	protected String getURL() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void executeTask() {
		if(mPeopleListTask == null) {
			mPeopleListTask = new PeopleListTask(getURLWithPageNum());
			mPeopleListTask.execute((Void) null); 
		}
	}


	@Override
	protected BaseAdapter getAdapter() {
		return mAdapter;
	}

	private class PeopleListTask extends AsyncTask<Void, Void, ArrayList<People>> {

		private String mUrl;
		
		public PeopleListTask(String url) {
			mUrl = url;
		}
		
		@Override
		protected ArrayList<People> doInBackground(Void... params) {
			return ProfileModel.getPeoples();
		}
		
		@Override
		protected void onPostExecute(ArrayList<People> result) {
			mPeopleListTask = null;
			showProgress(false);
			mFooterView.setVisibility(View.GONE);
			
			if (result != null) {
				if(mAdapter != null) {
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
		}
		
		@Override
		protected void onCancelled() {
			mPeopleListTask = null;
			showProgress(false);
		}
	}
}
