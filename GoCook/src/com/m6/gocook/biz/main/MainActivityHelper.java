package com.m6.gocook.biz.main;

import java.util.ArrayList;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;

import com.m6.gocook.base.fragment.OnActionBarEventListener;
import com.m6.gocook.base.fragment.OnActivityAction;
import com.m6.gocook.base.fragment.OnKeyDown;

public class MainActivityHelper {
	
	/** request */
	public static final int REQUEST_CODE_FOLLOW = 101;
	public static final int REQUEST_CODE_RECIPE_EDIT = 102;
	public static final int REQUEST_CODE_RECIPE = 103;
	public static final int REQUEST_CODE_INPUT = 104;

	/** result */
	public static final int RESULT_CODE_FOLLOW = 201;
	public static final int RESULT_CODE_RECIPE_EDIT_CREATED = 202;
	public static final int RESULT_CODE_RECIPE_EDIT_UPDATED = 203;
	public static final int RESULT_CODE_RECIPE_DELETED = 204;
	public static final int RESULT_CODE_RECIPE_UPDATED = 205;
	public static final int RESULT_CODE_INPUT = 206;
	
	
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
			listener.onCustomActivityResult(requestCode, resultCode, data);
		}
	}
	
	public static void clearOnActivityActionListeners() {
		mOnActivityActions.clear();
	}
	
	public static ArrayList<OnKeyDown> mOnKeyDownListeners = new ArrayList<OnKeyDown>();
	
	public static void registerOnKeyDownListener(OnKeyDown listener) {
		if(listener == null || mOnKeyDownListeners.contains(listener)) {
			return;
		}
		mOnKeyDownListeners.add(listener);
	}
	
	public static void unRegisterOnKeyDownListener(OnKeyDown listener) {
		if(listener == null || !mOnKeyDownListeners.contains(listener)) {
			return;
		}
		mOnKeyDownListeners.remove(listener);
	}
	
	public static void clearOnKeyDownListeners() {
		mOnKeyDownListeners.clear();
	}
	
	public static boolean onKeyDown(int keyCode, KeyEvent event) {
		for(OnKeyDown listener : mOnKeyDownListeners) {
			return listener.onKeyDown(keyCode, event);
		}
		return false;
	}

	public static ArrayList<OnActionBarEventListener> mOnActionBarEventListeners = new ArrayList<OnActionBarEventListener>();
	
	public static void registerOnActionBarEventListener(OnActionBarEventListener listener) {
		if(listener == null || mOnActionBarEventListeners.contains(listener)) {
			return;
		}
		mOnActionBarEventListeners.add(listener);
		
	}
	
	public static void unRegisterOnActionBarEventListener(OnActionBarEventListener listener) {
		if(listener == null || !mOnActionBarEventListeners.contains(listener)) {
			return;
		}
		mOnActionBarEventListeners.remove(listener);
		
	}
	
	public static void clearOnActionBarEventListener() {
		mOnActionBarEventListeners.clear();
	}
	
	public static void OnFragmentSwitch(Class<? extends Fragment> fragment) {
		for(OnActionBarEventListener listener : mOnActionBarEventListeners) {
			listener.OnFragmentSwitch(fragment);
		}
	}
	
	public static void OnActionBarClick(View view, int id) {
		for(OnActionBarEventListener listener : mOnActionBarEventListeners) {
			listener.OnActionBarClick(view ,id);
		}
	}
	
}
