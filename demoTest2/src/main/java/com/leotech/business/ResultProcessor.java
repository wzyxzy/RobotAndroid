package com.leotech.business;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.leo.api.LeoRobot;
import com.leo.api.LeoSpeech;
import com.leo.api.abstracts.IResultProcessor;
import com.leo.api.nlp.NLPResult;
import com.leo.api.processor.MessageType;
import com.leo.api.nav.FileUtils;
import com.leo.api.util.Logs;
import com.leotech.SpeechTools;
import com.leotech.actioncontroller.R;
/**
 * 识别结果/出错 处理
 * 
 * @author ydshu create 2013-02-20
 */
public class ResultProcessor implements IResultProcessor {

	private final static String TAG = "ResultProcessor";
	//some commands
	public final static String START_ENGLISH_CLASS = "#start_class_E"; 
	public final static String START_CHINESE_CLASS = "#start_class_C"; 
	public final static String PAUSE_CLASS = "#pause_class";
	public final static String RESTART_CLASS = "#restart_class";
	public final static String STOP_CLASS = "#stop_class";
	public final static String EXIT_CLASS = "exit_class";
	private final static String ERROR = "客户端发送的录音数据不完整";
	private Context mContext;
	private boolean mIsBusy;
	// 休息功能
	//private PreBusiness mPreBusiness;
	// 阻塞性且非必须的business，有了这个，就不能执行后面的business了，比如自由点播，复位后要重启语音识别
	private IBusiness mBlockBusiness;
	// business列表，都是固定的功能，初始化以后不能增删，也不能无效化
	private ArrayList<IBusinessNLP> mBusinesses = new ArrayList<IBusinessNLP>();

	/**
	 * 构造函数
	 * 
	 * @param context
	 */
	public ResultProcessor(Context context) {
		mContext = context;
		//mPreBusiness = new PreBusiness(mContext);
		//mBusinesses.add(new FunctionBusiness(mContext));
		//mBusinesses.add(new SimpleAnswerBusiness(mContext)); //动作库+本地问答
		//mBusinesses.add(new CustomBusiness(mContext));
		//mBusinesses.add(new DataChatBusiness(mContext)); //达闼语义理解
		//mBusinesses.add(new ChatBusiness(mContext));
		mBusinesses.add(new ChatTuringBusiness(mContext));
	}

	@Override
	public void onInit() {

	}


	@Override
	public void onResult(NLPResult result) {
		Logs.d(TAG, "onResult");
		mIsBusy = true;		
		handleResult(result);
		
	}

	@Override
	public void onError(int errorCode) {
		FileUtils.core("errorCode:" + errorCode);		
		
	
		Log.v(TAG,"ResultProcessor.onError errorCode="+errorCode);


		//if(mPreBusiness.onError()) return;
		if (mBlockBusiness != null && mBlockBusiness.isActive()) {
			if(mBlockBusiness.onError())return;
		}
		for (IBusinessNLP business : mBusinesses) {
			if (business.isActive()) {
				if(business.onError()) return;
			}
		}

		// //////////////////////////////////////////////////////////

		// 对投影的意外中断进行处理
		if (MessageType.unexpectedHalt) {
			MessageType.unexpectedHalt = false;
			reset();
			return;
		}
		if(!SpeechTools.isBusy(mContext)){
//			LeoSpeech.startRecognize();
		}
	}
	
	@Override
	public String onLoaclRecResult(String resultStr){ //离线语音识别
		Log.d("leo", TAG + ": onLoaclRecResult");
		if(null == resultStr){ //这里不判断"", ""是有用的。
			return null;
		}
		handleResult(new NLPResult(resultStr));		
		return null;
	}

	@Override
	public void onSwitchOK() {
		Logs.d(TAG, "onSwitchOK");
		// 播放提示语
		String tip = mContext.getResources().getString(R.string.SwitchOK);
		LeoSpeech.speak(tip, null);
	}
	
	private void handleResult(NLPResult nlp){
		String result = trimString(nlp.getRawtext());
		String response = nlp.getAnswer();
		handleResult(result, response);
	}

	@Override
	public void handleResult(String result, String response) {
		mIsBusy = true;

		if(ERROR.equalsIgnoreCase(result)){
			LeoRobot.repeat_count++;
			onError(9527);
			return;
		}

		Log.d(TAG,  "getEnglishMode ="+LeoSpeech.getEnglishMode());

			if (mBlockBusiness != null && mBlockBusiness.isActive()) {
				mBlockBusiness.handle(result);
				LeoRobot.repeat_count = 0;
				Log.d(TAG,  "mBlockBusiness processed");
				return;
			}
			if (addBusiness(result)) {
				LeoRobot.repeat_count = 0;
				Log.d(TAG,  "addBusiness processed");
				return;
			}
			for (IBusinessNLP business : mBusinesses) {
				if (business.isActive()) {
					if (business.handle(result, response)) {
						LeoRobot.repeat_count = 0;
						Log.d(TAG,  "mBusinesses processed");
						return;
					}
				}
			}
		
		mIsBusy = false;

		LeoRobot.repeat_count++;
		Log.d(TAG,  "no business repeat_count ++ ="+LeoRobot.repeat_count);
	//	SpeechTools.startRecognize();//离线语音识别，如果命令没有被处理，再次开启识别
	}

	public void reset() {
		MessageType.rest = false;
		/*
		mPreBusiness.reset();
		if (mBlockBusiness != null && mBlockBusiness.isActive()) {
			Log.d("wss","mBlockBusiness return");
			mBlockBusiness.reset();
			return;
		}
		*/
		for (IBusinessNLP business : mBusinesses) {
			if (business.isActive()) {
				business.reset();
			}
		}

		mIsBusy = false;
		LeoSpeech.stopSpeak();
		SpeechTools.stopRecognize();
		SpeechTools.startRecognize();
	}

	private boolean addBusiness(String cmd) {
		cmd = cmd.replaceAll("[。|!|\\?|！|？]$", "");
		// ////////////进入休息状态
		/*if (IBusinessNLP.checkAnswer(mContext, R.string.cmd_rest, cmd)) {
			LeoRobot.doSleep();
			String path = "prompt/rest.mp3";
			MusicPlayer.getInstance(mContext).playAssetsFile(path, false);
			return true;
		}*/
		// ///////////自由点播
		/*
		if (IBusinessNLP.checkAnswer(mContext, R.string.cmd_net_music, cmd)) {
			if (NetUtil.is_Network_Available(mContext)) {
				mBlockBusiness = new NetMusicBusiness(mContext);
			} else {
				LeoSpeech.speakAndRestartRecognise(mContext, R.string.word_net_error);
			}
			return true;
		}
		*/
		//////////////////自定义课程
		/*
		if (IBusinessNLP.checkAnswer(mContext, R.string.cmd_english_class, cmd)) {			
			mBlockBusiness = new ClassBusiness(mContext, true);			
			return true;
		}
		if (IBusinessNLP.checkAnswer(mContext, R.string.cmd_class, cmd)) {			
			mBlockBusiness = new ClassBusiness(mContext, false);			
			return true;
		}
		*/
		// /////////////英语聊天
		/*else if (IBusinessNLP.checkAnswer(mContext, R.string.cmd_speak_english, cmd)) {
			if (NetUtil.is_Network_Available(mContext)) {
				mBlockBusiness = new EnglishChatBusiness(mContext);
				String response = mContext.getString(R.string.cmd_speak_english_reponse);
				LeoSpeech.speakAndRestartRecognise(response);
			} else {
				// "网络有问题，请设置网络！"
				LeoSpeech.speakAndRestartRecognise(mContext, R.string.word_net_error);
			}
			return true;
		}*/
		//故事

		if(IBusinessNLP.checkAnswer(mContext, R.string.cmd_story, cmd)){
			mBlockBusiness = new StoryBusiness(mContext);
			return true;
		}

		//唱歌
		/*if(IBusinessNLP.checkAnswer(mContext, R.string.cmd_sing, cmd)){
			if (NetUtil.is_Network_Available(mContext)) {
				mBlockBusiness = new NetMusicBusiness(mContext);
			} else {
				LeoSpeech.speakAndRestartRecognise(mContext, R.string.word_net_error);
			}
			return true;
		}*/
		//唐诗
		/*
		if(IBusinessNLP.checkAnswer(mContext, R.string.cmd_poetry, cmd)){
			mBlockBusiness = new PoetryBusiness(mContext);
			return true;
		}
		*/
		//离线知识库
		/*if(IBusinessNLP.checkAnswer(mContext, R.string.cmd_offline_qalib, cmd)){
			mBlockBusiness = new OffLineQALib(mContext);
			return true;
		}*/
		//导航相关的业务
		/*
		if(IBusinessNLP.checkAnswer(mContext, R.string.cmd_licai, cmd) || 
		   IBusinessNLP.checkAnswer(mContext, R.string.cmd_non_cash, cmd) || 
		   IBusinessNLP.checkAnswer(mContext, R.string.cmd_kai_hu_deng, cmd) || 
		   IBusinessNLP.checkAnswer(mContext, R.string.cmd_self_help, cmd)){
			mBlockBusiness = new NavAbleBusiness(mContext, cmd);
		//	mBlockBusiness = new ManageMoneyMattersBusiness(mContext);
			return true;
		}
		*/
		//算利息
		/*
		if(IBusinessNLP.checkAnswer(mContext, R.string.cmd_suan_li_xi, cmd)){
			mBlockBusiness = new LiXiJiSuanBusiness(mContext);
			return true;
		}
		*/
		return false;
	}
	
	public boolean isBusy() {
		return mIsBusy;
	}

	@Override
	public void clearTask() {

	}
	
	private String trimString(String s){
		return s.replaceAll("[。|!|\\?|！|？]$", "").replaceAll("&apos;", "'").replaceAll("\\.", "");
	}

	@Override
	public void handleCmd(String cmd) {
		/*
		if(START_ENGLISH_CLASS.equalsIgnoreCase(cmd)){
			mBlockBusiness = new ClassBusiness(mContext, true);
			return;
		}
		if(mBlockBusiness instanceof ClassBusiness){
			if(PAUSE_CLASS.equalsIgnoreCase(cmd)){
//				MediaResourcePlayer.stopCurrentActions(mContext);
				((ClassBusiness)mBlockBusiness).pauseCurrentClass();
				return;
			}
			if(RESTART_CLASS.equalsIgnoreCase(cmd)){
				((ClassBusiness)mBlockBusiness).restartCurrentClass();
				return;
			}
			if(STOP_CLASS.equalsIgnoreCase(cmd)){
				MediaResourcePlayer.stopCurrentActions(mContext);
				((ClassBusiness)mBlockBusiness).stopCurrentClass();
				return;
			}			
			if(EXIT_CLASS.equalsIgnoreCase(cmd)){
				MediaResourcePlayer.stopCurrentActions(mContext);
				mBlockBusiness.exit();
				SpeechTools.startRecognize();
				return;
			}
			if(((ClassBusiness)mBlockBusiness).startNewClass(cmd)){
				return;
			}
		}
		*/
	}

}
