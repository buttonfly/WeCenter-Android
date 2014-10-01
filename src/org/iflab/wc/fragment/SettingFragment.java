package org.iflab.wc.fragment;

import org.iflab.wc.R;
import org.iflab.wc.ui.AboutActivity;
import org.iflab.wc.ui.ConnectUsActivity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingFragment extends Fragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View fragmentView = inflater.inflate(R.layout.fragment_setting,
				container, false);
		LinearLayout ll_feedback = (LinearLayout) fragmentView
				.findViewById(R.id.ll_feedback);
		LinearLayout ll_about_zhiwen = (LinearLayout) fragmentView
				.findViewById(R.id.ll_about_zhiwen);
		LinearLayout ll_connect_us = (LinearLayout) fragmentView
				.findViewById(R.id.ll_connect_us);
		ll_about_zhiwen.setOnClickListener(this);
		ll_connect_us.setOnClickListener(this);
		ll_feedback.setOnClickListener(this);
		return fragmentView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_feedback:
			Toast.makeText(getActivity(), "服务器正在升级，请用邮件方式反馈意见",
					Toast.LENGTH_SHORT).show();
			break;
		case R.id.ll_about_zhiwen:
			Intent intent = new Intent(getActivity(), AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.ll_connect_us:
			Intent intent2 = new Intent(getActivity(), ConnectUsActivity.class);
			startActivity(intent2);
			break;

		default:
			break;
		}
	}
}
