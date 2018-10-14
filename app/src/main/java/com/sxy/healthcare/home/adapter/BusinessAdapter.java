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
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sxy.healthcare.BuildConfig;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.home.activity.BusinessDetailActivity;
import com.sxy.healthcare.home.activity.FoodActivity;
import com.sxy.healthcare.home.activity.FunActivity;
import com.sxy.healthcare.home.activity.GoodsActivity;
import com.sxy.healthcare.home.activity.HealthActivity;
import com.sxy.healthcare.home.activity.OtherActivity;
import com.sxy.healthcare.home.bean.TraderBean;

import java.util.List;

public class BusinessAdapter extends RecyclerArrayAdapter<TraderBean> {

    private Context mContext;

    private String typeId;

    public BusinessAdapter(Context context,String id){
        super(context);
        this.mContext = context;
        this.typeId = id;
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        ViewHolder viewHolder =new ViewHolder(parent);

        return  viewHolder;
    }


    public class ViewHolder extends BaseViewHolder<TraderBean>{


        public TextView name;
        public ImageView img;
        public TextView phone;
        public TextView time;

        private RelativeLayout layout;

        public ViewHolder(ViewGroup itemView) {
            super(itemView,R.layout.item_category);
            name = $(R.id.item_tv_category);
            img = $(R.id.item_iv_category);
            layout = $(R.id.rl_business);
            phone = $(R.id.item_tv_phone);
            time = $(R.id.item_tv_time);
        }



        @Override
        public void setData(final TraderBean data) {
            super.setData(data);
            name.setText(data.getTraderName());
            phone.setText("电话："+data.getContactNo());
            time.setText("营业时间："+data.getOnlineTime());

            Glide.with(mContext).load(data.getPic()).apply(GlideUtils.getOptions()).into(img);

            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent;
                    if("6".equals(typeId)){
                        intent = new Intent(mContext, BusinessDetailActivity.class);
                    }else  if("7".equals(typeId)){
                         intent = new Intent(mContext, HealthActivity.class);
                    }else  if("3".equals(typeId)){
                         intent = new Intent(mContext, GoodsActivity.class);
                    }else  if("4".equals(typeId)){
                         intent = new Intent(mContext, FunActivity.class);
                    }else {
                         intent = new Intent(mContext, OtherActivity.class);
                    }
                    intent.putExtra(Constants.EXTRA_TRADER_BEAN,data);
                    intent.putExtra(Constants.EXTRA_MENU_ID,typeId);
                    mContext.startActivity(intent);
                }
            });
        }
    }
}
