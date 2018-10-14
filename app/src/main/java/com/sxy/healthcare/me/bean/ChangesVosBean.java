package com.sxy.healthcare.me.bean;

public class ChangesVosBean {

    private String id;

    private String content;

    private int state;

    private String createTime;

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

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "ChangesVosBean{" +
                "id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", state=" + state +
                ", createTime='" + createTime + '\'' +
                '}';
    }
}
