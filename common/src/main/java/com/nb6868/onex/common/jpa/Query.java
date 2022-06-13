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
    String column() default "";

    // 排查查询方法
    String[] exclude() default {};

    // 将column或者参数名转换成下划线分割
    boolean underlineCase() default true;

    // 查询方式
    Type type() default Type.EQ;

    // 多字段匹配方式
    BlurryType blurryType() default BlurryType.NULL;

    enum BlurryType {
        // null
        NULL,
        // and
        AND
        // or
        , OR
    }

    enum Type {
        // 相等
        EQ
        // 严格相等,相等对于null
        ,EQ_STRICT
        // 严格相等,相等对于null或者empty会做处理
        ,EQ_STRICT_EMPTY
        // 大于等于
        , GE
        // 大于
        , GT
        // 小于等于
        , LE
        // 中模糊查询
        , LIKE
        // 中模糊查询
        , NOT_LIKE
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
        // Null匹配
        , NULL
        // 空匹配
        , EMPTY
        // not between
        , NOT_BETWEEN
        // between
        , BETWEEN
        // 查询时间
        , BETWEEN_TIME
        // 排序
        , ORDER_BY
        // limit
        , LIMIT
        // find in set
        , FIND_IN_SET
    }

}

