package com.zgty.robotandroid.activity;

import com.zgty.robotandroid.beans.TrainInfoEntity;

/**
 * Created by zy on 2017/10/23.
 */

public interface TrainInfoView {
    void showError();

    void setTrainInfo(TrainInfoEntity trainInfo);
}
