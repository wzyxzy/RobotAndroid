//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.zgty.robotandroid.util;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.SynthesizerListener;
import com.leo.api.abstracts.IResultProcessor;
import com.leo.api.abstracts.ISpeakListener;
import com.leo.api.abstracts.IViewUpdater;
import com.leo.api.util.GrammarManager;
import com.leo.api.util.JsonParser;
import java.util.Locale;
import org.json.JSONException;
import org.json.JSONObject;

public class LeoSpeech {
    private static SpeechRecognizer mRecognizer;
    private static RecognizerListener mRecognizerListener;
    private static IResultProcessor mResultProcessor;
    private static IViewUpdater mViewUpdater;
    private static SpeechSynthesizer mTts;
    private static boolean mIsGuangdonghua = false;
    private static boolean mIsEnglish = false;
    private static boolean mIsCmd = false;
    private static String PARAM_RECORDING_BASE_TIME = "5000";
    private static String PARAM_RECORDING_STOPPING_TIME = "1800";
    private static String mVoicerCN = "xiaoyan";
    private static String mVoicerEN = "catherine";
    private static InitListener mInitListener = new InitListener() {
        public void onInit(int code) {
            if(code != 0) {
                Log.d("wss", "初始化失败，错误码：" + code);
            }

        }
    };

    public LeoSpeech() {
    }

    public static void init(Context context, IResultProcessor processor) {
        mIsEnglish = !isZh(context);
        SpeechUtility.createUtility(context.getApplicationContext(), "appid=5a705940");
        mRecognizer = SpeechRecognizer.createRecognizer(context, mInitListener);
        mTts = SpeechSynthesizer.createSynthesizer(context, mInitListener);
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
        if(mViewUpdater != null) {
            mViewUpdater.onIdleState();
        }

    }

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
                if(error != null) {
                    Log.d("wss", "语法构建失败,错误码：" + error.getErrorCode());
                }

            }
        });
    }

    private static void setTtsParam() {
        mTts.setParameter("params", (String)null);
        if(mIsEnglish) {
            mTts.setParameter("engine_type", "cloud");
            mTts.setParameter("voice_name", mVoicerEN);
        } else {
            mTts.setParameter("voice_name", "");
            if(mIsGuangdonghua) {
                mTts.setParameter("accent", "cantonese");
                mTts.setParameter("engine_type", "cloud");
            } else {
                mTts.setParameter("accent", "mandarin");
                mTts.setParameter("engine_type", "local");
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

    private static void setRecongizeParam() {
        mRecognizer.setParameter("params", (String)null);
        mRecognizer.setParameter("engine_type", "cloud");
        mRecognizer.setParameter("result_type", "json");
        if(mIsEnglish) {
            mRecognizer.setParameter("language", "en_us");
            mRecognizer.setParameter("accent", (String)null);
        } else {
            mRecognizer.setParameter("language", "zh_cn");
            if(mIsGuangdonghua) {
                mRecognizer.setParameter("accent", "cantonese");
            } else {
                mRecognizer.setParameter("accent", "mandarin");
            }

            mRecognizer.setParameter("domain", "fariat");
            mRecognizer.setParameter("aue", "speex-wb;10");
        }

//        mRecognizer.setParameter("vad_bos", PARAM_RECORDING_BASE_TIME);
//        mRecognizer.setParameter("vad_eos", PARAM_RECORDING_STOPPING_TIME);
        mRecognizer.setParameter("asr_ptt", "0");
        mRecognizer.setParameter("audio_format", "wav");
        mRecognizer.setParameter("asr_audio_path", Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    private static void makeResultListener() {
        mRecognizerListener = new RecognizerListener() {
            public void onVolumeChanged(int volumn, byte[] arg1) {
                if(LeoSpeech.mViewUpdater != null) {
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

                if("1".equalsIgnoreCase(sn)) {
                    LeoSpeech.mResultProcessor.handleResult(text, "");
                }

            }

            public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
            }

            public void onError(SpeechError error) {
                Log.d("wss", "recongise error " + error.getErrorDescription());
                if(LeoSpeech.mResultProcessor != null) {
                    LeoSpeech.mResultProcessor.onError(error.getErrorCode());
                }

            }

            public void onBeginOfSpeech() {
                if(LeoSpeech.mViewUpdater != null) {
                    LeoSpeech.mViewUpdater.onRecordingState();
                }

            }

            public void onEndOfSpeech() {
                if(LeoSpeech.mViewUpdater != null) {
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
            if(this.mListener != null) {
                this.mListener.onSpeakOver(0);
            }

        }
    }
}
