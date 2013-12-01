package com.m6.gocook.biz.popular;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

import com.m6.gocook.base.entity.Popular;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.util.net.NetUtils;

public class PopularModel {
	
	public static Popular getPopularData() {
		String result = NetUtils.httpGet(Protocol.URL_POPULAR);
		if(TextUtils.isEmpty(result)) {
			return null;
		}
		
		try {
			JSONObject json = new JSONObject(result);
			Popular popular = new Popular();
			if(popular.parse(json)) {
				return popular;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
