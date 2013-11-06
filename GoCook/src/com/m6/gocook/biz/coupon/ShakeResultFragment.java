package com.m6.gocook.biz.coupon;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.view.ActionBar;

public class ShakeResultFragment extends BaseFragment {

	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_coupon_shake_result, container, false);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_coupon_shake_title);
		
		view.findViewById(R.id.positive).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
			}
		});
		
		view.findViewById(R.id.cancel).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getActivity().finish();
			}
		});
	}
}
