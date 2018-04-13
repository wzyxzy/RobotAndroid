package com.zgty.robotandroid.presenter;

import com.zgty.robotandroid.activity.BroadCastInfo;
import com.zgty.robotandroid.beans.BroadCast;
import com.zgty.robotandroid.beans.BroadCastModel;
import com.zgty.robotandroid.beans.BroadCastModelImpl;

/**
 * Created by zy on 2018/4/11.
 */

public class BroadCastPresenterImpl implements BroadCastPresenter, OnBroadCastListener {
    private BroadCastModel broadCastModel;
    private BroadCastInfo broadCastInfo;

    public BroadCastPresenterImpl(BroadCastInfo broadCastInfo) {
        this.broadCastInfo = broadCastInfo;
        broadCastModel = new BroadCastModelImpl();

    }

    @Override
    public void getBroadCast(String platform) {
        broadCastModel.getBroadCast(platform, this);
    }

    @Override
    public void onSuccess(BroadCast[] response) {
        broadCastInfo.setTrainTime(response);
    }

    @Override
    public void onError() {
        broadCastInfo.showError();
    }
}
