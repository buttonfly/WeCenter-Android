package org.iflab.wc.app;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 全局应用程序类： 用于应用初始化检测，保存和调用全局应用配置
 * 
 * @author: Timor(www.LogcatBlog.com)
 * @created: 2014-09-17
 */
public class WecenterApplication extends Application {
	private static boolean isLogin = false;
	// 储存登录后获取到的Uid 用户名(昵称)和 头像url；
	private static int uid = 0;
	private static String userName = "";
	private static String avatarUrl = "";
	private static String password = "";
	// 用于记录启动时用存储的信息异步登录是否有错误，在MainActivity会读取此数据判断并处理
	private static String loginErrInfo = "";
	private static boolean isloginErr = false;
	private static int focusTopic = 1;

	@Override
	public void onCreate() {
		// TODO 应用初始化
		super.onCreate();
		getDatd();
	}

	/**
	 * 读取储存的数据
	 */
	private void getDatd() {
		SharedPreferences sharedPreferences = getSharedPreferences("userData",
				MODE_PRIVATE);
		int uid = sharedPreferences.getInt("uid", 0);
		String userName = sharedPreferences.getString("userName", "");
		String password = sharedPreferences.getString("password", "");
		WecenterApplication.setPassword(password);
		WecenterApplication.setUserName(userName);
		WecenterApplication.setUid(uid);
	}

	public static String getLoginErrInfo() {
		return loginErrInfo;
	}

	public static void setLoginErrInfo(String loginErrInfo) {
		WecenterApplication.loginErrInfo = loginErrInfo;
	}

	public static int getfocusTopic() {
		return focusTopic;
	}

	public static boolean isIsloginErr() {
		return isloginErr;
	}

	public static void setIsloginErr(boolean isloginErr) {
		WecenterApplication.isloginErr = isloginErr;
	}

	public static void setPassword(String password) {
		WecenterApplication.password = password;
	}

	public static void setUserName(String userName) {
		WecenterApplication.userName = userName;
	}

	public static void setUid(int uid) {
		WecenterApplication.uid = uid;
	}

	public static void setIsLogin(boolean isLogin) {
		WecenterApplication.isLogin = isLogin;
	}

	public static void setAvatarUrl(String avatarUrl) {
		WecenterApplication.avatarUrl = avatarUrl;
	}

	public static int getUid() {
		return uid;
	}

	public static boolean isIsLogin() {
		return isLogin;
	}

	public static String getUserName() {
		return userName;
	}

	public static String getAvatarUrl() {
		return avatarUrl;
	}

	public static String getPassword() {
		return password;
	}

	/**
	 * 检测网络是否可用
	 * 
	 * @return
	 */
	public static boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	/**
	 * 判断WIFI网络是否可用
	 * 
	 * @return
	 */
	public static boolean isWifiConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager
					.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			if (mWiFiNetworkInfo != null) {
				return mWiFiNetworkInfo.isAvailable();
			}
		}
		return false;
	}

}