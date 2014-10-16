package org.iflab.wc.receiver;

import org.iflab.wc.app.ActivityCollector;
import org.iflab.wc.ui.LoginActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Receiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i("localReceiver", "BroadcastlocalReceive");
		// String err = intent.getStringExtra("data");
		Toast.makeText(context, "您的用户名或者密码已更改，请重新登录！", Toast.LENGTH_SHORT)
				.show();
		ActivityCollector.finishAll();
		Intent mIntent = new Intent(context, LoginActivity.class);
		mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(mIntent);
	}

}
