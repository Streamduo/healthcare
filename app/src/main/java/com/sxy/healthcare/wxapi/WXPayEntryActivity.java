package com.sxy.healthcare.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.sxy.healthcare.R;
import com.sxy.healthcare.base.AppConfig;
import com.sxy.healthcare.base.Constants;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.event.PayEvent;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = WXPayEntryActivity.class.getSimpleName();



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);

        HealthcaseApplication.getIwxapi().handleIntent(getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        HealthcaseApplication.getIwxapi().handleIntent(getIntent(),this);
    }

    @Override
    public void onReq(BaseReq req) {
        LogUtils.d(TAG,"[onReq] errcode=");
    }

    @Override
    public void onResp(BaseResp resp) {
        LogUtils.d(TAG, "onPayFinish, errCode = " + resp.errCode);

       // ToastUtils.shortToast(getApplicationContext(),resp.errCode);

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {

            if(resp.errCode==0){
                ToastUtils.show(getApplicationContext(),"支付成功", Toast.LENGTH_SHORT);
                PayEvent payEvent = new PayEvent();
                payEvent.setPayType(1);
                EventBus.getDefault().post(payEvent);

            }else {
                ToastUtils.show(getApplicationContext(),"支付失败", Toast.LENGTH_SHORT);
            }
          //  WXPayEntryActivity.this.finish();
        }

        WXPayEntryActivity.this.finish();
    }
}
