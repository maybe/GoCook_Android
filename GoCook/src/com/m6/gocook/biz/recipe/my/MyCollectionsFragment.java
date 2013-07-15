package com.m6.gocook.biz.recipe.my;

import android.os.Bundle;
import android.view.View;

import com.m6.gocook.R;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.recipe.list.RecipeListFragment;

public class MyCollectionsFragment extends RecipeListFragment {

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		ActionBar action = getActionBar();
		action.setTitle(getString(R.string.biz_profile_mycollection_title, AccountModel.getUsername(getActivity())));
	}
	
	@Override
	protected String getURL() {
		return Protocol.URL_PROFILE_MY_COLLECTION;
	}

	@Override
	protected boolean needCookie() {
		return true;
	}
}