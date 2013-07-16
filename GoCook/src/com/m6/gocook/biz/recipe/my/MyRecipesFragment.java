package com.m6.gocook.biz.recipe.my;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.list.RecipeListFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeEditFragment;

public class MyRecipesFragment extends RecipeListFragment {

	public static final String PARAM_FROM_PROFILE = "param_from_profile";
	
	private boolean mFromProfile = false; // 从我的个人资料页面跳转而来就从本地取数据，否则取网络数据
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args != null) {
			mFromProfile = args.getBoolean(PARAM_FROM_PROFILE);
		}
	}
	
	@Override
	public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.fragment_myrecipes_header, container, false);
		view.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				RecipeEditFragment.startInActivity(getActivity(),
						RecipeEditFragment.Mode.RECIPE_NEW, "");
			}
		});
		return view;
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
		return RecipeModel.getMyRecipes(getActivity(), mFromProfile);
	}
	
}
