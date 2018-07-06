package com.leotech.boot;

import com.leotech.actioncontroller.MainActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(final Context context, Intent intent) {
		Log.d("wss", "receive boot completed");
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_BOOT_COMPLETED)) {
			/*
			Intent clinetIntent = new Intent(context, MainActivity.class);
			clinetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(clinetIntent);
			*/
			return;
		}
	}

}
