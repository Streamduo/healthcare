package com.sxy.healthcare.home.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.Option;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.common.GlideCircleTransform;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.home.activity.BusinessDetailActivity;
import com.sxy.healthcare.home.bean.CommentBean;
import com.sxy.healthcare.home.bean.TraderBean;

public class CommentAdapter extends RecyclerArrayAdapter<CommentBean> {

    private Context mContext;

    private RequestOptions options;

    public CommentAdapter(Context context) {
        super(context);
        this.mContext = context;
        options = new RequestOptions()
                .placeholder(R.color.gray_979797)
                .error(R.color.gray_979797)
                .transform(new GlideCircleTransform());
    }

    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(parent);
    }

    public class ViewHolder extends BaseViewHolder<CommentBean>{


        public TextView name;
        public ImageView img;
        private TextView comment;

        private RelativeLayout layout;


        public ViewHolder(ViewGroup itemView) {
            super(itemView, R.layout.item_commet);
            name = $(R.id.tv_name);
            img = $(R.id.iv_avatar);
            comment = $(R.id.tv_comment);

        }



        @Override
        public void setData(final CommentBean data) {
            super.setData(data);
            name.setText(data.getUserNick());
            comment.setText(data.getCommentText());
            Glide.with(mContext).load("http://sxy.wo946.com/upload/1527141381719shop3.jpg").apply(options).into(img);
        }
    }
}
