package com.m6.gocook.biz.recipe.search;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.SearchHistory;
import com.m6.gocook.base.entity.RecipeListItem;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.util.net.NetUtils;

public class SearchModel {
	
	public static Cursor readSearchHistory(Context context) {
		Uri uri = GoCookProvider.getTableUri(SearchHistory.TABLE);
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		return cursor;
	}
	
	public static void recordSearchHistory(Context context, String keyWords) {
		ContentValues values = new ContentValues();
		values.put(SearchHistory.CONTENT, keyWords);
		Uri uri = GoCookProvider.getTableUri(SearchHistory.TABLE);
		context.getContentResolver().insert(uri, values);
	}
	
	public static RecipeListItem getSearchData(String keyWords, int page) {
		String result = NetUtils.httpGet(String.format(Protocol.URL_RECIPE_SEARCH, keyWords, page));
		if(TextUtils.isEmpty(result)) {
			return null;
		}
		
		try {
			JSONObject json = new JSONObject(result);
			RecipeListItem popularHot = new RecipeListItem();
			if(popularHot.parse(json)) {
				return popularHot;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
}
