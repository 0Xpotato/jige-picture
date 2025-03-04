package com.jige.jigepicturebackend.model.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class SpaceLevel implements Serializable {

    private static final long serialVersionUID = -4963499111331270055L;

    private int value;

    private String text;

    private long maxCount;

    private long maxSize;

}
