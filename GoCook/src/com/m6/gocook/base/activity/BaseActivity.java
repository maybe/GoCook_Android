package com.m6.gocook.base.activity;

import com.m6.gocook.R;
import com.m6.gocook.base.fragment.FragmentHelper;
import com.m6.gocook.base.fragment.OnKeyDown;
import com.m6.gocook.biz.main.MainActivityHelper;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

public class BaseActivity extends FragmentActivity {
	
	private OnKeyDown mOnkeyDownListener;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.base_activity);
		
		FragmentHelper fragmentHelper = new FragmentHelper(this);
		fragmentHelper.initFragment(getIntent(), R.id.container);
	}
	
	@Override
	protected void onDestroy() {
		mOnkeyDownListener = null;
		super.onDestroy();
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(R.anim.base_stay_orig, R.anim.base_slide_right_out);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		MainActivityHelper.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mOnkeyDownListener != null && mOnkeyDownListener.onKeyDown(keyCode, event)) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void registerOnkeyDownListener(OnKeyDown onKeyDownListener) {
		if (mOnkeyDownListener == onKeyDownListener) {
			return;
		}
		mOnkeyDownListener = onKeyDownListener;
	}
	
	public void unRegisterOnkeyDownListener(OnKeyDown onKeyDownListener) {
		if (mOnkeyDownListener == onKeyDownListener) {
			mOnkeyDownListener = null;
		}
	}
}
