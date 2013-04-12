package com.m6.gocook.biz.purchase;

import com.m6.gocook.R;
import com.m6.gocook.biz.recipe.RecipeActivity;

import android.content.Context;
import android.content.Intent;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;

public class PurchaseListFragment extends Fragment {

	private Context mContext;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		
		mContext = this.getActivity();

		View view = inflater.inflate(R.layout.adapter_purchase_recipe_list_item, container, false);
		
		View bgView = view.findViewById(R.id.recipe_title_bg);
		final View deleteButtonView = view.findViewById(R.id.delete_item_image);
		bgView.setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				deleteButtonView.setVisibility(View.VISIBLE);
				return true;
			}
		});
		
		bgView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, RecipeActivity.class);
				mContext.startActivity(intent);
			}
		});
		
		View materialItemView = view.findViewById(R.id.material_item);
		final View lineSwipeImage = view.findViewById(R.id.line_swipe);
		
		materialItemView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				lineSwipeImage.setVisibility((lineSwipeImage.getVisibility() == View.VISIBLE) ?
						View.INVISIBLE : View.VISIBLE);
			}
		});
		
		return view;

	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
	}
	
}
