package com.m6.gocook.biz.order;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

import com.m6.gocook.base.entity.request.CShopcartInfo;
import com.m6.gocook.base.entity.response.COrderQueryResult;
import com.m6.gocook.base.entity.response.CShopCartResult;
import com.m6.gocook.base.protocol.Protocol;
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
}
