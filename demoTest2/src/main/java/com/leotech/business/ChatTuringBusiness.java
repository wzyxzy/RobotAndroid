package com.leotech.business;

import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.leotech.actioncontroller.R;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.leo.api.LeoSpeech;
import com.leo.api.abstracts.ISpeakListener;
import com.leo.api.control.promptWord.PromptWord;
import com.leotech.SpeechTools;
import com.turing.androidsdk.HttpRequestListener;
import com.turing.androidsdk.TuringManager;
/**
 * 聊天 和 英语聊天
 * @author Administrator
 *
 */
public class ChatTuringBusiness extends IBusinessNLP{
	final String TAG = this.getClass().getSimpleName();
	
	private static final int CODE_COMMON = 100000;
	private static final int CODE_NEWS = 302000;
	private static final int CODE_RECIPE = 308000;
	private static final int CODE_URL = 200000;
	private static final int CODE_SING = 313000;
	private static final int CODE_POETRY = 314000;
	private static boolean getwronganswer = false;
	 /**
     * 申请的turing的apikey
     * **/
    //private final String TURING_APIKEY = "137f25e9a7ac40d4a27358fb3da8742d";
   // private final String TURING_SECRET = "cd92f35fc3b45353";
    private final String TURING_APIKEY = "908d592e3e46466d83cdc63db0934c5e";//图灵机器人
    private final String TURING_SECRET = "6c6bc0b1cd39ad46";
    
    
	private TuringManager mTuringManager;
	private boolean mIsSuccess = false;
	private boolean mIsOvertime = false;
	private final String LOCK = "lock_wss";
	
	private final String[] WRONG_WORDS = {"图灵机器人","耍流氓","小三"};
	private final String[] ANSWER4EMPTY ; 
	
	public ChatTuringBusiness(Context context) {
		super(context);
		mTuringManager = new TuringManager(context, TURING_APIKEY, TURING_SECRET);
	    mTuringManager.setHttpRequestListener(myHttpConnectionListener);
	    ANSWER4EMPTY = context.getResources().getStringArray(R.array.b_msg_chat_empty_answer);
	}

	@Override
	public boolean handle(final String result, final String response) {		
		
		// 只是进入离线识别状态，并不退出聊天
		if (result == null)	return false;
		if (checkAnswer(mContext, R.string.cmd_exit_chatmode, result)) {
			LeoSpeech.setCmdMode(true);
			String exitChatResp = mContext.getString(R.string.word_exitchatmode_resp);
			SpeechTools.speakAndRestartRecognize(exitChatResp);
			return true;
		}

		//只有一个字，多数是识别错误
		if(result.length() <2){
			//LeoSpeech.speakAndRestartRecognise(getWrongAnswer());
			//return true;
			return false;
		}
		
		new Thread(){

			@Override
			public void run(){
				synchronized (LOCK) {
					mIsSuccess = false;
					mIsOvertime = false;
					mTuringManager.requestTuring(result);
					try {
						LOCK.wait(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(!mIsSuccess){
						getwronganswer = true;
						/*
						mIsOvertime = true;
						String iflytek = replaceImproperAnswer(response);
						poupText(iflytek);
						LeoSpeech.speakAndRestartRecognise(iflytek);
						*/
					}

				}
			}
		}.start();
		if(getwronganswer)
			return false;
		else
			return true;
	}
	
	private void poupText(String info){
		Intent intent = new Intent();
		intent.setAction("update_ui");
		intent.putExtra("text", info);
		mContext.sendBroadcast(intent);
	}
	
	@Override
	public boolean onError() {
		return false;
	}

	@Override
	public void reset() {
		
	}
	
	 /**
     * 网络请求回调
     */
    HttpRequestListener myHttpConnectionListener = new HttpRequestListener() {

        @Override
		public void onSuccess(String result) {
        	synchronized (LOCK) {
			if (result != null) {
				try {
					if(mIsOvertime) return;//已经超时不再处理
					mIsSuccess = readResult(new JSONObject(result));
					LOCK.notifyAll();
					return;
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			mIsSuccess = false;
			LOCK.notifyAll();
        	}
		}

		@Override
		public void onFail(int arg0, String arg1) {
			synchronized (LOCK) {
				mIsSuccess = false;
				LOCK.notifyAll();
			}
		}
    };
    
    private boolean readResult(JSONObject json){
    	
    	int code = 0;    	
    	try {
    		code = json.getInt("code");
		} catch (JSONException e) {
			e.printStackTrace();
		}
    	Log.d(TAG, "Turing code="+code);
    	switch (code) {
		case CODE_NEWS:
			readNews(json);
			return true;
			
		
		case CODE_RECIPE: 
		case CODE_URL: return false;
		case CODE_SING: 
			SpeechTools.speakAndRestartRecognize("要听我唱歌吗，请对我说唱歌");
			return true;
		case CODE_POETRY:
			SpeechTools.speakAndRestartRecognize("要听唐诗吗，请对我说唐诗");
			return true;
			
		case CODE_COMMON:
		default:
			String text = "";
			try {
				text = (String) json.get("text");
			} catch (JSONException e) {
				e.printStackTrace();
			}
			Log.d(TAG, "Turing Text="+text);
			
			text = replaceImproperAnswer(text);
			poupText(text);
			SpeechTools.speakAndRestartRecognize(text);
			return true;

		}
    	
    }
    
     //替换出错提示和不合适的回答
    private String replaceImproperAnswer(String text){
    	if (text == null || "".equals(text)){				
			text = getWrongAnswer();
		}else{
			for(String s : WRONG_WORDS){
				if(text.contains(s)){
					text = getWrongAnswer();
					break;
				}
			}
		}
    	return text;
    }
    private String getWrongAnswer(){
    	Random random = new Random();
		int index = random.nextInt(ANSWER4EMPTY.length);
		return ANSWER4EMPTY[index];
    }
    
    private void readNews(JSONObject json){
    	StringBuffer sb_show = new StringBuffer();
    	StringBuffer sb_read = new StringBuffer();
    	try {
			JSONArray list = json.getJSONArray("list");
			int size = list.length();
			for(int i=0; i<size; i++){
				String article = list.getJSONObject(i).getString("article");
				if(article == null || article.equalsIgnoreCase("")) continue;
				sb_read.append(i+1+"[p500]"+article+"[p1000]");
				sb_show.append(i+1+"、"+article+"\n");
			}
			poupText(sb_read.toString());
			SpeechTools.speakAndRestartRecognize(sb_read.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}    	
    }
	
}
