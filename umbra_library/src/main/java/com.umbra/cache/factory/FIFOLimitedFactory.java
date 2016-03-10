package com.umbra.cache.factory;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhangweiding on 15/9/18.
 */
public class FIFOLimitedFactory<Key, Val> implements ILimitedFactory<Key, Val> {


    private final List<Key> mQueue = Collections.synchronizedList(new LinkedList<Key>());


    @Override
    public void onPut(Key key, Val val) {
        mQueue.add(key);
    }

    @Override
    public void onGet(Key key,Val val) {

    }

    @Override
    public void onRemove(Key key, Val val) {
        mQueue.remove(key);
    }

    @Override
    public Key onCheckRemoveKey() {
        return mQueue.get(0);
    }
}
