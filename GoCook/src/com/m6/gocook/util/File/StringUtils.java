package com.m6.gocook.util.File;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {

	/**
	 * 去除换行符
	 * 
	 * @param src
	 * @return
	 */
	public static String trimLineFeed(String src) {
		Pattern p = Pattern.compile("\\s*|\t|\r|\n");
        Matcher m = p.matcher(src);
        return m.replaceAll("");
	}
	
}
