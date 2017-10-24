package com.zgty.robotandroid.beans;

import com.zgty.robotandroid.presenter.OnTrainInfoListener;

/**
 * Created by zy on 2017/10/23.
 */

public interface TrainInfoModel {
    void loadTrainInfo(String curTrainNo, OnTrainInfoListener onTrainInfoListener);
}
