package com.zgty.robotandroid.common;

/**
 * Created by zy on 2017/10/23.
 */

public class Constant {
    public static final String HTTP_HOST = "http://192.168.18.154:8090/robot/";//主网址
    public static final String BROADCASTACTIONLIST = "com.zgty.robotlist";//刷新list广播
    public static final int BROADCASTLISTTIME = 2000;//刷新list频率
    public static final String BROADCASTACTIONINFO = "com.zgty.robotinfo";//刷新info广播
    public static final int BROADCASTINFOTIME = 10000;//刷新info频率
    public static final String SERVICE_INTENT_INFO = "SERVICE_INTENT_INFO";//传递service名字
    public static final String SERVICE_INTENT_LIST = "SERVICE_INTENT_LIST";//传递service名字
    public static final int SERVICE_INFO = 1004;//传递类型为info
    public static final int SERVICE_LIST = 1002;//传递类型为list
    public static String NOW_STATION = "8";//当前站台
    public static String BEGIN_TRAIN_NUM = "T123";//当前车次
}
