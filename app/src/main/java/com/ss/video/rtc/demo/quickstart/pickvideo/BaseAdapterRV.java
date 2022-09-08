package com.ss.video.rtc.demo.quickstart.pickvideo;

import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;


public abstract class BaseAdapterRV<T> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public List<T> mDatas = new ArrayList<>();

    private OnItemClickListener mOnItemClickListener;//点击事件

    public void addData(List<T> datas) {
        mDatas.clear();
        mDatas.addAll(datas);
        notifyDataSetChanged();
    }

    public void cleanData() {
        mDatas.clear();
    }

    public void delectData(int pos) {
        mDatas.remove(pos);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return createVHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final T t = mDatas.get(position);
        onBindVH(holder, position, t);

        if (mOnItemClickListener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnItemClickListener.onItemClick(position, holder.itemView  ,t);

                }
            });

        }
    }


    @Override
    public int getItemCount() {
        return mDatas.size();
    }

    public void addItem(T t) {

        mDatas.add(0, t);
        //notifyDataSetChanged();
        notifyItemInserted(0);
    }

    public T removeItem(T t) {
        int position = mDatas.indexOf(t);
        mDatas.remove(position);
        notifyItemRemoved(position);
        return  t;
    }

    public abstract RecyclerView.ViewHolder createVHolder(ViewGroup parent, int viewType);

    protected abstract void onBindVH(RecyclerView.ViewHolder viewHolder, int position, T t);

    static abstract class Holder extends RecyclerView.ViewHolder {
        public Holder(View itemView) {
            super(itemView);
        }
    }

    public void setItemOnClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }


    public interface OnItemClickListener<T> {
        void onItemClick(int position, View view, T t);
    }

}