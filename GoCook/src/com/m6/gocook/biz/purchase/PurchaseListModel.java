package com.m6.gocook.biz.purchase;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;

import com.m6.gocook.R.string;
import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.base.db.table.RecipePurchaseList;
import com.m6.gocook.base.db.table.SearchHistory;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.util.log.Logger;

public class PurchaseListModel {

	public static Cursor getRecipePurchaseListCursor(Context context) {
		
		Uri uri = GoCookProvider.getTableUri(RecipePurchaseList.TABLE);
		Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
		return cursor;
	}
	
	public static Cursor getRecipeMaterialPurchaseListCursor(Context context, String recipeId) {
		
		Uri uri = GoCookProvider.getTableUri(RecipeMaterialPurchaseList.TABLE);
		Cursor cursor = context.getContentResolver().query(uri, null,
				RecipeMaterialPurchaseList.RECIPE_ID + "=?",
				new String[] { recipeId }, null);
		return cursor;
	}
	
	public static boolean isRecipeSavedToProcedureList(Context context, String recipeId) {

		Uri uri = GoCookProvider.getTableUri(RecipeMaterialPurchaseList.TABLE);
		Cursor cursor = context.getContentResolver().query(uri, null,
				RecipeMaterialPurchaseList.RECIPE_ID + "=?",
				new String[] { recipeId }, null);
		boolean isSaved = cursor.getCount() > 0;
		cursor.close();
		return isSaved;
	}

	public static void saveRecipeToProcedureList(Context context, RecipeEntity recipeEntity) {
		
		if(recipeEntity != null) {
			
			ContentValues values = new ContentValues();
			values.put(RecipePurchaseList.RECIPE_ID, recipeEntity.getId());
			values.put(RecipePurchaseList.RECIPE_NAME, recipeEntity.getName());
			Uri uri = GoCookProvider.getTableUri(RecipePurchaseList.TABLE);
			Uri resultUri = context.getContentResolver().insert(uri, values);
			
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
	
	public static void removeRecipeFromPurchaseList(Context context, String recipeId) {
		
		Uri uri = GoCookProvider.getTableUri(RecipePurchaseList.TABLE);
		int result = context.getContentResolver().delete(uri, 
				RecipePurchaseList.RECIPE_ID + "=?", 
				new String[] { recipeId });
		
		Uri materialUri = GoCookProvider.getTableUri(RecipeMaterialPurchaseList.TABLE);
		int result2 = context.getContentResolver().delete(materialUri, 
				RecipeMaterialPurchaseList.RECIPE_ID + "=?", 
				new String[] { recipeId });
	}
	
	public static void updateRecipeMaterialPurchased(Context context, String recipeId, boolean isBought) {
		
		Uri uri = GoCookProvider.getTableUri(RecipeMaterialPurchaseList.TABLE);
		ContentValues values = new ContentValues();
		values.put(RecipeMaterialPurchaseList.IS_BOUGHT, isBought ? 1 : 0);
		int result = context.getContentResolver().update(uri, values,
				RecipeMaterialPurchaseList._ID + "=?", 
				new String[] { recipeId });
	}
	
}
