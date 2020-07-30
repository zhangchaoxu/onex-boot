package com.nb6868.onex;

import com.nb6868.onex.booster.util.SpringBeanNameGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

/**
 * One Api Application
 * 自定义bean名称生成策略,解决同名bean冲突的问题
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(nameGenerator = SpringBeanNameGenerator.class)
@MapperScan(basePackages="com.nb6868.onex.modules.*.dao", nameGenerator = SpringBeanNameGenerator.class)
public class OneApiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(OneApiApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(OneApiApplication.class);
    }

}
