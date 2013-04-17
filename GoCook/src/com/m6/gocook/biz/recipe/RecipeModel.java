package com.m6.gocook.biz.recipe;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.m6.gocook.R.string;
import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.base.db.table.RecipePurchaseList;
import com.m6.gocook.base.db.table.SearchHistory;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.util.log.Logger;
import com.m6.gocook.util.net.NetUtils;

public class RecipeModel {
	
	private static final String TAG = RecipeModel.class.getCanonicalName();

	public static RecipeEntity getRecipe(Context context, int recipeId) {
		
		String result = NetUtils.httpGet(Constants.URL_RECIPE);
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(jsonObject != null) {
			RecipeEntity entity = new RecipeEntity();
			entity.parse(null);
			return entity;
		} 

		// Temporary Fake Data
		RecipeEntity entity = new RecipeEntity();
		entity.parse(null);
		return entity;
		
//		return null;
	}
	
	public static void saveRecipeToProcedureList(Context context, RecipeEntity recipeEntity) {
		
		if(recipeEntity != null) {
			
			ContentValues values = new ContentValues();
			values.put(RecipePurchaseList.RECIPE_ID, recipeEntity.getId());
			values.put(RecipePurchaseList.RECIPE_NAME, recipeEntity.getName());
			Uri uri = GoCookProvider.getTableUri(RecipePurchaseList.TABLE);
			Uri resultUri = context.getContentResolver().insert(uri, values);
			Logger.i(TAG, "inserted:" + resultUri.toString());
			
			int materialNum = recipeEntity.getMaterials().size();
			ContentValues[] valuesA = new ContentValues[materialNum];
			for (int i = 0; i < materialNum; i++) {
				RecipeEntity.Material material = recipeEntity.getMaterials().get(i);
				ContentValues newValues = new ContentValues();
				newValues.put(RecipeMaterialPurchaseList.RECIPE_ID, recipeEntity.getId());
				newValues.put(RecipeMaterialPurchaseList.MATERIAL_NAME, material.getName());
				newValues.put(RecipeMaterialPurchaseList.MATERIAL_REMARK, material.getRemark());

				valuesA[i] = newValues;
			}
			
			Uri uriMaterial = GoCookProvider.getTableUri(RecipeMaterialPurchaseList.TABLE);
			int result = context.getContentResolver().bulkInsert(uriMaterial, valuesA);
		}
	}
	
}
