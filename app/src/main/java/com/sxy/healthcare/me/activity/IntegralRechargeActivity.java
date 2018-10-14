package com.sxy.healthcare.me.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.cart.adapter.ShoppingCartAdapter;
import com.sxy.healthcare.cart.bean.GoodsBean;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.adapter.IntegralAdapter;
import com.sxy.healthcare.me.bean.IntegralBean;
import com.sxy.healthcare.me.bean.IntegralInfo;
import com.sxy.healthcare.me.bean.OrderBean;
import com.sxy.healthcare.me.bean.PayInfo;
import com.sxy.healthcare.me.bean.PayResult;
import com.sxy.healthcare.me.bean.UserInfo;
import com.sxy.healthcare.me.bean.WxPayBean;
import com.sxy.healthcare.me.dialog.PayDialog;
import com.sxy.healthcare.me.event.CancelEvent;
import com.sxy.healthcare.me.event.CheckBoxEvent;
import com.sxy.healthcare.me.event.PayEvent;
import com.tencent.mm.opensdk.modelpay.PayReq;

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

public class IntegralRechargeActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = IntegralRechargeActivity.class.getSimpleName();

  /*  @BindView(R.id.rc_integral)
    RecyclerView mRecyclerView;

    private IntegralAdapter mAdapter;*/

    private UserInfo userInfo;

    @BindView(R.id.tv_balance)
    TextView balance;

    @BindView(R.id.btn_submit)
    Button btn;

    @BindView(R.id.et_price)
    EditText price;

    @BindView(R.id.item_cb_integral)
    CheckBox checkBox;
    @BindView(R.id.item_cb_integral_1)
    CheckBox checkBox1;

    private Disposable disposable;

    private String  orderInfo;

    private IntegralBean integralBean;

    public static final int SDK_PAY_FLAG = 1;

    IntegralInfo response;

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
                        Toast.makeText(IntegralRechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        PayEvent payEvent = new PayEvent();
                        payEvent.setPayType(2);
                        EventBus.getDefault().post(payEvent);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(IntegralRechargeActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(IntegralRechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
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
        setContentView(R.layout.activity_integral_recharge);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(R.string.integral_recharge);
        doReturn();

      /*  mAdapter = new  IntegralAdapter(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);*/

      price.addTextChangedListener(new TextWatcher() {
          @Override
          public void beforeTextChanged(CharSequence s, int start, int count, int after) {

          }

          @Override
          public void onTextChanged(CharSequence s, int start, int before, int count) {
              if (price.getText().toString().matches("^0")) {//判断当前的输入第一个数是不是为0
                  price.setText("");
              }
          }

          @Override
          public void afterTextChanged(Editable s) {

          }
      });
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.EXTRA_USER_INFO);
        if(userInfo!=null){
            balance.setText(userInfo.getBalance()+"积分");
        }
        getIntegrals();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(CheckBoxEvent checkBoxEvent) {
        checkBox.setChecked(false);
    }

    @Override
    protected void initListener() {
        super.initListener();
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkBox.setChecked(true);
                    checkBox1.setChecked(false);
                }
            }
        });
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    checkBox1.setChecked(true);
                    checkBox.setChecked(false);
                }
            }
        });
       /* checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox.isChecked()){
                    checkBox.setChecked(true);
                    checkBox1.setChecked(false);
                }
            }
        });

        checkBox1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox1.isChecked()){
                    checkBox1.setChecked(true);
                    checkBox.setChecked(false);
                }
            }
        });*/

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!checkBox1.isChecked()&&!checkBox.isChecked()){
                    ToastUtils.LongToast(IntegralRechargeActivity.this,"请输入充值类型！");
                    return;
                }

                if(checkBox.isChecked()){
                    if(StringUtils.isEmpty(price.getText().toString())){
                        ToastUtils.LongToast(IntegralRechargeActivity.this,"请输入充值金额！");
                        return;
                    }
                }

                LogUtils.d(TAG,"1111");

                final PayDialog payDialog = PayDialog.newInstance(2);
                payDialog.show(getSupportFragmentManager(),TAG);
                payDialog.setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        payDialog.dismiss();
                    }
                });

                payDialog.setAlListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            payDialog.dismiss();
                            doScoreRecharge(4);
                    }
                });

                payDialog.setWxListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        payDialog.dismiss();
                        doScoreRecharge(3);
                    }
                });

                LogUtils.d(TAG,"2222");
            }
        });
    }



    /**
     * 积分充值
     * */
    private void doScoreRecharge(final int type){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

       /* if(mAdapter.getIntegralBeans()==null){
            return;
        }

        for (int i=0;i<mAdapter.getIntegralBeans().size();i++){
            if(mAdapter.getIntegralBeans().get(i).isSelected()){
                integralBean = mAdapter.getIntegralBeans().get(i);
                break;
            }
        }*/

        LogUtils.d(TAG,"333");

        if(checkBox1.isChecked()){
            LogUtils.d(TAG,"4444");
            if(response==null&&response.getData()==null){
                return;
            }
            integralBean = response.getData().get(1);
            if(integralBean==null){
                ToastUtils.shortToast(getApplicationContext(),"请选择充值金额");
                return;
            }
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("rechargeId",integralBean.getId());
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
                    .scoreRecharge(body)
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

                                    if(type==4){
                                        orderInfo = response.getData();
                                        doForAlPay(orderInfo);
                                    }else if(type==3){
                                        //  LogUtils.d(TAG,"WX="+orderInfo.replace("\\",""));
                                        //  String wwxpay = orderInfo.replace("\\","");

                                        Gson gsonwx = new Gson();
                                        PayInfo payInfo = gsonwx.fromJson(result,PayInfo.class);

                                        doForWxPay(payInfo.getData());
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
        }else if(checkBox.isChecked()){
            LogUtils.d(TAG,"5555");
            final JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("score",price.getText().toString());
            jsonObject.addProperty("paymentWay",type);
            jsonObject.addProperty("isTry",1);

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
                    .customScoreRecharge(body)
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

                                    if(type==4){
                                        orderInfo = response.getData();
                                        doForAlPay(orderInfo);
                                    }else if(type==3){
                                        //  LogUtils.d(TAG,"WX="+orderInfo.replace("\\",""));
                                        //  String wwxpay = orderInfo.replace("\\","");

                                        Gson gsonwx = new Gson();
                                        PayInfo payInfo = gsonwx.fromJson(result,PayInfo.class);

                                        doForWxPay(payInfo.getData());
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


    }

    private void doForAlPay(final String orderInfo){
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(IntegralRechargeActivity.this);
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


    /**
     * 我的交易记录
     * */
    private void getIntegrals(){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        destroyDis();

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("token",sharedPrefsUtil.getString(Constants.USER_TOKEN,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject1.toString());


        ApiServiceFactory.getStringApiService()
                .getRecharges(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));
                            LogUtils.d(TAG,"result11="+result);
                            Gson gson = new Gson();
                            response = gson.fromJson(result,IntegralInfo.class);

                            if(response.isSuccess()){
                                LogUtils.d(TAG,"result="+response.toString());
                              //  mAdapter.setIntegralBeans(response.getData());
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
        if (null!=disposable&&!disposable.isDisposed()){
            disposable.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDis();
    }
}
