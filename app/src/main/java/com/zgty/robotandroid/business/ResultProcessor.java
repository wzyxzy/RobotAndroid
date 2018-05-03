package com.zgty.robotandroid.business;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.github.promeg.pinyinhelper.Pinyin;
import com.zgty.robotandroid.util.LeoSpeech;
import com.leo.api.abstracts.IResultProcessor;
import com.leo.api.nlp.NLPResult;
import com.leo.api.util.Logs;
import com.zgty.robotandroid.common.Constant;
import com.zgty.robotandroid.util.IbotUtils;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * 识别结果/出错 处理
 *
 * @author ydshu create 2013-02-20
 */
public class ResultProcessor implements IResultProcessor {

    private final static String TAG = "ResultProcessor";
    //some commands
    private Context mContext;
    private boolean mIsBusy;
    // 休息功能
    //private PreBusiness mPreBusiness;
    // 阻塞性且非必须的business，有了这个，就不能执行后面的business了，比如自由点播，复位后要重启语音识别
//    private IBusiness mBlockBusiness;
//    // business列表，都是固定的功能，初始化以后不能增删，也不能无效化
//    private ArrayList<IBusinessNLP> mBusinesses = new ArrayList<IBusinessNLP>();
    private OnVoiceListener onVoiceListener;

    public void setOnVoiceListener(OnVoiceListener onVoiceListener) {
        this.onVoiceListener = onVoiceListener;
    }


    //定义回调接口
    public interface OnVoiceListener {
        void onWords(String words);

        void onSuccess(int nums);

        void onFail();
    }


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
//        mBusinesses.add(new ChatTuringBusiness(mContext));
    }

    @Override
    public void onInit() {
        Logs.d(TAG, "onInit");
    }


    @Override
    public void onResult(NLPResult result) {
        Logs.d(TAG, "onResult");
        mIsBusy = true;
        handleResult(result);

    }

    @Override
    public void onError(int errorCode) {
//        FileUtils.core("errorCode:" + errorCode);


        Log.v(TAG, "ResultProcessor.onError errorCode=" + errorCode);
        reset();

    }

    @Override
    public String onLoaclRecResult(String resultStr) { //离线语音识别
        Log.d("leo", TAG + ": onLoaclRecResult");
        if (null == resultStr) { //这里不判断"", ""是有用的。
            return null;
        }
        handleResult(new NLPResult(resultStr));
        return null;
    }

    @Override
    public void onSwitchOK() {
        Logs.d(TAG, "onSwitchOK");
        // 播放提示语
    }

    private void handleResult(NLPResult nlp) {
        String result = trimString(nlp.getRawtext());
        String response = nlp.getAnswer();
        handleResult(result, response);
    }

    @Override
    public void handleResult(String result, String response) {
        mIsBusy = true;

        Log.d(TAG, "result =" + result);
        if (TextUtils.isEmpty(result)) {
            return;
        }
        onVoiceListener.onWords(result);

        String pinyin = Pinyin.toPinyin(result, "|");
        int nums = 0;
        String num = "";
//        Toast.makeText(mContext, pinyin, Toast.LENGTH_LONG).show();
        Log.d(TAG, "pinyin =" + pinyin);
        if (pinyin.contains("CHE|XIANG")) {
            num = pinyin.split("CHE|XIANG")[0];
        } else if (pinyin.contains("CE|XIANG")) {
            num = pinyin.split("CE|XIANG")[0];
        } else if (pinyin.contains("CHE|XIAO")) {
            num = pinyin.split("CHE|XIAO")[0];
        } else if (pinyin.contains("CE|XIAO")) {
            num = pinyin.split("CE|XIAO")[0];
//        } else if (pinyin.contains("CHE")) {
//            num = pinyin.split("CHE")[0];
//        } else if (pinyin.contains("CE")) {
//            num = pinyin.split("CE")[0];
        }
        if (TextUtils.isEmpty(num)) {
            iBot(result);
        } else {

            if (num.contains("2|0") || num.contains("ER|SHI") || num.contains("ER|SI")) {
                nums = 20;
            } else if (num.contains("1|9") || num.contains("SHI|JIU") || num.contains("SI|JIU")) {
                nums = 19;
            } else if (num.contains("1|8") || num.contains("SHI|BA") || num.contains("SI|BA")) {
                nums = 18;
            } else if (num.contains("1|7") || num.contains("SHI|QI") || num.contains("SI|QI")) {
                nums = 17;
            } else if (num.contains("1|6") || num.contains("SHI|LIU") || num.contains("SI|LIU")) {
                nums = 16;
            } else if (num.contains("1|5") || num.contains("SHI|WU") || num.contains("SI|WU")) {
                nums = 15;
            } else if (num.contains("1|4") || num.contains("SHI|SI") || num.contains("SI|SI") || num.contains("SHI|SHI")) {
                nums = 14;
            } else if (num.contains("1|3") || num.contains("SHI|SAN") || num.contains("SI|SAN") || num.contains("SHI|SHAN")) {
                nums = 13;
            } else if (num.contains("1|2") || num.contains("SHI|ER") || num.contains("SI|ER")) {
                nums = 12;
            } else if (num.contains("1|1") || num.contains("SHI|YI") || num.contains("SI|YI")) {
                nums = 11;
            } else if (num.contains("1|0") || num.contains("SHI")) {
                nums = 10;
            } else if (num.contains("9") || num.contains("JIU")) {
                nums = 9;
            } else if (num.contains("8") || num.contains("BA")) {
                nums = 8;
            } else if (num.contains("7") || num.contains("QI")) {
                nums = 7;
            } else if (num.contains("6") || num.contains("LIU")) {
                nums = 6;
            } else if (num.contains("5") || num.contains("WU")) {
                nums = 5;
            } else if (num.contains("4") || num.contains("SI")) {
                nums = 4;
            } else if (num.contains("3") || num.contains("SAN") || num.contains("SHAN")) {
                nums = 3;
            } else if (num.contains("2") || num.contains("ER")) {
                nums = 2;
            } else if (num.contains("1") || num.contains("YI")) {
                nums = 1;
            } else {

                nums = 0;
            }
            Constant.CHOOSE_USER_NUM_ID = "no" + String.valueOf(nums);
            if (nums != 0) {
                onVoiceListener.onSuccess(nums);

            } else {

                iBot(result);
            }

        }

    }

    private void iBot(final String result) {

        Runnable downloadRun = new Runnable(){

            @Override
            public void run() {
                // TODO Auto-generated method stub
                String a = IbotUtils.askIBot(result);

                LeoSpeech.speak(a, null);

            }
        };
        new Thread(downloadRun).start();

//        if (LeoRobot.repeat_count<=3){
//            LeoRobot.repeat_count++;
//            Log.d(TAG, "no business repeat_count ++ =" + LeoRobot.repeat_count);
//            onVoiceListener.onFail();
////            SpeechTools.speakAndRestartRecognize("您去几车厢？");
//        }

    }


    public boolean isBusy() {
        return mIsBusy;
    }

    @Override
    public void reset() {

    }

    @Override
    public void clearTask() {

    }

    private String trimString(String s) {
        return s.replaceAll("[。|!|\\?|！|？]$", "").replaceAll("&apos;", "'").replaceAll("\\.", "");
    }

    @Override
    public void handleCmd(String cmd) {

    }

}
