package com.sxy.healthcare.home.bean;

import java.util.List;

public class CookerInfo {

    private List<CookerBean> cookerVos;

    private TraderBean traderDetail;


    public List<CookerBean> getCookerVos() {
        return cookerVos;
    }

    public void setCookerVos(List<CookerBean> cookerVos) {
        this.cookerVos = cookerVos;
    }

    public TraderBean getTraderDetail() {
        return traderDetail;
    }

    public void setTraderDetail(TraderBean traderDetail) {
        this.traderDetail = traderDetail;
    }

    @Override
    public String toString() {
        return "CookerInfo{" +
                "cookerVos=" + cookerVos +
                ", traderDetail=" + traderDetail +
                '}';
    }
}
