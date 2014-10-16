package org.iflab.wc.fragment;

import java.util.ArrayList;
import java.util.List;
import org.iflab.wc.R;
import org.iflab.wc.adapter.DrawerListAdapter;
import org.iflab.wc.adapter.DrawerListItem;
import org.iflab.wc.app.WecenterApi;
import org.iflab.wc.app.WecenterApplication;
import org.iflab.wc.image.SmartImageView;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class NavigationDrawerFragment extends Fragment {

	private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

	private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
	private NavigationDrawerCallbacks mCallbacks;

	private ActionBarDrawerToggle mDrawerToggle;

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerListView;
	private View mFragmentContainerView;

	private int mCurrentSelectedPosition = 0;
	private boolean mFromSavedInstanceState;
	private boolean mUserLearnedDrawer;
	private View headerView;
	private List<DrawerListItem> mData = new ArrayList<DrawerListItem>();
	private static final String TAG = "NavigationDrawerFragment";

	public NavigationDrawerFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 通过这个flag判断用户是否已经知道drawer了，只有应用第一次启动显示出Drawer
		// TODO 更改flag存放是默认
		SharedPreferences sp = PreferenceManager
				.getDefaultSharedPreferences(getActivity());
		mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
		if (savedInstanceState != null) {
			mCurrentSelectedPosition = savedInstanceState
					.getInt(STATE_SELECTED_POSITION);
			mFromSavedInstanceState = true;
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		mDrawerListView = (ListView) inflater.inflate(
				R.layout.fragment_navigation_drawer, container, false);
		headerView = inflater.inflate(R.layout.drawer_list_header, null);
		handleView();
		mDrawerListView
				.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						selectItem(position);
					}
				});
		mDrawerListView.setItemChecked(mCurrentSelectedPosition, true);
		return mDrawerListView;
	}

	/**
	 * 根据登录情况设置Drawer的mlistView的视图
	 */
	private void handleView() {
		// TODO Auto-generated method stub
		if (WecenterApplication.isIsLogined()) {
			String[] itemTitle = getResources().getStringArray(
					R.array.drawer_list_string_logined);
			int[] itemIconRes = { R.drawable.drawer_home_normal,
					R.drawable.drawer_explore_normal,
					R.drawable.drawer_follow_normal,
					R.drawable.drawer_ask_normal,
					R.drawable.drawer_setting_normal };

			for (int i = 0; i < itemTitle.length; i++) {
				DrawerListItem item = new DrawerListItem(getResources()
						.getDrawable(itemIconRes[i]), itemTitle[i]);
				mData.add(item);
			}
			setUserInfo();
			mDrawerListView.addHeaderView(headerView);
			DrawerListAdapter adapter = new DrawerListAdapter(
					this.getActivity(), mData);
			mDrawerListView.setAdapter(adapter);
			// 已登录设置DrawerList默认选中第二项（首页）
			mCurrentSelectedPosition = 1;
			selectItem(mCurrentSelectedPosition);
		} else {
			String[] itemTitle = getResources().getStringArray(
					R.array.drawer_list_string_no_login);
			int[] itemIconRes = { R.drawable.drawer_login_normal,
					R.drawable.drawer_register_normal,
					R.drawable.drawer_explore_normal, };

			for (int i = 0; i < itemTitle.length; i++) {
				DrawerListItem item = new DrawerListItem(getResources()
						.getDrawable(itemIconRes[i]), itemTitle[i]);
				mData.add(item);

			}
			DrawerListAdapter adapter = new DrawerListAdapter(
					this.getActivity(), mData);
			mDrawerListView.setAdapter(adapter);
			// 未登录设置DrawerList默认选中第三项（发现）
			mCurrentSelectedPosition = 2;
			selectItem(mCurrentSelectedPosition);
		}
	}

	/**
	 * 设置用户名和头像
	 */
	private void setUserInfo() {
		// TODO Auto-generated method stub
		String avatarUrl = WecenterApi.USER_IMAGE_BASE
				+ WecenterApplication.getAvatarUrl();
		String userName = WecenterApplication.getUserName();
		SmartImageView avatar = (SmartImageView) headerView
				.findViewById(R.id.login_icon);
		TextView name = (TextView) headerView.findViewById(R.id.name);
		if (!avatarUrl.equals("")) {
			avatar.setImageUrl(avatarUrl);
		}
		if (!userName.equals("")) {
			name.setText(userName);
		}

		Log.i(TAG, "avatarUrl" + avatarUrl);
		Log.i(TAG, "userName" + userName);
	}

	public boolean isDrawerOpen() {
		return mDrawerLayout != null
				&& mDrawerLayout.isDrawerOpen(mFragmentContainerView);
	}

	public void setUp(int fragmentId, DrawerLayout drawerLayout) {
		mFragmentContainerView = getActivity().findViewById(fragmentId);
		mDrawerLayout = drawerLayout;

		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		mDrawerToggle = new ActionBarDrawerToggle(getActivity(), mDrawerLayout,
				R.drawable.ic_drawer, R.string.navigation_drawer_open,
				R.string.navigation_drawer_close) {
			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				if (!isAdded()) {
					return;
				}
				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				if (!isAdded()) {
					return;
				}
				if (!mUserLearnedDrawer) {
					mUserLearnedDrawer = true;
					SharedPreferences sp = PreferenceManager
							.getDefaultSharedPreferences(getActivity());
					sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true)
							.apply();
				}
				getActivity().invalidateOptionsMenu(); // calls
														// onPrepareOptionsMenu()
			}
		};

		if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
			mDrawerLayout.openDrawer(mFragmentContainerView);
		}

		mDrawerLayout.post(new Runnable() {
			@Override
			public void run() {
				mDrawerToggle.syncState();
			}
		});
		mDrawerLayout.setDrawerListener(mDrawerToggle);
	}

	public void selectItem(int position) {
		mCurrentSelectedPosition = position;
		if (mDrawerListView != null) {
			mDrawerListView.setItemChecked(position, true);
		}
		if (mDrawerLayout != null) {
			mDrawerLayout.closeDrawer(mFragmentContainerView);
		}
		if (mCallbacks != null) {
			mCallbacks.onNavigationDrawerItemSelected(position);
		}
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		try {
			mCallbacks = (NavigationDrawerCallbacks) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(
					"Activity must implement NavigationDrawerCallbacks.");
		}
	}

	@Override
	public void onDetach() {
		super.onDetach();
		mCallbacks = null;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (mDrawerLayout != null && isDrawerOpen()) {
			showGlobalContextActionBar();
		}
		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void showGlobalContextActionBar() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayShowTitleEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		actionBar.setTitle(R.string.app_name);
	}

	private ActionBar getActionBar() {
		return getActivity().getActionBar();
	}

	public static interface NavigationDrawerCallbacks {
		void onNavigationDrawerItemSelected(int position);
	}

}
