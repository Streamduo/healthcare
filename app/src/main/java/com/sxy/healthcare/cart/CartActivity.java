package com.sxy.healthcare.cart;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.cart.adapter.ShoppingCartAdapter;
import com.sxy.healthcare.cart.bean.CartBean;
import com.sxy.healthcare.cart.bean.GoodsBean;
import com.sxy.healthcare.common.event.ShopEvent;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.activity.MyOrderActivity;
import com.sxy.healthcare.me.activity.ProfileOrderActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class CartActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = CartActivity.class.getSimpleName();

    @BindView(R.id.rc_shopping_cart)
    RecyclerView mRecyclerView;

    @BindView(R.id.cb_all_check)
    CheckBox checkBox;

    @BindView(R.id.btn_order)
    TextView commit;

    @BindView(R.id.tv_all_cost)
    TextView allCost;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private List<GoodsBean> datas = new ArrayList<>();

    private ShoppingCartAdapter mAdapter;

    private Disposable cartDis;

    private BigDecimal price;

    private boolean checked = false;

    List<GoodsBean> goodsBeans;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ShoppingCartAdapter(this,getSupportFragmentManager());
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        getCarts();
    }

    @Override
    protected void initListener() {
        super.initListener();

        findViewById(R.id.ibtn_return).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        swipeRefreshLayout.setOnRefreshListener(this);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checked){
                    checkBox.setChecked(false);
                    checked = false;
                    datas = mAdapter.getGoodsBeans();
                    if(null!=datas){
                        for(int i=0;i<datas.size();i++){
                            datas.get(i).setSelect(false);
                        }
                        price = BigDecimal.ZERO;
                        mAdapter.setGoodsBeans(datas);
                        allCost.setText("合计："+price+"积分");
                    }
                }else {
                    checkBox.setChecked(true);
                    checked = true;
                    price = BigDecimal.ZERO;
                    datas = mAdapter.getGoodsBeans();
                    if(null!=datas){
                        for(int i=0;i<datas.size();i++){
                            datas.get(i).setSelect(true);
                          //  price = price+(datas.get(i).getQuantity()* Float.parseFloat(datas.get(i).getPrice()));
                            price = price.add((new BigDecimal(datas.get(i).getQuantity())).multiply((new BigDecimal(datas.get(i).getPrice()))));
                        }
                        mAdapter.setGoodsBeans(datas);
                        allCost.setText("合计："+price+"积分");
                    }
                }
            }
        });

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doCommit();
            }
        });
    }

    private void getCarts(){

        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }
        destroyCartDis();


        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token",sharedPrefsUtil.getString(Constants.USER_TOKEN,""));

            RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),jsonObject.toString());

            ApiServiceFactory.getStringApiService()
                    .getCarts(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            cartDis = d;
                        }

                        @Override
                        public void onNext(String stringResponse) {
                            if(swipeRefreshLayout.isRefreshing()){
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            try {

                                String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                        sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                                LogUtils.d(TAG,"result="+result.toString());

                                Gson gson = new Gson();
                                CartBean response = gson.fromJson(result,CartBean.class);

                                if(null!=response){
                                    if(response.isSuccess()){
                                        LogUtils.d(TAG,"111="+response.getData().toString());
                                        mAdapter.setGoodsBeans(response.getData());
                                    }else {
                                        ToastUtils.shortToast(HealthcaseApplication.getApplication(),"");
                                    }
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"获取数据失败～");
                            if(swipeRefreshLayout.isRefreshing()){
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void doCommit(){

        commit.setEnabled(false);
        LogUtils.d(TAG,"[doCommit]");
        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            commit.setEnabled(true);
            return;
        }

        StringBuffer ids = new StringBuffer();
        goodsBeans = mAdapter.getGoodsBeans();
        if(goodsBeans!=null){
            for(int i=0;i<goodsBeans.size();i++){
                if(goodsBeans.get(i).isSelect()){
                    ids.append(goodsBeans.get(i).getId()+",");
                }
            }
        }

        LogUtils.d(TAG,"[doCommit] ids="+ids.toString());

        if(ids.toString().length()<2){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"请选择订单");
            commit.setEnabled(true);
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("ids",ids.toString());

        String param = null;
        try {
            param = ThreeDesUtils.encryptThreeDESECB(jsonObject.toString(),
                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("token",sharedPrefsUtil.getString(Constants.USER_TOKEN,""));
            jsonObject1.put("param",param);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject1.toString());

        ApiServiceFactory.getStringApiService()
                .commit(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result.toString());

                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result,Response.class);

                            if(null!=response){
                                if(response.isSuccess()){
                                    ToastUtils.shortToast(HealthcaseApplication.getApplication(),"下单成功～");

                                    for(int i=0;i<goodsBeans.size();i++){
                                        if(goodsBeans.get(i).isSelect()){
                                            goodsBeans.remove(i);
                                        }
                                        mAdapter.setGoodsBeans(goodsBeans);

                                        Intent intent = new Intent(CartActivity.this, MyOrderActivity.class);
                                        startActivity(intent);
                                    }
                                }else {
                                    ToastUtils.shortToast(HealthcaseApplication.getApplication(),"");
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        commit.setEnabled(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(HealthcaseApplication.getApplication(),"下单失败～");
                        commit.setEnabled(true);
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void destroyCartDis(){
        if (null!=cartDis&&!cartDis.isDisposed()){
            cartDis.dispose();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(ShopEvent shopEvent) {
        datas = mAdapter.getGoodsBeans();
        price = BigDecimal.ZERO;
        if(null!=datas){
            for(int i=0;i<datas.size();i++){
                if(datas.get(i).isSelect()){
                   // price = price+(datas.get(i).getQuantity()* Float.parseFloat(datas.get(i).getPrice()));
                    price = price.add((new BigDecimal(datas.get(i).getQuantity())).multiply((new BigDecimal(datas.get(i).getPrice()))));
                }
            }
            allCost.setText("合计："+price+"积分");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyCartDis();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        getCarts();
    }
}
