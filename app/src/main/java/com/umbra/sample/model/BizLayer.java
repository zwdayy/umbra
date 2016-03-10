package com.umbra.sample.model;

/**
 * Created by zhangweiding on 15/12/9.
 */
public class BizLayer {


    static final OrderModel mOrder = new OrderModel();

    private BizLayer(){

    }

    public static OrderModel getOrderModel(){
        return mOrder;
    }
}
