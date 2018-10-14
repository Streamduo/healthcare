package com.sxy.healthcare.home.bean;

public class ServiceGoodsVos {

    private String aname;
    private String category;
    private String createTime;
    private String id;
    private int isHot;
    private String pic;
    private int plate;
    private int ttype;
    private String updateTime;
    private String price;

    private String traderId;

    private String tabType;

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

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
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

    public String getTraderId() {
        return traderId;
    }

    public void setTraderId(String traderId) {
        this.traderId = traderId;
    }

    public String getTabType() {
        return tabType;
    }

    public void setTabType(String tabType) {
        this.tabType = tabType;
    }

    @Override
    public String toString() {
        return "ServiceGoodsVos{" +
                "aname='" + aname + '\'' +
                ", category='" + category + '\'' +
                ", createTime='" + createTime + '\'' +
                ", id='" + id + '\'' +
                ", isHot=" + isHot +
                ", pic='" + pic + '\'' +
                ", plate=" + plate +
                ", ttype=" + ttype +
                ", updateTime='" + updateTime + '\'' +
                ", price='" + price + '\'' +
                ", traderId='" + traderId + '\'' +
                ", tabType='" + tabType + '\'' +
                '}';
    }
}
