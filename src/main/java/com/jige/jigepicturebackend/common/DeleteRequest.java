package com.jige.jigepicturebackend.common;

import lombok.Data;

/**
 * 通用的删除请求类
 */
@Data
public class DeleteRequest {

    /**
     * id
     */
    private Long id;

    private static final Long serialVersionUID = 1L;
}
