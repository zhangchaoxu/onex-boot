package com.nb6868.onex.coder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * Coder Application
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@SpringBootApplication(exclude = { DataSourceAutoConfiguration.class })
public class CoderApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoderApplication.class, args);
    }

}
