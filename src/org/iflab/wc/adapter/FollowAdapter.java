package org.iflab.wc.adapter;

import java.util.List;

import org.iflab.wc.app.WcApis;
import org.iflab.wc.model.FollowModel;
import org.iflab.wc.R;
import org.iflab.wc.topic.imageload.ImageDownLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class FollowAdapter extends BaseAdapter {
	private Context context;
	private ImageDownLoader mImageDownLoader;
	private List<FollowModel> attentionUserModels;

	public FollowAdapter(Context context, ImageDownLoader mImageDownLoader,
			List<FollowModel> attentionUserModels) {
		super();
		this.context = context;
		this.mImageDownLoader = mImageDownLoader;
		this.attentionUserModels = attentionUserModels;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return attentionUserModels.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return attentionUserModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHodler viewHodler;
		final String mImageUrl = WcApis.USER_IMAGE_BASE
				+ attentionUserModels.get(position).getUserImageUrl();
		if (convertView == null) {
			viewHodler = new ViewHodler();
			convertView = LayoutInflater.from(context).inflate(
					R.layout.attention_user, parent, false);
			viewHodler.topicTitle = (TextView) convertView
					.findViewById(R.id.topic_tag);
			viewHodler.topicSummary = (TextView) convertView
					.findViewById(R.id.topic1);
			viewHodler.imageView = (ImageView) convertView
					.findViewById(R.id.avatar);
			convertView.setTag(viewHodler);
		} else {
			viewHodler = (ViewHodler) convertView.getTag();
		}
		viewHodler.imageView.setTag(mImageUrl);
		// viewHodler.imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		viewHodler.topicTitle.setText(attentionUserModels.get(position)
				.getUserName());
		viewHodler.topicSummary.setText(attentionUserModels.get(position)
				.getSingnature());
		Bitmap bitmap = mImageDownLoader.getBitmapFromMemCache(mImageUrl
				.replaceAll("[^\\w]", ""));
		if (bitmap != null) {
			viewHodler.imageView.setImageBitmap(bitmap);
		} else {
			viewHodler.imageView.setImageDrawable(context.getResources()
					.getDrawable(R.drawable.ic_avatar_default));
		}
		return convertView;
	}

	class ViewHodler {
		private TextView topicTitle;
		private TextView topicSummary;
		private ImageView imageView;
	}
}
