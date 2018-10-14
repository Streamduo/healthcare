package com.sxy.healthcare.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.home.adapter.SearchFunAdapter;
import com.sxy.healthcare.home.adapter.SearchHealthAdapter;
import com.sxy.healthcare.home.bean.BusinessBean;
import com.sxy.healthcare.home.bean.EntertainmentVosBean;
import com.sxy.healthcare.home.bean.VegetableResponse;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class FunCategoryActivity  extends BaseActivity{

    private static final String TAG = FunCategoryActivity.class.getSimpleName();

    private BusinessBean businessBean;

    private String id;

    private EntertainmentVosBean entertainmentVosBean;

    @BindView(R.id.rc_fun)
    EasyRecyclerView recyclerView;

    private SearchFunAdapter searchFunAdapter;

    private Disposable mDis;

    private int pageSize=20;

    private int pageNo=1;


    @Override
    public void onCreate(Bundle savedInstanceState) {

        businessBean = (BusinessBean)getIntent().getSerializableExtra(Constants.EXTRA_BUSINESS_DETAIL);
        id = getIntent().getStringExtra(Constants.EXTRA_MENU_ID);
        entertainmentVosBean = (EntertainmentVosBean)getIntent().getSerializableExtra(Constants.EXTRA_FUN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fun_category);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(entertainmentVosBean.getName());
        doReturn();

        searchFunAdapter = new SearchFunAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(searchFunAdapter);

        searchFunAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(FunCategoryActivity.this, HealthDetailActivity.class);
                intent.putExtra(Constants.EXTRA_HEALTH,searchFunAdapter.getItem(position));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        getFoods();
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
        // jsonObject.addProperty("plate", 4);
        jsonObject.addProperty("category", entertainmentVosBean.getId());
        jsonObject.addProperty("traderId", businessBean.getTraderDetail().getId());
       // jsonObject.addProperty("tabType", id);

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
                                    if(response.getData()!=null){
                                        searchFunAdapter.addAll(response.getData().getGoodsVos());
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

    private void destroyDis(){
        if (null!=mDis&&!mDis.isDisposed()){
            mDis.dispose();
        }
    }
}
