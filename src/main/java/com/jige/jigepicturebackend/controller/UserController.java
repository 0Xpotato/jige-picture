package com.jige.jigepicturebackend.controller;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jige.jigepicturebackend.annotation.AuthCheck;
import com.jige.jigepicturebackend.common.BaseResponse;
import com.jige.jigepicturebackend.common.DeleteRequest;
import com.jige.jigepicturebackend.common.ResultUtils;
import com.jige.jigepicturebackend.constant.UserConstant;
import com.jige.jigepicturebackend.exception.BusinessException;
import com.jige.jigepicturebackend.exception.ErrorCode;
import com.jige.jigepicturebackend.exception.ThrowUtils;
import com.jige.jigepicturebackend.model.dto.user.*;
import com.jige.jigepicturebackend.model.entity.User;
import com.jige.jigepicturebackend.model.enums.UserRoleEnum;
import com.jige.jigepicturebackend.model.vo.LoginUserVO;
import com.jige.jigepicturebackend.model.vo.PictureTagCategory;
import com.jige.jigepicturebackend.model.vo.UserRole;
import com.jige.jigepicturebackend.model.vo.UserVO;
import com.jige.jigepicturebackend.service.SpaceService;
import com.jige.jigepicturebackend.service.UserService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.jige.jigepicturebackend.model.entity.Space;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    /**
     * 用户注册
     *
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        long result = userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param userLoginRequest
     * @return
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(userLoginRequest == null, ErrorCode.PARAMS_ERROR);
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        LoginUserVO loginUserVO = userService.userLogin(userAccount, userPassword, request);
        return ResultUtils.success(loginUserVO);
    }

    /**
     * 获取当前登录用户
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(userService.getLoginUserVO(loginUser));
    }

    /**
     * 用户注销
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = userService.userLogout(request);
        return ResultUtils.success(result);
    }

    /**
     * 创建用户
     *
     * @return
     */
    @PostMapping("/add")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {
        ThrowUtils.throwIf(userAddRequest == null, ErrorCode.PARAMS_ERROR);
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        //默认密码
        final String DEFAULT_PASSWORD = "12345678";
        String encryptPassword = userService.getEncryptPassword(DEFAULT_PASSWORD);
        user.setUserPassword(encryptPassword);
        //插入数据库
        boolean result = userService.save(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(user.getId());
    }

    /**
     * 根据id获取用户 （仅管理员）
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<User> getUserById(Long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        User user = userService.getById(id);
        ThrowUtils.throwIf(user == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(user);
    }

    /**
     * 根据id获取用户脱敏信息
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<UserVO> getUserVOById(Long id) {
        BaseResponse<User> response = getUserById(id);
        User user = response.getData();
        return ResultUtils.success(userService.getUserVO(user));
    }

    /**
     * 删除用户 （仅管理员）
     *  todo事务开启之后，要注意未创建空间的用户删除不了用户，具体看业务需求
     * @param deleteRequest
     * @return
     */
    @PostMapping("/delete")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
//    @Transactional(rollbackFor = Exception.class) // 开启事务，确保操作失败时回滚
    public BaseResponse<Boolean> deleteUser(@RequestBody DeleteRequest deleteRequest) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 删除用户
        boolean userDeleted = userService.lambdaUpdate().eq(User::getId, deleteRequest.getId()).remove();
        ThrowUtils.throwIf(!userDeleted, ErrorCode.OPERATION_ERROR, "删除用户失败，数据库异常");

        // 删除用户所拥有的空间
        boolean spaceDeleted = spaceService.lambdaUpdate().eq(Space::getUserId, deleteRequest.getId()).remove();
        ThrowUtils.throwIf(!spaceDeleted, ErrorCode.OPERATION_ERROR, "删除用户空间失败，数据库异常");

        return ResultUtils.success(true);
    }

    /**
     * 更新用户
     *
     * @param userUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest) {
        if (userUpdateRequest == null || userUpdateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = new User();
        BeanUtil.copyProperties(userUpdateRequest, user);
        boolean result = userService.updateById(user);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 分页获取用户封装列表 （仅管理员）
     *
     * @param userQueryRequest  查询请求参数
     * @return
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<UserVO>> listUserVOByPage(@RequestBody UserQueryRequest userQueryRequest) {
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);
        long current = userQueryRequest.getCurrent();
        long pageSize = userQueryRequest.getPageSize();
        Page<User> userPage = userService.page(new Page<>(current, pageSize), userService.getQueryWrapper(userQueryRequest));
        Page<UserVO> userVOPage = new Page<>(current, pageSize, userPage.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(userPage.getRecords());
        userVOPage.setRecords(userVOList);
        return ResultUtils.success(userVOPage);
    }

    /**
     * 获取用户角色列表
     */
    @GetMapping("/userRole")
    public BaseResponse<UserRole> listUserRole() {
        UserRole userRole = new UserRole();
        List<String> userRoleList = Arrays.asList(UserRoleEnum.USER.getValue(),UserRoleEnum.ADMIN.getValue(),UserRoleEnum.VIP.getValue());
        userRole.setUserRoleList(userRoleList);
        return ResultUtils.success(userRole);
    }

    /**
     * 兑换会员
     */
    @PostMapping("/exchange/vip")
    public BaseResponse<Boolean> exchangeVip(@RequestBody VipExchangeRequest vipExchangeRequest,
                                             HttpServletRequest httpServletRequest) {
        ThrowUtils.throwIf(vipExchangeRequest == null, ErrorCode.PARAMS_ERROR);
        String vipCode = vipExchangeRequest.getVipCode();
        User loginUser = userService.getLoginUser(httpServletRequest);
        // 调用 service 层的方法进行会员兑换
        boolean result = userService.exchangeVip(loginUser, vipCode);
        return ResultUtils.success(result);
    }
}
