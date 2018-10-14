package com.sxy.healthcare.me.activity;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.adapter.OrderAdapter;
import com.sxy.healthcare.me.adapter.ReserveAdapter;
import com.sxy.healthcare.me.bean.BookingResponse;
import com.sxy.healthcare.me.event.CancelEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class ProfileReserveActivity extends BaseActivity implements View.OnClickListener,
    SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = ProfileReserveActivity.class.getSimpleName();

    @BindView(R.id.rc_reserve)
    RecyclerView rcReserve;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    ReserveAdapter reserveAdapter;

    private Disposable orderDis;

    private int pageSize=20;

    private int pageNo=1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_reserve);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(R.string.profile_reserve);
        doReturn();

        reserveAdapter = new ReserveAdapter(this,getSupportFragmentManager());
        rcReserve.setLayoutManager(new LinearLayoutManager(this));
        rcReserve.setAdapter(reserveAdapter);

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        getReservations();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }

    }

    /**
     * 我的预定
     * */
    private void getReservations(){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("pageSize",pageSize);
        jsonObject.addProperty("pageNo", pageNo);

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
                .getReservations(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        orderDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {

                        if(swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }

                        LogUtils.d(TAG,"stringResponse="+stringResponse.toString());
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result);

                            Gson gson = new Gson();
                            BookingResponse response = gson.fromJson(result,BookingResponse.class);


                            if(response.isSuccess()){
                                if(response.getData()!=null)
                                reserveAdapter.setBookingBeans(response.getData().getBookingMainVos());
                            }else {
                                ToastUtils.shortToast(getApplicationContext(),"获取数据失败～");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        if(swipeRefreshLayout.isRefreshing()){
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        ToastUtils.shortToast(getApplicationContext(),"获取失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(CancelEvent cancelEvent) {
        pageNo = 1;
      getReservations();
    }



    private void destroyDis(){
        if (null!=orderDis&&!orderDis.isDisposed()){
            orderDis.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyDis();

        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onRefresh() {
        getReservations();
    }
}
