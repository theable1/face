package com.ffcs.face.vo;

import java.util.Date;

public class ImageConditionVO {
    private String groupName;
    private Integer imageNum;
    private Date startTime;
    private Date endTime;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Integer getImageNum() {
        return imageNum;
    }

    public void setImageNum(Integer imageNum) {
        this.imageNum = imageNum;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    @Override
    public String toString() {
        return "ImageConditionVO{" +
                "groupName='" + groupName + '\'' +
                ", imageNum=" + imageNum +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
