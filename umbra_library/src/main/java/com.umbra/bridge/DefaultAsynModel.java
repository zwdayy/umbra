package com.umbra.bridge;

import com.umbra.bridge.helper.UmbraAsynStub;
import com.umbra.bridge.listener.IBaseVListener;
import com.umbra.bridge.listener.IUmbraListener;
import com.umbra.bridge.pool.AsynEventException;

/**
 * Created by zhangweiding on 15/10/16.
 */
public abstract class DefaultAsynModel<Cond,Resp> extends BaseAsynModel<Cond,Resp> {

    public DefaultAsynModel(IUmbraListener<Resp> listener){
        super(new DefaultStubImpl<Resp>(listener));
    }

    @Override
    public Cond getCondition() {
        return null;
    }

    private static class DefaultStubImpl<V> extends UmbraAsynStub implements IBaseVListener<V> {
        public DefaultStubImpl(IUmbraListener<V> listener) {
            super(listener);
        }

        @Override
        public void onLoading(int what) {
            IUmbraListener<V> listener = getUmbraListener();
            if(listener != null){
                listener.onLoading(what);
            }
        }

        @Override
        public void onError(int what, String message, AsynEventException e) {
            IUmbraListener<V> listener = getUmbraListener();
            if(listener != null){
                listener.onError(what, message, e);
            }
        }

        @Override
        public void onHandlerResult(int what, V val) {
            IUmbraListener<V> listener = getUmbraListener();
            if(listener != null){
                listener.onHandlerResult(what, val);
            }
        }

        @Override
        public void beforeHandlerMessage(int what) {
            IUmbraListener<V> listener = getUmbraListener();
            if(listener != null){
                listener.beforeHandlerMessage(what);
            }
        }

    }


}
