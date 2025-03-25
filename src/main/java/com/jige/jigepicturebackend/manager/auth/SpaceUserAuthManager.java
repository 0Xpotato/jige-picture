package com.jige.jigepicturebackend.manager.auth;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.jige.jigepicturebackend.manager.auth.model.SpaceUserAuthConfig;
import com.jige.jigepicturebackend.manager.auth.model.SpaceUserRole;
import com.jige.jigepicturebackend.service.SpaceUserService;
import com.jige.jigepicturebackend.service.UserService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * 加载配置文件到对象，并提供根据角色获取权限列表的方法
 */
public class SpaceUserAuthManager {

    @Resource
    private SpaceUserService spaceUserService;

    @Resource
    private UserService userService;

    public static final SpaceUserAuthConfig SPACE_USER_AUTH_CONFIG;

    static {
        String json = ResourceUtil.readUtf8Str("biz/SpaceUserAuthConfig.json");
        SPACE_USER_AUTH_CONFIG = JSONUtil.toBean(json, SpaceUserAuthConfig.class);
    }

    /**
     * 根据角色获取权限列表
     * @param spaceUserRole
     * @return
     */
    public List<String> getPermissionByRole(String spaceUserRole){
        if (StrUtil.isBlank(spaceUserRole)){
            return new ArrayList<>();
        }
        //找到匹配的角色
        SpaceUserRole role = SPACE_USER_AUTH_CONFIG.getRoles()
                .stream()
                .filter(r -> spaceUserRole.equals(r.getKey()))
                .findFirst()
                .orElse(null);
        if (role==null){
            return new ArrayList<>();
        }
        return role.getPermissions();
    }
}
