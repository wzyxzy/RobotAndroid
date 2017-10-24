package com.zgty.robotandroid.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.zgty.robotandroid.beans.TrainTimeList;
import com.zgty.robotandroid.presenter.TrainInfoPresenter;
import com.zgty.robotandroid.presenter.TrainInfoPresenterImpl;
import com.zgty.robotandroid.presenter.TrainTimePresenter;
import com.zgty.robotandroid.presenter.TrainTimePresenterImpl;
import com.zgty.robotandroid.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TrainInfoView, TrainTimeView {

    private TextView station_welcome_text;
    private TextView train_num_id;
    private TextView train_station_from;
    private TextView train_station_to;
    private TextView pre_station_text;
    private TextView after_station_text;
    private TextView station_state;
    private TextView train_station_num;
    private TextView train_from_time;
    private TextView train_reminder_time;
    private TextView choose_train_num;
    private ListView train_list_all;

    private TrainInfoPresenter trainInfoPresenter;
    private TrainTimePresenter trainTimePresenter;

    private String curTrainNoAdd;
    private String curTrainNo;
    private String direction1;
    private String direction2;
    private TrainTimeAdapter trainTimeAdapter;
    private List<TrainTimeEntity> trainTimeEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);//设置全屏
        setContentView(R.layout.activity_main);
        if (getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);//设置横屏
        }
        initView();

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
        trainTimeEntities = new ArrayList<>();
        trainTimeAdapter = new TrainTimeAdapter(trainTimeEntities, this, R.layout.item_train);
        train_list_all.setAdapter(trainTimeAdapter);
//        LayoutInflater inflater = getLayoutInflater();
//        ViewGroup header = (ViewGroup)inflater.inflate(R.layout.item_train, train_list_all, false);
//        train_list_all.addHeaderView(header, null, false);
//        View header = getLayoutInflater().inflate(R.layout.item_train, null);
//        train_list_all.addHeaderView(header);
        choose_train_num.setOnClickListener(this);

        trainInfoPresenter = new TrainInfoPresenterImpl(this);
        trainInfoPresenter.getTrainInfo("8");
        trainTimePresenter = new TrainTimePresenterImpl(this);
        trainTimePresenter.getTrainTime("T123");

        train_list_all.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                ToastUtil.ShowShort(getApplicationContext(), position + "");
//                train_list_all.setSelection(position);
//                train_list_all.setFocusable(true);
//                train_list_all.setItemChecked(position, false);
//                train_list_all.setSelector();
            }
        });

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
}
