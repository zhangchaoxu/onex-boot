package com.nb6868.onex.common.annotation;

import java.lang.annotation.*;

/**
 * 数据过滤注解
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface QueryDataScope {

    /**
     * 是否按用户过滤
     */
    boolean userFilter() default false;
    /**
     * 用户ID
     */
    String userId() default "creator";
    /**
     * 是否按部门过滤
     */
    boolean deptFilter() default false;
    /**
     * 部门ID
     */
    String deptCode() default "deptCode";
    /**
     * 是否按租户过滤
     */
    boolean tenantFilter() default false;
    /**
     * 租户编码
     */
    String tenantCode() default "tenantCode";
    /**
     * 是否按区域过滤
     */
    boolean areaFilter() default false;
    /**
     * 区域编码
     */
    String areaCode() default "areaCode";

}
