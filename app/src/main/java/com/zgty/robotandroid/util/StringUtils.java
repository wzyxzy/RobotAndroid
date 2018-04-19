package com.zgty.robotandroid.util;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import com.zgty.robotandroid.common.RobotApplication;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by zy on 2018/4/11.
 */

public class StringUtils {
    public static String splitString(String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            result.append(c);
            result.append(" ");

        }
        return result.toString();
    }


    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static String splitString(String s, String split) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            result.append(c);
            result.append(split);

        }
        return result.toString();
    }

    /**
     * 获取手机的IMEI号码
     */
    public static String getPhoneIMEI(Context context) {
        TelephonyManager mTm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 0x0010);
            return "";
        } else {
            String imei = mTm.getDeviceId();
            return imei;
        }

//        String imsi = mTm.getSubscriberId();
//        String mtype = android.os.Build.MODEL; // 手机型号
//        String numer = mTm.getLine1Number(); // 手机号码，有的可得，有的不可得
//        return null;
    }
}
