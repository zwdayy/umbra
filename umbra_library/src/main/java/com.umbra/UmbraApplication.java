package com.umbra;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.text.TextUtils;

import java.util.List;

/**
 * Created by zhangweiding on 15/12/9.
 */
public class UmbraApplication{


    private boolean isRemote;

    private int mVersionCode;

    private String mVersionName;

    private String mRemoteName;

    private static Application mApplication;

    private static final UmbraApplication mUmbra = new UmbraApplication();

    private UmbraApplication(){}

    public static UmbraApplication getInstance(){
        return mUmbra;
    }

    public boolean applicationOnCreate(Application application){
        mApplication = application;
        try {
            PackageInfo packInfo = application.getPackageManager().getPackageInfo(
                    application.getPackageName(), 0);
            mVersionCode = packInfo.versionCode;
            mVersionName = packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        isRemote = isRemote(mApplication);
        return true;
    }

    public <T extends Application> T getApplication(){
        if(mApplication == null){
            throw new NullPointerException();
        }
        return (T)mApplication;
    }



    private boolean isRemote(Context context) {
        try {
            int uid = Process.myUid();
            int pid = Process.myPid();
            ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
            List<ActivityManager.RunningAppProcessInfo> process = am.getRunningAppProcesses();
            String postFixName = TextUtils.isEmpty(mRemoteName) ? ":remote" : mRemoteName;
            for (ActivityManager.RunningAppProcessInfo proInfo : process) {
                if (proInfo.uid == uid) {
                    if (pid == proInfo.pid
                            && proInfo.processName.contains(postFixName)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        return false;

    }

    public void setRemoteProcesName(String remoteName){
        mRemoteName = remoteName;
    }


    public boolean isRemote(){
        return isRemote;
    }


    public final String getVersionName() {
        return mVersionName;
    }

    public final int getVersionCode() {
        return mVersionCode;
    }

}
