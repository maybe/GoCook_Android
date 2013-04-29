package com.m6.gocook.biz.purchase;

import com.m6.gocook.R;
import com.m6.gocook.biz.recipe.recipe.RecipeActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class PurchaseListFragment extends Fragment {

	private Context mContext;
	private Cursor mCursor;
	
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
		
		if(mCursor != null) {
			mCursor.close();
		}
	}
	
}
