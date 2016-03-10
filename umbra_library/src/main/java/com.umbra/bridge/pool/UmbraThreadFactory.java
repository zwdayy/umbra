package com.umbra.bridge.pool;

import android.text.TextUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

class UmbraThreadFactory implements ThreadFactory {

	private int mPriority;
	private final ThreadGroup mGroup;
	private final String mNamePrefix;
	private final AtomicInteger mThreadNumber = new AtomicInteger(1);
	private static final AtomicInteger mPoolNumber = new AtomicInteger(1);

	public UmbraThreadFactory(String groupName, int priority) {
		SecurityManager s = System.getSecurityManager();
		mGroup = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
		mNamePrefix = "p-" + (TextUtils.isEmpty(groupName) ? mPoolNumber.getAndIncrement() : (groupName+mPoolNumber.getAndIncrement())) + "-thread-";
		this.mPriority = priority;
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(mGroup, r, mNamePrefix + mThreadNumber.getAndIncrement(), 0);
		if (t.isDaemon()) {
			t.setDaemon(false);
		}
		if (mPriority > Thread.MAX_PRIORITY || mPriority < Thread.MIN_PRIORITY) {
			mPriority = Thread.NORM_PRIORITY;
		}
		t.setPriority(mPriority);
		return t;
	}

}