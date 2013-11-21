package com.m6.gocook.util.File;

import java.io.File;
import java.io.FileInputStream;
import java.text.DecimalFormat;

import android.content.Context;

public class FileUtils {

	/**
	 * If this file exists, the method will return it's size, otherwise, will
	 * create the file.
	 * 
	 * @param f
	 *            : file name
	 * @return size of this file
	 * @throws Exception
	 */
	public static long getFileSizes(File f) throws Exception {
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			fis = new FileInputStream(f);
			s = fis.available();
		} else {
			f.createNewFile();
		}
		return s;
	}

	/**
	 * Return the file size by a recursion method.
	 * 
	 * @param f
	 *            : file name
	 * @return size this file
	 * @throws Exception
	 */
	public static long getFileSize(File f) throws Exception {
		long size = 0;
		if (!f.exists())
			return size;
		File flist[] = f.listFiles();
		for (int i = 0; i < flist.length; i++) {
			if (flist[i].isDirectory()) {
				size = size + getFileSize(flist[i]);
			} else {
				size = size + flist[i].length();
			}
		}
		return size;
	}
	
	public static long getSingleFileSize(File f) throws Exception {
		long size = 0;
		if (!f.exists() || f.isDirectory()) {
			return size;
		}
		return f.length();
	}
	
	
	
	/**
	 * 获得非目录文件的文件大小
	 * 
	 * @param f
	 * @return
	 */
	public static String getFormatFileSize(File f) {
		try {
			return formatFileSize(getSingleFileSize(f));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "0K";
	}

	/**
	 * Translate long data to xxK, xxM, xxG format. 2048 --> 2K
	 * 
	 * @param fileS
	 * @return
	 */
	public static String formatFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if (fileS < 1024) {
			fileSizeString = "0K";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

}
