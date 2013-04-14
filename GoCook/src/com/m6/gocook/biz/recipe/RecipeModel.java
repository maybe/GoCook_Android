package com.m6.gocook.biz.recipe;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.util.net.NetUtils;

public class RecipeModel {

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
	
}
