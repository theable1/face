package com.ffcs.face.vo;

import java.io.Serializable;

public class ImageVo implements Serializable {
    private String imageId;
    private String imageB64;
    private String group;

    public ImageVo() {
    }

    public ImageVo(String imageId, String imageB64, String group) {
        this.imageId = imageId;
        this.imageB64 = imageB64;
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
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
}
