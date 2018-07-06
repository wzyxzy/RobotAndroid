package com.leotech.actioncontroller;



import java.io.BufferedInputStream;
import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import com.leo.api.LeoRobot;
import com.leo.api.LeoSpeech;
import com.leo.api.abstracts.IRobotListener;
import com.leo.api.abstracts.ISpeakListener;
import com.leo.api.abstracts.IViewActionListener;
import com.leo.api.abstracts.IViewUpdater;
import com.leo.api.cagome.stripcard.CardInfo;
import com.leo.api.cagome.stripcard.IDCardInfo;
import com.leo.api.control.ControlService;
import com.leo.api.control.agora.AgoraConstant;
import com.leo.api.control.music.MediaResourcePlayer;
import com.leo.api.control.music.OnMediaEndListener;
import com.leo.api.control.net.Letter;
import com.leo.api.control.net.LetterType;
import com.leo.api.control.net.RemoteSendThread;
import com.leo.api.socket.SocketThread;
import com.leotech.SpeechTools;
import com.leotech.agora.OpenVCallActivity;
import com.leotech.agora.RemoteVideoParamActivity;
import com.leotech.business.ResultProcessor;
import com.leotech.print.PicFromPrintUtils;
import com.leotech.print.SerialPrinter;

public class MainActivity extends Activity implements IViewUpdater{
	private static String  tag = "main";
	private EditText mEtAction; 
	private EditText mEtSpeak;
	private EditText mEtNav; 
	private EditText mEtNav2; 
	private EditText mEtbluetooth; 
	private EditText mEtprint; 
	private EditText mEtengine_id;
	private EditText mEtangle;
	private EditText mEtranparent;
	private EditText mEtPcSerial;
	private Button mEtElect; 
	private Button mEtDistance; 
	private Button mEtStatus;
	private Button mEttranparent;
	private Button mEtFace; 
	private Button mEtFace1;
	
	private int charge_full_count = 0;
	private int charge_high_count = 0;
	private static boolean incharge = false;
	public static final byte[] GKB_mode = { 0X1B, 0X39, 0x0 }; // ESC 9 n: 0 GBK
	public  final static byte[] all_cut = { 0X1B, 0X69 };
	protected static final byte LF = 0x0A; // line feed
	public  final static byte[] center = { 0x1b, 0x61, 0x31 };//居中对齐
	private ImageView mImageShow;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mEtAction = (EditText) findViewById(R.id.et_action);
		mEtSpeak =  (EditText) findViewById(R.id.et_speak);
		mEtNav =  (EditText) findViewById(R.id.et_nav);
		mEtNav2 =  (EditText) findViewById(R.id.et_nav2);
		mEtElect =  (Button) findViewById(R.id.btn_elect);
		mEtDistance =  (Button) findViewById(R.id.btn_distance);
		mEtbluetooth =  (EditText) findViewById(R.id.et_blue_addr);
		mEtStatus =  (Button) findViewById(R.id.btn_status);
		mEtprint = (EditText) findViewById(R.id.et_print);
		mEtengine_id = (EditText) findViewById(R.id.engine_id);
		mEtangle = (EditText) findViewById(R.id.angle);
		mImageShow = (ImageView) findViewById(R.id.photo);
		mEtFace = (Button) findViewById(R.id.btn_face);
		mEtFace1 = (Button) findViewById(R.id.btn_face1);
		mEtranparent = (EditText) findViewById(R.id.et_tranparent);
		mEtPcSerial = (EditText) findViewById(R.id.et_pc_serial);
		ResultProcessor  mResultProcessor = new ResultProcessor(this);	
		Log.v("wss","init................");
        LeoSpeech.init(this, mResultProcessor);
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
        LeoSpeech.setViewUpdater(this);
		(new LeoRobot()).init(this, new IRobotListener() {
			@Override
			public void onMicWakeUp() {
				LeoSpeech.stopSpeak();
				LeoRobot.stopAction();
				SpeechTools.speakAndRestartRecognize("在呢,有何吩咐");	
				//可以再次启动语音识别界面
			}
			@Override
			public void onTouch() {
				LeoSpeech.stopSpeak();
			}
			//导航到达接口
			@Override
			public void onReached(String dest) {
				// TODO 自动生成的方法存根
				Log.d(tag,"reached dest ="+dest);
				Toast.makeText(MainActivity.this, "到达目的地="+dest, 3000).show();
				if(dest.equalsIgnoreCase("充电区")){
					//通知蓝牙板开始寻找充电座
					LeoRobot.search_charge_station();
				}
			}
			//漫游过程中遇到障碍物
			@Override
			public void onMeetObstacle() {
				// TODO 自动生成的方法存根
				Toast.makeText(MainActivity.this, "onMeetObstacle", 2000).show();
			}
			//充电状态变化
			@Override
			public void onCharegState(int state) {
				/*
				0x01	0x00	充电对接成功
				0x01	0x01	正在寻找充电座
				0x01	0x02	寻找充电座失败
				0x01	0x03	机器人与充电座分离
				 */
				incharge = false;
				if(state == 0)
				{
					LeoSpeech.speak("充电座对接成功，开始充电",null);
					Toast.makeText(MainActivity.this, "充电对接成功="+state, 2000).show();
					incharge = true;
					charge_full_count = 0;
					charge_high_count = 0;
					
				}
				else if (state == 1)
					Toast.makeText(MainActivity.this, "正在寻找充电座="+state, 2000).show();
				else if (state == 2)
				{
					LeoSpeech.speak("寻找充电座失败",null);
					Toast.makeText(MainActivity.this, "寻找充电座失败="+state, 2000).show();
				}
				else if (state == 3)
					Toast.makeText(MainActivity.this, "机器人与充电座分离="+state, 2000).show();
				//Toast.makeText(MainActivity.this, "onCharegState="+state, 2000).show();
			}
			@Override
			public void onActionStop() {
				// TODO 自动生成的方法存根
				
			}
			
			@Override
			public void getWifiInfo(String arg0, String arg1, int arg2) {
				// TODO 自动生成的方法存根
				
			}
			
			@Override
			public void getElect(int arg0) {
				// TODO 自动生成的方法存根
				mEtElect.setText("电池电量:"+arg0);
				mEtElect.invalidate();
				Log.v(tag,"battery ="+arg0+" charge_high_count="+charge_high_count+" charge_full_count="+charge_full_count);
				if(arg0 == 100)
					charge_full_count++;
				else if(arg0 >= 98)
					charge_high_count ++;
				if((charge_high_count >= 2400)||(charge_full_count >=100)){
					if(incharge == true)
					{
						Log.v(tag,"charege over");
						LeoSpeech.speak("充电完成", new ISpeakListener() {
							
							@Override
							public void onSpeakOver(int arg0) {
								LeoRobot.stop_charge();
								incharge = false;
								charge_full_count = 0;
								charge_high_count = 0;
							}
						});					

					}
					
				}
				//Toast.makeText(MainActivity.this, "电池电量为"+arg0, Toast.LENGTH_SHORT).show();
				
			}
			
			@Override
			public void getDistance(int arg0) {
				// TODO 自动生成的方法存根
				mEtDistance.setText("红外距离:"+arg0);
				mEtDistance.invalidate();
				Log.v(tag,"distance ="+arg0);
			}
			
			@Override
			public void getBoardVersion(String arg0) {
				// TODO 自动生成的方法存根
				
			}

			@Override
			public void onReachCardSuccess(Object object) {
				// TODO 自动生成的方法存根
				String cardNo = null;
				String cardName = null;
				String cardSext = null;
				
				if (object instanceof String) {
					cardNo = (String) object;
					Log.d(tag,"String card no ="+cardNo);
					Toast.makeText(MainActivity.this, cardNo, 3000).show();
				}
				else if (object instanceof IDCardInfo) {//身份证
					IDCardInfo idCardInfo = (IDCardInfo) object;
					if (idCardInfo!=null) {
						Log.d(tag,"IDCardInfo card no ="+idCardInfo.toString());
						cardNo  = idCardInfo.getCardNo();
						cardSext  = idCardInfo.getSex();
					    cardName = idCardInfo.getName();;
					    String cardinfo = "姓名="+cardName+"身份证号="+cardNo+"性别="+cardSext;
						Toast.makeText(MainActivity.this, cardinfo, 3000).show();
						String media = "/storage/sdcard0/wltlib/zp.bmp";
						Drawable drawable = Drawable.createFromPath(media);
						mImageShow.setBackgroundDrawable(drawable);
						mImageShow.setVisibility(View.VISIBLE);
						mImageShow.invalidate();
/*
 * 
					 * 	private String name;//姓名
						private String sex;//性别
						private String national;//民族
						private String birthday;//出生日期 yyyyMMdd
						private String address;//地址
						private String cardNo;//身份证号码
						private String organ;//签发机关
						private String validTerm ;//有效期限
						private String otherInfo1;//其他信息
						private String otherInfo2;//其他信息
						private Bitmap portrait;//身份证头像
						public String getName() {
							return name;
						}
						public String getSex() {
							return sex;
						}
					
						public String getNational() {
							return national;
						}
					
						public String getBirthday() {
							return birthday;
						}
					
						public String getAddress() {
							return address;
						}
					
						public String getCardNo() {
							return cardNo;
						}
					
						public String getOrgan() {
							return organ;
						}
					
						public String getValidTerm() {
							return validTerm;
						}
					
						public String getOtherInfo1() {
							return otherInfo1;
						}
					
						public String getOtherInfo2() {
							return otherInfo2;
						}
					
						
						public Bitmap getPortrait() {
							return portrait;
						}
					
 */
					}
				}else if(object instanceof CardInfo){//银行卡
					CardInfo idCardInfo = (CardInfo) object;
					if (idCardInfo!=null) {
						Log.d(tag,"CardInfo card no ="+idCardInfo.getCardString());
						cardNo  = idCardInfo.getCardString();
						Toast.makeText(MainActivity.this, cardNo, 3000).show();
					}
				}
				LeoRobot.stopReadCard();
			}
			@Override
			public void onReachCardError(String error) {
				// TODO 自动生成的方法存根
				Toast.makeText(MainActivity.this, "error no = "+error, 2000).show();
				LeoRobot.stopReadCard();
			}
			@Override
			public void onPlayVideo(Uri uri) {
				// TODO 自动生成的方法存根
				Intent intent = new Intent(getApplicationContext(),VideoActivity.class);
				intent.setData(uri);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				getApplicationContext().startActivity(intent);
			}
			@Override
			public void onPCSerialDate(byte[] data) {
				// TODO 自动生成的方法存根
				Log.d("pc", "pc data received ");
				
			}			
		});
		SharedPreferences sp = getSharedPreferences("bluetooth", Context.MODE_PRIVATE);
		String adress = sp.getString("address", null);
		Log.v(tag,"hint address ="+adress);
		if(adress!=null){
			mEtbluetooth.setHint(adress);
		}
		startService(new Intent(this,ControlService.class));
		IntentFilter swFilter = new IntentFilter();
		swFilter.addAction(AgoraConstant.ACTION_SHENGWANG_START);
		registerReceiver(mShengWangActivityStartReceiver, swFilter);
	}

	public void onClick(View view){
		switch(view.getId()){
		case R.id.btn_action: {
			String action = mEtAction.getText().toString();
			LeoRobot.doAction(action);
			break;
		}
		case R.id.btn_diantou: {
			LeoSpeech.speak("点头",null);
			LeoRobot.doAction("diantou");
			break;
		}
		case R.id.btn_yaotou: {
			LeoSpeech.speak("摇头",null);
			LeoRobot.doAction("yaotou");
			break;
		}
		case R.id.btn_left_pls: {
			LeoSpeech.speak("左请",null);
			LeoRobot.doAction("left");
			break;
		}
		case R.id.btn_right_pls: {
			LeoSpeech.speak("右请",null);
			LeoRobot.doAction("right");
			break;
		}
		case R.id.btn_dance: {
			LeoSpeech.speak("开始跳舞", new ISpeakListener() {
				@Override
				public void onSpeakOver(int errorCode) {
					MediaResourcePlayer.playRadomDance(getApplicationContext(), new OnMediaEndListener(){
						@Override
						public void onCompletion(MediaPlayer arg0) {
							LeoSpeech.speak("跳舞结束",null);
						}
						@Override
						public void onSpeakOver(int errorCode) {
						
						}
					});
				}
			});
			break;
		}
		case R.id.btn_action_stop: {
			LeoSpeech.speak("停止动作",null);
			LeoRobot.stopAction();
			break;
		}
		case R.id.btn_dance_stop: {
			LeoSpeech.speak("停止跳舞", new ISpeakListener() {
				@Override
				public void onSpeakOver(int errorCode) {
					MediaResourcePlayer.stopCurrentActions(getApplicationContext());
				}
			});
			
			break;
		}
		case R.id.btn_tranparent: {
			String action = mEtranparent.getText().toString();
			LeoRobot.tranparent(action);
			break;
		}
		case R.id.btn_pc_serial: {
			String data = mEtPcSerial.getText().toString();
		    String str2 = data.replaceAll(" ", ""); 
		    Log.d("pc", "str2 ="+data);
		    byte[] bytes= hex2Bytes(str2);
			LeoRobot.writePCserial(bytes);
			break;
		}
		case R.id.btn_remote_video: {
			LeoSpeech.stopSpeak();
			LeoSpeech.stopRecognize();
			Intent remoteVideoIntent = new Intent(this, OpenVCallActivity.class);
			remoteVideoIntent.putExtra(AgoraConstant.CALL_DIRECTION, AgoraConstant.CALL_DIRECTION_ROBOT_TO_CONTROLLER);
			this.startActivity(remoteVideoIntent);
			break;
		}
		case R.id.btn_video_para: {
			Intent i = new Intent(this, RemoteVideoParamActivity.class);
			this.startActivity(i);
			break;
		}
		
		case R.id.btn_print: {
			LeoSpeech.speak("打印文字",null);
			//String print_data = mEtprint.getText().toString()+"\n";
			final String print_data = mEtprint.getText().toString();
			/*
			
			LeoRobot.print(GKB_mode);
			try {
			LeoRobot.print(print_data.getBytes("GBK"));
			} catch (Exception e) {
				e.printStackTrace();
			}
			LeoRobot.print(LF);
	*/
			SerialPrinter.getInstance().schedule(new SerialPrinter.PrinterTask() {
				
				@Override
				public void run() {
				//	LeoRobot.print(GKB_mode);
					send(GKB_mode);
					try {
					//LeoRobot.print(print_data.getBytes("GBK"));
					send(print_data.getBytes("GBK"));
					if(SocketThread.baudrate.equals("115200"))
					Thread.sleep(100);
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					//LeoRobot.print(LF);
					send(LF);
				}
			});
			
			break;
		}
		case R.id.btn_print_pic: {
			LeoSpeech.speak("打印图形",null);
			//LeoRobot.print();
			print_pic();
			/*
			new Thread(new Runnable() {
				@Override
				public void run() {
					print_pic();
				}
			}).start();
        	*/
			break;
		}
		case R.id.btn_huanhang: {
			LeoSpeech.speak("换行",null);
		//	LeoRobot.print(LF);
		SerialPrinter.getInstance().schedule(new SerialPrinter.PrinterTask() {
				
				@Override
				public void run() {		
					//LeoRobot.print(LF);
					send(LF);
				}
			});
			break;
		}
		case R.id.btn_cut: {
			LeoSpeech.speak("切纸",null);
		SerialPrinter.getInstance().schedule(new SerialPrinter.PrinterTask() {
				
				@Override
				public void run() {		
					//LeoRobot.print(all_cut);
					send(all_cut);
				}
			});
			//LeoRobot.print(all_cut);
			break;
		}
		case R.id.btn_speak:{
			String word = mEtSpeak.getText().toString();
			LeoSpeech.speak(word, new ISpeakListener() {
				
				@Override
				public void onSpeakOver(int arg0) {
					Log.d("demo", "speak over");
				}
			});
			break;
		}
		case R.id.btn_Pronunciation:{
			setupPronunciation(this);
			break;
		}
		case R.id.btn_nav:{
			LeoRobot.stopmove();
			String word = mEtNav.getText().toString();
			LeoRobot.doNav(getApplicationContext(),word);
			break;
		}
		case R.id.btn_nav2:{
			
			LeoRobot.stopmove();
			String word = mEtNav2.getText().toString();
			LeoRobot.doNav(getApplicationContext(),word);
			
			
			break;
		}
		case R.id.btn_nav_stop1:
		case R.id.btn_nav_stop2:
		{
			LeoSpeech.speak("停止导航",null);
			LeoRobot.stopNav(getApplicationContext());
			break;
		}	
		case R.id.btn_start_roaming:{
			LeoRobot.stopmove();
			LeoRobot.startRoaming();
			break;
		}
		case R.id.btn_face:{
			LeoRobot.stopNav(getApplicationContext());
			
			String word = mEtNav.getText().toString();
			LeoRobot.doFace(getApplicationContext(),word);
			break;
		}
		case R.id.btn_face1:{
			LeoRobot.stopNav(getApplicationContext());
			
			String word = mEtNav2.getText().toString();
			LeoRobot.doFace(getApplicationContext(),word);
			break;
		}
		case R.id.btn_stop_roaming:{
			LeoRobot.stopRoaming();
			break;
		}
		case R.id.btn_forward:{
			LeoSpeech.speak("前进",null);
			LeoRobot.moveforward();
			break;
		}
		case R.id.btn_backward:{
			LeoSpeech.speak("后退",null);
			LeoRobot.movebackward();
			break;
		}
		case R.id.btn_left:{
			LeoSpeech.speak("左转",null);
			LeoRobot.turnLeft();
			break;
		}
		case R.id.btn_right:{
			LeoSpeech.speak("右转",null);
			LeoRobot.turnRight();
			break;
		}
		case R.id.btn_stop:{
			LeoSpeech.speak("停止",null);
			LeoRobot.stopmove();
			break;
		}
		
		case R.id.btn_blue_ok:{
		
			String	address= mEtbluetooth.getText().toString();
			if(!TextUtils.isEmpty(address)){
				SharedPreferences sp = getSharedPreferences("bluetooth",Context.MODE_PRIVATE);
				Editor editor = sp.edit();
				editor.putString("address",address);
				editor.commit();
			}
			break;
		}
		case R.id.btn_rcgn:{
			SpeechTools.speakAndRestartRecognize("开始识别");
			break;
		}
		case R.id.btn_stop_reg:{
			LeoSpeech.speak("停止识别", new ISpeakListener() {
				@Override
				public void onSpeakOver(int arg0) {
					LeoSpeech.stopRecognize();
				}
			});
			break;
		}
		//0  touch  1  untouch  2 identify
		case R.id.btn_touch:{
			LeoSpeech.speak("请插入IC卡", new ISpeakListener() {
				@Override
				public void onSpeakOver(int arg0) {
					LeoRobot.startReadCard(0);
				}
			});
			break;
		}
		case R.id.btn_untouch:{
			LeoSpeech.speak("请刷感应卡", new ISpeakListener() {
				@Override
				public void onSpeakOver(int arg0) {
					LeoRobot.startReadCard(1);
				}
			});
			break;
		}
		case R.id.btn_nav_charge:{
			LeoRobot.stopmove();
			LeoSpeech.speak("导航充电", new ISpeakListener() {
				@Override
				public void onSpeakOver(int arg0) {
					LeoRobot.doNav(getApplicationContext(),"充电区");
				}
			});
			break;
		}
		case R.id.btn_station_charge:{
			
			LeoSpeech.speak("寻找充电桩", new ISpeakListener() {
				@Override
				public void onSpeakOver(int arg0) {
					LeoRobot.search_charge_station();
				}
			});
			break;
		}
		case R.id.btn_stop_charge:{
		
			LeoSpeech.speak("停止充电", new ISpeakListener() {
				@Override
				public void onSpeakOver(int arg0) {
					LeoRobot.stop_charge();
				}
			});
			break;
		}
		case R.id.btn_identy:{
			LeoSpeech.speak("请刷身份证", new ISpeakListener() {
				@Override
				public void onSpeakOver(int arg0) {
					LeoRobot.startReadCard(2);
				}
			});
			break;
		}
		//case R.id.btn_reset: LeoRobot.doReset();break;
		case R.id.btn_mouth_on: LeoRobot.doMouthOn();break;
		case R.id.btn_mouth_off: LeoRobot.doMouthOff();break;
		case R.id.btn_eyes_on: LeoRobot.doEyesOn();break;
		case R.id.btn_eyes_off: LeoRobot.doEyesOff();break;
		/**
		 * 眼灯转圈
		 * direction  方向     0x00：正向 0x01：反向
		 * speed  速度 0x00-0x05速度一次降低
		 */
		case R.id.btn_positive_rotate: LeoRobot.doEyesRotate(0,3);break;
		case R.id.btn_negative_rotate: LeoRobot.doEyesRotate(1,2);break;
		/**
		 * 眨眼
		 * speed  速度 0x00-0x05速度一次降低
		 */
		case R.id.btn_eyes_blink: LeoRobot.doEyesBlink(3);break;
		case R.id.btn_eyes_cancel: LeoRobot.doEyesCancel();break;


		 //   红亮：10  00  00 
		  //  绿亮：00  10  00 
		  //  蓝亮：00  00  10
		  //  默认：0  6  13
		  //   
		case R.id.btn_eyes_red: LeoRobot.doEyesColor(0x10,0,0);break;
		case R.id.btn_eyes_green: LeoRobot.doEyesColor(0,0x10,0);break;
		case R.id.btn_eyes_blue: LeoRobot.doEyesColor(0,0,0x10);break;
		case R.id.btn_eyes_hun: LeoRobot.doEyesColor(16,16,0);break;
		case R.id.btn_eyes_default: LeoRobot.doEyesColor(0,6,13);break;
		case R.id.btn_offset: 
			String engine = mEtengine_id.getText().toString();
			String angle =  mEtangle.getText().toString();
			if(!TextUtils.isEmpty(engine) && !TextUtils.isEmpty(angle)){
				LeoRobot.set_off_set_angle(Integer.parseInt(engine), Integer.parseInt(angle));
			}		
		}
	}
	private BroadcastReceiver mShengWangActivityStartReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(AgoraConstant.ACTION_SHENGWANG_START.equals(action)){
				Intent i = new Intent(MainActivity.this, OpenVCallActivity.class);
				MainActivity.this.startActivity(i);
			}
		}
	};	
	private void sendMessage(final Bitmap sbitmap) {
		
		SerialPrinter.getInstance().schedule(new SerialPrinter.PrinterTask() {
			
			@Override
			public void run() {
				// TODO 自动生成的方法存根
				
				// 发送打印图片前导指令
				BufferedInputStream bis = null;
				try {
					bis = new BufferedInputStream(getAssets()
						      .open("wzy.jpg"));
				} catch (IOException e) {
					e.printStackTrace();
				}
				Log.v(tag,"opened  wzy.jpg");
				Bitmap bitmap = BitmapFactory.decodeStream(bis);
				byte[] start = { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1B,
						0x40, 0x1B, 0x33, 0x00 };
				//LeoRobot.print(start);
				
				//LeoRobot.print(center);
				send(center);
				Log.v(tag,"center");
				byte[] draw2PxPoint = PicFromPrintUtils.draw2PxPoint(this,bitmap);
				Log.v(tag,"start  print  length="+draw2PxPoint.length);
				//LeoRobot.print(draw2PxPoint);
				// 发送结束指令
				Log.v(tag," end print");
				byte[] end = { 0x1d, 0x4c, 0x1f, 0x00 };
				//LeoRobot.print(end);
				//LeoRobot.print(GKB_mode);
		    	//LeoRobot.print(LF);
	            try {
	 	  
	 	            if(SocketThread.baudrate.equals("115200"))
					Thread.sleep(100);
			    	send(LF);
			    	//LeoRobot.print(LF);
					if(SocketThread.baudrate.equals("115200"))
			    	Thread.sleep(100);
			    	send(LF);
			    	//LeoRobot.print(LF);
					if(SocketThread.baudrate.equals("115200"))
			    	Thread.sleep(100);
			    	send(LF);
			    	//LeoRobot.print(LF);
					if(SocketThread.baudrate.equals("115200"))
			    	Thread.sleep(100);
			    	send(LF);
					if(SocketThread.baudrate.equals("115200"))
			    	Thread.sleep(100);
			    	//LeoRobot.print(all_cut);
			    	send(all_cut);
	 	        } catch (Exception e) {
	 				e.printStackTrace();
	 			}

			}
		});
		
	
	}	
	//implent interface IViewUpdater 

		/**
		 * 闲置状态视图
		 */
	    @Override
		public void onIdleState()
		{
	    	mEtStatus.setText("空闲");
	    	mEtStatus.invalidate();
	    	Log.d(tag,"onIdleState");
		}
		
		/**
		 * 准备中状态视图
		 */
	    @Override
		public void onPreparingState(){
	    	mEtStatus.setText("初始化");
	    	mEtStatus.invalidate();
	    	Log.d(tag,"onPreparingState");
		}
		/**
		 * 录音状态视图
		 */
	    @Override
		public void onRecordingState(){
	    	mEtStatus.setText("语音输入中");
	    	mEtStatus.invalidate();
	    	Log.d(tag,"onRecordingState");
		}
		/**
		 * 刷新录音音量
		 * @param vol
		 * 		音量
		 */
	    @Override
		public void onVolumeUpdate(int vol){
			
		}
		/**
		 * 播放控制
		 */
	    @Override
		public void onSpeakUpdate(String state){
			
		}
		/**
		 * 等待识别结果状态视图
		 */
	    @Override
		public void onRecognizingState(){
	    	mEtStatus.setText("正在识别");
	    	mEtStatus.invalidate();
	    	Log.d(tag,"onRecognizingState");
		}
		
		/**
		 * 识别出错状态视图
		 * @param errorCode
		 * 			错误号
		 */
	    @Override
		public void onErrorState(int errorCode){
	    	mEtStatus.setText("识别出错");
	    	mEtStatus.invalidate();
	    	Log.d(tag,"onErrorState");
		}
		
		/**
		 * 设置视图动作监听
		 * @param listener
		 * 			动作监听
		 */
	    @Override
		public void setViewActionListener(IViewActionListener listener){
			
		}
		
		/**
		 * 处理视图操作消息。如添加、删除视图等
		 * @param msg
		 * 		消息
		 */
	    @Override
		public void procViewOperation(Message msg){
			
		}
		private static void setupPronunciation(Context context){
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
	        builder.setIcon(R.drawable.ic_launcher);
	        builder.setTitle(context.getString(R.string.title_dialog_setup_pronunciation));
	        String[] pronunciationNames = context.getResources().getStringArray(R.array.pronunciation_name);
	        final String[] pronunciationIndexs = context.getResources().getStringArray(R.array.pronunciation_value);
	        builder.setItems(pronunciationNames, new DialogInterface.OnClickListener()
	        {
	            @Override
	            public void onClick(DialogInterface dialog, int which)
	            {
	            	String pronunciationIndex = pronunciationIndexs[which];
	        		LeoRobot.updatePronunciation(pronunciationIndex);
	            }
	        });
	        builder.show();
		}
		/**
		 * 显示正在初始化引擎页面
		 */
	    @Override
		public void showInitView(){
			
		}
		/**
		 * 设置当前按钮
		 */
	    @Override
		public void setImageButton(ImageButton btn){
			
		}
	    @Override
		public void onDestroy() {
	    	stopService(new Intent(this,ControlService.class));
	    	LeoSpeech.release();
	    	super.onDestroy();
	    	
	    }
	    private void print_pic(){
			Log.v(tag,"hit btn_print_pic");
			sendMessage(null);

	    }
	    public static byte[] hex2Bytes(String src){  
	        byte[] res = new byte[src.length()/2];        
	        char[] chs = src.toCharArray();  
	        for(int i=0,c=0; i<chs.length; i+=2,c++){  
	            res[c] = (byte) (Integer.parseInt(new String(chs,i,2), 16));  
	        }  
	          
	        return res;  
	    }  	  	    
}
