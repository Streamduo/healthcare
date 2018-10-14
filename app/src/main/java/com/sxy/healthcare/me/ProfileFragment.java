package com.sxy.healthcare.me;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.alipay.sdk.app.PayTask;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sxy.healthcare.BuildConfig;
import com.sxy.healthcare.MainActivity;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.AppConfig;
import com.sxy.healthcare.base.BaseFragment;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.event.ModifyAvatar;
import com.sxy.healthcare.common.event.NicknameEvent;
import com.sxy.healthcare.common.event.RxBus;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ShareUtils;
import com.sxy.healthcare.common.utils.SharedPrefsUtil;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.activity.BillActivity;
import com.sxy.healthcare.me.activity.DealHistoryActivity;
import com.sxy.healthcare.me.activity.IntegralRechargeActivity;
import com.sxy.healthcare.me.activity.InvitedMemberActivity;
import com.sxy.healthcare.me.activity.ModifyInfoActivity;
import com.sxy.healthcare.me.activity.MyOrderActivity;
import com.sxy.healthcare.me.activity.ProfileReserveActivity;
import com.sxy.healthcare.me.bean.CreateOrderBean;
import com.sxy.healthcare.me.bean.PayInfo;
import com.sxy.healthcare.me.bean.PayResult;
import com.sxy.healthcare.me.bean.UserInfo;
import com.sxy.healthcare.me.bean.WxPayBean;
import com.sxy.healthcare.me.dialog.OrderSettleDialog;
import com.sxy.healthcare.me.dialog.ShareDialog;
import com.sxy.healthcare.me.event.CancelEvent;
import com.sxy.healthcare.me.event.PayEvent;
import com.tencent.mm.opensdk.modelpay.PayReq;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

public class ProfileFragment extends BaseFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = ProfileFragment.class.getSimpleName();

    @BindView(R.id.tv_share)
    TextView btnShare;

    @BindView(R.id.tv_js)
    TextView btnOrderSettle;

    @BindView(R.id.tv_user_name)
    TextView mobile;

    @BindView(R.id.tv_card_no)
    TextView cardNo;

    @BindView(R.id.my_balance)
    TextView balance;

    @BindView(R.id.iv_user_avatar)
    CircleImageView avatar;

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;


    private SharedPrefsUtil sharedPrefsUtil;

    private Disposable disposable;

    private Disposable userDis;

    private UserInfo userInfo;
    private int selectPayType;

    public static final int SDK_PAY_FLAG = 1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {
                        Toast.makeText(getActivity(), "支付成功", Toast.LENGTH_SHORT).show();
                        PayEvent payEvent = new PayEvent();
                        payEvent.setPayType(2);
                        EventBus.getDefault().post(payEvent);
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(getActivity(), "支付结果确认中", Toast.LENGTH_SHORT).show();
                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(getActivity(), "支付失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                }
                default:
                    break;
            }
        }

    };
    private OrderSettleDialog orderSettleDialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void initViews() {
        super.initViews();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //  setImmerseLayout(mRootView);
    }

    protected void setImmerseLayout(View view) {// view为标题栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getActivity().getWindow();
            //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = getStatusBarHeight(getActivity().getBaseContext());
            view.setPadding(0, 0, 0, 0);
        }
    }

    public int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void initDatas() {
        super.initDatas();
        sharedPrefsUtil = SharedPrefsUtil.getInstance(getActivity());
        HealthcaseApplication.getIwxapi().registerApp(AppConfig.WEI_XIN_APP_ID);
        operateBus();
        getUserProfile();
    }

    @Override
    protected void initListener() {
        super.initListener();
        mRootView.findViewById(R.id.rl_deal_history).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_modify_info).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_profile_order).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_profile_reserve).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_integral_recharge).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_invited_member).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_exit).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_server_phone).setOnClickListener(this);
        mRootView.findViewById(R.id.rl_bill).setOnClickListener(this);

        btnShare.setOnClickListener(this);
        btnOrderSettle.setOnClickListener(this);

        swipeRefreshLayout.setOnRefreshListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.rl_deal_history:
                intent = new Intent(getContext(), DealHistoryActivity.class);
                intent.putExtra(Constants.EXTRA_USER_INFO, userInfo);
                startActivity(intent);
                break;
            case R.id.rl_integral_recharge:
                intent = new Intent(getContext(), IntegralRechargeActivity.class);
                intent.putExtra(Constants.EXTRA_USER_INFO, userInfo);
                startActivity(intent);
                break;
            case R.id.rl_invited_member:
                intent = new Intent(getContext(), InvitedMemberActivity.class);
                intent.putExtra(Constants.EXTRA_USER_INFO, userInfo);
                startActivity(intent);
                break;
            case R.id.rl_modify_info:
                intent = new Intent(getContext(), ModifyInfoActivity.class);
                intent.putExtra(Constants.EXTRA_USER_INFO, userInfo);
                startActivity(intent);
                break;
            case R.id.rl_profile_order:
                intent = new Intent(getContext(), MyOrderActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_profile_reserve:
                intent = new Intent(getContext(), ProfileReserveActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_share:
                final ShareDialog shareDialog = ShareDialog.newInstance();
                shareDialog.show(getFragmentManager(), TAG);
                shareDialog.setWxListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareDialog.dismiss();
                        ShareUtils.shareTowx(0, BuildConfig.SHARE_URL + userInfo.getUserId(), HealthcaseApplication.getIwxapi());
                    }
                });
                shareDialog.setMomentListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        shareDialog.dismiss();
                        ShareUtils.shareTowx(1, BuildConfig.SHARE_URL + userInfo.getUserId(), HealthcaseApplication.getIwxapi());
                    }
                });

                break;
            case R.id.tv_js:
                orderSettleDialog = OrderSettleDialog.newInstance();
                orderSettleDialog.show(getFragmentManager(), TAG);
                orderSettleDialog.setConfirmListener(new OrderSettleDialog.ConfirmListener() {
                    @Override
                    public void OnSelctedClick(int position, String price) {
                        Log.i("ssssss", position + "====" + price);
                        createOrder(position, price);
                    }
                });
                break;
            case R.id.rl_exit:
                exit();
                break;
            case R.id.rl_server_phone:
                call("01081558190");
                break;
            case R.id.rl_bill:
                intent = new Intent(getContext(), BillActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    private void createOrder(int position, String price) {
        selectPayType = position;
        if (!NetUtils.isNetworkAvailable(getActivity().getApplicationContext())) {
            ToastUtils.shortToast(getActivity().getApplicationContext(), "当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("price", price + "");
        jsonObject.addProperty("paymentWay", position + "");


        String param = null;
        try {
            param = ThreeDesUtils.encryptThreeDESECB(jsonObject.toString(),
                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("token", sharedPrefsUtil.getString(Constants.USER_TOKEN, ""));
            jsonObject1.put("param", param);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject1.toString());

        ApiServiceFactory.getStringApiService()
                .getCreateOrder(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String stringResponse) {

                        try {
                            String result = ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY, ""));

                            LogUtils.d(TAG, "result=" + result);
                            Gson gson = new Gson();
                            CreateOrderBean response = gson.fromJson(result, CreateOrderBean.class);

                            if (response.isSuccess()) {
                                if (response.getData() != null) {
                                    CreateOrderBean.DataBean dataBean = response.getData();
                                    createPayOrder(dataBean.getBillNo(), dataBean.getPayBsType(), dataBean.getPaymentWay());
                                }
                            } else {
                                ToastUtils.shortToast(getActivity().getApplicationContext(), response.getMsg());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(getActivity().getApplicationContext(), "支付失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void createPayOrder(String billNo, String payBsType, String paymentWay) {
        if (!NetUtils.isNetworkAvailable(getActivity().getApplicationContext())) {
            ToastUtils.shortToast(getActivity().getApplicationContext(), "当前网络不可用～");
            return;
        }

        final JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("billNo", billNo);
        jsonObject.addProperty("payBsType", payBsType);
        jsonObject.addProperty("paymentWay", paymentWay);

        String param = null;
        try {
            param = ThreeDesUtils.encryptThreeDESECB(jsonObject.toString(),
                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY, ""));
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("token", sharedPrefsUtil.getString(Constants.USER_TOKEN, ""));
            jsonObject1.put("param", param);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject1.toString());

        ApiServiceFactory.getStringApiService()
                .getPayOrder(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        if (orderSettleDialog!=null){
                            orderSettleDialog.dismiss();
                        }
                        try {
                            String result = ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY, ""));

                            LogUtils.d(TAG, "result2=" + result);
                            Gson gson = new Gson();
                            Response<String> response = gson.fromJson(result, Response.class);

                            if (response.isSuccess()) {
                                if (selectPayType == 3) {
                                    Gson gsonwx = new Gson();
                                    PayInfo payInfo = gsonwx.fromJson(result, PayInfo.class);
                                    doForWxPay(payInfo.getData());
                                } else if (selectPayType == 4) {
                                    String orderInfo = response.getData();
                                    doForAlPay(orderInfo);
                                } else {
                                    JSONObject jsonObject2 = new JSONObject(result);
                                    boolean data = jsonObject2.getBoolean("data");
                                    if (data == true) {
                                        getUserProfile();
                                        ToastUtils.LongToast(getActivity(), "支付成功");
                                    }else {
                                        ToastUtils.LongToast(getActivity(), "支付失败");
                                    }
                                }
                            } else if (response.getCode() == 9002) {
                                ToastUtils.shortToast(getActivity().getApplicationContext(), response.getMsg());
                                Intent intent = new Intent(getContext(), IntegralRechargeActivity.class);
                                intent.putExtra(Constants.EXTRA_USER_INFO, userInfo);
                                startActivity(intent);
                            } else {
                                ToastUtils.shortToast(getActivity().getApplicationContext(), response.getMsg());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(getActivity().getApplicationContext(), "支付失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    private void doForWxPay(final WxPayBean wxPayBean) {
        LogUtils.d(TAG, "doForWxPay11");

        PayReq request = new PayReq();
        request.appId = wxPayBean.getAppid();
        request.partnerId = wxPayBean.getPartnerid();
        request.prepayId = wxPayBean.getPrepayid();
        request.packageValue = wxPayBean.getPackageValue();
        request.nonceStr = wxPayBean.getNoncestr();
        request.timeStamp = wxPayBean.getTimestamp();
        request.sign = wxPayBean.getSign();
        HealthcaseApplication.getIwxapi().sendReq(request);
        LogUtils.d(TAG, "doForWxPay22");
    }

    private void doForAlPay(final String orderInfo) {
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(getActivity());
                String result = alipay.pay(orderInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }


    /**
     * 调用拨号界面
     *
     * @param phone 电话号码
     */
    private void call(String phone) {

        try {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //退出登录
    private void exit() {

        destroyDis();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", sharedPrefsUtil.getString(Constants.USER_TOKEN, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        ApiServiceFactory.getStringApiService()
                .doExit(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        LogUtils.d(TAG, "result=" + stringResponse.toString());
                        String result = null;
                        try {
                            result = ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY, ""));
                            Gson gson = new Gson();

                            Type type = new TypeToken<Response<UserInfo>>() {
                            }.getType();

                            Response<UserInfo> response = gson.fromJson(result, type);

                            LogUtils.d(TAG, "result=" + response.toString());

                            if (response.isSuccess()) {
                                ToastUtils.shortToast(getContext(), "退出成功～");
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                                sharedPrefsUtil.removeByKey(Constants.USER_INFO);
                                sharedPrefsUtil.removeByKey(Constants.LOGIN_SUCCESS);
                            } else {
                                ToastUtils.shortToast(getContext(), response.getMsg());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                        ToastUtils.shortToast(getContext(), "退出失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    /**
     * 获取概要信息
     */
    private void getUserProfile() {

        if (!NetUtils.isNetworkAvailable(getActivity().getApplicationContext())) {
            ToastUtils.shortToast(getActivity().getApplicationContext(), "当前网络不可用～");
            return;
        }

        destroyUserDis();

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("token", sharedPrefsUtil.getString(Constants.USER_TOKEN, ""));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

        ApiServiceFactory.getStringApiService()
                .getUserProfile(body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        userDis = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        try {
                            String result = ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    sharedPrefsUtil.getString(Constants.USER_SECRET_KEY, ""));

                            LogUtils.d(TAG, "[getUserProfile]result=" + result);
                            Gson gson = new Gson();
                            Type type = new TypeToken<Response<UserInfo>>() {
                            }.getType();
                            Response<UserInfo> response = gson.fromJson(result, type);

                            if (response.isSuccess()) {
                                if (response.getData() != null) {
                                    userInfo = response.getData();
                                    mobile.setText(response.getData().getNickName());
                                    cardNo.setText("会员号：" + response.getData().getCardNo());
                                    balance.setText(response.getData().getBalance() + "");
                                    sharedPrefsUtil.setString(Constants.USER_INFO, response.getData().getUserId());
                                    sharedPrefsUtil.setString(Constants.USER_INFO_BALANCE, response.getData().getBalance() + "");
                                    Glide.with(getContext())
                                            .load(response.getData().getHeadImg()).apply(GlideUtils.getOptionsAvatar()).into(avatar);
                                }
                            } else {
                                ToastUtils.shortToast(getActivity().getApplicationContext(), response.getMsg());
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        swipeRefreshLayout.setRefreshing(false);
                        e.printStackTrace();
                        ToastUtils.shortToast(getActivity().getApplicationContext(), "获取失败～");
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }


    private void destroyUserDis() {
        if (null != userDis && !userDis.isDisposed()) {
            userDis.dispose();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(NicknameEvent jionEvent) {
        LogUtils.d(TAG, "jionEvent=" + jionEvent.toString());
        getUserProfile();
      /*  userInfo.setNickName(jionEvent.getNickname());
        userInfo.setSex(jionEvent.getSex());
        userInfo.setBirthday(jionEvent.getBirth());
        mobile.setText(userInfo.getNickName());
        cardNo.setText("会员号："+userInfo.getCardNo());*/
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(PayEvent payEvent) {
        getUserProfile();
      /* userInfo.setBalance((Double.parseDouble(userInfo.getBalance())-payEvent.getPrice())+"");
        SharedPrefsUtil.getInstance(getActivity()).setString(Constants.USER_INFO_BALANCE,userInfo.getBalance());*/
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        destroyDis();
        destroyUserDis();
        EventBus.getDefault().unregister(this);
    }

    private void destroyDis() {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    /**
     * RxBus
     */
    private void operateBus() {
        RxBus.get().toFlowable()
                .map(new Function<Object, ModifyAvatar>() {
                    @Override
                    public ModifyAvatar apply(@NonNull Object o) throws Exception {
                        return (ModifyAvatar) o;
                    }

                })
                .subscribe(new Consumer<ModifyAvatar>() {
                    @Override
                    public void accept(@NonNull ModifyAvatar modifyAvatar) throws Exception {
                        userInfo.setHeadImg(modifyAvatar.getImgUrl());
                        if (getActivity() != null)
                            Glide.with(getContext())
                                    .load(modifyAvatar.getImgUrl()).apply(GlideUtils.getOptionsAvatar()).into(avatar);
                    }

                });
    }


    private void hookOnClickListener(View view) {
        try {
            // 得到 View 的 ListenerInfo 对象
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            Object listenerInfo = getListenerInfo.invoke(view);
            // 得到 原始的 OnClickListener 对象
            Class<?> listenerInfoClz = Class.forName("android.view.View$ListenerInfo");
            Field mOnClickListener = listenerInfoClz.getDeclaredField("mOnClickListener");
            mOnClickListener.setAccessible(true);
            View.OnClickListener originOnClickListener =
                    (View.OnClickListener) mOnClickListener.get(listenerInfo);
            // 用自定义的 OnClickListener 替换原始的 OnClickListener
            View.OnClickListener hookedOnClickListener =
                    new HookedOnClickListener(originOnClickListener);
            mOnClickListener.set(listenerInfo, hookedOnClickListener);
        } catch (Exception e) {

        }

    }

    @Override
    public void onRefresh() {
        getUserProfile();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(CancelEvent cancelEvent) {
        getUserProfile();
    }

    class HookedOnClickListener implements View.OnClickListener {
        private View.OnClickListener origin;

        HookedOnClickListener(View.OnClickListener origin) {
            this.origin = origin;
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getActivity(), "hook click", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Before click, do what you want to to.");
            if (origin != null) {
                origin.onClick(v);
            }
            Log.d(TAG, "After click, do what you want to to.");
        }
    }
}
