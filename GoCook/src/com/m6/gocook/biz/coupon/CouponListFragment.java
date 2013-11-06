package com.m6.gocook.biz.coupon;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.m6.gocook.R;
import com.m6.gocook.base.activity.BaseActivity;
import com.m6.gocook.base.fragment.BaseFragment;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.view.ActionBar;
import com.m6.gocook.biz.profile.PeopleFragment;

public class CouponListFragment extends BaseFragment {

	@Override
	public View onCreateFragmentView(LayoutInflater inflater,
			ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_coupon_list, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		
		ActionBar actionBar = getActionBar();
		actionBar.setTitle(R.string.biz_coupon_list_title);
		actionBar.setRightButton(null, R.drawable.actionbar_refresh_selector);
		
		view.findViewById(R.id.shake).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = FragmentHelper.getIntent(getActivity(), BaseActivity.class, 
						ShakeFragment.class.getName(), ShakeFragment.class.getName(), null);
				startActivity(intent);
			}
		});
	}
}
