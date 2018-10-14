package com.sxy.healthcare.home.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.sxy.healthcare.R;
import com.sxy.healthcare.home.bean.CookerBean;

import java.util.List;

public class CookerAdapter extends RecyclerView.Adapter<CookerAdapter.ViewHolder> {

    private Context mContext;

    private List<CookerBean> cookerBeans;

    public CookerAdapter(Context context){
        this.mContext = context;
    }

    public void setCookerBeans(List<CookerBean> cookerBeans) {
        this.cookerBeans = cookerBeans;
        notifyDataSetChanged();
    }

    public List<CookerBean> getCookerBeans() {
        return cookerBeans;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_cooker, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.cooker.setText(cookerBeans.get(position).getCookerName());
        holder.cost.setText("服务费："+cookerBeans.get(position).getServiceCharge()+"积分");
        if("1".equals(cookerBeans.get(position).getStar())){
            holder.tvLevel.setText("一星级");
            holder.cookerLevel.setRating(1);
        }else  if("2".equals(cookerBeans.get(position).getStar())){
            holder.tvLevel.setText("二星级");
            holder.cookerLevel.setRating(2);
        }else  if("3".equals(cookerBeans.get(position).getStar())){
            holder.tvLevel.setText("三星级");
            holder.cookerLevel.setRating(3);
        }else  if("4".equals(cookerBeans.get(position).getStar())){
            holder.tvLevel.setText("四星级");
            holder.cookerLevel.setRating(4);
        }else  if("5".equals(cookerBeans.get(position).getStar())){
            holder.tvLevel.setText("五星级");
            holder.cookerLevel.setRating(5);
        }




        if(cookerBeans.get(position).isSelected()){
            holder.cb.setChecked(true);
        }else {
            holder.cb.setChecked(false);
        }

        holder.cb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int i=0;i<cookerBeans.size();i++){
                    if(position==i){
                        cookerBeans.get(position).setSelected(true);
                    }else {
                        cookerBeans.get(i).setSelected(false);
                    }
                }
                notifyDataSetChanged();
            }
        });

        holder.cb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return null!=cookerBeans?cookerBeans.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView cooker;
        private TextView tvLevel;
        private TextView cost;
        private RatingBar cookerLevel;
        private CheckBox cb;

        public ViewHolder(View itemView) {
            super(itemView);
            cooker = (TextView)itemView.findViewById(R.id.tv_cooker_no);
            tvLevel = (TextView)itemView.findViewById(R.id.tv_cooker_level);
            cost = (TextView)itemView.findViewById(R.id.tv_cost);
            cookerLevel = (RatingBar) itemView.findViewById(R.id.rb_cooker);
            cb = (CheckBox) itemView.findViewById(R.id.item_cb_cooker);
        }
    }
}
