package com.nb6868.onex.common.annotation;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogOperation {

    String value() default "";

}
