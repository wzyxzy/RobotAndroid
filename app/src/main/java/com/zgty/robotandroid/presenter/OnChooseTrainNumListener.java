package com.zgty.robotandroid.presenter;

import com.zgty.robotandroid.beans.ChooseTrainNum;

/**
 * Created by zy on 2017/10/26.
 */

public interface OnChooseTrainNumListener {
    /**
     * 成功时回调
     */
    void onSuccess(ChooseTrainNum[] chooseTrainNum);

    /**
     * 失败时回调
     */
    void onError();
}
