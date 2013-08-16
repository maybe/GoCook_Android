package com.m6.gocook.biz.recipe;

import java.io.File;
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
import android.widget.MultiAutoCompleteTextView.CommaTokenizer;

import com.m6.gocook.R.string;
import com.m6.gocook.base.constant.Constants;
import com.m6.gocook.base.constant.PrefKeys;
import com.m6.gocook.base.db.GoCookProvider;
import com.m6.gocook.base.db.table.RecipeMaterialPurchaseList;
import com.m6.gocook.base.db.table.RecipePurchaseList;
import com.m6.gocook.base.db.table.SearchHistory;
import com.m6.gocook.base.entity.RecipeCommentList;
import com.m6.gocook.base.entity.RecipeEntity;
import com.m6.gocook.base.entity.RecipeList;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.biz.profile.ProfileModel;
import com.m6.gocook.util.log.Logger;
import com.m6.gocook.util.net.NetUtils;
import com.m6.gocook.util.preference.PrefHelper;

public class RecipeModel {
	
	private static final String TAG = RecipeModel.class.getCanonicalName();

	
	public static RecipeEntity getRecipe(Context context, String recipeId) {
		
		return getRecipe(context, recipeId, false);
	}
	
	public static RecipeEntity getRecipe(Context context, String recipeId, boolean fromLocal) {
		
		if(context == null || TextUtils.isEmpty(recipeId)) {
			Logger.e(TAG, "getRecipe failed, parameter is invalid");
			return null;
		}
		
		String result = null;
		if (fromLocal) {
			result = PrefHelper.getString(context, PrefKeys.RECIPE_ENTITY, "");
		} else {
			result = NetUtils.httpGet(String.format(Protocol.URL_RECIPE, recipeId), AccountModel.getCookie(context));
			PrefHelper.putString(context, PrefKeys.RECIPE_ENTITY, result);
		}
		
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
	
	public static String postRecipe(Context context, RecipeEntity recipeEntity) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(Protocol.KEY_RECIPE_POST_ID, recipeEntity.getId()));
		params.add(new BasicNameValuePair(Protocol.KEY_RECIPE_POST_NAME, recipeEntity.getName()));
		params.add(new BasicNameValuePair(Protocol.KEY_RECIPE_POST_COVER_IMG, recipeEntity.getCoverImgURL()));
		params.add(new BasicNameValuePair(Protocol.KEY_RECIPE_POST_DESC, recipeEntity.getDesc()));
		params.add(new BasicNameValuePair(Protocol.KEY_RECIPE_POST_CATEGORY, ""));
		params.add(new BasicNameValuePair(Protocol.KEY_RECIPE_POST_MATERIALS, recipeEntity.getMaterialsString()));
		params.add(new BasicNameValuePair(Protocol.KEY_RECIPE_POST_STEPS, recipeEntity.getProcedureString()));
		params.add(new BasicNameValuePair(Protocol.KEY_RECIPE_POST_TIPS, recipeEntity.getTips()));
		return NetUtils.httpPost(context, Protocol.URL_RECIPE_CREATE, params);

	}
	
	public static RecipeList getRecipeData(String url, String cookie) {
		String result = NetUtils.httpGet(url, cookie);
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
	 * 获取我的菜谱(默认加载网络数据) 
	 * 
	 * @param context
	 * @return
	 */
	public static RecipeList getMyRecipes(Context context) {
		return getMyRecipes(context, false);
	}
	public static RecipeList getMyRecipes(Context context, boolean fromLocal) {
		String result;
//		if (fromLocal) {
//			result = ProfileModel.getMyRecipesText(context);
//		} else {
//		}
		result = NetUtils.httpGet(Protocol.URL_PROFILE_MY_RECIPE, AccountModel.getCookie(context));
		if(TextUtils.isEmpty(result)) {
			return null;
		}
		
		try {
			JSONObject json = new JSONObject(result);
			RecipeList recipeListItem = new RecipeList();
			if(recipeListItem.parse(json)) {
				if (!fromLocal) {
					ProfileModel.saveMyRecipesText(context, result);
				}
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
		return getRecipeComments(context, recipeId, false);
	}
	public static RecipeCommentList getRecipeComments(Context context, String recipeId, boolean fromLocal) {
		String result;
		if (fromLocal) {
			result = PrefHelper.getString(context, PrefKeys.RECIPE_COMMENTS, "");
		} else {
			result = NetUtils.httpGet(String.format(Protocol.URL_RECIPE_COMMENT, recipeId), AccountModel.getCookie(context));
			PrefHelper.putString(context, PrefKeys.RECIPE_COMMENTS, result);
		}
		
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(jsonObject != null) {
			RecipeCommentList commentList = new RecipeCommentList();
			commentList.parse(jsonObject);
			return commentList;
		} else {
			return null;
		}
	}
	
	public static String postComment(Context context, String recipeId, String content) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair(Protocol.KEY_POST_RECIPE_COMMENT_RECIPE_ID, recipeId));
		params.add(new BasicNameValuePair(Protocol.KEY_POST_RECIPE_COMMENT_CONTENT, content));
		return NetUtils.httpPost(context, Protocol.URL_RECIPE_COMMENT_POST, params);
	}
	
	public static Boolean addToCollectList(Context context, String recipeId) {
		String result = NetUtils.httpGet(String.format(Protocol.URL_RECIPE_COLLECT_ADD, recipeId), AccountModel.getCookie(context));
		
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(jsonObject != null && jsonObject.optInt(Protocol.KEY_RESULT) == Protocol.VALUE_RESULT_OK) {
			return true;
		} else {
			return false;
		}
	}
	
	public static Boolean removeFromCollectList(Context context, String recipeId) {
		String result = NetUtils.httpGet(String.format(Protocol.URL_RECIPE_COLLECT_DELETE, recipeId), AccountModel.getCookie(context));
		
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		if(jsonObject != null && jsonObject.optInt(Protocol.KEY_RESULT) == Protocol.VALUE_RESULT_OK) {
			return true;
		} else {
			return false;
		}
	}
	
	public static String uploadRecipeCoverImage(Context context, File imageFile) {
		String result = NetUtils.httpPost(context,
				Protocol.URL_RECIPE_UPLOAD_COVERIMAGE_STRING,
				imageFile,
				Protocol.KEY_RECIPE_UPLOAD_COVERIMAGE);
		
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
			if(jsonObject != null && jsonObject.optInt(Protocol.KEY_RESULT) == Protocol.VALUE_RESULT_OK) {
				return jsonObject.optString(Protocol.KEY_RECIPE_UPLOAD_AVATAR);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String uploadRecipeStepImage(Context context, File imageFile) {
		String result = NetUtils.httpPost(context,
				Protocol.URL_RECIPE_UPLOAD_STEPIMAGE_STRING,
				imageFile,
				Protocol.KEY_RECIPE_UPLOAD_STEPIMAGE);
		
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(result);
			if(jsonObject != null && jsonObject.optInt(Protocol.KEY_RESULT) == Protocol.VALUE_RESULT_OK) {
				return jsonObject.optString(Protocol.KEY_RECIPE_UPLOAD_AVATAR);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
}
