package com.m6.gocook.biz.buy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;

import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;

public class BuyModel {

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
}
