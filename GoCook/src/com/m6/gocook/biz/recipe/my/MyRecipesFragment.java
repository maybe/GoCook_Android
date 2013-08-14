package com.m6.gocook.biz.recipe.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.list.RecipeListFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeEditFragment;
import com.m6.gocook.util.log.Logger;

public class MyRecipesFragment extends RecipeListFragment implements OnActivityAction {

	public static final String PARAM_FROM_PROFILE = "param_from_profile";
	
	private static final int REQUEST_CODE = 11;
	
	private boolean mFromPersonnalProfile = false; // 从我的个人资料页面跳转而来就从本地取数据，否则取网络数据
	
	private boolean mIsFreshData = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args != null) {
			mFromPersonnalProfile = args.getBoolean(PARAM_FROM_PROFILE, false);
		}
	}
	
	@Override
	public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container) {
		if (mFromPersonnalProfile) {
			View view = inflater.inflate(R.layout.fragment_myrecipes_header, container, false);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
//					RecipeEditFragment.startInActivityForResult(getActivity(),
//							RecipeEditFragment.Mode.RECIPE_NEW, "", REQUEST_CODE);
					RecipeEditFragment.startInActivity(getActivity(),
							RecipeEditFragment.Mode.RECIPE_NEW, "");
					mIsFreshData = true;
				}
			});
			return view;
		} else {
			return null;
		}
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		ActionBar action = getActionBar();
		String username = AccountModel.getUsername(getActivity());
		action.setTitle(getString(R.string.biz_profile_myrecipes_title, username));
	}

	@Override
	protected String getURL() {
		return null; // 数据从getListData中得到，不需要url
	}
	
	@Override
	protected boolean doPaginate() {
		return false;
	}
	
	@Override
	protected RecipeList getListData(String url) {
		return RecipeModel.getMyRecipes(getActivity(), mFromPersonnalProfile);
	}
	
	@Override
	protected String getEmptyMessage() {
		return null;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(mIsFreshData) {
			executeTask();
			mIsFreshData = false;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(requestCode == REQUEST_CODE
				&& resultCode == MainActivityHelper.RESULT_OK) {
			executeTask();
			Logger.i("achieve my recipe list again");
		}
	}
	
}
