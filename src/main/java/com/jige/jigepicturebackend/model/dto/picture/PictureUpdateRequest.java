package com.jige.jigepicturebackend.model.dto.picture;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 图片更新请求类，给 管理员 使用。注意要将 tags 的类型改为 List<String>，便于前端上传：
 */
@Data
public class PictureUpdateRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private List<String> tags;

    private static final long serialVersionUID = -6006219129847015852L;
}
