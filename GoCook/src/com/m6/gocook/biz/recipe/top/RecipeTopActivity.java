package com.m6.gocook.biz.recipe.top;

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
		
		Fragment f = new RecipeTopFragment();
		Intent it = getIntent();
		if(it != null) {
			Bundle args = new Bundle();
			args.putString(PARAM_FROM, it.getStringExtra(PARAM_FROM));
			f.setArguments(args);
		}
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container, f, RecipeTopFragment.class.getName());
		ft.commit();
	}
}
