package org.iflab.wc.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * 所有活动的基类
 * 
 */
public class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 每创建一个活动，就加入到活动管理器中
		ActivityCollector.addActivity(this);
		Log.i("BaseActivity", getClass().getSimpleName());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 每销毁一个活动，就从活动管理器中移除
		ActivityCollector.removeActivity(this);
	}
}
