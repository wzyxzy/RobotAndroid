package com.zgty.robotandroid.beans;

/**
 * Created by zy on 2017/10/23.
 * 机器人配置信息
 */

public class RobotManageEntity {
    /**
     * ids : 1
     * curTrainNo : 8
     * direction : 向前
     */

    private int ids;
    private String curTrainNo;//当前车厢号
    private String lastTrainNo;// 最大车厢号
    private String direction;//方向
    private int platform;//站台
    private int redDirection;// 红外距离

    public int getIds() {
        return ids;
    }

    public void setIds(int ids) {
        this.ids = ids;
    }

    public String getCurTrainNo() {
        return curTrainNo;
    }

    public void setCurTrainNo(String curTrainNo) {
        this.curTrainNo = curTrainNo;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public String getLastTrainNo() {
        return lastTrainNo;
    }

    public void setLastTrainNo(String lastTrainNo) {
        this.lastTrainNo = lastTrainNo;
    }

    public int getRedDirection() {
        return redDirection;
    }

    public void setRedDirection(int redDirection) {
        this.redDirection = redDirection;
    }

    @Override
    public String toString() {
        return "RobotManageEntity{" +
                "ids=" + ids +
                ", curTrainNo='" + curTrainNo + '\'' +
                ", lastTrainNo='" + lastTrainNo + '\'' +
                ", direction='" + direction + '\'' +
                ", platform=" + platform +
                ", redDirection=" + redDirection +
                '}';
    }
}
