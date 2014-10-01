package org.iflab.wc.detail.question;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.client.CookieStore;
import org.iflab.wc.app.WecenterApi;
import org.iflab.wc.common.TextShow;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.iflab.wc.R;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.PersistentCookieStore;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class QuestionDetailActivity extends Activity {
	private static final String TAG = "QuestionDetailActivity";
	private CookieStore myCookieStore;
	private ListView comlist;
	private List<AnswerItem> comlists;
	private CommentAdapter adapter;
	private TextView questiontitle, questiondetil, focus, answercount;
	private LinearLayout addanswer;
	private AsyncHttpClient client;
	private TextShow textShow;
	private String question_content;
	private String question_id;
	private Button focusques;
	private int focustag = 1;
	private LinearLayout layout;
	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.question_detail);
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(null);
		actionBar.setTitle("问题详细");
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.show();
		client = new AsyncHttpClient();
		layout = (LinearLayout) findViewById(R.id.linear);
		myCookieStore = new PersistentCookieStore(this);
		client.setCookieStore(myCookieStore);
		focusques = (Button) findViewById(R.id.focusques);
		progressBar = (ProgressBar) findViewById(R.id.pb_change_follow);
		focusques.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				focusques.setText("");
				progressBar.setVisibility(View.VISIBLE);
				Focusorno();
			}
		});
		comlist = (ListView) findViewById(R.id.comlist);
		comlists = new ArrayList<AnswerItem>();
		Intent intent = getIntent();
		question_id = intent.getStringExtra("questionid");
		Log.i(TAG, "questionid:" + question_id);
		comlist.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("answerid", comlists.get(arg2).getAnswer_id());
				intent.setClass(QuestionDetailActivity.this,
						AnswerActivity.class);
				startActivity(intent);
			}
		});
		addanswer = (LinearLayout) findViewById(R.id.addanswer);
		questiontitle = (TextView) findViewById(R.id.question_contents);
		questiondetil = (TextView) findViewById(R.id.question_detail);
		focus = (TextView) findViewById(R.id.focus_count);
		answercount = (TextView) findViewById(R.id.answer_count);
		addanswer.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.putExtra("questionid", question_id);
				intent.setClass(QuestionDetailActivity.this,
						WriteAnswerActivity.class);
				startActivityForResult(intent, 1);

			}
		});
		GetQuestion(question_id);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			flash();
		}

	}

	private void GetQuestion(String question_id) {
		String url = WecenterApi.QUESTION_DETAIL + question_id;
		Log.d(TAG, url);
		client.get(url, new AsyncHttpResponseHandler() {
			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(QuestionDetailActivity.this, "网络连接失败！",
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] responseResult) {
				// TODO Auto-generated method stub
				String result = new String(responseResult);
				JSONObject rsm = null;
				int answer_count = 0;
				int errno = 0;
				JSONObject jsonObject = null;
				try {
					jsonObject = new JSONObject(result);
					errno = jsonObject.getInt("errno");
				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if (errno == 1) {
					try {

						rsm = jsonObject.getJSONObject("rsm");
						JSONObject question_info = rsm
								.getJSONObject("question_info");
						answer_count = rsm.getInt("answer_count");

						answercount.setText(Integer.toString(answer_count));
						final String question_topics = rsm
								.getString("question_topics");

						question_content = question_info
								.getString("question_content");
						focustag = question_info.getInt("has_focus");
						setFollow();
						layout.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								// TODO Auto-generated method stub
								Intent intent = new Intent();
								intent.putExtra("topic", question_topics);
								intent.setClass(QuestionDetailActivity.this,
										TopicAboutActivity.class);
								startActivity(intent);
							}
						});

						String question_detail = question_info
								.getString("question_detail");
						String focus_count = question_info
								.getString("focus_count");
						questiontitle.setText(question_content);
						DisplayMetrics dm = new DisplayMetrics();
						getWindowManager().getDefaultDisplay().getMetrics(dm);
						float screenW = dm.widthPixels;
						textShow = new TextShow(JSONTokener(question_detail),
								questiondetil, QuestionDetailActivity.this,
								screenW);
						textShow.execute();
						focus.setText(focus_count);
						if (answer_count != 0) {
							JSONArray answers = rsm.getJSONArray("answers");
							for (int i = 0; i < answers.length(); i++) {
								JSONObject answer = answers.getJSONObject(i);
								AnswerItem answerItem = new AnswerItem();
								answerItem.setAnswer_id(answer
										.getString("answer_id"));
								answerItem.setAnswer_content(JSONTokener(answer
										.getString("answer_content")));
								answerItem.setAgree_count(answer
										.getString("agree_count"));
								answerItem.setUid(answer.getString("uid"));
								answerItem.setName(answer
										.getString("user_name"));

								comlists.add(answerItem);
							}

						}

					} catch (JSONException e) {
						// TODO Auto-generated catch block
						// e.printStackTrace();
						try {
							JSONObject answers = rsm.getJSONObject("answers");
							for (int i = 0; i < Integer.valueOf(answer_count); i++) {
								JSONObject answer = answers
										.getJSONObject(String.valueOf(i + 1));
								AnswerItem answerItem = new AnswerItem();
								answerItem.setAnswer_id(answer
										.getString("answer_id"));
								answerItem.setAnswer_content(JSONTokener(answer
										.getString("answer_content")));
								answerItem.setAgree_count(answer
										.getString("agree_count"));
								answerItem.setUid(answer.getString("uid"));
								answerItem.setName(answer
										.getString("user_name"));
								// answerItem.setAvatar_file(answer.getString("avatar_file"));
								comlists.add(answerItem);
							}
						} catch (Exception e2) {
							// TODO: handle exception
							e2.printStackTrace();
						}

					}

					adapter = new CommentAdapter(comlists,
							QuestionDetailActivity.this);
					comlist.setAdapter(adapter);
				} else {
					try {
						String err = jsonObject.getString("err");
						Toast.makeText(QuestionDetailActivity.this, err,
								Toast.LENGTH_LONG).show();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

			}

		});
	}

	private void Focusorno() {
		String url = WecenterApi.FOLLOW_QUESTION + question_id;
		client.get(url, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int arg0, Header[] arg1, byte[] arg2,
					Throwable arg3) {
				// TODO Auto-generated method stub
				Toast.makeText(QuestionDetailActivity.this, "网络连接失败！",
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onSuccess(int arg0, Header[] arg1, byte[] arg2) {
				// TODO Auto-generated method stub
				String info = new String(arg2);

				try {
					JSONObject jsonObject = new JSONObject(info);
					int errno = jsonObject.getInt("errno");
					if (errno == 1) {
						JSONObject rsm = jsonObject.getJSONObject("rsm");
						String type = rsm.getString("type");
						progressBar.setVisibility(View.GONE);
						if (type.equals("add")) {
							focustag = 1;
						} else {
							focustag = 0;
						}
						setFollow();
					} else {
						String err = jsonObject.getString("err");
						Toast.makeText(QuestionDetailActivity.this, err,
								Toast.LENGTH_LONG).show();
					}

				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		});
	}

	public static String JSONTokener(String in) {
		// consume an optional byte order mark (BOM) if it exists
		if (in != null && in.startsWith("\ufeff")) {
			in = in.substring(1);
		}
		return in;
	}

	private void setFollow() {
		if (focustag == 1) {
			focusques.setBackgroundResource(R.drawable.btn_silver_normal);
			focusques.setText("取消关注");
			focusques.setTextColor(QuestionDetailActivity.this.getResources()
					.getColor(R.color.text_color_gray));
		} else {
			focusques.setBackgroundResource(R.drawable.btn_green_normal);
			focusques.setText("关注");
			focusques.setTextColor(QuestionDetailActivity.this.getResources()
					.getColor(R.color.text_color_white));
		}
	}

	private void flash() {
		comlists.clear();
		GetQuestion(question_id);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
