package com.sxy.healthcare;

import android.content.Intent;
import android.os.Bundle;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sxy.healthcare.base.AppConfig;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.bean.BaseInfo;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.SharedPrefsUtil;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.me.activity.LoginActivity;
import com.sxy.healthcare.me.bean.UserInfo;

import java.lang.reflect.Type;
import java.util.concurrent.TimeUnit;

import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();


    private Disposable disposable;

    private SharedPrefsUtil sharedPrefsUtil;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        try {
          /*  LogUtils.d(TAG,"111111="+ThreeDesUtils.decryptThreeDESECB("fmpYrH2qbBLKbD35uIr9t9+KsOe1evQz8wb/i8JBm8OQ+LGTZZDtZpwVTtr8wdiiKQXDdHMAuHw3ey1OUjAIjWscniW/job8FWWkAPKtn0nLXtM8/JZtF8xob4yulF4tFfngs6Bz9uhxlbKWiB0Ri5S/jVg8rDA6/uFBp7JnGAvmENg+JRM0yWe2XQxZ/Gsk4I8UmiudA8zeKSHnPej8s4P3a5vlNbOrVeSmkluiH4N96eZfR4Zj35kuxpSxH0mj3/H+/kJqDIjZdvcnM0ijnj8+N59/xcImuxuIqPb9A4yECtLHT1X1Qg==",
                    AppConfig.commonKey));*/
        } catch (Exception e) {
            e.printStackTrace();
        }

        sharedPrefsUtil = SharedPrefsUtil.getInstance(getApplicationContext());

        if(StringUtils.isEmpty(sharedPrefsUtil.getString(Constants.USER_INFO,""))){
            if(StringUtils.isEmpty(sharedPrefsUtil.getString(Constants.USER_TOKEN,""))
                    ||StringUtils.isEmpty(sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""))){
                getToken();
            }
        }


        Observable.just("")
                //延时两秒，第一个参数是数值，第二个参数是事件单位
                .delay(1000, TimeUnit.MILLISECONDS)
               /* // Run on a background thread
                .subscribeOn(Schedulers.io())
                // Be notified on the main thread
                .observeOn(AndroidSchedulers.mainThread())*/
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        goToHomeOrLogin();
                    }
                });//这里的观察者依然不重要

    }


    private void goToHomeOrLogin(){
        LogUtils.d(TAG,"is_login="+sharedPrefsUtil.getString(Constants.LOGIN_SUCCESS,""));
        Intent intent;
       // if ("login".equals(sharedPrefsUtil.getString(Constants.LOGIN_SUCCESS,""))){
            intent = new Intent(SplashActivity.this,MainActivity.class);
       /* }else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }*/
        startActivity(intent);
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);

        finish();
    }


    private void getToken(){

        destroyDis();

        ApiServiceFactory.getStringApiService()
                .getBaseInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                            disposable = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        LogUtils.d(TAG,"stringResponse="+stringResponse.toString());
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    AppConfig.commonKey);


                            Gson gson = new Gson();
                            Type type = new TypeToken<Response<BaseInfo>>(){}.getType();
                            Response<BaseInfo> response = gson.fromJson(result,type);
                            LogUtils.d(TAG,"result="+response.toString());
                            if(response.getData()!=null){
                                sharedPrefsUtil.setString(Constants.USER_TOKEN,response.getData().getToken());
                                sharedPrefsUtil.setString(Constants.USER_SECRET_KEY,response.getData().getSecretKey());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //  sharedPrefsUtil.setString(Constants.USER_TOKEN,stringResponse.getToken());
                      //  sharedPrefsUtil.setString(Constants.USER_SECRET_KEY,stringResponse.getSecretKey());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
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
        overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    }

    private void destroyDis(){
        if(null!=disposable&&!disposable.isDisposed()){
            disposable.dispose();
        }
    }
}
