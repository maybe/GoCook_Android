package com.m6.gocook.biz.buy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.m6.gocook.biz.purchase.PurchaseListModel;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;

public class BuyModel {

	public static List<Map<String, Object>> getBuyList(Context context, String recipeId) {
		if (TextUtils.isEmpty(recipeId)) {
			return null;
		}
		
		Cursor cursor = PurchaseListModel.getRecipeMaterialPurchaseListCursorById(context, recipeId);
		if (cursor != null && cursor.moveToFirst()) {
			List<Map<String, Object>> list = new ArrayList<Map<String,Object>>();
			do {
				Map<String, Object> map = new HashMap<String, Object>();
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
