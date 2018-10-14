package com.sxy.healthcare.cart.bean;

public class GoodsBean {

    private boolean isSelect;
    private String id;
    private String pic;
    private String goodsName;
    private String price;
    private String unit;
    private int quantity;

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    @Override
    public String toString() {
        return "GoodsBean{" +
                "isSelect=" + isSelect +
                ", id='" + id + '\'' +
                ", pic='" + pic + '\'' +
                ", goodsName='" + goodsName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}
