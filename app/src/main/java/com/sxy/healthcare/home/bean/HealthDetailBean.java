package com.sxy.healthcare.home.bean;

public class HealthDetailBean {

    private VegetableBean data;
    private boolean success;


    public VegetableBean getData() {
        return data;
    }

    public void setData(VegetableBean data) {
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
        return "HealthDetailBean{" +
                "data=" + data +
                ", success=" + success +
                '}';
    }
}
