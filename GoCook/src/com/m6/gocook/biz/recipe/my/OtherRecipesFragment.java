package com.m6.gocook.biz.recipe.my;

import android.os.Bundle;
import android.text.TextUtils;

import com.m6.gocook.R;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.recipe.list.RecipeListFragment;

public class OtherRecipesFragment extends RecipeListFragment {

	private static String PARAM_USERNAME = "param_username";
	private static String PARAM_USERID = "param_userid";
	
	private String mUserId;
	
	public static OtherRecipesFragment newInstance(String userid, String username) {
		OtherRecipesFragment fragment = new OtherRecipesFragment();
		Bundle bundle = new Bundle();
		bundle.putString(PARAM_USERID, userid);
		bundle.putString(PARAM_USERNAME, username);
		fragment.setArguments(bundle);
		return fragment;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args != null) {
			mUserId = args.getString(PARAM_USERID);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		Bundle args = getArguments();
		if(args != null) {
			ActionBar action = getActionBar();
			action.setTitle(getString(R.string.biz_profile_myrecipe_title, args.getString(PARAM_USERNAME)));
		}
	}
	
	@Override
	protected String getURL() {
		if (!TextUtils.isEmpty(mUserId)) {
			return String.format(Protocol.URL_PROFILE_OTHER_RECIPES, mUserId);
		}
		return super.getURL();
	}
}
