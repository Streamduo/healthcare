package com.sxy.healthcare.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.sxy.healthcare.R;
import com.sxy.healthcare.base.HealthcaseApplication;
import com.sxy.healthcare.common.utils.LogUtils;
import com.sxy.healthcare.common.utils.ToastUtils;
import com.sxy.healthcare.me.event.PayEvent;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = WXEntryActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wxentry);
        HealthcaseApplication.getIwxapi().handleIntent(getIntent(),this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        LogUtils.d(TAG,"[onReq] errcode=");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        int result = 0;
        LogUtils.d(TAG,"[onResp] errcode="+baseResp.errCode+",msg="+baseResp.errStr);
        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_OK:
                result = R.string.errcode_success;
                break;
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                result = R.string.errcode_cancel;
                break;
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                result = R.string.errcode_deny;
                break;
            case BaseResp.ErrCode.ERR_UNSUPPORT:
                result = R.string.errcode_unsupported;
                break;
            default:
                result = R.string.errcode_unknown;
                break;
        }



        ToastUtils.show(getApplicationContext(),result, Toast.LENGTH_SHORT);
        WXEntryActivity.this.finish();
    }
}
