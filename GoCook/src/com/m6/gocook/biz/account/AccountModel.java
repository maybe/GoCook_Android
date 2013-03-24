package com.m6.gocook.biz.account;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.m6.gocook.util.net.NetUtils;

public class AccountModel {
	
	public static boolean login(String username, String password) {
		boolean success = false;
		String url = "http://192.168.1.100/user/login";
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("login", username));
		params.add(new BasicNameValuePair("password", password));
		NetUtils.httpPost(url, params);
		return success;
	}
}
