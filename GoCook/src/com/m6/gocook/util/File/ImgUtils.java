package com.m6.gocook.util.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;

/**
 * 图片加工处理类
 * 
 */
public class ImgUtils {
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
