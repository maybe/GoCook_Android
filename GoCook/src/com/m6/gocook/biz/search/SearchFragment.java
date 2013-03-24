package com.m6.gocook.biz.search;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.m6.gocook.R;
import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.SearchHistory;
import com.m6.gocook.base.db.table.SearchList;
import com.m6.gocook.base.view.SearchListView;

public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private SimpleCursorAdapter mAdapter;
	private SimpleCursorAdapter mHistoryAdapter;
	
	private SearchHistoryTask mHistoryTask;
	
	private SearchListView mListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search_layout, container, false); 
		mListView = (SearchListView) view.findViewById(R.id.search_list);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		View view = getView();
		EditText searchBox = (EditText) view.findViewById(R.id.search_box);
		
		String[] from = {SearchList.NAME, SearchList.TREND};
		int[] to = {R.id.name, R.id.trend};
		mAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.fragment_search_list_item,
				null, 
				from, 
				to, 
				CursorAdapter.FLAG_AUTO_REQUERY);
		mListView.setSearchResultAdapter(mAdapter);
		
		String[] hFrom = {SearchHistory.CONTENT};
		int[] hTo = {R.id.content};
		mHistoryAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.fragment_search_history_item, 
				null,
				hFrom, 
				hTo,
				CursorAdapter.FLAG_AUTO_REQUERY);
		mListView.setHistoryOrSuggestAdapter(mHistoryAdapter);
		
		searchBox.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if(mHistoryTask != null) {
					mHistoryTask.cancel(true);
				}
				mHistoryTask = new SearchHistoryTask(getActivity());
				mHistoryTask.equals(null);
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}
		});
		
		new SearchListTask(this).equals(null);
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		Uri uri = GoCookProvider.getTableUri(SearchList.TABLE);
		return new CursorLoader(getActivity(), uri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
		if(mListView != null) {
			mListView.setResultListShown();
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
	
	private static class SearchListTask extends AsyncTask<Void, Void, Void> {
		
		private Fragment mFragment;
		
		public SearchListTask(Fragment fragment) {
			mFragment = fragment;
		}

		@Override
		protected Void doInBackground(Void... params) {
			SearchModel.getSearchList(mFragment.getActivity());
			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			mFragment.getLoaderManager().initLoader(0, null, (LoaderCallbacks) mFragment);
		}
	}
	
	private class SearchHistoryTask extends AsyncTask<Void, Void, Cursor> {
		
		private Context mContext;
		
		public SearchHistoryTask(Context context) {
			mContext = context.getApplicationContext();
		}

		@Override
		protected Cursor doInBackground(Void... params) {
			Cursor cursor = SearchModel.readSearchHistory(mContext);
			return cursor;
		}

		@Override
		protected void onPostExecute(Cursor result) {
			mHistoryAdapter.swapCursor(result);
			if(mListView != null) {
				mListView.setHistoryListShown();
			}
		}
		
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mListView = null;
	}
}
