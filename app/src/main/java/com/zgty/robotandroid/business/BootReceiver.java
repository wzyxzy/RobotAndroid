package com.zgty.robotandroid.business;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.zgty.robotandroid.activity.MainActivity;

public class BootReceiver extends BroadcastReceiver {

    private static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        if (intent.getAction().equals(ACTION)) {
            Intent newIntent = new Intent(context, MainActivity.class);
//            //后边的XXX.class就是要启动的服务
//            Intent service = new Intent(context,AutoStartService.class);
//            context.startService(service);
//
//            // 启动应用，参数为需要自动启动的应用的包名，只是启动app的activity的包名
//            Intent newIntent = context.getPackageManager()
//                    .getLaunchIntentForPackage("com.example.autostart");
//            context.startActivity(newIntent);
            newIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(newIntent);
        }

    }
}
