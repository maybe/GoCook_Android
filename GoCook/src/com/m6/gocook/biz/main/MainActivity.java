package com.m6.gocook.biz.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.biz.account.AccountFragment;
import com.m6.gocook.biz.account.LoginFragment;
import com.m6.gocook.biz.main.TabHelper.Tab;
import com.m6.gocook.biz.search.SearchFragment;

public class MainActivity extends FragmentActivity implements TabHost.OnTabChangeListener {
	
	private static final String TAG = "MainActivity";

	private TextView mTitle;
	
	FragmentTabHost mTabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LayoutInflater inflater = LayoutInflater.from(this);
		
		mTitle = (TextView) findViewById(R.id.actionBar_title);
		mTabHost = (FragmentTabHost) findViewById(R.id.main_tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.tabContent);
		
		addTab(mTabHost, inflater, Tab.SEARCH.tag, R.string.biz_main_tab_search, R.drawable.tab_pop_alpha, SearchFragment.class, null);
//		addTab(tabHost, inflater, Tab.HOT.tag, R.string.biz_main_tab_hot, R.drawable.tab_pop_alpha, Fragment.class, null);
		addTab(mTabHost, inflater, Tab.SHOPPING.tag, R.string.biz_main_tab_shopping, R.drawable.tab_buy_alpha, Fragment.class, null);
		addTab(mTabHost, inflater, Tab.ACCOUNT.tag, R.string.biz_main_tab_account, R.drawable.tab_me_alpha, AccountFragment.class, null);
		
		mTabHost.setOnTabChangedListener(this);
		mTitle.setText(TabHelper.getActionBarTitle(this, Tab.SEARCH.tag));
	}

	private void addTab(FragmentTabHost tabHost, LayoutInflater inflater, String tag, int titleId, int iconId, Class<?> clss, Bundle args) {
		View indicator = inflater.inflate(R.layout.fragment_account_tabhost_tabitem, null);
		((TextView) indicator.findViewById(R.id.title)).setText(titleId);
		((ImageView) indicator.findViewById(R.id.icon)).setImageResource(iconId);
		indicator.setTag(tag);
		tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(indicator), clss, args);
	}
	
	@Override
	public void onTabChanged(String tabId) {
		if(TextUtils.isEmpty(tabId)) {
			return;
		}
		
		mTitle.setText(TabHelper.getActionBarTitle(this, tabId));
		TabWidget tabWidget = mTabHost.getTabWidget();
		int count = tabWidget.getChildCount();
		ImageView icon;
		TextView title;
		String tag;
		for(int i = 0; i < count; i++) {
			View indicator = tabWidget.getChildTabViewAt(i);
			icon = (ImageView) indicator.findViewById(R.id.icon);
			title = (TextView) indicator.findViewById(R.id.title);
			tag = (String)indicator.getTag();
			
			if(TextUtils.isEmpty(tag)) {
				continue;
			}
			
			if(tabId.equals(tag)) { // 当前选中的tab
				if(tag.equals(Tab.SEARCH.tag)) {
					icon.setImageResource(R.drawable.tab_pop);
				} else if(tag.equals(Tab.SHOPPING.tag)) {
					icon.setImageResource(R.drawable.tab_buy);
				} else if(tag.equals(Tab.ACCOUNT.tag)) {
					icon.setImageResource(R.drawable.tab_me);
				}
			} else { // 非选中的tab
				if(tag.equals(Tab.SEARCH.tag)) {
					icon.setImageResource(R.drawable.tab_pop_alpha);
				} else if(tag.equals(Tab.SHOPPING.tag)) {
					icon.setImageResource(R.drawable.tab_buy_alpha);
				} else if(tag.equals(Tab.ACCOUNT.tag)) {
					icon.setImageResource(R.drawable.tab_me_alpha);
				}
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mTabHost = null;
	}

}
