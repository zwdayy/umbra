package com.umbra.bridge.pool;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.umbra.bridge.listener.IAsynListener;
import com.umbra.util.ValidateUtil;

public class DefaultTaskRunnable<Cond, Resp> extends Handler implements Runnable, IAsynListener<Cond, Resp> {

    public static final int STATE_INIT = 0;

    public static final int STATE_LOADING = 1;

    public static final int STATE_FINISH = 2;

    public static final int STATE_ERROR = 3;

    public static final int MSG_WHAT_DEFAFULT = Integer.MAX_VALUE;

    private int state = STATE_INIT;

    private Resp mData;

    private String mTag;

    private AsynEventException errorMsg;

    private IAsynListener<Cond, Resp> mCallBack;

    private int mWhat = MSG_WHAT_DEFAFULT;

    public void setWhat(int what) {
        this.mWhat = what;
    }

    public DefaultTaskRunnable() {
        super(Looper.getMainLooper());
    }

    public synchronized boolean checkLoading() {
        return state == STATE_INIT;
    }

    public synchronized boolean isLoading() {
        return state == STATE_LOADING;
    }

    public synchronized void setState(int state) {
        this.state = state;
    }

    public synchronized void reset() {
        state = STATE_INIT;
        mData = null;
        errorMsg = null;
    }

    public synchronized boolean isFinished() {
        return state == STATE_FINISH;
    }

    public synchronized boolean isError() {
        return state == STATE_ERROR;
    }

    public void setTag(String tag) {
        mTag = tag;
    }

    public String getTag() {
        return mTag;
    }

    public final boolean checkSubmit(int what) {
        if (isLoading() || isFinished()) {
            return false;
        }
        mWhat = what;
        if (ValidateUtil.isMainThread()) {
            onLoading(what);
        } else {
            Message msg = Message.obtain();
            msg.what = mWhat;
            sendMessage(msg);
        }
        return true;
    }

    @Override
    public void handleMessage(Message msg) {

        int what = msg.what;
        beforeHandlerMessage(what);
        int state = getState();
        switch (state) {
            case STATE_ERROR:
                onError(what, errorMsg == null ? null : errorMsg.getMessage(), errorMsg);
                reset();
                break;
            case STATE_FINISH:
                onHandlerResult(what, mData);
                reset();
                break;
            case STATE_INIT:
                onLoading(what);
                break;
            default:
                reset();
                break;
        }

    }

    public DefaultTaskRunnable setDataListener(IAsynListener<Cond, Resp> listener) {
        if (mCallBack == listener) {
            return this;
        }
        mCallBack = listener;
        return this;
    }

    public final synchronized int getState() {
        return state;
    }

    @Override
    public final void run() {
        setState(STATE_LOADING);
        mData = null;
        errorMsg = null;
        try {
            mData = onExecute(mWhat, getCondition());
            setState(STATE_FINISH);
        } catch (Throwable e) {
            e.printStackTrace();
            setState(STATE_ERROR);
            if (!(e instanceof AsynEventException)) {
                errorMsg = new AsynEventException(-1).setCatchException(e);
            } else {
                errorMsg = (AsynEventException) e;
            }
        }
        Message msg = Message.obtain();
        msg.what = mWhat;
        sendMessage(msg);
    }

    @Override
    public Resp onExecute(int what, Cond condition) throws Throwable {
        // TODO Auto-generated method stub
        if (mCallBack != null) {
            return mCallBack.onExecute(what, condition);
        }
        return null;
    }

    @Override
    public void onLoading(int what) {
        // TODO Auto-generated method stub
        if (mCallBack != null) {
            mCallBack.onLoading(what);
        }
    }

    @Override
    public void onError(int what, String message, AsynEventException e) {
        // TODO Auto-generated method stub
        if (mCallBack != null) {
            mCallBack.onError(what, message, e);
        }
    }


    @Override
    public void onHandlerResult(int what, Resp object) {
        // TODO Auto-generated method stub
        if (mCallBack != null) {
            mCallBack.onHandlerResult(what, object);
        }
    }

    @Override
    protected void finalize() throws Throwable {
        // TODO Auto-generated method stub
        try {
            setDataListener(null);
        } finally {
            super.finalize();
        }
    }

    @Override
    public void beforeHandlerMessage(int what) {
        // TODO Auto-generated method stub
        if (mCallBack != null) {
            mCallBack.beforeHandlerMessage(what);
        }
    }

    @Override
    public Cond getCondition() {
        if (mCallBack != null) {
            return mCallBack.getCondition();
        }
        return null;
    }


}
