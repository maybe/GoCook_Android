package com.m6.gocook.biz.purchase;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.m6.gocook.R;

public class PurchaseListFragment extends Fragment {

	private Context mContext;
	private Cursor mCursor;
	

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mContext = this.getActivity();

		View view = inflater.inflate(R.layout.fragment_purchase_list, container, false);
		
		mCursor = PurchaseListModel.getRecipePurchaseListCursor(mContext);
		BaseAdapter mAdapter = new PurchaseListAdapter(mContext, mCursor, CursorAdapter.FLAG_AUTO_REQUERY);
		
		ListView purchaseListView = (ListView) view.findViewById(R.id.purchase_listview);
		
		View footerView = inflater.inflate(R.layout.adapter_purchase_recipe_list_footer, null);
		purchaseListView.addFooterView(footerView, null, false);
		
		purchaseListView.setAdapter(mAdapter);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();

		if (mCursor != null) {
			mCursor.close();
		}
	}
	
}
