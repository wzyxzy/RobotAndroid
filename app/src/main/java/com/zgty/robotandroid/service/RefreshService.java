package com.zgty.robotandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;

import com.zgty.robotandroid.common.Constant;
import com.zgty.robotandroid.presenter.TrainTimePresenterImpl;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zy on 2017/10/25.
 * 定时刷新页面服务
 */

public class RefreshService extends Service {

    private Timer timer_list;
    private Timer timer_info;
    private Timer timer_broad;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
//        intent.putExtra(Constant.SERVICE_INTENT_INFO, Constant.SERVICE_INFO);
//        intent.putExtra(Constant.SERVICE_INTENT_LIST, Constant.SERVICE_LIST);
        int info_service = intent.getIntExtra(Constant.SERVICE_INTENT_INFO, -1);
        int list_service = intent.getIntExtra(Constant.SERVICE_INTENT_LIST, -1);
        int broad_service = intent.getIntExtra(Constant.SERVICE_INTENT_BROAD, -1);
        if (info_service == Constant.SERVICE_INFO) {
            timer_info = new Timer();
            timer_info.schedule(new TimerTask() {
                @Override
                public void run() {

                    Intent intent1 = new Intent();
                    intent1.setAction(Constant.BROADCASTACTIONINFO);
                    sendBroadcast(intent1);
                }
            }, 0, Constant.BROADCASTINFOTIME);
        }
        if (list_service == Constant.SERVICE_LIST) {
            timer_list = new Timer();
            timer_list.schedule(new TimerTask() {
                @Override
                public void run() {

                    Intent intent1 = new Intent();
                    intent1.setAction(Constant.BROADCASTACTIONLIST);
                    sendBroadcast(intent1);
                }
            }, 0, Constant.BROADCASTLISTTIME);
        }
        if (broad_service == Constant.SERVICE_BROAD) {
            timer_broad = new Timer();
            timer_broad.schedule(new TimerTask() {
                @Override
                public void run() {

                    Intent intent1 = new Intent();
                    intent1.setAction(Constant.BROADCASTACTIONBROADCAST);
                    sendBroadcast(intent1);
                }
            }, 0, Constant.BROADCASTBROADCAST);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (timer_list != null) {
            timer_list.cancel();
        }
        if (timer_info != null) {
            timer_info.cancel();
        }
    }
}
