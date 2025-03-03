package com.jige.jigepicturebackend.service;

import com.jige.jigepicturebackend.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;

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

}
