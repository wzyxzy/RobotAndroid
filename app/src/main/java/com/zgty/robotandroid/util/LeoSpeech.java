//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zgty.robotandroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.LexiconListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;
import com.leo.api.abstracts.IResultProcessor;
import com.leo.api.abstracts.ISpeakListener;
import com.leo.api.abstracts.IViewUpdater;
import com.leo.api.util.GrammarManager;
import com.leo.api.util.JsonParser;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import static com.zgty.robotandroid.common.Constant.useLocalRecongnise;


public class LeoSpeech {
    private static SpeechRecognizer mRecognizer;
    private static RecognizerListener mRecognizerListener;
    private static IResultProcessor mResultProcessor;
    private static IViewUpdater mViewUpdater;
    private static SpeechSynthesizer mTts;
    private static boolean mIsGuangdonghua = false;
    private static boolean mIsEnglish = false;
    private static boolean mIsCmd = false;
    private static String PARAM_RECORDING_BASE_TIME = "3000";
    private static String PARAM_RECORDING_STOPPING_TIME = "900";
    private static String mVoicerCN = "xiaoyan";
    private static String mVoicerEN = "catherine";
    private static Context mContext;

    // 本地语法文件
    private static String mLocalGrammar = null;
    public static final String GRAMMAR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test";

    private static InitListener mInitListener = new InitListener() {
        public void onInit(int code) {
            if (code != 0) {
                Log.d("wss", "初始化失败，错误码：" + code);
            }

        }
    };

    public static void setViewUpdater(IViewUpdater viewUpdater) {
        mViewUpdater = viewUpdater;
    }

    public LeoSpeech() {
    }

    public static void init(Context context, IResultProcessor processor) {
        mContext = context;
        mIsEnglish = !isZh(mContext);
        SpeechUtility.createUtility(mContext.getApplicationContext(), "appid=5ac1a294");
//        SpeechUtility.createUtility(context.getApplicationContext(), "appid=5a705940");
        mRecognizer = SpeechRecognizer.createRecognizer(mContext, mInitListener);
        mTts = SpeechSynthesizer.createSynthesizer(mContext, mInitListener);
        mResultProcessor = processor;
        makeResultListener();
        setTtsParam();
    }

    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        Log.d("wss", "language=" + language);
        return language.endsWith("zh");
    }

    public static void addGrammarWords(String words) {
        GrammarManager.addWords(words);
        uploadGrammar();
    }

    public static void speak(String text, ISpeakListener listener) {
        Log.d("wss", "start speak:" + text);
        mTts.startSpeaking(text, new LeoSpeech.TtsListener(listener));
    }

    public static boolean isSpeaking() {
        return mTts.isSpeaking();
    }

    public static void stopSpeak() {
        mTts.stopSpeaking();
    }

    public static void startRecognize() {
        Log.d("wss", "start recognize");
        setRecongizeParam();
        mRecognizer.startListening(mRecognizerListener);
    }

    public static void stopRecognize() {
        Log.d("wss", "stop recognize");
        mRecognizer.cancel();
        if (mViewUpdater != null) {
            mViewUpdater.onIdleState();
        }

    }

    /**
     * 构建语法监听器。
     */
    private static GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {

                Log.d("makeGrammar", "语法构建成功：" + grammarId);
            } else {
                Log.d("makeGrammar", "语法构建失败,错误码：" + error.getErrorCode());
            }
        }
    };

    public static void makelocalGrammar() {
        mLocalGrammar = FucUtil.readFile(mContext, "zgty.bnf", "utf-8");
        mRecognizer.setParameter(SpeechConstant.PARAMS, null);
        // 设置文本编码格式
        mRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        // 设置引擎类型
        mRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, "local");
        // 设置语法构建路径
        mRecognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, GRAMMAR_PATH);
        //使用8k音频的时候请解开注释
//					mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        // 设置资源路径
        mRecognizer.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        int ret = mRecognizer.buildGrammar("bnf", mLocalGrammar, grammarListener);
//        mRecognizer.updateLexicon()
        if (ret != ErrorCode.SUCCESS) {
            Log.e("makeGrammar", "语法构建失败,错误码：" + ret);
        }
    }

    public static void updateGrammar(String contents) {
        mRecognizer.setParameter(SpeechConstant.PARAMS, null);
        // 设置引擎类型
        mRecognizer.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // 设置资源路径
        mRecognizer.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        //使用8k音频的时候请解开注释
//				mRecognizer.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        // 设置语法构建路径
        mRecognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, GRAMMAR_PATH);
        // 设置语法名称
        mRecognizer.setParameter(SpeechConstant.GRAMMAR_LIST, "zgty");
        // 设置文本编码格式
        mRecognizer.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
        int ret = mRecognizer.updateLexicon("contact", contents, lexiconListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.d("updateGrammar", "更新词典失败,错误码：" + ret);
        }
    }

    /**
     * 更新词典监听器。
     */
    private static LexiconListener lexiconListener = new LexiconListener() {
        @Override
        public void onLexiconUpdated(String lexiconId, SpeechError error) {
            if (error == null) {
                Log.d("updateGrammar", "词典更新成功");
            } else {
                Log.d("updateGrammar", "词典更新失败,错误码：" + error.getErrorCode());
            }
        }
    };

    public static void release() {
        mRecognizer.destroy();
    }

    public static void setCmdMode(boolean isCmdMode) {
        mIsCmd = isCmdMode;
    }

    public static void setEnglishMode(boolean isEnglish) {
        mIsEnglish = isEnglish;
        setTtsParam();
    }

    public static boolean getEnglishMode() {
        return mIsEnglish;
    }

    public static void setGuangdongMode(boolean isGuangdong) {
        mIsGuangdonghua = isGuangdong;
        mIsEnglish = false;
        setTtsParam();
    }


    public static void handleControllerResult(String cmd) {
        String result = null;
        String answer = null;

        try {
            JSONObject speech = new JSONObject(cmd);
            result = speech.getString("result");
            answer = speech.getString("answer");
        } catch (JSONException var5) {
            var5.printStackTrace();
        }

        Log.d("wss", "get speech cmd from controller:" + result + "\n" + answer);
        stopRecognize();
        mResultProcessor.handleResult(result, answer);
    }

    private static void uploadGrammar() {
        mRecognizer.buildGrammar("abnf", GrammarManager.getGrammar(), new GrammarListener() {
            public void onBuildFinish(String grammarId, SpeechError error) {
                if (error != null) {
                    Log.d("wss", "语法构建失败,错误码：" + error.getErrorCode());
                }

            }
        });
    }

    private static void setTtsParam() {
        mTts.setParameter("params", (String) null);
        if (mIsEnglish) {
            mTts.setParameter("engine_type", "cloud");
            mTts.setParameter("voice_name", mVoicerEN);
        } else {
            mTts.setParameter("voice_name", mVoicerCN);
            if (mIsGuangdonghua) {
                mTts.setParameter("accent", "cantonese");
                mTts.setParameter("engine_type", "cloud");
            } else {
                mTts.setParameter("accent", "mandarin");
                mTts.setParameter("engine_type", "local");
                mTts.setParameter(ResourceUtil.TTS_RES_PATH, getRttsPath());
            }
        }

        mTts.setParameter("speed", "50");
        mTts.setParameter("pitch", "50");
        mTts.setParameter("volume", "50");
        mTts.setParameter("stream_type", "3");
        mTts.setParameter("request_audio_focus", "true");
        mTts.setParameter("audio_format", "wav");
        mTts.setParameter("tts_audio_path", Environment.getExternalStorageDirectory() + "/msc/tts.wav");
    }

    //获取发音人资源路径
    private static String getRttsPath() {
        StringBuffer tempBuffer = new StringBuffer();
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "tts/xiaoyan.jet"));
        return tempBuffer.toString();
    }

    // 获取识别资源路径
    private static String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        // 识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        // 识别8k资源-使用8k的时候请解开注释
        // tempBuffer.append(";");
        // tempBuffer.append(ResourceUtil.generateResourcePath(this,
        // RESOURCE_TYPE.assets, "asr/common_8k.jet"));
        return tempBuffer.toString();
    }

    private static void setRecongizeParam() {
        mRecognizer.setParameter("params", (String) null);
        if (useLocalRecongnise) {
            mRecognizer.setParameter("engine_type", "mixed");
            mRecognizer.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
            // 设置语法构建路径
            mRecognizer.setParameter(ResourceUtil.GRM_BUILD_PATH, GRAMMAR_PATH);
            // 设置本地识别使用语法id
            mRecognizer.setParameter(SpeechConstant.LOCAL_GRAMMAR, "zgty");
            // 设置识别的门限值
            mRecognizer.setParameter(SpeechConstant.MIXED_THRESHOLD, "60");
//        // 使用8k音频的时候请解开注释
//        // mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
            mRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
            mRecognizer.setParameter(SpeechConstant.NLP_VERSION, "2.0");
            mRecognizer.setParameter("asr_sch", "1");

        } else {
            mRecognizer.setParameter("engine_type", "cloud");
        }
        mRecognizer.setParameter("result_type", "json");
        if (mIsEnglish) {
            mRecognizer.setParameter("language", "en_us");
            mRecognizer.setParameter("accent", (String) null);
        } else {
            mRecognizer.setParameter("language", "zh_cn");
            if (mIsGuangdonghua) {
                mRecognizer.setParameter("accent", "cantonese");
            } else {
                mRecognizer.setParameter("accent", "mandarin");
            }

            mRecognizer.setParameter("domain", "fariat");
            mRecognizer.setParameter("aue", "speex-wb;10");
        }

        mRecognizer.setParameter("vad_bos", PARAM_RECORDING_BASE_TIME);
        mRecognizer.setParameter("vad_eos", PARAM_RECORDING_STOPPING_TIME);
        mRecognizer.setParameter("asr_ptt", "0");
        mRecognizer.setParameter("audio_format", "wav");
        mRecognizer.setParameter("asr_audio_path", Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    private static void makeResultListener() {
        mRecognizerListener = new RecognizerListener() {
            public void onVolumeChanged(int volumn, byte[] arg1) {
                if (LeoSpeech.mViewUpdater != null) {
                    LeoSpeech.mViewUpdater.onVolumeUpdate(volumn);
                }

            }

            public void onResult(RecognizerResult results, boolean arg1) {
                String text = JsonParser.parseIatResult(results.getResultString());
                String sn = null;

                try {
                    JSONObject resultJson = new JSONObject(results.getResultString());
                    sn = resultJson.optString("sn");
                } catch (JSONException var6) {
                    var6.printStackTrace();
                }

                if ("1".equalsIgnoreCase(sn)) {
                    LeoSpeech.mResultProcessor.handleResult(text, "");
                }

            }

            public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            }

            public void onError(SpeechError error) {
                Log.d("wss", "recongise error " + error.getErrorDescription());
                if (LeoSpeech.mResultProcessor != null) {
                    LeoSpeech.mResultProcessor.onError(error.getErrorCode());
                }

            }

            public void onBeginOfSpeech() {
                if (LeoSpeech.mViewUpdater != null) {
                    LeoSpeech.mViewUpdater.onRecordingState();
                }

            }

            public void onEndOfSpeech() {
                if (LeoSpeech.mViewUpdater != null) {
                    LeoSpeech.mViewUpdater.onIdleState();
                }

            }
        };
    }

    static class TtsListener implements SynthesizerListener {
        private ISpeakListener mListener;

        public TtsListener(ISpeakListener listener) {
            this.mListener = listener;
        }

        public void onSpeakBegin() {
        }

        public void onSpeakPaused() {
        }

        public void onSpeakResumed() {
        }

        public void onBufferProgress(int percent, int beginPos, int endPos, String info) {
        }

        public void onSpeakProgress(int percent, int beginPos, int endPos) {
        }

        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
        }

        public void onCompleted(SpeechError error) {
            if (this.mListener != null) {
                this.mListener.onSpeakOver(0);
            }

        }
    }
}
