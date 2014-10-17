package org.iflab.wc.ui;

import org.iflab.wc.R;
import org.iflab.wc.app.ActivityCollector;
import org.iflab.wc.app.WcApplication;
import org.iflab.wc.asking.AskingFragmentActivity;
import org.iflab.wc.fragment.ExploreFragment;
import org.iflab.wc.fragment.FollowFrament;
import org.iflab.wc.fragment.HomeFragment;
import org.iflab.wc.fragment.NavigationDrawerFragment;
import org.iflab.wc.fragment.SettingFragment;
import org.iflab.wc.topic.TopicFragment;
import org.iflab.wc.userinfo.UserInfoShowActivity;

import android.support.v4.app.FragmentTransaction;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * 主界面，提供导航跳转其他模块
 * 
 * @author Timor
 * 
 */
public class MainActivity extends FragmentActivity implements
		NavigationDrawerFragment.NavigationDrawerCallbacks {

	public static NavigationDrawerFragment mNavigationDrawerFragment;
	private CharSequence mTitle;

	private long exitTime = 0;

	private HomeFragment homeFragment;
	private ExploreFragment exploreFragment;
	private FollowFrament followFragment;
	private TopicFragment topicFragment;
	private SettingFragment settingFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		ActivityCollector.addActivity(this);
		Log.i("BaseActivity", "MainActivity");
		mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager()
				.findFragmentById(R.id.navigation_drawer);
		mTitle = getTitle();
		mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
				(DrawerLayout) findViewById(R.id.drawer_layout));
		init();
	}

	private void init() {
		// TODO Auto-generated method stub
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(null);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.show();
	}

	/*
	 * DrawerFragment的子项被点击时 会调用此方法
	 * 不使用replace，因为每次都得重新创建一个fragment，fragment初始化占资源
	 * 使用add，hide，show来实现切换同时将以加载过的fragment缓存起来
	 */
	@Override
	public void onNavigationDrawerItemSelected(int position) {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (position) {
		case 0:
			if (!WcApplication.isIsLogined()) {
				LoginActivity.actionStart(MainActivity.this);
			} else {
				UserInfoShowActivity.actionStar(MainActivity.this,
						WcApplication.getUid());
			}
			break;
		case 1:
			if (!WcApplication.isIsLogined()) {
				RegisterActivity.actionStart(MainActivity.this);
			} else {
				if (homeFragment == null) {
					homeFragment = new HomeFragment();
					transaction.add(R.id.container, homeFragment).commit();
				} else {
					transaction.show(homeFragment).commit();
				}
			}
			break;
		case 2:
			if (exploreFragment == null) {
				exploreFragment = new ExploreFragment();
				transaction.add(R.id.container, exploreFragment).commit();
			} else {
				transaction.show(exploreFragment).commit();
			}
			break;
		case 3:
			if (topicFragment == null) {
				topicFragment = new TopicFragment();
				transaction.add(R.id.container, topicFragment).commit();
			} else {
				transaction.show(topicFragment).commit();
			}
			break;
		case 4:
			AskingFragmentActivity.actionStar(MainActivity.this);
			break;
		case 5:

			if (settingFragment == null) {
				settingFragment = new SettingFragment();
				transaction.add(R.id.container, settingFragment).commit();
			} else {
				transaction.show(settingFragment).commit();
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		ActivityCollector.removeActivity(this);
	}

	/**
	 * 隐藏所有的Fragment，避免有多个Fragment显示在界面上
	 * 
	 * @param transaction
	 */
	private void hideFragments(FragmentTransaction transaction) {
		// TODO Auto-generated method stub
		if (homeFragment != null) {
			transaction.hide(homeFragment);
		}
		if (exploreFragment != null) {
			transaction.hide(exploreFragment);
		}
		if (followFragment != null) {
			transaction.hide(followFragment);
		}
		if (topicFragment != null) {
			transaction.hide(topicFragment);
		}
		if (settingFragment != null) {
			transaction.hide(settingFragment);
		}
	}

	public void restoreActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setTitle(mTitle);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			ExitApp();
			// TODO 管理Activity
		}
		return false;
	}

	public void ExitApp() {

		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(this, "再按一次退出", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			// FileUtils fileUtils = new FileUtils(MainActivity.this);
			// ImageFileUtils imageFileUtils = new ImageFileUtils(
			// MainActivity.this);
			// fileUtils.deleteFile();
			// imageFileUtils.deleteFile();
			ActivityCollector.finishAll();
			finish();
		}
	}

	/**
	 * 供其他Activity跳转
	 * 
	 * @param context
	 */
	public static void actionStar(Context context) {
		Intent intent = new Intent(context, MainActivity.class);
		// intent.putExtra("param1",data1);
		context.startActivity(intent);
	}

}
