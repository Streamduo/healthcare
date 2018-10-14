package com.sxy.healthcare.me.event;

import android.icu.math.BigDecimal;

public class PayEvent {

    private int payType;

    private double price;

    public int getPayType() {
        return payType;
    }

    public void setPayType(int payType) {
        this.payType = payType;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
