package com.sxy.healthcare.me.adapter;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.base.dialog.CommonDialog;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.SharedPrefsUtil;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.bean.OrderBean;
import com.sxy.healthcare.me.bean.PayInfo;
import com.sxy.healthcare.me.bean.WxPayBean;
import com.sxy.healthcare.me.dialog.OrderSettleDialog;
import com.sxy.healthcare.me.event.CancelEvent;
import com.sxy.healthcare.me.event.PayEvent;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class OrderAdapter extends RecyclerArrayAdapter<OrderBean> {

    private static final String TAG = OrderAdapter.class.getSimpleName();

    private Activity mContext;

    private List<OrderBean> orderBeans;

    private FragmentManager mFragmentManager;

    private SharedPrefsUtil sharedPrefsUtil;

    private OrderAdapter orderAdapter;

    private String orderInfo;

    private Handler mHandler;

    public static final int SDK_PAY_FLAG = 1;

   private  OrderSettleDialog orderSettleDialog;

    public OrderAdapter(Activity context,FragmentManager fragmentManager,Handler handler){
        super(context);
        this.mContext = context;
        this.mFragmentManager = fragmentManager;
        sharedPrefsUtil = SharedPrefsUtil.getInstance(mContext);
        orderAdapter = this;
        this.mHandler = handler;
    }


    @Override
    public BaseViewHolder OnCreateViewHolder(ViewGroup parent, int viewType) {

        ViewHolder viewHolder =new ViewHolder(parent);

        return  viewHolder;
    }


    public class ViewHolder extends BaseViewHolder<OrderBean>{

        public TextView btnCancel;
        public TextView btnJs;
        public TextView orderNo;
        public TextView orderName;
        public ImageView orderImg;
        public TextView orderPrice;
        private RequestOptions options;
        private TextView canceled;

        private LinearLayout layout;

        public ViewHolder(ViewGroup itemView) {
            super(itemView,R.layout.item_order);
            btnCancel = $(R.id.tv_cancel_order);
            btnJs = $(R.id.tv_order_settlement);
            orderNo = $(R.id.tv_order_no);
            orderName = $(R.id.tv_order_name);
            orderImg = $(R.id.iv_order_img);
            orderPrice = $(R.id.tv_order_price);
            canceled = $(R.id.tv_canceled);
            options = new RequestOptions()
                    .placeholder(R.color.gray_979797)
                    .error(R.color.gray_979797);
        }



        @Override
        public void setData(final OrderBean data) {
            super.setData(data);
            orderNo.setText("订单编号："+data.getOrderId());
            orderName.setText(data.getOrderDesc());
            Glide.with(mContext).load(data.getMainPic()).apply(options).into(orderImg);
            orderPrice.setText("积分消费："+data.getRealPrice()+"");

            if(data.getOrderStatus().equals("0")){
                btnJs.setVisibility(View.VISIBLE);
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText("删除订单");
                canceled.setVisibility(View.GONE);
                btnJs.setText("去支付");
            }else if(data.getOrderStatus().equals("1")){
                btnJs.setVisibility(View.VISIBLE);
                canceled.setVisibility(View.GONE);
               //canceled.setText("取消订单");
                btnJs.setText("已付款");
            } else if(data.getOrderStatus().equals("2")){
                btnJs.setVisibility(View.GONE);
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText("取消订单");
                canceled.setVisibility(View.VISIBLE);
                canceled.setText("已接单");
            }else if(data.getOrderStatus().equals("3")){
                btnJs.setVisibility(View.GONE);
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText("删除订单");
                canceled.setText("超时关闭");
                canceled.setVisibility(View.VISIBLE);
            }else if(data.getOrderStatus().equals("4")){
                btnJs.setVisibility(View.GONE);
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText("删除订单");
                canceled.setVisibility(View.VISIBLE);
                canceled.setText("订单取消");
            }else  if(data.getOrderStatus().equals("5")){
                btnJs.setVisibility(View.GONE);
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setText("删除订单");
                canceled.setVisibility(View.VISIBLE);
                canceled.setText("订单完成");
            }

           btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(btnCancel.getText().toString().equals("取消订单")){
                        final CommonDialog commonDialog = CommonDialog.newInstance("你确定要取消订单吗？",
                                "取消","确定");
                        commonDialog.show(mFragmentManager,TAG);
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
                                cancelOrder(data);
                            }
                        });
                    }else if(btnCancel.getText().toString().equals("删除订单")){
                        final CommonDialog commonDialog = CommonDialog.newInstance("你确定要删除订单吗？",
                                "取消","确定");
                        commonDialog.show(mFragmentManager,TAG);
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
                                delOrder(data);
                            }
                        });
                    }

                }
            });

          /*  btnJs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final PayDialog payDialog = PayDialog.newInstance(1);
                    payDialog.show(mFragmentManager,TAG);
                    payDialog.setWxListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            payDialog.dismiss();
                            if(data==null){
                                return;
                            }
                            doPay(data,3);
                        }
                    });
                    payDialog.setAlListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            payDialog.dismiss();
                            if(data==null){
                                return;
                            }
                            doPay(data,4);
                        }
                    });

                    payDialog.setJFalListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(data==null){
                                return;
                            }
                            doPay(data,2);
                            payDialog.dismiss();
                        }
                    });

                    payDialog.setWxJFListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(data==null){
                                return;
                            }
                            doPay(data,1);
                            payDialog.dismiss();
                        }
                    });

                    payDialog.setJFListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            payDialog.dismiss();
                            doPay(data,0);
                        }
                    });

                    payDialog.setCancelListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            payDialog.dismiss();
                        }
                    });
                   *//* orderSettleDialog = OrderSettleDialog.newInstance();
                    orderSettleDialog.show(mFragmentManager,TAG);
                    orderSettleDialog.setConfirmListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                          //  ToastUtils.shortToast(getContext(),"结算");
                            if(StringUtils.isEmpty(orderSettleDialog.getPay())){
                                ToastUtils.shortToast(getContext(),"请输入结算金额");
                                return;
                            }else {
                                doPay(data);
                            }
                        }
                    });*//*
                }
            });*/
        }
    }


    /**
     * 取消订单
     * */
    private void cancelOrder(final OrderBean orderBean){

        if(!NetUtils.isNetworkAvailable(mContext.getApplicationContext())){
            ToastUtils.shortToast(mContext.getApplicationContext(),"当前网络不可用～");
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
                                ToastUtils.shortToast(mContext.getApplicationContext(),"取消成功");
                              //  orderAdapter.remove(orderBean);
                                CancelEvent cancelEvent = new CancelEvent();
                                EventBus.getDefault().post(cancelEvent);
                            }else {
                                ToastUtils.shortToast(mContext.getApplicationContext(),response.getMsg());
                            }
                            LogUtils.d(TAG,"result="+response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(mContext.getApplicationContext(),"取消失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 删除订单
     * */
    private void delOrder(final OrderBean orderBean){

        if(!NetUtils.isNetworkAvailable(mContext.getApplicationContext())){
            ToastUtils.shortToast(mContext.getApplicationContext(),"当前网络不可用～");
            return;
        }

        if(orderBean==null){
            return;
        }

        int type =0;

        if("1".equals(orderBean.getOrderType())){
            type = 1;
        }else  if("2".equals(orderBean.getOrderType())){
            type = 2;
        }else {
            type =3;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type",type);
        jsonObject.addProperty("id",orderBean.getOrderId());


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
                .delOder(body)
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
                                ToastUtils.shortToast(mContext.getApplicationContext(),"删除成功");
                                //  orderAdapter.remove(orderBean);
                                CancelEvent cancelEvent = new CancelEvent();
                                EventBus.getDefault().post(cancelEvent);
                            }else {
                                ToastUtils.shortToast(mContext.getApplicationContext(),response.getMsg());
                            }
                            LogUtils.d(TAG,"result="+response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(mContext.getApplicationContext(),"删除失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    public void hideDialog() {
       if(orderSettleDialog!=null){
           orderSettleDialog.dismiss();;
           orderSettleDialog= null;
       }
    }

    /**
     * 支付
     * */
    private void doPay(final OrderBean orderBean,final int type){

        if(!NetUtils.isNetworkAvailable(mContext.getApplicationContext())){
            ToastUtils.shortToast(mContext.getApplicationContext(),"当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("billNo",orderBean.getOrderId());
        jsonObject.addProperty("payBsType",1);
        jsonObject.addProperty("paymentWay",type);

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
                             //   orderInfo = response.getData();
                               // doForPay(orderInfo);


                                if(type==4){
                                    orderInfo = response.getData();
                                    doForPay(orderInfo);
                                }else if(type==3){
                                    //  LogUtils.d(TAG,"WX="+orderInfo.replace("\\",""));
                                    //  String wwxpay = orderInfo.replace("\\","");

                                    Gson gsonwx = new Gson();
                                    PayInfo payInfo = gsonwx.fromJson(result,PayInfo.class);

                                    doForWxPay(payInfo.getData());
                                }else if(type==0){
                                    PayEvent payEvent = new PayEvent();
                                    payEvent.setPayType(3);
                                    EventBus.getDefault().post(payEvent);
                                    Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                                }else if(type==2){
                                    PayEvent payEvent = new PayEvent();
                                    payEvent.setPayType(3);
                                    EventBus.getDefault().post(payEvent);
                                    Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                                }else if(type==1){
                                    PayEvent payEvent = new PayEvent();
                                    payEvent.setPayType(3);
                                    EventBus.getDefault().post(payEvent);
                                    Toast.makeText(mContext, "支付成功", Toast.LENGTH_SHORT).show();
                                }

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

    private void doForPay(final String orderInfo){
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(mContext);
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
}
