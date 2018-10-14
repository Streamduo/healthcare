package com.sxy.healthcare.me.bean;

import java.util.List;

public class IntegralInfo {

    private int code;
    private boolean success;
    private String msg;
    private List<IntegralBean> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<IntegralBean> getData() {
        return data;
    }

    public void setData(List<IntegralBean> data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "IntegralInfo{" +
                "code=" + code +
                ", success=" + success +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
