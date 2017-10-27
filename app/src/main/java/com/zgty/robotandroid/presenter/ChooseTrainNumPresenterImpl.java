package com.zgty.robotandroid.presenter;

import com.zgty.robotandroid.activity.ChooseTrainNumView;
import com.zgty.robotandroid.beans.ChooseTrainNum;
import com.zgty.robotandroid.beans.ChooseTrainNumModel;
import com.zgty.robotandroid.beans.ChooseTrainNumModelImpl;

/**
 * Created by zy on 2017/10/26.
 */

public class ChooseTrainNumPresenterImpl implements ChooseTrainNumPresenter, OnChooseTrainNumListener {
    private ChooseTrainNumModel chooseTrainNumModel;
    private ChooseTrainNumView chooseTrainNumView;

    public ChooseTrainNumPresenterImpl(ChooseTrainNumView chooseTrainNumView) {
        this.chooseTrainNumView = chooseTrainNumView;
        chooseTrainNumModel = new ChooseTrainNumModelImpl();
    }

    @Override
    public void getTrainNum() {
        chooseTrainNumModel.loadTrainNum(this);
    }

    @Override
    public void onSuccess(ChooseTrainNum[] chooseTrainNums) {
        chooseTrainNumView.setChoosedNum(chooseTrainNums);
    }

    @Override
    public void onError() {
        chooseTrainNumView.showError();
    }
}
