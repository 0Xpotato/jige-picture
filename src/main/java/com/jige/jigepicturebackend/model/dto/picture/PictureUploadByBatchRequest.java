package com.jige.jigepicturebackend.model.dto.picture;

import lombok.Data;

@Data
public class PictureUploadByBatchRequest {
    /**
     * 搜索词
     */
    private String searchText;

    /**
     * 搜索数量
     */
    private Integer count = 10;
}
