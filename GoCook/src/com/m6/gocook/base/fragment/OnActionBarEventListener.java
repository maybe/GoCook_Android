package com.m6.gocook.base.fragment;

import android.support.v4.app.Fragment;
import android.view.View;

public interface OnActionBarEventListener {

	public void OnFragmentSwitch(Class<? extends Fragment> fragment);
	
	public void OnActionBarClick(View view, int id);
	
}
