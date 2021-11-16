package com.nb6868.onex.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;

/**
 * 基础Application
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class BaseApplication extends SpringBootServletInitializer {

    /**
     * 打印出环境信息
     * @param environment
     */
    protected void printEnvironmentInfo(Environment environment) {
        log.info("\n################## {} Running ##################\n" +
                        ":: Link ::\thttp://127.0.0.1:{}{}\n" +
                        ":: Doc ::\thttp://127.0.0.1:{}{}/doc.html\n" +
                        "################## {} Running ##################",
                environment.getProperty("spring.application.name"),
                environment.getProperty("server.port"),
                environment.getProperty("server.servlet.context-path"),
                environment.getProperty("server.port"),
                environment.getProperty("server.servlet.context-path"),
                environment.getProperty("spring.application.name"));
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(this.getClass());
    }

}
