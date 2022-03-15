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
    Type type() default Type.EQ;

    /**
     * 多字段模糊搜索，仅支持String类型字段，多个用逗号隔开, 如@Query(blurry = "email,username")
     */
    String blurry() default "";

    enum Type {
        // 相等
        EQ
        // 大于等于
        , GE
        // 大于
        , GT
        // 小于等于
        , LE
        // 中模糊查询
        , LIKE
        // 左模糊查询
        , LIKE_LEFT
        // 右模糊查询
        , LIKE_RIGHT
        // 小于
        , LT
        // 包含
        , IN
        // 不包含
        , NOT_IN
        // 不等于
        , NE
        // 不为空
        , IS_NOT_NULL
        // 为空
        , IS_NULL
        // between
        , BETWEEN
        // 查询时间
        , BETWEEN_TIME
        // 排序
        , ORDER_BY
    }

}

