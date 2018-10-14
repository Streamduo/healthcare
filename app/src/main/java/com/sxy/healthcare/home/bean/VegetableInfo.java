package com.sxy.healthcare.home.bean;

import java.util.List;

public class VegetableInfo {

    private int count;
    private List<VegetableBean> goodsVos;
    private int pageNo;
    private int pageSize;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<VegetableBean> getGoodsVos() {
        return goodsVos;
    }

    public void setGoodsVos(List<VegetableBean> goodsVos) {
        this.goodsVos = goodsVos;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    @Override
    public String toString() {
        return "VegetableInfo{" +
                "count=" + count +
                ", goodsVos=" + goodsVos +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                '}';
    }
}
