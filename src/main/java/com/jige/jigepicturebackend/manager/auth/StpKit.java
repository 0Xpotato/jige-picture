package com.jige.jigepicturebackend.manager.auth;

import cn.dev33.satoken.stp.StpLogic;
import cn.dev33.satoken.stp.StpUtil;
import org.springframework.stereotype.Component;

/**
 * StpLogic门面类，管理项目中所有的StpLogic账号体系
 * 添加@Component注解的目的是确保静态属性DEFAULT和SPACE被初始化
 */
@Component
public class StpKit {

    public static final String SPACE_TYPE = "space";

    /**
     * 默认原生会话对象，项目中目前没有使用到
     */
    public static final StpLogic DEFAULT = StpUtil.stpLogic;

    /**
     * Space会话对象，管理Space表所有账号的登录，权限认证
     */
    public static final StpLogic SPACE = new StpLogic(SPACE_TYPE);
}
