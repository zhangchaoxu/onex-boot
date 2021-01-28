package com.nb6868.onexboot.api.common.config;

import com.nb6868.onexboot.api.common.annotation.AccessControl;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.util.Map;

public class AnonScanner implements ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PostConstruct
    public void init() throws Exception {
        //  获取Test注解的类
        Map<String, Object> beans = applicationContext.getBeansWithAnnotation(AccessControl.class);
        for (Map.Entry<String, Object> entry : beans.entrySet()) {
            /*// 根据注解的属性值判断
            if (!TestSuper.class.isAssignableFrom(entry.getValue().getClass())) {
                throw new RuntimeException(entry.getKey() + " - 未继承 TestTestSuper");
            }*/
            System.out.println(entry);
        }
    }

}
