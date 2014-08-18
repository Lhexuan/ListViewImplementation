package com.telstra.listviewimpl;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.ListView;

public class ListViewActivity extends Activity {
	private ArrayList<FeedElementInfo> mListFeed = new ArrayList<FeedElementInfo>();
	final static String BLANK_STRING = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_view);

		getFeedInfoList(null);

		ListView lstView = (ListView) findViewById(R.id.lstView);
		SimpleAdapter adapter = new SimpleAdapter(this, mListFeed);
		lstView.setAdapter(adapter);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.list_view, menu);
		return true;
	}

	public void getFeedInfoList(String url) {

		// Sample list
		mListFeed
				.add(new FeedElementInfo(
						"Sample Title Test....",
						"kjfhsdefkjhsdfsd sdkhfdskjfhsdkfhdsfhsfjsf g sdfjgdsfdsg fdjs fgsdfg dsf hgsdjffjksfjsgfsd fFGSDJFKgdSfjf sfds dshgsdghdfghf",
						"dsadadsad"));

		mListFeed
				.add(new FeedElementInfo(
						"ssadsadsadsad",
						"kjfhsdefkjhsdfsd sdkhfdskjfhsdkfhdsfhsfjsf g sdfjgdsfdsg fdjs fgsdfg dsf hgsdjffjksfjsgfsd fFGSDJFKgdSfjf sfds dshgsdghdfghf",
						"dsadadsad"));
		mListFeed
				.add(new FeedElementInfo(
						"ssadsadsadsad",
						"kjfhsdefkjhsdfsd sdkhfdskjfhsdkfhdsfhsfjsf g sdfjgdsfdsg fdjs fgsdfg dsf hgsdjffjksfjsgfsd fFGSDJFKgdSfjf sfds dshgsdghdfghf",
						"dsadadsad"));

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
