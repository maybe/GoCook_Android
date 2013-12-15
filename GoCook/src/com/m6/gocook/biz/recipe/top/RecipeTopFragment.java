package com.m6.gocook.biz.recipe.top;

import android.content.Intent;
import android.os.Bundle;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.entity.RecipeList.RecipeItem;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.recipe.list.RecipeListFragment;
import com.m6.gocook.biz.recipe.recipe.RecipeFragment;

public class RecipeTopFragment extends RecipeListFragment implements OnActivityAction {

	public static final String PARAM_TYPE = "param_type";
	public static final String PARAM_TYPE_HOT = "param_type_hot";
	public static final String PARAM_TYPE_NEW = "param_type_new";
	
	private String mTopType;
	private String mUrl;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		MainActivityHelper.registerOnActivityActionListener(this);
		
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
		mImageFetcher.setLoadingImage(R.drawable.image_recipe_cover_default);
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		MainActivityHelper.unRegisterOnActivityActionListener(this);
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

	@Override
	public void onCustomActivityResult(int requestCode, int resultCode,
			Intent data) {
		if (requestCode == MainActivityHelper.REQUEST_CODE_RECIPE
				&& resultCode == MainActivityHelper.RESULT_CODE_RECIPE_COLLECT) {
			if (data != null) {
				int position = data.getIntExtra(RecipeFragment.ARGUMENT_KEY_RECIPE_POSITION, -1);
				int collectCount = data.getIntExtra(RecipeFragment.ARGUMENT_KEY_RECIPE_COLLECT_COUNT, -1);
				if (position != -1 && collectCount != -1) {
					RecipeList recipes = getRecipes();
					if (recipes != null) {
						RecipeItem item = recipes.getRecipes().get(position);
						if (item != null) {
							item.setCollectCount(collectCount);
							if (getAdapter() != null) {
								getAdapter().notifyDataSetChanged();
							}
						}
					}
				}
			}
		}
	}
	
}
