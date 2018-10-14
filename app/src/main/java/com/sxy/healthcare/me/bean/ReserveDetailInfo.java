package com.sxy.healthcare.me.bean;

public class ReserveDetailInfo {

    private ReserveDetailBean data;

    private boolean success;


    public ReserveDetailBean getData() {
        return data;
    }

    public void setData(ReserveDetailBean data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
