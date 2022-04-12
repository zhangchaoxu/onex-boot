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
     * 是否检查包含用户信息
     */
    boolean userValidate() default true;
    /**
     * 是否按用户过滤
     */
    boolean userFilter() default false;
    /**
     * 用户ID
     */
    String userId() default "creator";
    /**
     * 是否检查包含组织信息
     */
    boolean deptValidate() default true;
    /**
     * 是否按部门过滤
     */
    boolean deptFilter() default false;
    /**
     * 部门ID
     */
    String deptCode() default "deptCode";
    /**
     * 是否检查包含租户信息
     */
    boolean tenantValidate() default true;
    /**
     * 是否按租户过滤
     */
    boolean tenantFilter() default false;
    /**
     * 租户编码
     */
    String tenantCode() default "tenantCode";
    /**
     * 是否检查包含区域信息
     */
    boolean areaValidate() default true;
    /**
     * 是否按区域过滤
     */
    boolean areaFilter() default false;
    /**
     * 区域编码
     */
    String areaCode() default "areaCode";

}
