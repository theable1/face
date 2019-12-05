package com.ffcs.face.vo;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class ImageVO implements Serializable {
    private String imageId;
    private String imageB64;
    private Integer groupId;
    private String groupName;
    private String imageUrl;
    private Integer imageNum;
    private Date startTime;
    private Date endTime;

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

    public Integer getImageNum() {
        return imageNum;
    }

    public void setImageNum(Integer imageNum) {
        this.imageNum = imageNum;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageB64() {
        return imageB64;
    }

    public void setImageB64(String imageB64) {
        this.imageB64 = imageB64;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    @Override
    public String toString() {
        return "ImageVO{" +
                "imageId='" + imageId + '\'' +
                ", imageB64='" + imageB64 + '\'' +
                ", groupId=" + groupId +
                ", groupName='" + groupName + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", imageNum=" + imageNum +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                '}';
    }
}
