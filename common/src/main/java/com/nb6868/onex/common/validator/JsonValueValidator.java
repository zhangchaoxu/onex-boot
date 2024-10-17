package com.nb6868.onex.common.validator;

import cn.hutool.json.JSONUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * JsonValue校验方法
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class JsonValueValidator implements ConstraintValidator<EnumValue, Object> {

    @Override
    public void initialize(EnumValue constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof String) {
            return JSONUtil.isTypeJSON(value.toString());
        }
        return false;
    }

}
