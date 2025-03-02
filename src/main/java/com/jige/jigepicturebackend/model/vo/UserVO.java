package com.jige.jigepicturebackend.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户试图（脱敏）
 */
@Data
public class UserVO implements Serializable {
    private static final long serialVersionUID = 7670163462880708446L;
    /**
     * id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：0:：user,1：admin
     */
    private String userRole;

    /**
     * 创建时间
     */
    private Date createTime;


}