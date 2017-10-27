package com.zgty.robotandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.SupportActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.zgty.robotandroid.R;
import com.zgty.robotandroid.adapters.ChooseTrainNumAdapter;
import com.zgty.robotandroid.beans.ChooseTrainNum;
import com.zgty.robotandroid.common.Constant;
import com.zgty.robotandroid.presenter.ChooseTrainNumPresenter;
import com.zgty.robotandroid.presenter.ChooseTrainNumPresenterImpl;
import com.zgty.robotandroid.util.ToastUtil;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ChooseTrainNo extends SupportActivity implements ChooseTrainNumView, View.OnClickListener {


    private GridView gridview_list;
    private TextView back_button;
    private ChooseTrainNumAdapter chooseTrainNumAdapter;
    private List<ChooseTrainNum> chooseTrainNums;
    private ChooseTrainNumPresenter chooseTrainNumPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_train_no);
        initView();
    }

    @Override
    public void showError() {
        ToastUtil.ShowShort(this, "数据出错");
    }

    @Override
    public void setChoosedNum(ChooseTrainNum[] choosedNums) {
        chooseTrainNumAdapter.updateRes(Arrays.asList(choosedNums));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.finish();
        return true;

    }

    private void initView() {

        gridview_list = findViewById(R.id.gridview_list);
        back_button = findViewById(R.id.back_button);
        back_button.setOnClickListener(this);
        chooseTrainNums = new ArrayList<>();
        chooseTrainNumAdapter = new ChooseTrainNumAdapter(chooseTrainNums, this, R.layout.item_choose);
        gridview_list.setAdapter(chooseTrainNumAdapter);
        chooseTrainNumPresenter = new ChooseTrainNumPresenterImpl(this);
        chooseTrainNumPresenter.getTrainNum();
        gridview_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                chooseNum(position);

            }
        });
    }

    private void chooseNum(int position) {
        ToastUtil.ShowShort(this,"position:"+position);
        Constant.CHOOSE_USER_NUM_ID = chooseTrainNums.get(position).getTrain_id();
        setResult(22);
        this.finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.back_button:
                finish();
                break;
        }
    }
}
