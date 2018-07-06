package com.leotech.actioncontroller;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.leotech.actioncontroller.R;

public class VideoActivity extends Activity {

	private VideoView mVideoView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_video);
		Log.v("gaowenwen","VideoActivity.onCreate()");
		mVideoView = (VideoView)findViewById(R.id.video1);
		mVideoView.setMediaController(new MediaController(this));
		Uri videoUri = getIntent().getData();
		mVideoView.setVideoURI(videoUri);
		mVideoView.start();
		IntentFilter filter = new IntentFilter();
		filter.addAction("close_video");
		registerReceiver(receiver,filter);
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context arg0, Intent intent) {
			// TODO 自动生成的方法存根
			if(intent.getAction().equals("close_video")){
				Log.v("gaowenwen","finish videoActivity");
				finish();
			}
			
		}
		
	};


}
