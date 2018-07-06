package com.leotech.agora;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.leo.api.LeoRobot;
import com.leo.api.control.agora.AgoraConstant;
import com.leo.api.control.yb.YunBaMsg;
import com.leotech.actioncontroller.R;

public class RemoteVideoParamActivity extends Activity implements RadioGroup.OnCheckedChangeListener{
	
	private EditText mETRobotAlias = null;
	private EditText mETTopic = null;
	private EditText mETControllerAlias = null;
	private EditText mETChannel = null;
	private RadioGroup mRgAudioRoute = null;
	private RadioButton mRbSpeakerphone = null;
	private RadioButton mRbBluetooth = null;
	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		this.setContentView(R.layout.activity_remote_video_param);
		
		this.mETRobotAlias = (EditText)findViewById(R.id.et_robot_alias);
		this.mETTopic = (EditText)findViewById(R.id.et_topic);
		this.mETControllerAlias = (EditText)findViewById(R.id.et_controller_alias);
		this.mETChannel = (EditText)findViewById(R.id.et_video_channel);
		this.mRgAudioRoute = (RadioGroup)findViewById(R.id.rg_audio_route);
		this.mRbSpeakerphone = (RadioButton)findViewById(R.id.rb_speaker_phone);
		this.mRbBluetooth = (RadioButton)findViewById(R.id.rb_blue_tooth);
		
		this.mRgAudioRoute.setOnCheckedChangeListener(this);
		
		this.fixParams();
		this.initRgAudioRoute();
	}
	
	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch(checkedId){
		case R.id.rb_speaker_phone:
			LeoRobot.setSettingString(AgoraConstant.AUDIO_ROUTE, AgoraConstant.AUDIO_ROUTE_SPEAKERPHONE);
			break;
		case R.id.rb_blue_tooth:
			LeoRobot.setSettingString(AgoraConstant.AUDIO_ROUTE, AgoraConstant.AUDIO_ROUTE_BLUETOOTH);
			break;
		}
	}
	
	private void fixParams(){
		String robotAlias = LeoRobot.getSettingString(YunBaMsg.ROBOT_ALIAS);
		String topic = LeoRobot.getSettingString(YunBaMsg.TOPIC);
		String controllerAlias = LeoRobot.getSettingString(YunBaMsg.CONTROLLER_ALIAS);
		String channel = LeoRobot.getSettingString(AgoraConstant.AGORA_CHANNEL_NAME);
		
		this.mETRobotAlias.setText(robotAlias);
		this.mETTopic.setText(topic);
		this.mETControllerAlias.setText(controllerAlias);
		this.mETChannel.setText(channel);
	}

	private void initRgAudioRoute(){
		String audioRoute = LeoRobot.getSettingString(AgoraConstant.AUDIO_ROUTE);
		if(AgoraConstant.AUDIO_ROUTE_BLUETOOTH.equals(audioRoute)){
			mRbBluetooth.setChecked(true);
			mRbSpeakerphone.setChecked(false);
		} else {
			mRbBluetooth.setChecked(false);
			mRbSpeakerphone.setChecked(true);
		}
	}
}
