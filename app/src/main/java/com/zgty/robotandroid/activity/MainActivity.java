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
import com.zgty.robotandroid.util.ToastUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.zgty.robotandroid.common.Constant.NOW_STATION;
import static com.zgty.robotandroid.common.Constant.ROBOT_DIR;
import static com.zgty.robotandroid.common.Constant.ROBOT_PLATFORM;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, TrainInfoView, TrainTimeView, IViewUpdater {

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

    private TrainInfoPresenter trainInfoPresenter;
    private TrainTimePresenter trainTimePresenter;

    private String curTrainNoAdd;
    private String curTrainNo;
    private String direction1;
    private String direction2;
    private int platform;
    private TrainTimeAdapter trainTimeAdapter;
    private List<RobotEntity> trainTimeEntities;

    private RefreshListBroadCast listBroadCast;
    private Intent intentRefreshService;
    private Intent intentRobotService;
    private RobotService.ServiceBinder mBinderService;
    private ServiceConnection connection;

    private static final int COLOR_CHOICES[] = {
            Color.BLUE,
            Color.CYAN,
            Color.GREEN,
            Color.MAGENTA,
            Color.RED,
            Color.WHITE,
            Color.YELLOW
    };
    private static int mCurrentColorIndex = 0;
//    private Animation robotAnimation;
//    private Animation peopleAnimation;

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

//        mResultProcessor.onResult(new NLPResult());
        /*
        LeoSpeech.init(this, new IResultProcessor() {

			@Override
			public void reset() {
				// TODO 自动生成的方法存根

			}

			@Override
			public void onSwitchOK() {
				// TODO 自动生成的方法存根

			}

			@Override
			public void onResult(NLPResult arg0) {
				Toast.makeText(MainActivity.this, "识别结果："+arg0.getRawtext(), Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onPartialResult(RecognizerResult arg0) {
				// TODO 自动生成的方法存根

			}

			@Override
			public String onLoaclRecResult(String arg0) {
				// TODO 自动生成的方法存根
				return null;
			}

			@Override
			public void onInit() {
				// TODO 自动生成的方法存根

			}

			@Override
			public void onError(int arg0) {
				LeoSpeech.speakAndRestartRecognise("你在说什么啊？");
			}

			@Override
			public boolean isBusy() {
				// TODO 自动生成的方法存根
				return false;
			}

			@Override
			public void handleResult(String arg0, String arg1) {

			}

			@Override
			public void handleCmd(String arg0) {
				// TODO 自动生成的方法存根

			}

			@Override
			public void clearTask() {
				// TODO 自动生成的方法存根

			}
		});
		*/
        //设置语音引擎状态更新接口
//        LeoSpeech.setViewUpdater(this);
        ResultProcessor mResultProcessor = new ResultProcessor(this);
        Log.v("wss", "init................");
        LeoSpeech.init(this, mResultProcessor);
        LeoSpeech.addGrammarWords("一车厢");
        LeoSpeech.addGrammarWords("二车厢");
        LeoSpeech.addGrammarWords("三车厢");
        LeoSpeech.addGrammarWords("四车厢");
        LeoSpeech.addGrammarWords("五车厢");
        LeoSpeech.addGrammarWords("六车厢");
        LeoSpeech.addGrammarWords("七车厢");
        LeoSpeech.addGrammarWords("八车厢");
        LeoSpeech.addGrammarWords("九车厢");
        LeoSpeech.addGrammarWords("十车厢");
        LeoSpeech.addGrammarWords("十一车厢");
        LeoSpeech.addGrammarWords("十二车厢");
        LeoSpeech.addGrammarWords("十三车厢");
        LeoSpeech.addGrammarWords("十四车厢");
        LeoSpeech.addGrammarWords("十五车厢");
        LeoSpeech.addGrammarWords("十六车厢");
        mResultProcessor.setOnVoiceListener(new ResultProcessor.OnVoiceListener() {

            @Override
            public void onWords(String words) {
//                mCurrentColorIndex = (mCurrentColorIndex + 1) % COLOR_CHOICES.length;
//                final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];
//                people_speak.setTextColor(selectedColor);
//                people_speak.setText(words);
//                words_anim.startAnimation(peopleAnimation);


            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onSuccess(int nums) {
//                final int selectedColor = COLOR_CHOICES[mCurrentColorIndex];
//                words_anim.setTextColor(selectedColor);
//                words_anim.setText(String.valueOf(nums) + "车厢");
//                words_anim.startAnimation(robotAnimation);
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
    }

    private void initData() {
        trainTimeEntities = new ArrayList<>();
        trainTimeAdapter = new TrainTimeAdapter(trainTimeEntities, this, R.layout.item_train);
        train_list_all.setAdapter(trainTimeAdapter);
        choose_train_num.setOnClickListener(this);

        trainInfoPresenter = new TrainInfoPresenterImpl(this);
//        trainInfoPresenter.getTrainInfo(Constant.ROBOT_MAC);
        trainTimePresenter = new TrainTimePresenterImpl(this);
//        trainTimePresenter.getTrainTime(String.valueOf(Constant.ROBOT_PLATFORM));

//        train_list_all.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Constant.NOW_STATION = trainTimeEntities.get(position).getTrainNum();
//                trainInfoPresenter.getTrainInfo(Constant.NOW_STATION);
//            }
//        });

        intentRefreshService = new Intent();
        intentRefreshService.setClass(this, RefreshService.class);
        intentRefreshService.putExtra(Constant.SERVICE_INTENT_INFO, Constant.SERVICE_INFO);
        intentRefreshService.putExtra(Constant.SERVICE_INTENT_LIST, Constant.SERVICE_LIST);
        startService(intentRefreshService);
        intentRobotService = new Intent(this, RobotService.class);
        listBroadCast = new RefreshListBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BROADCASTACTIONLIST);
        filter.addAction(Constant.BROADCASTACTIONINFO);
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
//        robotAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.robot_speak);
//        robotAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                words_anim.setText("");
//                people_speak.setText("");
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });
//        peopleAnimation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.people_speak);
//        peopleAnimation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                people_speak.setText("");
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//
//            }
//        });

    }

    @Override
    protected void onResume() {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//保持屏幕常亮
//        SpeechTools.speakAndRestartRecognize("开始识别");
        SpeechTools.startRecognize();
        super.onResume();
    }

    @Override
    protected void onPause() {
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);//软件在后台屏幕不需要常亮
//        LeoSpeech.speak("停止识别", new ISpeakListener() {
//            @Override
//            public void onSpeakOver(int arg0) {
//                LeoSpeech.stopRecognize();
//            }
//        });
        LeoSpeech.stopRecognize();
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
//                bindRobotService();
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
        ToastUtil.ShowShort(this, "获取数据出错");
    }

    @Override
    public void setTrainTime(RobotEntity[] trainTime) {
        Log.d("message", trainTime.toString());
        trainTimeEntities = Arrays.asList(trainTime);
        trainTimeAdapter.updateRes(trainTimeEntities);
        if (trainTime != null && trainTime.length > 0) {
            train_num_id.setText(trainTimeEntities.get(3).getTrainNum());
            train_station_from.setText(trainTimeEntities.get(3).getStartStation());
            train_station_to.setText(trainTimeEntities.get(3).getEndStation());
            station_welcome_text.setText(trainTimeEntities.get(3).getStationName() + "欢迎您");
            long status = trainTimeEntities.get(3).getStatus();
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
            train_from_time.setText(trainTimeEntities.get(3).getDepartureTime());
            train_from_time.setTextColor(Color.YELLOW);
            train_reminder_time.setText(trainTimeEntities.get(3).getStopTime());
        }
    }

    @Override
    public void setTrainInfo(TrainInfoEntity trainInfo) {
        Log.d("message", trainInfo.toString());
        if (trainInfo.getMsg() == 200) {
            RobotManageEntity robotManageEntity = trainInfo.getRobotManageEntity();
//            RobotEntity robotEntity = trainInfo.getRobotEntity();
            if (robotManageEntity != null) {
                curTrainNo = robotManageEntity.getCurTrainNo();
                direction1 = robotManageEntity.getDirection();
                platform = robotManageEntity.getPlatform();
                NOW_STATION = curTrainNo;
                ROBOT_DIR = direction1;
                ROBOT_PLATFORM = platform;
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
                train_station_num.setText(String.valueOf(platform));
            }


        } else {
            ToastUtil.ShowShort(this, "获取数据出错");
        }
    }

    @Override
    public void onIdleState() {

    }

    @Override
    public void onPreparingState() {

    }

    @Override
    public void onRecordingState() {

    }

    @Override
    public void onVolumeUpdate(int i) {

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
            }
        }
    }
}
