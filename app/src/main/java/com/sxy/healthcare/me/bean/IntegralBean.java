package com.sxy.healthcare.me.bean;

public class IntegralBean {


    private boolean available;

    private boolean selected;

    private String desc;

    private int id;

    private float price;

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return "IntegralBean{" +
                "available=" + available +
                ", selected=" + selected +
                ", desc='" + desc + '\'' +
                ", id=" + id +
                ", price=" + price +
                '}';
    }
}
