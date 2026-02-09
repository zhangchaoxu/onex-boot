package com.nb6868.onex.common.validator;

import com.baomidou.mybatisplus.core.toolkit.sql.SqlInjectionUtils;
import com.nb6868.onex.common.pojo.SortItem;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * sql 安全检查
 * https://baomidou.com/reference/about-cve/
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class SqlSafeValidator implements ConstraintValidator<SqlSafe, Object> {

    @Override
    public void initialize(SqlSafe constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (null == value) {
            // 若不允许为空，应该用其它注解来控制?
            return true;
        }
        if (value instanceof String) {
            return SqlInjectionUtils.check(value.toString());
        } else if (value instanceof SortItem) {
            return SqlInjectionUtils.check(((SortItem) value).getColumn());
        }
        return false;
    }

}