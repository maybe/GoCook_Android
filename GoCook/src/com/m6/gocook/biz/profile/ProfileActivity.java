package com.m6.gocook.biz.profile;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.m6.gocook.R;

public class ProfileActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_activity);
		
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.add(R.id.container, new ProfileFragment(), ProfileFragment.class.getName());
		ft.commit();
	}
	
}
