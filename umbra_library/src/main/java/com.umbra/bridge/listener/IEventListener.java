package com.umbra.bridge.listener;

/**
 * Created by zhangweiding on 15/10/16.
 */
public interface IEventListener<Resp> {

    String submit(int what);

    Resp executeSync()throws Throwable;

    void onDestroy();


}
