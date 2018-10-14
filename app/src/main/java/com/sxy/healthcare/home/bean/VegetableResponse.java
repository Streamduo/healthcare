package com.sxy.healthcare.home.bean;

public class VegetableResponse {

    private VegetableInfo data;
    private boolean success;

    public VegetableInfo getData() {
        return data;
    }

    public void setData(VegetableInfo data) {
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
        return "VegetableResponse{" +
                "data=" + data +
                ", success=" + success +
                '}';
    }
}
