package com.sxy.healthcare.me.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.adapter.OrderAdapter;
import com.sxy.healthcare.me.bean.BookingBean;
import com.sxy.healthcare.me.bean.OrderBean;
import com.sxy.healthcare.me.bean.OrderResponse;
import com.sxy.healthcare.me.bean.PayResult;
import com.sxy.healthcare.me.event.CancelEvent;
import com.sxy.healthcare.me.event.PayEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

import static com.sxy.healthcare.me.adapter.OrderAdapter.SDK_PAY_FLAG;

public class ProfileOrderActivity extends BaseActivity implements View.OnClickListener,
        RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener,
        RecyclerArrayAdapter.OnNoMoreListener{

    private static final String TAG = ProfileOrderActivity.class.getSimpleName();

    @BindView(R.id.rc_order)
    EasyRecyclerView rcOrder;

    OrderAdapter orderAdapter;

    private List<OrderBean> orderBeans = new ArrayList<>();

    private Disposable orderDis;

    private int pageSize=20;

    private int pageNo=1;

    private int total = 0;

    private boolean isMore = true;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(ProfileOrderActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        orderAdapter.hideDialog();
                        pageNo = 1;
                        getOrders();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(ProfileOrderActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(ProfileOrderActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_order);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(R.string.profile_order);
        doReturn();


        orderAdapter = new OrderAdapter(ProfileOrderActivity.this,getSupportFragmentManager(),mHandler);
        rcOrder.setAdapter(orderAdapter);
        rcOrder.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));


        orderAdapter.setMore(R.layout.view_more, this);
        orderAdapter.setNoMore(R.layout.view_no_more, this);
        rcOrder.setRefreshListener(this);
        orderAdapter.pauseMore();



        orderAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                if(null!=orderBeans){
                    Intent intent;
                    if("2".equals(orderBeans.get(position).getOrderType())){
                        intent = new Intent(ProfileOrderActivity.this,ReserveDetailActivity.class);
                        BookingBean bookingBean = new BookingBean();
                        bookingBean.setBookNo(orderBeans.get(position).getOrderId());
                        intent.putExtra("reserveBean",bookingBean);
                    }else {
                        intent = new Intent(ProfileOrderActivity.this,OrderDetailActivity.class);
                        intent.putExtra(Constants.EXTRA_ORDER,orderBeans.get(position));
                    }
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void initDatas() {
        super.initDatas();
        pageNo = 1;
        getOrders();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }

    }



    /**
     * 我的订单
     * */
    private void getOrders(){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
      /*  if(Math.abs(total-pageSize)<pageSize){
            pageSize = total-pageSize;
            isMore = false;
        }else {
            pageSize = 10;
            isMore = true;
        }*/
        jsonObject.addProperty("pageSize",pageSize);
        jsonObject.addProperty("pageNo", pageNo);


        LogUtils.d(TAG,"jsonObject="+jsonObject.toString());

        String param = null;
        try {
            param = ThreeDesUtils.encryptThreeDESECB(jsonObject.toString(),
                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        destroyDis();

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
                .getOrders(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        orderDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result);

                            Gson gson = new Gson();
                            OrderResponse response = gson.fromJson(result,OrderResponse.class);

                            if(response.isSuccess()){
                                if(null!=response.getData().getOrdersMainVos()){

                                    if(pageNo==1){
                                        orderBeans.clear();
                                        orderBeans.addAll(response.getData().getOrdersMainVos());
                                    }else {
                                        orderBeans.addAll(response.getData().getOrdersMainVos());
                                    }
                                    orderAdapter.clear();
                                    orderAdapter.addAll(orderBeans);
                                    LogUtils.d(TAG,"size="+orderAdapter.getAllData().size()+",orderbean.size="+orderBeans.size());
                                    pageNo = pageNo+1;
                                }
                                total = response.getData().getCount();
                            }else {
                                ToastUtils.shortToast(getApplicationContext(),response.getMsg());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(getApplicationContext(),"获取失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



    private void destroyDis(){
        if (null!=orderDis&&!orderDis.isDisposed()){
            orderDis.dispose();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(PayEvent payEvent) {
        pageNo = 1;
       getOrders();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(CancelEvent cancelEvent) {
        pageNo = 1;
        getOrders();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDis();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        getOrders();
    }

    @Override
    public void onLoadMore() {
        LogUtils.d(TAG,"[onLoadMore] pageNo="+pageNo);
       // if(isMore){
        if(orderAdapter.getCount()<total){
            getOrders();
        }

     //   }
    }

    @Override
    public void onNoMoreShow() {

    }

    @Override
    public void onNoMoreClick() {

    }
}
