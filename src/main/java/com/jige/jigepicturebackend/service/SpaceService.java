package com.jige.jigepicturebackend.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.jige.jigepicturebackend.common.DeleteRequest;
import com.jige.jigepicturebackend.model.dto.space.SpaceAddRequest;
import com.jige.jigepicturebackend.model.dto.space.SpaceQueryRequest;
import com.jige.jigepicturebackend.model.entity.Space;
import com.jige.jigepicturebackend.model.entity.User;
import com.jige.jigepicturebackend.model.vo.SpaceVO;

import javax.servlet.http.HttpServletRequest;

/**
 * @author Administrator
 * @description 针对表【space(空间)】的数据库操作Service
 * @createDate 2025-03-03 19:58:20
 */
public interface SpaceService extends IService<Space> {

    /**
     * 创建空间
     *
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    long addSpace(SpaceAddRequest spaceAddRequest, User loginUser);

    /**
     * 校验空间
     *
     * @param space
     * @param add
     */
    void validSpace(Space space, Boolean add);


    /**
     * 获取空间封装的方法，可以为原有的空间关联创建用户的信息
     * 获取单个空间封装
     *
     * @param space
     * @return
     */
    SpaceVO getSpaceVO(Space space, HttpServletRequest request);

    /**
     * 分页获取空间封装
     *
     * @param spacePage
     * @param request
     * @return
     */
    Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request);

    /**
     * 将查询请求转换为QueryWrapper对象,获取查询对象
     *
     * @param spaceQueryRequest
     * @return
     */
    QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest);

    /**
     * 根据空间等级填充空间信息,自动填充限额数据
     *
     * @param space
     */
    void fillSpaceBySpaceLevel(Space space);

    /**
     * 删除空间同时删除空间内部的所有照片
     * @param spaceId
     * @param loginUser
     * @return
     */
    boolean deleteSpace(long spaceId, User loginUser);
}
