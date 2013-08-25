package com.m6.gocook.biz.buy;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.view.ActionBar;

public class BuyListFragment extends BaseFragment {

	public static final String PARAM_RECIPE_ID = "param_recipe_id";
	
	private BuyListAdapter mAdapter;
	private String mRecipeId;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle args = getArguments();
		if (args != null) {
			mRecipeId = args.getString(PARAM_RECIPE_ID);
		}
		
		if (!TextUtils.isEmpty(mRecipeId)) {
			
		}
	}
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_buy_title);
	}
	
	
}
