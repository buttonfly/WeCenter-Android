package org.iflab.wc.adapter;

import java.util.List;

import org.iflab.wc.detail.essay.EssayDetailActivity;
import org.iflab.wc.detail.question.AnswerActivity;
import org.iflab.wc.detail.question.QuestionDetailActivity;
import org.iflab.wc.image.SmartImageView;
import org.iflab.wc.model.HomeItemModel;
import org.iflab.wc.userinfo.UserInfoShowActivity;
import org.iflab.wc.R;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HomeAdapter extends ArrayAdapter<HomeItemModel> {
	private int resourceId;
	private Context context;

	public HomeAdapter(Context context, int itemViewResourceId,
			List<HomeItemModel> objects) {
		super(context, itemViewResourceId, objects);
		this.context = context;
		resourceId = itemViewResourceId;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final HomeItemModel itemModel = getItem(position);
		ViewHolder viewHolder;
		if (convertView == null) {
			convertView = LayoutInflater.from(getContext()).inflate(resourceId,
					null);
			viewHolder = new ViewHolder();
			viewHolder.llItemLayout = (LinearLayout) convertView
					.findViewById(R.id.llItemLayout);
			viewHolder.complexLayout = (LinearLayout) convertView
					.findViewById(R.id.llHomeListItemContent);
			if (itemModel.getLayoutType() == HomeItemModel.LAYOUT_TYPE_SIMPLE) {
				viewHolder.complexLayout.setVisibility(View.GONE);
			} else {
				viewHolder.complexLayout.setVisibility(View.VISIBLE);
			}
			viewHolder.avatar = (FrameLayout) convertView
					.findViewById(R.id.flAvatar);
			viewHolder.avatarImage = (SmartImageView) convertView
					.findViewById(R.id.ivHomeListItemAvatar);
			viewHolder.userName = (TextView) convertView
					.findViewById(R.id.tvHomeListItemName);
			viewHolder.action = (TextView) convertView
					.findViewById(R.id.tvHomeListIteAction);
			viewHolder.itemTitle = (TextView) convertView
					.findViewById(R.id.tvHomeListItemTitle);
			viewHolder.bestAnswer = (TextView) convertView
					.findViewById(R.id.tvHomeListItemBestAnswer);
			viewHolder.agreeCount = (TextView) convertView
					.findViewById(R.id.tvHomeListItemAgreeCount);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
			if (itemModel.getLayoutType() == HomeItemModel.LAYOUT_TYPE_SIMPLE) {
				viewHolder.complexLayout.setVisibility(View.GONE);
			} else {
				viewHolder.complexLayout.setVisibility(View.VISIBLE);
			}
		}
		// 将数据设置到相应的View上
		if (!itemModel.getAvatarUrl().equals("")) {
			viewHolder.avatarImage.setImageUrl(itemModel.getAvatarUrl());
		}
		viewHolder.agreeCount.setText(itemModel.getAgreeCount() + " ");
		viewHolder.action.setText(itemModel.getAction());
		viewHolder.userName.setText(itemModel.getUserName());
		viewHolder.itemTitle.setText(itemModel.getItemTitle());
		String replacAnswer = itemModel.getBestAnswer().replaceAll(
				"<img [^>]*>", "ͼƬ");
		viewHolder.bestAnswer.setText(replacAnswer);
		// 设置各个对象的监听事件
		viewHolder.llItemLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// TODO Auto-generated method stub
				if (itemModel.getAction().equals("发布该文章")
						|| itemModel.getAction().equals("赞同该文章")) {
					Intent mIntent = new Intent(context,
							EssayDetailActivity.class);
					mIntent.putExtra("eid",
							Integer.toString(itemModel.getItemTitleUid()));
					context.startActivity(mIntent);
				} else {
					Intent mIntent = new Intent(context,
							QuestionDetailActivity.class);
					mIntent.putExtra("questionid",
							Integer.toString(itemModel.getItemTitleUid()));
					context.startActivity(mIntent);
				}
			}
		});
		viewHolder.avatarImage.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UserInfoShowActivity.actionStar(context, itemModel.getUserUid());
			}
		});
		viewHolder.userName.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				UserInfoShowActivity.actionStar(context, itemModel.getUserUid());
			}
		});
		viewHolder.itemTitle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (itemModel.getAction().equals("发布该文章")
						|| itemModel.getAction().equals("赞同该文章")) {
					Intent mIntent = new Intent(context,
							EssayDetailActivity.class);
					mIntent.putExtra("eid",
							Integer.toString(itemModel.getItemTitleUid()));
					context.startActivity(mIntent);
				} else {
					Intent mIntent = new Intent(context,
							QuestionDetailActivity.class);
					mIntent.putExtra("questionid",
							Integer.toString(itemModel.getItemTitleUid()));
					context.startActivity(mIntent);
				}
			}
		});
		viewHolder.bestAnswer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent mIntent = new Intent(context, AnswerActivity.class);
				mIntent.putExtra("answerid",
						Integer.toString(itemModel.getBestAnswerUid()));
				context.startActivity(mIntent);
			}
		});
		return convertView;
	}

	class ViewHolder {
		LinearLayout complexLayout, llItemLayout;
		FrameLayout avatar;
		SmartImageView avatarImage;
		TextView userName;
		TextView action;
		TextView itemTitle;
		TextView bestAnswer;
		TextView agreeCount;
	}
}
