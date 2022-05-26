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

    /**
     * ip过滤器模式支持 none无过滤/white名单/black黑名单
     */
    String ipFilter() default "none";

    /**
     * ip白名单
     */
    String[] ipWhite() default {};

    /**
     * ip黑名单
     */
    String[] ipBlack() default {};

    /**
     * 允许token的名字
     */
    String[] allowTokenName() default {};

}
