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
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.home.activity.BusinessActivity;
import com.sxy.healthcare.home.activity.FunActivity;
import com.sxy.healthcare.home.activity.GoodsActivity;
import com.sxy.healthcare.home.activity.HealthActivity;
import com.sxy.healthcare.home.bean.ServiceVosBean;

import java.util.List;

public class HomeMenuAdapter  extends RecyclerView.Adapter<HomeMenuAdapter.ViewHolder> {

    private Context mContext;

    private List<ServiceVosBean> homeMenus;

    private RequestOptions options;

    public HomeMenuAdapter(Context context){
        this.mContext = context;
        options = new RequestOptions()
                .placeholder(R.color.gray_979797)
                .error(R.color.gray_979797);
    }

    public void setHomeMenus(List<ServiceVosBean> homeMenus) {
        this.homeMenus = homeMenus;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_home_menu, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name.setText(homeMenus.get(position).getName());

        Glide.with(mContext).load(homeMenus.get(position)
                .getPic()).apply(GlideUtils.getOptions()).into(holder.imageView);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                switch (position){
            case 0:
                intent = new Intent(mContext, BusinessActivity.class);
                intent.putExtra(Constants.EXTRA_MENU_ID,homeMenus.get(position).getId());
                mContext.startActivity(intent);
                break;
            case 1:
                intent = new Intent(mContext, BusinessActivity.class);
                intent.putExtra(Constants.EXTRA_MENU_ID,homeMenus.get(position).getId());
                mContext.startActivity(intent);
                break;
            case 2:
                intent = new Intent(mContext, BusinessActivity.class);
                intent.putExtra(Constants.EXTRA_MENU_ID,homeMenus.get(position).getId());
                mContext.startActivity(intent);
                break;
            case 3:
                intent = new Intent(mContext, BusinessActivity.class);
                intent.putExtra(Constants.EXTRA_MENU_ID,homeMenus.get(position).getId());
                mContext.startActivity(intent);
                break;
            case 4:
                intent = new Intent(mContext, BusinessActivity.class);
                intent.putExtra(Constants.EXTRA_MENU_ID,homeMenus.get(position).getId());
                mContext.startActivity(intent);
                break;
                default:
                break;
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return null!=homeMenus?homeMenus.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView name;
        private ImageView imageView;

        private LinearLayout layout;

        public ViewHolder(View itemView) {
            super(itemView);
            name = (TextView)itemView.findViewById(R.id.tv_menu_name);
            imageView = (ImageView)itemView.findViewById(R.id.iv_home_menu);
            layout = (LinearLayout)itemView.findViewById(R.id.item_ll_comm);
        }
    }
}
