package com.sxy.healthcare.me.bean;


import java.util.List;

public class DealBean {

    private int pageNo;

    private int pageSize;

    private int count;

    private List<ChangesVosBean> changesVos;

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

    public List<ChangesVosBean> getChangesVos() {
        return changesVos;
    }

    public void setChangesVos(List<ChangesVosBean> changesVos) {
        this.changesVos = changesVos;
    }

    @Override
    public String toString() {
        return "DealBean{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", count=" + count +
                ", changesVos=" + changesVos +
                '}';
    }
}
