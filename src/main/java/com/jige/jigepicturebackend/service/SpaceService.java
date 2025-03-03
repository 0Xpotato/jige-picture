package com.jige.jigepicturebackend.service;

import com.jige.jigepicturebackend.model.dto.space.SpaceAddRequest;
import com.jige.jigepicturebackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jige.jigepicturebackend.model.entity.User;

/**
* @author Administrator
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-03-03 19:58:20
*/
public interface SpaceService extends IService<Space> {

    /**
     * 校验空间
     * @param space
     * @param add
     */
    void validSpace(Space space,Boolean add);

    /**
     * 根据空间等级填充空间信息,自动填充限额数据
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);


    /**
     * 创建空间
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);
}
