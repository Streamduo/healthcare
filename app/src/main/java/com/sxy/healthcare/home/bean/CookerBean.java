package com.sxy.healthcare.home.bean;

import java.io.Serializable;

public class CookerBean implements Serializable {

    private String cookerName;
    private int id;

    private String serviceCharge;

    private String star;

    private boolean isSelected;

    public String getCookerName() {
        return cookerName;
    }

    public void setCookerName(String cookerName) {
        this.cookerName = cookerName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(String serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public String getStar() {
        return star;
    }

    public void setStar(String star) {
        this.star = star;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    @Override
    public String toString() {
        return "CookerBean{" +
                "cookerName='" + cookerName + '\'' +
                ", id=" + id +
                ", serviceCharge='" + serviceCharge + '\'' +
                ", star='" + star + '\'' +
                '}';
    }
}
