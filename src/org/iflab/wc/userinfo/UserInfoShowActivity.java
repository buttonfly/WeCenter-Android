package org.iflab.wc.userinfo;

import org.apache.http.Header;
import org.iflab.wc.app.BaseActivity;
import org.iflab.wc.app.WcApis;
import org.iflab.wc.app.WcApplication;
import org.iflab.wc.common.GlobalVariables;
import org.iflab.wc.common.TipsToast;
import org.iflab.wc.follow.FollowActivity;
import org.iflab.wc.image.SmartImageView;
import org.iflab.wc.topic.TopicFragmentActivity;
import org.iflab.wc.ui.MainActivity;
import org.json.JSONException;
import org.json.JSONObject;
import org.iflab.wc.R;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoShowActivity extends BaseActivity implements OnClickListener {
	private static final String TAG = "UserInfoShowActivity";

	private int haveFrocus = NO;
	private static final int YES = 1;
	private static final int NO = 0;

	private static TipsToast tipsToast;

	private SmartImageView iv_avatar;

	private Button bt_focus;

	private int uid;

	private TextView tv_username;
	private TextView tv_topic;
	private TextView tv_ifocus_person;
	private TextView tv_focusi_person;
	private TextView tv_thanks;
	private TextView tv_votes;
	private TextView tv_collect;
	private TextView tv_replys;
	private TextView tv_asks;
	private TextView tvSignature;

	private TextView tv_focusi_person_comment, tv_ifocus_person_comment,
			tv_topic_comment;

	private LinearLayout lv_topics, lv_replys, lv_asks, lv_focusi_person,
			lv_ifocus_person, lv_articles;

	private ProgressBar pb_change_follow;
	private AsyncHttpClient asyncHttpClient;
	private LinearLayout ll_logout;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_information_show);
		Intent intent = this.getIntent();
		uid = intent.getIntExtra("uid", 0);
		initView();
		if (WcApplication.isNetworkConnected(UserInfoShowActivity.this)) {
			getUserInfo();
		} else {
			showTips(R.drawable.tips_error, R.string.net_notconnect);

		}
	}

	private void initView() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(null);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.show();

		iv_avatar = (SmartImageView) findViewById(R.id.iv_avatar);
		tv_username = (TextView) findViewById(R.id.tv_username);

		lv_topics = (LinearLayout) findViewById(R.id.lv_topics);
		lv_topics.setOnClickListener(this);
		tv_topic = (TextView) findViewById(R.id.tv_topic);

		lv_ifocus_person = (LinearLayout) findViewById(R.id.lv_ifocus_person);
		lv_ifocus_person.setOnClickListener(this);
		tv_ifocus_person = (TextView) findViewById(R.id.tv_ifocus_person);

		lv_focusi_person = (LinearLayout) findViewById(R.id.lv_focusi_person);
		lv_focusi_person.setOnClickListener(this);
		tv_focusi_person = (TextView) findViewById(R.id.tv_focusi_person);

		tv_thanks = (TextView) findViewById(R.id.tv_thanks);
		tv_votes = (TextView) findViewById(R.id.tv_votes);
		tv_collect = (TextView) findViewById(R.id.tv_collect);

		lv_replys = (LinearLayout) findViewById(R.id.lv_replys);
		lv_replys.setOnClickListener(this);
		tv_replys = (TextView) findViewById(R.id.tv_replys);

		lv_asks = (LinearLayout) findViewById(R.id.lv_asks);
		lv_asks.setOnClickListener(this);
		tv_asks = (TextView) findViewById(R.id.tv_asks);

		lv_articles = (LinearLayout) findViewById(R.id.lv_articles);
		lv_articles.setOnClickListener(this);

		bt_focus = (Button) findViewById(R.id.bt_focus);
		bt_focus.setOnClickListener(this);

		tv_focusi_person_comment = (TextView) findViewById(R.id.tv_focusi_person_comment);
		tv_ifocus_person_comment = (TextView) findViewById(R.id.tv_ifocus_person_comment);
		tv_topic_comment = (TextView) findViewById(R.id.tv_topic_comment);

		tvSignature = (TextView) findViewById(R.id.tvSignature);

		pb_change_follow = (ProgressBar) findViewById(R.id.pb_change_follow);

		ll_logout = (LinearLayout) findViewById(R.id.ll_logout);
		ll_logout.setOnClickListener(this);
		Log.i(TAG, "UID:" + uid);
		if (uid == WcApplication.getUid()) {
			Log.i(TAG, "uid:" + WcApplication.getUid());
			ll_logout.setVisibility(View.VISIBLE);
		} else {
			if (WcApplication.getUid() == 0) {
				bt_focus.setVisibility(View.INVISIBLE);
			} else {
				bt_focus.setVisibility(View.VISIBLE);
			}
			ll_logout.setVisibility(View.GONE);
			tv_focusi_person_comment.setText("关注他的人");
			tv_ifocus_person_comment.setText("他关注的人");
			tv_topic_comment.setText("他关注的话题");
		}
		if (haveFrocus == YES) {
			bt_focus.setBackgroundResource(R.drawable.btn_silver_normal);
			bt_focus.setTextColor(android.graphics.Color.BLACK);
			bt_focus.setText("取消关注");
		}
	}

	/**
	 * 获取用户个人中心数据
	 */
	private void getUserInfo() {
		// TODO Auto-generated method stub
		AsyncHttpClient getUserInfo = new AsyncHttpClient();
		PersistentCookieStore mCookieStore = new PersistentCookieStore(this);
		getUserInfo.setCookieStore(mCookieStore);
		RequestParams params = new RequestParams();
		params.put("uid", uid);
		String url = WcApis.USER_INFO;
		getUserInfo.get(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onSuccess(int statusCode, Header[] headers,
					byte[] responseBody) {
				// TODO Auto-generated method stub
				String responseContent = new String(responseBody);
				parseResult(responseContent);

			}

			@Override
			public void onFailure(int statusCode, Header[] headers,
					byte[] responseBody, Throwable error) {
				// TODO Auto-generated method stub
				showTips(R.drawable.tips_error, R.string.get_user_info);
			}
		});
	}

	/**
	 * 解析服务器返回的JSON数据并设置到界面上
	 * 
	 * @param responseContent
	 */
	private void parseResult(String responseContent) {
		// TODO Auto-generated method stub
		try {
			JSONObject result = new JSONObject(responseContent);
			int errno = result.getInt("errno");
			String err = result.getString("err");
			if (errno == 1) {
				JSONObject rsm = result.getJSONObject("rsm");
				tv_username.setText(rsm.getString("user_name"));
				tv_focusi_person.setText(rsm.getString("fans_count"));
				tv_ifocus_person.setText(rsm.getString("friend_count"));
				tv_topic.setText(rsm.getString("topic_focus_count"));
				tv_votes.setText(rsm.getString("agree_count"));
				tv_thanks.setText(rsm.getString("thanks_count"));
				tv_collect.setText(rsm.getString("answer_favorite_count"));
				tv_replys.setText(rsm.getString("answer_count"));
				tv_asks.setText(rsm.getString("question_count"));
				if (rsm.getString("avatar_file") != null) {
					iv_avatar.setImageUrl(WcApis.USER_IMAGE_BASE
							+ rsm.getString("avatar_file"));
				}
				String signature = rsm.getString("signature");
				if (!signature.equals("false")) {
					tvSignature.setText(signature);
				}
				haveFrocus = rsm.getInt("has_focus");
				if (haveFrocus == YES) {
					bt_focus.setBackgroundResource(R.drawable.btn_silver_normal);
					bt_focus.setTextColor(android.graphics.Color.BLACK);
					bt_focus.setText("取消关注");
				} else {
					bt_focus.setBackgroundResource(R.drawable.btn_green_normal);
					bt_focus.setTextColor(android.graphics.Color.WHITE);
					bt_focus.setText("关注");
				}

			} else {
				Toast.makeText(UserInfoShowActivity.this, err,
						Toast.LENGTH_SHORT).show();
			}

		} catch (JSONException e) {
			Log.i(TAG, "JSONException:" + e.toString());
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		// 清除储存的cookie，用户数据并跳转
		case R.id.ll_logout:
			SharedPreferences mSharedPreferences = this.getSharedPreferences(
					"userData", MODE_PRIVATE);
			SharedPreferences.Editor SPeditor = mSharedPreferences.edit();
			SPeditor.remove("uid");
			SPeditor.remove("userName");
			SPeditor.remove("password");
			SPeditor.commit();
			WcApplication.setIsLogined(false);
			WcApplication.setUid(0);
			WcApplication.setAvatarUrl("");
			WcApplication.setUserName("");
			PersistentCookieStore cookieStore = new PersistentCookieStore(
					UserInfoShowActivity.this);
			cookieStore.clear();
			MainActivity.actionStar(UserInfoShowActivity.this);
			finish();
			break;
		case R.id.lv_topics:
			Intent intent = new Intent(UserInfoShowActivity.this,
					TopicFragmentActivity.class);
			intent.putExtra("uid", String.valueOf(uid));
			startActivity(intent);
			break;
		case R.id.lv_focusi_person:
			Intent intent2 = new Intent(UserInfoShowActivity.this,
					FollowActivity.class);
			intent2.putExtra("userorme", GlobalVariables.ATTENEION_ME);
			intent2.putExtra("uid", String.valueOf(uid));
			startActivity(intent2);
			break;
		case R.id.lv_ifocus_person:
			Intent intent1 = new Intent(UserInfoShowActivity.this,
					FollowActivity.class);
			intent1.putExtra("uid", String.valueOf(uid));
			intent1.putExtra("userorme", GlobalVariables.ATTENTION_USER);
			startActivity(intent1);
			break;
		case R.id.lv_articles:
			Intent intent3 = new Intent(UserInfoShowActivity.this,
					ArticleActivity.class);
			intent3.putExtra("isArticle", GlobalVariables.ARTICLE);
			intent3.putExtra("uid", String.valueOf(uid));
			startActivity(intent3);
			break;
		case R.id.lv_asks:
			Intent intent4 = new Intent(UserInfoShowActivity.this,
					ArticleActivity.class);
			intent4.putExtra("isArticle", GlobalVariables.QUESTION);
			intent4.putExtra("uid", String.valueOf(uid));
			startActivity(intent4);
			break;
		case R.id.lv_replys:
			Intent intent5 = new Intent(UserInfoShowActivity.this,
					MyAnswerActivity.class);
			intent5.putExtra("uid", String.valueOf(uid));
			startActivity(intent5);
			break;
		case R.id.bt_focus:
			if (haveFrocus == YES) {
				haveFrocus = NO;
				bt_focus.setBackgroundResource(R.drawable.btn_green_normal);
				bt_focus.setTextColor(android.graphics.Color.WHITE);
				bt_focus.setText("关注");
			} else {
				haveFrocus = YES;
				bt_focus.setBackgroundResource(R.drawable.btn_silver_normal);
				bt_focus.setTextColor(android.graphics.Color.BLACK);
				bt_focus.setText("取消关注");
			}
			pb_change_follow.setVisibility(View.VISIBLE);
			changeFrocusStatus();
			bt_focus.setClickable(false);
			break;
		default:
			break;
		}
	}

	/**
	 * 更改关注状态
	 */
	private void changeFrocusStatus() {
		// TODO Auto-generated method stub
		asyncHttpClient = new AsyncHttpClient();
		PersistentCookieStore mCookieStore = new PersistentCookieStore(this);
		asyncHttpClient.setCookieStore(mCookieStore);
		RequestParams followStatus = new RequestParams();
		followStatus.put("uid", uid);// 所要取消关注的uid
		asyncHttpClient.get(WcApis.CHANGE_FOLLOW, followStatus,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1,
							byte[] responseBody) {
						new String(responseBody);
						bt_focus.setClickable(true);
						pb_change_follow.setVisibility(View.GONE);
					}

					@Override
					public void onFailure(int arg0, Header[] arg1,
							byte[] responseBody, Throwable arg3) {
						// TODO Auto-generated method stub
						String responseContent = new String(responseBody);
						pb_change_follow.setVisibility(View.GONE);
						Toast.makeText(UserInfoShowActivity.this,
								responseContent + "关注失败，请重试！",
								Toast.LENGTH_SHORT).show();
						// 更改按钮状态
						bt_focus.setClickable(true);
						if (haveFrocus == YES) {
							haveFrocus = NO;
							bt_focus.setBackgroundResource(R.drawable.btn_green_normal);
							bt_focus.setTextColor(android.graphics.Color.WHITE);
							bt_focus.setText("关注");
						} else {
							haveFrocus = YES;
							bt_focus.setBackgroundResource(R.drawable.btn_silver_normal);
							bt_focus.setTextColor(android.graphics.Color.BLACK);
							bt_focus.setText("取消关注");
						}
					}
				});
	}

	private void showTips(int iconResId, int msgResId) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(getApplication().getBaseContext(),
					msgResId, TipsToast.LENGTH_SHORT);
		}
		tipsToast.show();
		tipsToast.setIcon(iconResId);
		tipsToast.setText(msgResId);
	}

	protected void onResume() {
		super.onResume();

		if (WcApplication.isNetworkConnected(UserInfoShowActivity.this)) {
			getUserInfo();
		} else {
			showTips(R.drawable.tips_error, R.string.net_break);

		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (uid == WcApplication.getUid()) {
			getMenuInflater().inflate(R.menu.userinforedit, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.edit) {
			Intent intent = new Intent(UserInfoShowActivity.this,
					UserInfoEditActivity.class);
			startActivity(intent);
			return true;
		}
		if (id == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public static void actionStar(Context context, int uid) {
		Intent mIntent = new Intent(context, UserInfoShowActivity.class);
		mIntent.putExtra("uid", uid);
		context.startActivity(mIntent);
	}

}
