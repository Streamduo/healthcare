package com.sxy.healthcare.me.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.sxy.healthcare.R;
import com.sxy.healthcare.common.utils.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class OrderSettleDialog extends DialogFragment {
    private Unbinder mUnbinder;

    //根视图
    private View rootView;

    //确认事件
    private ConfirmListener confirmListener;

    private ArrayAdapter adapter;

    @BindView(R.id.btn_js)
    Button btnJS;

    @BindView(R.id.et_pay)
    EditText editText;

    @BindView(R.id.sp_js_type)
    Spinner spinner;
    private int selectpos;

    public static OrderSettleDialog newInstance() {
        OrderSettleDialog dialog = new OrderSettleDialog();
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
        rootView = inflater.inflate(R.layout.dialog_order_settle, container, false);
        //noinspection ConstantConditions
        //getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mUnbinder = ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);

        Bundle bundle = getArguments();
        btnJS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                //隐藏软键盘
                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                int i = Integer.parseInt(getPay());
                if (i>0){
                    confirmListener.OnSelctedClick(selectpos, i);
                }else {
                    ToastUtils.shortToast(getContext(),"消费金额不能为零");
                }

            }
        });

        //将可选内容与ArrayAdapter连接起来
        adapter = ArrayAdapter.createFromResource(getActivity(), R.array.js_type, android.R.layout.simple_spinner_item);

        //设置下拉列表的风格
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        //将adapter2 添加到spinner中
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectpos = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    public void setConfirmListener(ConfirmListener confirmListener) {
        this.confirmListener = confirmListener;
    }

    public interface ConfirmListener {
        void OnSelctedClick(int position, int price);
    }

    public ArrayAdapter getAdapter() {
        return adapter;
    }

    public String getPay() {
        return editText.getText().toString();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }
}
