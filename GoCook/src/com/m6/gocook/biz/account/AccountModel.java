package com.m6.gocook.biz.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.R.bool;
import android.content.Context;
import android.text.TextUtils;

import com.m6.gocook.base.constant.PrefKeys;
import com.m6.gocook.util.net.NetUtils;
import com.m6.gocook.util.preference.PrefHelper;

public class AccountModel {
	
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_PASSWORD = "password";
	
	private static ArrayList<OnAccountChangedListener> mAccountChangedListeners = new ArrayList<OnAccountChangedListener>();
	
	public static void registerOnAccountChangedListener(OnAccountChangedListener listener) {
		if(listener == null || mAccountChangedListeners.contains(listener)) {
			return;
		}
		mAccountChangedListeners.add(listener);
	}
	
	public static void unRegisterOnAccountChangedListener(OnAccountChangedListener listener) {
		if(listener == null || !mAccountChangedListeners.contains(listener)) {
			return;
		}
		mAccountChangedListeners.remove(listener);
	}
	
	public static void onLogin(String email) {
		for(OnAccountChangedListener listener : mAccountChangedListeners) {
			listener.onLogin(email);
		}
	}
	
	public static void onLogout() {
		for(OnAccountChangedListener listener : mAccountChangedListeners) {
			listener.onLogout();
		}
	}
	
	public static boolean login(Context context, String username, String password) {
		boolean success = false;
		String url = "http://192.168.1.100/user/login";
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("login", username));
		params.add(new BasicNameValuePair("password", password));
		NetUtils.httpPost(url, params);
		
		saveAccount(context, username);
		return success;
	}
	
	public static void logout(Context context) {
		PrefHelper.putString(context, PrefKeys.ACCOUNT_EMAIL, "");
		onLogout();
	}
	
	public static boolean register(String username, String password, String email) {
		boolean success = false;
		String url = "http://192.168.1.100/user/register";
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("login", username));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("email", email));
		NetUtils.httpPost(url, params);
		return success;
	}
	
	public static boolean isLogon(Context context) {
		return !TextUtils.isEmpty(PrefHelper.getString(context, PrefKeys.ACCOUNT_EMAIL, ""));
	}
	
	public static void saveAccount(Context context, String email) {
		PrefHelper.putString(context, PrefKeys.ACCOUNT_EMAIL, email);
	}
}
