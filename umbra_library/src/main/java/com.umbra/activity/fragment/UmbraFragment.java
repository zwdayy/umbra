package com.umbra.activity.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.umbra.bridge.helper.UmbraManager;
import com.umbra.bridge.listener.IUmbraListener;
import com.umbra.bridge.pool.AsynEventException;

/**
 * Created by zhangweiding on 15/12/23.
 */
public class UmbraFragment<E> extends Fragment implements IUmbraListener<E>{

    private String mUmbraKey;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUmbraKey = UmbraManager.register(this);
    }

    @Override
    public String getUmbraKey() {
        return mUmbraKey;
    }

    @Override
    public void onLoading(int what) {

    }

    @Override
    public void onError(int what, String message, AsynEventException e) {

    }

    @Override
    public void onHandlerResult(int what, E val) {

    }

    @Override
    public void beforeHandlerMessage(int what) {

    }

    @Override
    public void onDestroy() {
        UmbraManager.unRegister(mUmbraKey);
        super.onDestroy();
    }
}
