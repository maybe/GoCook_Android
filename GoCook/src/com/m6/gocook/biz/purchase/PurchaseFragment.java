package com.m6.gocook.biz.purchase;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.OnActionBarEventListener;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.account.LoginOrRegisterFragment;
import com.m6.gocook.biz.main.MainActivityHelper;
import com.m6.gocook.biz.profile.MyAccountFragment;
import com.m6.gocook.biz.recipe.recipe.EditDialogFragment;

public class PurchaseFragment extends Fragment implements
		OnActionBarEventListener {
	
	private Class<? extends Fragment> mCurrentFragmentClass = null;
	
	@Override
	public void onAttach(Activity activity) {
		Log.i("PurchaseFragment", "LRL onAttach");
		super.onAttach(activity);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		Log.i("PurchaseFragment", "LRL onCreate");
		super.onCreate(savedInstanceState);
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("PurchaseFragment", "LRL onCreateView");
		return inflater.inflate(R.layout.fragment_purchase_tabcontent, null);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.i("PurchaseFragment", "LRL onActivityCreated");
		if(mCurrentFragmentClass == null) {
			launchFragment(PurchaseListFragment.class);
		}
		

		EditDialogFragment editDialog = new EditDialogFragment();
		editDialog.show(getChildFragmentManager(), EditDialogFragment.class.getName());
		
	}
	
	
	@Override
	public void onStart() {
		Log.i("PurchaseFragment", "LRL onStart");
		super.onStart();
	}
	
	@Override
	public void onResume() {
		Log.i("PurchaseFragment", "LRL onResume");
		super.onResume();
		MainActivityHelper.registerOnActionBarEventListener(this);
	}
	
	
	//--------
	
	@Override
	public void onPause() {
		Log.i("PurchaseFragment", "LRL onPause");
		super.onPause();
		MainActivityHelper.unRegisterOnActionBarEventListener(this);
	}
	
	@Override
	public void onStop() {
		Log.i("PurchaseFragment", "LRL onStop");
		super.onStop();
	}

	@Override
	public void onDestroyView() {
		Log.i("PurchaseFragment", "LRL onDestroyView");
		super.onDestroyView();
	}
	
	@Override
	public void onDestroy() {
		Log.i("PurchaseFragment", "LRL onDestroy");
		super.onDestroy();
	}
	
	@Override
	public void onDetach() {
		Log.i("PurchaseFragment", "LRL onDetach");
		super.onDetach();
	}
	

	@Override
	public void OnFragmentSwitch(Class<? extends Fragment> fragment) {
		launchFragment(fragment);
	}
	
	private void launchFragment(Class<? extends Fragment> fragment) {
		
		if(fragment == null) {
			return;
		}
		
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		
		Fragment curF;
		if(mCurrentFragmentClass != null &&
			(curF = fm.findFragmentByTag(mCurrentFragmentClass.getName())) != null) {
			ft.hide(curF);
		}
		Fragment f = fm.findFragmentByTag(fragment.getName());
		if(f == null) {
			f = Fragment.instantiate(getActivity(), fragment.getName());
			ft.add(R.id.purchase_tabcontent, f, fragment.getName());
			Log.i("ss", "LRL add:" + fragment.getName());
		} else {
			Log.i("ss", "LRL show:" + fragment.getName());
			ft.show(f);
		}
		
		ft.commit();
		fm.executePendingTransactions();
		
		mCurrentFragmentClass = fragment;
	}
	

}
