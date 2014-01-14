package com.m6.gocook.biz.order;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.accounts.AccountManager;
import android.content.Context;
import android.text.TextUtils;

import com.m6.gocook.base.entity.request.CShopcartInfo;
import com.m6.gocook.base.entity.response.COrderQueryResult;
import com.m6.gocook.base.entity.response.CShopCartResult;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.util.net.NetUtils;

public class OrderModel {

	public static CShopCartResult orderRequest(Context context, CShopcartInfo cShopcartInfo) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("wares", cShopcartInfo.getJsonData()));
		String result = NetUtils.httpPost(context, Protocol.URL_BUY_ORDER, params);
		CShopCartResult cShopCartResult = new CShopCartResult(result);
		return cShopCartResult;
	}
	
	public static COrderQueryResult getOrderQueryResult(Context context, String startDay, String endDay, int page) {
		try {
			List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
			// 注意开始时间和结束时间位置
			params.add(new BasicNameValuePair("start_day", URLEncoder.encode(startDay, "UTF-8")));
			params.add(new BasicNameValuePair("end_day", URLEncoder.encode(endDay, "UTF-8")));
			params.add(new BasicNameValuePair("page", URLEncoder.encode(String.valueOf(page), "UTF-8")));
			String result = NetUtils.httpPost(context, Protocol.URL_BUY_ORDER_QUERY, params);
			COrderQueryResult cOrderQueryResult = new COrderQueryResult(result);
			return cOrderQueryResult;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getCookie(Context context) {
		String result = NetUtils.httpGet(Protocol.URL_BUY_ORDER_AUTH, AccountModel.getCookie(context));
		if (!TextUtils.isEmpty(result)) {
			try {
				JSONObject json = new JSONObject(result);
				if (json != null) {
					int resultCode = json.optInt("result", 1);
					if (resultCode == Protocol.VALUE_RESULT_OK) {
						return json.getString("name") + "=" + json.getString("value");
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
