package com.m6.gocook.biz.recipe.hot;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.m6.gocook.R;

public class RecipeTopActivity extends FragmentActivity {
	
	public static final String PARAM_FROM = "param_from";
	public static final String PARAM_FROM_HOT = "param_from_hot";
	public static final String PARAM_FROM_NEW = "param_from_new";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_activity);
		
		Intent it = getIntent();
		Bundle args = new Bundle();
		if(it != null) {
			args.putString(PARAM_FROM, it.getStringExtra(PARAM_FROM));
		} else {
			// 默认显示最新菜谱
			args.putString(PARAM_FROM, PARAM_FROM_NEW);
		}
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment f = new RecipeTopFragment();
		f.setArguments(args);
		ft.add(R.id.container, f, RecipeTopFragment.class.getName());
		ft.commit();
	}
}
