package com.sxy.healthcare.base.bean;

import com.sxy.healthcare.common.net.Response;
import com.sxy.healthcare.me.activity.ProfileReserveActivity;

public class BaseInfo {

    private String token;

    private String secretKey;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    @Override
    public String toString() {
        return "BaseInfo{" +
                "token='" + token + '\'' +
                ", secretKey='" + secretKey + '\'' +
                '}';
    }
}

