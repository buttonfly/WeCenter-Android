package org.iflab.wc.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import android.view.View.OnFocusChangeListener;

import org.apache.http.Header;
import org.iflab.wc.R;
import org.iflab.wc.app.WecenterApi;
import org.iflab.wc.app.WecenterApplication;
import org.iflab.wc.app.WecenterManager;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity implements OnClickListener {
	private static final String TAG = "LoginActivity";
	private AsyncHttpClient client;
	private ProgressDialog pbDialog;
	private Button login_btn_login;
	private TextView login_tv_visit, login_tv_forget;
	private EditText login_et_account, login_et_password;
	private ImageView login_iv_acount_del, login_iv_password_del;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		WecenterManager.getInstance().pushOneActivity(LoginActivity.this);
		initView();
		handView();
	}

	/**
	 * 处理EditView操作逻辑
	 */
	private void handView() {
		// TODO Auto-generated method stub
		login_et_password.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					login_iv_password_del.setVisibility(View.VISIBLE);
				} else {
					login_iv_password_del.setVisibility(View.GONE);
				}
			}
		});
		login_et_password.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(login_et_password.getText().toString()
						.trim())) {
					login_iv_password_del.setVisibility(View.GONE);
				} else {
					login_iv_password_del.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
		login_et_account.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					login_iv_acount_del.setVisibility(View.VISIBLE);
				} else {
					login_iv_acount_del.setVisibility(View.GONE);
				}
			}
		});
		login_et_account.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(login_et_account.getText().toString()
						.trim())) {
					login_iv_acount_del.setVisibility(View.GONE);
				} else {
					login_iv_acount_del.setVisibility(View.VISIBLE);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	private void initView() {
		login_btn_login = (Button) findViewById(R.id.login_btn_login);
		login_btn_login.setOnClickListener(this);
		login_tv_forget = (TextView) findViewById(R.id.login_tv_forget);
		login_tv_forget.setOnClickListener(this);
		login_tv_visit = (TextView) findViewById(R.id.login_tv_register);
		login_tv_visit.setOnClickListener(this);
		login_et_account = (EditText) findViewById(R.id.et_account);
		login_et_password = (EditText) findViewById(R.id.et_password);
		login_iv_acount_del = (ImageView) findViewById(R.id.iv_acount_del);
		login_iv_password_del = (ImageView) findViewById(R.id.iv_password_del);
		login_iv_acount_del.setOnClickListener(this);
		login_iv_password_del.setOnClickListener(this);
	}

	/**
	 * 供其他Activity跳转
	 * 
	 * @param context
	 */
	public static void actionStart(Context context) {
		Intent intent = new Intent(context, LoginActivity.class);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.iv_acount_del:
			login_et_account.setText("");
			break;
		case R.id.iv_password_del:
			login_et_password.setText("");
			break;
		case R.id.login_btn_login:
			// 如果用户名或密码为空，提醒用户输入，不为空Login
			String username = login_et_account.getText().toString().trim();
			String password = login_et_password.getText().toString().trim();
			if (username.equals("") || password.equals("")) {
				if (username.equals("")) {
					Toast.makeText(this, "请输入用户名", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
				}
			} else {
				login(username, password);
			}
			break;
		case R.id.login_tv_register:
			RegisterActivity.actionStart(this);
			overridePendingTransition(R.anim.push_up_in, R.anim.push_up_out);
			break;
		case R.id.login_tv_forget:
			// TODO WebView加载手机版 忘记密码 页面
			Toast.makeText(this, "请在电脑上修改！", Toast.LENGTH_SHORT).show();
			break;

		}
	}

	private void login(String userName, String password) {
		// POST发送信息
		RequestParams params = new RequestParams();
		params.put("user_name", userName);
		params.put("password", password);
		String url = WecenterApi.LOGIN;
		if (WecenterApplication.isNetworkConnected(this)) {
			postData(url, params);
			Log.d(TAG, "LoginUrl" + url);
			Log.d(TAG, "user_name" + userName);
			Log.d(TAG, "password" + password);
		} else {
			Toast.makeText(this, "没有网络，请设置！", Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 登录中的post过程
	 * 
	 * @param url
	 * @param params
	 */
	private void postData(String url, RequestParams params) {
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		client = new AsyncHttpClient();
		client.setCookieStore(cookieStore);
		client.post(url, params, new AsyncHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
				showProgerssDialog();
			}

			// 处理返回信息
			@Override
			public void onSuccess(int arg0, Header[] arg1,
					byte[] responseContent) {
				// TODO Auto-generated method stub
				String reponseResult = new String(responseContent);
				parseResponseResult(reponseResult);
				Log.i(TAG, "reponseResult" + reponseResult);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1,
					byte[] responseContent, Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(LoginActivity.this, "网络不给力，请重试！",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {
				super.onFinish();
				pbDialog.dismiss();
			}
		});
	}

	/**
	 * 保存登录成功的用户数据
	 */
	private void saveLoginData(int uid, String userName, String avatarUrl,
			String password) {
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
		SPeditor.putString("userName", userName);
		SPeditor.putString("password", password);
		SPeditor.commit();
	}

	/**
	 * 解析Http返回的结果并根据结果做出相应处理
	 * 
	 * @param reponseResult
	 *            返回的结果
	 */
	private void parseResponseResult(String reponseResult) {
		Log.d("LoginResponse", reponseResult);
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
				String password = login_et_password.getText().toString().trim();
				saveLoginData(uid, userName, avatarUrl, password);
				MainActivity.actionStar(LoginActivity.this);
				finish();
				// TODO 添加动画
			} else {
				Toast.makeText(this, err, Toast.LENGTH_SHORT).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "Login" + "JSONException");
			e.printStackTrace();
		}
	}

	/**
	 * 显示ProgessDialog
	 */
	private void showProgerssDialog() {
		pbDialog = new ProgressDialog(this);
		pbDialog.setMessage(" 正在登录中");
		pbDialog.setCancelable(false);
		pbDialog.show();
		pbDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					client.cancelAllRequests(true);
					WecenterApplication.setIsLogin(false);
					pbDialog.dismiss();
				}
				return false;
			}
		});
	}

	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
		}
		return false;
	}

}
