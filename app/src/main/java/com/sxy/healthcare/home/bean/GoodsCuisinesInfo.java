package com.sxy.healthcare.home.bean;

import java.util.List;
import java.util.ListIterator;

public class GoodsCuisinesInfo {

    private List<GoodsCuisinesBean> data;
    private boolean success;

    public List<GoodsCuisinesBean> getData() {
        return data;
    }

    public void setData(List<GoodsCuisinesBean> data) {
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
        return "GoodsCuisinesInfo{" +
                "data=" + data +
                ", success=" + success +
                '}';
    }
}
