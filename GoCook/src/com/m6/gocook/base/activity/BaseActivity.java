package com.m6.gocook.base.activity;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.biz.main.MainActivityHelper;

import android.content.Intent;
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
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		MainActivityHelper.onActivityResult(requestCode, resultCode, data);
	}
}
