package com.m6.gocook.base.view;

import com.m6.gocook.R;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class ActionBar {
	
	private View mRoot;
	private TextView mTitle;
	
	private OnActionBarClick mActionBarClick;
	
	public ActionBar(View view) {
		mRoot = view;
		setUp();
	}
	
	private void setUp() {
		mTitle = (TextView) mRoot.findViewById(R.id.actionbar_title);
		
	}

	public void setTitle(int resId) {
		mTitle.setText(resId);
	}
	
	public void setTitle(String text) {
		mTitle.setText(text);
	}
	
	public void setRightButton(String text, int backgroundRes) {
		Button button = (Button) mRoot.findViewById(R.id.actionbar_right_button);
		button.setText(text);
		button.setBackgroundResource(backgroundRes);
		button.setVisibility(View.VISIBLE);
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mActionBarClick != null) {
					mActionBarClick.onActionBarRightButtonClick(v);
				}
			}
		});
	}
	
	public void setRightButton(int textRes, int backgroundRes) {
		Button button = (Button) mRoot.findViewById(R.id.actionbar_right_button);
		int paddingTop = button.getContext().getResources().getDimensionPixelSize(R.dimen.actionbar_button_padding_top);
		int paddingLeft = button.getContext().getResources().getDimensionPixelSize(R.dimen.actionbar_button_padding_left);
		button.setText(textRes);
		button.setBackgroundResource(backgroundRes);
		button.setPadding(paddingLeft, paddingTop, paddingLeft, paddingTop);
		button.setVisibility(View.VISIBLE);

		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(mActionBarClick != null) {
					mActionBarClick.onActionBarRightButtonClick(v);
				}
			}
		});
	}
	
	public void setActionBarClickListener(OnActionBarClick onActionBarClick) {
		mActionBarClick = onActionBarClick;
	}
	
	public interface OnActionBarClick {
		public void onActionBarRightButtonClick(View v);
		public void onActionBarLeftButtonClick(View v);
	}
}
