package com.sxy.healthcare.me.bean;

import java.util.List;

public class MembersInfo  {

    private int oneLevelInvite;

    private List<MembersBean> oneLevelInvites;

    private int twoLevelInvite;

    private List<MembersBean> twoLevelInvites;

    public int getOneLevelInvite() {
        return oneLevelInvite;
    }

    public void setOneLevelInvite(int oneLevelInvite) {
        this.oneLevelInvite = oneLevelInvite;
    }

    public List<MembersBean> getOneLevelInvites() {
        return oneLevelInvites;
    }

    public void setOneLevelInvites(List<MembersBean> oneLevelInvites) {
        this.oneLevelInvites = oneLevelInvites;
    }

    public int getTwoLevelInvite() {
        return twoLevelInvite;
    }

    public void setTwoLevelInvite(int twoLevelInvite) {
        this.twoLevelInvite = twoLevelInvite;
    }

    public List<MembersBean> getTwoLevelInvites() {
        return twoLevelInvites;
    }

    public void setTwoLevelInvites(List<MembersBean> twoLevelInvites) {
        this.twoLevelInvites = twoLevelInvites;
    }


    @Override
    public String toString() {
        return "MembersInfo{" +
                "oneLevelInvite=" + oneLevelInvite +
                ", oneLevelInvites=" + oneLevelInvites +
                ", twoLevelInvite=" + twoLevelInvite +
                ", twoLevelInvites=" + twoLevelInvites +
                '}';
    }
}
