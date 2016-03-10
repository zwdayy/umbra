package com.umbra.bridge;


import com.umbra.bridge.listener.IAsynListener;
import com.umbra.bridge.listener.IBaseVListener;
import com.umbra.bridge.listener.IEventListener;
import com.umbra.bridge.pool.AsynEventException;
import com.umbra.bridge.pool.DefaultTaskRunnable;
import com.umbra.bridge.pool.UmbraTPoolManager;

public abstract class BaseAsynModel<Cond, Rult> implements IAsynListener<Cond, Rult>,IEventListener<Rult>
{

	protected Rult mData;
	protected IBaseVListener<Rult> mController;
	protected DefaultTaskRunnable<Cond, Rult> mTask = new DefaultTaskRunnable<Cond, Rult>();
	
	public BaseAsynModel(IBaseVListener<Rult> controller){
		mController = controller;
		mTask.setDataListener(this);
	}


	@Override
	public String submit(int what){
		if(mTask.checkSubmit(what)) {
			if(mController == null){
				return UmbraTPoolManager.submitHideTask(mTask);
			}else {
				return UmbraTPoolManager.submitText(mTask);
			}
		}
		return null;
	}

	@Override
	public Rult executeSync() throws Throwable {
		return mTask.onExecute(1, getCondition());
	}

	public final boolean isError(){
		return mTask.isError();
	}


	@Override
	public final void onLoading(int what) {
		if(mController != null){
			mController.onLoading(what);
		}
	}


	@Override
	public final void onError(int what,String message, AsynEventException e) {
		if(mController != null){
			mController.onError(what, message, e);
		}
	}

	@Override
	public final void onHandlerResult(int what, Rult val) {
		if(mController != null){
			mData = val;
			mController.onHandlerResult(what, mData);
		}
	}

	@Override
	public final void beforeHandlerMessage(int what) {
		if(mController != null){
			mController.beforeHandlerMessage(what);
		}
	}


	@Override
	public void onDestroy(){
		mController = null;
		mTask.setDataListener(null);
	}

}
