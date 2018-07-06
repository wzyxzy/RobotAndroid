package com.leotech.business;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

//import com.iflytek.speechclient.R;
import com.leo.api.LeoSpeech;
import com.leo.api.abstracts.ISpeakListener;
import com.leo.api.control.music.MediaResourcePlayer;
import com.leo.api.control.music.MusicPlayer;
import com.leo.api.control.music.OnMediaEndListener;
import com.leotech.SpeechClientApp;
import com.leotech.SpeechTools;
import com.leotech.actioncontroller.R;

public class PoetryBusiness extends IBusiness {
	
	private final String tag = "SongsBusiness";
	private static Object obj = new Object();
	private final int second = 0;
	private final long intervalTime = second * 1000;
	
	private PoetryEndListener mPoetrySpeakListener = null;
	private boolean isPlaying = false;
	private PoetriesIntervalThread mIntervalThread = null;
	private PoetryEndThread mPoetryEndThread = null;
	
	//private String songMsgStart = null;
	private String poetryMsgExit = null;
	//private String promptMsg = null;
	
	public PoetryBusiness(Context context) {
		super(context);
		mPoetrySpeakListener = new PoetryEndListener();
		SpeechClientApp.setIsStopCount(true);
		this.initBusinessStr();
		SpeechTools.setPromptOff(true);
		LeoSpeech.stopRecognize();
		/*LeoSpeech.speak(songMsgStart, new ISpeakListener() {
			@Override
			public void onSpeakOver(int errorCode) {
				randomPlaySongs();
			}
		});*/
		randomPlayPoetry();
	}

	@Override
	public boolean handle(String result) {
		synchronized (obj) {
			Log.d("gaowenwen", "enter PoetryBusiness");
			if (TextUtils.isEmpty(result)) { //语音为空的时候，不做处理
				Log.d("gaowenwen", "result unll return false");
				return false;
			} else if (checkAnswer(mContext, R.string.b_cmd_exit, result)) { //“退出”命令
				this.exit();
				Log.d("leo", tag + ": exit");
				return true;
			} else { //其他的结果，找相应的歌放，没有就不处理
				boolean isPlay = playPoetry(result);
				if(isPlay){ //如果播放了，就返回，没播放，往下走，走到SpeechTools.startRecognize()
					Log.d("leo", tag + ":  handle(...) playSong");
					return true;
				}
			}
		}
		SpeechTools.startRecognize();
		Log.d("leo", tag + ": handle(...) not play");
		return true;
	}

	@Override
	public void reset() {
		Log.d("leo", tag + ": reset()");
		MusicPlayer.getInstance(mContext).stopPlay();
		this.isPlaying = false;
		this.exit(); //摸头退出
	}

	@Override
	public void exit() {
		synchronized (obj) {
			super.exit();
			SpeechClientApp.setIsStopCount(false);
			this.isPlaying = false;
			if (null != mIntervalThread) {
				this.mIntervalThread.setExit();
			}
			MediaResourcePlayer.resetType(); //业务退出时播放类型至空
		}
		SpeechTools.setPromptOff(false);
		SpeechTools.speakAndRestartRecognize(poetryMsgExit);
	}
	
	private void initBusinessStr(){
		//this.songMsgStart = this.mContext.getString(R.string.b_msg_song_start);
		this.poetryMsgExit = this.mContext.getString(R.string.b_msg_exitpoetry);
		//this.promptMsg = this.mContext.getString(R.string.b_msg_prompt);
	}
	
	private void randomPlayPoetry(){
		if(isPlaying){
			return;
		}
		SpeechTools.stopRecognize();
		this.isPlaying = true;
		MediaResourcePlayer.playRadomPoetryBusiness(mContext, mPoetrySpeakListener);
	}
	
	private boolean playPoetry(String poetryName){
		if(isPlaying){
			return true;
		}
		SpeechTools.stopRecognize();
		isPlaying = MediaResourcePlayer.playByName(mContext, poetryName, mPoetrySpeakListener);
		return isPlaying;
	}
	
	private class PoetryEndListener extends OnMediaEndListener {

		@Override
		public void onCompletion(MediaPlayer arg0) {
			isPlaying = false;
			/*mPoetryEndThread = new PoetryEndThread(0); //去掉中间询问的步骤
			mPoetryEndThread.start();*/
			//mIntervalThread = new PoetriesIntervalThread();
			//mIntervalThread.start();
			reset();//解决播放完毕死机问题
		}

		@Override
		public void onSpeakOver(int errorCode) {
			reset();
		}
		
	}
	
	private class PoetryEndThread extends Thread{
		
		private long sleepTime = 0;
		
		public PoetryEndThread(long sleepTime){
			this.sleepTime = sleepTime;
		}
		
		@Override
		public void run() {
			super.run();
			
			try {
				if(0 != sleepTime){
					Thread.sleep(sleepTime);
				}
				/*MusicPlayer.getInstance(mContext).stopPlay();
				SpeechTools.stopRecognize();
				LeoSpeech.speak(promptMsg, new ISpeakListener() {
					@Override
					public void onSpeakOver(int errorCode) {
						SpeechTools.startRecognize();
						mIntervalThread = new PoetriesIntervalThread();
						mIntervalThread.start();
					}
				});*/
				mIntervalThread = new PoetriesIntervalThread();
				mIntervalThread.start();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class PoetriesIntervalThread extends Thread {
		
		private boolean exit = false;
		
		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(intervalTime);
				synchronized (obj) {
					if (isPlaying) {
						Log.d("leo", "SongsBusiness_SongsIntervalThread: playing, return");
						return;
					}
					if (exit) {
						Log.d("leo", "SongsBusiness_SongsIntervalThread: exit, return");
						return;
					}
					randomPlayPoetry();
				}
				Log.d("leo", "SongsBusiness_SongsIntervalThread: play");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void setExit(){
			this.exit = true;
		}
	}
}
