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

public class ImageDownloadManager {
	private final static String LOG_TAG = "ImageDownLoadManager";
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

	class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> mImageViewReference;
		private String mImageUrl = null;
		private Context mContext = null;

		public ImageDownloader(ImageView imageView) {
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
					bitmap = BitmapFactory.decodeStream(openConnection
							.getInputStream());
				}
				if (bitmap != null) {
					addBitmapToMemoryCache(url, bitmap);

					writeFile(bitmap, filename);
				}
				return bitmap;

			} catch (Exception ex) {
				ex.printStackTrace();
				return null;
			}
		}

		private void writeFile(Bitmap bmp, String f) {
			FileOutputStream out = null;

			try {
				out = mContext.openFileOutput(f, Context.MODE_PRIVATE);
				bmp.compress(Bitmap.CompressFormat.PNG, 80, out);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (out != null)
						out.close();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}
