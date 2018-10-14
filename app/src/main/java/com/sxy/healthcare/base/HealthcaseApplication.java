package com.sxy.healthcare.base;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;
import com.tencent.bugly.crashreport.CrashReport;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class HealthcaseApplication extends Application {

    public static IWXAPI iwxapi;

    private static HealthcaseApplication application;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
        SDKInitializer.initialize(this);

        CrashReport.initCrashReport(getApplicationContext(), Constants.BUGLY_APPID, false);
    }

    public static IWXAPI getIwxapi() {
        if(iwxapi==null){
            iwxapi = WXAPIFactory.createWXAPI(getApplication(),AppConfig.WEI_XIN_APP_ID,true);
        }
        return iwxapi;
    }

    public static HealthcaseApplication getApplication() {
        return application;
    }
}
