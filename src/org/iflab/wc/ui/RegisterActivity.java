package org.iflab.wc.ui;

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
import android.widget.Toast;
import android.view.View.OnFocusChangeListener;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;

import org.apache.http.Header;
import org.iflab.wc.R;
import org.iflab.wc.app.BaseActivity;
import org.iflab.wc.app.WcApis;
import org.iflab.wc.app.WcApplication;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 用户注册 注册成功则储存用户信息并跳转到MainActivity 注册失败则提示用户
 * 
 * @author: Timor(www.LogcatBlog.com)
 * @created: 2014-09-15
 */
public class RegisterActivity extends BaseActivity implements OnClickListener {
	private static String TAG = "RegisterActivity";
	private Button register_btn_register;
	private ProgressDialog pbDialog;
	private AsyncHttpClient client;
	private EditText login_et_email, login_et_account, login_et_password;
	private ImageView login_iv_email_del, login_iv_acount_del,
			login_iv_password_del;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		initView();
	}

	private void initView() {
		register_btn_register = (Button) findViewById(R.id.register_btn_register);
		register_btn_register.setOnClickListener(new clickListener());
		// TODO 控件组合做恰当封装
		login_et_account = (EditText) findViewById(R.id.et_account);
		login_et_email = (EditText) findViewById(R.id.et_email);
		login_et_password = (EditText) findViewById(R.id.et_password);
		login_iv_email_del = (ImageView) findViewById(R.id.iv_email_del);
		login_iv_acount_del = (ImageView) findViewById(R.id.iv_acount_del);
		login_iv_password_del = (ImageView) findViewById(R.id.iv_password_del);
		login_iv_acount_del.setOnClickListener(this);
		login_iv_password_del.setOnClickListener(this);
		login_iv_email_del.setOnClickListener(this);
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
		login_et_email.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (TextUtils.isEmpty(login_et_email.getText().toString()
						.trim())) {
					login_iv_email_del.setVisibility(View.GONE);
				} else {
					login_iv_email_del.setVisibility(View.VISIBLE);
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
		login_et_email.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				// TODO Auto-generated method stub
				if (hasFocus) {
					login_iv_email_del.setVisibility(View.VISIBLE);
				} else {
					login_iv_email_del.setVisibility(View.GONE);
				}
			}
		});
	}

	private class clickListener implements View.OnClickListener {
		public void onClick(View v) {
			String email = login_et_email.getText().toString().trim();
			String userName = login_et_account.getText().toString().trim();
			String password = login_et_password.getText().toString().trim();
			if (TextUtils.isEmpty(email)) {
				// TODO 邮箱格式验证
				Toast.makeText(RegisterActivity.this, "请输入邮箱！",
						Toast.LENGTH_SHORT).show();
			} else if (!email
					.matches("^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$")) {
				Toast.makeText(RegisterActivity.this, "邮箱格式不正确",
						Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(userName)) {
				Toast.makeText(RegisterActivity.this, "请输入用户名！",
						Toast.LENGTH_SHORT).show();
			} else if (TextUtils.isEmpty(password)) {
				Toast.makeText(RegisterActivity.this, "请输入密码！",
						Toast.LENGTH_SHORT).show();
			} else {
				register(email, userName, password);

			}
		}
	}

	/**
	 * 发送注册数据给服务端并对返回结果解析处理
	 * 
	 * @param email
	 *            邮箱
	 * @param userName
	 *            用户名
	 * @param password
	 *            密码
	 */
	private void register(String email, String userName, String password) {
		RequestParams params = new RequestParams();
		PersistentCookieStore cookieStore = new PersistentCookieStore(this);
		client = new AsyncHttpClient();
		client.setCookieStore(cookieStore);
		params.put("email", email);
		params.put("user_name", userName);
		params.put("password", password);
		String url = WcApis.REGISTER;
		if (WcApplication.isNetworkConnected(this)) {
			postData(url, params);
		} else {
			Toast.makeText(this, "没有网络请设置！", Toast.LENGTH_SHORT).show();
		}
	}

	private void postData(String url, RequestParams params) {
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onStart() {
				// TODO Auto-generated method stub
				super.onStart();
				showProgerssDialog();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1,
					byte[] responseContent) {
				// TODO Auto-generated method stub
				String reponseResult = new String(responseContent);
				parseResponseResult(reponseResult);
			}

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(RegisterActivity.this, "网络不给力，请重试",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onFinish() {
				// TODO Auto-generated method stub
				super.onFinish();
				pbDialog.dismiss();
			}
		});
	}

	/**
	 * 解析Http返回的结果并根据结果做出相应处理
	 * 
	 * @param reponseResult
	 *            返回的结果
	 */
	private void parseResponseResult(String reponseResult) {
		try {
			JSONObject jsonObject = new JSONObject(reponseResult);
			int errno = jsonObject.getInt("errno");
			String err = jsonObject.getString("err");
			if (errno == 1) {
				JSONObject rsm = jsonObject.getJSONObject("rsm");
				int uid = rsm.getInt("uid");
				MainActivity.actionStar(RegisterActivity.this);
				String userName = login_et_account.getText().toString().trim();
				String password = login_et_password.getText().toString().trim();
				saveData(userName, password, uid);
				Toast.makeText(RegisterActivity.this, "注册成功!",
						Toast.LENGTH_SHORT).show();
				MainActivity.actionStar(this);
				finish();
				// 添加动画效果
			} else {
				Toast.makeText(RegisterActivity.this, err, Toast.LENGTH_SHORT)
						.show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			Log.d(TAG, "JSONException");
			e.printStackTrace();
		}
	}

	/**
	 * 将注册成功的数据处理至SharedPreferences：userData
	 */
	private void saveData(String userName, String password, int uid) {
		// TODO Auto-generated method stub
		// TODO 加密数据存放
		WcApplication.setUid(uid);
		WcApplication.setUserName(userName);
		WcApplication.setIsLogined(true);
		SharedPreferences sharedPreferences = getSharedPreferences("userData",
				MODE_PRIVATE);
		SharedPreferences.Editor SPeditor = sharedPreferences.edit();
		SPeditor.putString("userName", userName);
		SPeditor.putString("password", password);
		SPeditor.putInt("uid", uid);
		SPeditor.commit();
	}

	/**
	 * 显示ProgessDialog
	 */
	private void showProgerssDialog() {
		pbDialog = new ProgressDialog(this);
		pbDialog.setMessage("请稍候。。。");
		pbDialog.setCancelable(false);
		pbDialog.show();
		pbDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {

			@Override
			public boolean onKey(DialogInterface dialog, int keyCode,
					KeyEvent event) {
				// TODO Auto-generated method stub
				if (keyCode == KeyEvent.KEYCODE_BACK) {
					client.cancelAllRequests(true);
					pbDialog.dismiss();
				}
				return false;
			}
		});
	}

	/**
	 * 供其他Activity跳转
	 * 
	 * @param context
	 */
	public static void actionStart(Context context) {
		Intent intent = new Intent(context, RegisterActivity.class);
		context.startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			finish();
			overridePendingTransition(R.anim.push_down_in, R.anim.push_down_out);
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_acount_del:
			login_et_account.setText("");
			break;
		case R.id.iv_password_del:
			login_et_password.setText("");
			break;
		case R.id.iv_email_del:
			login_et_email.setText("");
			break;

		default:
			break;
		}
	}
}
