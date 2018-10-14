package com.sxy.healthcare.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxy.healthcare.R;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.me.bean.OrderDetailBean;

import java.util.List;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder>{

    private Context mContext;

    private List<OrderDetailBean> orderDetailBeans;

    public OrderDetailAdapter(Context context){
        this.mContext = context;
    }

    public void setOrderDetailBeans(List<OrderDetailBean> orderDetailBeans) {
        this.orderDetailBeans = orderDetailBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_order_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.name.setText(orderDetailBeans.get(position).getContent());
            holder.price.setText("积分消费："+orderDetailBeans.get(position).getPrice());


        Glide.with(mContext)
                .load(orderDetailBeans.get(position).getGoodsImg()).apply(GlideUtils.getOptions()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return null!=orderDetailBeans?orderDetailBeans.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;

        private TextView price;

        private ImageView imageView;

        private LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.tv_order_name);
            price = (TextView)itemView.findViewById(R.id.tv_order_price);
            imageView = (ImageView) itemView.findViewById(R.id.iv_order_img);
        }
    }

}
