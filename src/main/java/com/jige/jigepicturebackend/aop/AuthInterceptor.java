package com.jige.jigepicturebackend.aop;

import com.jige.jigepicturebackend.annotation.AuthCheck;
import com.jige.jigepicturebackend.exception.BusinessException;
import com.jige.jigepicturebackend.exception.ErrorCode;
import com.jige.jigepicturebackend.model.entity.User;
import com.jige.jigepicturebackend.model.enums.UserRoleEnum;
import com.jige.jigepicturebackend.service.UserService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 权限校验拦截器
 */
@Aspect
@Component
public class AuthInterceptor {
    @Resource
    private UserService userService;

    /**
     * 管理员可以访问所有VIP权限接口
     * VIP用户不能访问管理员权限接口
     * 普通用户（user）只能访问未标注权限或明确标注user权限的接口
     * 权限校验层级关系：ADMIN > VIP > USER
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        // 获取当前登录用户
        User loginUser = userService.getLoginUser(request);
        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);

        // 如果不需要权限，放行
        if (mustRoleEnum == null) {
            return joinPoint.proceed();
        }

        // 获取用户角色并校验
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 根据要求的角色进行校验
        if (mustRoleEnum == UserRoleEnum.ADMIN) {
            // 必须管理员角色
            if (userRoleEnum != UserRoleEnum.ADMIN) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        } else if (mustRoleEnum == UserRoleEnum.VIP) {
            // 需要VIP角色，允许VIP或ADMIN
            if (userRoleEnum != UserRoleEnum.VIP && userRoleEnum != UserRoleEnum.ADMIN) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        } else {
            // 其他角色需要严格匹配
            if (userRoleEnum != mustRoleEnum) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
        }

        return joinPoint.proceed();
    }
}