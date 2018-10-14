package com.sxy.healthcare.home.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
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
import com.sxy.healthcare.home.adapter.SearchFunAdapter;
import com.sxy.healthcare.home.adapter.SearchGoodsAdapter;
import com.sxy.healthcare.home.adapter.SearchHealthAdapter;
import com.sxy.healthcare.home.bean.TraderBean;
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

public class SearchActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = SearchActivity.class.getSimpleName();

    @BindView(R.id.rc_search)
    RecyclerView rcSearch;

    @BindView(R.id.tv_btn_goods)
    TextView btnGoods;

    @BindView(R.id.tv_btn_health)
    TextView btnHealth;

    @BindView(R.id.tv_btn_fun)
    TextView btnFun;


    @BindView(R.id.et_search)
    EditText search;

    private SearchGoodsAdapter searchGoodsAdapter;
    private SearchHealthAdapter searchHealthAdapter;
    private SearchFunAdapter searchFunAdapter;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }


    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(R.string.search_tv);
        doReturn();

        btnGoods.setSelected(true);
        btnHealth.setSelected(false);
        btnFun.setSelected(false);

        searchGoodsAdapter = new SearchGoodsAdapter(this);
        searchHealthAdapter = new SearchHealthAdapter(this);
        searchFunAdapter = new SearchFunAdapter(this);

        rcSearch.setLayoutManager(new GridLayoutManager(this,2));
        rcSearch.setAdapter(searchGoodsAdapter);

        searchGoodsAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(SearchActivity.this, GoodsDetailActivity.class);
                intent.putExtra(Constants.EXTRA_HEALTH,searchGoodsAdapter.getItem(position));
                startActivity(intent);
            }
        });

        searchFunAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(SearchActivity.this, HealthDetailActivity.class);
                intent.putExtra(Constants.EXTRA_HEALTH,searchFunAdapter.getItem(position));
                startActivity(intent);
            }
        });

        searchHealthAdapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(SearchActivity.this, HealthDetailActivity.class);
                intent.putExtra(Constants.EXTRA_HEALTH,searchHealthAdapter.getItem(position));
                startActivity(intent);
            }
        });

    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    @Override
    protected void initListener() {
        super.initListener();
        btnGoods.setOnClickListener(this);
        btnHealth.setOnClickListener(this);
        btnFun.setOnClickListener(this);

        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH){
                    doSearch();
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_btn_goods:
                btnGoods.setSelected(true);
                btnHealth.setSelected(false);
                btnFun.setSelected(false);
                rcSearch.setLayoutManager(new GridLayoutManager(this,2));
                rcSearch.setAdapter(searchGoodsAdapter);
                break;
            case R.id.tv_btn_health:
                btnGoods.setSelected(false);
                btnHealth.setSelected(true);
                btnFun.setSelected(false);
                rcSearch.setLayoutManager(new LinearLayoutManager(this));
                rcSearch.setAdapter(searchHealthAdapter);
                break;
            case R.id.tv_btn_fun:
                btnGoods.setSelected(false);
                btnHealth.setSelected(false);
                btnFun.setSelected(true);
                rcSearch.setLayoutManager(new LinearLayoutManager(this));
                rcSearch.setAdapter(searchFunAdapter);
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Util.hideSoftKeyboard(this);
    }

    /**
     * 搜索
     * */
    private void  doSearch(){

        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }

        if(StringUtils.isEmpty(search.getText().toString())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"请输入搜索内容");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("search", search.getText().toString());


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
                                        searchGoodsAdapter.clear();
                                        searchFunAdapter.clear();
                                        searchHealthAdapter.clear();
                                        for(int i=0;i<response.getData().getGoodsVos().size();i++){
                                            if(response.getData().getGoodsVos().get(i).getCategory().equals("6")){
                                                searchGoodsAdapter.addAll(response.getData().getGoodsVos());
                                            }else if(response.getData().getGoodsVos().get(i).getCategory().equals("1")){
                                                searchHealthAdapter.addAll(response.getData().getGoodsVos());
                                            }else {
                                                searchFunAdapter.addAll(response.getData().getGoodsVos());
                                            }
                                        }

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
                        ToastUtils.shortToast(HealthcaseApplication.getApplication(),"失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



}
