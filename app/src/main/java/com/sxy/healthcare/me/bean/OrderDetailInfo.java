package com.sxy.healthcare.me.bean;

import java.util.List;

public class OrderDetailInfo {

    private String bookTime;
    private int commendStatus;

    private String createTime;

    private int id;

    private int jsStatus;

    private String mainPic;

    private String orderDesc;

    private String orderId;

    private int orderStatus;

    private String orderUser;

    private float realPrice;

    private String traderId;

    private int orderType;

    public String getBookTime() {
        return bookTime;
    }

    public void setBookTime(String bookTime) {
        this.bookTime = bookTime;
    }

    private List<OrderDetailBean> ordersDetailVos;

    public int getCommendStatus() {
        return commendStatus;
    }

    public void setCommendStatus(int commendStatus) {
        this.commendStatus = commendStatus;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getJsStatus() {
        return jsStatus;
    }

    public void setJsStatus(int jsStatus) {
        this.jsStatus = jsStatus;
    }

    public String getMainPic() {
        return mainPic;
    }

    public void setMainPic(String mainPic) {
        this.mainPic = mainPic;
    }

    public String getOrderDesc() {
        return orderDesc;
    }

    public void setOrderDesc(String orderDesc) {
        this.orderDesc = orderDesc;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(int orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderUser() {
        return orderUser;
    }

    public void setOrderUser(String orderUser) {
        this.orderUser = orderUser;
    }

    public float getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(float realPrice) {
        this.realPrice = realPrice;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public List<OrderDetailBean> getOrdersDetailVos() {
        return ordersDetailVos;
    }

    public void setOrdersDetailVos(List<OrderDetailBean> ordersDetailVos) {
        this.ordersDetailVos = ordersDetailVos;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "OrderDetailInfo{" +
                "commendStatus=" + commendStatus +
                ", createTime='" + createTime + '\'' +
                ", id=" + id +
                ", jsStatus=" + jsStatus +
                ", mainPic='" + mainPic + '\'' +
                ", orderDesc='" + orderDesc + '\'' +
                ", orderId='" + orderId + '\'' +
                ", orderStatus=" + orderStatus +
                ", orderUser='" + orderUser + '\'' +
                ", realPrice=" + realPrice +
                ", traderId='" + traderId + '\'' +
                ", ordersDetailVos=" + ordersDetailVos +
                '}';
    }
}
