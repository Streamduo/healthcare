package com.sxy.healthcare.me.bean;

public class BookingGoodsVos {

    private String goodsName;

    private int id;

    private int num;

    private String pic;

    private String price;

    private String unit;

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "BookingGoodsVos{" +
                "goodsName='" + goodsName + '\'' +
                ", id='" + id + '\'' +
                ", num='" + num + '\'' +
                ", pic='" + pic + '\'' +
                ", price='" + price + '\'' +
                ", unit='" + unit + '\'' +
                '}';
    }
}
