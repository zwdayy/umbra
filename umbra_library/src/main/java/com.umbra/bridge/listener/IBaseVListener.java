package com.umbra.bridge.listener;


import com.umbra.bridge.pool.AsynEventException;

public interface IBaseVListener<V> {

	void onLoading(int what);

	void onError(int what, String message, AsynEventException e);

	void onHandlerResult(int what, V val);

	void beforeHandlerMessage(int what);


}