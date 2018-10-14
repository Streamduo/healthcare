package com.sxy.healthcare.home.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.BaseFragment;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.common.view.TipsDialog;
import com.sxy.healthcare.common.wheelview.BottomDialog;
import com.sxy.healthcare.common.wheelview.WheelView;
import com.sxy.healthcare.home.activity.FoodMenuActivity;
import com.sxy.healthcare.home.adapter.CookerAdapter;
import com.sxy.healthcare.home.adapter.FoodAdapter;
import com.sxy.healthcare.home.bean.BusinessBean;
import com.sxy.healthcare.home.bean.FoodInfo;
import com.sxy.healthcare.home.bean.GoodsCuisinesInfo;
import com.sxy.healthcare.home.bean.TraderBean;

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

public class BusinessDestailFragment extends BaseFragment{

    private static final String TAG = BusinessDestailFragment.class.getSimpleName();

    private TraderBean traderBean;

    private BusinessBean businessBean;

    private Disposable detailDis;

    private Disposable goodsDis;

    @BindView(R.id.rc_food_type)
    RecyclerView recyclerView;

    private FoodAdapter foodAdapter;

    @BindView(R.id.rc_cookers)
    RecyclerView rcCookers;

    @BindView(R.id.tv_subscribe)
    TextView subscribe;

    @BindView(R.id.tv_perCost)
    TextView perCost;

  /*  @BindView(R.id.num)
    SnappingStepper num;*/

    @BindView(R.id.tv_num)
    TextView persons;


    private CookerAdapter cookerAdapter;

    private String id;

    private BottomDialog bottomDialog;

    private List<String> costList;

    private List<String> numlist;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        traderBean = (TraderBean) getArguments().getSerializable(Constants.EXTRA_TRADER_BEAN);
        businessBean = (BusinessBean)getArguments().getSerializable(Constants.EXTRA_BUSINESS_DETAIL);
        id = getArguments().getString(Constants.EXTRA_MENU_ID);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_business_detail);
    }

    @Override
    protected void initViews() {
        super.initViews();
        foodAdapter = new FoodAdapter(getContext());

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(),3));
        foodAdapter.setList(businessBean.getGoodsCuisines());

        recyclerView.setAdapter(foodAdapter);

        cookerAdapter = new CookerAdapter(getContext());
        rcCookers.setLayoutManager(new GridLayoutManager(getContext(),2));
        rcCookers.setAdapter(cookerAdapter);

        if(businessBean.getCookerVos()!=null){
            for (int i=0;i<businessBean.getCookerVos().size();i++){
                if(i==0){
                    businessBean.getCookerVos().get(i).setSelected(true);
                }
            }
        }

        cookerAdapter.setCookerBeans(businessBean.getCookerVos());
    }

    @Override
    protected void initDatas() {
        super.initDatas();
      //  getTraderDetail();
      //  getGoodsCuisines();
        costList = new ArrayList<>();
        costList.add("100");
        costList.add("200");
        costList.add("300");
        costList.add("400");
        costList.add("500");
        costList.add("600");
        costList.add("700");
        costList.add("800");
        costList.add("900");
        costList.add("1000");
        costList.add("1100");
        costList.add("1200");
        costList.add("1300");
        costList.add("1400");
        costList.add("1500");

        numlist = new ArrayList<>();
        for(int i=1;i<=30;i++){
            numlist.add(i+"");
        }
    }

    @Override
    protected void initListener() {
        super.initListener();

        perCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View outerView1 = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_cost, null);

                final WheelView wheelView = (WheelView) outerView1.findViewById(R.id.wheel_view);


                wheelView.setItems(costList,0);

                //联动逻辑效果
                wheelView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index,String item) {
                        perCost.setText(wheelView.getSelectedItem());
                    }
                });


                TextView tv_ok = (TextView) outerView1.findViewById(R.id.tv_ok);
                TextView tv_cancel = (TextView) outerView1.findViewById(R.id.tv_cancel);
                //点击确定
                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        bottomDialog.dismiss();

                    }
                });
                //点击取消
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        bottomDialog.dismiss();
                    }
                });
                //防止弹出两个窗口
                if (bottomDialog !=null && bottomDialog.isShowing()) {
                    return;
                }

                bottomDialog = new BottomDialog(getContext(), R.style.ActionSheetDialogStyle);
                //将布局设置给Dialog
                bottomDialog.setContentView(outerView1);
                bottomDialog.show();//显示对话框
            }
        });

        persons.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View outerView1 = LayoutInflater.from(getContext()).inflate(R.layout.dialog_select_cost, null);

                final WheelView wheelView = (WheelView) outerView1.findViewById(R.id.wheel_view);


                wheelView.setItems(numlist,0);

                //联动逻辑效果
                wheelView.setOnItemSelectedListener(new WheelView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(int index,String item) {
                        persons.setText(wheelView.getSelectedItem());
                    }
                });


                TextView tv_ok = (TextView) outerView1.findViewById(R.id.tv_ok);
                TextView tv_cancel = (TextView) outerView1.findViewById(R.id.tv_cancel);
                //点击确定
                tv_ok.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        bottomDialog.dismiss();

                    }
                });
                //点击取消
                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        bottomDialog.dismiss();
                    }
                });
                //防止弹出两个窗口
                if (bottomDialog !=null && bottomDialog.isShowing()) {
                    return;
                }

                bottomDialog = new BottomDialog(getContext(), R.style.ActionSheetDialogStyle);
                //将布局设置给Dialog
                bottomDialog.setContentView(outerView1);
                bottomDialog.show();//显示对话框
            }
        });


        subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               /* if(StringUtils.isEmpty(perCost.getText().toString())){
                    ToastUtils.shortToast(getContext().getApplicationContext(),"请输入人均消费！");
                    return;
                }*/

               if(foodAdapter.getList()!=null){
                   boolean selectFood = false;
                   for(int i=0;i<foodAdapter.getList().size();i++){
                       if(foodAdapter.getList().get(i).isSelected()){
                           selectFood = true;
                           break;
                       }
                   }

                   if(!selectFood){
                       ToastUtils.shortToast(getContext().getApplicationContext(),"请选择菜系");
                       return;
                   }
               }

              /*  if(num.getValue()<=0){
                    ToastUtils.shortToast(getContext().getApplicationContext(),"请输入人数！");
                    return;
                }*/

                final TipsDialog tipsDialog = TipsDialog.newInstance();
                tipsDialog.show(getFragmentManager(),TAG);
                tipsDialog.setCancelListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        tipsDialog.dismiss();
                    }
                });

                tipsDialog.setConfirmListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(),FoodMenuActivity.class);
                        intent.putExtra(Constants.EXTRA_BUSINESS_DETAIL,businessBean);
                        intent.putExtra(Constants.EXTRA_MENU_ID,id);
                        if(foodAdapter.getList()!=null){
                            StringBuffer stringBuffer = new StringBuffer();
                            for(int i=0;i<foodAdapter.getList().size();i++){
                                if(foodAdapter.getList().get(i).isSelected()){
                                    stringBuffer.append(foodAdapter.getList().get(i).getId()+",");
                                }
                            }

                            String cookerid = "";
                            if(cookerAdapter.getCookerBeans()!=null){
                                for (int i=0;i<cookerAdapter.getCookerBeans().size();i++){
                                    if(cookerAdapter.getCookerBeans().get(i).isSelected()){
                                        cookerid = cookerAdapter.getCookerBeans().get(i).getId()+"";
                                    }
                                }
                            }

                            String ids = stringBuffer.toString().substring(0,stringBuffer.length()-1);
                            intent.putExtra(Constants.EXTRA_FOODS_ID,ids);
                            intent.putExtra(Constants.EXTRA_COOKER_ID,cookerid);
                            intent.putExtra(Constants.EXTRA_NUM,persons.getText().toString());
                            intent.putExtra(Constants.EXTRA_PER_COST,perCost.getText().toString());
                            startActivity(intent);
                        }
                       tipsDialog.dismiss();
                    }
                });

                /*Intent intent = new Intent(getContext(),FoodMenuActivity.class);
                intent.putExtra(Constants.EXTRA_BUSINESS_DETAIL,businessBean);
                intent.putExtra(Constants.EXTRA_MENU_ID,id);
                if(foodAdapter.getList()!=null){
                    StringBuffer stringBuffer = new StringBuffer();
                    for(int i=0;i<foodAdapter.getList().size();i++){
                        if(foodAdapter.getList().get(i).isSelected()){
                            stringBuffer.append(foodAdapter.getList().get(i).getId()+",");
                        }
                    }

                    String cookerid = "";
                    if(cookerAdapter.getCookerBeans()!=null){
                        for (int i=0;i<cookerAdapter.getCookerBeans().size();i++){
                            if(cookerAdapter.getCookerBeans().get(i).isSelected()){
                                cookerid = cookerAdapter.getCookerBeans().get(i).getId()+"";
                            }
                        }
                    }

                    String ids = stringBuffer.toString().substring(0,stringBuffer.length()-1);
                    intent.putExtra(Constants.EXTRA_FOODS_ID,ids);
                    intent.putExtra(Constants.EXTRA_COOKER_ID,cookerid);
                    intent.putExtra(Constants.EXTRA_NUM,num.getValue()+"");
                    intent.putExtra(Constants.EXTRA_PER_COST,perCost.getText().toString());
                    startActivity(intent);
                }*/
            }
        });
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
                            Type type = new TypeToken<FoodInfo>() {}.getType();
                            FoodInfo response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    LogUtils.d(TAG,"getTraderDetail="+response.getData().toString());
                                    cookerAdapter.setCookerBeans(response.getData().getCookerVos());
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

    /**
     * 获取菜系
     * */
    private void getGoodsCuisines(){


        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }
        destroyGoodsDis();

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("token",sharedPrefsUtil.getString(Constants.USER_TOKEN,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject1.toString());

        ApiServiceFactory.getStringApiService()
                .getGoodsCuisines(body)
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
                            Type type = new TypeToken<GoodsCuisinesInfo>() {}.getType();
                            GoodsCuisinesInfo response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    LogUtils.d(TAG,"111="+response.getData().toString());
                                    foodAdapter.setList(response.getData());
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

    private void destroyGoodsDis(){
        if (null!=goodsDis&&!goodsDis.isDisposed()){
            goodsDis.dispose();
        }
    }
}
