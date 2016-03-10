package com.umbra.cache.factory;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by zhangweiding on 15/9/18.
 */
public class LRULimitedFactory<Key, Val> implements ILimitedFactory<Key, Val> {


    private final Map<Key, Val> mLRUCache = Collections.synchronizedMap(new LinkedHashMap<Key, Val>(10, 1.1F, true));


    @Override
    public void onPut(Key key, Val val) {
        this.mLRUCache.put(key, val);
    }

    @Override
    public void onGet(Key key, Val val) {
        this.mLRUCache.get(key);
    }

    @Override
    public void onRemove(Key key, Val val) {
        this.mLRUCache.remove(key);
    }

    @Override
    public Key onCheckRemoveKey() {
        Key mostLongUsedKey = null;
        synchronized (this.mLRUCache) {
            Iterator it = this.mLRUCache.entrySet().iterator();
            if (it.hasNext()) {
                Map.Entry entry = (Map.Entry) it.next();
                mostLongUsedKey = (Key) entry.getKey();
                it.remove();
            }
            return mostLongUsedKey;
        }
    }
}
