package com.telstra.listviewimpl;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.telstra.listviewimpl.ListViewActivity.FeedElementInfo;

public class SimpleAdapter extends ArrayAdapter<FeedElementInfo> {

	ArrayList<FeedElementInfo> mListFeedInfo;
	Activity mActivityContext;

	// Overloaded constructor
	public SimpleAdapter(Context context, ArrayList<FeedElementInfo> lstFeedInfo) {
		super(context, R.layout.list_row_layout, lstFeedInfo);
		mActivityContext = (Activity) context;
		mListFeedInfo = lstFeedInfo;
	}

	/**
	 * This method called by Android system when android wants to call. We have
	 * no control on this method.
	 * <p>
	 * This method always returns immediately whether to draw image from map or
	 * queue the request that will be fulfilled later.
	 * 
	 * * @return View
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View rowView = convertView;

		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) mActivityContext
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.list_row_layout, null);
		}

		TextView tvTitle = (TextView) rowView.findViewById(R.id.title);
		TextView tvDesc = (TextView) rowView.findViewById(R.id.desc);
		ImageView ivImage = (ImageView) rowView.findViewById(R.id.img);

		FeedElementInfo feedInfo = mListFeedInfo.get(position);

		tvTitle.setText((feedInfo.mTitle));
		tvDesc.setText(feedInfo.mDescription);
		ivImage.setImageResource(R.drawable.ic_launcher);

		return rowView;
	}
}
