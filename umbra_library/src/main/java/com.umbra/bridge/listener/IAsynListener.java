package com.umbra.bridge.listener;

/**
 * Created by zhangweiding on 15/10/19.
 */
public interface IAsynListener<Cond,Resp> extends IBaseVListener<Resp> {


    Resp onExecute(int what, Cond condition) throws Throwable;

    Cond getCondition();


}
