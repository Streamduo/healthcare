package com.sxy.healthcare.me.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.adapter.OrderDetailAdapter;
import com.sxy.healthcare.me.bean.OrderBean;
import com.sxy.healthcare.me.bean.OrderDetailInfo;
import com.sxy.healthcare.me.bean.OrderDetailResponse;
import com.sxy.healthcare.me.bean.PayResult;
import com.sxy.healthcare.me.bean.UserInfo;
import com.sxy.healthcare.me.bean.WxPayBean;
import com.sxy.healthcare.me.event.CancelEvent;
import com.sxy.healthcare.me.event.PayEvent;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class OrderDetailActivity extends BaseActivity{

    private static final String TAG = OrderDetailActivity.class.getSimpleName();


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

    public static final int SDK_PAY_FLAG = 1;

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
                        Toast.makeText(OrderDetailActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        if(null!=orderBean){
                            getOrdersDetail(orderBean.getId());
                        }
                        PayEvent payEvent = new PayEvent();
                        payEvent.setPayType(2);
                        EventBus.getDefault().post(payEvent);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(OrderDetailActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(OrderDetailActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_order_detail);

        EventBus.getDefault().register(this);
    }


    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(R.string.order_detail);
        doReturn();

        detailAdapter = new OrderDetailAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(detailAdapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        orderBean = (OrderBean) getIntent().getSerializableExtra(Constants.EXTRA_ORDER);
        if(null!=orderBean){
            getOrdersDetail(orderBean.getId());
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(orderDetailInfo!=null&&orderDetailInfo.getJsStatus()==1&&pay.getText().equals("去支付")){

                    doPay(orderBean,0);
                   /* final PayDialog payDialog = PayDialog.newInstance(1);
                    payDialog.show(getSupportFragmentManager(),TAG);
                    payDialog.setWxListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            payDialog.dismiss();
                            doPay(orderBean,3);
                        }
                    });
                   payDialog.setAlListener(new View.OnClickListener() {
                       @Override
                       public void onClick(View v) {
                           payDialog.dismiss();
                           if(orderBean==null){
                               return;
                           }
                           doPay(orderBean,4);
                       }
                   });

                    payDialog.setJFalListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            payDialog.dismiss();
                            if(orderBean==null){
                                return;
                            }
                            doPay(orderBean,2);
                        }
                    });

                    payDialog.setWxJFListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            payDialog.dismiss();
                            if(orderBean==null){
                                return;
                            }
                            doPay(orderBean,1);
                        }
                    });

                    payDialog.setJFListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            payDialog.dismiss();
                            if(orderBean==null){
                                return;
                            }
                            doPay(orderBean,0);
                        }
                    });

                    payDialog.setCancelListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            payDialog.dismiss();
                        }
                    });*/
                }else if(pay.getText().toString().equals("取消订单")){
                    cancelOrder(orderBean);
                }
            }
        });

//
//        comment.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                 Intent intent = new Intent(OrderDetailActivity.this,CommentActivity.class);
//                 intent.putExtra(Constants.EXTRA_ORDER,orderBean);
//                 intent.putExtra("tradeId",orderDetailInfo.getTraderId());
//                 startActivityForResult(intent,0x12);
//            }
//        });
    }

    /**
     * 订单详情
     * */
    private void getOrdersDetail(String id){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("orderId",orderBean.getOrderId());

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
                            OrderDetailResponse response = gson.fromJson(result,OrderDetailResponse.class);

                            if(response.isSuccess()){
                                if (response.getData()!=null){
                                    orderDetailInfo = response.getData();
                                    tvNo.setText("订单号："+response.getData().getOrderId());
                                     price.setText("积分消费："+response.getData().getRealPrice());
                                     time.setText("下单时间："+response.getData().getCreateTime());

                                     if(orderDetailInfo.getOrderType()==3){
                                         time11.setText("预约时间："+response.getData().getBookTime());
                                     }else {
                                         time11.setVisibility(View.GONE);
                                     }


                                     if(response.getData().getOrderStatus()==0){
                                         status.setText("已下单");
                                         comment.setVisibility(View.GONE);
                                         pay.setVisibility(View.VISIBLE);
                                     }else    if(response.getData().getOrderStatus()==1){
                                         status.setText("已付款");
                                         comment.setVisibility(View.GONE);
                                         pay.setVisibility(View.VISIBLE);
                                         pay.setText("已付款");
                                         pay.setClickable(false);
                                     }else    if(response.getData().getOrderStatus()==2){
                                         status.setText("商家已接单");
                                         comment.setVisibility(View.GONE);
                                         pay.setVisibility(View.VISIBLE);
                                         pay.setText("商家已接单");
                                         pay.setClickable(false);
                                     }else    if(response.getData().getOrderStatus()==3){
                                         status.setText("超时关闭");
                                         comment.setVisibility(View.GONE);
                                         pay.setVisibility(View.VISIBLE);
                                         pay.setText("超时关闭");
                                         pay.setClickable(false);
                                     }else    if(response.getData().getOrderStatus()==4){
                                         status.setText("订单取消");
                                         comment.setVisibility(View.GONE);
                                         pay.setVisibility(View.VISIBLE);
                                         pay.setText("订单取消");
                                         pay.setClickable(false);
                                     }else    if(response.getData().getOrderStatus()==5){
                                         status.setText("订单完成");
                                         comment.setVisibility(View.VISIBLE);
                                         comment.setText("已完成");
                                         pay.setVisibility(View.GONE);
                                         pay.setClickable(false);
                                     }

         /*                            if(response.getData().getJsStatus()==1){
                                         status.setText("未结算");
                                     }else    if(response.getData().getOrderStatus()==2){
                                         status.setText("已取消");
                                     }*/

//                                     if(response.getData().getCommendStatus()==0){
//
//                                     }else if(response.getData().getCommendStatus()==1){
//                                          if (comment.getVisibility()==View.VISIBLE){
//                                               comment.setEnabled(false);
//                                               comment.setText("已评价");
//                                          }
//                                     }

                                    Glide.with(OrderDetailActivity.this)
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

    /**
     * 支付
     * */
    private void doPay(final OrderBean orderBean, final int type){

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
            Intent intent = new Intent(OrderDetailActivity.this,IntegralRechargeActivity.class);
            UserInfo userInfo = new UserInfo();
            userInfo.setBalance(balance+"");
            intent.putExtra(Constants.EXTRA_USER_INFO,userInfo);
            startActivity(intent);

            return;
        }


        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("billNo",orderBean.getOrderId());
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

                              /*  if(type==4){
                                    orderInfo = response.getData();
                                    doForAlPay(orderInfo);
                                }else if(type==3){
                                  //  LogUtils.d(TAG,"WX="+orderInfo.replace("\\",""));
                                  //  String wwxpay = orderInfo.replace("\\","");

                                    Gson gsonwx = new Gson();
                                    PayInfo payInfo = gsonwx.fromJson(result,PayInfo.class);

                                    doForWxPay(payInfo.getData());
                                }else if(type==0||type==1||type==2){
                                    comment.setVisibility(View.VISIBLE);
                                    pay.setVisibility(View.GONE);
                                    getOrdersDetail(orderBean.getId());
                                    PayEvent payEvent = new PayEvent();
                                    payEvent.setPayType(3);
                                    EventBus.getDefault().post(payEvent);
                                    Toast.makeText(OrderDetailActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                                }*/

                                pay.setText("已支付");
                                status.setText("已支付");
                                PayEvent payEvent = new PayEvent();
                                payEvent.setPrice(orderBean.getRealPrice());
                                EventBus.getDefault().post(payEvent);
                                Toast.makeText(OrderDetailActivity.this, "支付成功", Toast.LENGTH_SHORT).show();

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

    private void doForAlPay(final String orderInfo){
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(OrderDetailActivity.this);
                String result = alipay.pay(orderInfo,true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    private void doForWxPay(final WxPayBean wxPayBean){
        LogUtils.d(TAG,"doForWxPay11");

        PayReq request = new PayReq();
        request.appId = wxPayBean.getAppid();
        request.partnerId = wxPayBean.getPartnerid();
        request.prepayId= wxPayBean.getPrepayid();
        request.packageValue = wxPayBean.getPackageValue();
        request.nonceStr= wxPayBean.getNoncestr();
        request.timeStamp= wxPayBean.getTimestamp();
        request.sign= wxPayBean.getSign();
        HealthcaseApplication.getIwxapi().sendReq(request);
        LogUtils.d(TAG,"doForWxPay22");
    }

    private void destroyDis(){
        if (null!=orderDetailDis&&!orderDetailDis.isDisposed()){
            orderDetailDis.dispose();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(PayEvent payEvent) {
        if(null!=orderBean&&payEvent.getPayType()==1){
            getOrdersDetail(orderBean.getId());
        }
    }


    /**
     * 取消订单
     * */
    private void cancelOrder(final OrderBean orderBean){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        if(orderBean==null){
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("orderId",orderBean.getOrderId());


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
                .cancelOrder(body)
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

                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result,Response.class);

                            if(response.isSuccess()){
                                ToastUtils.shortToast(getApplicationContext(),"取消成功");
                                //  orderAdapter.remove(orderBean);
                                pay.setText("已取消");
                                pay.setClickable(false);
                                CancelEvent cancelEvent = new CancelEvent();
                                EventBus.getDefault().post(cancelEvent);
                            }else {
                                ToastUtils.shortToast(getApplicationContext(),response.getMsg());
                            }
                            LogUtils.d(TAG,"result="+response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(getApplicationContext(),"取消失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
//        if(resultCode==RESULT_OK){
//            if(requestCode==0x12){
//                comment.setText("已评价");
//                comment.setEnabled(false);
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDis();
        EventBus.getDefault().unregister(this);
    }

}
