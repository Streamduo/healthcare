package com.sxy.healthcare.home.bean;

import java.io.Serializable;

public class VegetableBean implements Serializable{

    private String aname;
    private String category;
    private String cooker;
    private String createTime;
    private String cuisines;
    private String id;
    private int isHot;
    private String pic;
    private String pic2;
    private int plate;
    private int ttype;
    private String updateTime;
    private String price;
    private String content;
    private String traderId;
    private String unit;

    private int currentNum;

    private boolean selected;


    public String getAname() {
        return aname;
    }

    public void setAname(String aname) {
        this.aname = aname;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCooker() {
        return cooker;
    }

    public void setCooker(String cooker) {
        this.cooker = cooker;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCuisines() {
        return cuisines;
    }

    public void setCuisines(String cuisines) {
        this.cuisines = cuisines;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getIsHot() {
        return isHot;
    }

    public void setIsHot(int isHot) {
        this.isHot = isHot;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public int getPlate() {
        return plate;
    }

    public void setPlate(int plate) {
        this.plate = plate;
    }

    public int getTtype() {
        return ttype;
    }

    public void setTtype(int ttype) {
        this.ttype = ttype;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getPic2() {
        return pic2;
    }

    public void setPic2(String pic2) {
        this.pic2 = pic2;
    }


    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public int getCurrentNum() {
        return currentNum;
    }

    public void setCurrentNum(int currentNum) {
        this.currentNum = currentNum;
    }

    @Override
    public String toString() {
        return "VegetableBean{" +
                "aname='" + aname + '\'' +
                ", category='" + category + '\'' +
                ", cooker='" + cooker + '\'' +
                ", createTime='" + createTime + '\'' +
                ", cuisines='" + cuisines + '\'' +
                ", id='" + id + '\'' +
                ", isHot=" + isHot +
                ", pic='" + pic + '\'' +
                ", plate=" + plate +
                ", ttype=" + ttype +
                ", updateTime='" + updateTime + '\'' +
                ", price='" + price + '\'' +
                '}';
    }
}
