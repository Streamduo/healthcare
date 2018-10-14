package com.sxy.healthcare.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.common.utils.Util;
import com.sxy.healthcare.home.adapter.BusinessAdapter;
import com.sxy.healthcare.home.adapter.CookerAdapter;
import com.sxy.healthcare.home.adapter.FoodAdapter;
import com.sxy.healthcare.home.bean.FoodInfo;
import com.sxy.healthcare.home.bean.GoodsCuisinesInfo;
import com.sxy.healthcare.home.bean.TraderBean;
import com.sxy.healthcare.home.bean.TraderResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class FoodActivity extends BaseActivity {

    private static final String TAG = FoodActivity.class.getSimpleName();

    @BindView(R.id.rc_food_type)
    RecyclerView recyclerView;

    private FoodAdapter foodAdapter;

    private TraderBean traderBean;

    private Disposable detailDis;

    private Disposable goodsDis;

    @BindView(R.id.rc_cookers)
    RecyclerView rcCookers;

    @BindView(R.id.tv_subscribe)
    TextView subscribe;

    @BindView(R.id.et_per_cost)
    TextView perCost;

    @BindView(R.id.et_num)
    EditText num;

    private CookerAdapter cookerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.hideSoftKeyboard(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(R.string.category_food);
        doReturn();

        foodAdapter = new FoodAdapter(this);

        recyclerView.setLayoutManager(new GridLayoutManager(this,3));


        recyclerView.setAdapter(foodAdapter);

        cookerAdapter = new CookerAdapter(this);
        rcCookers.setLayoutManager(new GridLayoutManager(this,2));
        rcCookers.setAdapter(cookerAdapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        traderBean = (TraderBean) getIntent().getSerializableExtra(Constants.EXTRA_TRADER_BEAN);
        LogUtils.d(TAG,"traderBean="+traderBean.toString());
        getTraderDetail();
        getGoodsCuisines();
    }

    @Override
    protected void initListener() {
        super.initListener();
        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(StringUtils.isEmpty(perCost.getText().toString())){
                    ToastUtils.shortToast(getApplicationContext(),"请输入人均消费！");
                    return;
                }
                if(StringUtils.isEmpty(num.getText().toString())){
                    ToastUtils.shortToast(getApplicationContext(),"请输入人数！");
                    return;
                }
                Intent intent = new Intent(FoodActivity.this,FoodMenuActivity.class);
                startActivity(intent);
            }
        });
    }

    private void getTraderDetail(){

        if(null==traderBean){
            return;
        }

        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }
        destroyDetailDis();

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("traderId", traderBean.getTraderId());

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
                .getTraderDetail(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        detailDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result.toString());

                            Gson gson = new Gson();
                            Type type = new TypeToken<FoodInfo>() {}.getType();
                            FoodInfo response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    LogUtils.d(TAG,"getTraderDetail="+response.getData().toString());
                                    cookerAdapter.setCookerBeans(response.getData().getCookerVos());
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

    private void destroyDetailDis(){
        if (null!=detailDis&&!detailDis.isDisposed()){
            detailDis.dispose();
        }
    }

    /**
     * 获取菜系
     * */
    private void getGoodsCuisines(){


        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }
        destroyGoodsDis();

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("token",sharedPrefsUtil.getString(Constants.USER_TOKEN,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject1.toString());


        ApiServiceFactory.getStringApiService()
                .getGoodsCuisines(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        detailDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result.toString());

                            Gson gson = new Gson();
                            Type type = new TypeToken<GoodsCuisinesInfo>() {}.getType();
                            GoodsCuisinesInfo response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    LogUtils.d(TAG,"111="+response.getData().toString());
                                    foodAdapter.setList(response.getData());
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

    private void destroyGoodsDis(){
        if (null!=goodsDis&&!goodsDis.isDisposed()){
            goodsDis.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDetailDis();
        destroyGoodsDis();
    }
}
