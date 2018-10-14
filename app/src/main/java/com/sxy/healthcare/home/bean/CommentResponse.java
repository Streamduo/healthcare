package com.sxy.healthcare.home.bean;

public class CommentResponse {

    private boolean success;
    private int code;
    private String msg;
    private CommentInfo data;

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

    public CommentInfo getData() {
        return data;
    }

    public void setData(CommentInfo data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "CommentResponse{" +
                "success=" + success +
                ", code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
