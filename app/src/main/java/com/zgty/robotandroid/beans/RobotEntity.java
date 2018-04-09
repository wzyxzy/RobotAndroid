package com.zgty.robotandroid.beans;

/**
 * Created by zy on 2017/10/23.
 */

public class RobotEntity {
    private int ids;
    private String trainNum;// 车次
    private String startStation;// 始发站
    private String endStation;// 终点站
    private long status;// 状态
    private String departureTime;// 发车时间
    private String stopTime;// 停靠时间
    private String stationName;// 站名

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
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

    public long getStatus() {
        return status;
    }

    public void setStatus(long status) {
        this.status = status;
    }

    public String getDepartureTime() {
        return departureTime;
    }

    public void setDepartureTime(String departureTime) {
        this.departureTime = departureTime;
    }

    public String getStopTime() {
        return stopTime;
    }

    public void setStopTime(String stopTime) {
        this.stopTime = stopTime;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    @Override
    public String toString() {
        return "RobotEntity{" +
                "ids=" + ids +
                ", trainNum='" + trainNum + '\'' +
                ", startStation='" + startStation + '\'' +
                ", endStation='" + endStation + '\'' +
                ", status=" + status +
                ", departureTime='" + departureTime + '\'' +
                ", stopTime='" + stopTime + '\'' +
                ", stationName='" + stationName + '\'' +
                '}';
    }
}
