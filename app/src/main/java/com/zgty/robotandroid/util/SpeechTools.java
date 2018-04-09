package com.zgty.robotandroid.util;

import android.content.Context;
import android.util.Log;

import com.leo.api.LeoRobot;
import com.zgty.robotandroid.util.LeoSpeech;
import com.leo.api.abstracts.ISpeakListener;
import com.leo.api.control.music.MediaResource;
import com.leo.api.control.music.MusicPlayer;
import com.leo.api.control.promptWord.PromptWord;
import com.leo.api.version.VersionManager;

import java.util.ArrayList;

public class SpeechTools {
	private static Context mContext;
	private static boolean mPromptOff = false;
	private static ArrayList<String> mPromptMedia;
	
	private static boolean mIsControlMode = false;//controller stop the recognize,use this to avoid others start it again.
	
	
	public static void init(Context context){
		mContext = context;
	}
	
	public static void startRecognize(){
		Log.d("gaowenwen", "SpeechTools.startRecognise....  ");
		
		if(mIsControlMode || MusicPlayer.getInstance(mContext).isPlayingMusic()){
			return;
		}
		
		if(mPromptOff){
			LeoSpeech.startRecognize();
			return;
		}
		
		//turnHead(SpeechClientApp.repeat_count);//小机器人代码，去掉 lihui
		
		if(mPromptMedia != null&& LeoSpeech.getEnglishMode()==false){
			playPromptMedia();
			return;
		}		
	       //mody  by lihui for high temp
		int total_count;
		//if (SpeechClientApp.roaming_from_detail == false){
			total_count=10;
		//}
		//else
		//{
		//	total_count=3;
		//}
		Log.d("gaowenwen", "repeat_count = "+ LeoRobot.repeat_count+"  version="+ VersionManager.getVersion());
		/*
		if(SpeechClientApp.repeat_count > total_count ){
			Log.d("gaowenwen", "repeat_count > 10 return!! ");
			String s;
		//	if(VersionManager.getVersion() == VersionManager.BANK)
		//		 s = "等待超时，即将返回首页";
		//	else
			if (SpeechClientApp.roaming_from_detail == false){
				 s = mContext.getString(R.string.main_backto_work);
			}
			else
			{
				s = mContext.getString(R.string.main_continue_roaming);
			}
	
			LeoSpeech.speak(s, new ISpeakListener() {
					@Override
					public void onSpeakOver(int errorCode) {
						stopRecognize();//银行机器人页面显示错误，并有时提示音不播放
						Log.d("gaowenwen", "start to FirstsActivity ");
						Intent intent;
						SpeechClientApp.repeat_count=0;
						if (SpeechClientApp.roaming_from_detail == true){
							SpeechClientApp.intruduce = true;						
							NavUtil nu = new NavUtil(mContext);
							nu.doNav(SpeechClientApp.get_goal("null"));
							 intent = new Intent(mContext,JumpActivity.class);
							
						}else
						{
							intent = new Intent(mContext,DetecterActivity.class);
						}
						mContext.startActivity(intent);
					}
				});

			return;
		}
		*/
		LeoSpeech.startRecognize();
	}
	
	public static void stopRecognize(){
		// 停止提示音播放
		MusicPlayer.getInstance(mContext).stopPlay();
		LeoSpeech.stopRecognize();
	}
	
	public static void speakAndRestartRecognize(String text){
		LeoSpeech.speak(text, new ISpeakListener() {
			
			@Override
			public void onSpeakOver(int errorCode) {
				startRecognize();
			}
		});
	}
	/**
	 * 重启语音服务
	 */
	public static void speakAndRestartRecognize(Context context, int id){
		speakAndRestartRecognize(context.getString(id));
	}
	private static void turnHead(int count){
		if(count%5 == 1){
			LeoRobot.turnLeft();
		}else if(count%5 == 3){
			LeoRobot.turnRight();
		}
	}
	
	
	public static String chackThePath(Context context, String introducePath) {
		String path = PromptWord.randomPath(introducePath);
		String[] strs = path.split("\\|", 2);
		if(strs.length == 0){
			return null;
		}else if(strs.length == 1){
			return strs[0];
		}else{
			if(strs[0].length() > 2){
				LeoRobot.doAction(strs[0]);
			}
			return strs[1];
		}		
	}
	public static void setControlMode(boolean isControlMode){
		if(mIsControlMode == isControlMode) return;
		mIsControlMode = isControlMode;
		if(isControlMode){
			SpeechTools.stopRecognize();
		}else{
			//add by lihui for high temp
			LeoRobot.repeat_count = 0;
			Log.d("wss", "setControlMode  repeat_count =0  isControlMode= "+isControlMode);
			//add end
			SpeechTools.startRecognize();
		}
	}
	public static void setPromptOff(boolean off){
		mPromptOff = off;
	}

	public static boolean getControlMode(){
		return mIsControlMode;
	}
	/**
	 * set assets media or tts word
	 * @param media
	 */ 
	public static void setPromptMedia(String media){
		mPromptMedia = new ArrayList<String>();
		mPromptMedia.add(media);
		Log.d("wss", "set prompt word @@"+mPromptMedia);
	}
	public static void setPromptMedia(ArrayList<String> medias){
		mPromptMedia = medias;
		Log.d("wss", "set prompt words @@"+medias.size());
	}
	public static void clearPromptMedia(){
		mPromptMedia = null;
	}
	private static void playPromptMedia(){
		if(mPromptMedia.size() == 0) return;
		String media = mPromptMedia.get((int)(Math.random() * mPromptMedia.size()));
		Log.d("wss", "play prompt media@@"+media);
		new MediaResource(media).playAndRecognizeSilently(mContext);
	}	
	public static boolean isBusy(Context context){
		return MusicPlayer.getInstance(context).isPlayingMusic() || LeoSpeech.isSpeaking();
	}
}
