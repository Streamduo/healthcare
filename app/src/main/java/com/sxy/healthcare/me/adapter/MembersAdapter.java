package com.sxy.healthcare.me.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sxy.healthcare.R;
import com.sxy.healthcare.me.bean.MembersBean;

import java.util.List;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder> {
    private Context mContext;

    private List<MembersBean> membersBeans;

    public MembersAdapter(Context context){
        this.mContext = context;
    }


    public void setMembersBeans(List<MembersBean> membersBeans) {
        this.membersBeans = membersBeans;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_members, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
            holder.level.setText(membersBeans.get(position).getLevel()+"");
            holder.phone.setText("手机号："+membersBeans.get(position).getMobile());
            holder.name.setText(membersBeans.get(position).getNickName());
    }

    @Override
    public int getItemCount() {
        return null!=membersBeans?membersBeans.size():0;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView phone;
        private TextView level;

        private TextView name;

        public ViewHolder(View itemView) {
            super(itemView);
            phone = (TextView)itemView.findViewById(R.id.tv_phone);
            level = (TextView)itemView.findViewById(R.id.tv_level);
            name = (TextView)itemView.findViewById(R.id.tv_name);
        }
    }
}
