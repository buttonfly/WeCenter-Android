package org.iflab.wc.fragment;

import java.util.ArrayList;
import java.util.List;
import org.iflab.wc.R;
import org.apache.http.Header;
import org.iflab.wc.adapter.HomeAdapter;
import org.iflab.wc.app.WecenterApi;
import org.iflab.wc.app.WecenterApplication;
import org.iflab.wc.model.HomeItemModel;
import org.iflab.wc.ui.MainActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnLastItemVisibleListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;
import com.loopj.android.http.RequestParams;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HomeFragment extends Fragment {
	public static final String TAG = "HomeFragment";
	private TextView tvHomePageLoading;
	private List<HomeItemModel> itemDataList = new ArrayList<HomeItemModel>();
	private HomeAdapter adapter;
	private int mPage = 0;
	private int totalRow;
	private PullToRefreshListView mPullRefreshListView;
	private Boolean isFirstEnter = true;
	// JSON解析的数据
	private int actionCode;

	private int userUid = 1;
	private String userName = "Null";
	private String avatarUrl = "Null";

	private String itemTitle = "Null";
	private int itemTitleUid = 1;

	private String bestAnswer = "Null";
	private int bestAnswerUid = 1;
	private int agreeCount;

	private String action = "没有动态";
	private int layoutType;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View fragmentView;
		fragmentView = inflater.inflate(R.layout.fragment_homepage, container,
				false);
		tvHomePageLoading = (TextView) fragmentView
				.findViewById(R.id.tvHomePageLoading);
		if (WecenterApplication.isNetworkConnected(getActivity())) {
			getHomePageInfo(mPage, false);// 获取数据
		} else {

			Toast.makeText(getActivity(), "没有网络耶！设置下吧", Toast.LENGTH_LONG)
					.show();
			tvHomePageLoading.setText("没有网络耶！设置下吧");
		}

		final MainActivity activity = (MainActivity) getActivity();
		adapter = new HomeAdapter(activity, R.layout.list_item_homepage,
				itemDataList);
		mPullRefreshListView = (PullToRefreshListView) fragmentView
				.findViewById(R.id.lvHomeListView);
		mPullRefreshListView.setMode(Mode.BOTH);// 上下都可以拉动
		if (isFirstEnter) {
			tvHomePageLoading.setVisibility(View.VISIBLE);
			mPullRefreshListView.setVisibility(View.GONE);
		}
		mPullRefreshListView.setAdapter(adapter);
		mPullRefreshListView
				.setOnLastItemVisibleListener(new OnLastItemVisibleListener() {

					@Override
					public void onLastItemVisible() {
						// TODO Auto-generated method stub
						mPullRefreshListView.getLoadingLayoutProxy()
								.setRefreshingLabel("正在加载");
						mPullRefreshListView.getLoadingLayoutProxy()
								.setPullLabel("上拉加载更多");
						mPullRefreshListView.getLoadingLayoutProxy()
								.setReleaseLabel("释放开始加载");
					}
				});
		mPullRefreshListView
				.setOnRefreshListener(new OnRefreshListener2<ListView>() {

					@Override
					public void onPullDownToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						mPage = 0;
						mPullRefreshListView.getLoadingLayoutProxy()
								.setRefreshingLabel("正在刷新");
						mPullRefreshListView.getLoadingLayoutProxy()
								.setPullLabel("下拉刷新");
						mPullRefreshListView.getLoadingLayoutProxy()
								.setReleaseLabel("释放开始刷新");
						getHomePageInfo(mPage, true);
						totalRow = 1;// 防止为0时无法上拉更多
					}

					@Override
					public void onPullUpToRefresh(
							PullToRefreshBase<ListView> refreshView) {
						// TODO Auto-generated method stub
						mPullRefreshListView.getLoadingLayoutProxy()
								.setRefreshingLabel("正在加载");
						mPullRefreshListView.getLoadingLayoutProxy()
								.setPullLabel("上拉加载更多");
						mPullRefreshListView.getLoadingLayoutProxy()
								.setReleaseLabel("释放开始加载");
						mPage = mPage + 1;
						getHomePageInfo(mPage, false);
					}
				});
		return fragmentView;
	}

	private void getHomePageInfo(int page, final boolean wantClearData) {
		final MainActivity activity = (MainActivity) getActivity();
		if (WecenterApplication.isNetworkConnected(activity)) {
			// 有网络后配置Http请求参数
			RequestParams params = new RequestParams();
			params.put("page", page);
			String url = WecenterApi.HOME;
			Log.d(TAG, "url:" + url);
			AsyncHttpClient client = new AsyncHttpClient();
			PersistentCookieStore mCookieStore = new PersistentCookieStore(
					activity);
			client.setCookieStore(mCookieStore);
			// 进行Http Get请求数据
			client.get(url, params, new AsyncHttpResponseHandler() {

				@Override
				public void onFailure(int arg0, Header[] arg1, byte[] arg2,
						Throwable arg3) {
					// TODO Auto-generated method stub
					// 请求失败后提示用户
					Toast.makeText((MainActivity) getActivity(),
							" 网络有点不好哦，请重试！", Toast.LENGTH_LONG).show();
					mPullRefreshListView.onRefreshComplete();
				}

				@Override
				public void onSuccess(int arg0, Header[] arg1,
						byte[] responseContent) {
					// TODO Auto-generated method stub
					// 如果为下拉刷新则需要清除旧数据
					if (wantClearData) {
						itemDataList.clear();
						adapter.notifyDataSetChanged();
					}
					// 请求成功后解析数据
					layoutType = HomeItemModel.LAYOUT_TYPE_SIMPLE;
					String string = new String(responseContent);
					if (isFirstEnter == true) {
						isFirstEnter = false;
						tvHomePageLoading.setVisibility(View.GONE);
						mPullRefreshListView.setVisibility(View.VISIBLE);
					}
					try {
						// 进行JSON解析数据
						JSONObject all = new JSONObject(string);
						JSONObject rsm = all.getJSONObject("rsm");
						totalRow = (rsm.getInt("total_rows"));
						// 如果请求成功totalRow为0时说明无更多数据了
						if (totalRow == 0) {
							// 已经加载全部的数据

							if (mPage == 0) {
								// TODO 没有数据跳转到发现
								Toast.makeText(activity, "没有东东哦，快去关注别人吧！",
										Toast.LENGTH_LONG).show();
							} else {
								mPage = mPage - 1;
								Toast.makeText(activity, "亲，今天就这么多了！",
										Toast.LENGTH_LONG).show();
							}
							mPullRefreshListView.onRefreshComplete();
						}
						JSONArray rows = rsm.getJSONArray("rows");
						for (int i = 0; i < rows.length(); i++) {
							JSONObject rowsObject = rows.getJSONObject(i);
							// actionCode不同则JSON中数组的对象不同分情况解析
							actionCode = rowsObject.getInt("associate_action");
							// 获取userInfo对象
							JSONObject userInfoObject = rowsObject
									.getJSONObject("user_info");
							userUid = userInfoObject.getInt("uid");
							userName = userInfoObject.getString("user_name");
							avatarUrl = WecenterApi.USER_IMAGE_BASE
									+ userInfoObject.getString("avatar_file");
							// 根据actionCode不同，不同分情况解析剩下的JSON数据
							switch (actionCode) {
							case 101:
								JSONObject questionInfoObject101 = rowsObject
										.getJSONObject("question_info");
								itemTitle = questionInfoObject101.getString(
										"question_content").trim();
								itemTitleUid = questionInfoObject101
										.getInt("question_id");
								action = "关注该问题";
								layoutType = HomeItemModel.LAYOUT_TYPE_SIMPLE;
								break;
							case 105:
								JSONObject questionInfoObject105 = rowsObject
										.getJSONObject("question_info");
								itemTitle = questionInfoObject105.getString(
										"question_content").trim();
								itemTitleUid = questionInfoObject105
										.getInt("question_id");
								action = "发布该文章";
								layoutType = HomeItemModel.LAYOUT_TYPE_SIMPLE;
								break;
							case 501:
								JSONObject articleInfoObject501 = rowsObject
										.getJSONObject("article_info");
								itemTitleUid = articleInfoObject501
										.getInt("id");
								itemTitle = articleInfoObject501.getString(
										"title").trim();
								action = "赞同该文章";
								layoutType = HomeItemModel.LAYOUT_TYPE_SIMPLE;
								break;
							case 502:
								JSONObject articleInfoObject502 = rowsObject
										.getJSONObject("article_info");
								itemTitleUid = articleInfoObject502
										.getInt("id");
								itemTitle = articleInfoObject502.getString(
										"title").trim();
								action = "回答该问题";
								layoutType = HomeItemModel.LAYOUT_TYPE_SIMPLE;
								break;
							case 201:
								JSONObject answerInfoObject201 = rowsObject
										.getJSONObject("answer_info");
								bestAnswerUid = answerInfoObject201
										.getInt("answer_id");
								bestAnswer = answerInfoObject201.getString(
										"answer_content").trim();
								agreeCount = answerInfoObject201
										.getInt("agree_count");
								JSONObject questionInfoObject201 = rowsObject
										.getJSONObject("question_info");
								itemTitle = questionInfoObject201.getString(
										"question_content").trim();
								itemTitleUid = questionInfoObject201
										.getInt("question_id");
								action = "回答该问题";
								layoutType = HomeItemModel.LAYOUT_TYPE_COMPLEX;
								break;
							case 204:
								JSONObject answerInfoObject204 = rowsObject
										.getJSONObject("answer_info");
								bestAnswerUid = answerInfoObject204
										.getInt("answer_id");
								bestAnswer = answerInfoObject204.getString(
										"answer_content").trim();
								agreeCount = answerInfoObject204
										.getInt("agree_count");
								JSONObject questionInfoObject204 = rowsObject
										.getJSONObject("question_info");
								itemTitle = questionInfoObject204.getString(
										"question_content").trim();
								itemTitleUid = questionInfoObject204
										.getInt("question_id");
								action = "赞同该回答";
								layoutType = HomeItemModel.LAYOUT_TYPE_COMPLEX;
								break;
							default:
								break;
							}
							// 加载到ListItemModel
							HomeItemModel item = new HomeItemModel(layoutType,
									avatarUrl, userName, userUid, action,
									itemTitle, itemTitleUid, bestAnswer,
									bestAnswerUid, agreeCount);
							itemDataList.add(item);
							// 关闭RefreshComplete
							mPullRefreshListView.onRefreshComplete();
						}
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						Log.i(TAG, "Json解析异常");
						mPullRefreshListView.onRefreshComplete();
					}

				}
			});
		} else {
			Toast.makeText((MainActivity) getActivity(), "未连接网络",
					Toast.LENGTH_LONG).show();
			mPullRefreshListView.onRefreshComplete();
		}
	}
}
