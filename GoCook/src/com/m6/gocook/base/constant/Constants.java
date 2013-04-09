package com.m6.gocook.base.constant;

import android.os.Environment;

public class Constants {

	public static final String FILE_IMAGE_PATH = Environment.getExternalStorageDirectory().getPath();
	
	public static final String FILE_GOCOOK_IMAGE = FILE_IMAGE_PATH + "/" + "GoCook/image/";
	
	public static final String URL_LOGIN = "http://192.168.1.100/user/login";
	
	public static final String URL_REGISTER = "http://192.168.1.100/user/register";
}
