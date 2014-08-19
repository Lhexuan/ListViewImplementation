package com.telstra.utils;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Log;

/**
 * This class defines the generic methods
 */

public class Utils {

	public static final String LOG_TAG = "Utils";

	/**
	 * This method scale the given bitmap to the desired size passed to this
	 * image.
	 * 
	 * @param bitmap
	 * 
	 * @param newWidth
	 * 
	 * @param newHeight
	 * 
	 * @return Bitmap
	 */
	public static Bitmap scaleImage(Bitmap bitmap, int newWidth, int newHeight) {

		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		// calculate the scale
		float scaleWidth = (float) newWidth / width;
		float scaleHeight = (float) newHeight / (height * scaleWidth);

		// create a matrix for the manipulation
		Matrix matrix = new Matrix();

		// resize the bit map
		matrix.postScale(scaleWidth, scaleWidth);
		matrix.postScale(scaleHeight, scaleHeight);

		try {
			// recreate the new Bitmap and set it back
			return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
					true);
		} catch (IllegalArgumentException e) {
			Log.d(LOG_TAG, "Can not scale the bitmap to the desired size.");
			return null;
		}

	}
}
