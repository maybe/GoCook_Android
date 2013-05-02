package com.m6.gocook.biz.recipe.top;

import android.R.raw;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeListItem;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.list.RecipeListFragment;
import com.m6.gocook.util.cache.util.ImageFetcher;

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

		ActionBar actionBar = getAction();
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
		return String.format(mUrl, mPage);
	}
	
	@Override
	protected int getAdapterLayout() {
		return R.layout.adapter_recipe_top_item;
	}
	
}
