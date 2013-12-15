package com.m6.gocook.base.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import com.m6.gocook.base.activity.BaseActivity;

public class FragmentHelper {

	public static final String FRAGMENT_ARGS = "fragment_arg";
	public static final String FRAGMENT_NAME = "fragment_name";
	public static final String FRAGMENT_TAG = "fragment_tag";
	
	private FragmentActivity mActivity;
	
	public FragmentHelper(FragmentActivity activity) {
		mActivity = activity;
	}
	
	public static void startActivity(Context context, Fragment fragment) {
		Intent intent = new Intent(context, BaseActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(FRAGMENT_NAME, fragment.getClass().getName());
		bundle.putString(FRAGMENT_TAG, fragment.getClass().getName());
		bundle.putBundle(FRAGMENT_ARGS, fragment.getArguments());
		intent.putExtras(bundle);
		context.startActivity(intent);
	}
	
	public static void startActivityForResult(Fragment host, Fragment target) {
		Intent intent = new Intent(host.getActivity(), BaseActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(FRAGMENT_NAME, target.getClass().getName());
		bundle.putString(FRAGMENT_TAG, target.getClass().getName());
		bundle.putBundle(FRAGMENT_ARGS, target.getArguments());
		intent.putExtras(bundle);
		host.startActivity(intent);
	}
	
	/**
	 * Get the intent to start fragment.
	 * 
	 * @param context
	 * @param clss
	 * @param fragmentName
	 * @param fragmentTag
	 * @param args
	 * @return
	 */
	public static Intent getIntent(Context context, Class<?> clss, String fragmentName, String fragmentTag, Bundle args) {
		Intent intent = new Intent(context, clss);
		Bundle bundle = new Bundle();
		bundle.putString(FRAGMENT_NAME, fragmentName);
		bundle.putString(FRAGMENT_TAG, fragmentTag);
		bundle.putBundle(FRAGMENT_ARGS, args);
		intent.putExtras(bundle);
		return intent;
	}

	public void initFragment(Intent intent, int containerId) {
		if (intent == null) {
			return;
		}

		Bundle bundle = intent.getExtras();
		if (bundle == null) {
			return;
		}
		
		String fragmentName = bundle.getString(FRAGMENT_NAME);
		String fragmentTag = bundle.getString(FRAGMENT_TAG);
		Bundle fragmentArgs = bundle.getBundle(FRAGMENT_ARGS);
		
		FragmentManager fm = mActivity.getSupportFragmentManager();
		Fragment fragment = fm.findFragmentByTag(fragmentTag);
		if(fragment == null) {
			FragmentTransaction ft = fm.beginTransaction();
			fragment = Fragment.instantiate(mActivity, fragmentName, fragmentArgs);
			ft.add(containerId, fragment, fragmentName);
			ft.commit();
		} else {
			if(fragment.isDetached()) {
				FragmentTransaction ft = fm.beginTransaction();
	            ft.attach(fragment);
	            ft.commit();
			}
		}
	}
}
