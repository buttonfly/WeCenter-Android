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
public class WcApplication extends Application {
	// 储存登录后获取到的Uid 用户名(昵称)和 头像url；
	private static int uid = 0;
	private static int focusTopic = 1;
	
	private static String userName = "";
	private static String avatarUrl = "";
	private static String password = "";
	
	private static String errInfo = "";
	
	private static boolean isloginErr = false;//记录登录时是否网络出错
	private static boolean isLogined = false;//记录是否有储存登录的信息

	@Override
	public void onCreate() {
		// TODO 应用初始化
		super.onCreate();
		getDatd();
	}

	public static String getErrInfo() {
		return errInfo;
	}

	public static void setErrInfo(String errInfo) {
		WcApplication.errInfo = errInfo;
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
		WcApplication.setPassword(password);
		WcApplication.setUserName(userName);
		WcApplication.setUid(uid);
	}


	public static int getfocusTopic() {
		return focusTopic;
	}

	public static boolean isIsloginErr() {
		return isloginErr;
	}

	public static void setIsloginErr(boolean isloginErr) {
		WcApplication.isloginErr = isloginErr;
	}

	public static void setPassword(String password) {
		WcApplication.password = password;
	}

	public static void setUserName(String userName) {
		WcApplication.userName = userName;
	}

	public static void setUid(int uid) {
		WcApplication.uid = uid;
	}

	public static void setIsLogined(boolean isLogin) {
		WcApplication.isLogined = isLogin;
	}

	public static void setAvatarUrl(String avatarUrl) {
		WcApplication.avatarUrl = avatarUrl;
	}

	public static int getUid() {
		return uid;
	}

	public static boolean isIsLogined() {
		return isLogined;
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