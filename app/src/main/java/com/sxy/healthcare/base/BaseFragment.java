package com.sxy.healthcare.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sxy.healthcare.common.utils.SharedPrefsUtil;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Author: chenlidong
 * TIME: 2018/4/1
 * Desc: This is BaseFragment
 */
public class BaseFragment extends Fragment {

    protected View mRootView;

    @LayoutRes private int layoutResId; // 布局resId

    private Unbinder mUnbinder; // ButterKnife 解绑对象

    protected SharedPrefsUtil sharedPrefsUtil;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPrefsUtil = SharedPrefsUtil.getInstance(HealthcaseApplication.getApplication());
    }

    /**
     * 设置布局页面
     * @param layoutResId 布局资源
     */
    protected void setContentView(@LayoutRes int layoutResId) {
        if (layoutResId != 0) {
            this.layoutResId = layoutResId;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(this.layoutResId, container, false);
        }
        return mRootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mUnbinder = ButterKnife.bind(this, mRootView);
        initViews();
        initDatas();
        initListener();
    }



    protected void initViews(){

    }

    protected void initDatas(){

    }

    protected void initListener(){

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}

