package com.m6.gocook.biz.profile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.m6.gocook.base.constant.PrefKeys;
import com.m6.gocook.base.entity.People;
import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.biz.account.AccountModel;
import com.m6.gocook.util.File.ImgUtils;
import com.m6.gocook.util.model.ModelUtils;
import com.m6.gocook.util.net.NetUtils;
import com.m6.gocook.util.preference.PrefHelper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

public class ProfileModel {
	
	public static final String NICKNAME = "nickname";
	public static final String SEX = "gender";
	public static final String AGE = "age";
	public static final String CAREER = "career";
	public static final String CITY = "city";
	public static final String PROVINCE = "province";
	public static final String TELEPHONE = "tel";
	public static final String INTRO = "intro";
	
	public static void saveAge(Context context, String age) {
		PrefHelper.putString(context, PrefKeys.PROFILE_AGE, age);
	}
	
	public static String getAge(Context context) {
		return PrefHelper.getString(context, PrefKeys.PROFILE_AGE, "");
	}
	
	public static void saveCity(Context context, String city) {
		PrefHelper.putString(context, PrefKeys.PROFILE_CITY, city);
	}
	
	public static String getCity(Context context) {
		return PrefHelper.getString(context, PrefKeys.PROFILE_CITY, "");
	}
	
	public static void saveProvince(Context context, String province) {
		PrefHelper.putString(context, PrefKeys.PROFILE_PROVINCE, province);
	}
	
	public static String getProvince(Context context) {
		return PrefHelper.getString(context, PrefKeys.PROFILE_PROVINCE, "");
	}
	
	public static void saveSex(Context context, String sex) {
		PrefHelper.putString(context, PrefKeys.PROFILE_SEX, sex);
	}
	
	public static String getSex(Context context) {
		return PrefHelper.getString(context, PrefKeys.PROFILE_SEX, "");
	}
	
	public static void saveCareer(Context context, String career) {
		PrefHelper.putString(context, PrefKeys.PROFILE_CAREER, career);
	}
	
	public static String getCareer(Context context) {
		return PrefHelper.getString(context, PrefKeys.PROFILE_CAREER, "");
	}
	
	public static void saveIntro(Context context, String intro) {
		PrefHelper.putString(context, PrefKeys.PROFILE_INTRO, intro);
	}
	
	public static String getIntro(Context context) {
		return PrefHelper.getString(context, PrefKeys.PROFILE_INTRO, "");
	}
	
	public static void saveTelephone(Context context, String tel) {
		PrefHelper.putString(context, PrefKeys.PROFILE_TELEPHONE, tel);
	}
	
	public static String getTelephone(Context context) {
		return PrefHelper.getString(context, PrefKeys.PROFILE_TELEPHONE, "");
	}
	
	public static String getInfo(Context context) {
		return PrefHelper.getString(context, PrefKeys.PROFILE_INFO, "");
	}
	
	public static void saveInfo(Context context, String info) {
		PrefHelper.putString(context, PrefKeys.PROFILE_INFO, info);
	}
	
	public static String getMyRecipesText(Context context) {
		return PrefHelper.getString(context, PrefKeys.PROFILE_MYRECIPES, "");
	}
	
	public static void saveMyRecipesText(Context context, String myRecipesText) {
		PrefHelper.putString(context, PrefKeys.PROFILE_MYRECIPES, myRecipesText);
	}
	
	/**
	 * 修改个人信息
	 * 
	 * @param context
	 * @param avatart
	 * @param name
	 * @param sex
	 * @param age
	 * @param career
	 * @param province
	 * @param city
	 * @param telephone
	 * @param intro
	 * @return
	 */
	public static String updateInfo(Context context, File avatart, String name, String sex, String age, String career, 
			String province, String city, String telephone, String intro) {
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		if (name != null) {
			params.add(new BasicNameValuePair(NICKNAME, name));
		}
		if (sex != null) {
			params.add(new BasicNameValuePair(SEX, sex));
		}
		if (age != null) {
			params.add(new BasicNameValuePair(AGE, age));
		}
		if (career != null) {
			params.add(new BasicNameValuePair(CAREER, career));
		}
		if (province != null) {
			params.add(new BasicNameValuePair(PROVINCE, province));
		}
		if (city != null) {
			params.add(new BasicNameValuePair(CITY, city));
		}
		if (telephone != null) {
			params.add(new BasicNameValuePair(TELEPHONE, telephone));
		}
		if (intro != null) {
			params.add(new BasicNameValuePair(INTRO, intro));
		}
		return NetUtils.httpPost(context, Protocol.URL_PROFILE_UPDATE, params);
//		return NetUtils.httpPost(context, Protocol.URL_PROFILE_UPDATE, params, avatart, "avatar");
	}
	
	/**
	 * 获取个人信息
	 * 
	 * @param context
	 * @return
	 */
	public static Map<String, Object> getBasicInfo(Context context) {
		String result = NetUtils.httpGet(Protocol.URL_PROFILE_BASIC_INFO, AccountModel.getCookie(context));
		if (TextUtils.isEmpty(result)) {
			return null;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(result);
			if(jsonObject != null) {
				Map<String, Object> resultMap = ModelUtils.json2Map(jsonObject);
				if (resultMap != null && ModelUtils.getIntValue(resultMap, Protocol.KEY_RESULT, 1) == Protocol.VALUE_RESULT_OK) {
					return ModelUtils.getMapValue(resultMap, "result_user_info");
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static Map<String, Object> follow(Context context, String followId, boolean follow) {
		String url;
		if (follow) {
			url = Protocol.URL_PROFILE_FOLLOW + followId;
		} else {
			url = Protocol.URL_PROFILE_UNFOLLOW + followId;
		}
		String result = NetUtils.httpGet(url, AccountModel.getCookie(context));
		if (TextUtils.isEmpty(result)) {
			return null;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(result);
			if(jsonObject != null) {
				Map<String, Object> resultMap = ModelUtils.json2Map(jsonObject);
				return resultMap;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
 	
	public static ArrayList<People> getPeoples() {
		ArrayList<People> peoples = new ArrayList<People>();
		for (int i = 0; i < 10; i++) {
			People people = new People();
			people.setFans(23);
			people.setFollows(30);
			people.setName("卖萌蜀黎喵呜桑");
			people.setImage("images/recipe/140/23403.1.jpg");
			peoples.add(people);
		}
		return peoples;
	}
	
	private static Bitmap getAvatarBitmap(Context context, Bitmap bitmap, Uri uri) {
		Bitmap avatarBitmap = null;
		if (uri == null) {
    		if (bitmap != null) {
    			avatarBitmap = ImgUtils.resizeBitmap(context, bitmap, 120, 120);
    		}
    	} else {
    		String imgPath = null;
    		Cursor cursor = context.getContentResolver().query(uri, null,
                    null, null, null);
    		if (cursor != null && cursor.moveToFirst()) {
    			imgPath = cursor.getString(1); // 图片文件路径
    		}

    		if (cursor != null) {
    			cursor.close();
    		}
    		
    		if (!TextUtils.isEmpty(imgPath)) {
    			avatarBitmap = ImgUtils.resizeBitmap(context, BitmapFactory.decodeFile(imgPath), 120, 120);
    		}
    	}
		return avatarBitmap;
	}
	
	public static File getAvatarFile(Context context, Bitmap bitmap, Uri uri) {
		Bitmap avatarBitmap = getAvatarBitmap(context, bitmap, uri);
		return ImgUtils.createBitmapFile("avatar" + System.currentTimeMillis(), avatarBitmap);
	}

}
