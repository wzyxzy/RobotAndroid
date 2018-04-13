package com.zgty.robotandroid.activity;

import com.zgty.robotandroid.beans.BroadCast;
import com.zgty.robotandroid.beans.RobotEntity;
import com.zgty.robotandroid.presenter.BroadCastPresenterImpl;

/**
 * Created by zy on 2018/4/11.
 */

public interface BroadCastInfo {
    void showError();

    void setTrainTime(BroadCast[] broadCasts);
}
