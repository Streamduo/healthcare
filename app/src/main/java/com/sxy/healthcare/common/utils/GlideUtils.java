package com.sxy.healthcare.common.utils;

import com.bumptech.glide.request.RequestOptions;
import com.sxy.healthcare.R;
import com.sxy.healthcare.common.GlideCircleTransform;

public class GlideUtils {

    private static RequestOptions options;

    public static RequestOptions getOptions(){
        options = new RequestOptions()
                .placeholder(R.color.gray_979797)
                .error(R.color.gray_979797);

        return options;
    }

    public static RequestOptions getOptionsAvatar(){
        options = new RequestOptions()
                .transform(new GlideCircleTransform())
                .placeholder(R.color.gray_979797)
                .error(R.color.gray_979797)
                .override((int)ScreenUtils.dip2px(42),(int)ScreenUtils.dip2px(42));

        return options;
    }
}
