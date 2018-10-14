package com.sxy.healthcare.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxy.healthcare.R;
import com.sxy.healthcare.home.bean.GoodsCuisinesBean;

import java.util.List;

public class FoodAdapter extends RecyclerView.Adapter<FoodAdapter.ViewHolder>{
    private Context mContext;

    private List<GoodsCuisinesBean> list;

    public FoodAdapter(Context context){
        this.mContext = context;
    }

    public void setList(List<GoodsCuisinesBean> list) {
        this.list = list;
        notifyDataSetChanged();
    }


    public List<GoodsCuisinesBean> getList() {
        return list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_food, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.name.setText(list.get(position).getName());
        if(list.get(position).isSelected()){
            holder.name.setBackgroundResource(R.drawable.search_selected);
        }else {
            holder.name.setBackgroundResource(R.drawable.search_unselected);
        }
        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<list.size();i++){
                    if(position==i){
                        list.get(position).setSelected(true);
                    }else {
                        list.get(i).setSelected(false);
                    }
                }
                notifyDataSetChanged();
            }
        });


    }

    @Override
    public int getItemCount() {
        return null!=list?list.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;


        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.item_food_name);
        }
    }
}
