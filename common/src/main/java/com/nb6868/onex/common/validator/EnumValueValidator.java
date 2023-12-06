package com.nb6868.onex.common.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * EnumValue校验方法
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class EnumValueValidator implements ConstraintValidator<EnumValue, Object> {

    private String[] strValues;
    private int[] intValues;

    @Override
    public void initialize(EnumValue constraintAnnotation) {
        strValues = constraintAnnotation.strValues();
        intValues = constraintAnnotation.intValues();
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value instanceof String) {
            for (String s : strValues) {
                if (s.equals(value)) {
                    return true;
                }
            }
        } else if (value instanceof Integer) {
            for (int s : intValues) {
                if (s == (Integer) value) {
                    return true;
                }
            }
        }
        return false;
    }

}
