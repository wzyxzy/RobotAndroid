package com.zgty.robotandroid.activity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.github.promeg.pinyinhelper.Pinyin;
import com.leo.api.abstracts.ISpeakListener;
import com.zgty.robotandroid.beans.BroadCast;
import com.zgty.robotandroid.presenter.BroadCastPresenter;
import com.zgty.robotandroid.presenter.BroadCastPresenterImpl;
import com.zgty.robotandroid.util.CompareAndSpeech;
import com.zgty.robotandroid.util.FucUtil;
import com.zgty.robotandroid.util.LeoSpeech;
import com.leo.api.abstracts.IViewActionListener;
import com.leo.api.abstracts.IViewUpdater;
import com.zgty.robotandroid.R;
import com.zgty.robotandroid.adapters.TrainTimeAdapter;
import com.zgty.robotandroid.beans.RobotEntity;
import com.zgty.robotandroid.beans.RobotManageEntity;
import com.zgty.robotandroid.beans.TrainInfoEntity;
import com.zgty.robotandroid.business.ResultProcessor;
import com.zgty.robotandroid.common.Constant;
import com.zgty.robotandroid.presenter.TrainInfoPresenter;
import com.zgty.robotandroid.presenter.TrainInfoPresenterImpl;
import com.zgty.robotandroid.presenter.TrainTimePresenter;
import com.zgty.robotandroid.presenter.TrainTimePresenterImpl;
import com.zgty.robotandroid.service.RefreshService;
import com.zgty.robotandroid.service.RobotService;
import com.zgty.robotandroid.util.SpeechTools;
import com.zgty.robotandroid.util.StringUtils;
import com.zgty.robotandroid.util.TimeUtils;
import com.zgty.robotandroid.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.zgty.robotandroid.common.Constant.END_STATION;
import static com.zgty.robotandroid.common.Constant.LAST_STATION;
import static com.zgty.robotandroid.common.Constant.NOW_STATION;
import static com.zgty.robotandroid.common.Constant.RED_DIRECTION;
import static com.zgty.robotandroid.common.Constant.ROBOT_DIR;
import static com.zgty.robotandroid.common.Constant.ROBOT_MAC;
import static com.zgty.robotandroid.common.Constant.ROBOT_PLATFORM;
import static com.zgty.robotandroid.common.Constant.START_STATION;
import static com.zgty.robotandroid.common.Constant.STATION_NAME;
import static com.zgty.robotandroid.common.Constant.TRAIN_NUM;
import static com.zgty.robotandroid.common.RobotApplication.canSpeech;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TrainInfoView, TrainTimeView, BroadCastInfo, IViewUpdater {

    private static final String TAG = MainActivity.class.getSimpleName();
    private TextView station_welcome_text;//xx站欢迎您，如果需要改，可以使用string，也可以写个setText
    private TextView train_num_id;//列车车次
    private TextView train_station_from;//始发站
    private TextView train_station_to;//终点站
    private TextView pre_station_text;//1--8车厢向前
    private TextView after_station_text;//9--16车厢向后
    private TextView station_state;//晚点等信息提示
    private TextView train_station_num;//当前的车厢号
    private TextView train_from_time;//列车到达时间
    private TextView train_reminder_time;//列车停留时间
    private TextView choose_train_num;//选择车厢号按钮
    private ListView train_list_all;//右边的车次列表
    private TextView words_anim;//机器人对话动画表示方式
    private TextView people_speak;//旅客说话动画表示方式
    private TextView speech_state;//机器人对话状态

    private TrainInfoPresenter trainInfoPresenter;
    private TrainTimePresenter trainTimePresenter;
    private BroadCastPresenter broadCastPresenter;

    private String curTrainNoAdd;
    private String direction2;
    private TrainTimeAdapter trainTimeAdapter;
    private List<RobotEntity> trainTimeEntities;

    private RefreshListBroadCast listBroadCast;
    private Intent intentRefreshService;
    private Intent intentRobotService;
    private RobotService.ServiceBinder mBinderService;
    private ServiceConnection connection;
    //    private boolean[] has_speech = {false, false, false, false};
    private int choose_num = 0;
    private CompareAndSpeech compareAndSpeech;
    private List<BroadCast> broadCasts;
//    private int[] count = {0, 0, 0, 0};
//    private String[] chn;
//    private String[] eng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initScreen();
        initView();
        initData();
        initRobot();

    }

    private void initRobot() {
        //设置语音引擎状态更新接口
//        LeoSpeech.setViewUpdater(this);
        ResultProcessor mResultProcessor = new ResultProcessor(this);
        Log.v("wss", "init................");
        LeoSpeech.init(this, mResultProcessor);
        LeoSpeech.setViewUpdater(this);
        LeoSpeech.makelocalGrammar();

        mResultProcessor.setOnVoiceListener(new ResultProcessor.OnVoiceListener() {

            @Override
            public void onWords(String words) {
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(int nums) {
                bindRobotService(Constant.CHOOSE_USER_NUM_ID);
                setButtonDisable();
            }

            @Override
            public void onFail() {

            }
        });
    }

    private void initScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏
        }
        if (Build.VERSION.SDK_INT >= 16 && Build.VERSION.SDK_INT < 19) {
            View view = this.getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void initData() {
        ROBOT_MAC = StringUtils.getPhoneIMEI(this);
        Log.d("mac", ROBOT_MAC);
        trainTimeEntities = new ArrayList<>();
        broadCasts = new ArrayList<>();
        trainTimeAdapter = new TrainTimeAdapter(trainTimeEntities, this, R.layout.item_train);
        train_list_all.setAdapter(trainTimeAdapter);
        choose_train_num.setOnClickListener(this);
        trainInfoPresenter = new TrainInfoPresenterImpl(this);
        trainTimePresenter = new TrainTimePresenterImpl(this);
        broadCastPresenter = new BroadCastPresenterImpl(this);
        compareAndSpeech = new CompareAndSpeech(this);
//        chn = new String[4];
//        eng = new String[4];
//        chn[3] = getString(R.string.station_after3m);
//        eng[3] = getString(R.string.station_after3mE);
//        chn[0] = getString(R.string.station_after);
//        eng[0] = getString(R.string.station_afterE);
//        chn[2] = getString(R.string.station_before1m);
//        eng[2] = getString(R.string.station_before1mE);
//        chn[1] = getString(R.string.station_before);
//        eng[1] = getString(R.string.station_beforeE);
        intentRefreshService = new Intent();
        intentRefreshService.setClass(this, RefreshService.class);
        intentRefreshService.putExtra(Constant.SERVICE_INTENT_INFO, Constant.SERVICE_INFO);
        intentRefreshService.putExtra(Constant.SERVICE_INTENT_LIST, Constant.SERVICE_LIST);
        intentRefreshService.putExtra(Constant.SERVICE_INTENT_BROAD, Constant.SERVICE_BROAD);
        startService(intentRefreshService);
        intentRobotService = new Intent(this, RobotService.class);
        listBroadCast = new RefreshListBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BROADCASTACTIONLIST);
        filter.addAction(Constant.BROADCASTACTIONINFO);
        filter.addAction(Constant.BROADCASTACTIONBROADCAST);
        registerReceiver(listBroadCast, filter);

    }

    private void initView() {
        station_welcome_text = findViewById(R.id.station_welcome_text);
        train_num_id = findViewById(R.id.train_num_id);
        train_station_from = findViewById(R.id.train_station_from);
        train_station_to = findViewById(R.id.train_station_to);
        pre_station_text = findViewById(R.id.pre_station_text);
        after_station_text = findViewById(R.id.after_station_text);
        station_state = findViewById(R.id.station_state);
        train_station_num = findViewById(R.id.train_station_num);
        train_from_time = findViewById(R.id.train_from_time);
        train_reminder_time = findViewById(R.id.train_reminder_time);
        choose_train_num = findViewById(R.id.choose_train_num);
        train_list_all = findViewById(R.id.train_list_all);
        words_anim = findViewById(R.id.words_anim);
        people_speak = findViewById(R.id.people_speak);
        speech_state = findViewById(R.id.speech_state);


    }

    @Override
    protected void onResume() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
        super.onResume();
    }


    @Override
    protected void onPause() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//软件在后台屏幕不需要常亮
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (intentRefreshService != null) {
            stopService(intentRefreshService);
            intentRefreshService = null;
        }
        if (listBroadCast != null) {
            unregisterReceiver(listBroadCast);
            listBroadCast = null;
        }
        if (connection != null) {
            unbindService(connection);

        }
        if (intentRobotService != null) {
            stopService(intentRobotService);
            intentRobotService = null;
        }

        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_train_num:
                Intent intent = new Intent(this, ChooseTrainNo.class);
                startActivityForResult(intent, 11);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 11 && resultCode == 22) {
            bindRobotService(Constant.CHOOSE_USER_NUM_ID);
            setButtonDisable();
        }
    }

    private void setButtonDisable() {
        choose_train_num.setClickable(false);
        choose_train_num.setText("");
        final int[] time_second = {5};
        final Timer timer = new Timer();
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        choose_train_num.setHint("请您等待" + time_second[0]-- + "秒");
                        break;
                    case 1:
                        choose_train_num.setClickable(true);
                        choose_train_num.setText(getString(R.string.choose_train_num));
                        time_second[0] = 5;
                        timer.cancel();
                        break;
                }
            }
        };

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(0);

                if (time_second[0] == 0) {
                    handler.sendEmptyMessage(1);

                }
            }
        };
        timer.schedule(timerTask, 0, 1000);

    }

    private void bindRobotService(final String id_choose) {
        connection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mBinderService = (RobotService.ServiceBinder) service;
                mBinderService.startChange(id_choose);
                Log.d("no", id_choose);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(intentRobotService, connection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void showError() {
        Log.e("getDataError", "获取数据出错");
    }

    @Override
    public void setTrainTime(BroadCast[] broadCast) {
        broadCasts = Arrays.asList(broadCast);
        for (BroadCast broadCast1 : broadCasts) {
            broadCast1.setCount(0);
        }
        compareAndSpeech.setBroadCasts(broadCasts);

//        for (int i = 0; i < broadCasts.length; i++) {
//
//            switch (broadCasts[i].getName()) {
//                case "before":
//                    chn[1] = broadCasts[i].getContent();
//                    break;
//                case "before1m":
//                    chn[2] = broadCasts[i].getContent();
//                    break;
//                case "after":
//                    chn[0] = broadCasts[i].getContent();
//                    break;
//                case "after3m":
//                    chn[3] = broadCasts[i].getContent();
//                    break;
//                case "beforeE":
//                    eng[1] = broadCasts[i].getContent();
//                    break;
//                case "before1mE":
//                    eng[2] = broadCasts[i].getContent();
//                    break;
//                case "afterE":
//                    eng[0] = broadCasts[i].getContent();
//                    break;
//                case "after3mE":
//                    eng[3] = broadCasts[i].getContent();
//                    break;
//            }
//        }

    }


//    private void speakTime(int i, final int num) {
//        LeoSpeech.setEnglishMode(false);
//
//        chn[num] = chn[num].replace("@StartStation", trainTimeEntities.get(i).getStartStation());
//        chn[num] = chn[num].replace("@trid", StringUtils.splitString(trainTimeEntities.get(i).getTrainNum()));
//        chn[num] = chn[num].replace("@TerStation", trainTimeEntities.get(i).getEndStation());
//        chn[num] = chn[num].replace("@LocalStation", trainTimeEntities.get(i).getStationName());
//        chn[num] = chn[num].replace("@PlatformC", String.valueOf(ROBOT_PLATFORM) + "站台");
//        final int finalI = i;
//        LeoSpeech.speak(chn[num], new ISpeakListener() {
//            @Override
//            public void onSpeakOver(int j) {
//                count[num]++;
//                if (count[num] >= 3) {
//                    has_speech[num] = true;
//                }
//                LeoSpeech.setEnglishMode(true);
//
//                eng[num] = eng[num].replace("@StartStation", Pinyin.toPinyin(StringUtils.splitString(trainTimeEntities.get(finalI).getStartStation().split("站")[0], "'"), "").toLowerCase());
//                eng[num] = eng[num].replace("@trid", StringUtils.splitString(trainTimeEntities.get(finalI).getTrainNum()));
//                eng[num] = eng[num].replace("@TerStation", Pinyin.toPinyin(StringUtils.splitString(trainTimeEntities.get(finalI).getEndStation().split("站")[0], "'"), "").toLowerCase());
//                eng[num] = eng[num].replace("@LocalStation", Pinyin.toPinyin(StringUtils.splitString(trainTimeEntities.get(finalI).getStationName().split("站")[0], "'"), "").toLowerCase());
//                eng[num] = eng[num].replace("@PlatformC", "platform " + String.valueOf(ROBOT_PLATFORM));
//                LeoSpeech.speak(eng[num], null);
//            }
//        });
//    }

    @Override
    public void setTrainTime(RobotEntity[] trainTime) {
        Log.d("message", trainTime.toString());
        trainTimeEntities = Arrays.asList(trainTime);
        Collections.sort(trainTimeEntities);
        trainTimeAdapter.updateRes(trainTimeEntities);
        if (trainTime != null && trainTime.length > 0) {
            boolean has_last = false;
            for (int i = 0; i < trainTimeEntities.size(); i++) {
                int time = TimeUtils.compareTime(trainTimeEntities.get(i).getDepartureTime());
                int train_state = 0;
                if (trainTimeEntities.get(i).getStationName().equalsIgnoreCase(trainTimeEntities.get(i).getStartStation())) {
                    train_state = -1;
                } else if (trainTimeEntities.get(i).getStationName().equalsIgnoreCase(trainTimeEntities.get(i).getStartStation())) {
                    train_state = 1;
                } else {
                    train_state = 0;
                }
                compareAndSpeech.speake(trainTimeEntities.get(i).getArriveTime(), trainTimeEntities.get(i).getDepartureTime(), train_state);
//                int time2 = TimeUtils.compareTime(trainTimeEntities.get(i).getArriveTime());
//                int time3 = TimeUtils.compareBefore(trainTimeEntities.get(i).getArriveTime(), 3);
//                int time4 = TimeUtils.compareBefore(trainTimeEntities.get(i).getArriveTime(), 1);
//                int time6 = TimeUtils.compareAfter(trainTimeEntities.get(i).getArriveTime(), 1);
//                int time7 = TimeUtils.compareAfter(trainTimeEntities.get(i).getArriveTime(), 2);
//                int time5 = TimeUtils.compareBefore(trainTimeEntities.get(i).getDepartureTime(), 3);
//                if (time5 == 0 && !has_speech[3] && canSpeech && !SpeechTools.isBusy(this)) {
//                    //语音播放
//                    speakTime(i, 3);
//                }
//                if ((time2 == 0 || time6 == 0 || time7 == 0) && !has_speech[0] && canSpeech && !SpeechTools.isBusy(this)) {
//                    //语音播放
//                    speakTime(i, 0);
//                }
//                if (time4 == 0 && !has_speech[2] && canSpeech && !SpeechTools.isBusy(this)) {
//                    //语音播放
//                    speakTime(i, 2);
//                }
//
//                if (time3 == 0 && !has_speech[1] && canSpeech && !SpeechTools.isBusy(this)) {
//                    //语音播放
//                    speakTime(i, 1);
//                }

                if (time <= 0) {
                    if (choose_num != i) {
                        for (BroadCast broadCast : broadCasts) {
                            broadCast.setCount(0);
                        }
//                        has_speech[0] = false;
//                        has_speech[1] = false;
//                        has_speech[2] = false;
//                        has_speech[3] = false;
//                        count[0] = 0;
//                        count[1] = 0;
//                        count[2] = 0;
//                        count[3] = 0;

                    }
                    has_last = true;
                    choose_num = i;
                    break;
                }
            }
            if (!has_last) {
                choose_num = 0;
            }

            trainTimeAdapter.setSelectItem(choose_num);
            trainTimeAdapter.notifyDataSetInvalidated();
            START_STATION = trainTimeEntities.get(choose_num).getStartStation();
            TRAIN_NUM = trainTimeEntities.get(choose_num).getTrainNum();
            END_STATION = trainTimeEntities.get(choose_num).getEndStation();
            STATION_NAME = trainTimeEntities.get(choose_num).getStationName();
            train_num_id.setText(TRAIN_NUM);
            train_station_from.setText(START_STATION);
            train_station_to.setText(END_STATION);
            station_welcome_text.setText(STATION_NAME + "欢迎您");
            long status = trainTimeEntities.get(choose_num).getStatus();
            if (status == 0) {
                station_state.setText("正点到达");
                station_state.setTextColor(Color.GREEN);
            } else if (status > 0) {
                station_state.setText("晚点" + String.valueOf(status) + "分钟");
                station_state.setTextColor(Color.RED);
            } else {
                station_state.setText("提前" + String.valueOf(status) + "分钟");
                station_state.setTextColor(Color.RED);
            }
            train_from_time.setText(trainTimeEntities.get(choose_num).getDepartureTime());
            train_from_time.setTextColor(Color.YELLOW);
            train_reminder_time.setText(trainTimeEntities.get(choose_num).getStopTime());
        }
    }

    @Override
    public void setTrainInfo(TrainInfoEntity trainInfo) {
        Log.d("message", trainInfo.toString());
        if (trainInfo.getMsg() == 200) {
            RobotManageEntity robotManageEntity = trainInfo.getRobotManageEntity();
            if (robotManageEntity != null) {
                NOW_STATION = robotManageEntity.getCurTrainNo();
                ROBOT_DIR = robotManageEntity.getDirection();
                ROBOT_PLATFORM = robotManageEntity.getPlatform();
                RED_DIRECTION = robotManageEntity.getRedDirection();
                LAST_STATION = robotManageEntity.getLastTrainNo();
                if (NOW_STATION.equalsIgnoreCase(LAST_STATION)) {
                    curTrainNoAdd = NOW_STATION;
                } else {
                    curTrainNoAdd = String.valueOf(Integer.valueOf(NOW_STATION) + 1);
                }
                if (ROBOT_DIR.equalsIgnoreCase("向前")) {
                    direction2 = "向后";
                } else {
                    direction2 = "向前";
                }
                pre_station_text.setText("1——" + NOW_STATION + "车厢" + ROBOT_DIR);
                after_station_text.setText(curTrainNoAdd + "——" + LAST_STATION + "车厢" + direction2);
                train_station_num.setText(String.valueOf(ROBOT_PLATFORM));
            }


        } else {
            Log.e("getData", "获取数据出错");
        }
    }

    @Override
    public void onIdleState() {
        Log.e(TAG, "说话结束");
        speech_state.setText("");
//        speech_state.setTextColor(Color.WHITE);
//        speech_state.setTextSize(28);

    }

    @Override
    public void onPreparingState() {
    }

    @Override
    public void onRecordingState() {
        Log.e(TAG, "开始说话");
        speech_state.setText("<<<正在聆听，请开始说话>>>");
        speech_state.setTextColor(Color.GREEN);
        speech_state.setTextSize(28);
    }

    @Override
    public void onVolumeUpdate(int i) {
//        Log.e(TAG, "onVolumeUpdate" + i);
    }

    @Override
    public void onSpeakUpdate(String s) {
    }

    @Override
    public void onRecognizingState() {
    }

    @Override
    public void onErrorState(int i) {
    }

    @Override
    public void setViewActionListener(IViewActionListener iViewActionListener) {
    }

    @Override
    public void procViewOperation(Message message) {
    }

    @Override
    public void showInitView() {
    }

    @Override
    public void setImageButton(ImageButton imageButton) {
    }

    public class RefreshListBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constant.BROADCASTACTIONINFO:
                    trainInfoPresenter.getTrainInfo(Constant.ROBOT_MAC);
                    break;
                case Constant.BROADCASTACTIONLIST:
                    trainTimePresenter.getTrainTime(String.valueOf(Constant.ROBOT_PLATFORM));
                    break;
                case Constant.BROADCASTACTIONBROADCAST:
                    broadCastPresenter.getBroadCast(String.valueOf(Constant.ROBOT_PLATFORM));
                    break;
            }
        }
    }
}
