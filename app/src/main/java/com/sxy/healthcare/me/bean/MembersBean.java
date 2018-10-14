package com.sxy.healthcare.me.bean;

public class MembersBean {

    private String mobile;

    private int level;

    private String nickName;

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    @Override
    public String toString() {
        return "MembersBean{" +
                "mobile='" + mobile + '\'' +
                '}';
    }
}
