package com.jige.jigepicturebackend.model.dto.space;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpaceEditRequest implements Serializable {

    /**
     * 空间 id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    private static final long serialVersionUID = -7167411404881404390L;

}
