package com.sxy.healthcare.me.bean;

public class PayInfo {

    private WxPayBean data;

    private boolean success;

    public WxPayBean getData() {
        return data;
    }

    public void setData(WxPayBean data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "PayInfo{" +
                "data=" + data +
                ", success=" + success +
                '}';
    }
}
