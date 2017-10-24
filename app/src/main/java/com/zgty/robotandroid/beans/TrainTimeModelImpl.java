package com.zgty.robotandroid.beans;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.gson.reflect.TypeToken;
import com.zgty.robotandroid.common.Constant;
import com.zgty.robotandroid.presenter.OnTrainTimeListener;
import com.zgty.robotandroid.util.VolleyRequest;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zy on 2017/10/24.
 */

public class TrainTimeModelImpl implements TrainTimeModel {
    @Override
    public void loadTrainTime(String trainNum, final OnTrainTimeListener onTrainTimeListener) {
//        final Type founderListType = new TypeToken<ArrayList<TrainTimeEntity>>() {
//        }.getType();
        VolleyRequest.newInstance().newGsonRequest(Constant.HTTP_HOST + "findTrainTime?trainNum=" + trainNum, TrainTimeEntity[].class, new Response.Listener<TrainTimeEntity[]>() {
            @Override
            public void onResponse(TrainTimeEntity[] response) {

                if (response != null) {
                    onTrainTimeListener.onSuccess(response);
                } else {
                    onTrainTimeListener.onError();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onTrainTimeListener.onError();
            }
        });
    }
}
