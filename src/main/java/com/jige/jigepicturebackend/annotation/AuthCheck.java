package com.jige.jigepicturebackend.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)         //指定注解在方法上生效
@Retention(RetentionPolicy.RUNTIME) //指定注解在运行时生效
public @interface AuthCheck {

    /**
     * 必须具有某个角色
     * 当给上这个注解的时候，就一定要给角色
     * 不用权限校验的时候就可以不加该注解
     * @return
     */
    String mustRole() default "";

}
