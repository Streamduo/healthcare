package com.sxy.healthcare.home.bean;

import java.io.Serializable;
import java.util.List;

public class BusinessBean implements Serializable{

    private List<EntertainmentVosBean> entertainmentVos;

    private List<CookerBean> cookerVos;
    private List<GoodsCuisinesBean> goodsCuisines;

    private BusinessDetailBean traderDetail;


    public List<CookerBean> getCookerVos() {
        return cookerVos;
    }

    public void setCookerVos(List<CookerBean> cookerVos) {
        this.cookerVos = cookerVos;
    }

    public List<GoodsCuisinesBean> getGoodsCuisines() {
        return goodsCuisines;
    }

    public void setGoodsCuisines(List<GoodsCuisinesBean> goodsCuisines) {
        this.goodsCuisines = goodsCuisines;
    }

    public BusinessDetailBean getTraderDetail() {
        return traderDetail;
    }

    public void setTraderDetail(BusinessDetailBean traderDetail) {
        this.traderDetail = traderDetail;
    }


    public List<EntertainmentVosBean> getEntertainmentVos() {
        return entertainmentVos;
    }

    public void setEntertainmentVos(List<EntertainmentVosBean> entertainmentVos) {
        this.entertainmentVos = entertainmentVos;
    }

    @Override
    public String toString() {
        return "BusinessBean{" +
                "entertainmentVos=" + entertainmentVos +
                ", cookerVos=" + cookerVos +
                ", goodsCuisines=" + goodsCuisines +
                ", traderDetail=" + traderDetail +
                '}';
    }
}
