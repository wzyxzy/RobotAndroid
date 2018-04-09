package com.zgty.robotandroid.beans;

/**
 * Created by zy on 2017/10/23.
 */

public class TrainInfoEntity {

    /**
     * msg : 200
     * robotManageEntity : {"ids":1,"curTrainNo":"8","direction":"向前"}
     * robotEntity : {"ids":"003","trainNum":"T123","startStation":"北京站","endStation":"济南站","curTrainNo":4,"arriveTrainNo":10,"status":"晚点5分钟","fixArrivalTime":"8：20","actArrivalTime":"8:23","platform":8,"departureTime":"8:25","fixDepartureTime":"8：30","stopTime":"5分钟","createTime":1507866096000,"updateTime":null}
     */

    private int msg;
    private RobotManageEntity robotManageEntity;
//    private RobotEntity robotEntity;

    public int getMsg() {
        return msg;
    }

    public void setMsg(int msg) {
        this.msg = msg;
    }

    public RobotManageEntity getRobotManageEntity() {
        return robotManageEntity;
    }

    public void setRobotManageEntity(RobotManageEntity robotManageEntity) {
        this.robotManageEntity = robotManageEntity;
    }

    @Override
    public String toString() {
        return "TrainInfoEntity{" +
                "msg=" + msg +
                ", robotManageEntity=" + robotManageEntity +
                '}';
    }

    //    public RobotEntity getRobotEntity() {
//        return robotEntity;
//    }
//
//    public void setRobotEntity(RobotEntity robotEntity) {
//        this.robotEntity = robotEntity;
//    }

}
