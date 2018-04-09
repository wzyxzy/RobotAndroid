package com.zgty.robotandroid.beans;

import java.util.List;

/**
 * Created by zy on 2017/10/24.
 */

public class TrainTimeList {
    private List<RobotEntity> trainTimeEntities;

    public List<RobotEntity> getTrainTimeEntities() {
        return trainTimeEntities;
    }

    public void setTrainTimeEntities(List<RobotEntity> trainTimeEntities) {
        this.trainTimeEntities = trainTimeEntities;
    }

    @Override
    public String toString() {
        return "TrainTimeList{" +
                "trainTimeEntities=" + trainTimeEntities +
                '}';
    }
}
