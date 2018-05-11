package com.zgty.robotandroid.util;

import android.util.Log;

import com.eastrobot.ask.sdk.AskRequest;
import com.eastrobot.ask.sdk.AskResponse;
import com.eastrobot.ask.sdk.AskService;
import com.eastrobot.ask.sdk.CloudNotInitializedException;
import com.eastrobot.ask.sdk.CloudServiceFactory;
import com.eastrobot.ask.sdk.SynthRequest;
import com.eastrobot.ask.sdk.SynthResponse;
import com.eastrobot.ask.sdk.SynthService;
import com.eastrobot.ask.utils.Constant;

import java.io.IOException;
import java.net.MalformedURLException;

public class IbotUtils {
    private static String appKey = "JK1anbmjZmtQ";
    private static String appSecret = "FIlyUIKhYeQe4qS3z2aV";

    public static String askIBot(String askWords) {
        // TODO Auto-generated method stub
        //智能问答
        AskRequest askRequest = new AskRequest(appKey, appSecret, askWords,
                Constant.PRIMARY_TYPE, null, Constant.WEIXIN_PLATFORM);
        AskService askService = CloudServiceFactory.getInstance()
                .createAskService();
        askService.init(null);
        AskResponse askResponse = null;
//        System.out.println(askRequest.getQuestion());
        try {
            askResponse = askService.ask(askRequest);

//            System.out.println(askResponse.getContent());
//            System.out.println(askResponse.getStatus() + "");
//            System.out.println(askResponse.getWords());
        } catch (CloudNotInitializedException e) {
            e.printStackTrace();
        }
        Log.d("answer", askResponse.getContent());
        assert askResponse != null;

        return askResponse.getContent();

//        //语音合成
//        SynthRequest synthRequest = new SynthRequest(appKey, appSecret, null,
//                question);
//        SynthService synthService = CloudServiceFactory.getInstance()
//                .createSynthService();
//        synthService.init(null);
//        SynthResponse synthResponse = null;
//        try {
//            synthResponse = synthService.synth(synthRequest);
//        } catch (CloudNotInitializedException e) {
//            e.printStackTrace();
//        }

        //语音识别
//		File file = new File(exampleFile);
//		byte[] data = FileUtils.readFileToByteArray(file);
//		RecogRequest recogRequest = new RecogRequest(appKey, appSecret, null,
//				data);
//		RecogService recogService = CloudServiceFactory.getInstance()
//				.createRecogService();
//		recogService.init(null);
//		RecogResponse recogResponse = null;
//		try {
//			recogResponse = recogService.recog(recogRequest);
//		} catch (CloudNotInitializedException e) {
//			e.printStackTrace();
//		}
    }
}