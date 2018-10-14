package com.sxy.healthcare.me.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.common.utils.ValidatorUtils;
import com.sxy.healthcare.me.bean.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class ModifyPwdActivity extends BaseActivity {

    private static final String TAG = ModifyPwdActivity.class.getSimpleName();

    @BindView(R.id.tv_submit)
    TextView btnSubmit;

    @BindView(R.id.et_old_pwd)
    EditText editTextOld;

    @BindView(R.id.et_pwd)
    EditText editTextPwd;

    @BindView(R.id.et_confirm_pwd)
    EditText editTextConfirm;


    private Disposable modifyDis;


    @BindView(R.id.tv_user_name)
    TextView mobile;

    @BindView(R.id.tv_card_no)
    TextView cardNo;

    @BindView(R.id.iv_user_avatar)
    CircleImageView avatar;

    private UserInfo userInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_pwd);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(R.string.profile_modify_pwd);
        doReturn();

    }

    @Override
    protected void initDatas() {
        super.initDatas();
        userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.EXTRA_USER_INFO);
        if(null!=userInfo){
            mobile.setText(userInfo.getMobile());
            cardNo.setText("会员号："+userInfo.getCardNo());
            Glide.with(ModifyPwdActivity.this)
                    .load(userInfo.getHeadImg()).apply(GlideUtils.getOptionsAvatar()).into(avatar);
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doModifyPwd();
            }
        });
    }

    /**
     * 修改密码
     * */
    private void doModifyPwd(){
        String oldPWD = editTextOld.getText().toString();
        String pwd = editTextPwd.getText().toString();
        String confirmPwd = editTextConfirm.getText().toString();

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        if(StringUtils.isEmpty(oldPWD)){
            ToastUtils.shortToast(getApplicationContext(),"请输入旧密码～");
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



        final JsonObject jsonObject = new JsonObject();
       // jsonObject.addProperty("userId",sharedPrefsUtil.getString(Constants.USER_INFO,""));
        jsonObject.addProperty("oldPassword", oldPWD);
        jsonObject.addProperty("password", pwd);

        destroyModify();

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
                .doChangePwd(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        modifyDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result,Response.class);

                            if(response.isSuccess()){
                                ToastUtils.shortToast(getApplicationContext(),"修改成功～");
                                editTextOld.setText("");
                                editTextConfirm.setText("");
                                editTextPwd.setText("");
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
                        ToastUtils.shortToast(getApplicationContext(),"修改失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



    private void destroyModify(){
        if (null!=modifyDis&&!modifyDis.isDisposed()){
            modifyDis.dispose();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyModify();
    }
}
