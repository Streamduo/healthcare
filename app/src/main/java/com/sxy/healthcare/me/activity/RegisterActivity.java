package com.sxy.healthcare.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sxy.healthcare.MainActivity;
import com.sxy.healthcare.R;
import com.sxy.healthcare.SplashActivity;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.EncryptUtils;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.SharedPrefsUtil;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.common.utils.Util;
import com.sxy.healthcare.common.utils.ValidatorUtils;
import com.sxy.healthcare.common.view.CodeCountDownTimer;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG= RegisterActivity.class.getSimpleName();

    @BindView(R.id.btn_obtain_vc)
    TextView btnObtainVc;

    @BindView(R.id.btn_submit)
    TextView btnSubmit;

    @BindView(R.id.et_vc)
    EditText etVc;

    @BindView(R.id.et_phone_number)
    EditText etPhone;

    @BindView(R.id.et_pwd)
    EditText etPwd;

    @BindView(R.id.et_confirm_pwd)
    EditText etConfirmPwd;



    private Disposable smsDis;

    private Disposable registerDis;

    private SharedPrefsUtil sharedPrefsUtil;

    //倒计时器对象
    private CodeCountDownTimer mCodeCountDownTimer = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
    }

    @Override
    protected void initViews() {
        super.initViews();
        //倒计时
        mCodeCountDownTimer = new CodeCountDownTimer(60 * 1000L,
                1000L, btnObtainVc, 0,
                0);

        sharedPrefsUtil = SharedPrefsUtil.getInstance(getApplicationContext());

        setCurrentTitle("注册");
        doReturn();

    }

    @Override
    protected void initListener() {
        super.initListener();
        btnObtainVc.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.btn_submit:
                /*intent = new Intent(RegisterActivity.this,MainActivity.class);
                startActivity(intent);*/
                doRegister();
                break;
            case R.id.btn_obtain_vc:
                clickVerify();
                break;
        }
    }


    /** 获取验证码 */
    private void clickVerify() {
      //  btnObtainVc.setClickable(false);
        String phone = etPhone.getText().toString();


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

        mCodeCountDownTimer.start();

        final JsonObject  jsonObject = new JsonObject();
        jsonObject.addProperty("type", Constants.SMS_TYPE_REGISTER);
        jsonObject.addProperty("phone",phone);



        destroySms();

        String param = null;
        try {
            param = ThreeDesUtils.encryptThreeDESECB(jsonObject.toString(),
                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        LogUtils.d(TAG,"json="+jsonObject.toString()+",param="+param);

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
                .getSmsValidateCode(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        smsDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result,Response.class);

                            if(response.isSuccess()){
                                ToastUtils.shortToast(getApplicationContext(),"获取成功～");
                            }else {
                                ToastUtils.shortToast(getApplicationContext(),response.getMsg());
                            }

                            LogUtils.d(TAG,"result="+response.toString());
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
    }



    /**
     * 注册
     * */
    private void doRegister(){
        String phone = etPhone.getText().toString();
        String pwd = etPwd.getText().toString();
        String confirmPwd = etConfirmPwd.getText().toString();
        String vcCode = etVc.getText().toString();


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

        if(StringUtils.isEmpty(confirmPwd)){
            ToastUtils.shortToast(getApplicationContext(),"请确认密码～");
            return;
        }

        if(!StringUtils.isEmpty(pwd)&&!StringUtils.isEmpty(confirmPwd)&&!pwd.equals(confirmPwd)){
            ToastUtils.shortToast(getApplicationContext(),"密码不一致～");
            return;
        }

        if(!ValidatorUtils.isPassword(pwd)){
            ToastUtils.shortToast(getApplicationContext(),"密码需由6到16个字符，数字或者下划线组成～");
            return;
        }

        if(StringUtils.isEmpty(vcCode)){
            ToastUtils.shortToast(getApplicationContext(),"请输入验证码～");
            return;
        }

        final JsonObject  jsonObject = new JsonObject();
        jsonObject.addProperty("mobile",phone);
        jsonObject.addProperty("password", pwd);
        jsonObject.addProperty("smscode", vcCode);



        destroyRegister();

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
                .doRegister(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        registerDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result,Response.class);

                            if(response.isSuccess()){
                              Intent  intent = new Intent(RegisterActivity.this, LoginActivity.class);
                              startActivity(intent);
                                ToastUtils.shortToast(getApplicationContext(),"注册成功～");
                                finish();
                            }else {
                                ToastUtils.shortToast(getApplicationContext(),response.getMsg());
                            }
                            LogUtils.d(TAG,"result="+response.toString());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(getApplicationContext(),"注册失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



    private void destroySms(){
        if (null!=smsDis&&!smsDis.isDisposed()){
            smsDis.dispose();
        }
    }

    private void destroyRegister(){
        if (null!=registerDis&&!registerDis.isDisposed()){
            registerDis.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroySms();
        destroyRegister();
        Util.hideSoftKeyboard(this);
    }
}
