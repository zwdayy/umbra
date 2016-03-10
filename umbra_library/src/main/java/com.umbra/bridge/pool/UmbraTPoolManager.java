package com.umbra.bridge.pool;

import android.text.TextUtils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class UmbraTPoolManager {

	private static ThreadPoolExecutor sHideServ;

	private static ThreadPoolExecutor sTextServ;


	private static ThreadPoolExecutor sLocalServ;

	private static final String POOL_PREFIX_NET = "net";
	private static final String POOL_PREFIX_HIDE = "hide";
	private static final String POOL_PREFIX_LOCAL = "local";


	private static final AtomicInteger KEY_IDX = new AtomicInteger(1);

	private static String register(StringBuilder prefix,DefaultTaskRunnable<?, ?> task) {
		String key = prefix.append(Integer.toHexString(task.hashCode())).append("_").append(KEY_IDX.getAndIncrement()).toString();
		task.setTag(key);
		return key;
	}

	public static String submitText(Runnable runnable) {
		synchronized (POOL_PREFIX_NET) {
			if (sTextServ == null) {
				sTextServ = newFixedThreadPool(POOL_PREFIX_NET, 5, 5);
			}
		}
		String key = null;
		if(runnable instanceof DefaultTaskRunnable) {
			register(new StringBuilder(POOL_PREFIX_NET), (DefaultTaskRunnable<?,?>)runnable);
		}
		sTextServ.execute(runnable);
		return key;
	}

	public static String submitHideTask(Runnable runnable) {
		synchronized (POOL_PREFIX_HIDE) {
			if (sHideServ == null) {
				sHideServ = newFixedThreadPool(POOL_PREFIX_HIDE, 4, 4);
			}
		}
		String key = null;
		if(runnable instanceof DefaultTaskRunnable) {
			register(new StringBuilder(POOL_PREFIX_HIDE), (DefaultTaskRunnable<?,?>)runnable);
		}
		sHideServ.execute(runnable);
		return key;
	}

	public static String submitLocalTask(Runnable runnable) {
		synchronized (POOL_PREFIX_LOCAL) {
			if (sLocalServ == null) {
				sLocalServ = newFixedThreadPool(POOL_PREFIX_LOCAL, 3, 5);
			}
		}
		String key = null;
		if(runnable instanceof DefaultTaskRunnable) {
			key = register(new StringBuilder(POOL_PREFIX_LOCAL), (DefaultTaskRunnable<?,?>)runnable);
		}
		sLocalServ.execute(runnable);
		return key;
	}

	public static boolean remove(String key) {
		if(TextUtils.isEmpty(key)){
			return false;
		}
		if(key.startsWith(POOL_PREFIX_NET)){
			return removeTask(key, sTextServ);
		}else if(key.startsWith(POOL_PREFIX_HIDE)){
			return removeTask(key, sHideServ);
		}else if(key.startsWith(POOL_PREFIX_LOCAL)){
			return removeTask(key, sLocalServ);
		}
		return false;
	}

	private static boolean removeTask(String key, ThreadPoolExecutor executor) {
		if(executor == null || executor.getQueue() == null){
			return false;
		}
		BlockingQueue<Runnable> queues = executor.getQueue();
		for(Runnable run : queues){
            if(run instanceof DefaultTaskRunnable && key.equals(((DefaultTaskRunnable<?, ?>) run).getTag())) {
                return queues.remove(run);
            }
        }
		return false;
	}

	private UmbraTPoolManager() {

	}


	private static ThreadPoolExecutor newFixedThreadPool(String groupName,int nThreads, int priority) {
		return new ThreadPoolExecutor(nThreads, nThreads, 60L, TimeUnit.MILLISECONDS,
				new LinkedBlockingQueue<Runnable>(), new UmbraThreadFactory(groupName,priority), new ThreadPoolExecutor.AbortPolicy());
	}


	public static void destory() {

		if (sTextServ != null) {
			sTextServ.shutdownNow();
			sTextServ = null;
		}

		if (sHideServ != null) {
			sHideServ.shutdownNow();
			sHideServ = null;
		}

		if(sLocalServ != null){
			sLocalServ.shutdownNow();
			sLocalServ = null;
		}
	}

}
