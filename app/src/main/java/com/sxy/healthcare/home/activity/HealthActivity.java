package com.sxy.healthcare.home.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseActivity;
import com.sxy.healthcare.base.BaseFragment;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ScreenUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.home.adapter.BusinessAdapter;
import com.sxy.healthcare.home.adapter.SearchHealthAdapter;
import com.sxy.healthcare.home.bean.BusinessInfo;
import com.sxy.healthcare.home.bean.TraderBean;
import com.sxy.healthcare.home.bean.TraderResponse;
import com.sxy.healthcare.home.bean.VegetableResponse;
import com.sxy.healthcare.home.fragment.BusinessDestailFragment;
import com.sxy.healthcare.home.fragment.CommentFragment;
import com.sxy.healthcare.home.fragment.DescFragment;
import com.sxy.healthcare.home.fragment.HealthFragment;

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

public class HealthActivity extends BaseActivity implements View.OnClickListener{

    private static final String TAG = HealthActivity.class.getSimpleName();

    @BindView(R.id.tv_btn_business)
    TextView btnBusiness;

    @BindView(R.id.tv_btn_desc)
    TextView btnDesc;

    @BindView(R.id.tv_btn_comment)
    TextView btnComment;

    @BindView(R.id.content)
    FrameLayout layout;

    private BaseFragment currentFragment;

    private TraderBean traderBean;

    private DescFragment descFragment;
  //  private CommentFragment commentFragment;
    private HealthFragment healthFragment;

    private String id;

    private Disposable detailDis;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        traderBean = (TraderBean) getIntent().getSerializableExtra(Constants.EXTRA_TRADER_BEAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health);
        setCurrentTitle(traderBean.getTraderName());
    }


    @Override
    protected void initViews() {
        super.initViews();
        doReturn();
        id = getIntent().getStringExtra(Constants.EXTRA_MENU_ID);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        getTraderDetail();
    }

    @Override
    protected void initListener() {
        super.initListener();
        btnBusiness.setOnClickListener(this);
        btnComment.setOnClickListener(this);
        btnDesc.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_btn_business:
                btnBusiness.setSelected(true);
                btnDesc.setSelected(false);
                btnComment.setSelected(false);
                add(healthFragment,1,R.id.content,"HealthFragment");
                break;
          /*  case R.id.tv_btn_comment:
                btnBusiness.setSelected(false);
                btnDesc.setSelected(false);
                btnComment.setSelected(true);
                add(commentFragment,3,R.id.content,"CommentFragment");

                break;*/
            case R.id.tv_btn_desc:
                btnBusiness.setSelected(false);
                btnDesc.setSelected(true);
                btnComment.setSelected(false);
                add(descFragment,2,R.id.content,"DescFragment");
                break;
            default:
                break;
        }
    }


    public void add(BaseFragment fragment, int type, int id,String tag){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        HealthFragment tempFragment1;
        DescFragment  tempFragment2;
        CommentFragment tempFragment3;
        if(type==1){
            //优先检查，fragment是否存在，避免重叠

            tempFragment1 = (HealthFragment) fragmentManager.findFragmentByTag(tag);

            if(null!=tempFragment1){
                fragment = tempFragment1;
            }

        }else if(type==2){
            tempFragment2 = (DescFragment) fragmentManager.findFragmentByTag(tag);
            if(null!=tempFragment2){
                fragment = tempFragment2;
            }
        }else if(type==3){
            tempFragment3 = (CommentFragment) fragmentManager.findFragmentByTag(tag);
            if(null!=tempFragment3){
                fragment = tempFragment3;
            }
        }

        if(fragment.isAdded()){
            addOrShowFragment(fragmentTransaction,fragment,id,tag);
        }else{
            if(currentFragment!=null&&currentFragment.isAdded()){
                fragmentTransaction.hide(currentFragment).add(id, fragment,tag).commit();
            }else{
                fragmentTransaction.add(id, fragment,tag).commit();
            }
            currentFragment = fragment;
        }
    }

    /**
     * 添加或者显示 fragment
     *
     * @param fragment
     */
    private void addOrShowFragment(FragmentTransaction transaction, BaseFragment fragment, int id,String tag) {
        if(currentFragment == fragment)
            return;
        if (!fragment.isAdded()) { // 如果当前fragment未被添加，则添加到Fragment管理器中
            transaction.hide(currentFragment).add(id, fragment,tag).commit();
        } else {
            transaction.hide(currentFragment).show(fragment).commit();
        }
        currentFragment.setUserVisibleHint(false);
        currentFragment =  fragment;
        currentFragment.setUserVisibleHint(true);
    }


    private void getTraderDetail(){

        if(null==traderBean){
            return;
        }

        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }
        destroyDetailDis();

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("traderId", traderBean.getTraderId());
        jsonObject.addProperty("type", id);

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
                .getTraderDetail(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        detailDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result.toString());

                            Gson gson = new Gson();
                            Type type = new TypeToken<BusinessInfo>() {}.getType();
                            BusinessInfo response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    LogUtils.d(TAG,"getTraderDetail="+response.getData().toString());
                                    Bundle bundle = new Bundle();
                                    bundle.putSerializable(Constants.EXTRA_TRADER_BEAN,traderBean);
                                    bundle.putString(Constants.EXTRA_MENU_ID,id);
                                    bundle.putSerializable(Constants.EXTRA_BUSINESS_DETAIL,response.getData());

                                    descFragment = new DescFragment();
                                    descFragment.setArguments(bundle);
                                 //   commentFragment = new CommentFragment();
                                 //   commentFragment.setArguments(bundle);

                                    btnBusiness.setSelected(true);
                                    btnDesc.setSelected(false);
                                    btnComment.setSelected(false);

                                    healthFragment = new HealthFragment();
                                    healthFragment.setArguments(bundle);
                                    add(healthFragment,1,R.id.content,"HealthFragment");


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

    private void destroyDetailDis(){
        if (null!=detailDis&&!detailDis.isDisposed()){
            detailDis.dispose();
        }
    }


}
