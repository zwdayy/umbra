package com.umbra.adapter.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.DisplayMetrics;
import android.widget.AbsListView;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.MemoryCacheAware;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.assist.MemoryCacheUtil;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
import com.nostra13.universalimageloader.utils.ImageSizeUtils;

/**
 * Created by zhangweiding on 15/10/12.
 */
public class ImageHelper {

    private ImageSize mMaxSize;
    private DisplayImageOptions mIconOption;
    private DisplayImageOptions.Builder mBuilder;
    private MemoryCacheAware<String, Bitmap> memoryCache;
    private ImageLoaderConfiguration mConfig;


    public ImageHelper(Context context){
        mBuilder = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisc(true);
        mIconOption = mBuilder.build();
        memoryCache =  ImageLoader.getInstance().getMemoryCache();
        mMaxSize = getMaxImageSize(context);
    }

    private ImageSize getMaxImageSize(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int width = displayMetrics.widthPixels;
        int height =   displayMetrics.heightPixels;
        return new ImageSize(width, height);
    }

    public void displayImageView(String uri,ImageView imageView,int scrollStatus){
        ImageViewAware mAware = new ImageViewAware(imageView);
        if(!displayImageInMemory(uri,mAware) && scrollStatus != AbsListView.OnScrollListener.SCROLL_STATE_FLING ){
            ImageLoader.getInstance().displayImage(uri,new ImageViewAware(imageView));
        }
    }


    private boolean displayImageInMemory(String uri,ImageViewAware imageAware){
        ImageSize targetSize = ImageSizeUtils.defineTargetSizeForView(imageAware, mMaxSize);
        Bitmap bmp = (Bitmap)memoryCache.get(MemoryCacheUtil.generateKey(uri, targetSize));
        if(bmp != null && !bmp.isRecycled() && !mIconOption.shouldPostProcess()) {
            return mIconOption.getDisplayer().display(bmp, imageAware, LoadedFrom.MEMORY_CACHE) !=null;
        }
        return false;
    }
}
