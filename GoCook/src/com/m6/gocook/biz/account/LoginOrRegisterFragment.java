package com.m6.gocook.biz.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m6.gocook.R;

public class LoginOrRegisterFragment extends Fragment implements View.OnClickListener {
	
	public static final String TAB_TAG_LOGIN = "login";
	public static final String TAB_TAG_REGISTER = "register";
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_loginorregister_tabcontent, container, false);
	}
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		view.findViewById(R.id.login).setOnClickListener(this);
		view.findViewById(R.id.register).setOnClickListener(this);
		
		FragmentManager fm = getChildFragmentManager();
		
		Fragment f, ff = null;
		
		f = fm.findFragmentByTag(LoginFragment.class.getName());
		if(f == null) {
			FragmentTransaction ft = fm.beginTransaction();
			f = LoginFragment.instantiate(getActivity(), LoginFragment.class.getName());
			ft.add(R.id.loginorregister_tabcontent, f, LoginFragment.class.getName());
			ft.show(f);
			ft.commit();
		}
		
		ff = fm.findFragmentByTag(RegisterFragment.class.getName());
		if(ff == null) {
			FragmentTransaction ft = fm.beginTransaction();
			ff = RegisterFragment.instantiate(getActivity(), RegisterFragment.class.getName());
			ft.add(R.id.loginorregister_tabcontent, ff, RegisterFragment.class.getName());
			ft.hide(ff);
			ft.commit();
		}
	}


	@Override
	public void onClick(View v) {
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment f, ff = null;
		
		switch (v.getId()) {
		case R.id.login:
			f = fm.findFragmentByTag(LoginFragment.class.getName());
			if(f == null) {
				f = LoginFragment.instantiate(getActivity(), LoginFragment.class.getName());
				ft.add(R.id.loginorregister_tabcontent, f, LoginFragment.class.getName());
			}
			ft.commit();
			
			ft = fm.beginTransaction();
			ff = fm.findFragmentByTag(RegisterFragment.class.getName());
			ft.show(f);
			ft.hide(ff);
			ft.commit();
			fm.executePendingTransactions();
			break;
		case R.id.register:
			f = fm.findFragmentByTag(RegisterFragment.class.getName());
			if(f == null) {
				f = RegisterFragment.instantiate(getActivity(), RegisterFragment.class.getName());
				ft.add(R.id.loginorregister_tabcontent, f, RegisterFragment.class.getName());
			}
			ft.commit();
			
			ft = fm.beginTransaction();
			ff = fm.findFragmentByTag(LoginFragment.class.getName());
			ft.show(f);
			ft.hide(ff);
			ft.commit();
			fm.executePendingTransactions();
			break;
		default:
			break;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
//	private FragmentTabHost mTabHost;
	
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		mTabHost = (FragmentTabHost) inflater.inflate(R.layout.fragment_account_tabhost, container, false);
//		mTabHost.setup(getActivity(), getChildFragmentManager(), R.id.loginorregister_tabcontent);
//		
//		mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_LOGIN).setIndicator(getString(R.string.biz_account_tab_login)), 
//				LoginFragment.class, null);
//		mTabHost.addTab(mTabHost.newTabSpec(TAB_TAG_REGISTER).setIndicator(getString(R.string.biz_account_tab_register)), 
//				RegisterFragment.class, null);
//		
//		mTabHost.setOnTabChangedListener(this);
//		
//		return mTabHost;
//	}
//	
//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//		mTabHost = null;
//	}
//
//	@Override
//	public void onTabChanged(String tabId) {
//		// TODO Auto-generated method stub
//		
//	}
}
