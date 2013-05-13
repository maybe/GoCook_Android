package com.m6.gocook.biz.profile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.message.BasicNameValuePair;

import com.m6.gocook.base.protocol.Protocol;
import com.m6.gocook.util.File.ImgUtils;
import com.m6.gocook.util.net.NetUtils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.text.TextUtils;

public class ProfileModel {
	
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

	public static String UpdateProfile(String name, String birth, String sex, 
			String profession, String city, String intro, File avatarFile) {
		
		List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
		params.add(new BasicNameValuePair("", name));
		params.add(new BasicNameValuePair("", birth));
		params.add(new BasicNameValuePair("", sex));
		params.add(new BasicNameValuePair("", profession));
		params.add(new BasicNameValuePair("", city));
		params.add(new BasicNameValuePair("", intro));
		return NetUtils.httpPost(Protocol.URL_REGISTER, params, avatarFile, "avatar");
	}
}
