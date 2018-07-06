package com.leotech.agora;

import io.agora.rtc.IRtcEngineEventHandler.RtcStats;
import io.agora.rtc.RtcEngine;
import io.yunba.android.manager.YunBaManager;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.leo.api.LeoRobot;
import com.leo.api.LeoSpeech;
import com.leo.api.bluetooth.LeoBTDeviceController;
import com.leo.api.control.agora.AGEventHandler;
import com.leo.api.control.agora.AgoraConstant;
import com.leo.api.control.agora.CurrentUserSettings;
import com.leo.api.control.agora.WorkerThread;
import com.leo.api.control.agora.WorkerThread.WorkThreadInitReadyListener;
import com.leo.api.control.yb.YunBaMsg;
import com.leo.api.socket.SocketThread;
import com.leotech.actioncontroller.R;

public class OpenVCallActivity extends Activity implements View.OnClickListener{
	private final String tag = "leo_2";
	private final String clazzName = OpenVCallActivity.class.getSimpleName();
	private static final Object lock = new Object();
	
	private static boolean isRemoteVideoRunning = false;
	
	private WorkerThread mWorkerThread;
	private static final CurrentUserSettings mVideoSettings = new CurrentUserSettings();
	private FrameLayout vContainer = null;
	private SurfaceView localeView = null;
	private String mAgoraChannel = "";
	private ProcessListener1 pl = new ProcessListener1();
	private boolean isChangeProfile = false;
	private int profile = AgoraConstant.PROFILE_MEDIUM;
	private Button btnPre = null;
	private String mCallDirection = "";
	private int mCurrentSpeakerphoneVolume = 150;
	private final int mVolumeOffset = 25;
	private boolean stopedShengWang = false;
	
	private boolean isExit = false;
	private String where = "user offline callback";

	private BluetoothAdapter mBtAdapter;
	private LeoBTDeviceController deviceController;
	
	private AlertDialog mPromptDialog = null;
	private RelativeLayout mOpenVCalllayout = null;
	
	private Handler agoraInitHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			switch(msg.what){
				case 1:
					initWorkerThread();
					break;
			}
		}
	};
	
	private BroadcastReceiver mStopReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(AgoraConstant.ACTION_SHENGWANG_STOP.equals(action)){
				where = "stop receiver";
				passivelyClose();
			} else if(AgoraConstant.ACTION_CHANGE_PROFILE.equals(action)){
				profile = intent.getIntExtra("profile", AgoraConstant.PROFILE_MEDIUM);
				changeProfile();
			} else if(AgoraConstant.ACTION_CONTROLLER_IS_RUNNING.equals(action)){
				Toast.makeText(OpenVCallActivity.this, "客服正在服务中，请等待客服连接", Toast.LENGTH_LONG).show();
				where = "controller is running receiver";
				OpenVCallActivity.this.passivelyClose();
			}
		}
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(tag, clazzName + ": onCreate()");
		
		this.stopedShengWang = false;
		OpenVCallActivity.setRemoteVideoRunning();
		Window window = getWindow();
		WindowManager.LayoutParams params = window.getAttributes();
		params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
		window.setAttributes(params);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_agora_openvcall);
		this.vContainer = (FrameLayout)findViewById(R.id.activity_openvcall_view_container);
		this.mOpenVCalllayout = (RelativeLayout)findViewById(R.id.activity_openvcall_layout);
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(AgoraConstant.ACTION_SHENGWANG_STOP);
		filter.addAction(AgoraConstant.ACTION_CHANGE_PROFILE);
		filter.addAction(AgoraConstant.ACTION_CONTROLLER_IS_RUNNING);
		this.registerReceiver(mStopReceiver, filter);
		
		this.btnPre = (Button)findViewById(R.id.agora_backPrevious);
		this.btnPre.setOnClickListener(this);
		this.btnPre.setEnabled(true);
		this.findViewById(R.id.addVolume).setOnClickListener(this);
		this.findViewById(R.id.reduceVolume).setOnClickListener(this);
		
		mAgoraChannel = LeoRobot.getSettingString(AgoraConstant.AGORA_CHANNEL_NAME);
		
		Intent intent = this.getIntent();
		mCallDirection = intent.getStringExtra(AgoraConstant.CALL_DIRECTION);
		initBlueDevice();
		initPromptDialog();
		LeoRobot.ifinvideo = true;
		
		//this.initWorkerThread();
		agoraInitHandler.sendEmptyMessageDelayed(1, 2000);
	}
	
	
    @Override
	protected void onDestroy() {
    	Log.d(tag, clazzName + ": onDestroy()");
    	LeoRobot.ifinvideo = false;
		super.onDestroy();
	}


	@SuppressLint("NewApi")
	public void initBlueDevice(){
    	deviceController = LeoBTDeviceController.getInstance(this);
        mBtAdapter = BluetoothAdapter.getDefaultAdapter();	       
        Log.v("gaowenwen","BtAdapter btAdapter="+mBtAdapter);
        if (mBtAdapter == null) {
            Toast.makeText(this, "adapter is null", Toast.LENGTH_LONG).show();
        } else {
        	Log.v("gaowenwen","Btadapter.isEnable="+mBtAdapter.isEnabled());
        	if(!mBtAdapter.isEnabled()){
        		LeoSpeech.speak("打开蓝牙", null);
        		Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        		startActivityForResult(enableIntent, 2);
        	}else{
/*        		Log.v("gaowenwen","startLeScan");
        		mBtAdapter.startLeScan(mLeScanCallback);*/
        		if(!deviceController.hasMatchedBtDevice()){
        			
            		//SharedPreferences sp = getSharedPreferences("bluetooth", Context.MODE_PRIVATE);
            		//String adress = sp.getString("address", null);
            		String adress = SocketThread.bluetooth;
            		Log.v("gaowenwen","connect bluetoot adress="+adress);
            		if(adress!=null&&(adress.length()>1)){
            			deviceController.connectDevice(adress);
            		}
            		
        		}

        	}
        }
    }
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent keyEvent) {
		if(keyCode == KeyEvent.KEYCODE_BACK){
			where = "return button";
			this.forwardlyClose();
			return true;
		}
		return super.onKeyDown(keyCode, keyEvent);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		LeoRobot.ifinvideo = false;
		if(stopedShengWang){
			OpenVCallActivity.setRemoteVideoClosed();
		}
		
		Log.d(tag, clazzName + ": onPause()");
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		LeoRobot.ifinvideo = false;
		Log.d(tag, clazzName + ": onStop()");
	}
	
	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch(id){
			case R.id.agora_backPrevious:
				btnPre.setEnabled(false);
				where = "return button";
				this.forwardlyClose();
				finish();
				break;
			case R.id.addVolume:
				this.changeSpeakerphoneVolume(mVolumeOffset, true);
				break;
			case R.id.reduceVolume:
				this.changeSpeakerphoneVolume(mVolumeOffset, false);
				break;
		}
	}
	
	private void initPromptDialog(){
		mPromptDialog = new AlertDialog.Builder(this).setTitle("问题")
													 .setIcon(android.R.drawable.ic_dialog_alert)
													 .setCancelable(false)
													 .setPositiveButton("确定", new DialogInterface.OnClickListener() {
														@Override
														public void onClick(DialogInterface arg0, int arg1) {
															passivelyClose();
														}
													 }).create();
	}
	
	private synchronized void initWorkerThread() {
        if (mWorkerThread == null) {
            mWorkerThread = new WorkerThread(getApplicationContext(), pl);
            mWorkerThread.start();
        }
    }
	
    private void stopShengWang(){
    	synchronized (lock) {
			if (isExit) {
				return;
			}
			this.isExit = true;
			Log.d(tag, clazzName + ": stopShengWang(), from " + where);
			if (mWorkerThread == null) {
				return;
			}
			this.stopedShengWang = true;
			try {
				this.unregisterReceiver(mStopReceiver);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mWorkerThread.preview(false, null, 0);
			mWorkerThread.leaveChannel(mAgoraChannel);
			this.deInitWorkerThread();
		}
    }

    private void deInitWorkerThread() {
        mWorkerThread.exit();
        try {
            mWorkerThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        mWorkerThread = null;
    }
    
    private void forwardlyClose(){
    	this.sendStopCmd();
    	this.stopShengWang();
    }
    
    private void passivelyClose(){
    	this.stopShengWang();
    }

    private synchronized void changeProfile(){
    	isChangeProfile = true;
		mWorkerThread.changeProfileLeaveChannel();
    }
    
    private void sendCallVideoOpen(){
    	String controllerAlias = LeoRobot.getSettingString(YunBaMsg.CONTROLLER_ALIAS);
    	sendYunBaMsg(controllerAlias, AgoraConstant.VIDEO_OPEN);
    }
    
    private void sendStopCmd(){
    	String controllerAlias = LeoRobot.getSettingString(YunBaMsg.CONTROLLER_ALIAS);
    	sendYunBaMsg(controllerAlias, AgoraConstant.VIDEO_CLOSE);
    }
    
    private void sendYunBaMsg(final String alias, String request){
    	Log.v(tag, clazzName + ": sendYunBaMsg, request=" + request);
    	String robotAlias = LeoRobot.getSettingString(YunBaMsg.ROBOT_ALIAS);
		YunBaMsg ybm = new YunBaMsg(robotAlias, request);
		final String jsonStr = ybm.toJsonStr();
		YunBaManager.getState(getApplicationContext(), alias, new IMqttActionListener() {

			@Override
			public void onSuccess(IMqttToken arg0) {
				JSONObject result = arg0.getResult();
				try {
                    String status = result.getString("status");
                    Log.d(tag, clazzName + ": status = " + status);
                    if("online".equalsIgnoreCase(status)){
                    	JSONObject opts = new JSONObject();
        				opts.put("qos", 1);
        				opts.put("time_to_live", 0);
        				
                    	YunBaManager.publish2ToAlias(getApplicationContext(), alias, jsonStr, opts, null);
                    } else {
                    	runOnUiThread(new Runnable() {
							@Override
							public void run() {
								mPromptDialog.setMessage("客服端不在线，请联系大堂经理，按\"确定\"退出");
		                    	mPromptDialog.show();
							}
						});
                    }
                } catch (JSONException e) {
                	
                }
			}

			@Override
			public void onFailure(IMqttToken arg0, Throwable arg1) {
				Log.d(tag, clazzName + ": getState failure");
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
					if(LeoRobot.ifinvideo)
					{
						mPromptDialog.setMessage("客服端状态错误，请联系大堂经理，按\"确定\"退出");
		            			mPromptDialog.show();
					}
					}
				});
			}
		});
	}
    
    private void changeSpeakerphoneVolume(int volumeOffset, boolean isIncrease){
		if(isIncrease){
			mCurrentSpeakerphoneVolume += volumeOffset;
			if(mCurrentSpeakerphoneVolume >= AgoraConstant.SPEAKERPHONE_VOLUME_MAX){
				mCurrentSpeakerphoneVolume = AgoraConstant.SPEAKERPHONE_VOLUME_MAX;
			}
		} else {
			mCurrentSpeakerphoneVolume -= volumeOffset;
			if(mCurrentSpeakerphoneVolume <= AgoraConstant.SPEAKERPHONE_VOLUME_MIN){
				mCurrentSpeakerphoneVolume = AgoraConstant.SPEAKERPHONE_VOLUME_MIN;
			}
		}
		if(mWorkerThread != null){
			mWorkerThread.setSpeakerphoneVolume(mCurrentSpeakerphoneVolume);
		}
	}
    
    //***********************
    private class ProcessListener1 implements WorkThreadInitReadyListener, AGEventHandler{

		@Override
		public void onFirstRemoteVideoDecoded(int uid, int width, int height, int elapsed) {
			Log.d(tag, clazzName + ": onFirstRemoteVideoDecoded");
			
		}

		@Override
		public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
			Log.d(tag, clazzName + ": into agora channel, channel = " + mAgoraChannel);
			// TODO 改变背景图片
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					mOpenVCalllayout.setBackgroundResource(R.drawable.mic_active);
				}
			});
		}

		@Override
		public void onUserOffline(int uid, int reason) {
			Log.d(tag, clazzName + ": user offline, reason = " + reason);
			passivelyClose();
		}

		@Override
		public void onExtraCallback(int type, Object... data) {
			switch(type){
				case AGEventHandler.EVENT_TYPE_ON_STREAM_MESSAGE:
					/*RequestProcesser processor = RequestProcesser.createInstance();
					try {
						String messageStr = new String((byte[])data[1], "UTF-8");
						processor.process(context, handler, new JSONObject(messageStr));
					} catch (JSONException e) {
						e.printStackTrace();
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}*/
					break;
				case AGEventHandler.EVENT_TYPE_ON_CONNECTION_INTERRUPTED:
					Log.d(tag, clazzName + ": connection interrupted");
					break;
				case AGEventHandler.EVENT_TYPE_ON_CONNECTION_LOST:
					Log.d(tag, clazzName + ": connection lost");
					break;
				default:
					break;
			}
		}
		
		@Override
		public void onLeaveChannel(RtcStats stats) {
			if(isChangeProfile){
				Log.d(tag, clazzName + ": onLeaveChannel(...), change profile");
				isChangeProfile = false;
				mWorkerThread.changeProfile(profile);
			} else {
				Log.d(tag, clazzName + ": onLeaveChannel(...)");
				OpenVCallActivity.this.finish();
			}
		}
		
		@Override
		public void onWarning(int warn, String message){
			Log.d(tag, clazzName + ": onWarning(...), warn = " + warn + ", " + message);
			
		}
		
		@Override
		public void onError(int error, String message){
			Log.d(tag, clazzName + ": onError(...), error = " + error + ", " + message);
			
		}
		
		@Override
		public void onLastmileQuality(int quality){
			Log.d(tag, clazzName + ": onLastmileQuality(...), quality = " + quality);
		}
		
		@Override
		public void onRejoinChannelSuccess(String channel, int uid, int elapsed){
			Log.d(tag, clazzName + ": onRejoinChannelSuccess(...), elapsed = " + elapsed);
		}

		@Override
		public void workThreadReady() {
			mWorkerThread.eventHandler().addEventHandler(this);
			mWorkerThread.configEngine(AgoraConstant.PROFILE_LOW, "", "aes-128-xts");
			localeView = RtcEngine.CreateRendererView(getApplicationContext());
			localeView.setZOrderOnTop(false);
			localeView.setZOrderMediaOverlay(false);
			mWorkerThread.preview(true, localeView, 0);
			mWorkerThread.setSpeakerphoneVolume(mCurrentSpeakerphoneVolume);
			mWorkerThread.joinChannel(mAgoraChannel, 0);
			if(AgoraConstant.CALL_DIRECTION_ROBOT_TO_CONTROLLER.equals(mCallDirection)){
				sendCallVideoOpen();
			}
			
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					vContainer.addView(localeView);
				}
			});
		}
	}

    public static void setRemoteVideoRunning(){
    	OpenVCallActivity.isRemoteVideoRunning = true;
	}
	
	public static void setRemoteVideoClosed(){
		OpenVCallActivity.isRemoteVideoRunning = false;
	}
	
	public static boolean isRemoteVideoRunning(){
		return OpenVCallActivity.isRemoteVideoRunning;
	}
}
