package com.sxy.healthcare.home.bean;

public class VersionInfo {
    private VersionBean data;

    private boolean success;

    public VersionBean getData() {
        return data;
    }

    public void setData(VersionBean data) {
        this.data = data;
    }


    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    @Override
    public String toString() {
        return "VersionInfo{" +
                "data=" + data +
                ", success=" + success +
                '}';
    }
}
