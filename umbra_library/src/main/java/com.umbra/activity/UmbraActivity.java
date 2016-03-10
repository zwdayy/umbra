package com.umbra.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.umbra.bridge.helper.UmbraManager;
import com.umbra.bridge.listener.IUmbraListener;
import com.umbra.bridge.pool.AsynEventException;

/**
 * Created by zhangweiding on 15/12/9.
 */
public class UmbraActivity<E> extends FragmentActivity implements IUmbraListener<E> {

    private boolean isShown;

    private boolean isDestory;

    private String mBridgeKey;

    private ProgressDialog mLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBridgeKey = UmbraManager.register(this);
    }

    @Override
    public void finish() {
        UmbraManager.unRegister(mBridgeKey);
        super.finish();
    }


    public String getUmbraKey(){
        return mBridgeKey;
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
        dismissLoadingDialog();
    }

    @Override
    protected void onResume() {
        super.onResume();
        isShown = true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isShown = false;
    }

    @Override
    protected void onDestroy() {
        isDestory = true;
        dismissLoadingDialog();
        super.onDestroy();
    }


    public boolean isShown(){
        return isShown;
    }

    public boolean isDestory(){
        return isDestory;
    }

    public void showLoadingDialog(final String msg )
    {
        if (!isFinishing() || isShown){
            if(mLoading ==null)
                mLoading =new ProgressDialog(this);
            mLoading.setCanceledOnTouchOutside(false);
            mLoading.setMessage(msg);
            mLoading.show();
        }
    }

    public void dismissLoadingDialog(){
        if(mLoading!=null&& mLoading.isShowing() && !isFinishing()){
            mLoading.dismiss();
        }
    }
}
