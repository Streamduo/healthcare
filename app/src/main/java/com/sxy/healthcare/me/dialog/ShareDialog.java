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


public class ShareDialog extends DialogFragment {


    private Unbinder mUnbinder;

    //根视图
    private View rootView;

    @BindView(R.id.btn_share_wx)
    Button btnShareWx;

    @BindView(R.id.btn_share_moment)
    Button btnShareMoment;



    private View.OnClickListener wxListener;


    private View.OnClickListener momentListener;

    public static ShareDialog newInstance() {
        ShareDialog dialog = new ShareDialog();
        Bundle bundle = new Bundle();
        dialog.setArguments(bundle);
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

        rootView = inflater.inflate(R.layout.dialog_share, container, false);
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

        if (btnShareWx != null) {
            btnShareWx.setOnClickListener(wxListener);
        }


        if (btnShareMoment != null) {
            btnShareMoment.setOnClickListener(momentListener);
        }
    }

    public void setWxListener(View.OnClickListener wxListener) {
        this.wxListener = wxListener;
    }

    public void setMomentListener(View.OnClickListener momentListener) {
        this.momentListener = momentListener;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}