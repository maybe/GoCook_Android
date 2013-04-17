package com.m6.gocook.biz.purchase;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.RecipePurchaseList;
import com.m6.gocook.base.db.table.SearchHistory;

public class PurchaseListModel {

	public static Cursor getRecipePurchaseListCursor(Context context) {
		Uri uri = GoCookProvider.getTableUri(RecipePurchaseList.TABLE);
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		return cursor;
	}
	
	public static void addPurchaseItem(Context context) {
		
	}
	
}
