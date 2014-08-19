package com.telstra.listviewimpl;

import java.util.ArrayList;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class ListViewActivity extends Activity implements
		SwipeRefreshLayout.OnRefreshListener {
	static final String LOGCAT_TAG = "ListViewActivity";
	private ArrayList<FeedElementInfo> mListFeed = new ArrayList<FeedElementInfo>();
	SimpleAdapter mAdapter = null;
	final static String BLANK_STRING = "";
	private SwipeRefreshLayout mSwipeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);

		// For implementing Swipe to refresh functionality//
		mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.container);
		mSwipeLayout.setOnRefreshListener(this);
		mSwipeLayout.setColorScheme(android.R.color.holo_blue_bright,
				android.R.color.holo_green_light,
				android.R.color.holo_orange_light,
				android.R.color.holo_red_light);

		getFeedInfoList(this.getResources().getString(R.string.dropbox_url));

		ListView lstView = (ListView) findViewById(R.id.lstView);
		mAdapter = new SimpleAdapter(this, mListFeed);
		lstView.setAdapter(mAdapter);
	}

	// Callback for swipe down the screen
	@Override
	public void onRefresh() {
		getFeedInfoList(this.getResources().getString(R.string.dropbox_url));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_view, menu);
		return true;
	}

	/**
	 * This method calls asynchronous function to download and parse the json
	 * method
	 * <p>
	 * This method always returns immediately
	 * 
	 * @param url
	 *            an String URL giving the base location of the json file
	 * @return void
	 */
	public void getFeedInfoList(String url) {

		if (ifNetworkAvailable()) {
			mSwipeLayout.setRefreshing(true);
			new DownloaderTask(mListFeed).execute(url);
		} else
			mSwipeLayout.setRefreshing(false);

	}

	private Boolean ifNetworkAvailable() {
		final ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
		if (activeNetwork != null)
			if (!activeNetwork.isConnected()) {
				Toast.makeText(
						getBaseContext(),
						this.getResources().getString(
								R.string.no_network_warning),
						Toast.LENGTH_SHORT).show();
				return false;
			} else
				return true;
		else {
			Toast.makeText(getBaseContext(),
					this.getResources().getString(R.string.no_network_warning),
					Toast.LENGTH_SHORT).show();
			return false;
		}
	}

	/**
	 * This class handles the json download and parsing functionality. It
	 * provide this functionality in asynchronous manner so that activity should
	 * not block.
	 * 
	 */
	public class DownloaderTask extends AsyncTask<String, Void, Boolean> {

		private ArrayList<FeedElementInfo> mLstFeed;
		private String mJsonTitle = null;

		public DownloaderTask(ArrayList<FeedElementInfo> lstFeed) {
			this.mLstFeed = lstFeed;
		}

		/**
		 * This method is called asynchronously to download and parse the json
		 * method. It initializes the lstFeed ArrayList.
		 * 
		 * @param String
		 *            [] an String URL giving the base location of the json file
		 * @return String returns the title of the json massege in case of
		 *         successful parsing.
		 */
		@Override
		protected Boolean doInBackground(String... params) {

			Log.d(LOGCAT_TAG, "Inside <doInBackground> downloading JSON");

			// android.os.Debug.waitForDebugger();
			HttpClient httpCLient = new DefaultHttpClient();
			HttpGet httpget = new HttpGet(params[0]);
			ResponseHandler<String> responseHndlr = new BasicResponseHandler();

			String responseBody = null;

			try {
				responseBody = httpCLient.execute(httpget, responseHndlr);

				JsonParser jParser = new JsonParser();
				JsonObject jArray = (JsonObject) jParser.parse(responseBody);
				JsonArray jRows = jArray.getAsJsonArray("rows");
				mJsonTitle = jArray.get("title").isJsonNull() ? null : jArray
						.get("title").getAsString();
				mLstFeed.clear();

				for (JsonElement jElement : jRows) {

					String title = jElement.getAsJsonObject().get("title")
							.isJsonNull() ? BLANK_STRING : jElement
							.getAsJsonObject().get("title").getAsString();
					String desc = jElement.getAsJsonObject().get("description")
							.isJsonNull() ? BLANK_STRING : jElement
							.getAsJsonObject().get("description").getAsString();
					String url = jElement.getAsJsonObject().get("imageHref")
							.isJsonNull() ? BLANK_STRING : jElement
							.getAsJsonObject().get("imageHref").getAsString();

					mLstFeed.add(new FeedElementInfo(title, desc, url));
				}
				Log.d(LOGCAT_TAG, "Exiting <doInBackground>downloading JSON");
			} catch (Exception ex) {
				ex.printStackTrace();
				return false;

			}

			return true;
		}

		/**
		 * This method is called on Activity main thread. It sets the ActionBar
		 * title and notify adapter for ArrayList change
		 * 
		 * @param String
		 *            an String representing ActionBar title.
		 * @return String returns the title of the json massege in case of
		 *         successful parsing.
		 */
		@Override
		protected void onPostExecute(Boolean result) {
			if (result) {
				getActionBar().setTitle(mJsonTitle);
				refreshAdapter();
			} else
				Toast.makeText(getBaseContext(),
						getResources().getString(R.string.json_error),
						Toast.LENGTH_LONG).show();
			mSwipeLayout.setRefreshing(false);

		}

		/*
		 * Notify the Adapter for data change in ArrayList.
		 */
		public void refreshAdapter() {
			mAdapter.notifyDataSetChanged();
			mSwipeLayout.setRefreshing(false);
		}
	}

	public class FeedElementInfo {
		public String mTitle;
		public String mDescription;
		public String mImgURL;

		public FeedElementInfo(String title, String desc, String url) {
			this.mTitle = title;
			this.mDescription = desc;
			this.mImgURL = url;
		}
	}

}
