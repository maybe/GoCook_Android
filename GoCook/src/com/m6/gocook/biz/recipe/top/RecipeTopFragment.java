package com.m6.gocook.biz.recipe.top;

import android.os.Bundle;

import com.m6.gocook.R;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.recipe.list.RecipeListFragment;

public class RecipeTopFragment extends RecipeListFragment {

	public static final String PARAM_TYPE = "param_type";
	public static final String PARAM_TYPE_HOT = "param_type_hot";
	public static final String PARAM_TYPE_NEW = "param_type_new";
	
	private String mTopType;
	private String mUrl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		if(args != null) {
			mTopType = args.getString(RecipeTopFragment.PARAM_TYPE);
			if(RecipeTopFragment.PARAM_TYPE_HOT.equalsIgnoreCase(mTopType)) {
				mUrl = Protocol.URL_RECIPE_HOT;
			} else {
				mUrl = Protocol.URL_RECIPE_NEW;
			}
		} else {
			mUrl = Protocol.URL_RECIPE_HOT;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ActionBar actionBar = getActionBar();
		Bundle args = getArguments();
		if(args != null) {
			if(RecipeTopFragment.PARAM_TYPE_HOT.equalsIgnoreCase(mTopType)) {
				actionBar.setTitle(R.string.biz_popular_tophot);
			} else {
				actionBar.setTitle(R.string.biz_popular_topnew);
			}
		} else {
			actionBar.setTitle(R.string.biz_popular_tophot);
		}
	}

	@Override
	protected String getURL() {
		return mUrl;
	}
	
	@Override
	protected int getAdapterLayout() {
		return R.layout.adapter_recipe_top_item;
	}
	
}
