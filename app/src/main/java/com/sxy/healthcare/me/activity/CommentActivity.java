package com.sxy.healthcare.me.activity;

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
import com.sxy.healthcare.me.bean.OrderBean;
import com.sxy.healthcare.me.bean.OrderDetailResponse;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class CommentActivity extends BaseActivity {

    private static final String TAG = CommentActivity.class.getSimpleName();

    @BindView(R.id.et_comment)
    EditText editText;

    @BindView(R.id.btn_submit)
    TextView submit;

    private Disposable disposable;

    private OrderBean orderBean;

    private String tradeID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle("评价");
        doReturn();
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        orderBean = (OrderBean) getIntent().getSerializableExtra(Constants.EXTRA_ORDER);
        tradeID = getIntent().getStringExtra("tradeId");
    }


    @Override
    protected void initListener() {
        super.initListener();
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComment();
            }
        });
    }

    /**
     * 添加评价
     * */
    private void addComment(){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        if(StringUtils.isEmpty(editText.getText().toString())){
            ToastUtils.shortToast(getApplicationContext(),"请输入评论内容");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("orderId",orderBean.getOrderId());
        jsonObject.addProperty("traderId",tradeID);
        jsonObject.addProperty("commentText",editText.getText().toString());

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
                .getOrdersDetail(body)
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

                            LogUtils.d(TAG,"【getOrdersDetail】="+result);

                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result,Response.class);

                            if(response.isSuccess()){
                                ToastUtils.shortToast(getApplicationContext(),"评价成功！");
                                setResult(RESULT_OK);
                                finish();
                            }else {
                                ToastUtils.shortToast(getApplicationContext(),"添加失败");
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(getApplicationContext(),"添加失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

}
