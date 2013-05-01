package com.m6.gocook.biz.recipe.search;

import android.os.Bundle;

import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.recipe.list.RecipeListFragment;

public class SearchFragment extends RecipeListFragment {
	
	public static final String	PARAM_KEYWORDS = "param_keywords";

	private String mKeyWords;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args != null) {
			mKeyWords = args.getString(PARAM_KEYWORDS);
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ActionBar action = getAction();
		action.setTitle(mKeyWords);
	}
	
	@Override
	protected String getURL() {
		return String.format(Protocol.URL_RECIPE_SEARCH, mKeyWords, mPage);
	}

	
}
