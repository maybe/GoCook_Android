package com.m6.gocook.biz.purchase;

import com.m6.gocook.R;
import com.m6.gocook.biz.recipe.RecipeActivity;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

public class PurchaseListFragment extends Fragment {

	private Context mContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mContext = this.getActivity();

		View view = inflater.inflate(R.layout.fragment_purchase_list, container, false);
		
		Cursor cursor = PurchaseListModel.getRecipePurchaseListCursor(getActivity());
		
		ListView purchaseListView = (ListView) view.findViewById(R.id.purchase_listview);
		purchaseListView.setAdapter(new PurchaseListAdapter(getActivity(), cursor));
		
		return view;

	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
}
