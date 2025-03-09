package com.jige.jigepicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class SearchPictureByPictureRequest implements Serializable {

    /**
     * 图片ID
     */
    private Long pictureId;

    private static final long serialVersionUID = 5806657791576164852L;

}
