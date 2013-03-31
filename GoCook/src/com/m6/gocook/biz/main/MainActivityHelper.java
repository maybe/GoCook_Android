package com.m6.gocook.biz.main;

import java.util.ArrayList;

import android.content.Intent;

import com.m6.gocook.base.fragment.OnActivityAction;

public class MainActivityHelper {

	public static ArrayList<OnActivityAction> mOnActivityActions = new ArrayList<OnActivityAction>();
	
	public static void registerOnActivityActionListener(OnActivityAction listener) {
		if(listener == null || mOnActivityActions.contains(listener)) {
			return;
		}
		mOnActivityActions.add(listener);
	}
	
	public static void unRegisterOnActivityActionListener(OnActivityAction listener) {
		if(listener == null || !mOnActivityActions.contains(listener)) {
			return;
		}
		mOnActivityActions.remove(listener);
	}
	
	public static void onActivityResult(int requestCode, int resultCode, Intent data) {
		for(OnActivityAction listener : mOnActivityActions) {
			listener.onActivityResult(requestCode, resultCode, data);
		}
	}
	
	public static void clearOnActivityActionListeners() {
		mOnActivityActions.clear();
	}
}
