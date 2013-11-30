package com.m6.gocook.util.File;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.text.TextUtils;

import com.m6.gocook.base.constant.Constants;

/**
 * 图片加工处理类
 * 
 */
public class ImgUtils {
	
	/**
	 * 根据uri获得图片真实地址
	 * 
	 * @param context
	 * @param uri
	 * @return
	 */
    public static String getPath(Context context, Uri uri) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(uri, null, null, null, null);
        if (cursor.moveToFirst()) {
        	int index = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
        	if (index != -1) {
        		return cursor.getString(index);
        	}
        }
        return null;
    }
    
	/**
     * Decode and sample down a bitmap from a file to the requested width and height.
     *
     * @param filename The full path of the file to decode
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @param cache The ImageCache used to find candidate bitmaps for use with inBitmap
     * @return A bitmap sampled down from the original with the same aspect ratio and dimensions
     *         that are equal to or greater than the requested width and height
     */
    public static Bitmap decodeSampledBitmapFromFile(String filename,
            int reqWidth, int reqHeight) {
    	
    	if (TextUtils.isEmpty(filename)) {
    		return null;
    	}

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filename, options);
    }
    
    /**
     * Calculate an inSampleSize for use in a {@link BitmapFactory.Options} object when decoding
     * bitmaps using the decode* methods from {@link BitmapFactory}. This implementation calculates
     * the closest inSampleSize that will result in the final decoded bitmap having a width and
     * height equal to or larger than the requested width and height. This implementation does not
     * ensure a power of 2 is returned for inSampleSize which can be faster when decoding but
     * results in a larger bitmap which isn't as useful for caching purposes.
     *
     * @param options An options object with out* params already populated (run through a decode*
     *            method with inJustDecodeBounds==true
     * @param reqWidth The requested width of the resulting bitmap
     * @param reqHeight The requested height of the resulting bitmap
     * @return The value to be used for inSampleSize
     */
    public static int calculateInSampleSize(BitmapFactory.Options options,
            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }
	
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
//					src.recycle();
					src = null;
				}

				return resizedBitmap;
			} catch (java.lang.OutOfMemoryError e) {

			}
			return null;
		}
	}
}
