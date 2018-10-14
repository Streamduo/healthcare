package com.sxy.healthcare.common.event;

import com.sxy.healthcare.cart.bean.GoodsBean;

import java.util.List;

public class ShopEvent {

    private List<GoodsBean> goodsBeans;

    public List<GoodsBean> getGoodsBeans() {
        return goodsBeans;
    }

    public void setGoodsBeans(List<GoodsBean> goodsBeans) {
        this.goodsBeans = goodsBeans;
    }
}
