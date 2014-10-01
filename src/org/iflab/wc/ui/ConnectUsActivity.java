package org.iflab.wc.ui;

import org.iflab.wc.R;

import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;

public class ConnectUsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_connect_us);
		ActionBar actionBar = getActionBar();
		actionBar.setIcon(null);
		actionBar.setTitle("联系我们");
		actionBar.setDisplayUseLogoEnabled(false);
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.show();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

}
