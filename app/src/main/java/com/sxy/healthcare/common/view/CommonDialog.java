package com.sxy.healthcare.common.view;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.sxy.healthcare.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * @Description: 通用dialog
 * @author ※简单※
 * @date  2016-11-23 15:40
 */
public class CommonDialog extends DialogFragment {


    private Unbinder mUnbinder;

    //根视图
    private View rootView;

    //提示信息
    @BindView(R.id.tv_message)
    TextView messageTv;

    //提示信息
    @BindView(R.id.et_desc)
    EditText editText;
    // 取消
    @BindView(R.id.btn_cancel)
    Button cancelBtn;

    // 确认
    @BindView(R.id.btn_confirm)
    Button confirmBtn;

    //取消事件
    private View.OnClickListener cancelListener;

    //确认事件
    private View.OnClickListener confirmListener;

    public static CommonDialog newInstance() {
        CommonDialog dialog = new CommonDialog();
        Bundle bundle = new Bundle();
        /*bundle.putString("message", message);
        bundle.putString("cancelText", cancelText);
        bundle.putString("confirmText", confirmText);*/
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
        rootView = inflater.inflate(R.layout.dialog_common, container, false);
        //noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mUnbinder = ButterKnife.bind(this,rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        Bundle bundle = getArguments();
       /* String message = bundle.getString("message");
        String cancelText = bundle.getString("cancelText");
        String confirmText = bundle.getString("confirmText");*/

       // messageTv.setText(message);

        /**
         * 取消按钮
         */
       // cancelBtn.setText(cancelText);
        if (cancelListener != null) {
            cancelBtn.setOnClickListener(cancelListener);
        }

        /**
         * 确认按钮
         */
       // confirmBtn.setText(confirmText);
        if (confirmListener != null) {
            confirmBtn.setOnClickListener(confirmListener);
        }
    }

    public void setCancelListener(View.OnClickListener cancelListener) {
        this.cancelListener = cancelListener;
    }

    public void setConfirmListener(View.OnClickListener confirmListener) {
        this.confirmListener = confirmListener;
    }
    public Button setClick(){
        return confirmBtn;
    }

    public String getDesc(){
        return editText.getText().toString();
    }
}
