package com.umbra.bridge.helper;

import com.umbra.bridge.listener.IUmbraListener;

/**
 * Created by zhangweiding on 15/10/27.
 */
public class UmbraAsynStub {


    private String mUmbraKey;

    public UmbraAsynStub(IUmbraListener<?> listener){
        if(listener != null) {
            mUmbraKey = listener.getUmbraKey();
        }
    }

    public final <T extends IUmbraListener<?>> T getUmbraListener(){
        return  UmbraManager.getUmbraListener(mUmbraKey);
    }

}
