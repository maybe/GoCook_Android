package com.m6.gocook.biz.recipe.my;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.m6.gocook.R;
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.entity.RecipeList.RecipeItem;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.profile.ProfileModel;
import com.m6.gocook.biz.recipe.RecipeModel;
import com.m6.gocook.biz.recipe.list.RecipeListFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeEditFragment;
import com.m6.gocook.util.log.Logger;
import com.m6.gocook.util.model.ModelUtils;

public class MyRecipesFragment extends RecipeListFragment implements OnActivityAction {

	public static final String PARAM_FROM_PROFILE = "param_from_profile";
	
	public static final String PARAM_USERNAME = "param_username";

	private boolean mFromPersonnalProfile = false; // 从我的个人资料页面跳转而来就从本地取数据，否则取网络数据

	private String mUsername;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle args = getArguments();
		if(args != null) {
			mFromPersonnalProfile = args.getBoolean(PARAM_FROM_PROFILE, false);
			if (mFromPersonnalProfile) {
				mUsername = AccountModel.getUsername(getActivity());
			} else {
				mUsername = args.getString(PARAM_USERNAME);;
			}
		}
		
		MainActivityHelper.registerOnActivityActionListener(this);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		MainActivityHelper.unRegisterOnActivityActionListener(this);
	}
	
	
	@Override
	public View onCreateHeaderView(LayoutInflater inflater, ViewGroup container) {
		if (mFromPersonnalProfile) {
			View view = inflater.inflate(R.layout.fragment_myrecipes_header, container, false);
			view.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					RecipeEditFragment.startInActivityForResult(getActivity(),
							RecipeEditFragment.Mode.RECIPE_NEW, "");
				}
			});
			return view;
		} else {
			return null;
		}
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		ActionBar action = getActionBar();
		action.setTitle(getString(R.string.biz_profile_myrecipes_title, mUsername));
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
		if (mFromPersonnalProfile) {
			return RecipeModel.getMyRecipes(getActivity(), mFromPersonnalProfile);
		} else {
			List<Map<String, Object>> recipes = ModelUtils.getListMapValue(ProfileModel.getOtherInfo(getActivity(), "", true), ProfileModel.RECIPES);
			if (recipes != null) {
				int size = recipes.size();
				ArrayList<RecipeItem> recipeItems = new ArrayList<RecipeList.RecipeItem>(); 
				for (int i = 0; i < size; i++) {
					RecipeItem item = new RecipeItem();
					item.setId(ModelUtils.getStringValue(recipes.get(i), Protocol.KEY_RECIPE_ID));
					item.setName(ModelUtils.getStringValue(recipes.get(i), Protocol.KEY_RECIPE_LIST_NAME));
					item.setImage(ModelUtils.getStringValue(recipes.get(i), Protocol.KEY_RECIPE_LIST_IMAGE));
					item.setMaterial(ModelUtils.getStringValue(recipes.get(i), Protocol.KEY_RECIPE_MATERIALS));
					item.setCollectCount(ModelUtils.getIntValue(recipes.get(i), Protocol.KEY_RECIPE_DISH_COUNT, 0));
					recipeItems.add(item);
				}
				RecipeList recipeList = new RecipeList();
				recipeList.setRecipes(recipeItems);
				return recipeList;
			}
			return null;
		}
	}
	
	@Override
	protected String getEmptyMessage() {
		return null;
	}
	
	@Override
	public void onCustomActivityResult(int requestCode, int resultCode, Intent data) {
		Log.i("MyRecipesFragment", String.format("onActivityResult: requesCode:%d, resultCode:%d", requestCode, resultCode));
		
		if(requestCode == MainActivityHelper.REQUEST_CODE_RECIPE_EDIT) {
			if(resultCode == MainActivityHelper.RESULT_CODE_RECIPE_EDIT_CREATED) {
				refresh();
			}
		}
		
		if(requestCode == MainActivityHelper.REQUEST_CODE_RECIPE) {
			if(resultCode == MainActivityHelper.RESULT_CODE_RECIPE_DELETED ||
					resultCode == MainActivityHelper.RESULT_CODE_RECIPE_UPDATED) {
				refresh();
			}
		}
	}
	
}
