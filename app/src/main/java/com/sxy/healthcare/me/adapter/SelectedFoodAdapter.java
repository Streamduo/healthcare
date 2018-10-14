package com.sxy.healthcare.me.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sxy.healthcare.R;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.me.bean.BookingGoodsVos;

import java.util.List;

public class SelectedFoodAdapter extends  RecyclerView.Adapter<SelectedFoodAdapter.ViewHolder>  {

    private Context mContext;

    private List<BookingGoodsVos> vegetableBeans;

    public SelectedFoodAdapter(Context context){
        this.mContext = context;
    }

    public void setVegetableBeans(List<BookingGoodsVos> vegetableBeans) {
        this.vegetableBeans = vegetableBeans;
        notifyDataSetChanged();
    }

    public List<BookingGoodsVos> getVegetableBeans() {
        return vegetableBeans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_food_menu, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvName.setText(vegetableBeans.get(position).getGoodsName());
        Glide.with(mContext).load(vegetableBeans.get(position).getPic()).apply(GlideUtils.getOptions()).into(holder.imageView);

        holder.checkBox.setVisibility(View.GONE);
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
