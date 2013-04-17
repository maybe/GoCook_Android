package com.m6.gocook.biz.purchase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.base.db.table.RecipePurchaseList;
import com.m6.gocook.base.db.table.SearchHistory;

public class PurchaseListModel {

	public static Cursor getRecipePurchaseListCursor(Context context) {
		
		Uri uri = GoCookProvider.getTableUri(RecipePurchaseList.TABLE);
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		return cursor;
	}
	
	public static Cursor getRecipeMaterialPurchaseListCursor(Context context, String recipeId) {
		
		Uri uri = GoCookProvider.getTableUri(RecipeMaterialPurchaseList.TABLE);
		ContentValues values = new ContentValues();
		values.put(RecipePurchaseList.RECIPE_ID, recipeId);
		Cursor cursor = context.getContentResolver().query(uri, null,
				RecipeMaterialPurchaseList.RECIPE_ID + "=?",
				new String[] { recipeId }, null);
		return cursor;
	}
	
	public static void addPurchaseItem(Context context) {
		
	}
	
}
