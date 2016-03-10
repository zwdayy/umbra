package com.umbra.bridge.helper;

import com.umbra.bridge.pool.UmbraTPoolManager;

import java.util.List;

/**
 * Created by zhangweiding on 15/12/9.
 */
public class BridgeManager {

    private BridgeManager(){

    }

    public static void cancel(String key) {
        UmbraTPoolManager.remove(key);
    }

    public static synchronized void cancel(List<String> keys) {
        for(String key : keys){
            UmbraTPoolManager.remove(key);
        }

    }

}
