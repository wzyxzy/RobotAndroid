package com.zgty.robotandroid.presenter;

import com.zgty.robotandroid.beans.TrainInfoEntity;

/**
 * Created by zy on 2017/10/23.
 * 在Presenter层实现，给Model层回调，更改View层的状态，确保Model层不直接操作View层
 */

public interface OnTrainInfoListener {
    /**
     * 成功时回调
     */
    void onSuccess(TrainInfoEntity trainInfoEntity);

    /**
     * 失败时回调
     */
    void onError();
}
