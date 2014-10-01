package org.iflab.wc.adapter;

import java.util.List;

import org.iflab.wc.R;
import org.iflab.wc.app.WecenterApi;
import org.iflab.wc.image.SmartImageView;
import org.iflab.wc.model.ExploreItem;
import org.iflab.wc.topic.imageload.ImageDownLoader;
import org.iflab.wc.userinfo.UserInfoShowActivity;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ExploreAdapter extends BaseAdapter {
	private static final String TAG = "ExploreAdapter";
	private List<ExploreItem> newitems;
	private Context context;

	public ExploreAdapter(List<ExploreItem> comitems, Context context,
			ImageDownLoader imageDownLoader) {
		// TODO Auto-generated constructor stub

		super();
		this.newitems = comitems;
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return newitems.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return newitems.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ViewHodler hodler;
		final String mImageUrl = WecenterApi.USER_IMAGE_BASE
				+ newitems.get(arg0).getAvatar_file();
		if (arg1 == null) {
			hodler = new ViewHodler();
			arg1 = LayoutInflater.from(context).inflate(
					R.layout.found_question, null);
			hodler.name = (TextView) arg1.findViewById(R.id.username);
			hodler.question = (TextView) arg1.findViewById(R.id.quescontent);
			hodler.userimage = (SmartImageView) arg1
					.findViewById(R.id.userAvatar);
			hodler.tag = (TextView) arg1.findViewById(R.id.tag);
			arg1.setTag(hodler);

		} else {
			hodler = (ViewHodler) arg1.getTag();
		}
		hodler.userimage.setTag(mImageUrl);
		hodler.name.setText(newitems.get(arg0).getName());
		hodler.question.setText(newitems.get(arg0).getQuestion());
		switch (newitems.get(arg0).getInttag()) {
		case 0:
			hodler.tag.setText("发起了问题");
			break;
		case 1:
			hodler.tag.setText("回复了问题");
			break;
		case 2:
			hodler.tag.setText("发表了文章");
			break;
		default:
			break;
		}
		Log.d(TAG, "ImageUrl:" + mImageUrl);
		if (!TextUtils.isEmpty(mImageUrl)) {
			hodler.userimage.setImageUrl(mImageUrl);
		}

		hodler.userimage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// TODO Auto-generated method stub
				String stringUid = newitems.get(arg0).getUid();
				if (!TextUtils.isEmpty(stringUid)) {
					UserInfoShowActivity.actionStar(context,
							Integer.parseInt(stringUid));
					Log.d("Explore", "uid:" + stringUid);
				}

			}
		});
		return arg1;
	}

	class ViewHodler {
		private TextView name, question, tag;
		private SmartImageView userimage;
	}
}
