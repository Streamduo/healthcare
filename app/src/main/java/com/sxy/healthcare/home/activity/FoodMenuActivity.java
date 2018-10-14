package com.sxy.healthcare.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.common.view.CommonDialog;
import com.sxy.healthcare.common.view.CustomDatePicker;
import com.sxy.healthcare.home.adapter.FoodMenuAdapter;
import com.sxy.healthcare.home.bean.BusinessBean;
import com.sxy.healthcare.home.bean.VegetableResponse;
import com.sxy.healthcare.me.activity.LoginActivity;
import com.sxy.healthcare.me.activity.ReserveDetailActivity;
import com.sxy.healthcare.me.bean.BookingBean;

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

public class FoodMenuActivity extends BaseActivity {

    private static final String TAG = FoodMenuActivity.class.getSimpleName();

    @BindView(R.id.rc_menu)
    RecyclerView recyclerView;

    @BindView(R.id.confirm_btn)
    TextView btnConfirm;

    private FoodMenuAdapter adapter;

    private Disposable mDis;

    private int pageSize=20;

    private int pageNo=1;

    private BusinessBean businessBean;
    private String id;
    private String foodId;
    private String cookerId;
    private String num;
    private String perCost;
    private CustomDatePicker customDatePicker2;

    private String now;

    private CommonDialog commonDialog;

    private String mTime;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_menu);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle("菜单");
        doReturn();

        businessBean = (BusinessBean)getIntent().getSerializableExtra(Constants.EXTRA_BUSINESS_DETAIL);
        id = getIntent().getStringExtra(Constants.EXTRA_MENU_ID);
        foodId = getIntent().getStringExtra(Constants.EXTRA_FOODS_ID);
        cookerId = getIntent().getStringExtra(Constants.EXTRA_COOKER_ID);
        num = getIntent().getStringExtra(Constants.EXTRA_NUM);
        perCost = getIntent().getStringExtra(Constants.EXTRA_PER_COST);


        adapter = new FoodMenuAdapter(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this,2));

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        getFoods();
    }

    @Override
    protected void initListener() {
        super.initListener();
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!"login".equals(sharedPrefsUtil.getString(Constants.LOGIN_SUCCESS,""))){
                    Intent intent = new Intent(FoodMenuActivity.this, LoginActivity.class);
                    startActivity(intent);
                }else {
                  // bookingRestaurant();
                    initDatePicker();
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
                bookingRestaurant();
            }
        }, "2018-05-29 00:00", "2028-05-29 00:00"); // 初始化日期格式请用：yyyy-MM-dd HH:mm，否则不能正常运行
        customDatePicker2.showSpecificTime(true); // 显示时和分
        customDatePicker2.setIsLoop(true); // 允许循环滚动

        customDatePicker2.show(now);
    }

    /**
     * 获取菜单
     * */
    private void getFoods(){

        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }

        if(null==businessBean){
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pageSize", pageSize);
        jsonObject.addProperty("pageNo", pageNo);
       // jsonObject.addProperty("plate", 2);
        jsonObject.addProperty("cuisines", foodId);
        jsonObject.addProperty("traderId", businessBean.getTraderDetail().getId());
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
                .getGoods(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result.toString());

                            Gson gson = new Gson();
                            Type type = new TypeToken<VegetableResponse>() {}.getType();
                            VegetableResponse response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    LogUtils.d(TAG,"111="+response.getData().toString());
                                    if(response.getData()!=null)
                                    adapter.setVegetableBeans(response.getData().getGoodsVos());
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
     * 餐饮预定
     * */
    private void bookingRestaurant(){

        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }

        if(null==businessBean){
            return;
        }

        StringBuffer stringBuffer = new StringBuffer();
        if(adapter.getVegetableBeans()==null){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"请选择菜品～");
            return;
        }else {
            for(int i=0;i<adapter.getVegetableBeans().size();i++){
                if(adapter.getVegetableBeans().get(i).isSelected()){
                    stringBuffer.append(adapter.getVegetableBeans().get(i).getId()+",");
                }
            }
        }
        if(stringBuffer.length()<1){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"请选择菜品～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("traderId", businessBean.getTraderDetail().getId());
        jsonObject.addProperty("cookerId", cookerId);
        jsonObject.addProperty("foods", foodId);
        jsonObject.addProperty("goods", stringBuffer.toString().substring(0,stringBuffer.length()-1));
        jsonObject.addProperty("avgCost", perCost.substring(0,perCost.length()));
        jsonObject.addProperty("num", num);
        jsonObject.addProperty("bookTime", mTime+":59");
        jsonObject.addProperty("bookRemark", "");

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


        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject1.toString());


        ApiServiceFactory.getStringApiService()
                .bookingRestaurant(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result.toString());

                            Gson gson = new Gson();
                            Type type = new TypeToken<Response<String>>() {}.getType();
                            Response<String> response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    ToastUtils.shortToast(HealthcaseApplication.getApplication(),"预定成功");
                                    BookingBean bookingBean = new BookingBean();
                                    bookingBean.setBookNo(response.getData());

                                    Intent intent = new Intent(FoodMenuActivity.this, ReserveDetailActivity.class);
                                    intent.putExtra("reserveBean",bookingBean);
                                    startActivity(intent);
                                }else {
                                    ToastUtils.shortToast(HealthcaseApplication.getApplication(),response.getMsg());
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

    private void destroyDis(){
        if (null!=mDis&&!mDis.isDisposed()){
            mDis.dispose();
        }
    }
}
