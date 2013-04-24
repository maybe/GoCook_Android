package com.m6.gocook.biz.popular;

import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.m6.gocook.base.protocol.ServerProtocol;
import com.m6.gocook.util.model.ModelUtils;
import com.m6.gocook.util.net.NetUtils;

public class PopularModel {
	
	public static Map<String, Object> getPopularData() {
		String result = NetUtils.httpGet(ServerProtocol.URL_POPULAR);
		if(TextUtils.isEmpty(result)) {
			return null;
		}
		
		try {
			JSONObject json = new JSONObject(result);
			Map<String, Object> data =  ModelUtils.json2Map(json);
			return data;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
