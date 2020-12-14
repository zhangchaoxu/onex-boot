package com.nb6868.onexboot.common.validator;

import com.nb6868.onexboot.common.exception.ErrorCode;
import com.nb6868.onexboot.common.exception.OnexException;
import com.nb6868.onexboot.common.pojo.MsgResult;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.beanvalidation.MessageSourceResourceBundleLocator;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Locale;
import java.util.Set;

/**
 * hibernate-validator校验工具类
 * 参考文档：http://docs.jboss.org/hibernate/validator/6.0/reference/en-US/html_single/
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ValidatorUtils {

    /**
     * 文件路径在yml配置文件中定义
     */
    private static ResourceBundleMessageSource getMessageSource() {
        ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
        bundleMessageSource.setDefaultEncoding("UTF-8");
        bundleMessageSource.setBasenames("i18n/messages");
        return bundleMessageSource;
    }

    /**
     * 校验对象
     * @param object        待校验对象
     * @param groups        待校验的组
     */
    public static void validateEntity(Object object, Class<?>... groups) {
        Locale.setDefault(LocaleContextHolder.getLocale());
        Validator validator = Validation.byProvider(HibernateValidator.class).configure()
                // 只要出现校验失败的情况，就立即结束校验，不再进行后续的校验，Provider需为HibernateValidate
                .failFast(false)
                .messageInterpolator(new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(getMessageSource())))
                .buildValidatorFactory()
                .getValidator();

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            // 返回所有错误;分割
            StringBuilder msg = new StringBuilder();
            if (constraintViolations.size() == 1) {
                msg.append(constraintViolations.iterator().next().getMessage());
            } else {
                constraintViolations.forEach(objectConstraintViolation -> msg.append(objectConstraintViolation.getMessage()).append(";"));
            }
            throw new OnexException(ErrorCode.ERROR_REQUEST, msg.toString());
        }
    }

    public static MsgResult getValidateResult(Object object, Class<?>... groups) {
        Locale.setDefault(LocaleContextHolder.getLocale());
        Validator validator = Validation.byProvider(HibernateValidator.class).configure()
                // 只要出现校验失败的情况，就立即结束校验，不再进行后续的校验，Provider需为HibernateValidate
                .failFast(false)
                .messageInterpolator(new ResourceBundleMessageInterpolator(new MessageSourceResourceBundleLocator(getMessageSource())))
                .buildValidatorFactory()
                .getValidator();

        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, groups);
        if (!constraintViolations.isEmpty()) {
            // 返回所有错误;分割
            StringBuilder msg = new StringBuilder();
            if (constraintViolations.size() == 1) {
                msg.append(constraintViolations.iterator().next().getMessage());
            } else {
                constraintViolations.forEach(objectConstraintViolation -> msg.append(objectConstraintViolation.getMessage()).append(";"));
            }
            return new MsgResult().error(ErrorCode.ERROR_REQUEST, msg.toString());
        } else {
            return new MsgResult();
        }
    }
}
