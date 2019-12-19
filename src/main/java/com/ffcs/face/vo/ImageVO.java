package com.ffcs.face.vo;

import java.io.File;
import java.io.Serializable;
import java.util.Date;

public class ImageVO implements Serializable {
    private String imageId;
    private String imageB64;
    private String imageUrl;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    @Override
    public String toString() {
        return "ImageVO{" +
                "imageId='" + imageId + '\'' +
                ", imageB64='" + imageB64 + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
