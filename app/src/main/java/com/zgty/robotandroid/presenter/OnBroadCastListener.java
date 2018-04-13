package com.zgty.robotandroid.presenter;

import com.zgty.robotandroid.beans.BroadCast;

/**
 * Created by zy on 2018/4/11.
 */

public interface OnBroadCastListener {

    void onSuccess(BroadCast[] response);

    void onError();

}
