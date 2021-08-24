package com.nb6868.onex.common.validator;

import com.nb6868.onex.common.pojo.MsgResult;
import com.nb6868.onex.common.util.MessageUtils;
import com.nb6868.onex.common.exception.ErrorCode;
import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.messageinterpolation.ResourceBundleMessageInterpolator;
import org.springframework.context.i18n.LocaleContextHolder;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import java.util.Locale;
import java.util.Set;
import java.util.StringJoiner;

/**
 * hibernate-validator校验工具类
 * 参考文档：http://docs.jboss.org/hibernate/validator/6.0/reference/en-US/html_single/
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class ValidatorUtils {

    /**
     * 校验对象,出错抛出自定义异常
     *
     * @param object 待校验对象
     * @param groups 待校验的组
     */
    public static void validateEntity(Object object, Class<?>... groups) {
        MsgResult result = getValidateResult(object, groups);
        AssertUtils.isFalse(result.isSuccess(), result.getCode(), result.getMsg());
    }

    /**
     * 获取校验结果
     * @param object 待校验对象
     * @param groups 待校验的组
     * @return 校验结果
     */
    public static MsgResult getValidateResult(Object object, Class<?>... groups) {
        Locale.setDefault(LocaleContextHolder.getLocale());
        Set<ConstraintViolation<Object>> constraintViolations = Validation
                .byProvider(HibernateValidator.class)
                .configure()
                // 只要出现校验失败的情况，就立即结束校验，不再进行后续的校验，Provider需为HibernateValidate
                .failFast(false)
                .messageInterpolator(new ResourceBundleMessageInterpolator(MessageUtils.getMessageSourceSourceBundleLocator()))
                .buildValidatorFactory()
                .getValidator()
                .validate(object, groups);
        if (constraintViolations.isEmpty()) {
            return new MsgResult();
        } else {
            // 返回所有错误;分割
            StringJoiner errorMsg = new StringJoiner(";");
            constraintViolations.forEach(objectConstraintViolation -> errorMsg.add(objectConstraintViolation.getMessage()));
            return new MsgResult().error(ErrorCode.ERROR_REQUEST, errorMsg.toString());
        }
    }

}
