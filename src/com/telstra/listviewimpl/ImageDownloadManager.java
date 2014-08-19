package com.telstra.listviewimpl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;
import android.widget.ImageView;

import com.telstra.utils.Utils;

public class ImageDownloadManager {
	private final static String LOG_TAG = "ImageDownLoadManager";

	// This object holds the memory area for memory caching. It is thread safe
	// so no need to implement synchronization here.
	private LruCache<String, Bitmap> mMemorycache;

	private ImageDownloadManager() {
		// Assigned 1/10th of the available process memory to Memory Cache
		mMemorycache = new LruCache<String, Bitmap>((int) Runtime.getRuntime()
				.maxMemory() / (1024 * 10));

	}

	public static ImageDownloadManager getInstance() {
		return new ImageDownloadManager();
	}

	private void addBitmapToMemoryCache(String url, Bitmap bitMap) {
		if (mMemorycache.get(url) == null)
			mMemorycache.put(url, bitMap);
	}

	private Bitmap getBitmapFromMemoryCache(String url) {
		return mMemorycache.get(url);
	}

	public void drawImage(String imageUrl, ImageView iv) {
		if (imageUrl == null) {
			iv.setVisibility(ImageView.INVISIBLE);
		} else {
			if (getBitmapFromMemoryCache(imageUrl) != null)
				iv.setImageBitmap(getBitmapFromMemoryCache(imageUrl));
			else {
				ImageDownloader imgDownloader = new ImageDownloader(iv);
				imgDownloader.executeOnExecutor(
						ImageDownloader.THREAD_POOL_EXECUTOR, imageUrl);
			}
		}
	}

	/*
	 * This class provide background image download and add it to memory and
	 * persistent cache for future use. It also scale the image.
	 */

	private class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> mImageViewReference;
		private String mImageUrl = null;
		private Context mContext = null;

		private ImageDownloader(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
			this.mContext = imageView.getContext();
			mImageViewReference = new WeakReference<ImageView>(imageView);
		}

		// Download image in background.
		@Override
		protected Bitmap doInBackground(String... params) {
			mImageUrl = params[0];
			return getBitmap(mImageUrl);
		}

		// Once complete, see if ImageView is still around and set bitmap.
		@Override
		protected void onPostExecute(Bitmap bitmap) {
			if (mImageViewReference != null && bitmap != null) {
				final ImageView imageView = mImageViewReference.get();
				if (imageView != null) {
					if (((String) imageView.getTag()).equals(mImageUrl))
						imageView.setImageBitmap(bitmap);
				}
			}
		}

		private Bitmap getBitmap(String url) {
			Bitmap bitmap = null;
			try {
				Log.d(LOG_TAG, "Inside bitBitmap().");
				String filename = String.valueOf(url.hashCode());
				try {
					bitmap = BitmapFactory.decodeFileDescriptor(mContext
							.openFileInput(filename).getFD());
				} catch (FileNotFoundException ex) {
					Log.v(LOG_TAG, "File <" + filename
							+ "> not found in storage cache");
				}

				if (bitmap == null) {
					URLConnection openConnection = new URL(url)
							.openConnection();
					openConnection.setConnectTimeout(20000);
					openConnection.setReadTimeout(20000);
					bitmap = BitmapFactory.decodeStream(openConnection
							.getInputStream());

					if (bitmap != null) {
						bitmap = Utils.scaleImage(bitmap, 200, 200);
					}
				}
				if (bitmap != null) {
					addBitmapToMemoryCache(url, bitmap);

					writeFile(bitmap, filename);
				}
				return bitmap;

			} catch (Exception ex) {
				Log.w(LOG_TAG,
						"Network stream is not correct. there is something wrong with URL or network.");
				// ex.printStackTrace();
				return null;
			}
		}

		private void writeFile(Bitmap bmp, String f) {
			FileOutputStream out = null;

			try {
				out = mContext.openFileOutput(f, Context.MODE_PRIVATE);
				bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
			} catch (Exception e) {
				Log.d(LOG_TAG,
						"File <"
								+ f
								+ "> can not be opened in write mode in internal storage");
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex) {
					Log.w(LOG_TAG, "FileOutputStream is not available");
				}
			}
		}
	}
}
