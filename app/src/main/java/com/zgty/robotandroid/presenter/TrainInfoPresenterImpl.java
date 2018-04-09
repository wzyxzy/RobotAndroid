package com.zgty.robotandroid.presenter;

import com.zgty.robotandroid.activity.TrainInfoView;
import com.zgty.robotandroid.beans.TrainInfoEntity;
import com.zgty.robotandroid.beans.TrainInfoModel;
import com.zgty.robotandroid.beans.TrainInfoModelImpl;

/**
 * Created by zy on 2017/10/23.
 */

public class TrainInfoPresenterImpl implements TrainInfoPresenter, OnTrainInfoListener {
    private TrainInfoView trainInfoView;
    private TrainInfoModel trainInfoModel;


    public TrainInfoPresenterImpl(TrainInfoView trainInfoView) {
        this.trainInfoView = trainInfoView;
        trainInfoModel = new TrainInfoModelImpl();
    }


    @Override
    public void getTrainInfo(String robot_mac) {
        trainInfoModel.loadTrainInfo(robot_mac, this);
    }

    @Override
    public void onSuccess(TrainInfoEntity trainInfoEntity) {
        trainInfoView.setTrainInfo(trainInfoEntity);
    }

    @Override
    public void onError() {
        trainInfoView.showError();
    }
}
