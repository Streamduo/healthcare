package com.sxy.healthcare.home.bean;

public class BusinessInfo {
    private BusinessBean data;
    private boolean success;

    public BusinessBean getData() {
        return data;
    }

    public void setData(BusinessBean data) {
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
        return "BusinessInfo{" +
                "data=" + data +
                ", success=" + success +
                '}';
    }
}
