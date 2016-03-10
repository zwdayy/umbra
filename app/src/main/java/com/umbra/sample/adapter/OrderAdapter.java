package com.umbra.sample.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.umbra.activity.UmbraActivity;
import com.umbra.adapter.UmbraAdapter;
import com.umbra.adapter.helper.ItemHolder;
import com.umbra.sample.R;
import com.umbra.sample.entity.OrderEntity;


/**
 * Created by zhangweiding on 15/11/13.
 */
public class OrderAdapter extends UmbraAdapter<OrderEntity> {


    public OrderAdapter(UmbraActivity context) {
        super(context);
    }

    @Override
    protected ItemHolder getHolder() {
        return new OrderHolder();
    }

    @Override
    public View onCreateView(ViewGroup parent, ItemHolder itemHolder) {
        OrderHolder holder = (OrderHolder)itemHolder;
        return holder.createView(mInflater);
    }

    @Override
    protected void invalidateItemView(ItemHolder itemHolder) {
        OrderHolder holder = (OrderHolder)itemHolder;
        OrderEntity entity = holder.getData();
        holder.mTxtView.setText(entity.createdTime);
    }

    private static class OrderHolder extends ItemHolder{

        TextView mTxtView;

        View mItemView;

        View createView(LayoutInflater inflater){
            mItemView = inflater.inflate(R.layout.order_item,null);
            mTxtView = getElementById(R.id.order_time);
            return mItemView;
        }

        private  <E extends View> E getElementById(int id){
            return (E)mItemView.findViewById(id);
        }

    }
}
