package com.jige.jigepicturebackend.model.vo;

import lombok.Data;

import java.util.List;

/**
 * 图片标签分类列表视图
 */
@Data
public class UserRole {

    /**
     * 用户角色列表
     */
    private List<String> userRoleList;
}
