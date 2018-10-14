package com.sxy.healthcare.me.dialog;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.sxy.healthcare.R;
import com.sxy.healthcare.common.utils.ScreenUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class PayDialog extends DialogFragment {

    private Unbinder mUnbinder;

    //根视图
    private View rootView;

    @BindView(R.id.btn_jf_and_wx_pay)
    TextView btnJFWx;

    @BindView(R.id.btn_jf_pay)
    TextView btnJF;

    @BindView(R.id.btn_jf_al_pay)
    TextView btnJfAl;

    @BindView(R.id.btn_wx_pay)
    TextView btnWx;

    @BindView(R.id.btn_al_pay)
    TextView btnAl;

    @BindView(R.id.btn_cancel)
    TextView btnCancel;


    private View.OnClickListener wxListener;


    private View.OnClickListener alListener;

    private View.OnClickListener wxjfListener;


    private View.OnClickListener aljfListener;

    private View.OnClickListener jfListener;


    private View.OnClickListener cancelListener;

    private static int mType;



    public static PayDialog newInstance(int type) {
        PayDialog dialog = new PayDialog();
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
        mType = type;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(true);
        setStyle(DialogFragment.STYLE_NO_TITLE, 0);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        WindowManager.LayoutParams params = getDialog().getWindow()
                .getAttributes();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        getDialog().getWindow().setAttributes(params);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        rootView = inflater.inflate(R.layout.dialog_pay, container, false);
        //noinspection ConstantConditions

        mUnbinder = ButterKnife.bind(this,rootView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ScreenUtils.getScreenWidth(getContext()), WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        Bundle bundle = getArguments();
        if(mType==2){
            btnJF.setVisibility(View.GONE);
            btnJfAl.setVisibility(View.GONE);
            btnJFWx.setVisibility(View.GONE);
        }

        if (btnJF != null) {
            btnJF.setOnClickListener(jfListener);
        }

        if (btnJFWx != null) {
            btnJFWx.setOnClickListener(wxjfListener);
        }

        if (btnJfAl != null) {
            btnJfAl.setOnClickListener(aljfListener);
        }

        if (btnWx != null) {
            btnWx.setOnClickListener(wxListener);
        }

        if (btnAl != null) {
            btnAl.setOnClickListener(alListener);
        }

        if (btnCancel != null) {
            btnCancel.setOnClickListener(cancelListener);
        }


    }

    public void setWxListener(View.OnClickListener wxListener) {
        this.wxListener = wxListener;
    }

    public void setWxJFListener(View.OnClickListener wxJFListener) {
        this.wxjfListener = wxJFListener;
    }

    public void setJFListener(View.OnClickListener jfListener) {
        this.jfListener = jfListener;
    }

    public void setJFalListener(View.OnClickListener aljfListener) {
        this.aljfListener = aljfListener;
    }

    public void setAlListener(View.OnClickListener alListener) {
        this.alListener = alListener;
    }

    public void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

}
