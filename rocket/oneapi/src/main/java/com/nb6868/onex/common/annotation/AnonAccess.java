package com.nb6868.onex.common.annotation;

import java.lang.annotation.*;

/**
 * 是否允许匿名访问
 *
 * @author Charles (zhanngchaoxu@gmail.com)
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AnonAccess {

    /**
     * 是否允许,默认允许
     */
    boolean value() default true;

}
