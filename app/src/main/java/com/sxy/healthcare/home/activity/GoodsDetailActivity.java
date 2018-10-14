package com.sxy.healthcare.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.cart.CartActivity;
import com.sxy.healthcare.cart.bean.DelShopEvent;
import com.sxy.healthcare.common.event.JionEvent;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.common.view.CommonDialog;
import com.sxy.healthcare.common.view.CustomDatePicker;
import com.sxy.healthcare.home.bean.BusinessBean;
import com.sxy.healthcare.home.bean.HealthDetailBean;
import com.sxy.healthcare.home.bean.VegetableBean;
import com.sxy.healthcare.me.activity.LoginActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class GoodsDetailActivity extends BaseActivity {
    private static final String TAG = GoodsDetailActivity.class.getSimpleName();

    private BusinessBean businessBean;

    private VegetableBean vegetableBean;

    @BindView(R.id.iv_pic)
    ImageView imageView;

    @BindView(R.id.tv_name)
    TextView name;

    @BindView(R.id.tv_price)
    TextView price;

    @BindView(R.id.tv_content)
    TextView content;

    @BindView(R.id.btn)
    TextView btn;

    @BindView(R.id.num)
    TextView num;

    @BindView(R.id.iv_cart)
    ImageView toCart;

    private CustomDatePicker customDatePicker2;

    private String now;

    private CommonDialog commonDialog;

    private String mTime;

    private int currentNum=0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        vegetableBean = (VegetableBean) getIntent().getSerializableExtra(Constants.EXTRA_HEALTH);
        businessBean = (BusinessBean) getIntent().getSerializableExtra(Constants.EXTRA_BUSINESS_DETAIL);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goods_detail);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        if(null!=vegetableBean){
            setCurrentTitle(vegetableBean.getAname());
        }
        doReturn();
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        getDetail();
       // initDatePicker();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(DelShopEvent delShopEvent) {
      getDetail();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void initListener() {
        super.initListener();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent;
                if (!"login".equals(sharedPrefsUtil.getString(Constants.LOGIN_SUCCESS,""))){
                    intent = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    addToCart();
                }
            }
        });

        toCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;
                if (!"login".equals(sharedPrefsUtil.getString(Constants.LOGIN_SUCCESS,""))){
                    intent = new Intent(GoodsDetailActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                    intent = new Intent(GoodsDetailActivity.this,CartActivity.class);
                    startActivity(intent);
                }

            }
        });
    }

    private void initDatePicker() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
        now = sdf.format(new Date());


        customDatePicker2 = new CustomDatePicker(this, new CustomDatePicker.ResultHandler() {
            @Override
            public void handle(String time) { // 回调接口，获得选中的时间
                mTime = time;
                //bookingOther();
            }
        }, "2018-05-29 00:00", "2028-05-29 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动
    }

    /**
     * 获取详情
     * */
    private void getDetail(){

        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }

        if(null==vegetableBean){
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id", vegetableBean.getId());

        //  jsonObject.addProperty("tabType", id);

        LogUtils.d(TAG,"[getDetail] JsonObject="+jsonObject.toString());
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


        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),jsonObject1.toString());


        ApiServiceFactory.getStringApiService()
                .getGoodsDetail(body)
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

                            LogUtils.d(TAG,"[getDetail] result="+result.toString());

                            Gson gson = new Gson();
                            Type type = new TypeToken<HealthDetailBean>() {}.getType();
                            HealthDetailBean response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    LogUtils.d(TAG,"[getDetail] 111="+response.getData().toString());
                                    if(response.getData()!=null){
                                        Glide.with(GoodsDetailActivity.this)
                                                .load(response.getData().getPic())
                                                .apply(GlideUtils.getOptions()).into(imageView);
                                        name.setText(response.getData().getAname());
                                        price.setText(response.getData().getPrice()+"/"+response.getData().getUnit());
                                        if(response.getData().getContent()!=null){
                                            content.setText(Html.fromHtml(response.getData().getContent()));
                                        }
                                        num.setText(response.getData().getCurrentNum()+"");
                                        currentNum = response.getData().getCurrentNum();
                                    }

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
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    /**
     * 加入购物车
     * */
    private void addToCart(){

        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("goodsId", vegetableBean.getId());
        jsonObject.addProperty("quantity", 1);

        //  jsonObject.addProperty("tabType", id);

        LogUtils.d(TAG,"JsonObject="+jsonObject.toString());
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


        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),jsonObject1.toString());

        ApiServiceFactory.getStringApiService()
                .addToCart(body)
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
                            Type type = new TypeToken<Response>() {}.getType();
                            Response response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    currentNum = currentNum+1;
                                    num.setText((currentNum)+"");

                                    ToastUtils.shortToast(HealthcaseApplication.getApplication(),"加入成功");
                                    EventBus.getDefault().post(new JionEvent());

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
                        ToastUtils.shortToast(HealthcaseApplication.getApplication(),"加入失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }
}
