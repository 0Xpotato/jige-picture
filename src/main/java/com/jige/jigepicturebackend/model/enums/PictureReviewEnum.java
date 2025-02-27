package com.jige.jigepicturebackend.model.enums;

import cn.hutool.core.util.ObjectUtil;
import lombok.Getter;

/**
 * 图片审核状态枚举
 */
@Getter
public enum PictureReviewEnum {
    REVIEWING("待审核",0),
    PASS("通过",1),
    REJECT("拒绝",2);

    private final String text;
    private final int value;

    PictureReviewEnum(String text, int value) {
        this.text = text;
        this.value = value;
    }

    /**
     * 根据 value 获取枚举
     */
    public static PictureReviewEnum getEnumByValue(Integer value){
        if (ObjectUtil.isEmpty(value)){
            return null;
        }
        for (PictureReviewEnum pictureReviewEnum : PictureReviewEnum.values()) {
            if (pictureReviewEnum.value==value){
                return pictureReviewEnum;
            }
        }
        return null;
    }
}
