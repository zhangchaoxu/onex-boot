package com.nb6868.onex.common.validator;

import com.nb6868.onex.common.pojo.PageForm;
import org.hibernate.validator.HibernateValidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import javax.validation.Validation;

/**
 * 分页校验
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class PageValidator implements ConstraintValidator<Page, Object> {

    @Override
    public void initialize(Page constraintAnnotation) {
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (!(value instanceof PageForm)) {
            return false;
        }
        return getValidateResult(value);
    }

    public static boolean getValidateResult(Object object) {
        return Validation
                .byProvider(HibernateValidator.class)
                .configure()
                // 只要出现校验失败的情况，就立即结束校验，不再进行后续的校验，Provider需为HibernateValidate
                .failFast(true)
                .buildValidatorFactory()
                .getValidator()
                .validate(object)
                .isEmpty();
    }
}

