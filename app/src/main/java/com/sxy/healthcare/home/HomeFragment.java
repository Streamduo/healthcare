package com.sxy.healthcare.home;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jude.easyrecyclerview.adapter.RecyclerArrayAdapter;
import com.sxy.healthcare.R;
import com.sxy.healthcare.base.AppConfig;
import com.sxy.healthcare.base.BaseFragment;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.base.bean.BaseInfo;
import com.sxy.healthcare.common.net.ApiServiceFactory;
import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.common.utils.GlideUtils;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.NetUtils;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.home.activity.GoodsDetailActivity;
import com.sxy.healthcare.home.activity.HealthDetailActivity;
import com.sxy.healthcare.home.activity.SearchActivity;
import com.sxy.healthcare.home.adapter.HomeMenuAdapter;
import com.sxy.healthcare.home.adapter.HotServiceAdapter;
import com.sxy.healthcare.home.adapter.SearchGoodsAdapter;
import com.sxy.healthcare.home.bean.AdsVosBean;
import com.sxy.healthcare.home.bean.HomeInfo;
import com.sxy.healthcare.home.bean.VersionInfo;
import com.sxy.healthcare.me.event.GetTokenEvent;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
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

public class HomeFragment extends BaseFragment implements View.OnClickListener,
        SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = HomeFragment.class.getSimpleName();
    private static final String saveFileName = "HealthCare.apk"; //完整路径名
    private static final int DOWNLOADING = 1; //表示正在下载
    private static final int DOWNLOADED = 2; //下载完毕
    private static final int DOWNLOAD_FAILED = 3; //下载失败
    @BindView(R.id.rv_hot_service)
    RecyclerView hotService;
    @BindView(R.id.rv_hot_goods)
    RecyclerView hotGoods;
    @BindView(R.id.rc_home_menu)
    RecyclerView homeMenu;
    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.et_search)
    EditText etSearch;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    NotificationManager nm;
    NotificationCompat.Builder builder;
    private HotServiceAdapter hotServiceAdapter;
    private HotServiceAdapter hotGoodsAdapter;
    private HomeMenuAdapter homeMenuAdapter;
    private Disposable homeDis;
    private SearchGoodsAdapter adapter11;
    private SearchGoodsAdapter adapter22;
    private List<AdsVosBean> adsVosBeans;
    private List<String> images = new ArrayList<>();
    private ProgressBar mProgress; //下载进度条控件
    private int progress; //下载进度
    private boolean cancelFlag = false; //取消下载标志位
    private int con = 0;
    private Disposable disposable;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    protected void initViews() {
        super.initViews();
        builder = new NotificationCompat.Builder(getActivity());
        nm = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        hotService.setNestedScrollingEnabled(false);
        hotGoods.setNestedScrollingEnabled(false);
        hotService.setLayoutManager(new GridLayoutManager(getContext(), 2));

        hotGoods.setLayoutManager(new GridLayoutManager(getContext(), 2));

        etSearch.setFocusable(false);

    }

    @Override
    protected void initDatas() {
        super.initDatas();

        getHome();
        getVersion();

/*        List<String> images111 = new ArrayList<>();
        images.add("http://img.zcool.cn/community/0166c756e1427432f875520f7cc838.jpg");
        images.add("http://img.zcool.cn/community/018fdb56e1428632f875520f7b67cb.jpg");
        images.add("http://img.zcool.cn/community/01c8dc56e1428e6ac72531cbaa5f2c.jpg");
        images.add("http://img.zcool.cn/community/01fda356640b706ac725b2c8b99b08.jpg");*/

        // hotServiceAdapter = new HotServiceAdapter(getContext());
        adapter11 = new SearchGoodsAdapter(getContext());
        hotService.setAdapter(adapter11);

        // hotGoodsAdapter = new HotServiceAdapter(getContext());
        adapter22 = new SearchGoodsAdapter(getContext());
        hotGoods.setAdapter(adapter22);

        homeMenuAdapter = new HomeMenuAdapter(getContext());
        homeMenu.setAdapter(homeMenuAdapter);
        homeMenu.setLayoutManager(new GridLayoutManager(getContext(), 5));
    }

    @Override
    protected void initListener() {
        super.initListener();
       /* mRootView.findViewById(R.id.tv_category_food).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_category_health).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_category_fun).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_category_goods).setOnClickListener(this);
        mRootView.findViewById(R.id.tv_category_other).setOnClickListener(this);*/
        etSearch.setOnClickListener(this);
        swipeRefreshLayout.setOnRefreshListener(this);

        adapter11.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), HealthDetailActivity.class);
                intent.putExtra(Constants.EXTRA_HEALTH, adapter11.getItem(position));
                intent.putExtra("tradeId", adapter11.getItem(position).getTraderId());
                getContext().startActivity(intent);
            }
        });

        adapter22.setOnItemClickListener(new RecyclerArrayAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                Intent intent = new Intent(getContext(), GoodsDetailActivity.class);
                intent.putExtra(Constants.EXTRA_HEALTH, adapter22.getItem(position));
                getContext().startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //setImmerseLayout(mRootView);
    }

    protected void setImmerseLayout(View view) {// view为标题栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            int statusBarHeight = getStatusBarHeight(getActivity().getBaseContext());
            view.setPadding(0, statusBarHeight, 0, 0);
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

    private void setmBanner(List<String> images) {
        mBanner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        mBanner.setIndicatorGravity(BannerConfig.CENTER);
        mBanner.isAutoPlay(true);
        mBanner.setDelayTime(5000);
        mBanner.setImages(images);

        mBanner.setImageLoader(new ImageLoader() {
            @Override
            public void displayImage(Context context, Object path, ImageView imageView) {
                Glide.with(getContext()).load(path).apply(GlideUtils.getOptions()).into(imageView);
            }
        });

        mBanner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {

            }
        });

        mBanner.start();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.et_search:
                intent = new Intent(getContext(), SearchActivity.class);
                startActivity(intent);
                break;
        }
    }

    private void getVersion() {
        if (!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())) {
            ToastUtils.shortToast(HealthcaseApplication.getApplication(), "当前网络不可用～");
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", sharedPrefsUtil.getString(Constants.USER_TOKEN, ""));

            RequestBody body = RequestBody
                    .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            ApiServiceFactory.getStringApiService()
                    .getVersion(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            homeDis = d;
                        }

                        @Override
                        public void onNext(String stringResponse) {
                            try {
                                if (stringResponse != null) {
                                    String result = ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                            sharedPrefsUtil.getString(Constants.USER_SECRET_KEY, ""));

                                    LogUtils.d(TAG, "result=" + result.toString());

                                    Gson gson = new Gson();
                                    VersionInfo response = gson.fromJson(result, VersionInfo.class);

                                    if (response.isSuccess()) {
                                        String sysversion = response.getData().versionNo;
                                        String s = sysversion.replace(".", "");
                                        int sysversioncode = Integer.parseInt(s);
                                        String versionName = getCurrentVersionName(getActivity());
                                        String s1 = versionName.replace(".", "");
                                        int versioncode = Integer.parseInt(s1);
                                        if (sysversioncode > versioncode) {
                                            showDialog(response.getData().updateDesc, response.getData().updateUrl);
                                        }

                                    } else {
                                        ToastUtils.shortToast(HealthcaseApplication.getApplication(), "");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            ToastUtils.shortToast(HealthcaseApplication.getApplication(), "获取数据失败～");
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        } catch (Exception e) {

        }
    }

    private void getHome() {
        if (!NetUtils.isNetworkAvailable(HealthcaseApplication.getApplication())) {
            ToastUtils.shortToast(HealthcaseApplication.getApplication(), "当前网络不可用～");
            return;
        }

        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("token", sharedPrefsUtil.getString(Constants.USER_TOKEN, ""));

            RequestBody body = RequestBody
                    .create(okhttp3.MediaType.parse("application/json; charset=utf-8"), jsonObject.toString());

            destroyHomeDis();

            ApiServiceFactory.getStringApiService()
                    .getHomeDataBak(body)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            homeDis = d;
                        }

                        @Override
                        public void onNext(String stringResponse) {
                            if (swipeRefreshLayout != null && swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                            try {
                                if (stringResponse != null) {
                                    String result = ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                            sharedPrefsUtil.getString(Constants.USER_SECRET_KEY, ""));

                                    LogUtils.d(TAG, "result=" + result.toString());

                                    Gson gson = new Gson();
                                    HomeInfo response = gson.fromJson(result, HomeInfo.class);

                                    if (response.isSuccess()) {
                                        //  hotServiceAdapter.setHotServiceBeanList(response.getData().getServiceVos());
                                        //   hotGoodsAdapter.setHotServiceBeanList(response.getData().getGoodsVos());
                                        adapter11.clear();
                                        adapter11.addAll(response.getData().getServiceGoodsVos());
                                        adapter22.clear();
                                        adapter22.addAll(response.getData().getHotGoodsVos());
                                        homeMenuAdapter.setHomeMenus(response.getData().getMenuVos());
                                        adsVosBeans = response.getData().getAdsVos();
                                        if (null != adsVosBeans) {
                                            images.clear();
                                            for (int i = 0; i < adsVosBeans.size(); i++) {
                                                images.add(adsVosBeans.get(i).getPic());
                                            }
                                            setmBanner(images);
                                        }
                                    } else {
                                        ToastUtils.shortToast(HealthcaseApplication.getApplication(), "");
                                    }
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            e.printStackTrace();
                            ToastUtils.shortToast(HealthcaseApplication.getApplication(), "获取数据失败～");
                            if (null != swipeRefreshLayout && swipeRefreshLayout.isRefreshing()) {
                                swipeRefreshLayout.setRefreshing(false);
                            }
                        }

                        @Override
                        public void onComplete() {

                        }
                    });

        } catch (Exception e) {

        }

    }

    private void destroyHomeDis() {
        if (null != homeDis && !homeDis.isDisposed()) {
            homeDis.dispose();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Event(GetTokenEvent getTokenEvent) {
        getToken();
    }

    private void destroyDis() {
        if (null != disposable && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    private void getToken() {
        destroyDis();
        ApiServiceFactory.getStringApiService()
                .getBaseInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @Override
                    public void onNext(String stringResponse) {
                        LogUtils.d(TAG, "stringResponse=" + stringResponse.toString());
                        try {
                            String result = ThreeDesUtils.decryptThreeDESECB(stringResponse.toString(),
                                    AppConfig.commonKey);


                            Gson gson = new Gson();
                            Type type = new TypeToken<Response<BaseInfo>>() {
                            }.getType();
                            Response<BaseInfo> response = gson.fromJson(result, type);
                            LogUtils.d(TAG, "result=" + response.toString());
                            if (response.getData() != null) {
                                sharedPrefsUtil.setString(Constants.USER_TOKEN, response.getData().getToken());
                                sharedPrefsUtil.setString(Constants.USER_SECRET_KEY, response.getData().getSecretKey());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //  sharedPrefsUtil.setString(Constants.USER_TOKEN,stringResponse.getToken());
                        //  sharedPrefsUtil.setString(Constants.USER_SECRET_KEY,stringResponse.getSecretKey());
                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onRefresh() {
        getHome();
    }

    public void showDialog(String updateInfo, final String apkUrl) {
        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setMessage(updateInfo)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        downloadAPK(apkUrl);
                        dialog.dismiss();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dialog.show();
    }

    /**
     * 下载apk的线程
     */
    public void downloadAPK(final String apkUrl) {
        if (TextUtils.isEmpty(apkUrl)) {
            return;
        }
        try {
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(apkUrl);
            intent.setData(content_url);
            getActivity().startActivity(intent);
        } catch (ActivityNotFoundException a) {
            a.getMessage();
        }

    }

    private String getCurrentVersionName(Context context) {
        try {
            return context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "";
        }
    }
}
