package org.iflab.wc.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.iflab.wc.R;
import org.iflab.wc.app.BaseActivity;
import org.iflab.wc.app.WcApis;
import org.iflab.wc.app.WcApplication;
import org.iflab.wc.ui.RegisterActivity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 应用启动判断是否已有用户信息，如果有则异步登录并延时跳转至MainActivity 没有则显示登录，注册按钮让用户登录或注册。
 * 
 * @author: Timor(www.LogcatBlog.com)
 * @created: 2014-09-15
 */
public class LaunchActivity extends BaseActivity implements
		View.OnClickListener {
	private static final String TAG = "LaunchActivity";
	private Button btn_login, btn_register;
	private TextView tv_visit;
	private LinearLayout login_ll;
	private AsyncHttpClient client;
	private Boolean loginErr;// 记录储存的用户信息是否登录出错

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);

		initView();
		asyncLogin();
	}

	private void initView() {
		btn_login = (Button) findViewById(R.id.launch_btn_login);
		btn_register = (Button) findViewById(R.id.launch_btn_register);
		tv_visit = (TextView) findViewById(R.id.launch_tv_visit);
		btn_login.setOnClickListener(this);
		btn_register.setOnClickListener(this);
		tv_visit.setOnClickListener(this);
		login_ll = (LinearLayout) findViewById(R.id.login_ll);
		ImageView launch_iv_logo = (ImageView) findViewById(R.id.laucnch_iv_logo);
		Animation animation = AnimationUtils.loadAnimation(this,
				R.anim.launch_logo_push_down_in);
		launch_iv_logo.startAnimation(animation);
	}

	/**
	 * 用本地存储的用户数据异步登录。如果数据为空则显示登录注册，否则直接异步登录。
	 */
	private void asyncLogin() {
		// TODO 解密数据
		// 如果都不为空则延时切换到MainActivity。发送数据到服务端验证
		String userName = WcApplication.getUserName();
		String password = WcApplication.getPassword();
		if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
			loginErr = false;
			WcApplication.setIsLogined(true);
			login_ll.setVisibility(View.GONE);
			login(userName, password);
			// 新创建的线程以延时跳转
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						if (!loginErr) {
							MainActivity.actionStar(LaunchActivity.this);
							finish();
							// TODO 添加动画效果
						}

					}
				}
			}.start();
		} else {
			WcApplication.setIsLogined(false);
			login_ll.setVisibility(View.VISIBLE);
			Animation animation = AnimationUtils.loadAnimation(this,
					R.anim.launch_ll_fade_in);
			login_ll.startAnimation(animation);
		}
	}

	/**
	 * 登录，如果没有网络则直接进入MainActivit并启用离线模式
	 * 
	 * @param userName
	 * @param password
	 */
	private void login(String userName, String password) {
		// 如果有网络则POST发送信息，否则通知主页面稍候登录
		RequestParams params = new RequestParams();
		params.put("user_name", userName);
		params.put("password", password);
		String url = WcApis.LOGIN;
		if (WcApplication.isNetworkConnected(this)) {
			postData(url, params);
		} else {
			// TODO 开启离线模式
			WcApplication.setIsloginErr(true);
		}
	}

	/**
	 * 将数据POST 到服务器以登录，然后交由parseResponseResult（）处理结果
	 * 
	 * @param url
	 *            登录API
	 * @param params
	 *            用户名和密码
	 */
	private void postData(String url, RequestParams params) {
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		client = new AsyncHttpClient();
		client.setCookieStore(cookieStore);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onSuccess(int arg0, Header[] arg1,
					byte[] responseContent) {
				// TODO Auto-generated method stub
				String reponseResult = new String(responseContent);
				parseResponseResult(reponseResult);
				Log.d(TAG, "reponseResult：" + reponseResult);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1,
					byte[] responseContent, Throwable arg3) {
				// TODO Auto-generated method stub
				WcApplication.setIsloginErr(true);
			}
		});
	}

	/**
	 * 解析Http返回的结果并根据结果做出相应处理，将需要保存的结果交由saveLoginData保存
	 * 
	 * @param reponseResult
	 *            返回的结果
	 */
	private void parseResponseResult(String reponseResult) {
		try {
			JSONObject jsonObject = new JSONObject(reponseResult);
			int errno = jsonObject.getInt("errno");
			String err = jsonObject.getString("err");
			// 成功时errno为1，失败为-1。err为出错信息
			if (errno == 1) {
				JSONObject rsm = jsonObject.getJSONObject("rsm");
				int uid = rsm.getInt("uid");
				String userName = rsm.getString("user_name");
				String avatarUrl = rsm.getString("avatar_file");
				saveLoginData(uid, userName, avatarUrl);
			} else {
				loginErr = true;
				WcApplication.setErrInfo(err);// 保存err到Applicant供其他Activity使用
				Intent intent = new Intent();
				intent.putExtra("data", err);
				intent.setAction("org.iflab.wc.broadcast.LOGINERR");
				sendBroadcast(intent);
				Log.i(TAG, "sendBroadcast");
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// TODO 保存异常信息并根据用户设置决定是否上传至服务器
			Log.e(TAG, "LOGIN" + "JSONException");
			e.printStackTrace();
		}
	}

	/**
	 * 保存登录成功的用户数据
	 */
	private void saveLoginData(int uid, String userName, String avatarUrl) {
		// TODO Auto-generated method stub
		// TODO 加密数据存放
		WcApplication.setUid(uid);
		WcApplication.setUserName(userName);
		WcApplication.setAvatarUrl(avatarUrl);
		SharedPreferences mSharedPreferences = this.getSharedPreferences(
				"userData", MODE_PRIVATE);
		SharedPreferences.Editor SPeditor = mSharedPreferences.edit();
		SPeditor.putInt("uid", uid);
		SPeditor.commit();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.launch_btn_login:
			LoginActivity.actionStart(this);
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			break;
		case R.id.launch_btn_register:
			RegisterActivity.actionStart(this);
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			break;
		case R.id.launch_tv_visit:
			MainActivity.actionStar(this);
			// TODO 添加切换动画
			break;
		}

	}

}
