package org.iflab.wc.ui;

import android.app.Activity;
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
import org.iflab.wc.app.WecenterApi;
import org.iflab.wc.app.WecenterApplication;
import org.iflab.wc.app.WecenterManager;
import org.iflab.wc.ui.RegisterActivity;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 应用启动 已有用户信息则异步登录并延时跳转至MainActivity 没有则显示登录，注册按钮
 * 
 * @author: Timor(www.LogcatBlog.com)
 * @created: 2014-09-15
 */
public class LaunchActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "LaunchActivity";
	private Button btn_login, btn_register;
	private TextView tv_visit;
	private LinearLayout login_ll;
	private AsyncHttpClient client;
	private Long postTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_launch);
		WecenterManager.getInstance().pushOneActivity(LaunchActivity.this);
		initView();
		handleView();
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
	 * 根据情况处理View的隐藏和显示
	 */
	private void handleView() {
		// TODO 解密数据
		// 如果都不为空则延时切换到MainActivity。发送数据到服务端验证
		String userName = WecenterApplication.getUserName();
		String password = WecenterApplication.getPassword();
		Log.i(TAG, "userName：" + userName);
		Log.i(TAG, "password：" + password);
		if (!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)) {
			login_ll.setVisibility(View.GONE);
			login(userName, password);
			postTime = System.currentTimeMillis();
			// 新创建的线程以延时跳转
			// new Thread() {
			// @Override
			// public void run() {
			// try {
			// Thread.sleep(1000);
			// } catch (InterruptedException e) {
			// e.printStackTrace();
			// } finally {
			// MainActivity.actionStar(LaunchActivity.this);
			// finish();
			// // TODO 添加动画效果
			// }
			// }
			// }.start();
		} else {
			// 密码或账号为空则让用户输入
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
		// POST发送信息
		RequestParams params = new RequestParams();
		params.put("user_name", userName);
		params.put("password", password);
		String url = WecenterApi.LOGIN;
		Log.d(TAG, "url：" + url);
		if (WecenterApplication.isNetworkConnected(this)) {
			postData(url, params);
		} else {
			// TODO 开启离线模式
			WecenterApplication.setIsloginErr(true);
			WecenterApplication.setIsLogin(false);
			WecenterApplication.setLoginErrInfo("NetBreak");
			MainActivity.actionStar(this);
			finish();
			// TODO 添加动画效果
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
				// 用存储的信息异步登录有错误，在MainActivity会读取此数据判断并处理
				WecenterApplication.setIsloginErr(true);
				WecenterApplication.setIsLogin(false);
				WecenterApplication.setLoginErrInfo("NetBreak");
				MainActivity.actionStar(LaunchActivity.this);
				finish();
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
			// 成功时errno为1，失败为-1
			if (errno == 1) {
				JSONObject rsm = jsonObject.getJSONObject("rsm");
				int uid = rsm.getInt("uid");
				String userName = rsm.getString("user_name");
				String avatarUrl = rsm.getString("avatar_file");
				saveLoginData(uid, userName, avatarUrl);
				starMain();
			} else {
				WecenterApplication.setIsloginErr(true);
				WecenterApplication.setLoginErrInfo(err);
				WecenterApplication.setIsLogin(false);
				MainActivity.actionStar(LaunchActivity.this);
				finish();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			// TODO 保存异常信息并根据用户设置决定是否上传至服务器
			Log.e(TAG, "LOGIN" + "JSONException");
			e.printStackTrace();
		}
	}

	/**
	 * 延时跳转至MainActivity，以保证动画播放完毕。
	 */
	private void starMain() {
		// TODO Auto-generated method stub
		if (System.currentTimeMillis() - postTime < 1000) {
			// 新创建的线程以延时跳转
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						MainActivity.actionStar(LaunchActivity.this);
						finish();
						// TODO 添加动画效果
					}
				}
			}.start();
		} else {
			MainActivity.actionStar(LaunchActivity.this);
			finish();
		}

	}

	/**
	 * 保存登录成功的用户数据
	 */
	private void saveLoginData(int uid, String userName, String avatarUrl) {
		// TODO Auto-generated method stub
		// TODO 加密数据存放
		WecenterApplication.setIsLogin(true);
		WecenterApplication.setUid(uid);
		WecenterApplication.setUserName(userName);
		WecenterApplication.setAvatarUrl(avatarUrl);
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
