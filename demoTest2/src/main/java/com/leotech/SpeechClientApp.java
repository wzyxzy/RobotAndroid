package com.leotech;

import io.yunba.android.manager.YunBaManager;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Stack;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.xmlpull.v1.XmlPullParser;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Xml;
import android.widget.Toast;

import com.leo.api.LeoRobot;
import com.leo.api.LeoSpeech;
import com.leo.api.abstracts.ISpeakListener;
import com.leo.api.bluetooth.LeoBTDeviceController;
import com.leo.api.cagome.stripcard.LeoDriveTool;
import com.leo.api.control.ControlService;
import com.leo.api.control.agora.AgoraConstant;
import com.leo.api.control.music.MusicPlayer;
import com.leo.api.control.net.IpAndMac;
import com.leo.api.control.net.Letter;
import com.leo.api.control.net.LetterType;
import com.leo.api.control.net.RemoteSendThread;
import com.leo.api.control.promptWord.PromptParseFactory;
import com.leo.api.control.wifi.WifiChangeReceive;
import com.leo.api.control.yb.YunBaMsg;
import com.leo.api.nav.BrandItem;
import com.leo.api.nav.NavBrandParser;
import com.leo.api.serialport.PackageUtils;
import com.leo.api.socket.EyeThread;
import com.leo.api.socket.SimpleCommand;
import com.leo.api.socket.SocketThread;
import com.leo.api.util.NetUtil;
import com.leotech.business.ResultProcessor;
import com.leotech.print.HexDump;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;
//add by lihui for high temp
/**
 * Application类
 * @author ydshu
 *
 */
public class SpeechClientApp extends Application{
	private final static String TAG = SpeechClientApp.class.getSimpleName();

	String SETTING_PATH = "mnt/sdcard/leotech/settings/";
	/** 配置文件文件名 */
	private final static String SHARED_PREFERENCES_NAME = "com.iflytek.speechclient";
	private final static String SHARED_PREFERENCES_INTRODUCE_MUSIC = "introduce_path";
	private final static String SHARED_PREFERENCES_INTRODUCE_ACTION = "introduce_action";
	/** 参数存取句柄 */
	private static SharedPreferences mPreferences;
	/** 网络工具类对象 */
	private static NetUtil mNetUtil;
	
	private static MUncaughtExceptionHandler uncaughtExceptionHandler;
	
	//路径
	public  static String PicCacheDir = "/wjb/cache/pics"; //图片缓存路径
	public static String ApkCacheDir = "/wjb/apks";   //apk 存储路径
	public static String printer_string = null;
	
	public static  Context navContext;
	public static  boolean ifinvideo = false;
	public static  boolean voiceNav = false;
	public static  boolean exit_from_VideoActivity = false;
	
	//广播
	public static String GetServerIPReady = "com.leotech.leocontrollerclient.getserveripready";  //获取server ip
	
	public static boolean isConnected = false;       //连接是否可用
	public static String ServerIP = null;             //server ip
	public static String remoteIP="255.255.255.255";
	public static WifiP2pManager mManager = null;
	public static Channel mChannel = null;
	private static DeviceUuidFactory uuidFactory;
	private static boolean inAction = false;
		
	public static int myintvalue = 0;           //支持传参数。````````````
	public static boolean isself = false;   // 20150925 add for 自定义
	
	public static int rtpinclue = 0;
	private BluetoothAdapter btAdapter;
	public static String speakText = "";
//add by lihui for tcp
	public static String clientIP = "";
	public static RecieveTcpThread mRecieveTcpThread = null;
//add end
//add by lihui for high temp
	public static boolean inclass = false;
	public static int  repeat_count = 0;
	public static int  low_battery_count = 0;
	public static int  high_battery_count = 0;
	public final static int  battery_change_count = 20;
	public final static int  low_battery_level = 80;
	public static int  battery_level = 90;
	public static boolean  low_battery = false;
	public static boolean incharge = false;
	public static boolean inchargeactivity = false;
	public static boolean inNavactivity = false;
	public static boolean inMain = false;
	public static boolean enter_money_class = false;
	public static boolean speechidle = false;
	public static boolean  full_battery = false;
	public final static int   high_max_count = 2400;//2400*3/3600=2hour
	public final static int   full_max_count = 1200;//1 hour
	public static int  high_battery_level_count = 0;
	public static int  full_battery_level_count = 0;
	public static boolean  hasface = false;
	public static boolean  hasface_last = false;
	public static boolean  detect_face = false;
	public static boolean  is_speaking = false;
	public static boolean  infirst_activity = false;
	public static boolean  roaming = false;
	public static boolean  roaming_from_detail = false;
	public static boolean  intruduce = false;
	public static boolean  open_camera= true;
	public static boolean  searching_face = false;
	public static boolean  simu_printer = false;
	public static boolean  qianduo = false;
	public static boolean  bluetooth_state = false;//bluetooth connnect state
	public static boolean sound_tip = false;
	public static int  simu_a = 1;
	public static int  simu_c = 1;
	public static int  simu_v = 1;
	public static Context  first_context = null;
	public static ResultProcessor mResultProcessor = null;
	//当处于唱歌或者讲故事模式时，停止repeatCount计数
	private static boolean isStopCount = false;
	private static String   cmd;
	private static final int MSG_RECEIV_SOCKE_PACKET = 121;
	private static final int MSG_OPEN_MIC_WAKE           = 122;
	private static final int MSG_ENABLE_MIC_DIRECT     = 123;
	private static final int MSG_RELEASE_HEAD_POWER = 124;
	private static final int MSG_XUNFI_WAKEUP = 125;
	public static final String START_NEW_ACTIVITY="start.new.activity";
	public static  int  boxcommuncation = 2; ///add for this。 boxcommuncation    1. only com  2.com  replace net  3. net
	public static int boxserial = 3;//////////Hex////////////need confgure with  mSocketConfigArray
	public static  int  boxchangsha = 0;
	
	public static final int IDENTIFY = 250;
	public static final int UNTOUCH_CARD           = 251;
	public static final int TOUCH_CARD     = 252;
	public static final int MAGCARD = 253;
	public static final int INIT_XUTILS = 254;
//add end
	//remember introduce path that set by user
	private static String mIntroducePath;
	private static String mIntroduceAction;
	private WifiChangeReceive mWifiChangeReceive;
	
	private static boolean canStartRecognize = false;//硬件命令词
	
	public static Drawable myBackground = null;
	public static Drawable myBackgroundFirst = null;
	public static Drawable myBackgroundFirst1 = null;
	public static Drawable myIntruduce = null;
	public static String myWordWelcome = null;
	public static String myWordWelcomeface = null;
	public static String myActionWelcome = null;
	public static String myActionWelcomeface = null;
	public static String myAttendance = null;
	public static String myAttendanceface = null;
	public static String myActionAttendance = null;
	public static String myActionAttendanceface = null;
	public static String myMale = null;
	public static String myFemal = null;
	public static String myAppid = null;
	public static String mySecret = null;
	public static String myUrl = null;
	public static String myWeb = null;
	public static String myBusiness = null;
	
	public static int myfacenext = 2;  //0  do nothing  1 only face enter  next  2 all enter
	public static String myname = null;
	public static int mywelcomefaceCust = 0;  //0  no cunst  1 cust:only say the name
	public static int myrecgcount = 5;  //0  
	public static ArrayList<BrandItem> parsedata = new ArrayList<BrandItem>();
	public static ArrayList<QaItem> qadata = new ArrayList<QaItem>();
	public static ArrayList<SentenceItem> sentencedata = new ArrayList<SentenceItem>();
	public static int alarmCount = 0;
	public FaceDB mFaceDB;
	@Override
	public void onCreate(){
		super.onCreate();
		//mFaceDB = new FaceDB(this.getExternalCacheDir().getPath());
		mFaceDB = new FaceDB(PackageUtils.PATH_LEOTECH_FACE);
		mFaceDB.loadFaces();
		/** 生成设备唯一id */
		uuidFactory=new DeviceUuidFactory(getApplicationContext());
		
		
		/** 初始化参数存取句柄 */
		mPreferences = this.getSharedPreferences(SHARED_PREFERENCES_NAME, MODE_PRIVATE);
		
		//yunba init
		initYunBa();

		/** 创建网络工具实例 */
		mNetUtil = NetUtil.getInstance(this);
		
		navContext = getApplicationContext();
		
		/**创建全局异常处理   */
		uncaughtExceptionHandler=MUncaughtExceptionHandler.createInstance(this);
		uncaughtExceptionHandler.init();
		//解析提示语
		PromptParseFactory.parsePromptWord(this);
		//add lihui for socket
		SimpleCommand.initSocket();
		for(int i=0; i < SimpleCommand.max_port_count; i++){
			
			if (boxserial != i){
				new ReadThread(i).start();
			}
		}
		//add end
		handler.sendEmptyMessageDelayed(INIT_XUTILS, 3000);
		mWifiChangeReceive = new WifiChangeReceive(navContext);
		IntentFilter wifi_filter = new IntentFilter();
		wifi_filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		wifi_filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		wifi_filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		wifi_filter.addAction("com.android.interrupt");
		registerReceiver(mWifiChangeReceive,wifi_filter);
		if(mWifiChangeReceive.is_data_connected()==true)
		{
			mWifiChangeReceive.setWifiApEnabled(true);
		}
		instance = this;
		//org.xutils.x.Ext.init(this);
		getDIYItems();
		initNavData();
		initQaData();
		initSentenceData();
		//初始化蓝牙
		btAdapter = BluetoothAdapter.getDefaultAdapter();
		initBluetooth();
		IntentFilter swFilter = new IntentFilter();
		swFilter.addAction(AgoraConstant.ACTION_SHENGWANG_START);
		registerReceiver(mShengWangActivityStartReceiver, swFilter);
		startService(new Intent(this,ControlService.class));
		if(btAdapter==null){
			Toast.makeText(this, "btAdapter is null...", Toast.LENGTH_LONG).show();
		}else{
			if(!btAdapter.isEnabled()){
				Log.v("gaowenwen","btAdapter enalbe");
				btAdapter.enable();
			}
		}
	}
	
	private void getDIYItems(){
		//解析背景图片
		try {
			myBackground = Drawable.createFromPath(SETTING_PATH+"background.png");
			myBackgroundFirst = Drawable.createFromPath(SETTING_PATH+"background_first.png");
			myBackgroundFirst1 = Drawable.createFromPath(SETTING_PATH+"background_first1.png");
		} catch (Exception e) {
			e.printStackTrace();
		}
		//解析提示词
		File file = new File(SETTING_PATH, "settings.xml");
		if(!file.exists()) return;
		InputStream in = null;
		try {
			in = new FileInputStream(file);		
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(in, "utf-8");
			int type = parser.getEventType();
			while (type != XmlPullParser.END_DOCUMENT) {
				switch (type) {
				case XmlPullParser.START_TAG:
					if ("welcome".equals(parser.getName())) {
						myWordWelcome = parser.nextText().trim();
					}else if ("welcomeface".equals(parser.getName())) {
						myWordWelcomeface = parser.nextText().trim();
					}else if ("myname".equals(parser.getName())) {
						myname = parser.nextText().trim();
					}else if ("welcomeaction".equals(parser.getName())) {
						myActionWelcome = parser.nextText().trim();
					}else if ("welcomefaceaction".equals(parser.getName())) {
						myActionWelcomeface = parser.nextText().trim();
					}else if ("facenext".equals(parser.getName())) {
						String s = parser.nextText().trim();
						Log.d("settings","facenext ="+s);
						if(s!=null)
						{
							try { 
							     myfacenext = Integer.parseInt(s); 
							} catch (NumberFormatException e) { 
							    e.printStackTrace(); 
							}
						}
					}else if ("attendance".equals(parser.getName())) {
						myAttendance = parser.nextText().trim();
					}else if ("attendanceface".equals(parser.getName())) {
						myAttendanceface = parser.nextText().trim();
					}else if ("attendanceaction".equals(parser.getName())) {
						myActionAttendance = parser.nextText().trim();
					}else if ("attendancefaceaction".equals(parser.getName())) {
						myActionAttendanceface = parser.nextText().trim();
					}else if ("male".equals(parser.getName())) {
						myMale = parser.nextText().trim();
					}else if ("female".equals(parser.getName())) {
						myFemal = parser.nextText().trim();
					}else if ("welcomefaceCust".equals(parser.getName())) {
						String s = parser.nextText().trim();
						Log.d("settings","welcomefaceCust ="+s);
						if(s!=null)
						{
							try { 
							     mywelcomefaceCust = Integer.parseInt(s); 
							} catch (NumberFormatException e) { 
							    e.printStackTrace(); 
							}
						}
					}else if ("recgcount".equals(parser.getName())) {
						String s = parser.nextText().trim();
						Log.d("settings","recgcount ="+s);
						if(s!=null)
						{
							try { 
							     myrecgcount = Integer.parseInt(s); 
							} catch (NumberFormatException e) { 
							    e.printStackTrace(); 
							}
						}
					}else if ("turingappid".equals(parser.getName())) {
						myAppid = parser.nextText().trim();
						Log.d("settings","turingappid ="+myAppid);
					}else if ("turingsecret".equals(parser.getName())) {
						mySecret = parser.nextText().trim();
						Log.d("settings","turingsecret ="+mySecret);
					}else if ("buttonweb".equals(parser.getName())) {
						myWeb = parser.nextText().trim();
						Log.d("settings","myWeb ="+myWeb);
					}else if ("buttonbusiness".equals(parser.getName())) {
						myBusiness = parser.nextText().trim();
						Log.d("settings","myBusiness ="+myBusiness);
					}
					
					break;
				}
				type = parser.next();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (in != null) {
				try {
					in.close();
					in = null;
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
    //add lihui for socket
	public  class ReadThread extends Thread{
		
		private int mIndex;
		public ReadThread(int index){
			super();
			mIndex = index;
		}
		
		@Override
		public void run(){
			byte[] buffer = new byte[512];
			int len = 0;
			while(true){
				
				try {
					len = SimpleCommand.getInputStream(mIndex).read(buffer);
				} catch (Exception e) {}
				if(len > 0) {
					
					byte[] content  = Arrays.copyOf(buffer, len);

					Message msg = handler.obtainMessage(MSG_RECEIV_SOCKE_PACKET, mIndex, len, content);
					// public final Message obtainMessage(int what, int arg1, int arg2, Object obj)
					handler.sendMessage(msg);
					
					//handler.obtainMessage(1, 0, len, buffer).s ;
				}
//				Log.d("lihui", "Got msg len="+ len + ", coming from index=" + mIndex);
				
			}
		}
	}
	private void analysis_battery(int battery)
	{
		int battery_offset = SystemProperties.getInt("persist.battery.offset",0);
		int mbattery = battery_offset + battery;
		if(incharge)
		{
			if(battery == 100)
				full_battery_level_count++;
			else if(battery >= 96)
				high_battery_level_count++;
		}
		//65--80(low battery)-----100   (x-65)/(100-65)=?   
		if(low_battery == false)
		{
			if(mbattery >= low_battery_level)
			{
				low_battery_count = 0;
			}
			else
			{
				low_battery_count++;
				high_battery_count = 0;
			}
		}else if(low_battery == true){

			if(mbattery >= low_battery_level)
			{
				low_battery_count = 0;
				high_battery_count++;
			}
			else
			{
				high_battery_count = 0;
			}
		
		}
		/*
		 * 100-----90-------80---------65
		 *      60      25       15
		 * 
		 */
		int cap = 0;
		if( mbattery >= 97){
			cap = 100;
		}
		else if( mbattery >= 90){
			cap = 15+35+ (mbattery-90)*7;
		}else if(mbattery >= 80){
			if(mbattery ==89)
				cap = 48;
			else
				cap = 15+ (mbattery-80)*4;
		}
		else if(mbattery >= 65){
			cap = mbattery-65;
		}else{
			cap = 0;
		}
		battery_level = cap;
		//int cap= ((mbattery-65)*100)/(100-65);
		Log.d("lihui", " battery ="+battery+"  offset ="+battery_offset+" final battery ="+mbattery+ "   high_battery_count = "+high_battery_count+
		"   capacity = "+cap+  "   low_battery_count = "+low_battery_count+  "   low_battery = "+low_battery);
	}
    private Handler handler=new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
    		case INIT_XUTILS:
    			Log.d("lihui","org.xutils.x.Ext.init");
    			org.xutils.x.Ext.init(SpeechClientApp.this);
			LeoDriveTool.init(SpeechClientApp.this);
    			SimpleCommand.open_mic_wakeup();
    			SimpleCommand.xunfei_mic_wakeup();
    			break;
//add lihui for socket
			case MSG_OPEN_MIC_WAKE:
				//SimpleCommand.open_mic_wakeup();
				if(canStartRecognize){
					Log.d("leo_4", "SpeechClientApp: MSG_OPEN_MIC_WAKE speak breaken down and start recognize");
					//Toast.makeText(getApplicationContext(), "收到 里奥里奥 了", Toast.LENGTH_SHORT).show();
					MusicPlayer.getInstance(getApplicationContext()).stopAndReleaseMusic();
					//MediaResourcePlayer.resetType();
					LeoSpeech.stopSpeak();
					SpeechTools.startRecognize();
				}
				break;
			case MSG_XUNFI_WAKEUP:
				//if((SpeechClientApp.infirst_activity != true)&&(SpeechClientApp.inMain != true))
				//	return;
				if((inNavactivity||inchargeactivity)||(SpeechClientApp.inMain == true && SpeechClientApp.speechidle == false))
				{
					//正在语音对话时不唤醒
					return;
				}
					SpeechClientApp.repeat_count = 0;							
					SimpleCommand.stopAction();
					SpeechClientApp.mResultProcessor.reset_business();
					LeoSpeech.speak("在呢", new ISpeakListener(){
						@Override
						public void onSpeakOver(int errorCode) {

							if(SpeechClientApp.inMain == false)
							{
								
								Intent mintent = new Intent(getApplicationContext(),SpeechMainActivity.class);
								mintent.putExtra("wakeup", "wakeup");
								mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								startActivity(mintent);
							}
							else
							{
								SpeechTools.stopRecognize();
								SpeechTools.startRecognize();
							}
						}
						
					});	
				//}
				break;
				
			case MSG_RELEASE_HEAD_POWER:
				SimpleCommand.resetHead();
				break;
			case MSG_ENABLE_MIC_DIRECT:
				Log.d("lihui", "write  mic module="+cmd+".");
				try {
					//SimpleCommand.getOutputStream(0).write(cmd.getBytes());
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				LeoSpeech.speak("在呢，有什么吩咐？", new ISpeakListener(){
					@Override
					public void onSpeakOver(int errorCode) {
						Intent mintent = new Intent(getApplicationContext(),SpeechMainActivity.class);
						mintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(mintent);
					}
					
				});	
				break;
			case MSG_RECEIV_SOCKE_PACKET:
				String content = HexDump.toHexString((byte[])msg.obj);
				String message = "receive msg form socket_" + msg.arg1 +"  content=" +  content;
				int len = (int)msg.arg2;
				Log.d("lihui", message);

				//HMA\twake\tntf\t0\t\t 
				//HMA\twake_dir\tntf\t90\t\t 
				if(msg.arg1==0)//mic array
				{
					String s1 = HexDump.toHexString(("HMA	wake	ntf	").getBytes());
					String s2 = HexDump.toHexString(("HMA	wake_dir").getBytes());
					String s3 = HexDump.toHexString(("HMA	wake	ntf	0		HMA	wake_dir	ntf	").getBytes());
					Log.d("lihui", "s1="+s1);
					Log.d("lihui", "s2="+s2);
					Log.d("lihui", "s3="+s3);
					String contentStr = new String((byte[])msg.obj);
					Log.d("leo_4", "SpeechClientApp: received xunfi mic message, content = " + contentStr);
					
					if(contentStr.contains("WAKE UP!angle:"))
					{
						handler.sendEmptyMessageDelayed(MSG_XUNFI_WAKEUP,100);
						return;
					}
					
				/*	if(content.contains("wake_dir"))
						Log.d("lihui", "contains wake_dir");
					else
						Log.d("lihui", "not   contains wake_dir");
					*/
					if(content.startsWith(s1))
					{
						Log.d("lihui", "len="+msg.arg2 +"s3="+ s3.length());
						if(msg.arg2*2 > s3.length())
						{
							Log.d("lihui", "two sentence");
								int s = 0;
								Log.d("lihui", "receive rsp from mic module2");
								if(((byte[])msg.obj)[34]==0x9)
								{
									s=((byte[])msg.obj)[33]-0x30;
								}
								else if(((byte[])msg.obj)[35]==0x9)
								{
									s=(((byte[])msg.obj)[33]-0x30)*10+((byte[])msg.obj)[34]-0x30;
								}
								else if(((byte[])msg.obj)[36]==0x9)
								{
									s=(((byte[])msg.obj)[33]-0x30)*100+(((byte[])msg.obj)[34]-0x30)*10+((byte[])msg.obj)[35]-0x30;
								}
								//SimpleCommand.turnHead(180-s);
								SimpleCommand.turnHead(s);
								//HMA	vqe_dir	set	90		
								Log.d("lihui", "receive reg from mic module "+s);
								cmd = "HMA	vqe_dir	set	"+s+"		";	
								Toast.makeText(getApplicationContext(), "angle = "+s, Toast.LENGTH_SHORT).show();
								Log.d("leo_4", "SpeechClientApp: receive HMA	wake	ntf	, > s3");
								SpeechClientApp.repeat_count = 0;
								handler.sendEmptyMessageDelayed(MSG_OPEN_MIC_WAKE,100);
								handler.sendEmptyMessageDelayed(MSG_ENABLE_MIC_DIRECT,200);
								//handler.sendEmptyMessageDelayed(MSG_RELEASE_HEAD_POWER,2500);

						}
						else 
						{
							//Log.d("leo_4", "SpeechClientApp: receive jie tong message, content = " + contentStr);
							SpeechClientApp.repeat_count = 0;
							handler.sendEmptyMessageDelayed(MSG_OPEN_MIC_WAKE,100);
						}

					}
					else if(content.startsWith(s2))
					{
//						Log.d("lihui", "one sentence");
//						int s = 0;
//						Log.d("lihui", "receive rsp from mic module2");
//						if(((byte[])msg.obj)[34-16]==0x9)
//						{
//							s=((byte[])msg.obj)[33-16]-0x30;
//						}
//						else if(((byte[])msg.obj)[35-16]==0x9)
//						{
//							s=(((byte[])msg.obj)[33-16]-0x30)*10+((byte[])msg.obj)[34-16]-0x30;
//						}
//						else if(((byte[])msg.obj)[36-16]==0x9)
//						{
//							s=(((byte[])msg.obj)[33-16]-0x30)*100+(((byte[])msg.obj)[34-16]-0x30)*10+((byte[])msg.obj)[35-16]-0x30;
//						}
//						SimpleCommand.turnHead(180-s);
//						//HMA	vqe_dir	set	90		
//						Log.d("lihui", "receive reg from mic module "+s);
//						cmd = "HMA	vqe_dir	set	"+s+"		";	
//						//Toast.makeText(getApplicationContext(), "angle = "+s, Toast.LENGTH_SHORT).show();
						Log.d("leo_4", "SpeechClientApp: receive s2");
						SpeechClientApp.repeat_count = 0;
						handler.sendEmptyMessageDelayed(MSG_OPEN_MIC_WAKE,100);
						handler.sendEmptyMessageDelayed(MSG_ENABLE_MIC_DIRECT,200);
						//handler.sendEmptyMessageDelayed(MSG_RELEASE_HEAD_POWER,2500);
					}
				}
				//Message msg = handler.obtainMessage(MSG_RECEIV_SOCKE_PACKET, mIndex, len, content);
				// public final Message obtainMessage(int what, int arg1, int arg2, Object obj)
				else if(msg.arg1==4)//distance sensor
				{
					if(low_battery_count > battery_change_count)
					{
						low_battery = true ;
						low_battery_count = 0;
						EyeThread.set_eye_color(16,16,0);
					
						LeoSpeech.stopRecognize();
						LeoSpeech.stopSpeak();
						SpeechClientApp.intruduce = false;
						SpeechClientApp.roaming_from_detail = false;	
						LeoSpeech.speak("能量不足，已经没有力气做动作了，我要去充电了", new ISpeakListener(){
							@Override
							public void onSpeakOver(int errorCode) {
								if(SpeechClientApp.inNavactivity)
								{
									getApplicationContext().sendBroadcast(new Intent(ChargeActivity.GOTO_WORK));
								}	
								if(SpeechClientApp.inchargeactivity==false)
								{
									getApplicationContext().startActivity(new Intent(getApplicationContext(),  ChargeActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
								}				

							}
							
						});	
						//notify leoNav
						//notify eye
						//notify alarm
						//voice
					}else if(high_battery_count > battery_change_count)
					{
						low_battery = false;
						high_battery_count = 0;
						EyeThread.set_eye_color(0,6,13);
						LeoSpeech.stopRecognize();
						LeoSpeech.speak("我又恢复了一些能量，有力气做动作了！", new ISpeakListener(){
							@Override
							public void onSpeakOver(int errorCode) {
								
								// TODO 自动生成的方法存根
/*										Intent intent = new Intent(first_context,SpeechMainActivity.class);
								first_context.startActivity(intent);*/
							}
							
						});	
						//notify leoNav
						//notify eye
						//notify alarm
						//voice
					}
					else if((high_battery_level_count > high_max_count || full_battery_level_count > full_max_count) && incharge)
					{
						EyeThread.set_eye_color(0,6,13);
						high_battery_level_count = 0;
						full_battery_level_count = 0;
						incharge = false;
						getCurrentActivity().sendBroadcast(new Intent(ChargeActivity.BATTERY_FULL));
						LeoSpeech.speak("电量已经充满", new ISpeakListener(){
							@Override
							public void onSpeakOver(int errorCode) {
								getCurrentActivity().sendBroadcast(new Intent(ChargeActivity.BATTERY_FULL));
							}
							
						});	

					}						
					byte[] s = (byte[])msg.obj;
					//丢弃距离为0的值，丢弃电量为0xff的值
					int SubCmd =s[0] & 0xff;
					if(SubCmd == 0x01)
					{
						int mdistance =s[1] & 0xff;
						int mbattery  =s[2] & 0xff;
						String text;
						String mbatterylevel;
						
						if(mdistance!=0)
						{
							if(mdistance == 255)
							{
								SpeechClientApp.hasface = false;
							}
							else
							{
								SpeechClientApp.hasface = true;
								Log.d("lihui", "distance="+mdistance);
								
								//if((SpeechClientApp.hasface_last == false)
								//		&&(SpeechClientApp.detect_face == true)
								//		&&(SpeechClientApp.infirst_activity == true))//person entered
								if(false)
								{
									SpeechClientApp.repeat_count = 0;
									Log.d("lihui", "startActivity SpeechMainActivity");
									LeoSpeech.speak("您好，看到您真是太开心了，办理业务请点击屏幕", new ISpeakListener(){
										@Override
										public void onSpeakOver(int errorCode) {
											// TODO 自动生成的方法存根
	/*										Intent intent = new Intent(first_context,SpeechMainActivity.class);
											first_context.startActivity(intent);*/
										}
										
									});	
									
								}
							}
							SpeechClientApp.hasface_last = SpeechClientApp.hasface;
						}
						if(mbattery!=255)
						{
							analysis_battery(mbattery);

						}
						if(len ==8)//两个包，分析第二个content=0128FF0501005905
						{
							int mdistance2 =s[5] & 0xff;
							int mbattery2  =s[6] & 0xff;
							
							if(mdistance2!=0)
							{
								if(mdistance2 == 255)
								{
									SpeechClientApp.hasface = false;
								}
								else
								{
									Log.d("lihui", "distance="+mdistance2);
									SpeechClientApp.hasface = true;
									
								}
								SpeechClientApp.hasface_last = SpeechClientApp.hasface;
							}
							if(mbattery2!=255)
							{
								analysis_battery(mbattery2);

							}	
						}
					}
					else if(SubCmd == 0x02)
					{
						//reset message
						int reset =s[1] & 0xff;
						if(reset == 0)//down
						{
							Log.d("lihui", "receive reset !!");
							Toast.makeText(getApplicationContext(), "receive reset !!", Toast.LENGTH_SHORT).show();
							SpeechClientApp.repeat_count = 0;
							SimpleCommand.stopAction();
							Intent intent = new Intent("reset_key");
							getApplicationContext().sendBroadcast(intent);	
							if(mResultProcessor!=null)
								mResultProcessor.reset();
							SpeechTools.setControlMode(false);//解决摸头后不能启动语音识别问题
							//LeoSpeech.stopSpeak();
							//LeoSpeech.stopRecognize();
						}
						else if(reset == 1)//up
						{
							
						}
						
					}
					else
					{
						int mdistance =s[0] & 0xff;
						int mbattery  =s[1] & 0xff;
						String text;
						String mbatterylevel;
						
						if(mdistance!=0)
						{
							if(mdistance == 255)
							{
								SpeechClientApp.hasface = false;
							}
							else
							{
								SpeechClientApp.hasface = true;
								Log.d("lihui", "distance="+mdistance);
								//if((SpeechClientApp.hasface_last == false)
								//		&&(SpeechClientApp.detect_face == true)
								//		&&(SpeechClientApp.infirst_activity == true))//person entered
								if(false)
								{
									SpeechClientApp.repeat_count = 0;
									Log.d("lihui", "startActivity SpeechMainActivity");
									LeoSpeech.speak("您好，看到您真是太开心了，办理业务请点击屏幕", new ISpeakListener(){
										@Override
										public void onSpeakOver(int errorCode) {
											// TODO 自动生成的方法存根
	/*										Intent intent = new Intent(first_context,SpeechMainActivity.class);
											first_context.startActivity(intent);*/
										}
										
									});	
									
								}
							}
							SpeechClientApp.hasface_last = SpeechClientApp.hasface;
						}
						if(mbattery!=255)
						{
							
							analysis_battery(mbattery);
						}
						if(len ==8)//两个包，分析第二个content=0128FF0501005905
						{
							int mdistance2 =s[5] & 0xff;
							int mbattery2  =s[6] & 0xff;
							
							if(mdistance2!=0)
							{
								if(mdistance2 == 255)
								{
									SpeechClientApp.hasface = false;
								}
								else
								{
									Log.d("lihui", "distance="+mdistance2);
									SpeechClientApp.hasface = true;
									
								}
								SpeechClientApp.hasface_last = SpeechClientApp.hasface;
							}
							if(mbattery2!=255)
							{
								analysis_battery(mbattery2);

							}	
						}
					}					
				}

			break;

			default:
				break;
			}
    	}
    };
	@Override
	public void onTerminate() {
    	    //add lihui for socket
		SimpleCommand.uninitSocket();
		mWifiChangeReceive.release();
		unregisterReceiver(mWifiChangeReceive);	
	    //add end
		unregisterReceiver(mShengWangActivityStartReceiver);
		stopService(new Intent(this,ControlService.class));
		this.deviceController.close();
		if(btAdapter.isEnabled()){
			btAdapter.disable();
		}
		super.onTerminate();
	 
	}


	//获取设备唯一ID
	public static String getUUID(){
		return uuidFactory.getDeviceUuid().toString();
	}

	public static void setIsStopCount(boolean mode){
		isStopCount = mode;
	}

	public static boolean getIsStopCount(){
		return isStopCount;
	}

	public static String getSettingString(String key){
		String ret = mPreferences.getString(key, null);
		return ret;
	}

	public static void setSettingString(String key, String val){
		Editor editor = mPreferences.edit();
		editor.putString(key, val);
		editor.commit();
	}
	
	public static void setIntroduce(String action, String path){
		mIntroduceAction = action;
		mIntroducePath = path;
		setSettingString(SHARED_PREFERENCES_INTRODUCE_MUSIC, path);
		setSettingString(SHARED_PREFERENCES_INTRODUCE_ACTION, action);
		Log.d("wss", "set introduce path="+path+" @@action="+action);
	}
	/**
	 * 
	 * @return true, find path and play; false, path is null.
	 */
	public static boolean playIntroduce(Context context){
		if(mIntroducePath == null){
			mIntroducePath = getSettingString(SHARED_PREFERENCES_INTRODUCE_MUSIC);
		}
		if(mIntroduceAction == null){
			mIntroduceAction = getSettingString(SHARED_PREFERENCES_INTRODUCE_ACTION);
		}
		if(mIntroduceAction == null) return false;
		MusicPlayer.getInstance(context).playMusicAndRestartRecognise(mIntroducePath);
		LeoRobot.doAction(mIntroduceAction);
		return true;
	}
	
	public static void setInAction(boolean inAction){
		SpeechClientApp.inAction = inAction;
		String ip= IpAndMac.getLocalIpv4AddressString().split("\\,")[0];
		String words = (inAction?"actionstart/":"actionstop/")+ip;
		Letter response = new Letter(LetterType.RobotInfo, words);
		RemoteSendThread remoteSendTask = RemoteSendThread.createInstance();
		if (remoteSendTask.isRunning() == false) {
			RemoteSendThread.createInstance().start();
		}
		RemoteSendThread.createInstance().send(response.toString());
	}
	public static boolean isInAction(){
		return inAction;
	}
	//lihui tbd del
	public static void doRadomAction(Context context){
		if(SocketThread.randomaction==0)
		return;
		String[] name = {"left","right"};
		Random rand = new Random();
		int randNum = rand.nextInt(name.length-1);
		Log.v("gaowenwen","randNum="+randNum);
		SimpleCommand.doAction(context, name[randNum]);
		
	}
	//lihui tbd del
	public static void doRadomAction2(Context context){
		if(SocketThread.randomaction2==0)
		return;
		String[] name = {"diantou","baibi","yaotou","taitou"};
		Random rand = new Random();
		int randNum = rand.nextInt(name.length-1);
		Log.v("gaowenwen","randNum="+randNum);
		SimpleCommand.doAction(context, name[randNum]);
		

	}
	
	public static void setCanStartRecognize(boolean can){
		canStartRecognize = can;
	}
	
	/*
	 * start copy
	 * */
	
	public static boolean DEBUG = false;
	
	public static SpeechClientApp instance;
	
	private LoadingDialog loadingDialog;
	
	public String httpip;
	
	public int pdnum;

	
	public static SpeechClientApp getInstance(){
		return instance;
	}
	
	public void setLoadingDialog(LoadingDialog loadingDialog) {
		this.loadingDialog = loadingDialog;
	}

	public void dismissDialog() {
		if (loadingDialog != null)
			loadingDialog.dismiss();
	}
	
	public void setIP(String httpip){
		this.httpip = httpip;
	}
	
	public String getIP(){
		return httpip;
	}
	
	public void setPdNum(int num){
		this.pdnum = num;
	}
	
	public int getPdNum(){
		return pdnum;
	}
	
	private static Stack<Activity> activityStack = new Stack<Activity>();

	public void addActivity(Activity activity){
		activityStack.add(activity);
	}
	
	public void removeActivity(Activity activity){
		activityStack.remove(activity);
	}
	
	public Activity getCurrentActivity(){
		return activityStack.lastElement();
	}
	private void initNavData() {
		// TODO 自动生成的方法存根
		try {
			FileInputStream is = new FileInputStream(new File(
					"/storage/sdcard0/leotech/nav/NavBrand.xml"));
			SpeechClientApp.parsedata.clear();
			SpeechClientApp.parsedata = NavBrandParser.getInstance(this).readQAXml(is);
			Log.v("leo_4","parsedata.isize="+parsedata.size());
		} catch (Exception e) {

		}
		
		for(int i=0;i<SpeechClientApp.parsedata.size();i++){
			Log.v("leo_4","data. name ="+SpeechClientApp.parsedata.get(i).getName());
			
		}
		
	}
	private void initQaData() {
		// TODO 自动生成的方法存根
		try {
			FileInputStream is = new FileInputStream(new File(
					"/storage/sdcard0/leotech/settings/qa.xml"));
			SpeechClientApp.qadata.clear();
			SpeechClientApp.qadata = QaParser.getInstance(this).readQAXml(is);
			Log.v("leo_4","qadata.isize="+qadata.size());
		} catch (Exception e) {

		}
		
	}	
	private void initSentenceData() {
		// TODO 自动生成的方法存根
		try {
			FileInputStream is = new FileInputStream(new File(
					"/storage/sdcard0/leotech/settings/sentence.xml"));
			SpeechClientApp.sentencedata.clear();
			SpeechClientApp.sentencedata = SentenceParser.getInstance(this).readQAXml(is);
			Log.v("leo_4","sentencedata.isize="+sentencedata.size());
		} catch (Exception e) {

		}
		
	}	
	
	private void initYunBa(){
		final String robotAlias = getSettingString(YunBaMsg.ROBOT_ALIAS);
		final String topic = getSettingString(YunBaMsg.TOPIC);
		YunBaManager.start(getApplicationContext());
		
		if(!TextUtils.isEmpty(topic)){
			YunBaManager.subscribe(getApplicationContext(), topic, new IMqttActionListener() {
	        	public void onSuccess(IMqttToken arg0) {
	                Log.d("leo_2", "subscribe topic succeed, topic = " + topic);
	            }

	            @Override
	            public void onFailure(IMqttToken arg0, Throwable arg1) {
	                Log.d("leo_2", "subscribe topic failed");
	            }
			});
		}
		
		if(!TextUtils.isEmpty(robotAlias)){
			YunBaManager.setAlias(getApplicationContext(), robotAlias, new IMqttActionListener() {
	        	public void onSuccess(IMqttToken arg0) {
	                Log.d("leo_2", "setAlias succeed, alias = " + robotAlias);
	            }

	            @Override
	            public void onFailure(IMqttToken arg0, Throwable arg1) {
	                Log.d("leo_2", "setAlias failed");
	            }
			});
		}
	}
	public static String get_goal(String last )
	{
		String[] dests;
		Log.v("intruduce","get_goal last="+last);
		if(last.equals("null"))
			dests= new String[parsedata.size()];
		else
			dests= new String[parsedata.size()-1];
		
		int k = 0;
		for(int i=0; i < parsedata.size(); i++){
			Log.v("intruduce","dest="+i+"="+parsedata.get(i).getName());
			if(parsedata.get(i).getName().equalsIgnoreCase(last))
			{
			}
			else
			{
				dests[k]=parsedata.get(i).getName();
				k++;
			}
		}
		
		//0~dests_count-1
		//(数据类型)(最小值+Math.random()*(最大值-最小值+1))
		int randi =(int)(0+Math.random()*((k)));
		Log.v("intruduce","all_size="+parsedata.size()+"leftsize="+k+" randi="+randi+" dests="+ dests[randi]);
		return dests[randi];		
	}
	public static String get_dest(String result )
	{
		String[] dests;
		Log.v("leo_4","get_dest result="+result);
		for(int i=0; i < SpeechClientApp.parsedata.size(); i++){
			Log.v("leo_4","dest="+i+"="+SpeechClientApp.parsedata.get(i).getName());
			if(result.contains(SpeechClientApp.parsedata.get(i).getName()))
			{
				Log.v("leo_4","get_dest final="+SpeechClientApp.parsedata.get(i).getName());
				return SpeechClientApp.parsedata.get(i).getName();
			}
			
		
		}	
		return null;
	}
	public static String get_lyric(String last)
	{
		String[] dests = new String[parsedata.size()-1];
		int k = 0;
		for(int i=0; i < parsedata.size(); i++){
			if(parsedata.get(i).getName().equalsIgnoreCase(last))
			{
				return  parsedata.get(i).getEndWord();
			}

		}
		return "到达目的地";
	}
	private BluetoothAdapter mBtAdapter;
	private LeoBTDeviceController deviceController = null;

	@SuppressLint("NewApi")
	public void initBluetooth(){
		Log.v("gaowenwen","initBluetooth...........");
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBtAdapter = bluetoothManager.getAdapter();
        deviceController = LeoBTDeviceController.getInstance(this);
        deviceController.initialize();
        //deviceController.setOnControllerUartServiceStateChangeListener(this);
        
        if(deviceController!=null){
    		Log.v("gaowenwen","GetDrivestr connectDevice");
    		String adress="DD:23:53:EE:A5:A0";
    		//deviceController.connectDevice(adress);
        }
	}

	private BroadcastReceiver mShengWangActivityStartReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(AgoraConstant.ACTION_SHENGWANG_START.equals(action)){
				if(SpeechClientApp.infirst_activity == false)
				{
					Intent i = new Intent(navContext, com.control.agora.OpenVCallActivity.class);
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(i);
				}
				else
				{
					Intent intent1=new Intent(SpeechClientApp.START_NEW_ACTIVITY);
					intent1.putExtra("name","OpenVCallActivity" );
					sendBroadcast(intent1);
				}
				
			}
		}
	};
	/**
	 * @param path
	 * @return
	 */
	public static Bitmap decodeImage(String path) {
		Bitmap res;
		try {
			ExifInterface exif = new ExifInterface(path);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

			BitmapFactory.Options op = new BitmapFactory.Options();
			op.inSampleSize = 1;
			op.inJustDecodeBounds = false;
			//op.inMutable = true;
			res = BitmapFactory.decodeFile(path, op);
			//rotate and scale.
			Matrix matrix = new Matrix();

			if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
				matrix.postRotate(90);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
				matrix.postRotate(180);
			} else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
				matrix.postRotate(270);
			}

			Bitmap temp = Bitmap.createBitmap(res, 0, 0, res.getWidth(), res.getHeight(), matrix, true);
			Log.d("com.arcsoft", "check target Image:" + temp.getWidth() + "X" + temp.getHeight());

			if (!temp.equals(res)) {
				res.recycle();
			}
			return temp;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
