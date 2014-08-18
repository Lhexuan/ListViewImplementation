package com.telstra.listviewimpl;

import java.lang.ref.WeakReference;
import java.net.URL;
import java.net.URLConnection;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

public class ImageDownloadManager {
	private final static String LOG_TAG = "ImageDownLoadManager";

	private ImageDownloadManager() {

	}

	public static ImageDownloadManager getInstance() {
		return new ImageDownloadManager();
	}

	public void drawImage(String imageUrl, ImageView iv) {
		if (imageUrl == null || imageUrl.isEmpty()) {
			iv.setVisibility(ImageView.INVISIBLE);
		} else {

			ImageDownloader imgDownloader = new ImageDownloader(iv);
			imgDownloader.execute(imageUrl);
		}
	}

	class ImageDownloader extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference<ImageView> mImageViewReference;
		private String mImageUrl = null;

		public ImageDownloader(ImageView imageView) {
			// Use a WeakReference to ensure the ImageView can be garbage
			// collected
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

				URLConnection openConnection = new URL(url).openConnection();
				bitmap = BitmapFactory.decodeStream(openConnection
						.getInputStream());
			}

			catch (Exception ex) {
				ex.printStackTrace();
			}
			return bitmap;
		}
	}
}
