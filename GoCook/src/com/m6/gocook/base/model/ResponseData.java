package com.m6.gocook.base.model;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

public class ResponseData {

	private int flag = -1;
	
	private String msg;
	
	private String data;
	

	public ResponseData(String response) {
		resolve(response);
	}
	
	private void resolve(String response) {
		if (TextUtils.isEmpty(response)) {
			return;
		}
		
		try {
			JSONObject responseJson = new JSONObject(response);
			flag = responseJson.optInt("Flag");
			msg = responseJson.optString("Msg");
			data = responseJson.optString("Data");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public int getFlag() {
		return flag;
	}

	public void setFlag(int flag) {
		this.flag = flag;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public boolean isSuccessful() {
		return flag == Flag.SUCCESS;
	}
}
