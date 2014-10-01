package org.iflab.wc.topic;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import org.iflab.wc.R;

public class TopicDetailFragmentActivity extends FragmentActivity {
	private PagerSlidingTabStrip tabs;
	private DisplayMetrics dm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topic_detail);
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(null);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.show();
		Intent intent = getIntent();
		String topic_id = intent.getStringExtra("topic_id");
		int isFocus = intent.getIntExtra("isFocus", 10);
		dm = getResources().getDisplayMetrics();
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		tabs = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		pager.setAdapter(new TopicDetailAdapter(getSupportFragmentManager(),
				topic_id, isFocus));
		tabs.setViewPager(pager);
		setTabsValue();
	}

	private void setTabsValue() {
		tabs.setShouldExpand(true);
		tabs.setDividerColor(Color.TRANSPARENT);
		tabs.setUnderlineHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 1, dm));
		tabs.setIndicatorHeight((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 4, dm));
		tabs.setTextSize((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_SP, 16, dm));
		tabs.setIndicatorColor(Color.parseColor("#f96b03"));
		tabs.setSelectedTextColor(Color.parseColor("#f96b03"));
		tabs.setTabBackground(0);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}
}
