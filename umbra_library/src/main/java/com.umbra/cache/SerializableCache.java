package com.umbra.cache;

import android.content.Context;

import com.umbra.util.IOUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * Created by zhangweiding on 15/9/21.
 */
public class SerializableCache extends AbstCache<String,Serializable> {


    @Override
    protected int getSize(Serializable serializable) {
        return 0;
    }

    public SerializableCache(Context context, String dir, int capacity) {
        super(context,capacity, dir, 7*24*60);
    }

    @Override
    public synchronized boolean putInMemery(String s, Serializable value) {
        return true;
    }

    @Override
    protected String getFileNameForKey(String key) {
        return key;
    }

    @Override
    protected Serializable readValueFromDisk(File file) throws IOException {
        ObjectInputStream input = null;
        Serializable obj = null;
        try {
            input = new ObjectInputStream(new FileInputStream(file));
            obj = (Serializable)input.readObject();
        } catch (Throwable e) {
            e.printStackTrace();
        }finally {
            IOUtil.closeStream(input);
        }
        return obj;
    }

    @Override
    protected void writeValueToDisk(File file,Serializable value) throws IOException {
        ObjectOutputStream output = null;
        try {
            output = new ObjectOutputStream(new FileOutputStream(file));
            output.writeObject(value);
            output.flush();
        }finally {
            IOUtil.closeStream(output);
        }

    }
}
