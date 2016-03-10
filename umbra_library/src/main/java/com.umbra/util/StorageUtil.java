package com.umbra.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

import java.io.File;

import static android.os.Environment.MEDIA_MOUNTED;

/**
 * Created by zhangweiding on 15/12/9.
 */
public class StorageUtil {
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";

    private StorageUtil() {
    }
    /**
     *  获取缓存目录文件，优先使用外部存储卡
     * @param context
     * @return
     */
    public static File getCacheDirectory(Context context) {
        return getCacheDirectory(context, true);
    }

    /**
     *  获取缓存目录文件
     * @param context
     * @param preferExternal
     *         true优先使用外部存储卡，false使用手机rom空间
     * @return
     */
    public static File getCacheDirectory(Context context, boolean preferExternal) {
        File appCacheDir = null;
        if (preferExternal && MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            appCacheDir = new File(cacheDirPath);
        }
        return appCacheDir;
    }


    private static File getExternalCacheDir(Context context) {
        File dataDir = new File(Environment.getExternalStorageDirectory(), "kuaihuoyun");
        File appCacheDir = new File(dataDir, context.getPackageName());
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
        }
        return appCacheDir;
    }

    private static boolean hasExternalStoragePermission(Context context) {
        return PackageManager.PERMISSION_GRANTED == context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
    }


    /**
     * 检查存储设备是否超过10M的空间
     *
     * @param size
     * @return
     */
    public static synchronized boolean checkDeviceHasEnghStorage(File file, long size) {
        if (file != null) {
            StatFs mStat = new StatFs(file.getAbsolutePath());
            long val = 0;
            if(Build.VERSION.SDK_INT >=  Build.VERSION_CODES.JELLY_BEAN_MR2){
                val =  mStat.getBlockSizeLong() *  mStat.getAvailableBlocksLong();
            }else{
                val = mStat.getBlockSize() * mStat.getAvailableBlocks();
            }
            return (size + 10 * 1024 * 1024) <= val;
        }
        return false;
    }
}
