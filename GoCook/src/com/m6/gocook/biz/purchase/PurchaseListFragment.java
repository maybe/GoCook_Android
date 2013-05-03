package com.m6.gocook.biz.purchase;

import com.m6.gocook.R;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
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
	public void onAttach(Activity activity) {
		Log.i("PurchaseListFragment", "LRL onAttach");
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("PurchaseListFragment", "LRL onCreate");
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("PurchaseListFragment", "LRL onCreateView");
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
		Log.i("PurchaseListFragment", "LRL onActivityCreated");

	}
	
	
	@Override
	public void onStart() {
		Log.i("PurchaseListFragment", "LRL onStart");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		Log.i("PurchaseListFragment", "LRL onResume");
		super.onResume();
		
	}
	
	
	//--------
	
	@Override
	public void onPause() {
		Log.i("PurchaseListFragment", "LRL onPause");
		super.onPause();
		
	}
	
	@Override
	public void onStop() {
		Log.i("PurchaseListFragment", "LRL onStop");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.i("PurchaseListFragment", "LRL onDestroyView");
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		Log.i("PurchaseListFragment", "LRL onDestroy");
		super.onDestroy();

		if (mCursor != null) {
			mCursor.close();
		}
	}
	
	@Override
	public void onDetach() {
		Log.i("PurchaseListFragment", "LRL onDetach");
		super.onDetach();
	}
	
	
	
}
