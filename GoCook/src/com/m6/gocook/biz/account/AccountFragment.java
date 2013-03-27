package com.m6.gocook.biz.account;

import com.m6.gocook.R;

import android.R.interpolator;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AccountFragment extends Fragment implements OnAccountChangedListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_account_tabcontent, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		if(AccountModel.isLogon(getActivity())) {
			ft.add(R.id.account_tabcontent, new MyAccountFragment(), MyAccountFragment.class.getName());
		} else {
			ft.add(R.id.account_tabcontent, new LoginOrRegisterFragment(), LoginOrRegisterFragment.class.getName());
		}
		ft.commit();
		
		AccountModel.registerOnAccountChangedListener(this);
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		AccountModel.unRegisterOnAccountChangedListener(this);
	}

	@Override
	public void onLogin(String email) {
		FragmentTransaction ft = getChildFragmentManager().beginTransaction();
		ft.replace(R.id.account_tabcontent, new MyAccountFragment());
		ft.commit();
	}

	@Override
	public void onLogout() {
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		Fragment f = fm.findFragmentByTag(LoginOrRegisterFragment.class.getName());
		if(f == null) {
			f = new LoginOrRegisterFragment();
			ft.add(R.id.account_tabcontent, f, LoginOrRegisterFragment.class.getName());
			Fragment mf = fm.findFragmentByTag(MyAccountFragment.class.getName());
			ft.hide(mf);
			ft.commit();
			fm.executePendingTransactions();
		} else {
			ft.attach(f);
			Fragment mf = fm.findFragmentByTag(MyAccountFragment.class.getName());
			ft.hide(mf);
			ft.commit();
			fm.executePendingTransactions();
		}
	}
	
}
