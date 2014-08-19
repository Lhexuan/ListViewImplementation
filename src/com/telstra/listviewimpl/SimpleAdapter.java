package com.telstra.listviewimpl;

import java.util.List;

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

	List<FeedElementInfo> mListFeedInfo;
	Activity mActivityContext;
	ImageDownloadManager mImgDwnldMgr = ImageDownloadManager.getInstance();

	public SimpleAdapter(Context context, List<FeedElementInfo> lstFeedInfo) {
		super(context, R.layout.list_row_layout, lstFeedInfo);
		mActivityContext = (Activity) context;
		mListFeedInfo = lstFeedInfo;
	}

	// Implemented Holder pattern to enhance the performance as inflatation and
	// using findViewById are expensive calls.
	private class ViewHolder {
		TextView tvTitle;
		TextView tvDesc;
		ImageView ivIcon;
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
		ViewHolder holder;

		if (rowView == null) {
			LayoutInflater inflater = (LayoutInflater) mActivityContext
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			rowView = inflater.inflate(R.layout.list_row_layout, null);
			holder = new ViewHolder();
			holder.tvTitle = (TextView) rowView.findViewById(R.id.title);
			holder.tvDesc = (TextView) rowView.findViewById(R.id.desc);
			holder.ivIcon = (ImageView) rowView.findViewById(R.id.img);

			rowView.setTag(holder);
		} else
			holder = (ViewHolder) rowView.getTag();

		FeedElementInfo feedInfo = mListFeedInfo.get(position);
		if (feedInfo != null) {
			holder.tvTitle.setText((feedInfo.mTitle));
			holder.tvDesc.setText(feedInfo.mDescription);

			if (holder.ivIcon.getTag() != null)
				if (!((String) holder.ivIcon.getTag()).equals(feedInfo.mImgURL)) {
					holder.ivIcon.setImageDrawable(null);
				}
			holder.ivIcon.setTag(feedInfo.mImgURL);

			mImgDwnldMgr.drawImage(feedInfo.mImgURL, holder.ivIcon);

		}

		return rowView;
	}
}
