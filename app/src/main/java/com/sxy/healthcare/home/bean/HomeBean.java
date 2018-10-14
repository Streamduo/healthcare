package com.sxy.healthcare.home.bean;

import java.util.List;

public class HomeBean {

    private List<AdsVosBean> adsVos;

    private List<ServiceVosBean> goodsVos;

    private List<ServiceVosBean> menuVos;

    private List<ServiceVosBean> serviceVos;

    private List<VegetableBean> hotGoodsVos;

    private List<VegetableBean> serviceGoodsVos;


    public List<AdsVosBean> getAdsVos() {
        return adsVos;
    }

    public void setAdsVos(List<AdsVosBean> adsVos) {
        this.adsVos = adsVos;
    }

    public List<ServiceVosBean> getGoodsVos() {
        return goodsVos;
    }

    public void setGoodsVos(List<ServiceVosBean> goodsVos) {
        this.goodsVos = goodsVos;
    }

    public List<ServiceVosBean> getMenuVos() {
        return menuVos;
    }

    public void setMenuVos(List<ServiceVosBean> menuVos) {
        this.menuVos = menuVos;
    }

    public List<ServiceVosBean> getServiceVos() {
        return serviceVos;
    }

    public void setServiceVos(List<ServiceVosBean> serviceVos) {
        this.serviceVos = serviceVos;
    }

    public List<VegetableBean> getHotGoodsVos() {
        return hotGoodsVos;
    }

    public void setHotGoodsVos(List<VegetableBean> hotGoodsVos) {
        this.hotGoodsVos = hotGoodsVos;
    }

    public List<VegetableBean> getServiceGoodsVos() {
        return serviceGoodsVos;
    }

    public void setServiceGoodsVos(List<VegetableBean> serviceGoodsVos) {
        this.serviceGoodsVos = serviceGoodsVos;
    }

    @Override
    public String toString() {
        return "HomeBean{" +
                "adsVos=" + adsVos +
                ", goodsVos=" + goodsVos +
                ", menuVos=" + menuVos +
                ", serviceVos=" + serviceVos +
                ", hotGoodsVos=" + hotGoodsVos +
                ", serviceGoodsVos=" + serviceGoodsVos +
                '}';
    }
}
