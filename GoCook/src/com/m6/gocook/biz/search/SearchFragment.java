package com.m6.gocook.biz.search;

import com.m6.gocook.R;
import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.SearchList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class SearchFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
	
	private ListView mListView;
	private SimpleCursorAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search_layout, container, false); 
		mListView = (ListView) view.findViewById(R.id.search_list);
		return view;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		String[] from = {SearchList.NAME, SearchList.TREND};
		int[] to = {R.id.name, R.id.trend};
		mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.fragment_search_list_item, null, from, to, CursorAdapter.FLAG_AUTO_REQUERY);
		mListView.setAdapter(mAdapter);
		
		getLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		return new SearchListTask(getActivity());
	}

	@Override
	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		mAdapter.swapCursor(data);
	}

	@Override
	public void onLoaderReset(Loader<Cursor> loader) {
		mAdapter.swapCursor(null);
	}
	
	private static class SearchListTask extends AsyncTaskLoader<Cursor> {

		private final Context mContext;
		
		public SearchListTask(Context context) {
			super(context);
			mContext = context.getApplicationContext();
		}

		@Override
		public Cursor loadInBackground() {
			return SearchListModel.getSearchList(mContext);
		}
		
		@Override
		protected void onStartLoading() {
			super.onStartLoading();
			if(isStarted()) {
				forceLoad();
			}
		}
		
		@Override
		protected void onStopLoading() {
			super.onStopLoading();
			cancelLoad();
		}
		
		@Override
		protected void onReset() {
			super.onReset();
			onStopLoading();
		}
	}
	
}
