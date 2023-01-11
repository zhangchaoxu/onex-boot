package com.nb6868.onex.uc;

import com.nb6868.onex.common.BaseApplication;
import com.nb6868.onex.common.util.SpringBeanNameGenerator;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class})
@MapperScan(nameGenerator = SpringBeanNameGenerator.class, basePackages = {
        "com.nb6868.onex.common.shiro",
        "com.nb6868.onex.**.dao"
})
@ComponentScan(nameGenerator = SpringBeanNameGenerator.class, basePackages = {"com.nb6868.onex.**"})
public class TestApplication extends BaseApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(TestApplication.class, args);
        printEnvironmentInfo(app.getEnvironment());
    }

}
