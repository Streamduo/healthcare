package com.sxy.healthcare.home.bean;

public class TraderResponse {
    private boolean success;
    private TraderInfo data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public TraderInfo getData() {
        return data;
    }

    public void setData(TraderInfo data) {
        this.data = data;
    }
}
