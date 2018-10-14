package com.sxy.healthcare.me.bean;

import java.io.Serializable;
import java.lang.ref.PhantomReference;

public class BookingBean implements Serializable{
    private String bookDesc;
    private String bookNo;
    private String id;
    private String mainPic;
    private String realPrice;
    private int state;

    private int orderType;


    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    public String getBookNo() {
        return bookNo;
    }

    public void setBookNo(String bookNo) {
        this.bookNo = bookNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMainPic() {
        return mainPic;
    }

    public void setMainPic(String mainPic) {
        this.mainPic = mainPic;
    }

    public String getRealPrice() {
        return realPrice;
    }

    public void setRealPrice(String realPrice) {
        this.realPrice = realPrice;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }


    public int getOrderType() {
        return orderType;
    }

    public void setOrderType(int orderType) {
        this.orderType = orderType;
    }

    @Override
    public String toString() {
        return "BookingBean{" +
                "bookDesc='" + bookDesc + '\'' +
                ", bookNo='" + bookNo + '\'' +
                ", id='" + id + '\'' +
                ", mainPic='" + mainPic + '\'' +
                ", realPrice='" + realPrice + '\'' +
                ", state='" + state + '\'' +
                '}';
    }
}
