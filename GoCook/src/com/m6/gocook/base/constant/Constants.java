package com.m6.gocook.base.constant;

import android.os.Environment;

public class Constants {

	public static final String FILE_IMAGE_PATH = Environment.getExternalStorageDirectory().getPath();
	
	public static final String FILE_GOCOOK_IMAGE = FILE_IMAGE_PATH + "/" + "GoCook/image/";
	
	public static final String BASE_URL = "http://192.168.1.103/";
	
	public static final String URL_LOGIN = BASE_URL + "user/login";
	
	public static final String URL_REGISTER = BASE_URL + "user/register";
}
