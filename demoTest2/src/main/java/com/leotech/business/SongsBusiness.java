package com.leotech.business;

import android.content.Context;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;

import com.leo.api.LeoSpeech;
import com.leo.api.abstracts.ISpeakListener;
import com.leo.api.control.music.MediaResourcePlayer;
import com.leo.api.control.music.MusicPlayer;
import com.leo.api.control.music.OnMediaEndListener;
import com.leotech.SpeechClientApp;
import com.leotech.SpeechTools;
import com.leotech.actioncontroller.R;

public class SongsBusiness extends IBusiness {

	private final String tag = "SongsBusiness";
	private static Object obj = new Object();
	private final int second = 5;
	private final long intervalTime = second * 1000;
	
	private SongsEndListener mSongsSpeakListener = null;
	private boolean isPlaying = false;
	private SongsIntervalThread mIntervalThread = null;
	private SongEndThread mSongEndThread = null;
	
	private String songMsgStart = null;
	private String songMsgExit = null;
	private String promptMsg = null;
	
	public SongsBusiness(Context context) {
		super(context);
		mSongsSpeakListener = new SongsEndListener();
		SpeechClientApp.setIsStopCount(true);
		this.initBusinessStr();
		SpeechTools.setPromptOff(true);
		LeoSpeech.stopRecognize();
		LeoSpeech.speak(songMsgStart, new ISpeakListener() {
			@Override
			public void onSpeakOver(int errorCode) {
				randomPlaySongs();
			}
		});
	}

	@Override
	public boolean handle(String result) {
		synchronized (obj) {
			Log.d("gaowenwen", "enter SongsBusiness");
			if (TextUtils.isEmpty(result)) { //语音为空的时候，不做处理
				Log.d("gaowenwen", "result unll return false");
				return false;
			} else if (checkAnswer(mContext, R.string.b_cmd_exit, result)) { //“退出”命令
				this.exit();
				Log.d("leo", tag + ": exit");
				return true;
			} else { //其他的结果，找相应的歌放，没有就不处理
				boolean isPlay = playSong(result);
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
		/*if(null != mIntervalThread && (Thread.State.RUNNABLE == mIntervalThread.getState() || Thread.State.TIMED_WAITING == mIntervalThread.getState())){
			return;
		}
		if(null != mSongEndThread && (Thread.State.RUNNABLE == mSongEndThread.getState() || Thread.State.TIMED_WAITING == mSongEndThread.getState())){
			return;
		}
		this.mSongEndThread = new SongEndThread(500);
		this.mSongEndThread.start();*/
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
		SpeechTools.speakAndRestartRecognize(songMsgExit);
	}
	
	private void initBusinessStr(){
		this.songMsgStart = this.mContext.getString(R.string.b_msg_song_start);
		this.songMsgExit = this.mContext.getString(R.string.b_msg_exitsong);
		this.promptMsg = this.mContext.getString(R.string.b_msg_prompt);
	}
	
	private void randomPlaySongs(){
		if(isPlaying){
			return;
		}
		SpeechTools.stopRecognize();
		this.isPlaying = true;
		MediaResourcePlayer.playRadomMusicBusiness(mContext, mSongsSpeakListener);
	}
	
	private boolean playSong(String songName){
		if(isPlaying){
			return true;
		}
		SpeechTools.stopRecognize();
		isPlaying = MediaResourcePlayer.playByName(mContext, songName, mSongsSpeakListener);
		return isPlaying;
	}
	
	private class SongsEndListener extends OnMediaEndListener {

		@Override
		public void onCompletion(MediaPlayer arg0) {
			isPlaying = false;
			/*LeoSpeech.speak(promptMsg, new ISpeakListener() {
				@Override
				public void onSpeakOver(int errorCode) {
					SpeechTools.startRecognize();
					mIntervalThread = new SongsIntervalThread();
					mIntervalThread.start();
				}
			});*/
			//mSongEndThread = new SongEndThread(0);
			//mSongEndThread.start();
			reset();//解决播放完毕死机问题
		}

		@Override
		public void onSpeakOver(int errorCode) {
			reset();
		}
		
	}
	
	private class SongEndThread extends Thread{
		
		private long sleepTime = 0;
		
		public SongEndThread(long sleepTime){
			this.sleepTime = sleepTime;
		}
		
		@Override
		public void run() {
			super.run();
			
			try {
				if(0 != sleepTime){
					Thread.sleep(sleepTime);
				}
				//LeoSpeech.stopSpeak();
				MusicPlayer.getInstance(mContext).stopPlay();
				SpeechTools.stopRecognize();
				LeoSpeech.speak(promptMsg, new ISpeakListener() {
					@Override
					public void onSpeakOver(int errorCode) {
						SpeechTools.startRecognize();
						mIntervalThread = new SongsIntervalThread();
						mIntervalThread.start();
					}
				});
				/*SpeechTools.startRecognize();
				mIntervalThread = new SongsIntervalThread();
				mIntervalThread.start();*/
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class SongsIntervalThread extends Thread {
		
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
					randomPlaySongs();
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
