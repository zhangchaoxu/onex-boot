package com.nb6868.onex.booster.util;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.AnnotationBeanNameGenerator;

/**
 * Spring Bean名称生成器
 * 自定义bean名称生成策略,解决同名bean冲突的问题
 *
 * @author Charles zhangchaoxu@gmail.com
 */
public class SpringBeanNameGenerator extends AnnotationBeanNameGenerator {

    @Override
    protected String buildDefaultBeanName(BeanDefinition definition) {
        return definition.getBeanClassName();
    }

}
