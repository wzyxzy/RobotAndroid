package com.zgty.robotandroid.presenter;

import com.zgty.robotandroid.activity.TrainTimeView;
import com.zgty.robotandroid.beans.RobotEntity;
import com.zgty.robotandroid.beans.TrainTimeModel;
import com.zgty.robotandroid.beans.TrainTimeModelImpl;

import java.util.List;

/**
 * Created by zy on 2017/10/24.
 */

public class TrainTimePresenterImpl implements TrainTimePresenter, OnTrainTimeListener {

    private TrainTimeModel trainTimeModel;
    private TrainTimeView trainTimeView;

    public TrainTimePresenterImpl(TrainTimeView trainTimeView) {
        this.trainTimeView = trainTimeView;
        trainTimeModel = new TrainTimeModelImpl();
    }

    @Override
    public void getTrainTime(String trainNum) {
        trainTimeModel.loadTrainTime(trainNum, this);

    }

    @Override
    public void onSuccess(RobotEntity[] robotEntities) {
        trainTimeView.setTrainTime(robotEntities);

    }

    @Override
    public void onError() {
        trainTimeView.showError();

    }
}
