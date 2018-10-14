package com.sxy.healthcare.me.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
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
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.adapter.DealAdapter;
import com.sxy.healthcare.me.adapter.OrderAdapter;
import com.sxy.healthcare.me.bean.DealInfo;
import com.sxy.healthcare.me.bean.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;


public class DealHistoryActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = DealHistoryActivity.class.getSimpleName();


    @BindView(R.id.rc_deal)
    RecyclerView rcDeal;

    DealAdapter dealAdapter;


    private Disposable disposable;


    @BindView(R.id.tv_user_name)
    TextView mobile;

    @BindView(R.id.tv_card_no)
    TextView cardNo;

    @BindView(R.id.iv_user_avatar)
    ImageView avatar;


    private UserInfo userInfo;

    private int pageSize=20;

    private int pageNo=1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deal_history);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(R.string.profile_deal_history);
        doReturn();

        dealAdapter = new DealAdapter(this);
        rcDeal.setLayoutManager(new LinearLayoutManager(this));
        rcDeal.setAdapter(dealAdapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.EXTRA_USER_INFO);
        if(null!=userInfo){
            mobile.setText(userInfo.getNickName());
            cardNo.setText("会员号："+userInfo.getCardNo());
            Glide.with(DealHistoryActivity.this)
                    .load(userInfo.getHeadImg()).apply(GlideUtils.getOptionsAvatar()).into(avatar);
        }

        getDealHistory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }

    }

    /**
     * 我的交易记录
     * */
    private void getDealHistory(){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        destroyDis();

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
                .getChanges(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result);
                            Gson gson = new Gson();
                            DealInfo response = gson.fromJson(result,DealInfo.class);

                            if(response.isSuccess()){
                                if(response.getData()!=null){
                                    dealAdapter.setOrderBeans(response.getData().getChangesVos());
                                }
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
                        ToastUtils.shortToast(getApplicationContext(),"获取失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



    private void destroyDis(){
        if (null!=disposable&&!disposable.isDisposed()){
            disposable.dispose();
        }
    }

}
