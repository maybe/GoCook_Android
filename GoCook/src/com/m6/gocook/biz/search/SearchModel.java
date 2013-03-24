package com.m6.gocook.biz.search;

import java.util.ArrayList;
import java.util.List;

import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.SearchHistory;
import com.m6.gocook.base.db.table.SearchList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SearchModel {

	public static void getSearchList(Context context) {
//		ArrayList<SearchItem> list = new ArrayList<SearchItem>();
//		
//		Uri uri = GoCookProvider.getTableUri(SearchHistory.TABLE);
//		for(SearchItem item : list) {
//			
//		}
//		context.getContentResolver().bulkInsert(uri, values);
	}
	
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
}
