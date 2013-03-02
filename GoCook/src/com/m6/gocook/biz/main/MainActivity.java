package com.m6.gocook.biz.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.Log;
import android.widget.TabHost;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.biz.main.TabHelper.Tab;
import com.m6.gocook.biz.search.SearchFragment;

public class MainActivity extends FragmentActivity implements TabHost.OnTabChangeListener {
	
	private static final String TAG = "MainActivity";

	private FragmentTabHost mTabHost;
	private TextView mTitle;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mTitle = (TextView) findViewById(R.id.actionBar_title);
		mTabHost = (FragmentTabHost) findViewById(R.id.main_tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.tabContent);
		mTabHost.addTab(mTabHost.newTabSpec(Tab.SEARCH.tag).setIndicator(getString(R.string.biz_main_tab_search)), SearchFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(Tab.HOT.tag).setIndicator(getString(R.string.biz_main_tab_hot)), Fragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(Tab.SHOPPING.tag).setIndicator(getString(R.string.biz_main_tab_shopping)), Fragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(Tab.ACCOUNT.tag).setIndicator(getString(R.string.biz_main_tab_account)), Fragment.class, null);
		
		mTabHost.setOnTabChangedListener(this);
		mTitle.setText(TabHelper.getActionBarTitle(null));
	}

	@Override
	public void onTabChanged(String tabId) {
//		Log.i(TAG, tabId);
		mTitle.setText(TabHelper.getActionBarTitle(tabId));
	}

}
