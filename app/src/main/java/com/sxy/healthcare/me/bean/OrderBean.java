package com.sxy.healthcare.me.bean;

import java.io.Serializable;

public class OrderBean implements Serializable{

    private String id;

    private String orderId;

    private String orderDesc;

    private String mainPic;

    private String orderUser;

    private String orderStatus;

    private String jsStatus;

    private String payType;

    private float realPrice;

    private String createTime;

     private String orderType;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getMainPic() {
        return mainPic;
    }

    public void setMainPic(String mainPic) {
        this.mainPic = mainPic;
    }

    public String getOrderUser() {
        return orderUser;
    }

    public void setOrderUser(String orderUser) {
        this.orderUser = orderUser;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getJsStatus() {
        return jsStatus;
    }

    public void setJsStatus(String jsStatus) {
        this.jsStatus = jsStatus;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public float getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(float realPrice) {
        this.realPrice = realPrice;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }


    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "OrderBean{" +
                "id='" + id + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderDesc='" + orderDesc + '\'' +
                ", mainPic='" + mainPic + '\'' +
                ", orderUser='" + orderUser + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", jsStatus='" + jsStatus + '\'' +
                ", payType='" + payType + '\'' +
                ", realPrice=" + realPrice +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}


