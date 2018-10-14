package com.sxy.healthcare.home.bean;

public class HomeInfo {

    private HomeBean data;

    private boolean success;

    public HomeBean getData() {
        return data;
    }

    public void setData(HomeBean data) {
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
        return "HomeInfo{" +
                "data=" + data +
                ", success=" + success +
                '}';
    }
}
