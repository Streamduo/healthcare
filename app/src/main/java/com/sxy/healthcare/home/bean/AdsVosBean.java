package com.sxy.healthcare.home.bean;

public class AdsVosBean {
    private String content;
    private int id;

    private String name;
    private  String pic;
    private String title;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "AdsVosBean{" +
                "content='" + content + '\'' +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", pic='" + pic + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
