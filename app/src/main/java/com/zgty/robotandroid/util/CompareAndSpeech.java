package com.zgty.robotandroid.util;

import android.content.Context;

import com.github.promeg.pinyinhelper.Pinyin;
import com.leo.api.abstracts.ISpeakListener;
import com.zgty.robotandroid.beans.BroadCast;

import java.util.List;

import static com.zgty.robotandroid.common.Constant.END_STATION;
import static com.zgty.robotandroid.common.Constant.ROBOT_PLATFORM;
import static com.zgty.robotandroid.common.Constant.START_STATION;
import static com.zgty.robotandroid.common.Constant.STATION_NAME;
import static com.zgty.robotandroid.common.Constant.TRAIN_NUM;
import static com.zgty.robotandroid.common.RobotApplication.canSpeech;

/**
 * Created by zy on 2018/4/16.
 */

public class CompareAndSpeech {

    private List<BroadCast> broadCasts;

    private Context context;

    public CompareAndSpeech(Context context) {
        this.context = context;
    }

    public void setBroadCasts(List<BroadCast> broadCasts) {
        this.broadCasts = broadCasts;
    }

    public void speake(String arrive_time, String leave_time, int train_state) {
        if (broadCasts == null || broadCasts.size() == 0) {
            return;
        }
        for (BroadCast broadCast : broadCasts) {
            if (broadCast.getTrain_state() == 2 || broadCast.getTrain_state() == train_state) {
                String compTime = broadCast.getStatus() == 0 ? arrive_time : leave_time;
                if (TimeUtils.compareAfter(compTime, broadCast.getMinute()) == 0 && canSpeech && !SpeechTools.isBusy(context)) {
                    speakTime(broadCast);
                }
            }
        }
    }


    private void speakTime(final BroadCast broadCast) {
        LeoSpeech.setEnglishMode(false);
        String chn = broadCast.getContent();
        final String[] eng = {broadCast.getContentEnglish()};
        chn = chn.replace("@StartStation", START_STATION);
        chn = chn.replace("@trid", StringUtils.splitString(TRAIN_NUM));
        chn = chn.replace("@TerStation", END_STATION);
        chn = chn.replace("@LocalStation", STATION_NAME);
        chn = chn.replace("@PlatformC", String.valueOf(ROBOT_PLATFORM) + "站台");
        LeoSpeech.speak(chn, new ISpeakListener() {
            @Override
            public void onSpeakOver(int j) {
                LeoSpeech.setEnglishMode(true);

                eng[0] = eng[0].replace("@StartStation", Pinyin.toPinyin(StringUtils.splitString(START_STATION.split("站")[0], "'"), "").toLowerCase());
                eng[0] = eng[0].replace("@trid", StringUtils.splitString(TRAIN_NUM));
                eng[0] = eng[0].replace("@TerStation", Pinyin.toPinyin(StringUtils.splitString(END_STATION.split("站")[0], "'"), "").toLowerCase());
                eng[0] = eng[0].replace("@LocalStation", Pinyin.toPinyin(StringUtils.splitString(STATION_NAME.split("站")[0], "'"), "").toLowerCase());
                eng[0] = eng[0].replace("@PlatformC", "platform " + String.valueOf(ROBOT_PLATFORM));
                LeoSpeech.speak(eng[0], null);
            }
        });
    }
}
