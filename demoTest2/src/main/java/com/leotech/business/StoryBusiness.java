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

public class StoryBusiness extends IBusiness{

	private static Object obj = new Object();
	private final int second = 5;
	private final long intervalTime = second * 1000;
	
	private StoryEndListener mStorySpeakListener = null;
	private boolean isPlaying = false;
	private StoryIntervalThread mIntervalThread = null;
	private StoryEndThread mStoryEndThread = null;
	
	private String storyMsgStart = null;
	private String storyMsgExit = null;
	private String promptMsg = null;
	
	public StoryBusiness(Context context){
		super(context);
		mStorySpeakListener = new StoryEndListener();
		SpeechClientApp.setIsStopCount(true);
		this.initBusinessStr();
		SpeechTools.setPromptOff(true);
		LeoSpeech.stopRecognize();
		LeoSpeech.speak(storyMsgStart, new ISpeakListener() {
			@Override
			public void onSpeakOver(int errorCode) {
				randomPlayStory();
			}
		});
	}
	
	@Override
	public boolean handle(String result) {
		synchronized (obj) {
			Log.d("gaowenwen", "enter StoryBusiness");
			if (TextUtils.isEmpty(result)) { //语音为空的时候，不做处理
				Log.d("gaowenwen", "result unll return false");
				return false;
			} else if (checkAnswer(mContext, R.string.b_cmd_exit, result)) { //“退出”命令
				this.exit();
				Log.d("leo", "StoryBusiness: exit");
				return true;
			} else { //其他的结果，找相应的故事播放，没有不处理
				boolean isPlay = playStory(result);
				if(isPlay){ //如果播放了，就返回，没播放，往下走，走到SpeechTools.startRecognize()
					Log.d("leo", "StoryBusiness:  handle(...) playStory");
					return true;
				}
			}
		}
		SpeechTools.startRecognize();
		Log.d("leo", "StoryBusiness: handle(...) not play");
		return true;
	}

	@Override
	public void reset() {
		Log.d("leo", "StoryBusiness: reset()");
		MusicPlayer.getInstance(mContext).stopPlay();
		this.isPlaying = false;
		this.exit(); //某头退出
		/*if(null != mIntervalThread && (Thread.State.RUNNABLE == mIntervalThread.getState() || Thread.State.TIMED_WAITING == mIntervalThread.getState())){
			return;
		}
		if(null != mStoryEndThread && (Thread.State.RUNNABLE == mStoryEndThread.getState() || Thread.State.TIMED_WAITING == mStoryEndThread.getState())){
			return;
		}
		mStoryEndThread = new StoryEndThread(500);
		mStoryEndThread.start();*/
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
		SpeechTools.speakAndRestartRecognize(storyMsgExit);
	}
	
	private void initBusinessStr(){
		this.storyMsgStart = this.mContext.getString(R.string.b_msg_story_start);
		this.storyMsgExit = this.mContext.getString(R.string.b_msg_exitstory);
		this.promptMsg = this.mContext.getString(R.string.b_msg_prompt);
	}
	
	private void randomPlayStory(){
		if(isPlaying){
			return;
		}
		SpeechTools.stopRecognize();
		this.isPlaying = true;
		MediaResourcePlayer.playRadomStoryBusiness(mContext, mStorySpeakListener);
	}
	
	private boolean playStory(String storyName){
		if(isPlaying){
			return true;
		}
		SpeechTools.stopRecognize();
		isPlaying = MediaResourcePlayer.playByName(mContext, storyName, mStorySpeakListener);
		return isPlaying;
	}
	
	private class StoryEndListener extends OnMediaEndListener {

		@Override
		public void onCompletion(MediaPlayer arg0) {
			isPlaying = false;
			/*LeoSpeech.speak("继续听请说继续，退出故事请说退出，也可以说故事名，" + second + "秒后自动播放", new ISpeakListener() {
				@Override
				public void onSpeakOver(int errorCode) {
					SpeechTools.startRecognize();
					mIntervalThread = new StoryIntervalThread();
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
	
	private class StoryEndThread extends Thread{
		
		private long sleepTime = 0;
		
		public StoryEndThread(long sleepTime){
			this.sleepTime = sleepTime;
		}
		
		@Override
		public void run() {
			super.run();
			
			try {
				if(0 != sleepTime){
					Thread.sleep(sleepTime);
				}
				MusicPlayer.getInstance(mContext).stopPlay();
				LeoSpeech.stopRecognize();
				LeoSpeech.speak(promptMsg, new ISpeakListener() {
					@Override
					public void onSpeakOver(int errorCode) {
						SpeechTools.startRecognize();
						mIntervalThread = new StoryIntervalThread();
						mIntervalThread.start();
					}
				});
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private class StoryIntervalThread extends Thread {
		
		private boolean exit = false;
		
		@Override
		public void run() {
			super.run();
			try {
				Thread.sleep(intervalTime);
				synchronized (obj) {
					if (isPlaying) {
						Log.d("leo", "StoryBusiness_StoryIntervalThread: playing, return");
						return;
					}
					if (exit) {
						Log.d("leo", "StoryBusiness_StoryIntervalThread: exit, return");
						return;
					}
					randomPlayStory();
				}
				Log.d("leo", "StoryBusiness_StoryIntervalThread: play");
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		public void setExit(){
			this.exit = true;
		}
	}
}
