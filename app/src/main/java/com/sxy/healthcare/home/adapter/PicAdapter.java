package com.sxy.healthcare.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sxy.healthcare.R;
import com.sxy.healthcare.common.utils.GlideUtils;

public class PicAdapter extends RecyclerView.Adapter<PicAdapter.ViewHolder> {

    private Context mContext;

    private String[] mImages;

    public PicAdapter(Context context,String[] images){
        this.mContext = context;
        this.mImages = images;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext)
                .inflate(R.layout.item_pic,parent,false));

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Glide.with(mContext).load(mImages[position]).apply(GlideUtils.getOptions()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return null!=mImages?mImages.length:0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.iv_pic);
        }
    }
}
