package com.jige.jigepicturebackend.controller;

import com.jige.jigepicturebackend.common.BaseResponse;
import com.jige.jigepicturebackend.common.ResultUtils;
import com.jige.jigepicturebackend.exception.ErrorCode;
import com.jige.jigepicturebackend.exception.ThrowUtils;
import com.jige.jigepicturebackend.model.dto.UserLoginRequest;
import com.jige.jigepicturebackend.model.dto.UserRegisterRequest;
import com.jige.jigepicturebackend.model.entity.User;
import com.jige.jigepicturebackend.model.vo.LoginUserVO;
import com.jige.jigepicturebackend.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    /**
     * 用户注册
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        ThrowUtils.throwIf(userRegisterRequest==null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        ThrowUtils.throwIf(userLoginRequest==null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVo(loginUser));
    }

    /**
     * 用户注销
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogin(HttpServletRequest request){
        ThrowUtils.throwIf(request==null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }
}
