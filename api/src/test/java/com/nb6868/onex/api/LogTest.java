package com.nb6868.onex.api;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

/**
 * 日志测试
 */
@Slf4j
//@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties={"spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration" +
        ",com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceAutoConfigure" +
        ",org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration" +
        ",com.baomidou.dynamic.datasource.spring.boot.autoconfigure.DynamicDataSourceAutoConfiguration"})
public class LogTest {

    @Test
    public void testLog() {
        log.info("info log");
        log.debug("debug log");
        log.error("error log");
    }

}
