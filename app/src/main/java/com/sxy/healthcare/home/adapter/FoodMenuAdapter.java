package com.sxy.healthcare.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxy.healthcare.R;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.home.bean.VegetableBean;

import java.util.List;

public class FoodMenuAdapter extends RecyclerView.Adapter<FoodMenuAdapter.ViewHolder> {

    private Context mContext;

    private List<VegetableBean> vegetableBeans;

    public FoodMenuAdapter(Context context){
        this.mContext = context;
    }

    public void setVegetableBeans(List<VegetableBean> vegetableBeans) {
        this.vegetableBeans = vegetableBeans;
        notifyDataSetChanged();
    }

    public List<VegetableBean> getVegetableBeans() {
        return vegetableBeans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_food_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvName.setText(vegetableBeans.get(position).getAname());
        Glide.with(mContext).load(vegetableBeans.get(position).getPic()).apply(GlideUtils.getOptions()).into(holder.imageView);

        if(vegetableBeans.get(position).isSelected()){
            holder.checkBox.setChecked(true);
        }else {
            holder.checkBox.setChecked(false);
        }

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(vegetableBeans.get(position).isSelected()){
                   vegetableBeans.get(position).setSelected(false);
                }else {
                    vegetableBeans.get(position).setSelected(true);
                }
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return null!=vegetableBeans?vegetableBeans.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imageView;
        public TextView tvName;
        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = (ImageView)itemView.findViewById(R.id.iv_food);
            tvName = (TextView) itemView.findViewById(R.id.tv_name);
            checkBox = (CheckBox) itemView.findViewById(R.id.item_cb);
        }
    }
}
