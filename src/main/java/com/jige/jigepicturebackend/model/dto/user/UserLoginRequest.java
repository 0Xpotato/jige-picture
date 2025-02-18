package com.jige.jigepicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求
 */
@Data
public class UserLoginRequest implements Serializable {
    //实现序列化接口，为了方便在网络中传输,防止序列化对象被修改
    private static final long serialVersionUID = 5553363366482338086L;
    /**
     * 账号
     */
    public String userAccount;

    /**
     * 密码
     */
    public String userPassword;

}
