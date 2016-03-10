package com.umbra.sample.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.LoadingLayoutProxy;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.internal.LoadingLayout;
import com.umbra.adapter.UmbraAdapter;
import com.umbra.sample.R;

public class UmbraPullListView extends PullToRefreshListView {

    private UmbraAdapter<?> mAbstAdapter;

    public UmbraPullListView(Context context) {
        super(context);
        init();
    }

    public UmbraPullListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public UmbraPullListView(Context context, Mode mode) {
        super(context, mode);
        init();
    }

    public UmbraPullListView(Context context, Mode mode, AnimationStyle style) {
        super(context, mode, style);
        init();
    }

    private void init(){
        ListView listView = getRefreshableView();
        listView.setFadingEdgeLength(0);// 设置上下面无黑色阴影
        listView.setCacheColorHint(0);// 设置拖动列表的时候防止出现黑色背景
    }

    @Override
    public void setAdapter(ListAdapter adapter) {
        if(adapter instanceof UmbraAdapter<?>){
            mAbstAdapter = (UmbraAdapter<?>)adapter;
            setOnScrollListener(mAbstAdapter);
        }
        super.setAdapter(adapter);

    }

    @Override
    protected void onDetachedFromWindow() {
        if(mAbstAdapter != null){
            mAbstAdapter.onDestroy();
            mAbstAdapter = null;
        }
        super.onDetachedFromWindow();
    }

    @Override
    protected LoadingLayout createLoadingLayout(Context context, Mode mode, TypedArray attrs) {
        LoadingLayout layout = new CarFrameLoadingLayout(context, mode,
                getPullToRefreshScrollDirection(), attrs);
        layout.setVisibility(View.INVISIBLE);
        return layout;
    }

    public class CarFrameLoadingLayout extends LoadingLayout {


        private AnimationDrawable animationDrawable;

        public CarFrameLoadingLayout(Context context, Mode mode, Orientation scrollDirection, TypedArray attrs) {
            super(context, mode, scrollDirection, attrs);
            mHeaderImage.setVisibility(GONE);
            mHeaderText.setCompoundDrawablesWithIntrinsicBounds(0,R.drawable.car_loading,0,0);
            animationDrawable = (AnimationDrawable) mHeaderText.getCompoundDrawables()[1];
        }

        @Override
        public void onLoadingDrawableSet(Drawable imageDrawable) {
        }

        protected void onPullImpl(float scaleOfLayout) {

        }

        @Override
        protected void refreshingImpl() {
            if(animationDrawable != null) {
                animationDrawable.start();
            }
        }

        @Override
        protected void resetImpl() {
            mHeaderText.clearAnimation();
            if(animationDrawable != null) {
                animationDrawable.stop();
            }
        }

        @Override
        protected void pullToRefreshImpl() {
            // NO-OP

        }

        @Override
        protected void releaseToRefreshImpl() {
            // NO-OP

        }

        @Override
        protected int getDefaultDrawableResId() {
            return R.drawable.bg_blue;
        }

    }

}