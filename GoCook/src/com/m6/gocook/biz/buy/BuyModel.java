package com.m6.gocook.biz.buy;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.base.entity.request.CShopcartInfo;
import com.m6.gocook.base.entity.response.CKeywordQueryResult;
import com.m6.gocook.base.entity.response.COrderQueryResult;
import com.m6.gocook.base.entity.response.CShopCartResult;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.util.net.NetUtils;

public class BuyModel {

	public static final String NAME = "name";
	public static final String NORM = "norm";
	public static final String QUANTITY = "count";
	public static final String PRICE = "price";
	public static final String METHOD = "method";
	public static final String UNIT = "unit";
	public static final String REMARK = "remark";
	public static final String WAREID = "wareid";
	
	
	public static List<Map<String, Object>> getBuyList(Context context, String recipeId) {
		Uri uri = GoCookProvider.getTableUri(RecipeMaterialPurchaseList.TABLE);
		Cursor cursor;
		if (!TextUtils.isEmpty(recipeId)) {
			String selection = RecipeMaterialPurchaseList.RECIPE_ID + "=?" + " AND " + RecipeMaterialPurchaseList.IS_BOUGHT + "<>?";
			String[] selectionArgs = {recipeId, "1"};
			cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);
		} else {
			String selection = RecipeMaterialPurchaseList.IS_BOUGHT + "<>?";
			String[] selectionArgs ={"1"};
			cursor = context.getContentResolver().query(uri, null, selection, selectionArgs, null);
		}
		
		if (cursor != null && cursor.moveToFirst()) {
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			do {
				Map<String, Object> map = new HashMap<String, Object>();
				map.put(RecipeMaterialPurchaseList._ID, cursor.getString(cursor.getColumnIndex(RecipeMaterialPurchaseList._ID)));
				map.put(RecipeMaterialPurchaseList.RECIPE_ID, cursor.getString(cursor.getColumnIndex(RecipeMaterialPurchaseList.RECIPE_ID)));
				map.put(RecipeMaterialPurchaseList.IS_BOUGHT, cursor.getString(cursor.getColumnIndex(RecipeMaterialPurchaseList.IS_BOUGHT)));
				map.put(RecipeMaterialPurchaseList.IS_MAIN, cursor.getString(cursor.getColumnIndex(RecipeMaterialPurchaseList.IS_MAIN)));
				map.put(RecipeMaterialPurchaseList.MATERIAL_NAME, cursor.getString(cursor.getColumnIndex(RecipeMaterialPurchaseList.MATERIAL_NAME)));
				map.put(RecipeMaterialPurchaseList.MATERIAL_REMARK, cursor.getString(cursor.getColumnIndex(RecipeMaterialPurchaseList.MATERIAL_REMARK)));
				list.add(map);
			} while (cursor.moveToNext());
			return list;
		}
		
		if (cursor != null) {
			cursor.close();
		}
		return null;
	}
	
	public static CKeywordQueryResult getBuySearchResult(Context context, String keyword, int pageIndex, int pageRows) {
		try {
			String result = NetUtils.httpGet(String.format(Protocol.URL_BUY_SEARCH, URLEncoder.encode(keyword, "UTF-8"), URLEncoder.encode(String.valueOf(pageIndex), "UTF-8")));
			CKeywordQueryResult cKeywordQueryResult = new CKeywordQueryResult();
			cKeywordQueryResult.parse(result);
			return cKeywordQueryResult;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return null;
	}
	
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
