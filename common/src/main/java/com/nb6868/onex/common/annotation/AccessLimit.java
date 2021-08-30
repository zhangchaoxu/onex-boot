package com.nb6868.onex.common.annotation;

import java.lang.annotation.*;

/**
 * 访问限流
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface AccessLimit {

}
