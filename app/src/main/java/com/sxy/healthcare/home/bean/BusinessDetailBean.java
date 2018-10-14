package com.sxy.healthcare.home.bean;

import java.io.Serializable;
import java.util.Arrays;

public class BusinessDetailBean implements Serializable {

    private String address;
    private String addressXy;
    private String contactNo;
    private String id;
    private String onlineTime;
    private String pic;
    private String[] pics;
    private int state;
    private String traderName;

    private String context;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getAddressXy() {
        return addressXy;
    }

    public void setAddressXy(String addressXy) {
        this.addressXy = addressXy;
    }

    public String getContactNo() {
        return contactNo;
    }

    public void setContactNo(String contactNo) {
        this.contactNo = contactNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOnlineTime() {
        return onlineTime;
    }

    public void setOnlineTime(String onlineTime) {
        this.onlineTime = onlineTime;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String[] getPics() {
        return pics;
    }

    public void setPics(String[] pics) {
        this.pics = pics;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getTraderName() {
        return traderName;
    }

    public void setTraderName(String traderName) {
        this.traderName = traderName;
    }


    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @Override
    public String toString() {
        return "BusinessDetailBean{" +
                "address='" + address + '\'' +
                ", addressXy='" + addressXy + '\'' +
                ", contactNo='" + contactNo + '\'' +
                ", id='" + id + '\'' +
                ", onlineTime='" + onlineTime + '\'' +
                ", pic='" + pic + '\'' +
                ", pics=" + Arrays.toString(pics) +
                ", state=" + state +
                ", traderName='" + traderName + '\'' +
                '}';
    }
}
