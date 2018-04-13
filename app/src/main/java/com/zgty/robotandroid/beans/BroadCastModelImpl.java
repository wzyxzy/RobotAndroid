package com.zgty.robotandroid.beans;


import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.zgty.robotandroid.common.Constant;
import com.zgty.robotandroid.presenter.OnBroadCastListener;
import com.zgty.robotandroid.util.VolleyRequest;

/**
 * Created by zy on 2018/4/11.
 */

public class BroadCastModelImpl implements BroadCastModel {
    @Override
    public void getBroadCast(String platform, final OnBroadCastListener onBroadCastListener) {
        VolleyRequest.newInstance().newGsonRequest(Constant.HTTP_HOST + "findTrainBroad?platform=" + platform, BroadCast[].class, new Response.Listener<BroadCast[]>() {
            @Override
            public void onResponse(BroadCast[] response) {

                if (response != null) {
                    onBroadCastListener.onSuccess(response);
                } else {
                    onBroadCastListener.onError();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                onBroadCastListener.onError();
            }
        });
    }
}
