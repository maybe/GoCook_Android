package com.m6.gocook.biz.search;

import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.SearchList;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SearchListModel {

	public static Cursor getSearchList(Context context) {
		Uri uri = GoCookProvider.getTableUri(SearchList.TABLE);
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		return cursor;
	}
}
