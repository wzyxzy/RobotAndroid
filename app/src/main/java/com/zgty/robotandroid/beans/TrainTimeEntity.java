package com.zgty.robotandroid.beans;

/**
 * Created by zy on 2017/10/23.
 */

public class TrainTimeEntity {

    /**
     * ids : 001
     * trainNum : T123
     * startendTime : 06:23/12:34
     * startendStation : 重庆/武汉
     */

    private String ids;
    private String trainNum;//车次
    private String startendTime;//发到时间
    private String startendStation;//发到站

    public String getIds() {
        return ids;
    }

    public void setIds(String ids) {
        this.ids = ids;
    }

    public String getTrainNum() {
        return trainNum;
    }

    public void setTrainNum(String trainNum) {
        this.trainNum = trainNum;
    }

    public String getStartendTime() {
        return startendTime;
    }

    public void setStartendTime(String startendTime) {
        this.startendTime = startendTime;
    }

    public String getStartendStation() {
        return startendStation;
    }

    public void setStartendStation(String startendStation) {
        this.startendStation = startendStation;
    }
}
