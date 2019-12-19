package com.ffcs.face.vo;

import java.util.List;

public class ViewVO {
    private List<ImageVO> imageVOList;
    private ImageConditionVO imageConditionVO;

    public List<ImageVO> getImageVOList() {
        return imageVOList;
    }

    public void setImageVOList(List<ImageVO> imageVOList) {
        this.imageVOList = imageVOList;
    }

    public ImageConditionVO getImageConditionVO() {
        return imageConditionVO;
    }

    public void setImageConditionVO(ImageConditionVO imageConditionVO) {
        this.imageConditionVO = imageConditionVO;
    }

    @Override
    public String toString() {
        return "ViewVO{" +
                "imageVOList=" + imageVOList +
                ", imageConditionVO=" + imageConditionVO +
                '}';
    }

    public ViewVO(List<ImageVO> imageVOList, ImageConditionVO imageConditionVO) {
        this.imageVOList = imageVOList;
        this.imageConditionVO = imageConditionVO;
    }

    public ViewVO() {
    }
}
