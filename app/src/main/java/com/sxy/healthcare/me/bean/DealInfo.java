package com.sxy.healthcare.me.bean;

public class DealInfo {

    private boolean success;

    private int code;

    private String msg;

    private DealBean data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DealBean getData() {
        return data;
    }

    public void setData(DealBean data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "DealInfo{" +
                "success=" + success +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
