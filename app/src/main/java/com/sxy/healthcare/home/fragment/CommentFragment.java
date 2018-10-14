package com.sxy.healthcare.home.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;

import com.baidu.navisdk.adapter.BaiduNaviManager;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.EasyRecyclerView;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.jude.easyrecyclerview.decoration.DividerDecoration;
import com.sxy.healthcare.R;
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
import com.sxy.healthcare.home.adapter.CommentAdapter;
import com.sxy.healthcare.home.bean.CommentBean;
import com.sxy.healthcare.home.bean.CommentResponse;
import com.sxy.healthcare.home.bean.TraderBean;
import com.sxy.healthcare.home.bean.TraderResponse;

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

public class CommentFragment extends BaseFragment implements  RecyclerArrayAdapter.OnLoadMoreListener,
        SwipeRefreshLayout.OnRefreshListener{

    private static final String TAG = CommentFragment.class.getSimpleName();

    @BindView(R.id.rc_comments)
    EasyRecyclerView recyclerView;

    private CommentAdapter commentAdapter;

    private List<CommentBean> datas = new ArrayList<>();

    private Disposable commentDis;

    private int pageSize=10;

    private int pageNo=1;

    private TraderBean traderBean;

    private int count;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        traderBean = (TraderBean) getArguments().getSerializable(Constants.EXTRA_TRADER_BEAN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_commetn);
    }

    @Override
    protected void initViews() {
        super.initViews();

        commentAdapter = new CommentAdapter(getContext());

        recyclerView.setAdapter(commentAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(),LinearLayoutManager.VERTICAL, false));

        /*DividerDecoration itemDecoration = new DividerDecoration(Color.LTGRAY, (int) ScreenUtils.dip2px( 0.5f), 0, 0);
        itemDecoration.setDrawLastItem(false);
        recyclerView.addItemDecoration(itemDecoration);*/

        commentAdapter.setMore(R.layout.view_more,CommentFragment.this);
        recyclerView.setRefreshListener(this);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        getTradersComment();
    }

    @Override
    public void onRefresh() {
        pageNo = 1;
        getTradersComment();
    }

    @Override
    public void onLoadMore() {
        if(count>commentAdapter.getCount())
        getTradersComment();
    }


    private void getTradersComment(){

        if(!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())){
            ToastUtils.shortToast(HealthcaseApplication.getApplication(),"当前网络不可用～");
            return;
        }
        destroyDis();

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("traderId",traderBean.getTraderId());
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
                .getTraderComments(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        commentDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result.toString());

                            Gson gson = new Gson();
                            Type type = new TypeToken<CommentResponse>() {}.getType();
                            CommentResponse response = gson.fromJson(result,type);

                            if(null!=response){
                                if(response.isSuccess()){
                                    LogUtils.d(TAG,"getTradersComment="+response.getData().getTraderCommentVos().toString());
                                    count = response.getData().getCount();
                                    if(pageNo==1){
                                        datas.clear();
                                        commentAdapter.clear();
                                    }
                                    datas.addAll(response.getData().getTraderCommentVos());
                                    commentAdapter.addAll(datas);
                                    if (commentAdapter.getCount()<10){
                                        commentAdapter.stopMore();
                                    }
                                    pageNo=pageNo+1;
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

    private void destroyDis(){
        if (null!=commentDis&&!commentDis.isDisposed()){
            commentDis.dispose();
        }
    }

}
