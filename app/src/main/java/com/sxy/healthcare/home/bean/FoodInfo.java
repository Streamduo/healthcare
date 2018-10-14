package com.sxy.healthcare.home.bean;

public class FoodInfo {

    private CookerInfo data;

    private boolean success;


    public CookerInfo getData() {
        return data;
    }

    public void setData(CookerInfo data) {
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
        return "FoodInfo{" +
                "data=" + data +
                ", success=" + success +
                '}';
    }
}
