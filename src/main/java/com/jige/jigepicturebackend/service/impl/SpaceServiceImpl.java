package com.jige.jigepicturebackend.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jige.jigepicturebackend.common.DeleteRequest;
import com.jige.jigepicturebackend.exception.BusinessException;
import com.jige.jigepicturebackend.exception.ErrorCode;
import com.jige.jigepicturebackend.exception.ThrowUtils;
import com.jige.jigepicturebackend.mapper.SpaceMapper;
import com.jige.jigepicturebackend.model.dto.space.SpaceAddRequest;
import com.jige.jigepicturebackend.model.dto.space.SpaceQueryRequest;
import com.jige.jigepicturebackend.model.entity.Picture;
import com.jige.jigepicturebackend.model.entity.Space;
import com.jige.jigepicturebackend.model.entity.User;
import com.jige.jigepicturebackend.model.enums.SpaceLevelEnum;
import com.jige.jigepicturebackend.model.vo.SpaceVO;
import com.jige.jigepicturebackend.service.PictureService;
import com.jige.jigepicturebackend.service.SpaceService;
import com.jige.jigepicturebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

/**
 * @author Administrator
 * @description 针对表【space(空间)】的数据库操作Service实现
 * @createDate 2025-03-03 19:58:20
 */
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space> implements SpaceService {

    @Resource
    private UserService userService;

    @Resource
    private TransactionTemplate transactionTemplate;

    @Resource
    private PictureService pictureService;

    /**
     * 校验空间
     *
     * @param space
     * @param add
     */
    @Override
    public void validSpace(Space space, Boolean add) {
        ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR);
        String spaceName = space.getSpaceName();
        Integer spaceLevel = space.getSpaceLevel();
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(spaceLevel);
        //要创建
        if (add) {
            ThrowUtils.throwIf(StrUtil.isBlank(spaceName), ErrorCode.PARAMS_ERROR, "空间名称不能为空");
            ThrowUtils.throwIf(spaceLevel == null, ErrorCode.PARAMS_ERROR, "空间级别不能为空");
        }
        //修改数据时，如果要修改空间级别
        if (spaceLevel != null && spaceLevelEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间级别不存在");
        }
        if (StrUtil.isNotBlank(spaceName) && spaceName.length() > 30) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "空间名称过长");
        }
    }

    /**
     * 将查询请求转换为QueryWrapper对象,获取查询对象
     *
     * @param spaceQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Space> getQueryWrapper(SpaceQueryRequest spaceQueryRequest) {

        return null;
    }

    @Override
    public SpaceVO getSpaceVO(Space space, HttpServletRequest request) {
        return null;
    }

    @Override
    public Page<SpaceVO> getSpaceVOPage(Page<Space> spacePage, HttpServletRequest request) {
        return null;
    }

    /**
     * 根据空间级别填充空间信息,自动填充限额数据
     *
     * @param space
     */
    @Override
    public void fillSpaceBySpaceLevel(Space space) {
        // 根据空间级别，自动填充限额
        //如果空间本身没有设置限额，才会自动填充，保证了灵活性。
        SpaceLevelEnum spaceLevelEnum = SpaceLevelEnum.getEnumByValue(space.getSpaceLevel());
        if (spaceLevelEnum != null) {
            long maxSize = spaceLevelEnum.getMaxSize();
            if (space.getMaxSize() == null) {
                space.setMaxSize(maxSize);
            }
            long maxCount = spaceLevelEnum.getMaxCount();
            if (space.getMaxCount() == null) {
                space.setMaxCount(maxCount);
            }
        }
    }

    /**
     * 删除空间同时删除空间内部的所有照片
     *
     * @param spaceId
     * @param loginUser
     * @return
     */
    @Override
    public boolean deleteSpace(long spaceId, User loginUser) {
        ThrowUtils.throwIf(spaceId <= 0, ErrorCode.PARAMS_ERROR);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NO_AUTH_ERROR);
        // 判断是否存在
        Space space = this.getById(spaceId);
        ThrowUtils.throwIf(space == null, ErrorCode.NOT_FOUND_ERROR, "空间不存在");
        // 校验权限,仅空间本人可删除
        if (!loginUser.getId().equals(space.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 开启事务,删除图片后更新额度
        transactionTemplate.execute(status -> {
            // 操作数据库
            boolean result = this.removeById(spaceId);
            ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
            // 释放额度
            boolean update = pictureService.lambdaUpdate().eq(Picture::getSpaceId, spaceId).setSql("totalSize=0").setSql("totalCount=0").update();
            ThrowUtils.throwIf(!update, ErrorCode.OPERATION_ERROR, "额度更新失败");

            return true;
        });
        //  删除关联的对象存储文件图片，包括webp格式图和缩略图
        //  异步清理文件
//        pictureService.clearPictureFile(oldPicture);
        return false;
    }

    /**
     * 创建空间
     *
     * @param spaceAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long addSpace(SpaceAddRequest spaceAddRequest, User loginUser) {
        // 在此处将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceAddRequest, space);
        // 默认值
        if (StrUtil.isBlank(spaceAddRequest.getSpaceName())) {
            space.setSpaceName("默认空间");
        }
        if (spaceAddRequest.getSpaceLevel() == null) {
            space.setSpaceLevel(SpaceLevelEnum.COMMON.getValue());
        }
        // 1）填充参数默认值
        this.fillSpaceBySpaceLevel(space);
        // 2）校验参数
        this.validSpace(space, true);
        Long userId = loginUser.getId();
        space.setUserId(userId);
        // 3）校验权限，非管理员只能创建普通级别的空间
        if (SpaceLevelEnum.COMMON.getValue() != spaceAddRequest.getSpaceLevel() && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限创建指定级别的空间");
        }
        // 针对用户进行加锁
        //我们使用本地 synchronized 锁对 userId 进行加锁，这样不同的用户可以拿到不同的锁，对性能的影响较低。
        //在加锁的代码中，我们使用 Spring 的 编程式事务管理器 transactionTemplate 封装跟数据库有关的查询和插入操作，
        // 而不是使用 @Transactional 注解来控制事务，这样可以保证事务的提交在加锁的范围内。
        //4）同一个账号自能创建一个私有空间
        String lock = String.valueOf(userId).intern();
        synchronized (lock) {
            Long newSpaceId = transactionTemplate.execute(status -> {
                boolean exists = this.lambdaQuery().eq(Space::getUserId, userId).exists();
                ThrowUtils.throwIf(exists, ErrorCode.OPERATION_ERROR, "每个用户仅能有一个私有空间");
                //用户没有私有空间，写入数据库
                //5）操作数据库
                boolean result = this.save(space);
                ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
                // 返回新写入的数据 id
                return space.getId();
            });
            // 返回结果是包装类，可以做一些处理
            return Optional.ofNullable(newSpaceId).orElse(-1L);
        }
    }

}




