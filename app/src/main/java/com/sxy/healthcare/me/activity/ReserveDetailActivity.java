package com.sxy.healthcare.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
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
import com.sxy.healthcare.home.adapter.FoodAdapter;
import com.sxy.healthcare.home.bean.GoodsCuisinesBean;
import com.sxy.healthcare.me.adapter.SelectedFoodAdapter;
import com.sxy.healthcare.me.bean.BookingBean;
import com.sxy.healthcare.me.bean.ReserveDetailBean;
import com.sxy.healthcare.me.bean.ReserveDetailInfo;
import com.sxy.healthcare.me.bean.UserInfo;
import com.sxy.healthcare.me.event.PayEvent;

import org.greenrobot.eventbus.EventBus;
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

public class ReserveDetailActivity extends BaseActivity {

    private static final String TAG = ReserveDetailActivity.class.getSimpleName();

    @BindView(R.id.rc_1)
    RecyclerView recyclerView1;

    @BindView(R.id.rc_2)
    RecyclerView recyclerView2;

    @BindView(R.id.iv_order)
    ImageView imageView;

    @BindView(R.id.tv_order_name)
    TextView tvName;

    @BindView(R.id.tv_price)
    TextView price;

    @BindView(R.id.tv_status)
    TextView status;

    @BindView(R.id.tv_person_num)
    TextView num;

    @BindView(R.id.tv_time)
    TextView time;

    @BindView(R.id.tv_time11)
    TextView time11;

    @BindView(R.id.tv_pay)
    TextView pay;

    private BookingBean bookingBean;


    private Disposable orderDetailDis;

    private FoodAdapter foodAdapter;

    List<GoodsCuisinesBean> goodsCuisines;

    private SelectedFoodAdapter selectedFoodAdapter;

    private ReserveDetailBean reserveDetailBean;

    private UserInfo userInfo;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserve_detail);

        setCurrentTitle(R.string.order_detail);
        doReturn();
    }

    @Override
    protected void initViews() {
        super.initViews();
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        sharedPrefsUtil = SharedPrefsUtil.getInstance(getApplicationContext());

        bookingBean = (BookingBean)getIntent().getSerializableExtra("reserveBean");

        foodAdapter = new FoodAdapter(this);
        recyclerView1.setLayoutManager(new GridLayoutManager(this,4));
        recyclerView1.setAdapter(foodAdapter);

        goodsCuisines = new ArrayList<>();

        selectedFoodAdapter = new SelectedFoodAdapter(this);
        recyclerView2.setLayoutManager(new GridLayoutManager(this,2));
        recyclerView2.setAdapter(selectedFoodAdapter);

        getOrdersDetail();

    }

    @Override
    protected void initListener() {
        super.initListener();
        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pay.getText().toString().equals("去支付")){
                    final CommonDialog commonDialog = CommonDialog.newInstance("确认支付"
                                    +reserveDetailBean.getSummaryVo().getRealPrice()+"积分？",
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
        jsonObject.addProperty("bookNo",bookingBean.getBookNo());

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
                .getBookingRestaurantDetail(body)
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
                            ReserveDetailInfo response = gson.fromJson(result,ReserveDetailInfo.class);

                            if(response.isSuccess()){
                                if (response.getData()!=null){

                                    reserveDetailBean = response.getData();

                                    Glide.with(ReserveDetailActivity.this)
                                            .load(response.getData().getTraderDetailVo().getPic()).apply(GlideUtils.getOptions()).into(imageView);

                                    tvName.setText(response.getData().getTraderDetailVo().getTraderName());

                                     price.setText("积分消费："+response.getData().getSummaryVo().getRealPrice()+"");

                                    num.setText("人数："+response.getData().getSummaryVo().getNum());

                                    time.setText("下单时间："+response.getData().getSummaryVo().getCreateTime());

                                    time11.setText("就餐时间："+response.getData().getSummaryVo().getBookTime());

                                    if(response.getData().getGoodsCuisines()!=null){
                                        for(int i=0;i<response.getData().getGoodsCuisines().length;i++){
                                            GoodsCuisinesBean goodsCuisinesBean = new GoodsCuisinesBean();
                                            goodsCuisinesBean.setName(response.getData().getGoodsCuisines()[i]);

                                            goodsCuisines.add(goodsCuisinesBean);
                                        }
                                        foodAdapter.setList(goodsCuisines);
                                    }else {
                                        findViewById(R.id.ll_caixi).setVisibility(View.GONE);
                                    }

                                    if(response.getData().getBookingGoodsVos()!=null){
                                        selectedFoodAdapter.setVegetableBeans(response.getData().getBookingGoodsVos());
                                    }else {
                                        findViewById(R.id.ll_food).setVisibility(View.GONE);
                                    }

                                    if(response.getData().getSummaryVo().getState()==0){
                                        status.setText("已下单");
                                        pay.setVisibility(View.VISIBLE);
                                    }else    if(response.getData().getSummaryVo().getState()==1){
                                        status.setText("已完成");
                                        pay.setVisibility(View.VISIBLE);
                                        pay.setText("已完成");
                                        pay.setClickable(false);
                                    }else    if(response.getData().getSummaryVo().getState()==2){
                                        status.setText("商家已接单");
                                        pay.setVisibility(View.VISIBLE);
                                        pay.setText("商家已接单");
                                        pay.setClickable(false);
                                    }else    if(response.getData().getSummaryVo().getState()==3){
                                        status.setText("超时关闭");
                                        pay.setVisibility(View.VISIBLE);
                                        pay.setText("超时关闭");
                                        pay.setClickable(false);
                                    }else    if(response.getData().getSummaryVo().getState()==4){
                                        status.setText("已取消");
                                        pay.setVisibility(View.VISIBLE);
                                        pay.setText("已取消");
                                        pay.setClickable(false);
                                    }else    if(response.getData().getSummaryVo().getState()==5){
                                        status.setText("订单完成");
                                        pay.setVisibility(View.GONE);
                                        pay.setClickable(false);
                                    }

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

        if(reserveDetailBean==null){
            return;
        }

        double balance = 0;

        if(!StringUtils.isEmpty(sharedPrefsUtil.getString(Constants.USER_INFO_BALANCE,""))){
            balance = Double.parseDouble(sharedPrefsUtil.getString(Constants.USER_INFO_BALANCE,""));
        }

        if(balance<reserveDetailBean.getSummaryVo().getRealPrice()){
            Intent intent = new Intent(ReserveDetailActivity.this,IntegralRechargeActivity.class);
            userInfo = new UserInfo();
            userInfo.setBalance(balance+"");
            intent.putExtra(Constants.EXTRA_USER_INFO,userInfo);
            startActivity(intent);
        }else {
            final JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("billNo",bookingBean.getBookNo());
            jsonObject.addProperty("payBsType",2);
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
                                    pay.setText("已付款");
                                    status.setText("已支付");
                                    Toast.makeText(ReserveDetailActivity.this, "支付成功", Toast.LENGTH_SHORT).show();

                                    PayEvent payEvent = new PayEvent();
                                    EventBus.getDefault().post(payEvent);
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
                                    Toast.makeText(ReserveDetailActivity.this, "取消成功", Toast.LENGTH_SHORT).show();

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
