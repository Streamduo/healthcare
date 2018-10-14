package com.sxy.healthcare.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.home.activity.BusinessActivity;
import com.sxy.healthcare.home.bean.HotServiceBean;
import com.sxy.healthcare.home.bean.ServiceVosBean;

import java.util.List;

public class HotServiceAdapter extends RecyclerView.Adapter<HotServiceAdapter.ViewHolder> {

    private Context mContext;

    private List<ServiceVosBean> hotServiceBeanList;

    private RequestOptions options;

    public HotServiceAdapter(Context context){
            this.mContext = context;
        options = new RequestOptions()
                .placeholder(R.color.gray_979797)
                .error(R.color.gray_979797);
    }

    public void setHotServiceBeanList(List<ServiceVosBean> hotServiceBeanList) {
        this.hotServiceBeanList = hotServiceBeanList;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_comm, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
            holder.name.setText(hotServiceBeanList.get(position).getName());
        Glide.with(mContext).load(hotServiceBeanList.get(position).getPic()).apply(options).into(holder.imageView);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, BusinessActivity.class);
                intent.putExtra(Constants.EXTRA_MENU_ID,hotServiceBeanList.get(position).getId());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return hotServiceBeanList==null?0:hotServiceBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView imageView;

        private LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.item_tv_name);
            imageView = (ImageView)itemView.findViewById(R.id.item_iv_pic);
            layout = (LinearLayout)itemView.findViewById(R.id.item_ll_comm);
        }
    }
}
