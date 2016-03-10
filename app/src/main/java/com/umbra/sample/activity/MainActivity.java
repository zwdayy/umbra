package com.umbra.sample.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.umbra.activity.UmbraActivity;
import com.umbra.sample.R;
import com.umbra.sample.adapter.OrderAdapter;
import com.umbra.sample.entity.OrderEntity;
import com.umbra.sample.model.BizLayer;
import com.umbra.sample.widget.UmbraPullListView;

import java.util.List;

public class MainActivity extends UmbraActivity {

    private UmbraPullListView mListView;
    private OrderAdapter mAdapter;
    private static final int WHAT_GETORDER = 0X1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (UmbraPullListView) findViewById(R.id.listview);
        mAdapter = new OrderAdapter(this);
        mListView.setAdapter(mAdapter);
        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
        mListView.post(new Runnable() {
            @Override
            public void run() {
                BizLayer.getOrderModel().getOrder(MainActivity.this, WHAT_GETORDER);
            }
        });

    }

    @Override
    public void onLoading(int what) {
        showLoadingDialog("正在载入数据...");
    }

    @Override
    public void onHandlerResult(int what, Object val) {
        switch (what) {
            case WHAT_GETORDER:
                List<OrderEntity> list = (List<OrderEntity> )val;
                if(list != null && list.size() > 0){
                    mListView.setVisibility(View.VISIBLE);
                    mAdapter.clearAndAdd(list);
                }else {
                    mListView.setVisibility(View.GONE);
                }
                break;
        }
    }

}
