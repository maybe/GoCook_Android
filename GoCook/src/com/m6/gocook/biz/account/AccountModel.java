package com.m6.gocook.biz.account;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.bool;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;

import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.constant.PrefKeys;
import com.m6.gocook.util.net.NetUtils;
import com.m6.gocook.util.preference.PrefHelper;

public class AccountModel {
	
	public static final String PARAM_USERNAME = "username";
	public static final String PARAM_EMAIL = "email";
	public static final String PARAM_PASSWORD = "password";
	
	public static final String RETURN_RESULT = "result";	
	public static final String RETURN_ERRORCODE = "errorcode";	
	public static final String RETURN_USERNAME = "username";	
	public static final String RETURN_ICON = "icon";
	
	/** 注册失败 */
	public static final int ERRORCODE_FAILURE = 1;
	/** email不可用 */
	public static final int ERRORCODE_EMAIL = 2;
	/** nickname不可用 */
	public static final int ERRORCODE_NICKNAME = 3;
	/** 密码格式不对 */
	public static final int ERRORCODE_PASSWORD = 4;
	/** 其它 */
	public static final int ERRORCODE_OTHERS = 5;
			
	public static final int SUCCESS = 0;
	public static final int FAILURE = 1;
	
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
	
	public static void onLogin(String email, String avatarUrl, String userName) {
		for(OnAccountChangedListener listener : mAccountChangedListeners) {
			listener.onLogin(email, avatarUrl, userName);
		}
	}
	
	public static void onLogout() {
		for(OnAccountChangedListener listener : mAccountChangedListeners) {
			listener.onLogout();
		}
	}
	
	public static void onRegister(String email, String avatarUrl, String userName) {
		for(OnAccountChangedListener listener : mAccountChangedListeners) {
			listener.onRegister(email, avatarUrl, userName);
		}
	}
	
	public static String login(Context context, String username, String password) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("login", username));
		params.add(new BasicNameValuePair("password", password));
		return NetUtils.httpPost(Constants.URL_LOGIN, params);
	}
	
	public static void logout(Context context) {
		PrefHelper.putString(context, PrefKeys.ACCOUNT_EMAIL, "");
		onLogout();
	}
	
	public static String register(String email, String password, String rePassword, String nickname, File avatart) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("email", email));
		params.add(new BasicNameValuePair("nickname", nickname));
		params.add(new BasicNameValuePair("password", password));
		params.add(new BasicNameValuePair("repassword", password));
		params.add(new BasicNameValuePair("avatar", avatart == null ? "" : avatart.toString()));
		
		return NetUtils.httpPost(Constants.URL_REGISTER, params);
	}
	
	public static boolean isLogon(Context context) {
		return !TextUtils.isEmpty(PrefHelper.getString(context, PrefKeys.ACCOUNT_EMAIL, ""));
	}
	
	public static void saveAccount(Context context, String email) {
		PrefHelper.putString(context, PrefKeys.ACCOUNT_EMAIL, email);
	}
}
