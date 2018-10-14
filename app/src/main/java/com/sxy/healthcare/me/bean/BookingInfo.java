package com.sxy.healthcare.me.bean;

import java.util.List;

public class BookingInfo {

    private List<BookingBean> bookingMainVos;

    private int count;

    private int pageNo;

    private int pageSize;

    public List<BookingBean> getBookingMainVos() {
        return bookingMainVos;
    }

    public void setBookingMainVos(List<BookingBean> bookingMainVos) {
        this.bookingMainVos = bookingMainVos;
    }

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

    @Override
    public String toString() {
        return "BookingInfo{" +
                "bookingMainVos=" + bookingMainVos +
                ", count=" + count +
                ", pageNo=" + pageNo +
                ", pageSize=" + pageSize +
                '}';
    }
}
