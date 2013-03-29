package com.m6.gocook.biz.account;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m6.gocook.R;

public class AccountFragment extends Fragment implements OnAccountChangedListener {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AccountModel.registerOnAccountChangedListener(this);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_account_tabcontent, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment f = null;
		
		if(AccountModel.isLogon(getActivity())) {
			f = fm.findFragmentByTag(MyAccountFragment.class.getName());
			if(f== null) {
				f = MyAccountFragment.instantiate(getActivity(), MyAccountFragment.class.getName());
				ft.add(R.id.account_tabcontent, f, MyAccountFragment.class.getName());
				ft.commit();
			}
		} else {
			f = fm.findFragmentByTag(LoginOrRegisterFragment.class.getName());
			if(f == null) {
				f = LoginOrRegisterFragment.instantiate(getActivity(), LoginOrRegisterFragment.class.getName());
				ft.add(R.id.account_tabcontent, f, LoginOrRegisterFragment.class.getName());
				ft.commit();
			}
		}
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AccountModel.unRegisterOnAccountChangedListener(this);
	}
	
	@Override
	public void onLogin(String email) {
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Fragment f = fm.findFragmentByTag(MyAccountFragment.class.getName());
		if(f == null) {
			f = MyAccountFragment.instantiate(getActivity(), MyAccountFragment.class.getName());
		}
		ft.replace(R.id.account_tabcontent, f, MyAccountFragment.class.getName());
		ft.commit();
		fm.executePendingTransactions();
	}

	@Override
	public void onLogout() {
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Fragment f = fm.findFragmentByTag(LoginOrRegisterFragment.class.getName());
		if(f == null) {
			f = LoginOrRegisterFragment.instantiate(getActivity(), LoginOrRegisterFragment.class.getName());
		}
		ft.replace(R.id.account_tabcontent, f, LoginOrRegisterFragment.class.getName());
		ft.commit();
		fm.executePendingTransactions();
	}
	
}
