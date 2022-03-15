package com.nb6868.onex.common.jpa;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 查选条件
 *
 * @author Zheng Jie
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {

    // 基本对象的属性名
    String propName() default "";

    // 查询方式
    Type type() default Type.EQUAL;

    /**
     * 多字段模糊搜索，仅支持String类型字段，多个用逗号隔开, 如@Query(blurry = "email,username")
     */
    String blurry() default "";

    enum Type {
        // 相等
        EQUAL
        // 大于等于
        , GREATER_THAN
        // 大于
        , GREATER_THAN_NQ
        // 小于等于
        , LESS_THAN
        // 中模糊查询
        , INNER_LIKE
        // 左模糊查询
        , LEFT_LIKE
        // 右模糊查询
        , RIGHT_LIKE
        // 小于
        , LESS_THAN_NQ
        // 包含
        , IN
        // 不等于
        , NOT_EQUAL
        // between
        , BETWEEN
        // 不为空
        , NOT_NULL
        // 查询时间
        , UNIX_TIMESTAMP
    }

}

