package com.juzi.oj.aspect;

import java.lang.annotation.*;

/**
 * 权限校验
 *
 * @author codejuzi
 */
@Documented
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthCheck {

    /**
     * 必须有某个角色
     *
     * @return role
     */
    String mustRole() default "user";

}

