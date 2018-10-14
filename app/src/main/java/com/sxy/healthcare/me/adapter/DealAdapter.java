package com.sxy.healthcare.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxy.healthcare.R;
import com.sxy.healthcare.me.bean.ChangesVosBean;
import com.sxy.healthcare.me.bean.OrderBean;

import java.util.List;

public class DealAdapter extends RecyclerView.Adapter<DealAdapter.ViewHolder> {
    private Context mContext;

    private List<ChangesVosBean> orderBeans;

    public void setOrderBeans(List<ChangesVosBean> orderBeans) {
        this.orderBeans = orderBeans;

        notifyDataSetChanged();
    }

    public DealAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_deal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.desc.setText(orderBeans.get(position).getContent());
        holder.date.setText(orderBeans.get(position).getCreateTime());
        if(orderBeans.get(position).getState()==0){
            holder.state.setText("成功");
        }else {
            holder.state.setText("失败");
        }
    }

    @Override
    public int getItemCount() {
        return null!=orderBeans?orderBeans.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView desc;
        private TextView state;
        private TextView date;

        public ViewHolder(View itemView) {
            super(itemView);
            desc = (TextView)itemView.findViewById(R.id.tv_desc);
            state = (TextView)itemView.findViewById(R.id.tv_state);
            date = (TextView)itemView.findViewById(R.id.tv_date);
        }
    }
}
