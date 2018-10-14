package com.sxy.healthcare.me.bean;

public class SummaryVo {

    private double avgCost;
    private String bookNo;
    private String bookTime;
    private int num;
    private int orderType;
    private double realPrice;
    private int state;

    private String createTime;

    public double getAvgCost() {
        return avgCost;
    }

    public void setAvgCost(double avgCost) {
        this.avgCost = avgCost;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getBookTime() {
        return bookTime;
    }

    public void setBookTime(String bookTime) {
        this.bookTime = bookTime;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    public double getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(double realPrice) {
        this.realPrice = realPrice;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "SummaryVo{" +
                "avgCost=" + avgCost +
                ", bookNo='" + bookNo + '\'' +
                ", bookTime='" + bookTime + '\'' +
                ", num=" + num +
                ", orderType=" + orderType +
                ", realPrice=" + realPrice +
                ", state=" + state +
                '}';
    }
}
