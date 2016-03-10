package com.umbra.bridge.helper;

import android.support.v4.util.ArrayMap;
import android.text.TextUtils;

import com.umbra.bridge.listener.IUmbraListener;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhangweiding on 15/10/23.
 */
public class UmbraManager {

    private static final ArrayMap<String,IUmbraListener<?>> umbraMap = new ArrayMap<String,IUmbraListener<?>>();
    private static final AtomicInteger mPoolNumber = new AtomicInteger(1);


    public static String register(IUmbraListener<?> listener){
        String key = listener.getClass().getName() + mPoolNumber.getAndIncrement();
        umbraMap.put(key, listener);
        return key;
    }

    public static void unRegister(String  key){
        umbraMap.remove(key);
    }

    public static <T extends IUmbraListener<?>> T getUmbraListener(String key){
        if(TextUtils.isEmpty(key)){
            return null;
        }
        return (T) umbraMap.get(key);
    }
}
