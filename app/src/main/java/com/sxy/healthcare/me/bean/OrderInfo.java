package com.sxy.healthcare.me.bean;

import java.util.List;

public class OrderInfo {

    private int pageNo;
    private int pageSize;

    private int count;

    private List<OrderBean> ordersMainVos;

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<OrderBean> getOrdersMainVos() {
        return ordersMainVos;
    }

    public void setOrdersMainVos(List<OrderBean> ordersMainVos) {
        this.ordersMainVos = ordersMainVos;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", count=" + count +
                ", ordersMainVos=" + ordersMainVos +
                '}';
    }
}
