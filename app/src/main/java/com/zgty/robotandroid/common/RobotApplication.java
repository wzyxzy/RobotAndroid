package com.zgty.robotandroid.common;

import android.app.Application;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.github.promeg.pinyinhelper.Pinyin;
import com.leo.api.LeoRobot;
import com.zgty.robotandroid.util.LeoSpeech;
import com.leo.api.abstracts.IResultProcessor;
import com.leo.api.abstracts.IRobotListener;
import com.leo.api.nlp.NLPResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.zgty.robotandroid.business.ResultProcessor;
import com.zgty.robotandroid.util.SpeechTools;
import com.zgty.robotandroid.util.VolleyRequest;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

import static com.zgty.robotandroid.common.Constant.RED_DIRECTION;

/**
 * Created by zy on 2017/10/20.
 */

public class RobotApplication extends Application {
    private static RobotApplication instance;
    public static boolean canSpeech = true;

    @Override
    public void onCreate() {
        super.onCreate();
        VolleyRequest.buildRequestQueue(this);
        instance = this;
        initOkGo();
        initStetho();
        initLeo();
    }

    private void initLeo() {
        new LeoRobot().init(this, new IRobotListener() {
            @Override
            public void onTouch() {

            }

            @Override
            public void getElect(int i) {

            }

            @Override
            public void getDistance(int i) {
                Log.d("红外距离", String.valueOf(i));

                if (canSpeech && i <= RED_DIRECTION) {
                    if (SpeechTools.isBusy(instance)) {
                        LeoSpeech.stopSpeak();
                    }
                    canSpeech = false;
                    LeoSpeech.setEnglishMode(false);
                    SpeechTools.speakAndRestartRecognize("您去几车厢？");
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            //execute the task
                            canSpeech = true;
                        }
                    }, 10000);

                }

//                Timer timer = new Timer();
//                timer.schedule(new TimerTask() {
//                    @Override
//                    public void run() {
//                        canSpeech = true;
//                    }
//                }, 10000);

            }

            @Override
            public void onActionStop() {

            }

            @Override
            public void getBoardVersion(String s) {

            }

            @Override
            public void getWifiInfo(String s, String s1, int i) {

            }

            @Override
            public void onReached(String s) {

            }

            @Override
            public void onReachCardSuccess(Object o) {

            }

            @Override
            public void onReachCardError(String s) {

            }

            @Override
            public void onMeetObstacle() {

            }

            @Override
            public void onCharegState(int i) {

            }

            @Override
            public void onPlayVideo(Uri uri) {

            }

            @Override
            public void onMicWakeUp() {

            }

            @Override
            public void onPCSerialDate(byte[] bytes) {

            }
        });
        Pinyin.init(null);


    }

    private void initStetho() {
        Stetho.initializeWithDefaults(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    private void initOkGo() {
//        OkGo.getInstance().init(this);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
//        //使用sp保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
//        //使用数据库保持cookie，如果cookie不过期，则一直有效
//        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));
//        //使用内存保持cookie，app退出后，cookie消失
//        builder.cookieJar(new CookieJarImpl(new MemoryCookieStore()));
        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);

        OkGo.getInstance().init(this)                             //必须调用初始化
                .setOkHttpClient(builder.build())                 //建议设置OkHttpClient，不设置将使用默认的
                .setCacheMode(CacheMode.REQUEST_FAILED_READ_CACHE)                //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)                //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(3);                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
//                .addCommonHeaders(headers)                     //全局公共头
//                .addCommonParams(params);                      //全局公共参数

    }

    public static RobotApplication getInstance() {
        return instance;
    }
}
