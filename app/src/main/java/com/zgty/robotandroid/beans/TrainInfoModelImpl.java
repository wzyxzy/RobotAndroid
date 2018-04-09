package com.zgty.robotandroid.beans;

import android.util.Log;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zgty.robotandroid.common.Constant;
import com.zgty.robotandroid.presenter.OnTrainInfoListener;
import com.zgty.robotandroid.util.VolleyRequest;

/**
 * Created by zy on 2017/10/23.
 */

public class TrainInfoModelImpl implements TrainInfoModel {
    @Override
    public void loadTrainInfo(String robot_mac, final OnTrainInfoListener listener) {

         /*数据层操作*/
        VolleyRequest.newInstance().newGsonRequest(Constant.HTTP_HOST + "findTrainInfo?robot_mac=" + robot_mac,
                TrainInfoEntity.class, new Response.Listener<TrainInfoEntity>() {

                    @Override
                    public void onResponse(TrainInfoEntity response) {
                        if (response != null) {
                            listener.onSuccess(response);
                        } else {
                            listener.onError();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        listener.onError();
                    }
                });
    }
}
