package com.nb6868.onex.common.annotation;

import java.lang.annotation.*;

/**
 * 访问控制
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessControl {
    /**
     * 访问路径
     */
    String[] value() default {};

    /**
     * 过滤器
     */
    String filter() default "anon";

}
