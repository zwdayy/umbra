package com.umbra.bridge.pool;

import java.io.PrintStream;

public class AsynEventException extends Exception {

	private static final long serialVersionUID = 1L;

	private int mExcpCode = -1;

	private Throwable rootCause;

	public AsynEventException(int excpCode) {
		mExcpCode = excpCode;
	}

	public AsynEventException(String detailMessage) {
		super(detailMessage);
	}

	public AsynEventException(int excpCode, String detailMessage) {
		this(detailMessage);
		mExcpCode = excpCode;
	}

	public int getExcpCode() {
		return mExcpCode;
	}

	@Override
	public void printStackTrace(PrintStream err) {
		err.append("error code:" + mExcpCode +"\n");
		super.printStackTrace(err);
	}

	public AsynEventException setCatchException(Throwable rootCause)
	{
		this.rootCause = rootCause;
		return this;
	}

	@Override
	public Throwable getCause()
	{
		if(rootCause != null){
			return this.rootCause;
		}
		return super.getCause();
	}

}
