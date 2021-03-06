package com.sxy.healthcare.home.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseFragment;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ScreenUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.home.activity.GoodsDetailActivity;
import com.sxy.healthcare.home.activity.HealthDetailActivity;
import com.sxy.healthcare.home.adapter.SearchGoodsAdapter;
import com.sxy.healthcare.home.adapter.SearchHealthAdapter;
import com.sxy.healthcare.home.bean.BusinessBean;
import com.sxy.healthcare.home.bean.VegetableBean;
import com.sxy.healthcare.home.bean.VegetableResponse;

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

public class GoodFragment extends BaseFragment  implements View.OnClickListener,
        RecyclerArrayAdapter.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener,
        RecyclerArrayAdapter.OnNoMoreListener{

    private static final String TAG = GoodFragment.class.getSimpleName();

    @BindView(R.id.rc_health)
    EasyRecyclerView recyclerView;

    private SearchGoodsAdapter adapter;

    private Disposable tradersDis;

    private int pageSize=20;

    private int pageNo=1;

    private List<VegetableBean> datas = new ArrayList<>();

    private BusinessBean businessBean;
    private String id;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        businessBean = (BusinessBean)getArguments().getSerializable(Constants.EXTRA_BUSINESS_DETAIL);
        id = getArguments().getString(Constants.EXTRA_MENU_ID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_health);
    }


    @Override
    protected void initViews() {
        super.initViews();

        adapter = new SearchGoodsAdapter(getContext());
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));

       /* DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, (int) ScreenUtils.dip2px( 0.5f), 0, 0);
        itemDecoration.setDrawLastItem(false);
        recyclerView.addItemDecoration(itemDecoration);*/

        adapter.setMore(R.layout.view_more, this);
        adapter.setNoMore(R.layout.view_no_more, this);


        recyclerView.setAdapter(adapter);
        recyclerView.setRefreshListener(this);
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        getFoods();
    }

    @Override
    protected void initListener() {
        super.initListener();
        adapter.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), GoodsDetailActivity.class);
                intent.putExtra(Constants.EXTRA_HEALTH,adapter.getItem(position));
                getContext().startActivity(intent);
            }
        });
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
        jsonObject.addProperty("plate", 4);
        // jsonObject.addProperty("cuisines", foodId);
        jsonObject.addProperty("traderId", businessBean.getTraderDetail().getId());
        //  jsonObject.addProperty("tabType", id);

        LogUtils.d(TAG,"[getFoods] JsonObject="+jsonObject.toString());
        String param = null;
        try {
            param = ThreeDesUtils.encryptThreeDESECB(jsonObject.toString(),
                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        destroyTradersDis();

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
                        tradersDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"[getFoods] result="+result.toString());

                            Gson gson = new Gson();
                            Type type = new TypeToken<VegetableResponse>() {}.getType();
                            VegetableResponse response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    LogUtils.d(TAG,"[getFoods] 111="+response.getData().toString());
                                    if(response.getData()!=null){
                                        if(pageNo==1){
                                            datas.clear();
                                            adapter.clear();
                                        }
                                        datas.addAll(response.getData().getGoodsVos());
                                        adapter.addAll(datas);
                                        pageNo=pageNo+1;
                                    }
                                    adapter.pauseMore();
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
        getFoods();
    }

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onNoMoreShow() {

    }

    @Override
    public void onNoMoreClick() {

    }

}
