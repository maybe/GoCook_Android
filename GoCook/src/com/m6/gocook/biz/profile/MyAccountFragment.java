package com.m6.gocook.biz.profile;

import com.m6.gocook.R;
import com.m6.gocook.biz.account.AccountModel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class MyAccountFragment extends Fragment {
	
	public static final String TAG = "MyAccountFragment";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_myaccount, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View view = getView();
		
		view.findViewById(R.id.logout).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				AccountModel.logout(getActivity());
			}
		});
		
		view.findViewById(R.id.profile).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getActivity(), ProfileActivity.class));
			}
		});
		
	}
	
}
