package org.iflab.wc.userinfo;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.iflab.wc.R;
import org.apache.http.Header;
import org.iflab.wc.app.WcApis;
import org.iflab.wc.app.WcApplication;
import org.iflab.wc.asking.Bimp;
import org.iflab.wc.common.AsyncFileUpLoad;
import org.iflab.wc.common.AsyncImageGet;
import org.iflab.wc.common.GlobalVariables;
import org.iflab.wc.common.AsyncFileUpLoad.CallBack;
import org.iflab.wc.image.SmartImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import android.app.ActionBar;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoEditActivity extends Activity implements OnClickListener,
		DatePickerDialog.OnDateSetListener {
	private String sex;
	private String birthday, err, job_id, user_name, signature, avatarpath,
			avatar_file;// birthday为unix时间戳
	private SmartImageView iv_avatar;
	private int uid;
	private EditText et_username, et_introduction;
	private LinearLayout lv_birthday, lv_business;
	private TextView tv_sex_f, tv_sex_m, tv_sex_f_background,
			tv_sex_m_background, tv_birthday_info, tv_business_info;
	private static final String MAN = "1";
	private static final String FEMAN = "2";
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int PICK_IMAGE_ACTIVITY_REQUEST_CODE = 300;
	private Uri avatarUri;
	private SelectPicPopupWindow menuWindow;// 点击头像弹出选择拍照或者选择图库的弹出菜单
	private String newAvatarPath;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.user_information_edit);
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(null);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.show();
		uid = WcApplication.getUid();
		avatar_file = WcApis.USER_IMAGE_BASE
				+ WcApplication.getAvatarUrl();
		Log.d("UserInfoEditActivity", "avatarUrl:" + avatar_file);
		init();
	}

	private void getUserProfile() {
		// TODO Auto-generated method stub
		AsyncHttpClient getUserInfo = new AsyncHttpClient();
		RequestParams params = new RequestParams();
		params.put("uid", uid);
		getUserInfo.get(WcApis.PROFILE, params,
				new AsyncHttpResponseHandler() {

					@Override
					public void onFailure(int arg0, Header[] arg1,
							byte[] responseBody, Throwable arg3) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onSuccess(int arg0, Header[] arg1,
							byte[] responseBody) {
						// TODO Auto-generated method stub
						String responseContent = new String(responseBody);
						JSONTokener jsonParser = new JSONTokener(
								responseContent);
						try {
							Log.d("userInfo", responseContent);
							JSONObject result = (JSONObject) jsonParser
									.nextValue();
							result.getString("errno");
							err = result.getString("err");
							JSONArray rsm = new JSONArray();
							rsm = result.getJSONArray("rsm");
							JSONObject rsmcontent = (JSONObject) rsm.get(0);
							JSONTokener jsonParser2 = new JSONTokener(
									rsmcontent.toString());
							JSONObject rsmcontents = (JSONObject) jsonParser2
									.nextValue();
							user_name = rsmcontents.getString("user_name");
							sex = rsmcontents.getString("sex");
							birthday = rsmcontents.getString("birthday");
							job_id = rsmcontents.getString("job_id");
							signature = rsmcontents.getString("signature");
							updateUI();
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
	}

	protected void updateUI() {
		// TODO Auto-generated method stub
		et_username.setText(user_name);
		if (TextUtils.isEmpty(signature)) {
			et_introduction.setHint("点击设置签名!");
		} else {
			et_introduction.setText(signature);
		}

		// p
		if (sex.equals(MAN)) {
			tv_sex_m_background.setBackgroundColor(Color.parseColor("#3A7DF0"));
			tv_sex_f_background.setBackgroundColor(Color.parseColor("#E5FFED"));
		}
		if (sex.equals(FEMAN)) {
			tv_sex_f_background.setBackgroundColor(Color.parseColor("#FB929C"));
			tv_sex_m_background.setBackgroundColor(Color.parseColor("#E5FFED"));
		}
		if (!birthday.equals("null")) {
			String date = TimeStamp2Date(birthday, "yyyy-MM-dd ");
			tv_birthday_info.setText(date);
		} else {
			tv_birthday_info.setText("未设置");
		}

		if (!TextUtils.isEmpty(avatar_file)) {
			iv_avatar.setImageUrl(avatar_file);
		} else {
			iv_avatar.setImageResource(R.drawable.ic_avatar_default);
		}
	}

	public String TimeStamp2Date(String timestampString, String formats) {
		Long timestamp = Long.parseLong(timestampString) * 1000;
		String date = new java.text.SimpleDateFormat(formats)
				.format(new java.util.Date(timestamp));
		return date;
	}

	private void init() {
		// TODO Auto-generated method stub
		iv_avatar = (SmartImageView) findViewById(R.id.iv_avatar);
		et_username = (EditText) findViewById(R.id.et_uername);
		et_introduction = (EditText) findViewById(R.id.et_introduction);
		lv_birthday = (LinearLayout) findViewById(R.id.lv_birthday);
		lv_business = (LinearLayout) findViewById(R.id.lv_business);
		tv_sex_f = (TextView) findViewById(R.id.tv_sex_f);
		tv_sex_m = (TextView) findViewById(R.id.tv_sex_m);
		tv_sex_f_background = (TextView) findViewById(R.id.tv_sex_f_background);
		tv_sex_m_background = (TextView) findViewById(R.id.tv_sex_m_background);
		tv_birthday_info = (TextView) findViewById(R.id.tv_birthday_info);
		tv_business_info = (TextView) findViewById(R.id.tv_business_info);
		iv_avatar.setOnClickListener(this);
		et_username.setOnClickListener(this);
		et_introduction.setOnClickListener(this);
		lv_birthday.setOnClickListener(this);
		lv_business.setOnClickListener(this);
		tv_sex_f_background.setOnClickListener(this);
		tv_sex_m_background.setOnClickListener(this);
		tv_sex_m.setOnClickListener(this);
		tv_sex_f.setOnClickListener(this);
		tv_birthday_info.setOnClickListener(this);
		tv_business_info.setOnClickListener(this);

		if (WcApplication.isNetworkConnected(UserInfoEditActivity.this)) {
			getUserProfile();
		} else {
			Toast.makeText(this, "没有网络，请连接后操作！", Toast.LENGTH_SHORT).show();
		}

	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_avatar:
			menuWindow = new SelectPicPopupWindow(UserInfoEditActivity.this,
					itemsOnClick);
			menuWindow.showAtLocation(UserInfoEditActivity.this
					.findViewById(R.id.infoedit_layout), Gravity.BOTTOM
					| Gravity.CENTER_HORIZONTAL, 0, 0);
			break;
		case R.id.et_uername:
			user_name = et_username.getText().toString();
			break;
		case R.id.et_introduction:
			signature = et_introduction.getText().toString();
			break;
		case R.id.tv_sex_m:
			sex = "1";
			tv_sex_m_background.setBackgroundColor(Color.parseColor("#3A7DF0"));
			tv_sex_f_background.setBackgroundColor(Color.parseColor("#E5FFED"));
			break;
		case R.id.tv_sex_f:
			sex = "2";
			tv_sex_f_background.setBackgroundColor(Color.parseColor("#FB929C"));
			tv_sex_m_background.setBackgroundColor(Color.parseColor("#E5FFED"));
			break;
		case R.id.lv_birthday:
			DialogFragment newFragment = new DatePickerFragment();
			newFragment.show(getFragmentManager(), "datePicker");
			break;
		case R.id.tv_birthday_info:
			DialogFragment newFragment2 = new DatePickerFragment();
			newFragment2.show(getFragmentManager(), "datePicker");
			break;
		case R.id.lv_business:
			break;

		default:
			break;
		}
	}

	private OnClickListener itemsOnClick = new OnClickListener() {

		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_pick_photo:
				menuWindow.dismiss();
				Intent openAlbumIntent = new Intent(
						Intent.ACTION_PICK,
						android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(openAlbumIntent,
						PICK_IMAGE_ACTIVITY_REQUEST_CODE);
				break;
			case R.id.btn_take_photo:
				menuWindow.dismiss();
				Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
				String path = Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED) ? Environment
						.getExternalStorageDirectory().getPath() + "/fanfan"
						: null + "/fanfan";
				File foldFile = new File(path);
				if (!foldFile.exists()) {
					foldFile.mkdir();
				}
				avatarpath = path + File.separator + uid + "avatarImage.jpg";
				File avatarFile = new File(path + File.separator + uid
						+ "avatarImage.jpg");
				avatarUri = Uri.fromFile(avatarFile);
				intent.putExtra(MediaStore.EXTRA_OUTPUT, avatarUri);
				startActivityForResult(intent,
						CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
				break;
			case R.id.btn_cancel:
				menuWindow.dismiss();
				break;
			default:
				break;
			}

		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch (requestCode) {
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				upLoadAnim();
				try {
					newAvatarPath = Bimp.revitionImageSize(avatarpath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					Toast.makeText(UserInfoEditActivity.this, "选择照片失败，请重新选择。",
							Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
				if (newAvatarPath != null) {
					new AsyncFileUpLoad(UserInfoEditActivity.this,
							WcApis.AVATAR_UPLOAD, newAvatarPath,
							new CallBack() {
								@Override
								public void callBack(String preview,
										String err, String errno) {
									GlobalVariables.uSER_IMAGE_URL = preview;
									if (errno == "x") {
										Toast.makeText(
												UserInfoEditActivity.this,
												"网络有点不好哦，再来一次吧！",
												Toast.LENGTH_LONG).show();
									} else {
										iv_avatar.clearAnimation();
										AsyncImageGet getAvatarPreview = new AsyncImageGet(
												preview, iv_avatar);
										getAvatarPreview.execute();
									}

								}
							});
				}

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the Pick avatar
			} else {
				// Pick avatar failed, advise user
			}
			break;
		case PICK_IMAGE_ACTIVITY_REQUEST_CODE:
			if (resultCode == RESULT_OK) {
				if (data != null) {
					avatarUri = data.getData();
					String[] proj = { MediaStore.Images.Media.DATA };
					CursorLoader loader = new CursorLoader(
							UserInfoEditActivity.this, avatarUri, proj, null,
							null, null);
					Cursor cursor = loader.loadInBackground();
					int column_index = cursor
							.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
					cursor.moveToFirst();
					avatarpath = cursor.getString(column_index);

					// 获取完路径
					upLoadAnim();// 上传时的动画
					try {
						newAvatarPath = Bimp.revitionImageSize(avatarpath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						Toast.makeText(UserInfoEditActivity.this,
								"选择照片失败，请重新选择。", Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}
					if (avatarpath != null) {

					}
					new AsyncFileUpLoad(UserInfoEditActivity.this,
							WcApis.AVATAR_UPLOAD, newAvatarPath,
							new CallBack() {
								@Override
								public void callBack(String preview,
										String err, String errno) {
									// TODO Auto-generated method stub
									if (errno == "x") {
										Toast.makeText(
												UserInfoEditActivity.this,
												"网络有点不好哦，再来一次吧！",
												Toast.LENGTH_LONG).show();
									} else {
										iv_avatar.clearAnimation();
										AsyncImageGet getAvatarPreview = new AsyncImageGet(
												preview, iv_avatar);
										getAvatarPreview.execute();
									}
								}
							});
				}

			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
			break;

		default:
			break;
		}

		super.onActivityResult(requestCode, resultCode, data);
	}

	private void upLoadAnim() {
		iv_avatar.setImageResource(R.drawable.anim_rotate_image_avatar);
		RotateAnimation animation = new RotateAnimation(0, 359,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		LinearInterpolator linearInterpolator = new LinearInterpolator();
		animation.setDuration(1000);
		animation.setInterpolator(linearInterpolator);
		animation.setRepeatCount(-1);
		iv_avatar.startAnimation(animation);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		// Do something with the date chosen by the user
		tv_birthday_info.setText(Integer.toString(year) + "-"
				+ Integer.toString((month + 1)) + "-" + Integer.toString(day));
		String dateString = Integer.toString(year) + "-"
				+ Integer.toString((month + 1)) + "-" + Integer.toString(day)
				+ " " + "12:00:00";

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		try {
			Date date = sdf.parse(dateString);
			birthday = Long.toString(date.getTime() / 1000);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.edit_complete, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == R.id.edit_complete) {
			((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
					.hideSoftInputFromWindow(et_username.getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
			upLoadProfile();
			this.finish();
		}
		if (id == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	private void upLoadProfile() {
		// TODO Auto-generated method stub
		signature = et_introduction.getText().toString().trim();
		user_name = et_username.getText().toString();
		AsyncHttpClient upLoadProfile = new AsyncHttpClient();
		RequestParams UpParams = new RequestParams();
		UpParams.put("uid", uid);
		UpParams.put("user_name", user_name);
		UpParams.put("sex", sex);
		UpParams.put("job_id", job_id);
		UpParams.put("birthday", birthday);
		if (!TextUtils.isEmpty(signature)) {
			UpParams.put("signature", signature);
		}
		upLoadProfile.post(WcApis.PROFILE_SETTING, UpParams,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int arg0, Header[] arg1,
							byte[] responseBody) {
						// TODO Auto-generated method stub
						String responseContent = new String(responseBody);
						JSONTokener jsonParser = new JSONTokener(
								responseContent);
						try {
							JSONObject result = (JSONObject) jsonParser
									.nextValue();
							result.getString("errno");
							err = result.getString("err");
							adviseUesr(err);
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

					@Override
					public void onFailure(int arg0, Header[] arg1, byte[] arg2,
							Throwable arg3) {
						// TODO Auto-generated method stub
					}
				});
	}

	private void adviseUesr(String err) {
		if (!err.equals("null")) {
			Toast.makeText(this, err, Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(this, "修改成功！", Toast.LENGTH_LONG).show();
		}
	}

}
