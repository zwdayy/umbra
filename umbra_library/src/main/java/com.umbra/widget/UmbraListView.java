package com.umbra.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.umbra.adapter.UmbraAdapter;

/**
 * Created by zhangweiding on 15/12/23.
 */
public class UmbraListView extends ListView {
    public UmbraListView(Context context) {
        super(context);
    }

    public UmbraListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public UmbraListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDetachedFromWindow() {
        ListAdapter adapter = getAdapter();
        if(adapter != null && adapter instanceof UmbraAdapter){
            ((UmbraAdapter<?>)adapter).onDestroy();
        }
        super.onDetachedFromWindow();
    }
}
