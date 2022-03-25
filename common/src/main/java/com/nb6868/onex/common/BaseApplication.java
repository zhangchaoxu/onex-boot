package com.nb6868.onex.common;

import cn.hutool.system.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.core.env.Environment;

import java.net.InetAddress;

/**
 * 基础Application
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@Slf4j
public class BaseApplication extends SpringBootServletInitializer {

    /**
     * 打印出环境信息
     *
     * @param environment
     */
    protected static void printEnvironmentInfo(Environment environment) {
        log.info("\n################## {} Running ##################\n" +
                        ":: Active Profiles ::\t{}\n" +
                        ":: Doc ::\thttp://{}:{}{}/doc.html\n" +
                        ":: Onex ::\t{}-{}-{}-{}\n" +
                        ":: App ::\t{}-{}-{}-{}\n" +
                        "################## {} Running ##################",
                environment.getProperty("spring.application.name"),
                environment.getProperty("spring.profiles.active"),
                SystemUtil.getHostInfo().getAddress(),
                environment.getProperty("server.port"),
                environment.getProperty("server.servlet.context-path"),
                environment.getProperty("onex.parent-artifact-id"),
                environment.getProperty("onex.artifact-id"),
                environment.getProperty("onex.version"),
                environment.getProperty("onex.build-time"),
                environment.getProperty("onex.app.parent-artifact-id"),
                environment.getProperty("onex.app.artifact-id"),
                environment.getProperty("onex.app.version"),
                environment.getProperty("onex.app.build-time"),
                environment.getProperty("spring.application.name"));
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(this.getClass());
    }

}
