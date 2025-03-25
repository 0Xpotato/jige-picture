package com.jige.jigepicturebackend.manager.auth.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class SpaceUserRole implements Serializable {

    /**
     * 角色key
     */
    private String key;

    /**
     * 角色名称
     */
    private String name;

    /**
     * 权限键列表
     */
    private List<String> permissions;

    /**
     * 角色描述
     */
    private String description;


    private static final long serialVersionUID = -5941005122780868134L;
}
