package com.jige.jigepicturebackend.controller;

import com.jige.jigepicturebackend.annotation.AuthCheck;
import com.jige.jigepicturebackend.common.BaseResponse;
import com.jige.jigepicturebackend.common.ResultUtils;
import com.jige.jigepicturebackend.constant.UserConstant;
import com.jige.jigepicturebackend.exception.BusinessException;
import com.jige.jigepicturebackend.exception.ErrorCode;
import com.jige.jigepicturebackend.exception.ThrowUtils;
import com.jige.jigepicturebackend.model.dto.space.SpaceUpdateRequest;
import com.jige.jigepicturebackend.model.entity.Space;
import com.jige.jigepicturebackend.service.SpaceService;
import com.jige.jigepicturebackend.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/space")
public class SpaceController {
    @Resource
    private SpaceService spaceService;

    @Resource
    private UserService userService;


    /**
     * 更新空间：仅管理员可用，允许更新空间级别
     * @param spaceUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest){
        if (spaceUpdateRequest==null||spaceUpdateRequest.getId()<0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 判断是否存在
        Space oldSpace = spaceService.getById(spaceUpdateRequest.getId());
        ThrowUtils.throwIf(oldSpace==null,ErrorCode.NOT_FOUND_ERROR);
        // 将实体类和 DTO 进行转换
        Space space = new Space();
        BeanUtils.copyProperties(spaceUpdateRequest,space);
        // 自动填充数据
        spaceService.fillSpaceBySpaceLevel(space);
        //校验数据
        spaceService.validSpace(space,false);
        //操作数据库
        boolean result = spaceService.updateById(space);
        ThrowUtils.throwIf(!result,ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }




}
