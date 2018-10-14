package com.sxy.healthcare.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.dialog.CommonDialog;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.SharedPrefsUtil;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.adapter.OrderDetailAdapter;
import com.sxy.healthcare.me.bean.BookingBean;
import com.sxy.healthcare.me.bean.OrderBean;
import com.sxy.healthcare.me.bean.OrderDetailInfo;
import com.sxy.healthcare.me.bean.OrderDetailResponse;
import com.sxy.healthcare.me.bean.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class HeathReserveActivity extends BaseActivity {

    private static final String TAG = HeathReserveActivity.class.getSimpleName();

    @BindView(R.id.rc_order_detail)
    RecyclerView recyclerView;

    @BindView(R.id.iv_order)
    ImageView imageView;

    @BindView(R.id.tv_order_no)
    TextView tvNo;

    @BindView(R.id.tv_price)
    TextView price;

    @BindView(R.id.tv_status)
    TextView status;

    @BindView(R.id.tv_comment)
    TextView comment;

    @BindView(R.id.tv_pay)
    TextView pay;

    @BindView(R.id.tv_time)
    TextView time;

    @BindView(R.id.tv_time11)
    TextView time11;

    private OrderDetailAdapter detailAdapter;

    private Disposable orderDetailDis;

    private OrderBean orderBean;

    private OrderDetailInfo orderDetailInfo;

    private String orderInfo;

    private BookingBean bookingBean;


    private UserInfo userInfo;

    OrderDetailResponse response;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        setCurrentTitle(R.string.order_detail);
        doReturn();
    }

    @Override
    protected void initViews() {
        super.initViews();
        pay.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        sharedPrefsUtil = SharedPrefsUtil.getInstance(getApplicationContext());

        bookingBean = (BookingBean)getIntent().getSerializableExtra("reserveBean");

        detailAdapter = new OrderDetailAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(detailAdapter);


        getOrdersDetail();

    }

    @Override
    protected void initListener() {
        super.initListener();
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d(TAG,"【setOnClickListener】="+pay.getText().toString());

                if(pay.getText().toString().equals("去支付")){

                    if(response==null){
                        return;
                    }

                    final CommonDialog commonDialog = CommonDialog.newInstance("确认支付"
                                    +response.getData().getRealPrice()+"积分？",
                            "稍后支付","确认支付");
                    commonDialog.show(getSupportFragmentManager(),TAG);
                    commonDialog.setCancelListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            commonDialog.dismiss();
                        }
                    });
                    commonDialog.setConfirmListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            commonDialog.dismiss();
                            doPay();
                        }
                    });

                }else if(pay.getText().toString().equals("取消订单")){
                    cancel();
                }
            }
        });
    }

    /**
     * 订单详情
     * */
    private void getOrdersDetail(){

        if (bookingBean==null){
            return;
        }

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("orderId",bookingBean.getBookNo());

        LogUtils.d(TAG,"JsonObject="+jsonObject.toString());

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
                .getOrdersDetail(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        orderDetailDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"【getOrdersDetail】="+result);

                            Gson gson = new Gson();
                             response = gson.fromJson(result,OrderDetailResponse.class);

                            if(response.isSuccess()){
                                if (response.getData()!=null){
                                    orderDetailInfo = response.getData();
                                    tvNo.setText("订单号："+response.getData().getOrderId());
                                    price.setText("积分消费："+response.getData().getRealPrice());
                                    time.setText("下单时间："+response.getData().getCreateTime());
                                    time11.setText("预约时间："+response.getData().getBookTime());

                                    if(response.getData().getOrderStatus()==0){
                                        status.setText("已下单");
                                    }else    if(response.getData().getOrderStatus()==1){
                                        status.setText("已付款");
                                    }else    if(response.getData().getOrderStatus()==2){
                                        status.setText("商家已接单");
                                    }else    if(response.getData().getOrderStatus()==3){
                                        status.setText("超时关闭");

                                    }else    if(response.getData().getOrderStatus()==4){
                                        status.setText("订单取消");
                                    }else    if(response.getData().getOrderStatus()==5){
                                        status.setText("订单完成");
                                    }

                                    Glide.with(HeathReserveActivity.this)
                                            .load(response.getData().getMainPic()).apply(GlideUtils.getOptions()).into(imageView);

                                    detailAdapter.setOrderDetailBeans(response.getData().getOrdersDetailVos());
                                }
                            }else {
                                ToastUtils.shortToast(getApplicationContext(),"获取失败");
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
        if (null!=orderDetailDis&&!orderDetailDis.isDisposed()){
            orderDetailDis.dispose();
        }
    }


    /**
     * 支付
     * */
    private void doPay(){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        if(orderDetailInfo==null){
            return;
        }

        double balance = 0;

        if(!StringUtils.isEmpty(sharedPrefsUtil.getString(Constants.USER_INFO_BALANCE,""))){
            balance = Double.parseDouble(sharedPrefsUtil.getString(Constants.USER_INFO_BALANCE,""));
        }

        if(balance<orderDetailInfo.getRealPrice()){
            Intent intent = new Intent(HeathReserveActivity.this,IntegralRechargeActivity.class);
            userInfo = new UserInfo();
            userInfo.setBalance(balance+"");
            intent.putExtra(Constants.EXTRA_USER_INFO,userInfo);
            startActivity(intent);
        }else {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("billNo",bookingBean.getBookNo());
            jsonObject.addProperty("payBsType",1);
            jsonObject.addProperty("paymentWay",0);

            LogUtils.d(TAG,"jsonObject="+jsonObject.toString());

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
                    .bookingOrderPay(body)
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
                                LogUtils.d(TAG,"result="+result);

                                Gson gson = new Gson();
                                Response<String> response = gson.fromJson(result,Response.class);

                                if(response.isSuccess()){
                                    //   ToastUtils.shortToast(mContext.getApplicationContext(),"支付成功");
                                    pay.setText("已支付");
                                    status.setText("已支付");
                                    Toast.makeText(HeathReserveActivity.this, "支付成功", Toast.LENGTH_SHORT).show();

                                }else {
                                    //  ToastUtils.shortToast(mContext.getApplicationContext(),response.getMsg());
                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            // ToastUtils.shortToast(mContext.getApplicationContext(),"支付失败～");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });
        }

    }


    /**
     * 取消
     * */
    private void cancel(){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }




        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("bookNo",bookingBean.getBookNo());

        LogUtils.d(TAG,"jsonObject="+jsonObject.toString());

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
                .cancelBooking(body)
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
                            LogUtils.d(TAG,"result="+result);

                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result,Response.class);

                            if(response.isSuccess()){
                                //   ToastUtils.shortToast(mContext.getApplicationContext(),"支付成功");
                                pay.setText("已取消");
                                pay.setClickable(false);
                                status.setText("已取消");
                                Toast.makeText(HeathReserveActivity.this, "取消成功", Toast.LENGTH_SHORT).show();

                            }else {
                                //  ToastUtils.shortToast(mContext.getApplicationContext(),response.getMsg());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        // ToastUtils.shortToast(mContext.getApplicationContext(),"支付失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDis();
    }

}
