package com.sxy.healthcare.me.activity;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ScreenUtils;
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.common.utils.Util;
import com.sxy.healthcare.me.bean.OrderResponse;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class BillActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = BaseActivity.class.getSimpleName();

    private Disposable billDis;

    @BindView(R.id.tv_btn_person)
    TextView btnPerson;

    @BindView(R.id.tv_btn_company)
    TextView btnCompany;

    @BindView(R.id.ll_person)
    LinearLayout layoutPerson;

    @BindView(R.id.ll_company)
    LinearLayout layoutCompany;

    @BindView(R.id.et_person_name)
    EditText person;

    @BindView(R.id.et_company_name)
    EditText company;

    @BindView(R.id.et_company_no)
    EditText taxNo;

    @BindView(R.id.btn_submit)
    TextView commit;

    private boolean isPerson = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);
    }
    @Override
    protected void onStop() {
        super.onStop();
        Util.hideSoftKeyboard(this);
    }
    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle("发票");
        doReturn();

        btnPerson.setSelected(true);
        btnCompany.setSelected(false);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
    }

    @Override
    protected void initListener() {
        super.initListener();
        btnPerson.setOnClickListener(this);
        btnCompany.setOnClickListener(this);
        commit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_btn_person:
                btnPerson.setSelected(true);
                btnCompany.setSelected(false);
                layoutPerson.setVisibility(View.VISIBLE);
                layoutCompany.setVisibility(View.GONE);
                isPerson = true;
                break;
            case R.id.tv_btn_company:
                btnPerson.setSelected(false);
                btnCompany.setSelected(true);
                layoutPerson.setVisibility(View.GONE);
                layoutCompany.setVisibility(View.VISIBLE);
                isPerson = false;
                break;
            case R.id.btn_submit:
                addBill();
                break;
            default:
                break;
        }
    }

    /**
     * 添加发票
     * */
    private void addBill(){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        if(isPerson){
            if(StringUtils.isEmpty(person.getText().toString())){
                ToastUtils.shortToast(getApplicationContext(),"请填写个人姓名");
                return;
            }
            jsonObject.addProperty("userName",person.getText().toString());
        }else {
            if(StringUtils.isEmpty(company.getText().toString())){
                ToastUtils.shortToast(getApplicationContext(),"请填写公司名称");
                return;
            }
            if(StringUtils.isEmpty(taxNo.getText().toString())){
                ToastUtils.shortToast(getApplicationContext(),"请填写税号");
                return;
            }
            jsonObject.addProperty("companyName",company.getText().toString());
            jsonObject.addProperty("taxNo", taxNo.getText().toString());
        }

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
                .addBill(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        billDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result);

                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result,Response.class);

                            if(response.isSuccess()){
                                ToastUtils.shortToast(getApplicationContext(),"添加成功");
                                if(isPerson){
                                    person.setText("");
                                }else {
                                    company.setText("");
                                    taxNo.setText("");
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
                        ToastUtils.shortToast(getApplicationContext(),"添加失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



    private void destroyDis(){
        if (null!=billDis&&!billDis.isDisposed()){
            billDis.dispose();
        }
    }

}
