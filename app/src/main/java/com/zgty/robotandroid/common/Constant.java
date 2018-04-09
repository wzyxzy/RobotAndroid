package com.zgty.robotandroid.common;

/**
 * Created by zy on 2017/10/23.
 */

public class Constant {
    public static final String HTTP_HOST = "http://192.168.18.77:8080/StationRobot/";//主网址
    public static final String BROADCASTACTIONLIST = "com.zgty.robotlist";//刷新list广播
    public static final int BROADCASTLISTTIME = 2000;//刷新list频率
    public static final String BROADCASTACTIONINFO = "com.zgty.robotinfo";//刷新info广播
    public static final int BROADCASTINFOTIME = 10000;//刷新info频率
    public static final String SERVICE_INTENT_INFO = "SERVICE_INTENT_INFO";//传递service名字
    public static final String SERVICE_INTENT_LIST = "SERVICE_INTENT_LIST";//传递service名字
    public static String CHOOSE_USER_NUM_ID = "no1";//选择车厢号
    public static final int SERVICE_INFO = 1004;//传递类型为info
    public static final int SERVICE_LIST = 1002;//传递类型为list
    public static String NOW_STATION = "8";//当前机器人位置
    public static int ROBOT_PLATFORM = 8;//机器人所在站台
    public static String ROBOT_MAC = "ABCD";//当前机器人MAC
    public static String ROBOT_DIR = "向前";//当前机器人朝向
    public static String BEGIN_TRAIN_NUM = "T123";//当前车次
}
