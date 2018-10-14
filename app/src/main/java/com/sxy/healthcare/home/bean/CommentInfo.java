package com.sxy.healthcare.home.bean;

import java.util.List;

public class CommentInfo {

     private int pageNo;
    private int pageSize;
    private int count;
    private List<CommentBean> traderCommentVos;

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

    public List<CommentBean> getTraderCommentVos() {
        return traderCommentVos;
    }

    public void setTraderCommentVos(List<CommentBean> traderCommentVos) {
        this.traderCommentVos = traderCommentVos;
    }

    @Override
    public String toString() {
        return "CommentInfo{" +
                "pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                ", count=" + count +
                ", traderCommentVos=" + traderCommentVos +
                '}';
    }
}
