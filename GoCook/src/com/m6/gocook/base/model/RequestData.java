package com.m6.gocook.base.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.m6.gocook.util.util.SecurityUtils;


public class RequestData {

	private static final int AppId = 1;
	
	private int cmd;
	
	private String data;
	
	private String md5;
	

	public RequestData(int cmd, BaseData data) {
		this.cmd = cmd;
		this.data = data.getJsonData();
		this.md5 = SecurityUtils.MD5Encry(SecurityUtils.PASSWORD_CRYPT_KEY + this.cmd + AppId + this.data + SecurityUtils.PASSWORD_CRYPT_IV);
	}

	public int getCmd() {
		return cmd;
	}

	public void setCmd(int cmd) {
		this.cmd = cmd;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}
	
	public String getPostData() {
		try {
			JSONObject postData = new JSONObject();
			postData.put("Cmd", cmd);
			postData.put("Data", data);
			postData.put("Md5", md5);
			return postData.toString();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
