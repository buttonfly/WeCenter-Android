package org.iflab.wc.app;

import java.util.Stack;

import android.app.Activity;
import android.util.Log;

/**
 * 应用程序Activity管理类：用于Activity管理和应用程序退出
 * 
 * @author Timor
 * 
 */
public class WecenterManager {

	private static WecenterManager instance;
	private Stack<Activity> activityStack;

	// activity栈

	private WecenterManager() {
	}

	// 单例模式
	public static WecenterManager getInstance() {
		if (instance == null) {
			instance = new WecenterManager();
		}
		return instance;
	}

	/**
	 * 把一个activity压入栈中
	 * 
	 * @param actvity
	 */
	public void pushOneActivity(Activity actvity) {
		if (activityStack == null) {
			activityStack = new Stack<Activity>();
		}
		activityStack.add(actvity);
		Log.d("WecenterManager ", "size = " + activityStack.size());
	}

	/**
	 * 获取栈顶的activity，先进后出原则
	 * 
	 * @return
	 */
	public Activity getLastActivity() {
		return activityStack.lastElement();
	}

	/**
	 * 移除一个activity
	 * 
	 * @param activity
	 */
	public void popOneActivity(Activity activity) {
		if (activityStack != null && activityStack.size() > 0) {
			if (activity != null) {
				activity.finish();
				activityStack.remove(activity);
				activity = null;
			}
		}
	}

	/**
	 * 退出所有activity
	 */
	public void finishAllActivity() {
		if (activityStack != null) {
			while (activityStack.size() > 0) {
				Activity activity = getLastActivity();
				if (activity == null) {
					break;
				}
				popOneActivity(activity);
			}
			System.exit(0);
		}

	}
}
