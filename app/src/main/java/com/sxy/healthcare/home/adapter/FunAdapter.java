package com.sxy.healthcare.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxy.healthcare.R;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.home.activity.FunCategoryActivity;
import com.sxy.healthcare.home.bean.EntertainmentVosBean;

import java.util.List;

public class FunAdapter extends RecyclerView.Adapter<FunAdapter.ViewHolder> {

    private Context mContext;

    private List<EntertainmentVosBean> vosBeans;

    private OnItemClickListener onItemClickListener;

    public FunAdapter(Context context,OnItemClickListener listener)
    {
        this.mContext = context;
        this.onItemClickListener = listener;
    }

    public void setVosBeans(List<EntertainmentVosBean> vosBeans) {
        this.vosBeans = vosBeans;
        notifyDataSetChanged();
    }

    public List<EntertainmentVosBean> getVosBeans() {
        return vosBeans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_fun,parent,false));
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(vosBeans.get(position).getName());
        Glide.with(mContext).load(vosBeans.get(position).getPic())
                .apply(GlideUtils.getOptions()).into(holder.imageView);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onItemClickListener.onItemClick(position);
            }
        });
    }


    @Override
    public int getItemCount() {
        return null!=vosBeans?vosBeans.size():0;
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;

        private RelativeLayout layout;

        private ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.tv_name);
            layout = (RelativeLayout) itemView.findViewById(R.id.rl_layout);

            imageView = (ImageView)itemView.findViewById(R.id.iv_pic);
        }
    }

    public static interface OnItemClickListener {
        void onItemClick(int position);
        void onItemLongClick(View view);
    }
}
