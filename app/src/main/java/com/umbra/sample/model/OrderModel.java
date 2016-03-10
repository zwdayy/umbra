package com.umbra.sample.model;

import com.umbra.activity.UmbraActivity;
import com.umbra.bridge.DefaultAsynModel;
import com.umbra.bridge.listener.IUmbraListener;
import com.umbra.sample.entity.OrderEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangweiding on 15/12/9.
 */
public class OrderModel {

    public void getOrder(IUmbraListener<List<OrderEntity>> activity, int requestCode){
        new DefaultAsynModel<Void,List<OrderEntity>>(activity){
            @Override
            public List<OrderEntity> onExecute(int what, Void condition) throws Throwable {
                Thread.sleep(5000);
                List<OrderEntity> list = new ArrayList<OrderEntity>();
                for(int i = 0;i<12;i++){
                    list.add(new OrderEntity());
                }
                return list;
            }
        }.submit(requestCode);
    }
}
