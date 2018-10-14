package com.sxy.healthcare.me.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sxy.healthcare.BuildConfig;
import com.sxy.healthcare.MainActivity;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.SharedPrefsUtil;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.common.utils.Util;
import com.sxy.healthcare.common.utils.ValidatorUtils;
import com.sxy.healthcare.me.bean.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = LoginActivity.class.getSimpleName();

    @BindView(R.id.btn_register)
    TextView btnRegister;

    @BindView(R.id.et_mobile)
    EditText editTextMobile;

    @BindView(R.id.et_pwd)
    EditText editTextPwd;

    @BindView(R.id.et_vc)
    EditText editTextVc;

    @BindView(R.id.btn_submit)
    TextView btnSubmit;

    @BindView(R.id.tv_find_pwd)
    TextView findPwd;

    @BindView(R.id.iv_vc)
    ImageView imageView;

    private Disposable preVcCodeDis;

    private Disposable loginDis;

    private SharedPrefsUtil sharedPrefsUtil;

    private String preVc;

    private long firstPressedTime;

    private String vcURL;

    private Disposable disposable;

    private String entryType;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle("登录");
        doReturn();
    }

    @Override
    protected void initDatas() {
        super.initDatas();

        sharedPrefsUtil = SharedPrefsUtil.getInstance(getApplicationContext());
        entryType = getIntent().getStringExtra(Constants.EXTRA_ENTER_TYPE);
        getPreVerify();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(StringUtils.isEmpty(sharedPrefsUtil.getString(Constants.USER_TOKEN,""))){
            getToken();
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        btnRegister.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
       // findPwd.setOnClickListener(this);
        findViewById(R.id.ll_find_pwd).setOnClickListener(this);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RequestOptions options = new RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.NONE);

                Glide.with(LoginActivity.this)
                        .load(vcURL)
                        .apply(options)
                        .into(imageView);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btn_register:
                intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                Util.hideSoftKeyboard(this);
                break;
            case R.id.btn_submit:
                /*intent = new Intent(LoginActivity.this,MainActivity.class);
                startActivity(intent);*/
                doLogin();
                break;
            case R.id.ll_find_pwd:
                intent = new Intent(LoginActivity.this,FindPwdActivity.class);
                startActivity(intent);
                Util.hideSoftKeyboard(this);
                break;
        }
    }


    /** 获取验证码 */
    private void getPreVerify() {

        destroyPreVcCode();

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("token",sharedPrefsUtil.getString(Constants.USER_TOKEN,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject1.toString());

        ApiServiceFactory.getStringApiService()
                .getPreValidateCode(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        preVcCodeDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        LogUtils.d(TAG,"result="+stringResponse);

                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result,Response.class);

                            if(response.isSuccess()){
                               // ToastUtils.shortToast(getApplicationContext(),"获取成功～");
                                String url = BuildConfig.BASE_URL+"api/common/getValidateCode?token"
                                        +sharedPrefsUtil.getString(Constants.USER_TOKEN,"")
                                        +"&vc="+response.getData();

                                vcURL = url;

                                preVc = response.getData();

                                RequestOptions options = new RequestOptions()
                                       .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE);

                                Glide.with(LoginActivity.this)
                                        .load(url)
                                        .apply(options)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                                        Target<Drawable> target, boolean isFirstResource) {
                                                LogUtils.d(TAG,"[onLoadFailed]:"+e.getMessage());
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model,
                                                                           Target<Drawable> target, DataSource dataSource,
                                                                           boolean isFirstResource) {
                                                LogUtils.d(TAG,"[onResourceReady]");
                                                return false;
                                            }
                                        })
                                        .into(imageView);
                            }else {
                                ToastUtils.shortToast(getApplicationContext(),response.getMsg());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(getApplicationContext(),"获取验证码失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

         /*.flatMap(new Function<String, Observable<String>>() {
            @Override
            public Observable<String> apply(String s) throws Exception {
                String vc = ThreeDesUtils.decryptThreeDESECB(s,
                        sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));
                return ApiServiceFactory.getStringApiService()
                        .getValidateCode(sharedPrefsUtil.getString(Constants.USER_TOKEN,""),vc);
            }
        })*/
    }


    /**
     * 登录
     * */
    private void doLogin(){
        String phone = editTextMobile.getText().toString();
        String pwd = editTextPwd.getText().toString();
        String vcCode = editTextVc.getText().toString();


        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        if(StringUtils.isEmpty(phone)){
            ToastUtils.shortToast(getApplicationContext(),R.string.phone_number_empty);
            return;
        }

        if(!ValidatorUtils.isMobile(phone)){
            ToastUtils.shortToast(getApplicationContext(),"请输入正确的手机号码");
            return;
        }

        if(StringUtils.isEmpty(pwd)){
            ToastUtils.shortToast(getApplicationContext(),"密码不能为空～");
            return;
        }


        if(!ValidatorUtils.isPassword(pwd)){
            ToastUtils.shortToast(getApplicationContext(),"密码需由6到16个字符，数字或者下划线组成～");
            return;
        }

       /* if(StringUtils.isEmpty(vcCode)){
            ToastUtils.shortToast(getApplicationContext(),"请输入验证码～");
            return;
        }*/

        final JsonObject  jsonObject = new JsonObject();
        jsonObject.addProperty("mobile",phone);
        jsonObject.addProperty("password", pwd);
       // jsonObject.addProperty("vc", preVc);
      //  jsonObject.addProperty("captcha",vcCode);



        destroyLoginDis();

        LogUtils.d(TAG,"param="+jsonObject.toString());
        String param = null;
        try {
            param = ThreeDesUtils.encryptThreeDESECB(jsonObject.toString(),
                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JsonObject jsonObject1 = new JsonObject();
        jsonObject1.addProperty("token",sharedPrefsUtil.getString(Constants.USER_TOKEN,""));
        jsonObject1.addProperty("param",param);

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),jsonObject1.toString());

        ApiServiceFactory.getStringApiService()
                .doLoginBak(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        loginDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            Gson gson = new Gson();

                            Type type = new TypeToken<Response<UserInfo>>(){}.getType();

                            Response<UserInfo>  response = gson.fromJson(result,type);

                            LogUtils.d(TAG,"result="+response.toString());

                            if(response.isSuccess()){
                                if(response.getData()!=null){
                                    sharedPrefsUtil.setString(Constants.USER_INFO,response.getData().getUserId());
                                    sharedPrefsUtil.setString(Constants.USER_INFO_BALANCE,response.getData().getBalance()+"");
                                }
                                sharedPrefsUtil.setString(Constants.LOGIN_SUCCESS,"login");
                               // Intent  intent = new Intent(LoginActivity.this, MainActivity.class);
                            //    startActivity(intent);
                                ToastUtils.shortToast(getApplicationContext(),"登录成功～");
                                setResult(RESULT_OK);
                                Util.hideSoftKeyboard(LoginActivity.this);
                                finish();
                            }else {
                                ToastUtils.shortToast(getApplicationContext(),response.getMsg());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(getApplicationContext(),"登录失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

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
                        LogUtils.d(TAG,"result="+stringResponse.toString());

                        //sharedPrefsUtil.setString(Constants.USER_TOKEN,stringResponse.getToken());
                        //sharedPrefsUtil.setString(Constants.USER_SECRET_KEY,stringResponse.getSecretKey());
                        getPreVerify();
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void destroyDis(){
        if(null!=disposable&&!disposable.isDisposed()){
            disposable.dispose();
        }
    }


    private void destroyPreVcCode(){
        if (null!=preVcCodeDis&&!preVcCodeDis.isDisposed()){
            preVcCodeDis.dispose();
        }
    }

    private void destroyLoginDis(){
        if (null!=loginDis&&!loginDis.isDisposed()){
            loginDis.dispose();
        }
    }
/*
    @Override
    public void onBackPressed() {
            if (System.currentTimeMillis() - firstPressedTime < 2000) {
                super.onBackPressed();
            } else {
                ToastUtils.shortToast(getApplicationContext(),"再按一次退出！");
                firstPressedTime = System.currentTimeMillis();
            }
    }*/

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyLoginDis();
        destroyPreVcCode();
        destroyDis();
    }
}
