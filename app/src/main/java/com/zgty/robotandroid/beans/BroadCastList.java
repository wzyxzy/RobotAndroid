package com.zgty.robotandroid.beans;

import java.util.List;

/**
 * Created by zy on 2018/4/11.
 */

public class BroadCastList {
    private List<BroadCast> broadCasts;

    public List<BroadCast> getBroadCasts() {
        return broadCasts;
    }

    public void setBroadCasts(List<BroadCast> broadCasts) {
        this.broadCasts = broadCasts;
    }

    @Override
    public String toString() {
        return "BroadCastList{" +
                "broadCasts=" + broadCasts +
                '}';
    }
}
