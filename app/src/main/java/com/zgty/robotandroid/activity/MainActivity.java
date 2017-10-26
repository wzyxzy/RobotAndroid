package com.zgty.robotandroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.zgty.robotandroid.R;
import com.zgty.robotandroid.adapters.TrainTimeAdapter;
import com.zgty.robotandroid.beans.RobotEntity;
import com.zgty.robotandroid.beans.RobotManageEntity;
import com.zgty.robotandroid.beans.TrainInfoEntity;
import com.zgty.robotandroid.beans.TrainTimeEntity;
import com.zgty.robotandroid.common.Constant;
import com.zgty.robotandroid.presenter.TrainInfoPresenter;
import com.zgty.robotandroid.presenter.TrainInfoPresenterImpl;
import com.zgty.robotandroid.presenter.TrainTimePresenter;
import com.zgty.robotandroid.presenter.TrainTimePresenterImpl;
import com.zgty.robotandroid.service.RefreshService;
import com.zgty.robotandroid.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TrainInfoView, TrainTimeView {

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

    private TrainInfoPresenter trainInfoPresenter;
    private TrainTimePresenter trainTimePresenter;

    private String curTrainNoAdd;
    private String curTrainNo;
    private String direction1;
    private String direction2;
    private TrainTimeAdapter trainTimeAdapter;
    private List<TrainTimeEntity> trainTimeEntities;

    private RefreshListBroadCast listBroadCast;
    private Intent intentRefreshService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initScreen();
        initView();
        initData();

    }

    private void initScreen() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏
        }
    }

    private void initData() {
        trainTimeEntities = new ArrayList<>();
        trainTimeAdapter = new TrainTimeAdapter(trainTimeEntities, this, R.layout.item_train);
        train_list_all.setAdapter(trainTimeAdapter);
        choose_train_num.setOnClickListener(this);

        trainInfoPresenter = new TrainInfoPresenterImpl(this);
        trainInfoPresenter.getTrainInfo(Constant.NOW_STATION);
        trainTimePresenter = new TrainTimePresenterImpl(this);
        trainTimePresenter.getTrainTime(Constant.BEGIN_TRAIN_NUM);

        train_list_all.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Constant.NOW_STATION = trainTimeEntities.get(position).getTrainNum();
                trainInfoPresenter.getTrainInfo(Constant.NOW_STATION);
            }
        });

        intentRefreshService = new Intent();
        intentRefreshService.setClass(this, RefreshService.class);
        intentRefreshService.putExtra(Constant.SERVICE_INTENT_INFO, Constant.SERVICE_INFO);
        intentRefreshService.putExtra(Constant.SERVICE_INTENT_LIST, Constant.SERVICE_LIST);
        startService(intentRefreshService);
        listBroadCast = new RefreshListBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BROADCASTACTIONLIST);
        filter.addAction(Constant.BROADCASTACTIONINFO);
        registerReceiver(listBroadCast, filter);

    }

    private void initView() {
        station_welcome_text = (TextView) findViewById(R.id.station_welcome_text);
        train_num_id = (TextView) findViewById(R.id.train_num_id);
        train_station_from = (TextView) findViewById(R.id.train_station_from);
        train_station_to = (TextView) findViewById(R.id.train_station_to);
        pre_station_text = (TextView) findViewById(R.id.pre_station_text);
        after_station_text = (TextView) findViewById(R.id.after_station_text);
        station_state = (TextView) findViewById(R.id.station_state);
        train_station_num = (TextView) findViewById(R.id.train_station_num);
        train_from_time = (TextView) findViewById(R.id.train_from_time);
        train_reminder_time = (TextView) findViewById(R.id.train_reminder_time);
        choose_train_num = (TextView) findViewById(R.id.choose_train_num);
        train_list_all = (ListView) findViewById(R.id.train_list_all);


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
        super.onDestroy();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.choose_train_num:

                break;
        }
    }

    @Override
    public void showError() {
        ToastUtil.ShowShort(this, "获取数据出错");
    }

    @Override
    public void setTrainTime(TrainTimeEntity[] trainTime) {
        trainTimeEntities = Arrays.asList(trainTime);
        trainTimeAdapter.updateRes(trainTimeEntities);
    }

    @Override
    public void setTrainInfo(TrainInfoEntity trainInfo) {
        if (trainInfo.getMsg().equalsIgnoreCase("200")) {
            RobotManageEntity robotManageEntity = trainInfo.getRobotManageEntity();
            RobotEntity robotEntity = trainInfo.getRobotEntity();
            if (robotManageEntity != null) {
                curTrainNo = robotManageEntity.getCurTrainNo();
                direction1 = robotManageEntity.getDirection();
                if (curTrainNo.equalsIgnoreCase("16")) {
                    curTrainNoAdd = curTrainNo;
                } else {
                    curTrainNoAdd = String.valueOf(Integer.valueOf(curTrainNo) + 1);
                }
                if (direction1.equalsIgnoreCase("向前")) {
                    direction2 = "向后";
                } else {
                    direction2 = "向前";
                }
                pre_station_text.setText("1——" + curTrainNo + "车厢" + direction1);
                after_station_text.setText(curTrainNoAdd + "——16车厢" + direction2);

            }
            if (robotEntity != null) {
                train_num_id.setText(robotEntity.getTrainNum());
                train_station_from.setText(robotEntity.getStartStation());
                train_station_to.setText(robotEntity.getEndStation());
                station_state.setText(robotEntity.getStatus());
                train_station_num.setText(String.valueOf(robotEntity.getPlatform()));
                train_from_time.setText(robotEntity.getDepartureTime());
                train_reminder_time.setText(robotEntity.getStopTime());
            }


        } else {
            ToastUtil.ShowShort(this, "获取数据出错");
        }
    }

    public class RefreshListBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case Constant.BROADCASTACTIONINFO:
                    trainInfoPresenter.getTrainInfo(Constant.NOW_STATION);
                    break;
                case Constant.BROADCASTACTIONLIST:
                    trainTimePresenter.getTrainTime(Constant.BEGIN_TRAIN_NUM);
                    break;
            }
        }
    }
}
