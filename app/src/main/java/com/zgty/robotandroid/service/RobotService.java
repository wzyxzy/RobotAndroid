package com.zgty.robotandroid.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.IntDef;
import android.util.Log;

import com.leo.api.LeoRobot;
import com.zgty.robotandroid.util.LeoSpeech;
import com.zgty.robotandroid.util.SpeechTools;
import com.zgty.robotandroid.util.ToastUtil;

import static com.zgty.robotandroid.common.Constant.LAST_STATION;
import static com.zgty.robotandroid.common.Constant.NOW_STATION;
import static com.zgty.robotandroid.common.Constant.ROBOT_DIR;

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
            Log.d("choose_num", "OK" + "id=" + id_choose);
            String[] id = id_choose.split("no");
            int i = Integer.valueOf(id[1]) - Integer.valueOf(NOW_STATION);
            int j = Integer.valueOf(id[1]) - Integer.valueOf(LAST_STATION);
            if (j > 0) {
                LeoSpeech.setEnglishMode(false);
                LeoSpeech.speak("没有" + id[1] + "车厢", null);
                return;
            }
            if ((i > 0 && ROBOT_DIR.equalsIgnoreCase("向前") || (i <= 0 && ROBOT_DIR.equalsIgnoreCase("向后")))) {
                LeoRobot.doAction("left");
            } else {
                LeoRobot.doAction("right");
            }
//            SpeechTools.speakAndRestartRecognize(id[1] + "车厢，往这边走");
            LeoSpeech.setEnglishMode(false);
            LeoSpeech.speak(id[1] + "车厢，往这边走", null);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
