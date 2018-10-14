package com.sxy.healthcare.home.bean;

import java.util.List;

public class TraderInfo {
    private int count;
    private int pageNo;
    private int pageSize;
    private List<TraderBean> traders;

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
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

    public List<TraderBean> getTraders() {
        return traders;
    }

    public void setTraders(List<TraderBean> traders) {
        this.traders = traders;
    }

    @Override
    public String toString() {
        return "TraderInfo{" +
                "count=" + count +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", traders=" + traders +
                '}';
    }
}
