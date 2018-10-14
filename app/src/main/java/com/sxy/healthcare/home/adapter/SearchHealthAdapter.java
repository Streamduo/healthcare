package com.sxy.healthcare.home.adapter;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sxy.healthcare.R;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.home.bean.VegetableBean;

public class SearchHealthAdapter extends RecyclerArrayAdapter<VegetableBean> {

    private Context mContext;


    public SearchHealthAdapter(Context context){
        super(context);
        this.mContext = context;
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder =new ViewHolder(parent);

        return  viewHolder;
    }



    public class ViewHolder extends BaseViewHolder<VegetableBean>{


        public TextView name;
        public ImageView img;
        public TextView price;

        private RelativeLayout layout;

        public ViewHolder(ViewGroup itemView) {
            super(itemView,R.layout.item_health);
            name = $(R.id.item_health_name);
            img = $(R.id.item_health_img);
            layout = $(R.id.rl_layout);
            price = $(R.id.item_health_price);
        }



        @Override
        public void setData(final VegetableBean data) {
            super.setData(data);
            name.setText(data.getAname());
            Glide.with(mContext).load(data.getPic()).apply(GlideUtils.getOptions()).into(img);
            if(StringUtils.isEmpty(data.getUnit())){
                price.setText("积分消费："+data.getPrice()+"/"+"");
            }else {
                price.setText("积分消费："+data.getPrice()+"/"+data.getUnit());
            }

        }
    }


}
