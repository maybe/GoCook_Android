package com.m6.gocook.biz.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.m6.gocook.R;
import com.m6.gocook.biz.account.AccountFragment;
import com.m6.gocook.biz.main.TabHelper.Tab;
import com.m6.gocook.biz.popular.PopularFragment;
import com.m6.gocook.biz.purchase.PurchaseFragment;
import com.m6.gocook.biz.purchase.PurchaseListByTypeFragment;
import com.m6.gocook.biz.purchase.PurchaseListFragment;
import com.m6.gocook.biz.purchase.PurchaseListModel;

public class MainActivity extends FragmentActivity implements TabHost.OnTabChangeListener {
	
	private static final String TAG = "MainActivity";

	private TextView mTitle;
	
	private FragmentTabHost mTabHost;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		LayoutInflater inflater = LayoutInflater.from(this);
		
		mTitle = (TextView) findViewById(R.id.actionbar_title);
		mTabHost = (FragmentTabHost) findViewById(R.id.main_tabhost);
		mTabHost.setup(this, getSupportFragmentManager(), R.id.tabContent);
		
		addTab(mTabHost, inflater, Tab.SEARCH.tag, R.string.biz_main_tab_search, R.drawable.tab_pop_alpha, PopularFragment.class, null);
//		addTab(tabHost, inflater, Tab.HOT.tag, R.string.biz_main_tab_hot, R.drawable.tab_pop_alpha, Fragment.class, null);
		addTab(mTabHost, inflater, Tab.SHOPPING.tag, R.string.biz_main_tab_shopping, R.drawable.tab_buy_alpha, PurchaseFragment.class, null);
		addTab(mTabHost, inflater, Tab.ACCOUNT.tag, R.string.biz_main_tab_account, R.drawable.tab_me_alpha, AccountFragment.class, null);
		
		mTabHost.setOnTabChangedListener(this);
		mTitle.setText(TabHelper.getActionBarTitle(this, Tab.SEARCH.tag));
		
		((RadioGroup) findViewById(R.id.purchaselist_switch_radiogroup)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(PurchaseListModel.getRecipePurchaseCount(getApplicationContext()) > 0) {
					RadioGroup group = (RadioGroup) v;
					if (group.getCheckedRadioButtonId() == R.id.recipe_view_radiobutton) {
						group.check(R.id.total_view_radiobutton);
						MainActivityHelper.OnFragmentSwitch(PurchaseListByTypeFragment.class);
					} else {
						group.check(R.id.recipe_view_radiobutton);
						MainActivityHelper.OnFragmentSwitch(PurchaseListFragment.class);
					}
				}
			}
		});
		
		((Button) findViewById(R.id.actionbar_left_button)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MainActivityHelper.OnActionBarClick(v, v.getId());
			}
		});
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
		
		if(tabId.equals(Tab.SHOPPING.tag)) {
			findViewById(R.id.purchaselist_switch_radiogroup).setVisibility(View.VISIBLE);
			findViewById(R.id.actionbar_left_button).setVisibility(View.VISIBLE);
		} else {
			findViewById(R.id.purchaselist_switch_radiogroup).setVisibility(View.GONE);
			findViewById(R.id.actionbar_left_button).setVisibility(View.GONE);
		}
		
		mTitle.setText(TabHelper.getActionBarTitle(this, tabId));
		onIndicatorChanged(tabId);
	}
	
	private void onIndicatorChanged(String tabId) {
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
		MainActivityHelper.clearOnActivityActionListeners();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		MainActivityHelper.onActivityResult(requestCode, resultCode, data);
	}

}
