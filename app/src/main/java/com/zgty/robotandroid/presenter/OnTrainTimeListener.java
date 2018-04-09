package com.zgty.robotandroid.presenter;

import com.zgty.robotandroid.beans.RobotEntity;

import java.util.List;

/**
 * Created by zy on 2017/10/24.
 */

public interface OnTrainTimeListener {
    /**
     * 成功时回调
     */
    void onSuccess(RobotEntity[] robotEntities);

    /**
     * 失败时回调
     */
    void onError();
}
