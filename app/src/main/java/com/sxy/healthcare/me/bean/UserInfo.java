package com.sxy.healthcare.me.bean;

import com.sxy.healthcare.me.activity.ProfileReserveActivity;

import java.io.Serializable;

public class UserInfo  implements Serializable{
    private static final long serialVerisionUID = 1L ;

    private String userId;
    private String balance;
    private String cardNo;
    private String mobile;
    private String birthday;
    private String headImg;
    private String inviter;
    private String nickName;
    private String sex;
    private String firstRecharge;

    public String getFirstRecharge() {
        return firstRecharge;
    }

    public void setFirstRecharge(String firstRecharge) {
        this.firstRecharge = firstRecharge;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getHeadImg() {
        return headImg;
    }

    public void setHeadImg(String headImg) {
        this.headImg = headImg;
    }

    public String getInviter() {
        return inviter;
    }

    public void setInviter(String inviter) {
        this.inviter = inviter;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userId='" + userId + '\'' +
                ", balance='" + balance + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", mobile='" + mobile + '\'' +
                ", birthday='" + birthday + '\'' +
                ", headImg='" + headImg + '\'' +
                ", inviter='" + inviter + '\'' +
                ", nickName='" + nickName + '\'' +
                ", sex='" + sex + '\'' +
                '}';
    }
}
