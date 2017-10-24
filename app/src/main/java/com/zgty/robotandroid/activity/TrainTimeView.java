package com.zgty.robotandroid.activity;

import com.zgty.robotandroid.beans.TrainTimeEntity;

import java.util.List;

/**
 * Created by zy on 2017/10/24.
 */

public interface TrainTimeView {
    void showError();

    void setTrainTime(TrainTimeEntity[] trainTimeEntities);
}
