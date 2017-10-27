package com.zgty.robotandroid.beans;

import com.zgty.robotandroid.presenter.OnChooseTrainNumListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2017/10/26.
 */

public class ChooseTrainNumModelImpl implements ChooseTrainNumModel {
    private int maxNum = 16;

    @Override
    public void loadTrainNum(OnChooseTrainNumListener chooseTrainNumListener) {
        //使用数据库请求方式
        //假数据填充
        List<ChooseTrainNum> chooseTrainNums = new ArrayList<>();
        for (int i = 0; i < maxNum; i++) {
            ChooseTrainNum chooseTrainNum = new ChooseTrainNum();
            chooseTrainNum.setTrain_id("no" + (i + 1));
            chooseTrainNum.setTrain_num_name(String.valueOf(i + 1));
            chooseTrainNums.add(chooseTrainNum);
        }
        chooseTrainNumListener.onSuccess(chooseTrainNums.toArray(new ChooseTrainNum[maxNum]));
    }
}
