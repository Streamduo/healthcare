package com.sxy.healthcare.me.bean;

public class OrderDetailResponse {

    private boolean success;

    private OrderDetailInfo data;


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public OrderDetailInfo getData() {
        return data;
    }

    public void setData(OrderDetailInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "OrderDetailResponse{" +
                "success=" + success +
                ", data=" + data +
                '}';
    }
}
