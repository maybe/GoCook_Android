package com.m6.gocook.util.File;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.m6.gocook.base.constant.Constants;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Environment;

/**
 * 图片加工处理类
 * 
 */
public class ImgUtils {
	
	public static File createBitmapFile(String fileName, Bitmap bitmap) {
		if(bitmap != null) {
			File file = null;
			FileOutputStream out = null;
			try {
//				if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
					file = new File(Constants.FILE_GOCOOK_IMAGE + fileName + ".png");
					if(!file.exists()) {
						file.getParentFile().mkdirs();
						file.createNewFile();
					}
					
					out = new FileOutputStream(file);
					bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
					
//				}				
				
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if(out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return file;

		}
		return null;
	}
	
	/**
	 * 按固定比例比例进行裁剪
	 * 
	 * @param context
	 * @param src
	 * @param frameW
	 * @param frameH
	 * @return
	 */
	public static Bitmap resizeBitmap(Context context, Bitmap src,
			final int frameW, final int frameH) {

		if (src == null) {
			return null;
		}

		final int w = src.getWidth();// 源文件的大小
		final int h = src.getHeight();
		if (w == 0 || h == 0)
			return null;

		if (w == frameW && h == frameH) {// 如果比例相同，那么不进行缩略
			return src;
		} else {
			try {
				float scaleWidth = ((float) frameW) / w;// 宽度缩小比例
				float scaleHeight = ((float) frameH) / h;// 高度缩小比例

				Matrix matrix = new Matrix();// 矩阵
				matrix.postScale(scaleWidth, scaleHeight);// 设置矩阵比例

				Bitmap resizedBitmap = Bitmap.createBitmap(src, 0, 0, w, h,
						matrix, true);// 直接按照矩阵的比例把源文件画入进行

				if (src != null && !src.isRecycled()) {
					src.recycle();
					src = null;
				}

				return resizedBitmap;
			} catch (java.lang.OutOfMemoryError e) {

			}
			return null;
		}

	}
}
