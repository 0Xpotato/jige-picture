package com.jige.jigepicturebackend.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户注册请求
 */
@Data
public class UserRegisterRequest implements Serializable {
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

    /**
     * 确认密码
     */
    public String checkPassword;


}
