package com.m6.gocook.biz.recipe;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.R.integer;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.m6.gocook.R.string;
import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.base.db.table.RecipePurchaseList;
import com.m6.gocook.base.db.table.SearchHistory;
import com.m6.gocook.base.entity.RecipeCommentList;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.util.log.Logger;
import com.m6.gocook.util.net.NetUtils;

public class RecipeModel {
	
	private static final String TAG = RecipeModel.class.getCanonicalName();

	public static RecipeEntity getRecipe(Context context, String recipeId) {
		
		if(context == null || TextUtils.isEmpty(recipeId)) {
			Logger.e(TAG, "getRecipe failed, parameter is invalid");
			return null;
		}
		
		String result = NetUtils.httpGet(String.format(Protocol.URL_RECIPE, recipeId));
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
			entity.parse(jsonObject);
			return entity;
		}

		return null;
	}
	
	public static RecipeList getRecipeData(String url) {
		String result = NetUtils.httpGet(url);
		if(TextUtils.isEmpty(result)) {
			return null;
		}
		
		try {
			JSONObject json = new JSONObject(result);
			RecipeList recipeListItem = new RecipeList();
			if(recipeListItem.parse(json)) {
				return recipeListItem;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 将食材中的分量内容去掉，获得纯粹的食材名称。<br/>
	 * 如：茄子||葱||姜||蒜||酱油|1汤匙|醋|1汤匙|糖|2勺   =>  茄子 葱 姜 蒜 酱油 醋 糖
	 * 
	 * @param materials
	 * @return
	 */
	public static String getRecipePureMaterials(String materials) {
		if (!TextUtils.isEmpty(materials)) {
			String[] array = materials.split("\\|");
			if(array != null) {
				StringBuilder sb = new StringBuilder();
				int size = array.length;
				for(int i = 0; i < size; i++) {
					if(i % 2 == 0) {
						sb.append(array[i] + " ");
					}
				}
				return sb.toString();
			}
		}
		return null;
	}
	
	public static RecipeCommentList getRecipeComments(Context context, String recipeId) {
		return null;
	}
	
	public static String postComment(Context context, String recipeId, String content) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(Protocol.KEY_POST_RECIPE_COMMENT_RECIPE_ID, recipeId));
		params.add(new BasicNameValuePair(Protocol.KEY_POST_RECIPE_COMMENT_CONTENT, content));
		return NetUtils.httpPost(Protocol.URL_RECIPE_COMMENT_POST, params);
	}
	
}
