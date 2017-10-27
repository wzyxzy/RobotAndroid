package com.zgty.robotandroid.activity;

import com.zgty.robotandroid.beans.ChooseTrainNum;
import com.zgty.robotandroid.beans.TrainInfoEntity;

/**
 * Created by zy on 2017/10/26.
 */

public interface ChooseTrainNumView {
    void showError();

    void setChoosedNum(ChooseTrainNum[] choosedNums);
}
