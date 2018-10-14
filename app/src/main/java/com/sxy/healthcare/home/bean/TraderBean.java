package com.sxy.healthcare.home.bean;

import java.io.Serializable;

public class TraderBean implements Serializable{

    private static final long serialVerisionUID = 1L ;

    private String traderId;
    private String traderName;
    private String pic;

    private int state;

    private String contactNo;
    private String onlineTime;


    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
    }

    @Override
    public String toString() {
        return "TraderBean{" +
                "traderId='" + traderId + '\'' +
                ", traderName='" + traderName + '\'' +
                ", pic='" + pic + '\'' +
                ", state=" + state +
                '}';
    }
}
