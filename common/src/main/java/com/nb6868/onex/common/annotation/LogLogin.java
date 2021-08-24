package com.nb6868.onex.common.annotation;

import java.lang.annotation.*;

/**
 * 登录日志注解
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogLogin {

    /**
     * 登录类型
     */
    String type() default "";
}
