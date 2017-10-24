package com.zgty.robotandroid.adapters;

import android.content.Context;
import android.widget.TextView;

import com.zgty.robotandroid.R;
import com.zgty.robotandroid.beans.TrainTimeEntity;

import java.util.List;

/**
 * Created by zy on 2017/10/24.
 */

public class TrainTimeAdapter extends WZYBaseAdapter<TrainTimeEntity> {
    public TrainTimeAdapter(List<TrainTimeEntity> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, TrainTimeEntity trainTimeEntity, int indexPostion) {
        TextView name_train_num = (TextView) holder.getView(R.id.name_train_num);
        TextView name_startendTime = (TextView) holder.getView(R.id.name_startendTime);
        TextView name_startendStation = (TextView) holder.getView(R.id.name_startendStation);
        name_train_num.setText(trainTimeEntity.getTrainNum());
        name_startendTime.setText(trainTimeEntity.getStartendTime());
        name_startendStation.setText(trainTimeEntity.getStartendStation());
    }
}
