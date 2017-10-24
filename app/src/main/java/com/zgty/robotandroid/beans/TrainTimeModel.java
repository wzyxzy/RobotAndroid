package com.zgty.robotandroid.beans;

import com.zgty.robotandroid.presenter.OnTrainTimeListener;

/**
 * Created by zy on 2017/10/24.
 */

public interface TrainTimeModel {
    void loadTrainTime(String trainNum, OnTrainTimeListener onTrainTimeListener);
}
