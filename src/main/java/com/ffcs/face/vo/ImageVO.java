package com.ffcs.face.vo;

import java.io.File;
import java.io.Serializable;

public class ImageVO implements Serializable {
    private String imageId;
    private String imageB64;
    private Integer groupId;
    private String groupName;
    private String fileName;

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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
                ", fileName='" + fileName + '\'' +
                '}';
    }
}