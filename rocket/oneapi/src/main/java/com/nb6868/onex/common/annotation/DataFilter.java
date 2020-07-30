package com.nb6868.onex.common.annotation;

import java.lang.annotation.*;

/**
 * 数据过滤注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataFilter {
    /**
     * 表的别名
     */
    String tableAlias() default "";

    /**
     * 查询条件前缀，可选值有：[where、and]
     */
    String prefix() default "";

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
    boolean useFilter() default false;

    /**
     * 部门ID
     */
    String deptId() default "dept_id";

    /**
     * 是否按租户过滤
     */
    boolean tenantFilter() default false;

    /**
     * 租户ID
     */
    String tenantId() default "tenant_id";
}
