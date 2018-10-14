package com.sxy.healthcare.me.bean;

public class BookingResponse {

    private BookingInfo data;

    private boolean success;

    public BookingInfo getData() {
        return data;
    }

    public void setData(BookingInfo data) {
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
        return "BookingResponse{" +
                "data=" + data +
                ", success=" + success +
                '}';
    }
}
