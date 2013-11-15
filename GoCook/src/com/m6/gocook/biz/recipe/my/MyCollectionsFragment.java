package com.m6.gocook.biz.recipe.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.recipe.list.RecipeListFragment;

public class MyCollectionsFragment extends RecipeListFragment implements OnActivityAction {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivityHelper.registerOnActivityActionListener(this);
	}
	
	@Override
	public void onDestroy() {
		MainActivityHelper.unRegisterOnActivityActionListener(this);
		super.onDestroy();
	}

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
	
	@Override
	protected String getEmptyMessage() {
		return getString(R.string.biz_profile_mycollection_empty_message);
	}

	@Override
	public void onCustomActivityResult(int requestCode, int resultCode,
			Intent data) {

		if (requestCode == MainActivityHelper.REQUEST_CODE_RECIPE &&
					resultCode == MainActivityHelper.RESULT_CODE_RECIPE_COLLECT) {
			refresh();
		}
		
	}
}
