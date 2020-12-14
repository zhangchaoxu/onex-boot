package com.nb6868.onexboot.api.common.annotation;

import java.lang.annotation.*;

/**
 * 是否允许匿名访问
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AccessControl {

    /**
     * 是否允许匿名用户
     */
    boolean anon() default true;

}
