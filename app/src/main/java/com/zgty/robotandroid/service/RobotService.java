package com.zgty.robotandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;

import com.zgty.robotandroid.util.ToastUtil;

/**
 * 机器人硬件操作的服务
 */
public class RobotService extends Service {
    public ServiceBinder mBinder = new ServiceBinder();

    /* 数据通信的桥梁 */
  /* 重写Binder的onBind函数，返回派生类 */
    public RobotService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    /* 第一种模式通信：Binder */
    public class ServiceBinder extends Binder {
        public void startChange(String id_choose) {
            //通过传递的参数进行各种动作
            ToastUtil.ShowShort(RobotService.this, "OK" + "id=" + id_choose);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
