package com.sxy.healthcare.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxy.healthcare.R;
import com.sxy.healthcare.me.bean.IntegralBean;
import com.sxy.healthcare.me.bean.OrderBean;
import com.sxy.healthcare.me.event.CheckBoxEvent;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

public class IntegralAdapter extends RecyclerView.Adapter<IntegralAdapter.ViewHolder>{

    private Context mContext;

    private List<IntegralBean> integralBeans;

    public IntegralAdapter(Context context){
        this.mContext = context;
    }

    public void setIntegralBeans(List<IntegralBean> integralBeans) {
        this.integralBeans = integralBeans;
        notifyDataSetChanged();
    }

    public List<IntegralBean> getIntegralBeans() {
        return integralBeans;
    }

    public void modifyIntegralBean(){
        if(integralBeans!=null&&integralBeans.size()>1){
            integralBeans.get(1).setSelected(false);
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_integral, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
            holder.name.setText(integralBeans.get(1).getDesc());

            if(integralBeans.get(position).isSelected()){
                holder.checkBox.setChecked(true);
            }else {
                holder.checkBox.setChecked(false);
            }

           // holder.checkBox.setChecked(true);
            holder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!holder.checkBox.isChecked()){
                        EventBus.getDefault().post(new CheckBoxEvent());
                        integralBeans.get(1).setSelected(true);
                        notifyDataSetChanged();
                    }
                    /*for(int i=0;i<integralBeans.size();i++){
                        if(position==i){
                            integralBeans.get(position).setSelected(true);
                        }else {
                            integralBeans.get(i).setSelected(false);
                        }
                    }*/
                    notifyDataSetChanged();
                }
            });
    }

    @Override
    public int getItemCount() {
        return null!=integralBeans&&integralBeans.size()>1?1:0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView imageView;

        private LinearLayout layout;

        public CheckBox checkBox;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.item_tv_integral);
            checkBox = (CheckBox)itemView.findViewById(R.id.item_cb_integral);
        }
    }
}
