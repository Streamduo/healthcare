package com.sxy.healthcare.me.bean;

public class OrderDetailBean {


    private String id;

    private String content;

    private String traderId;

    private String price;

    private String orderTime;

    private int isTra;

    private String goodsId;

    private String goodsImg;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public int getIsTra() {
        return isTra;
    }

    public void setIsTra(int isTra) {
        this.isTra = isTra;
    }

    public String getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(String goodsId) {
        this.goodsId = goodsId;
    }


    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGoodsImg() {
        return goodsImg;
    }

    public void setGoodsImg(String goodsImg) {
        this.goodsImg = goodsImg;
    }

    @Override
    public String toString() {
        return "OrderDetailBean{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", traderId='" + traderId + '\'' +
                ", price=" + price +
                ", orderTime='" + orderTime + '\'' +
                ", isTra=" + isTra +
                ", goodsId='" + goodsId + '\'' +
                '}';
    }
}
