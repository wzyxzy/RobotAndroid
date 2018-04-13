package com.zgty.robotandroid.beans;

import com.zgty.robotandroid.presenter.OnBroadCastListener;

/**
 * Created by zy on 2018/4/11.
 */

public interface BroadCastModel {
    void getBroadCast(String platform, OnBroadCastListener onBroadCastListener);
}
