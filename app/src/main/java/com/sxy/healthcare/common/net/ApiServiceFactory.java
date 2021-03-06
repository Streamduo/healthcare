package com.sxy.healthcare.common.net;

import android.content.Intent;
import android.util.Log;

import com.google.gson.Gson;
import com.sxy.healthcare.BuildConfig;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.utils.SharedPrefsUtil;
import com.sxy.healthcare.common.utils.ThreeDesUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.event.GetTokenEvent;
import com.sxy.healthcare.me.event.LoginOutEvent;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceFactory {

    private static ApiService apiService;

    private static ApiService stringApiService;

    private static Object mLock = new Object();


    public static ApiService getApiService() {
        synchronized (mLock) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .cache(null)
                    .sslSocketFactory(createSSLSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .readTimeout(10000, TimeUnit.MILLISECONDS)
                    .connectTimeout(10000, TimeUnit.MILLISECONDS);


            //  if(BuildConfig.DEBUG){
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(httpLoggingInterceptor);
            // }

            Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
            retrofitBuilder.baseUrl(BuildConfig.BASE_URL)
                    .client(builder.build())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

            apiService = retrofitBuilder.build().create(ApiService.class);
        }
        return apiService;
    }


    public static ApiService getStringApiService() {
        synchronized (mLock) {
            OkHttpClient.Builder builder = new OkHttpClient.Builder()
                    .retryOnConnectionFailure(true)
                    .cache(null)
                    .sslSocketFactory(createSSLSocketFactory())
                    .hostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            return true;
                        }
                    })
                    .readTimeout(100000, TimeUnit.MILLISECONDS)
                    .connectTimeout(100000, TimeUnit.MILLISECONDS);


            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
                httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
                builder.addInterceptor(httpLoggingInterceptor);
            }
            builder.addInterceptor(new TokenInterceptor());

            Retrofit.Builder retrofitBuilder = new Retrofit.Builder();
            retrofitBuilder.baseUrl(BuildConfig.BASE_URL)
                    .client(builder.build())
                    .addConverterFactory(StringConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create());

            stringApiService = retrofitBuilder.build().create(ApiService.class);
        }
        return stringApiService;
    }

    private static SSLSocketFactory createSSLSocketFactory() {
        SSLSocketFactory ssfFactory = null;

        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, new TrustManager[]{new TrustAllCerts()}, new SecureRandom());

            ssfFactory = sc.getSocketFactory();
        } catch (Exception e) {
        }

        return ssfFactory;
    }

    static class TokenInterceptor implements Interceptor {

        private SharedPrefsUtil sharedPrefsUtil = SharedPrefsUtil.getInstance(HealthcaseApplication.getApplication());

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = chain.proceed(request);
            Response useResponse = chain.proceed(request);
            try {
                String responsebody = response.body().string();
                String result = ThreeDesUtils.decryptThreeDESECB(responsebody,
                        sharedPrefsUtil.getString(Constants.USER_SECRET_KEY, ""));
                JSONObject jsonObject = new JSONObject(result);
                String code = jsonObject.getString("code");
                if (code.equals("00002")) {
                    EventBus.getDefault().post(new LoginOutEvent());
                    return null;
                }
                if (code.equals("00005") || code.equals("00006")) {
                    EventBus.getDefault().post(new GetTokenEvent());
                    return null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return useResponse;
        }
    }

}
