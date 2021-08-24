package com.nb6868.onex.common.annotation;

import java.lang.annotation.*;

/**
 * 计时器注解
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogTimerInterval {

    String value() default "";

}
