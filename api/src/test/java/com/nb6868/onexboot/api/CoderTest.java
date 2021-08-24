package com.nb6868.onexboot.api;

import com.nb6868.onexboot.api.modules.sys.dao.TableSchemaDao;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * 代码生成器
 *
 * @author Charles zhangchaoxu@gmail.com
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class CoderTest {

    @Resource
    TableSchemaDao tableSchemaDao;

    @Test
    @DisplayName("加载所有表")
    void loadAllTables() {

    }

}

