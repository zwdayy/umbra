package com.umbra.sample;

import android.app.Application;

import com.umbra.UmbraApplication;

/**
 * Created by zhangweiding on 15/12/9.
 */
public class SampleApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        UmbraApplication.getInstance().applicationOnCreate(this);
        if(UmbraApplication.getInstance().isRemote()){
            return;
        }
    }
}
