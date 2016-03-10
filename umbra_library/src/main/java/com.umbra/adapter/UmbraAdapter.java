package com.umbra.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;

import com.umbra.adapter.helper.ItemHolder;
import com.umbra.bridge.helper.UmbraManager;
import com.umbra.bridge.listener.IUmbraListener;
import com.umbra.bridge.pool.AsynEventException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhangweiding on 15/10/19.
 */
public abstract class UmbraAdapter<T> extends BaseAdapter implements AbsListView.OnScrollListener,IUmbraListener<Object> {

    private String mUmbraKey;
    protected Context mContext;
    protected int mScrollStatus;
    protected AdapterView mView;
    protected int mFirstVisibleItem;
    protected int mVisibleItemCount;
    protected LayoutInflater mInflater;
    protected List<T> mData = new ArrayList<T>();


    public UmbraAdapter(Context context) {
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mUmbraKey = UmbraManager.register(this);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public T getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public <E extends View> E getElementById(View view,int id) {
        return (E) view.findViewById(id);
    }

    @Override
    public String getUmbraKey() {
        return mUmbraKey;
    }

    @Override
    public final View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        mView = (AdapterView)parent;
        int itemType = getItemViewType(position);
        boolean isConvertNull = convertView == null;
        ItemHolder holder = isConvertNull ? getHolder() :  (ItemHolder) convertView.getTag();
        holder.mItemType = itemType;
        holder.mPostion = position;
        holder.mTag = mData.get(position);
        if (isConvertNull) {
            convertView = onCreateView(parent, holder);
            convertView.setTag(holder);
            onFinishCreateView(holder);
        }
        invalidateItemView(holder);
        return convertView;
    }

    protected void onFinishCreateView(ItemHolder holder) {
    }

    protected abstract ItemHolder getHolder();

    public abstract View onCreateView(ViewGroup parent, ItemHolder holder);

    protected abstract void invalidateItemView(ItemHolder holder);

    public void addAll(List<T> list) {
        mData.addAll(list);
        notifyDataSetChanged();
    }

    public void addAll(int pos,List<T> list) {
        mData.addAll(pos, list);
        notifyDataSetChanged();
    }

    public void add(int pos,T object) {
        mData.add(pos, object);
        notifyDataSetChanged();
    }

    public void clearAndAdd(List<T> list) {
        mData.clear();
        addAll(list);
    }

    public void clear() {
        if (!mData.isEmpty()) {
            mData.clear();
            notifyDataSetChanged();
        }
    }

    public List<T> getData(){
        return mData;
    }


    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // TODO Auto-generated method stub
        mScrollStatus = scrollState;
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem,
                         int visibleItemCount, int totalItemCount) {
        // TODO Auto-generated method stub
        mVisibleItemCount = visibleItemCount;
        mFirstVisibleItem = firstVisibleItem;

    }


    public void onDestroy() {
        UmbraManager.unRegister(mUmbraKey);
        mContext = null;
        mView = null;
        mInflater = null;
    }

    @Override
    public void onLoading(int what) {

    }

    @Override
    public void onError(int what, String message, AsynEventException e) {

    }

    @Override
    public void onHandlerResult(int what, Object val) {

    }

    @Override
    public void beforeHandlerMessage(int what) {

    }
}