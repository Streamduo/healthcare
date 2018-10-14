package com.sxy.healthcare.base;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sxy.healthcare.R;
import com.sxy.healthcare.common.utils.SharedPrefsUtil;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.Util;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public class BaseActivity extends AppCompatActivity {

    private Unbinder mUnbinder;

    protected SharedPrefsUtil sharedPrefsUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        sharedPrefsUtil = SharedPrefsUtil.getInstance(getApplicationContext());
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        mUnbinder = ButterKnife.bind(this);
        initViews();
        initDatas();
        initListener();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        mUnbinder = ButterKnife.bind(this);
        initViews();
        initDatas();
        initListener();
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        mUnbinder = ButterKnife.bind(this);
        initViews();
        initDatas();
        initListener();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    /**
     * 防止字体随手机系统的字体变大而变大
     */
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        Configuration config = new Configuration();
        config.setToDefaults();
        //noinspection deprecation
        res.updateConfiguration(config, res.getDisplayMetrics());
        return res;
    }


    /**
     * 初始化view
     */
    protected void initViews() {}

    protected void initDatas(){}

    protected void initListener(){};

    /**
     * Activity间跳转（不传值）
     *
     * @param cls 对应 Activity 的 Class
     */
    protected void startActivity(Class<?> cls) {
        Intent intent = new Intent(this, cls);
        startActivity(intent);
    }

    /**
     * Activity间的跳转（传值）
     *
     * @param cls    对应 Activity 的 Class
     * @param bundle 传递值
     */
    public void startActivity(Class<?> cls, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 带返回值的Activity跳转
     *
     * @param cls         对应 Activity 的 Class
     * @param requestCode 请求码
     */
    protected void startActivityForResult(Class<?> cls, int requestCode) {
        Intent intent = new Intent(this, cls);
        startActivityForResult(intent, requestCode);
    }

    /**
     * 带返回值的Activity间跳转（传值）
     *
     * @param cls         对应 Activity 的 Class
     * @param bundle      传递值
     * @param requestCode 请求码
     */
    protected void startActivityForResult(Class<?> cls, Bundle bundle, int requestCode) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivityForResult(intent, requestCode);
    }

    /**
     * 返回按钮事件监听
     */
    protected void doReturn() {
        LinearLayout returnIbtn = (LinearLayout) findViewById(R.id.ibtn_return);
        if (returnIbtn != null) {
            returnIbtn.setVisibility(View.VISIBLE);
            returnIbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                    Util.hideSoftKeyboard(BaseActivity.this);
                }
            });
        }
    }

    /**
     * 设置标题
     */
    protected void setCurrentTitle(int resId) {
        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        if (titleTv == null) {
            return;
        }
        titleTv.setText(resId);
    }

    /**
     * 设置标题
     */
    protected void setCurrentTitle(CharSequence text) {
        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        if (titleTv == null) {
            return;
        }

        titleTv.setText(StringUtils.isEmpty(text) ? getString(R.string.app_name) : text);
    }

    /**
     * @param text       标题
     * @param colorResId 颜色
     */
    protected void setCurrentTitleWithColor(CharSequence text, int colorResId) {
        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        if (titleTv == null) {
            return;
        }
        titleTv.setText(StringUtils.isEmpty(text) ? getString(R.string.app_name) : text);
        titleTv.setTextColor(colorResId);
    }


    /**
     * @param textResId  字符串资源
     * @param colorResId 颜色资源
     */
    protected void setCurrentTitleWithColor(int textResId, int colorResId) {
        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        if (titleTv == null) {
            return;
        }
        titleTv.setText(textResId);
        titleTv.setTextColor(colorResId);
    }

    /**
     * @param textResId 标题资源
     * @param size      标题大小
     */
    protected void setCurrentTitleSize(int textResId, int size) {
        TextView titleTv = (TextView) findViewById(R.id.tv_title);
        if (titleTv == null) {
            return;
        }
        titleTv.setText(textResId);
        titleTv.setTextSize(size);
    }

}
