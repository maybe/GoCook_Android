package com.m6.gocook.base.fragment;

import android.content.Intent;

public interface OnActivityAction {

	public void onCustomActivityResult(int requestCode, int resultCode, Intent data);
	
}
