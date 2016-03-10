package com.umbra.cache;

import android.content.Context;

import java.io.File;
import java.io.IOException;

/**
 * Created by zhangweiding on 15/9/21.
 */
public class ByteCache extends AbstCache<String,byte[]> {



    public ByteCache(Context context,String dir,int capacity) {
        super(context,capacity, dir, 7*24*60);
    }

    @Override
    protected int getSize(byte[] bytes) {
        return bytes.length;
    }

    @Override
    protected String getFileNameForKey(String key) {
        return key;
    }

    @Override
    protected byte[] readValueFromDisk(File file) throws IOException {
        return readByteFromDisk(file);
    }

    @Override
    protected void writeValueToDisk(File file, byte[] value) throws IOException {
        if(value == null || value.length == 0){
            return;
        }
        writeByteToDisk(file,value,0,value.length);
    }
}
