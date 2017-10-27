package com.zgty.robotandroid.adapters;

import android.content.Context;
import android.widget.Button;
import android.widget.TextView;

import com.zgty.robotandroid.R;
import com.zgty.robotandroid.beans.ChooseTrainNum;

import java.util.List;

/**
 * Created by zy on 2017/10/27.
 */

public class ChooseTrainNumAdapter extends WZYBaseAdapter<ChooseTrainNum> {
    public ChooseTrainNumAdapter(List<ChooseTrainNum> data, Context context, int layoutRes) {
        super(data, context, layoutRes);
    }

    @Override
    public void bindData(ViewHolder holder, ChooseTrainNum chooseTrainNum, int indexPostion) {
        TextView choose_button_item = (TextView) holder.getView(R.id.button_item);
        choose_button_item.setText(chooseTrainNum.getTrain_num_name());
//        choose_button_item.setMaxHeight(choose_button_item.getWidth());
//        choose_button_item.setMinimumHeight(choose_button_item.getWidth());

    }
}
