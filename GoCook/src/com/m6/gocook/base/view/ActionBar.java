package com.m6.gocook.base.view;

import com.m6.gocook.R;

import android.view.View;
import android.widget.TextView;

public class ActionBar {
	
	private View mRoot;
	private TextView mTitle;
	
	public ActionBar(View view) {
		mRoot = view;
		setUp();
	}
	
	public void setUp() {
		mTitle = (TextView) mRoot.findViewById(R.id.actionbar_title);
	}

	public void setTitle(int resId) {
		mTitle.setText(resId);
	}
	
	public void setTitle(String text) {
		mTitle.setText(text);
	}
}
