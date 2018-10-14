package com.sxy.healthcare.home.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseFragment;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ScreenUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.home.activity.FunCategoryActivity;
import com.sxy.healthcare.home.activity.HealthDetailActivity;
import com.sxy.healthcare.home.adapter.FunAdapter;
import com.sxy.healthcare.home.adapter.SearchHealthAdapter;
import com.sxy.healthcare.home.bean.BusinessBean;
import com.sxy.healthcare.home.bean.VegetableBean;
import com.sxy.healthcare.home.bean.VegetableResponse;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FunFragment extends BaseFragment implements FunAdapter.OnItemClickListener {

    private static final String TAG = HealthFragment.class.getSimpleName();

    @BindView(R.id.rc_fun)
    RecyclerView recyclerView;

    private FunAdapter funAdapter;


    private BusinessBean businessBean;
    private String id;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        businessBean = (BusinessBean)getArguments().getSerializable(Constants.EXTRA_BUSINESS_DETAIL);
        id = getArguments().getString(Constants.EXTRA_MENU_ID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_fun);
    }


    @Override
    protected void initViews() {
        super.initViews();

        funAdapter = new FunAdapter(getContext(),this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));

        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, (int) ScreenUtils.dip2px( 0.5f), 0, 0);
        itemDecoration.setDrawLastItem(false);
        recyclerView.addItemDecoration(itemDecoration);

        funAdapter.setVosBeans(businessBean.getEntertainmentVos());
        recyclerView.setAdapter(funAdapter);

    }

    @Override
    protected void initDatas() {
        super.initDatas();

    }

    @Override
    protected void initListener() {
        super.initListener();

    }


    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getContext(), FunCategoryActivity.class);
        intent.putExtra(Constants.EXTRA_MENU_ID,id);
        intent.putExtra(Constants.EXTRA_BUSINESS_DETAIL,businessBean);
        intent.putExtra(Constants.EXTRA_FUN,funAdapter.getVosBeans().get(position));
        getContext().startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view) {

    }
}
