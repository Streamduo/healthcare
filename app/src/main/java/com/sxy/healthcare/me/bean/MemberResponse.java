package com.sxy.healthcare.me.bean;

public class MemberResponse {

    private int code;
    private boolean success;
    private String msg;
    private MembersInfo data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public MembersInfo getData() {
        return data;
    }

    public void setData(MembersInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "MemberResponse{" +
                "code=" + code +
                ", success=" + success +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
