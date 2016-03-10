package com.umbra.adapter.helper;

public  class ItemHolder{

    public Object mTag;
    public int mPostion;
    public int mItemType;


    public <T>T getData(){
        return (T)mTag;
    }

}