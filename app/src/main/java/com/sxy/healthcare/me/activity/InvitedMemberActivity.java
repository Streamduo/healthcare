package com.sxy.healthcare.me.activity;

import android.content.Intent;
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
import com.sxy.healthcare.common.utils.StringUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.common.utils.ValidatorUtils;
import com.sxy.healthcare.me.adapter.MembersAdapter;
import com.sxy.healthcare.me.adapter.OrderAdapter;
import com.sxy.healthcare.me.bean.MemberResponse;
import com.sxy.healthcare.me.bean.MembersBean;
import com.sxy.healthcare.me.bean.UserInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class InvitedMemberActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = InvitedMemberActivity.class.getSimpleName();

    @BindView(R.id.rc_members)
    RecyclerView rcMembers;

    MembersAdapter membersAdapter;

    private Disposable invitedDis;

    private UserInfo userInfo;

    @BindView(R.id.tv_user_name)
    TextView mobile;

    @BindView(R.id.tv_card_no)
    TextView cardNo;

    private List<MembersBean> datas = new ArrayList<>();

    @BindView(R.id.iv_user_avatar)
    ImageView avatar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invited_member);
    }

    @Override
    protected void initViews() {
        super.initViews();
        setCurrentTitle(R.string.profile_invited_members);
        doReturn();

        membersAdapter = new MembersAdapter(this);
        rcMembers.setLayoutManager(new LinearLayoutManager(this));
        rcMembers.setAdapter(membersAdapter);
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        userInfo = (UserInfo) getIntent().getSerializableExtra(Constants.EXTRA_USER_INFO);
        if(null!=userInfo){
            mobile.setText(userInfo.getNickName());
            cardNo.setText("会员号："+userInfo.getCardNo());
            Glide.with(InvitedMemberActivity.this)
                    .load(userInfo.getHeadImg()).apply(GlideUtils.getOptionsAvatar()).into(avatar);
        }
        getInvitedMembers();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            default:
                break;
        }

    }

    /**
     * 获取我邀请的人
     * */
    private void getInvitedMembers(){

        if(!NetUtils.isNetworkAvailable(getApplicationContext())){
            ToastUtils.shortToast(getApplicationContext(),"当前网络不可用～");
            return;
        }

        destroyInvitedDis();

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("token",sharedPrefsUtil.getString(Constants.USER_TOKEN,""));
        } catch (JSONException e) {
            e.printStackTrace();
        }


        RequestBody body=RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                jsonObject1.toString());


        ApiServiceFactory.getStringApiService()
                .getInviters(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        invitedDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        try {
                            String result= ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY,""));

                            LogUtils.d(TAG,"result="+result);
                            Gson gson = new Gson();
                             MemberResponse response = gson.fromJson(result,MemberResponse.class);

                            if(response.isSuccess()){
                                if(response.getData()!=null){
                                    if(response.getData().getOneLevelInvites()!=null){
                                        for (int i=0;i<response.getData().getOneLevelInvites().size();i++){
                                            response.getData().getOneLevelInvites().get(i).setLevel(1);
                                            datas.add(response.getData().getOneLevelInvites().get(i));
                                        }
                                    }

                                    /*if(response.getData().getTwoLevelInvites()!=null){
                                        for (int i=0;i<response.getData().getTwoLevelInvites().size();i++){
                                            response.getData().getTwoLevelInvites().get(i).setLevel(2);
                                            datas.add(response.getData().getTwoLevelInvites().get(i));
                                        }
                                    }*/
                                }

                                membersAdapter.setMembersBeans(datas);
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
                        ToastUtils.shortToast(getApplicationContext(),"获取失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }



    private void destroyInvitedDis(){
        if (null!=invitedDis&&!invitedDis.isDisposed()){
            invitedDis.dispose();
        }
    }
}
