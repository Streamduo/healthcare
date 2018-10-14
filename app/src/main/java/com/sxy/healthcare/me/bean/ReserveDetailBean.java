package com.sxy.healthcare.me.bean;

import com.sxy.healthcare.home.bean.BusinessDetailBean;
import com.sxy.healthcare.home.bean.CookerBean;

import java.util.List;

public class ReserveDetailBean {

    private List<BookingGoodsVos> bookingGoodsVos;

    private  CookerBean cookerVo;

    private String[] goodsCuisines;

    private SummaryVo summaryVo;

    private BusinessDetailBean traderDetailVo;


    public List<BookingGoodsVos> getBookingGoodsVos() {
        return bookingGoodsVos;
    }

    public void setBookingGoodsVos(List<BookingGoodsVos> bookingGoodsVos) {
        this.bookingGoodsVos = bookingGoodsVos;
    }


    public CookerBean getCookerVo() {
        return cookerVo;
    }

    public void setCookerVo(CookerBean cookerVo) {
        this.cookerVo = cookerVo;
    }

    public String[] getGoodsCuisines() {
        return goodsCuisines;
    }

    public void setGoodsCuisines(String[] goodsCuisines) {
        this.goodsCuisines = goodsCuisines;
    }

    public SummaryVo getSummaryVo() {
        return summaryVo;
    }

    public void setSummaryVo(SummaryVo summaryVo) {
        this.summaryVo = summaryVo;
    }

    public BusinessDetailBean getTraderDetailVo() {
        return traderDetailVo;
    }

    public void setTraderDetailVo(BusinessDetailBean traderDetailVo) {
        this.traderDetailVo = traderDetailVo;
    }
}
