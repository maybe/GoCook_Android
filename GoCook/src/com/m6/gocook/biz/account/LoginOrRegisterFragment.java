package com.m6.gocook.biz.account;

import com.m6.gocook.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;

public class LoginOrRegisterFragment extends Fragment implements TabHost.OnTabChangeListener {
	
	public static final String TAB_TAG_LOGIN = "login";
	public static final String TAB_TAG_REGISTER = "register";
	
	private FragmentTabHost mTabHost;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mTabHost = (FragmentTabHost) inflater.inflate(R.layout.fragment_account_tabhost, container, false);
		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.account_tabcontent);
		
		mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_LOGIN).setIndicator(getString(R.string.biz_account_tab_login)), 
				LoginFragment.class, null);
		mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_REGISTER).setIndicator(getString(R.string.biz_account_tab_register)), 
				RegisterFragment.class, null);
		
		mTabHost.setOnTabChangedListener(this);
		
		return mTabHost;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		mTabHost = null;
	}

	@Override
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		
	}
}
