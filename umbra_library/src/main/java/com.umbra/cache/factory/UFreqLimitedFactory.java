package com.umbra.cache.factory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by zhangweiding on 15/9/18.
 */
public class UFreqLimitedFactory<Key, Val> implements ILimitedFactory<Key, Val> {


    private final Map<Key, Integer> mUsingCounts = Collections.synchronizedMap(new HashMap<Key, Integer>());


    @Override
    public void onPut(Key key, Val val) {
        this.mUsingCounts.put(key, Integer.valueOf(0));
    }

    @Override
    public void onGet(Key key, Val val) {
        if (val != null) {
            Integer usageCount = mUsingCounts.get(key);
            if (usageCount != null) {
                this.mUsingCounts.put(key, Integer.valueOf(usageCount.intValue() + 1));
            }
        }
    }

    @Override
    public void onRemove(Key key, Val val) {
        mUsingCounts.remove(key);
    }

    @Override
    public Key onCheckRemoveKey() {
        Integer minUsageCount = null;
        Key leastUsedKey = null;
        Set entries = mUsingCounts.entrySet();
        synchronized (mUsingCounts) {
            Iterator i$ = entries.iterator();
            while (true) {
                if (!i$.hasNext()) {
                    break;
                }
                Map.Entry entry = (Map.Entry) i$.next();
                if (leastUsedKey == null) {
                    leastUsedKey = (Key) entry.getKey();
                    minUsageCount = (Integer) entry.getValue();
                } else {
                    Integer lastValueUsage = (Integer) entry.getValue();
                    if (lastValueUsage.intValue() < minUsageCount.intValue()) {
                        minUsageCount = lastValueUsage;
                        leastUsedKey = (Key) entry.getKey();
                    }
                }
            }
        }
        return leastUsedKey;

    }
}
