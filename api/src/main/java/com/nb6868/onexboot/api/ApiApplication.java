package com.nb6868.onexboot.api;

import com.nb6868.onexboot.common.util.SpringBeanNameGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * OneX Api Application
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@ComponentScan(nameGenerator = SpringBeanNameGenerator.class)
@MapperScan(basePackages="com.nb6868.onexboot.api.modules.*.dao", nameGenerator = SpringBeanNameGenerator.class)
// 启动异步处理
@EnableAsync
public class ApiApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(ApiApplication.class);
    }

}
