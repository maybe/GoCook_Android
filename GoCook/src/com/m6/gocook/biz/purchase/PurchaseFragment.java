package com.m6.gocook.biz.purchase;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.OnActionBarEventListener;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.account.LoginOrRegisterFragment;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.profile.MyAccountFragment;

public class PurchaseFragment extends Fragment implements
		OnActionBarEventListener {
	
	private Fragment mCurrentFragment = null;
	
	@Override
	public void onResume() {
		super.onResume();
		MainActivityHelper.registerOnActionBarEventListener(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		MainActivityHelper.unRegisterOnActionBarEventListener(this);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_purchase_tabcontent, null);
	}


	@Override
	public void OnFragmentSwitch(Class<? extends Fragment> fragment) {
		launchFragment(fragment);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		launchFragment(PurchaseListFragment.class);
	}
	
	private void launchFragment(Class<? extends Fragment> fragment) {
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		if(mCurrentFragment != null) {
			ft.detach(mCurrentFragment);
		}
		
		Fragment f = fm.findFragmentByTag(fragment.getName());
		if(f == null) {
			f = Fragment.instantiate(getActivity(), fragment.getName());
			ft.add(R.id.purchase_tabcontent, f, fragment.getName());
		} else {
			if(f == mCurrentFragment) {
				return;
			}
			ft.attach(f);
		}
		mCurrentFragment = f;
		
		ft.commit();

	}
	

}
