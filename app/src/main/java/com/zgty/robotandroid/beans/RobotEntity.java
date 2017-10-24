package com.zgty.robotandroid.beans;

/**
 * Created by zy on 2017/10/23.
 */

public class RobotEntity {
    /**
     * ids : 003
     * trainNum : T123
     * startStation : 北京站
     * endStation : 济南站
     * curTrainNo : 4
     * arriveTrainNo : 10
     * status : 晚点5分钟
     * fixArrivalTime : 8：20
     * actArrivalTime : 8:23
     * platform : 8
     * departureTime : 8:25
     * fixDepartureTime : 8：30
     * stopTime : 5分钟
     * createTime : 1507866096000
     * updateTime : null
     */

    private String ids;
    private String trainNum;//车次
    private String startStation;//始发站
    private String endStation;//终点站
    private int curTrainNo;//当前车厢号
    private int arriveTrainNo;//前往车厢号
    private String status;//状态
    private String fixArrivalTime;//固定到达时间
    private String actArrivalTime;//实际到达时间
    private int platform;//站台
    private String departureTime;//发车时间
    private String fixDepartureTime;//固定发车时间
    private String stopTime;//停靠时间
    private long createTime;//创建日期
    private Object updateTime;//更新日期

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

    public String getStartStation() {
        return startStation;
    }

    public void setStartStation(String startStation) {
        this.startStation = startStation;
    }

    public String getEndStation() {
        return endStation;
    }

    public void setEndStation(String endStation) {
        this.endStation = endStation;
    }

    public int getCurTrainNo() {
        return curTrainNo;
    }

    public void setCurTrainNo(int curTrainNo) {
        this.curTrainNo = curTrainNo;
    }

    public int getArriveTrainNo() {
        return arriveTrainNo;
    }

    public void setArriveTrainNo(int arriveTrainNo) {
        this.arriveTrainNo = arriveTrainNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFixArrivalTime() {
        return fixArrivalTime;
    }

    public void setFixArrivalTime(String fixArrivalTime) {
        this.fixArrivalTime = fixArrivalTime;
    }

    public String getActArrivalTime() {
        return actArrivalTime;
    }

    public void setActArrivalTime(String actArrivalTime) {
        this.actArrivalTime = actArrivalTime;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getFixDepartureTime() {
        return fixDepartureTime;
    }

    public void setFixDepartureTime(String fixDepartureTime) {
        this.fixDepartureTime = fixDepartureTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public Object getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Object updateTime) {
        this.updateTime = updateTime;
    }
}
