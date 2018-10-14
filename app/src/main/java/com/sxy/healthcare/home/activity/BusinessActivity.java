package com.sxy.healthcare.home.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ScreenUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.home.adapter.BusinessAdapter;
import com.sxy.healthcare.home.adapter.SearchHealthAdapter;
import com.sxy.healthcare.home.bean.TraderBean;
import com.sxy.healthcare.home.bean.TraderInfo;
import com.sxy.healthcare.home.bean.TraderResponse;
import com.wuxiaolong.pullloadmorerecyclerview.PullLoadMoreRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class BusinessActivity extends BaseActivity implements View.OnClickListener,
        RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener,
        RecyclerArrayAdapter.OnNoMoreListener{

    private static final String TAG = BusinessActivity.class.getSimpleName();

    @BindView(R.id.rc_category)
    EasyRecyclerView recyclerView;

    private BusinessAdapter businessAdapter;

    private List<TraderBean> datas = new ArrayList<>();

    private Disposable tradersDis;

    private int pageSize=10;

    private int pageNo=1;

    private int dataCount = 0;

    private String id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_business);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(R.string.business);
        doReturn();

        id = getIntent().getStringExtra(Constants.EXTRA_MENU_ID);

        businessAdapter = new BusinessAdapter(this,id);

        recyclerView.setAdapter(businessAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false));

        DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, (int) ScreenUtils.dip2px( 0.5f), 0, 0);
        itemDecoration.setDrawLastItem(false);
        recyclerView.addItemDecoration(itemDecoration);

        businessAdapter.setMore(R.layout.view_more, this);
     //   businessAdapter.setNoMore(R.layout.view_no_more, this);
        recyclerView.setRefreshListener(this);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        getTraders();
    }

    @Override
    protected void initListener() {
        super.initListener();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
           default:
                break;
        }

    }


    private void getTraders(){

        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }
        destroyTradersDis();

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pageSize",pageSize);
        jsonObject.addProperty("pageNo", pageNo);
        jsonObject.addProperty("type", id);

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
                .getTraders(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        tradersDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"pageNo="+pageNo+",result="+result.toString());

                            Gson gson = new Gson();
                            Type type = new TypeToken<TraderResponse>() {}.getType();
                            TraderResponse response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    dataCount = response.getData().getCount();
                                        LogUtils.d(TAG,"111="+response.getData().getTraders().toString());
                                        if(pageNo==1){
                                            datas.clear();
                                            businessAdapter.clear();
                                        }
                                        datas.addAll(response.getData().getTraders());
                                        businessAdapter.addAll(datas);
                                        pageNo=pageNo+1;
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

    private void destroyTradersDis(){
        if (null!=tradersDis&&!tradersDis.isDisposed()){
            tradersDis.dispose();
        }
    }

    @Override
    public void onRefresh() {
        pageNo=1;
        getTraders();
    }

    @Override
    public void onLoadMore() {
        if(dataCount>businessAdapter.getCount()){
         //   getTraders();
        }else {
            businessAdapter.addAll(new ArrayList<TraderBean>());

        }
    }


    @Override
    public void onNoMoreShow() {

    }

    @Override
    public void onNoMoreClick() {

    }
}
