package com.ss.video.rtc.demo.quickstart.pickvideo;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.ss.rtc.demo.quickstart.R;

public class SelectorVideoAdapter extends BaseAdapterRV<VideoInfo> {


    private Context mContext;
    private int mSelectorPosition = -1;

    @Override
    public RecyclerView.ViewHolder createVHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        return new SelectorVideoHolder(LayoutInflater.
                from(parent.getContext()).inflate(R.layout.item_selector_video , parent ,false));
    }

    @Override
    protected void onBindVH(RecyclerView.ViewHolder viewHolder, int position, VideoInfo videoInfo) {

        SelectorVideoHolder holder= (SelectorVideoHolder) viewHolder;

        String path = videoInfo.getPath();
        if (!TextUtils.isEmpty(path)){
            holder.mImageView.setImageBitmap(VideoUtils.getVideoThumbnail(path));
        }

        if (position == mSelectorPosition){
            holder.mSelectorView.setBackground(mContext.getResources().
                    getDrawable(R.drawable.video_check_select));
        }else {
            holder.mSelectorView.setBackground(mContext.getResources()
                    .getDrawable(R.drawable.video_check));
        }

    }

    public void setSelectorPosition(int position){
        if (mSelectorPosition == position){
            mSelectorPosition = -1;
        } else {
            mSelectorPosition = position ;
        }

        notifyDataSetChanged();
    }

    public int getSelectorPosition() {
        return mSelectorPosition;
    }


    class SelectorVideoHolder extends Holder {
        private final ImageView mImageView;
        private final View mSelectorView;

        public SelectorVideoHolder(View itemView) {
            super(itemView);
            mImageView = (ImageView) itemView.findViewById(R.id.image_item_video);
            mSelectorView = itemView.findViewById(R.id.view_selector);
        }
    }
}