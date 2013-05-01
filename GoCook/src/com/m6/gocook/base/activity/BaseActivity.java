package com.m6.gocook.base.activity;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.FragmentHelper;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_activity);
		
		FragmentHelper fragmentHelper = new FragmentHelper(this);
		fragmentHelper.initFragment(getIntent(), R.id.container);
	}
	
	
}
